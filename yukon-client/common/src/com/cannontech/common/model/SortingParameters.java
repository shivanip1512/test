package com.cannontech.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class SortingParameters {

    private final String sort; 
    private final Direction direction;
    
    private SortingParameters(String sort, Direction direction) {
        this.sort = sort;
        this.direction = direction;
    }

    public String getSort() {
        return sort;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return String.format("SortingParameters [sort=%s, direction=%s]", sort, direction);
    }

    @JsonCreator
    public static SortingParameters of(@JsonProperty("sort") String sort, @JsonProperty("direction") Direction direction) {
        return new SortingParameters(sort, direction);
    }
    
}
