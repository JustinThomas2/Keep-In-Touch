package com.keepintouch.jobwatcher.model;

public record DiscordEmbedField(String name, String value, boolean inline) {

  public DiscordEmbedField {
    name = name == null ? "" : name.trim();
    value = value == null ? "" : value.trim();
  }
}
