package com.anamaria.logmonitoring;

import com.anamaria.logmonitoring.util.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class LogProcessorTest {
    private final LogProcessor processor = new LogProcessor();


    @Test
    void jobUnder300ShouldNotBeReported() {
        Result result = processLines(
                "10:00:00,background job 001, START,1",
                "10:04:00,background job 001, END,1"
        );

        assertTrue(result.warnings.isEmpty());
        assertTrue(result.errors.isEmpty());
    }

    @Test
    void jobOver300ShouldTriggerWarning() {
        Result result = processLines(
                "11:00:00,background job 002, START,2",
                "11:05:01,background job 002, END,2"
        );

        assertEquals(1, result.warnings.size());
        assertTrue(result.warnings.get(0).contains("WARNING"));
    }

    @Test
    void jobOver600ShouldTriggerError() {
        Result result = processLines(
                "12:00:00,background job 003, START,3",
                "12:10:15,background job 003, END,3"
        );

        assertEquals(1, result.errors.size());
        assertTrue(result.errors.get(0).contains("ERROR"));
    }

    @Test
    void nonJobShouldBeIgnored() {
        Result result = processLines(
                "13:00:00,scheduled task 004, START,4",
                "13:10:00,scheduled task 004, END,4"
        );//task should be ignored

        assertTrue(result.warnings.isEmpty());
        assertTrue(result.errors.isEmpty());
    }

    @Test
    void invalidLineFormatShouldBeIgnored() {
        Result result = processLines(
                "invalid,line,missing,fields"
        );

        assertTrue(result.warnings.isEmpty());
        assertTrue(result.errors.isEmpty());
    }

    @Test
    void endWithoutStartShouldNotProduceAnything() {
        Result result = processLines(
                "14:00:00,scheduled job 005, END,5"
        );

        assertTrue(result.warnings.isEmpty());
        assertTrue(result.errors.isEmpty());
    }

    @Test
    void startWithoutEndShouldNotProduceAnything() {
        Result result = processLines(
                "16:00:00,background job lone, START,6"
        );

        assertTrue(result.warnings.isEmpty());
        assertTrue(result.errors.isEmpty());
    }

    @Test
    void invalidPidShouldBeIgnored() {
        Result result = processLines(
                "15:00:00,background job 006, START,not_a_number",
                "15:11:00,background job 006, END,6"
        );

        assertTrue(result.warnings.isEmpty());
        assertTrue(result.errors.isEmpty());
    }

    @Test
    void multipleJobsWithMixedResults() {
        Result result = processLines(
                // warning
                "10:00:00,background job a, START,10",
                "10:05:01,background job a, END,10",

                // error
                "11:00:00,background job b, START,11",
                "11:10:30,background job b, END,11",

                // ignored
                "12:00:00,scheduled task x, START,12",
                "12:10:00,scheduled task x, END,12"
        );
        assertEquals(1, result.warnings.size());
        assertEquals(1, result.errors.size());
    }

    private Result processLines(String... lines) {
        Result result = new Result();

        for (String line : lines) {
            processor.processLogEntry(line, result.warnings, result.errors);
        }
        return result;
    }
}
