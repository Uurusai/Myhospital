package com.hms.model;

import java.time.LocalTime;

public class TimeRange {
    public LocalTime startTime;
    public LocalTime endTime ;

    public TimeRange(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }
}
