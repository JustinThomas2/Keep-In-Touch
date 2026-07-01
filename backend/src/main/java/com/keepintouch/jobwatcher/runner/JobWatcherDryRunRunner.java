package com.keepintouch.jobwatcher.runner;

import com.keepintouch.domain.JobSourceType;
import com.keepintouch.jobwatcher.adapter.JobSourceAdapter;
import com.keepintouch.jobwatcher.logic.DeterministicJobMatcher;
import com.keepintouch.jobwatcher.logic.DiscordPayloadFormatter;
import com.keepintouch.jobwatcher.logic.JobIdentityGenerator;
import com.keepintouch.jobwatcher.model.DiscordEmbed;
import com.keepintouch.jobwatcher.model.DiscordEmbedField;
import com.keepintouch.jobwatcher.model.DiscordPayload;
import com.keepintouch.jobwatcher.model.JobMatchCriteria;
import com.keepintouch.jobwatcher.model.JobSourceFailure;
import com.keepintouch.jobwatcher.model.JobSourceResult;
import com.keepintouch.jobwatcher.model.JobSourceSuccess;
import com.keepintouch.jobwatcher.model.MatchResult;
import com.keepintouch.jobwatcher.model.NormalizedJob;
import com.keepintouch.jobwatcher.model.WatchedSourceConfig;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class JobWatcherDryRunRunner {

  private final Map<JobSourceType, JobSourceAdapter> adaptersBySourceType;

  public JobWatcherDryRunRunner(List<JobSourceAdapter> adapters) {
    this.adaptersBySourceType =
        adapters.stream()
            .collect(Collectors.toMap(JobSourceAdapter::sourceType, adapter -> adapter));
  }

  public RunSummary run(
      List<WatchedSourceConfig> sources,
      Function<WatchedSourceConfig, Optional<JobMatchCriteria>> criteriaLoader,
      PrintStream out) {
    RunSummary summary = new RunSummary();
    out.println("Job watcher dry run");
    out.println("Mode: dry-run");
    out.println("Preview only: no database writes and no Discord sends.");
    out.println();

    for (WatchedSourceConfig source : sources) {
      summary.sourcesChecked++;
      out.println("Source: " + source.companyName() + " [" + source.sourceType() + "]");

      JobSourceAdapter adapter = adaptersBySourceType.get(source.sourceType());
      if (adapter == null) {
        summary.sourceFailures++;
        out.println("  Failure: no adapter registered for " + source.sourceType());
        out.println();
        continue;
      }

      JobSourceResult result = adapter.fetchAndParse(source);
      if (result instanceof JobSourceFailure failure) {
        summary.sourceFailures++;
        out.println("  Failure: " + failure.message());
        if (failure.failingUrl() != null) {
          out.println("  Failing URL: " + failure.failingUrl());
        }
        out.println();
        continue;
      }

      JobSourceSuccess success = (JobSourceSuccess) result;
      summary.sourceSuccesses++;
      summary.jobsFound += success.jobs().size();
      out.println("  Success: " + success.jobs().size() + " jobs found");

      Optional<JobMatchCriteria> criteria = criteriaLoader.apply(source);
      if (criteria.isEmpty()) {
        summary.sourcesSkippedForMissingRules++;
        out.println("  Skipped matching: no active job_match_rules found for this source company.");
        out.println();
        continue;
      }
      for (NormalizedJob job : success.jobs()) {
        String stableKey = JobIdentityGenerator.stableKey(source, job);
        String contentHash = JobIdentityGenerator.contentHash(job);
        MatchResult matchResult = DeterministicJobMatcher.match(job, criteria.get());

        out.println("  Found: " + job.title());
        out.println("    Stable key: " + stableKey);
        out.println("    Content hash: " + contentHash);
        out.println("    Location: " + valueOrUnknown(job.location()));
        out.println("    URL: " + job.effectiveUrl());

        if (matchResult.matched()) {
          summary.matchedJobs++;
          out.println("    Match: yes");
          if (!matchResult.reasons().isEmpty()) {
            out.println("    Match notes: " + String.join(" ", matchResult.reasons()));
          }
          printDiscordPreview(source, job, matchResult, out);
        } else {
          out.println("    Match: no");
          out.println("    Reasons: " + String.join(" ", matchResult.reasons()));
        }
      }
      out.println();
    }

    out.println("Summary");
    out.println("  Sources checked: " + summary.sourcesChecked);
    out.println("  Source successes: " + summary.sourceSuccesses);
    out.println("  Source failures: " + summary.sourceFailures);
    out.println("  Sources skipped for missing rules: " + summary.sourcesSkippedForMissingRules);
    out.println("  Jobs found: " + summary.jobsFound);
    out.println("  Matched jobs: " + summary.matchedJobs);
    out.println("  Discord sends: 0 (dry-run)");
    return summary;
  }

  private static void printDiscordPreview(
      WatchedSourceConfig source, NormalizedJob job, MatchResult matchResult, PrintStream out) {
    DiscordPayload payload = DiscordPayloadFormatter.formatMatch(source, job, matchResult);
    out.println("    Discord payload preview:");
    out.println("      Content: " + payload.content());
    for (DiscordEmbed embed : payload.embeds()) {
      out.println("      Embed: " + embed.title());
      out.println("      URL: " + embed.url());
      for (DiscordEmbedField field : embed.fields()) {
        out.println("      " + field.name() + ": " + field.value());
      }
    }
  }

  private static String valueOrUnknown(String value) {
    return value == null || value.isBlank() ? "unknown" : value;
  }

  public static class RunSummary {
    private int sourcesChecked;
    private int sourceSuccesses;
    private int sourceFailures;
    private int sourcesSkippedForMissingRules;
    private int jobsFound;
    private int matchedJobs;

    public int sourcesChecked() {
      return sourcesChecked;
    }

    public int sourceSuccesses() {
      return sourceSuccesses;
    }

    public int sourceFailures() {
      return sourceFailures;
    }

    public int sourcesSkippedForMissingRules() {
      return sourcesSkippedForMissingRules;
    }

    public int jobsFound() {
      return jobsFound;
    }

    public int matchedJobs() {
      return matchedJobs;
    }
  }
}
