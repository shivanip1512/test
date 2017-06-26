package com.cannontech.common.tdc.model;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Column {
    private int displayId;
    private String title;
    private ColumnType type;
    private int order;
    private int width;

    public Column() {

    }
    
    public Column(ColumnType type, int order) {
        this.title = type.getColumnName();
        this.type = type;
        this.width = type.getDefaultWidth();
        this.order = order;
    }
    
    public int getDisplayId() {
        return displayId;
    }

    public void setDisplayId(int displayId) {
        this.displayId = displayId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public ColumnType getType() {
        return type;
    }

    public void setType(ColumnType type) {
        this.type = type;
    }
    
    /**
     * Returns columns that can be created by Web TDC.
     */
    public static List<Column> getDefaultColumns() {
        AtomicInteger ordering = new AtomicInteger(0);
        return ColumnType.getDefaultColumns().stream().map(type -> new Column(type, ordering.incrementAndGet())).collect(Collectors.toList());
    }
    
    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.append("displayId=" + displayId);
        tsb.append("title=" + title);
        tsb.append("type=" + type);
        tsb.append("order=" + order);
        tsb.append("width=" + width);
        return tsb.toString();
    }
}
