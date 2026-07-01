package com.keepintouch.jobwatcher.logic;

import com.keepintouch.jobwatcher.model.DiscordEmbed;
import com.keepintouch.jobwatcher.model.DiscordEmbedField;
import com.keepintouch.jobwatcher.model.DiscordPayload;
import com.keepintouch.jobwatcher.model.MatchResult;
import com.keepintouch.jobwatcher.model.NormalizedJob;
import com.keepintouch.jobwatcher.model.WatchedSourceConfig;
import java.util.ArrayList;
import java.util.List;

public final class DiscordPayloadFormatter {

  private static final int MATCH_COLOR = 0x2ECC71;

  private DiscordPayloadFormatter() {}

  public static DiscordPayload formatMatch(
      WatchedSourceConfig source, NormalizedJob job, MatchResult matchResult) {
    List<DiscordEmbedField> fields = new ArrayList<>();
    addField(fields, "Company", source.companyName(), true);
    addField(fields, "Location", job.location(), true);
    addField(fields, "Department", job.department(), true);
    addField(fields, "Matched", String.join(", ", matchResult.includeKeywordMatches()), false);
    addField(fields, "Apply", job.applyUrl() == null ? job.effectiveUrl() : job.applyUrl(), false);

    DiscordEmbed embed =
        new DiscordEmbed(
            job.title(),
            job.effectiveUrl(),
            job.descriptionSnippet() == null ? "" : job.descriptionSnippet(),
            MATCH_COLOR,
            fields);
    return new DiscordPayload("New matching role found at " + source.companyName(), List.of(embed));
  }

  private static void addField(
      List<DiscordEmbedField> fields, String name, String value, boolean inline) {
    if (value == null || value.isBlank()) {
      return;
    }
    fields.add(new DiscordEmbedField(name, value, inline));
  }
}
