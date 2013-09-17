package com.cannontech.common.tdc.model;

import java.util.List;


import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class Display {
    private int displayId;
    private String name;
    private DisplayTypeEnum type;
    private String title;
    private String description;
    private List<Column> columns;
    private boolean acknowledge;
        
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
    public DisplayTypeEnum getType() {
        return type;
    }
    public void setType(DisplayTypeEnum type) {
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
    public boolean isAcknowledge() {
        return acknowledge;
    }
    public void setAcknowledge(boolean acknowledge) {
        this.acknowledge = acknowledge;
    }
    
    public boolean hasColumn(final ColumnTypeEnum type) {
        Column column = Iterables.find(columns, new Predicate<Column>() {
            @Override
            public boolean apply(Column c) {
                return c.getType() == type;
            }
        });
        if (column == null) {
            return false;
        }
        return true;
    }
}
