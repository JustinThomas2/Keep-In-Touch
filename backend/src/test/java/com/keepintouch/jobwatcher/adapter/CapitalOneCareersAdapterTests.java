package com.keepintouch.jobwatcher.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.keepintouch.domain.JobSourceType;
import com.keepintouch.jobwatcher.model.JobSourceFailure;
import com.keepintouch.jobwatcher.model.JobSourceResult;
import com.keepintouch.jobwatcher.model.JobSourceSuccess;
import com.keepintouch.jobwatcher.model.WatchedSourceConfig;
import java.net.http.HttpTimeoutException;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CapitalOneCareersAdapterTests {

  @Test
  void parsesCapitalOneResultCards() {
    CapitalOneCareersAdapter adapter = new CapitalOneCareersAdapter(url -> ok(searchResultsHtml()));

    JobSourceSuccess result = (JobSourceSuccess) adapter.fetchAndParse(source());

    assertThat(result.jobs()).hasSize(2);
    assertThat(result.canonicalSourceUrl())
        .isEqualTo("https://www.capitalonecareers.com/search-jobs/software%20engineer/234/1");
    assertThat(result.jobs().getFirst().externalId()).isEqualTo("R12345");
    assertThat(result.jobs().getFirst().title()).isEqualTo("Senior Software Engineer, Front End");
    assertThat(result.jobs().getFirst().location()).isEqualTo("McLean, VA");
    assertThat(result.jobs().getFirst().country()).isEqualTo("US");
    assertThat(result.jobs().getFirst().url())
        .isEqualTo(
            "https://www.capitalonecareers.com/job/mclean/senior-software-engineer/234/12345");
    assertThat(result.jobs().getFirst().canonicalUrl()).isEqualTo(result.jobs().getFirst().url());
    assertThat(result.jobs().getFirst().postedAt())
        .isEqualTo(OffsetDateTime.parse("2026-06-15T00:00:00Z"));

    assertThat(result.jobs().get(1).externalId()).isEqualTo("R67890");
    assertThat(result.jobs().get(1).location()).isEqualTo("Remote - United States");
    assertThat(result.jobs().get(1).country()).isEqualTo("US");
  }

  @Test
  void followsNextPaginationLinks() {
    Map<String, String> pages =
        Map.of(
            "https://www.capitalonecareers.com/search-jobs/software%20engineer/234/1",
            pageWithOneJobAndNext("R1", "/job/one/234/1", "/search-jobs/software%20engineer/234/2"),
            "https://www.capitalonecareers.com/search-jobs/software%20engineer/234/2",
            pageWithOneJobAndNext("R2", "/job/two/234/2", null));
    CapitalOneCareersAdapter adapter =
        new CapitalOneCareersAdapter(url -> ok(pages.getOrDefault(url, "")));

    JobSourceSuccess result = (JobSourceSuccess) adapter.fetchAndParse(source());

    assertThat(result.jobs()).extracting("externalId").containsExactly("R1", "R2");
  }

  @Test
  void fallsBackToUrlSegmentWhenDataJobIdIsMissing() {
    CapitalOneCareersAdapter adapter =
        new CapitalOneCareersAdapter(url -> ok(jobWithoutDataJobIdHtml()));

    JobSourceSuccess result = (JobSourceSuccess) adapter.fetchAndParse(source());

    assertThat(result.jobs()).hasSize(1);
    assertThat(result.jobs().getFirst().externalId()).isEqualTo("99999");
    assertThat(result.jobs().getFirst().title()).isEqualTo("Software Engineer");
    assertThat(result.jobs().getFirst().country()).isEqualTo("US");
  }

  @Test
  void returnsNullPostedAtForYearlessPostedDates() {
    CapitalOneCareersAdapter adapter =
        new CapitalOneCareersAdapter(url -> ok(jobWithPostedDate("Jun 15")));

    JobSourceSuccess result = (JobSourceSuccess) adapter.fetchAndParse(source());

    assertThat(result.jobs()).hasSize(1);
    assertThat(result.jobs().getFirst().postedAt()).isNull();
  }

  @Test
  void parsesTextPostedDatesWhenYearIsPresent() {
    CapitalOneCareersAdapter adapter =
        new CapitalOneCareersAdapter(url -> ok(jobWithPostedDate("Jun 15, 2026")));

    JobSourceSuccess result = (JobSourceSuccess) adapter.fetchAndParse(source());

    assertThat(result.jobs()).hasSize(1);
    assertThat(result.jobs().getFirst().postedAt())
        .isEqualTo(OffsetDateTime.parse("2026-06-15T00:00:00Z"));
  }

  @Test
  void treatsZeroResultsAsSuccessfulEmptyResult() {
    CapitalOneCareersAdapter adapter =
        new CapitalOneCareersAdapter(url -> ok("<html><body><h1>0 results</h1></body></html>"));

    JobSourceSuccess result = (JobSourceSuccess) adapter.fetchAndParse(source());

    assertThat(result.jobs()).isEmpty();
  }

  @Test
  void failsClearlyWhenResultCardIsMissingRequiredLink() {
    CapitalOneCareersAdapter adapter = new CapitalOneCareersAdapter(url -> ok(missingLinkHtml()));

    JobSourceFailure result = (JobSourceFailure) adapter.fetchAndParse(source());

    assertThat(result.message()).isEqualTo("Capital One result card is missing a job detail link.");
    assertThat(result.failingUrl())
        .isEqualTo("https://www.capitalonecareers.com/search-jobs/software%20engineer/234/1");
  }

  @Test
  void returnsFailureForNonSuccessfulHttpStatus() {
    CapitalOneCareersAdapter adapter =
        new CapitalOneCareersAdapter(url -> new CapitalOneCareersAdapter.PageResponse(503, ""));

    JobSourceResult result = adapter.fetchAndParse(source());

    assertThat(result).isInstanceOf(JobSourceFailure.class);
    assertThat(((JobSourceFailure) result).message())
        .isEqualTo("Capital One careers returned HTTP 503.");
  }

  @Test
  void returnsFailureWhenRequestTimesOut() {
    CapitalOneCareersAdapter adapter =
        new CapitalOneCareersAdapter(
            url -> {
              throw new HttpTimeoutException("request timed out");
            });

    JobSourceResult result = adapter.fetchAndParse(source());

    assertThat(result).isInstanceOf(JobSourceFailure.class);
    JobSourceFailure failure = (JobSourceFailure) result;
    assertThat(failure.message()).isEqualTo("Timed out fetching Capital One careers page.");
    assertThat(failure.failingUrl())
        .isEqualTo("https://www.capitalonecareers.com/search-jobs/software%20engineer/234/1");
    assertThat(failure.cause()).isInstanceOf(HttpTimeoutException.class);
  }

  @Test
  void parserCanBeExercisedWithoutHttp() {
    CapitalOneCareersAdapter adapter = new CapitalOneCareersAdapter(url -> ok(""));

    CapitalOneCareersAdapter.ParsedPage page =
        adapter.parsePage(searchResultsHtml(), source().effectiveSourceUrl());

    assertThat(page.jobs()).hasSize(2);
    assertThat(page.nextPageUrl()).isEmpty();
    assertThatThrownBy(() -> adapter.parsePage(" ", source().effectiveSourceUrl()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Capital One careers returned an empty page.");
  }

  private static WatchedSourceConfig source() {
    return new WatchedSourceConfig(
        UUID.fromString("00000000-0000-0000-0000-000000000001"),
        UUID.fromString("00000000-0000-0000-0000-000000000002"),
        "Capital One",
        JobSourceType.CAPITAL_ONE_CAREERS,
        "https://www.capitalonecareers.com/search-jobs/software%20engineer/234/1",
        null);
  }

  private static CapitalOneCareersAdapter.PageResponse ok(String body) {
    return new CapitalOneCareersAdapter.PageResponse(200, body);
  }

  private static String searchResultsHtml() {
    return """
        <html>
          <body>
            <ul class="jobs-list">
              <li data-job-id="R12345">
                <a href="/job/mclean/senior-software-engineer/234/12345">
                  Senior Software Engineer, Front End
                </a>
                <span class="job-location">McLean, VA</span>
                <span class="job-date-posted">2026-06-15</span>
              </li>
              <li data-job-id="R67890">
                <a href="https://www.capitalonecareers.com/job/remote/software-engineer/234/67890">
                  Software Engineer
                </a>
                <span class="job-location">Remote - United States</span>
              </li>
            </ul>
          </body>
        </html>
        """;
  }

  private static String pageWithOneJobAndNext(String externalId, String href, String nextHref) {
    String nextLink = nextHref == null ? "" : "<a href=\"" + nextHref + "\">Next</a>";
    return """
        <html>
          <body>
            <li data-job-id="%s">
              <a href="%s">Software Engineer</a>
              <span class="job-location">New York, NY</span>
            </li>
            %s
          </body>
        </html>
        """
        .formatted(externalId, href, nextLink);
  }

  private static String missingLinkHtml() {
    return """
        <html>
          <body>
            <li data-job-id="R12345">
              <span class="job-location">McLean, VA</span>
            </li>
          </body>
        </html>
        """;
  }

  private static String jobWithoutDataJobIdHtml() {
    return """
        <html>
          <body>
            <li><a href="/search-jobs/software">Software search</a></li>
            <li>
              <a href="/job/new-york/software-engineer/234/99999">Software Engineer</a>
              <span class="job-location">New York, NY</span>
            </li>
          </body>
        </html>
        """;
  }

  private static String jobWithPostedDate(String postedDate) {
    return """
        <html>
          <body>
            <li data-job-id="R12345">
              <a href="/job/mclean/software-engineer/234/12345">Software Engineer</a>
              <span class="job-location">McLean, VA</span>
              <span class="job-date-posted">%s</span>
            </li>
          </body>
        </html>
        """
        .formatted(postedDate);
  }
}
