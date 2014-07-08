package com.cannontech.web.common.sort;

import com.cannontech.common.model.Direction;

public class SortableColumn {
    
    private Direction dir = Direction.desc;
    private boolean active;
    private String text;
    private String param;

    public SortableColumn(Direction dir, boolean active, String text, String param) {
        this.dir = dir;
        this.active = active;
        this.text = text;
        this.param = param;
    }

    public Direction getDir() {
        return dir;
    }

    public void setDir(Direction dir) {
        this.dir = dir;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    public String getParam() {
        return param;
    }
    
    public void setParam(String param) {
        this.param = param;
    }
    
}