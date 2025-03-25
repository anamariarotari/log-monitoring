package com.anamaria.logmonitoring;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public class LogFileWriter {
    public static void writeReport(String outputFile, List<String> warnings, List<String> errors) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            writeSection(bw, "Warnings", warnings);
            writeSection(bw, "Errors", errors);
        } catch (IOException e) {
            System.err.println("Error writing log file: " + e.getMessage());
        }
    }

    private static void writeSection(BufferedWriter writer, String title, List<String> messages) {
        try {
            writer.write("---- %s ----%n".formatted(title));

            writer.write(
                    messages.isEmpty()
                            ? "No %s found.%n".formatted(title.toLowerCase())
                            : messages.stream()
                            .map(msg -> msg + System.lineSeparator())
                            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                            .toString()
            );

            writer.write(System.lineSeparator());
        } catch (IOException e) {
            throw new UncheckedIOException("Failed writing section: " + title, e);
        }
    }

}
