# Log Monitoring Application

A Java application that reads a log file, analyzes job durations and generates a report based on defined thresholds.

# What it does
- Parses a CSV log file containing START and END entries.
- Filters only entries with "job" in the description.
- Calculates job duration based on HH:MM:SS timestamps.
- Logs a warning if duration > 5 minutes (300s).
- Logs an error if duration > 10 minutes (600s).
- Writes the report to output.log.

# Tech
- Java 21 
- JUnit 5 for unit testing
- Lombok for boilerplate reduction

# What I tested
- LogProcessor is tested with unit tests for:
  - Valid jobs (under/over thresholds)
  - Invalid lines
  - Unmatched START or END
  - Invalid PIDs or timestamps

# How to Run
1. Place your input log file as 'logs.log' in the project root.
2. Build and run the project using your IDE.

# Performance
-Time complexity:
Each log line is processed once: O(n)
Access to jobs by PID: O(1) via HashMap
Overall: O(n) time 


# If I had more time...
- Add tests for LogFileWriter: Ensure file output formatting is correct.
- Test integration flow: Validate end-to-end functionality including actual file reading/writing.
- Add CLI support: Let users provide file paths from command-line.
- Use a logging framework (SLF4J + Logback): Replace System.err.println with proper structured logging.


