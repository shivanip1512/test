package com.cannontech.common.model;

public class SortingParameters {

    private final String sort; 
    private final Direction direction;
    
    public SortingParameters(String sort, Direction direction) {
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
    
}
