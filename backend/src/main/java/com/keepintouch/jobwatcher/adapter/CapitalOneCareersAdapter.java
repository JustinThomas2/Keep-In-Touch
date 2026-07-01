package com.keepintouch.jobwatcher.adapter;

import com.keepintouch.domain.JobSourceType;
import com.keepintouch.jobwatcher.logic.CountryLocationClassifier;
import com.keepintouch.jobwatcher.model.JobSourceFailure;
import com.keepintouch.jobwatcher.model.JobSourceResult;
import com.keepintouch.jobwatcher.model.JobSourceSuccess;
import com.keepintouch.jobwatcher.model.NormalizedJob;
import com.keepintouch.jobwatcher.model.WatchedSourceConfig;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class CapitalOneCareersAdapter implements JobSourceAdapter {

  private static final int MAX_PAGES = 10;
  private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
  private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(20);
  private static final Pattern RESULT_CARD_PATTERN = Pattern.compile("(?is)<li\\b[^>]*>.*?</li>");
  private static final Pattern DATA_JOB_ID_PATTERN =
      Pattern.compile("(?is)\\bdata-job-id\\s*=\\s*['\"]([^'\"]+)['\"]");
  private static final Pattern LINK_PATTERN =
      Pattern.compile("(?is)<a\\b[^>]*href\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>(.*?)</a>");
  private static final Pattern NEXT_LINK_PATTERN =
      Pattern.compile("(?is)<a\\b[^>]*href\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>\\s*Next\\s*</a>");
  private static final Pattern LOCATION_PATTERN =
      Pattern.compile(
          "(?is)<[^>]*class\\s*=\\s*['\"][^'\"]*job-location[^'\"]*['\"][^>]*>(.*?)</[^>]+>");
  private static final Pattern POSTED_DATE_PATTERN =
      Pattern.compile(
          "(?is)<[^>]*class\\s*=\\s*['\"][^'\"]*job-date-posted[^'\"]*['\"][^>]*>(.*?)</[^>]+>");
  private static final Pattern FOUR_DIGIT_YEAR_PATTERN = Pattern.compile("\\b\\d{4}\\b");
  private static final List<DateTimeFormatter> POSTED_DATE_FORMATTERS =
      List.of(
          DateTimeFormatter.ofPattern("MMM d, uuuu", Locale.US),
          DateTimeFormatter.ofPattern("MMM d uuuu", Locale.US));

  private final PageFetcher pageFetcher;

  public CapitalOneCareersAdapter() {
    this(
        new HttpClientPageFetcher(HttpClient.newBuilder().connectTimeout(CONNECT_TIMEOUT).build()));
  }

  CapitalOneCareersAdapter(PageFetcher pageFetcher) {
    this.pageFetcher = pageFetcher;
  }

  @Override
  public JobSourceType sourceType() {
    return JobSourceType.CAPITAL_ONE_CAREERS;
  }

  @Override
  public JobSourceResult fetchAndParse(WatchedSourceConfig source) {
    String currentUrl = source.effectiveSourceUrl();
    List<NormalizedJob> jobs = new ArrayList<>();

    try {
      for (int page = 0; page < MAX_PAGES && currentUrl != null; page++) {
        PageResponse response = pageFetcher.fetch(currentUrl);
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
          return failure(
              source,
              "Capital One careers returned HTTP " + response.statusCode() + ".",
              currentUrl,
              null);
        }

        ParsedPage parsedPage = parsePage(response.body(), currentUrl);
        jobs.addAll(parsedPage.jobs());
        currentUrl = parsedPage.nextPageUrl().orElse(null);
      }

      return new JobSourceSuccess(source, jobs, source.effectiveSourceUrl());
    } catch (HttpTimeoutException e) {
      return failure(source, "Timed out fetching Capital One careers page.", currentUrl, e);
    } catch (IOException e) {
      return failure(source, "Could not fetch Capital One careers page.", currentUrl, e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return failure(source, "Interrupted while fetching Capital One careers page.", currentUrl, e);
    } catch (IllegalArgumentException e) {
      return failure(source, e.getMessage(), currentUrl, e);
    }
  }

  ParsedPage parsePage(String html, String pageUrl) {
    if (html == null || html.isBlank()) {
      throw new IllegalArgumentException("Capital One careers returned an empty page.");
    }

    Matcher cardMatcher = RESULT_CARD_PATTERN.matcher(html);
    List<NormalizedJob> jobs = new ArrayList<>();
    while (cardMatcher.find()) {
      parseCard(cardMatcher.group(), pageUrl).ifPresent(jobs::add);
    }

    return new ParsedPage(jobs, nextPageUrl(html, pageUrl));
  }

  private static Optional<NormalizedJob> parseCard(String cardHtml, String pageUrl) {
    Matcher linkMatcher = LINK_PATTERN.matcher(cardHtml);
    if (!linkMatcher.find()) {
      if (!DATA_JOB_ID_PATTERN.matcher(cardHtml).find()) {
        return Optional.empty();
      }
      throw new IllegalArgumentException("Capital One result card is missing a job detail link.");
    }

    String url = resolveUrl(pageUrl, htmlDecode(linkMatcher.group(1)));
    if (!url.contains("/job/")) {
      return Optional.empty();
    }

    String externalId =
        optional(DATA_JOB_ID_PATTERN, cardHtml).orElseGet(() -> externalIdFromUrl(url));
    String title = stripTags(linkMatcher.group(2));
    String location = optional(LOCATION_PATTERN, cardHtml).orElse(null);
    OffsetDateTime postedAt =
        optional(POSTED_DATE_PATTERN, cardHtml)
            .flatMap(CapitalOneCareersAdapter::parsePostedAt)
            .orElse(null);

    return Optional.of(
        new NormalizedJob(
            externalId,
            title,
            location,
            CountryLocationClassifier.isUsBased(null, location) ? "US" : null,
            url,
            url,
            null,
            null,
            null,
            null,
            postedAt,
            null));
  }

  private static Optional<String> nextPageUrl(String html, String pageUrl) {
    Matcher matcher = NEXT_LINK_PATTERN.matcher(html);
    if (!matcher.find()) {
      return Optional.empty();
    }
    return Optional.of(resolveUrl(pageUrl, htmlDecode(matcher.group(1))));
  }

  private static String externalIdFromUrl(String url) {
    String path = URI.create(url).getPath();
    int lastSlash = path.lastIndexOf('/');
    if (lastSlash < 0 || lastSlash == path.length() - 1) {
      throw new IllegalArgumentException("Capital One result card is missing job id.");
    }
    return path.substring(lastSlash + 1);
  }

  private static Optional<String> optional(Pattern pattern, String html) {
    Matcher matcher = pattern.matcher(html);
    if (!matcher.find()) {
      return Optional.empty();
    }
    String value = stripTags(matcher.group(1));
    return value.isBlank() ? Optional.empty() : Optional.of(value);
  }

  private static Optional<OffsetDateTime> parsePostedAt(String value) {
    String normalized =
        value
            .replace("Posted Date:", "")
            .replace("Posted", "")
            .replace("Date:", "")
            .trim()
            .replaceAll("\\s+", " ");
    for (DateTimeFormatter formatter :
        List.of(DateTimeFormatter.ISO_OFFSET_DATE_TIME, DateTimeFormatter.ISO_LOCAL_DATE)) {
      try {
        if (formatter == DateTimeFormatter.ISO_OFFSET_DATE_TIME) {
          return Optional.of(OffsetDateTime.parse(normalized, formatter));
        }
        return Optional.of(
            LocalDate.parse(normalized, formatter).atStartOfDay().atOffset(ZoneOffset.UTC));
      } catch (DateTimeParseException ignored) {
      }
    }
    if (!FOUR_DIGIT_YEAR_PATTERN.matcher(normalized).find()) {
      return Optional.empty();
    }
    for (DateTimeFormatter formatter : POSTED_DATE_FORMATTERS) {
      try {
        LocalDate date = LocalDate.parse(normalized, formatter);
        return Optional.of(date.atStartOfDay().atOffset(ZoneOffset.UTC));
      } catch (DateTimeParseException ignored) {
      }
    }
    return Optional.empty();
  }

  private static String resolveUrl(String pageUrl, String href) {
    return URI.create(pageUrl).resolve(href).toString();
  }

  private static String stripTags(String value) {
    return htmlDecode(value.replaceAll("(?is)<[^>]+>", " ")).replaceAll("\\s+", " ").trim();
  }

  private static String htmlDecode(String value) {
    return value
        .replace("&amp;", "&")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
        .replace("&lt;", "<")
        .replace("&gt;", ">");
  }

  private static JobSourceFailure failure(
      WatchedSourceConfig source, String message, String failingUrl, Throwable cause) {
    return new JobSourceFailure(source, message, failingUrl, cause);
  }

  record ParsedPage(List<NormalizedJob> jobs, Optional<String> nextPageUrl) {
    ParsedPage {
      jobs = List.copyOf(jobs);
      nextPageUrl = nextPageUrl == null ? Optional.empty() : nextPageUrl;
    }
  }

  record PageResponse(int statusCode, String body) {}

  @FunctionalInterface
  interface PageFetcher {
    PageResponse fetch(String url) throws IOException, InterruptedException;
  }

  private static final class HttpClientPageFetcher implements PageFetcher {
    private final HttpClient httpClient;

    private HttpClientPageFetcher(HttpClient httpClient) {
      this.httpClient = httpClient;
    }

    @Override
    public PageResponse fetch(String url) throws IOException, InterruptedException {
      HttpRequest request =
          HttpRequest.newBuilder(URI.create(url))
              .timeout(REQUEST_TIMEOUT)
              .header("User-Agent", "Keep-In-Touch Job Watcher")
              .GET()
              .build();
      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      return new PageResponse(response.statusCode(), response.body());
    }
  }
}
