package com.cannontech.stars.util.filter;


public class DirectionAwareOrderBy {
    public enum Direction { ASC, DESC };

    private final OrderBy orderBy;
    private final Direction direction;
    
    public DirectionAwareOrderBy(OrderBy orderBy, Direction direction) {
        this.orderBy = orderBy;
        this.direction = direction;
    }

    public DirectionAwareOrderBy(OrderBy orderBy, boolean isAscending) {
        this(orderBy, (isAscending) ? Direction.ASC : Direction.DESC);
    }
    
    public OrderBy getOrderBy() {
        return orderBy;
    }

    public Direction getDirection() {
        return direction;
    }

}
