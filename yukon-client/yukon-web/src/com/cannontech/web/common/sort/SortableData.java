package com.cannontech.web.common.sort;

import java.util.List;

public class SortableData {
    
    private List<? extends Object> data;
    private List<SortableColumn> columns;
    
    public SortableData(List<? extends Object> data, List<SortableColumn> columns) {
        this.data = data;
        this.columns = columns;
    }
    
    public List<? extends Object> getData() {
        return data;
    }
    
    public void setData(List<? extends Object> data) {
        this.data = data;
    }
    
    public List<SortableColumn> getColumns() {
        return columns;
    }
    
    public void setColumns(List<SortableColumn> columns) {
        this.columns = columns;
    }
    
}