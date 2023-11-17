package com.berkley.singleton;

import org.springframework.stereotype.Component;

@Component
public class SchedulerState {
    private boolean initialized = false;

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }
}
