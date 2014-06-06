package com.cannontech.common.model;

public class SortingParameters {

    private String sort; 
    private Direction direction;
    
    public SortingParameters(String sort, Direction direction) {
        this.sort = sort;
        this.direction = direction;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return String.format("SortingParameters [sort=%s, direction=%s]", sort, direction);
    }
    
}
