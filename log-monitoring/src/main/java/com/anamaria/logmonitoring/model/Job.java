package com.anamaria.logmonitoring.model;

import lombok.Data;

@Data
public class Job {
    private final String description;
    private final int pid;
    private final int startTime;
    private int endTime = -1;

    public int getDuration() {
        return endTime - startTime;
    }
}
