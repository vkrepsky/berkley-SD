package com.berkley.domain;

import java.time.LocalDateTime;

public class Time {

    private LocalDateTime time;

    public Time(){

    }

    public Time(LocalDateTime time) {
        setTime(time);
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
