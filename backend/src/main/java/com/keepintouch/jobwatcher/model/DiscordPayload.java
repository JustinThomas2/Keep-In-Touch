package com.keepintouch.jobwatcher.model;

import java.util.List;

public record DiscordPayload(String content, List<DiscordEmbed> embeds) {

  public DiscordPayload {
    content = content == null ? "" : content.trim();
    embeds = List.copyOf(embeds);
  }
}
