package com.hms.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;

public class TimeDateRange implements Serializable {
    private LocalDateTime start ;
    private LocalDateTime end ;

    public TimeDateRange(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalDateTime getStart() {return start;}

    public LocalDateTime getEnd() {
        return end;
    }
    public long getDuration(){
        return Duration.between(start,end).toMillis();
    }
}
