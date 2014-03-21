package com.cannontech.common.tdc.model;

import static com.cannontech.common.tdc.model.IDisplay.EVENT_VIEWER_DISPLAY_NUMBER;
import static com.cannontech.common.tdc.model.IDisplay.SOE_LOG_DISPLAY_NUMBER;
import static com.cannontech.common.tdc.model.IDisplay.TAG_LOG_DISPLAY_NUMBER;

import java.util.List;
import java.util.NoSuchElementException;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class Display {
    private int displayId;
    private String name;
    private DisplayType type;
    private String title;
    private String description;
    private List<Column> columns;
    private boolean acknowledgable;
        
    public int getDisplayId() {
        return displayId;
    }
    public void setDisplayId(int displayId) {
        this.displayId = displayId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public DisplayType getType() {
        return type;
    }
    public void setType(DisplayType type) {
        this.type = type;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public List<Column> getColumns() {
        return columns;
    }
    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }
    public boolean isAcknowledgable() {
        return acknowledgable;
    }
    public void setAcknowledgable(boolean acknowledge) {
        this.acknowledgable = acknowledge;
    }
    
    public boolean hasColumn(final ColumnType type) {
        try {
            Iterables.find(columns, new Predicate<Column>() {
                @Override
                public boolean apply(Column c) {
                    return c.getType() == type;
                }
            });
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    public boolean isPageable() {
        return displayId == SOE_LOG_DISPLAY_NUMBER || displayId == TAG_LOG_DISPLAY_NUMBER
               || displayId == EVENT_VIEWER_DISPLAY_NUMBER;
    }
}
