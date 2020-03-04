package com.cannontech.web.stars.mapNetwork;

public enum NearbyMiles {
    
    QUARTER(0.25),
    HALF(0.5),
    ONE(1),
    FIVE(5),
    TEN(10);
    
    private final double value;
    
    private NearbyMiles(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

}
