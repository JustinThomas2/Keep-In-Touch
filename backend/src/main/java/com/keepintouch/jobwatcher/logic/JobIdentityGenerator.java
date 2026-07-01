package com.keepintouch.jobwatcher.logic;

import com.keepintouch.jobwatcher.model.NormalizedJob;
import com.keepintouch.jobwatcher.model.WatchedSourceConfig;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;
import java.util.stream.Stream;

public final class JobIdentityGenerator {

  private JobIdentityGenerator() {}

  public static String stableKey(WatchedSourceConfig source, NormalizedJob job) {
    String sourcePrefix = source.sourceType().name();
    if (job.externalId() != null) {
      return sourcePrefix + ":" + normalizeToken(job.externalId());
    }
    return sourcePrefix + ":url:" + sha256(normalizeUrl(job.effectiveUrl())).substring(0, 32);
  }

  public static String contentHash(NormalizedJob job) {
    String content =
        String.join(
            "\n",
            Stream.of(
                    job.externalId(),
                    job.title(),
                    job.location(),
                    job.country(),
                    normalizeUrl(job.effectiveUrl()),
                    job.applyUrl(),
                    job.department(),
                    job.jobCategory(),
                    job.experienceLevel(),
                    job.postedAt() == null ? null : job.postedAt().toInstant().toString(),
                    job.descriptionSnippet())
                .map(JobIdentityGenerator::normalizeNullable)
                .toList());
    return sha256(content);
  }

  private static String normalizeUrl(String url) {
    try {
      URI uri = new URI(url.trim()).normalize();
      String scheme = uri.getScheme() == null ? null : uri.getScheme().toLowerCase(Locale.ROOT);
      String host = uri.getHost() == null ? null : uri.getHost().toLowerCase(Locale.ROOT);
      String path = trimTrailingSlash(uri.getPath());
      return new URI(scheme, uri.getUserInfo(), host, uri.getPort(), path, uri.getQuery(), null)
          .toString();
    } catch (URISyntaxException ex) {
      return normalizeNullable(url);
    }
  }

  private static String trimTrailingSlash(String value) {
    if (value == null || value.length() <= 1) {
      return value;
    }
    return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
  }

  private static String normalizeToken(String value) {
    return value.trim().toLowerCase(Locale.ROOT);
  }

  private static String normalizeNullable(String value) {
    return value == null ? "" : value.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
  }

  private static String sha256(String value) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException ex) {
      throw new IllegalStateException("SHA-256 is not available", ex);
    }
  }
}
