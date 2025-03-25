package com.anamaria.logmonitoring;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class LogProcessor {
    private final Map<Integer, Job> activeJobs = new HashMap<>();
    private static final int WARNING_THRESHOLD = 300;
    private static final int ERROR_THRESHOLD = 600;
    private static final Pattern TIME_PATTERN = Pattern.compile("\\d{2}:\\d{2}:\\d{2}");


    /**
     * Reads the input file line by line and processes each log entry.
     */
    public void processLogs(String inputFile, List<String> warnings, List<String> errors) {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;

            while ((line = br.readLine()) != null) {
                processLogEntry(line, warnings, errors);
            }

        } catch (IOException e) {
            System.err.println("Error reading log file: " + e.getMessage());
        }
    }


    /**
     * Parses and processes a single log line.
     * Accepts only entries that contain "job" in the description.
     */
    void processLogEntry(String line, List<String> warnings, List<String> errors) {
        String[] parts = line.split(",", 4);

        // Check for malformed entries or wrong time format
        if (parts.length != 4 || !TIME_PATTERN.matcher(parts[0].trim()).matches()) {
            System.err.println("Invalid log entry (skipped): " + line);
            return;
        }

        String time = parts[0].trim();
        String description = parts[1].trim();
        String action = parts[2].trim().toUpperCase();
        String pidStr = parts[3].trim();

        // Skip non job entries (ex. "scheduled task")
        if (!description.toLowerCase().contains("job")) {
            return;
        }

        try {
            int pid = Integer.parseInt(pidStr);
            int timestamp = parseTimestamp(time);

            switch (action) {
                case "START" -> activeJobs.put(pid, new Job(description, pid, timestamp));
                case "END"   -> processJobCompletion(pid, timestamp, warnings, errors);
                default      -> System.err.println("Invalid action type (skipped): " + line);
            }

        } catch (NumberFormatException e) {
            System.err.println("Invalid PID format (skipped): " + line);
        }
    }


    /**
     * Completes a job if a matching START exists and checks its duration
     * to determine if it exceeds warning or error thresholds.
     */
    private void processJobCompletion(int pid, int endTime, List<String> warnings, List<String> errors) {
        Optional.ofNullable(activeJobs.remove(pid)).ifPresentOrElse(
                job -> {
                    job.setEndTime(endTime);
                    int duration = job.getDuration();

                    String msg = "%s: %s (PID: %d) took %d seconds."
                            .formatted(
                                    duration > ERROR_THRESHOLD ? "ERROR" : "WARNING",
                                    job.getDuration(),
                                    pid,
                                    duration
                            );

                    if (duration > ERROR_THRESHOLD) errors.add(msg);
                    else if (duration > WARNING_THRESHOLD) warnings.add(msg);
                },

                () -> System.err.println("Unmatched END event (no START found) for PID: " + pid)
        );
    }

    /**
     * Converts HH:MM:SS string into number of seconds since 00:00:00.
     */
    private int parseTimestamp(String time) {
        var parts = time.split(":");

        return parseInt(parts[0]) * 3600 +
                parseInt(parts[1]) * 60 +
                parseInt(parts[2]);
    }
}
