package com.anamaria.logmonitoring;

import java.util.ArrayList;

public class LogMonitoringApplication {
    private static final String INPUT_FILE = "logs.log";
    private static final String OUTPUT_FILE = "output.log";

    public static void main(String[] args) {
        var warnings = new ArrayList<String>();
        var errors = new ArrayList<String>();

        new LogProcessor().processLogs(INPUT_FILE, warnings, errors);
        LogFileWriter.writeReport(OUTPUT_FILE, warnings, errors);
        System.out.println("Log processing complete. Report saved to: " + OUTPUT_FILE);
    }
}