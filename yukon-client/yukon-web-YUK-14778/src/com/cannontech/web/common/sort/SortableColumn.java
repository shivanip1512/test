package com.cannontech.web.common.sort;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;

public final class SortableColumn {
    
    private final Direction dir;
    private final boolean active;
    private final String text;
    private final String param;

    private SortableColumn(Direction dir, boolean active, String text, String param) {
        this.dir = dir;
        this.active = active;
        this.text = text;
        this.param = param;
    }
    
    public Direction getDir() {
        return dir;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public String getText() {
        return text;
    }
    
    public String getParam() {
        return param;
    }
    
    public static SortableColumn of(Direction dir, boolean active, String text, String param) {
        return new SortableColumn(dir, active, text, param);
    }
    
    public static SortableColumn of(SortingParameters sorting, String text, String param) {
        Direction dir = sorting.getDirection();
        boolean active = sorting.getSort().equalsIgnoreCase(param);
        return new SortableColumn(dir, active, text, param);
    }
    
}