package com.cannontech.web.common.sort;

public class SortableColumn {
    
    private Direction dir = Direction.desc;
    private boolean active;
    private boolean sortable = true;
    private String text;

    public SortableColumn(Direction dir, boolean active, boolean sortable, String text) {
        this.dir = dir;
        this.active = active;
        this.sortable = sortable;
        this.text = text;
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

    public boolean isSortable() {
        return sortable;
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
}