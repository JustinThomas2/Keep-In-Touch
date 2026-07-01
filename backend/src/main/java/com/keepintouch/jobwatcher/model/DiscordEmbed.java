package com.keepintouch.jobwatcher.model;

import java.util.List;

public record DiscordEmbed(
    String title, String url, String description, Integer color, List<DiscordEmbedField> fields) {

  public DiscordEmbed {
    title = title == null ? "" : title.trim();
    url = url == null ? "" : url.trim();
    description = description == null ? "" : description.trim();
    fields = List.copyOf(fields);
  }
}
