package com.cannontech.cbc.cyme.model;

import org.joda.time.Instant;

public class SimulationLoadFactor implements Comparable<SimulationLoadFactor> {
    private final Float load;
    private final Instant time;
    
    public SimulationLoadFactor(Float load, Instant time) {
        this.load = load;
        this.time = time;
    }

    public Float getLoad() {
        return load;
    }

    public Instant getTime() {
        return time;
    }

    @Override
    public int compareTo(SimulationLoadFactor o) {
        return time.compareTo(o.getTime());
    }
}
