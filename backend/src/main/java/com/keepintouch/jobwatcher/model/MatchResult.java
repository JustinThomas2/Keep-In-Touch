package com.keepintouch.jobwatcher.model;

import java.util.List;

public record MatchResult(
    boolean matched,
    List<String> includeKeywordMatches,
    List<String> excludeKeywordMatches,
    List<String> locationMatches,
    List<String> reasons) {

  public MatchResult {
    includeKeywordMatches = List.copyOf(includeKeywordMatches);
    excludeKeywordMatches = List.copyOf(excludeKeywordMatches);
    locationMatches = List.copyOf(locationMatches);
    reasons = List.copyOf(reasons);
  }

  public static MatchResult matched(List<String> includeMatches, List<String> locationMatches) {
    return new MatchResult(true, includeMatches, List.of(), locationMatches, List.of());
  }

  public static MatchResult rejected(
      List<String> includeMatches,
      List<String> excludeMatches,
      List<String> locationMatches,
      List<String> reasons) {
    return new MatchResult(false, includeMatches, excludeMatches, locationMatches, reasons);
  }
}
