package com.cannontech.web.bulk.model;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.simplereport.ColumnInfo;

/**
 * Data class which represents the meta data needed to setup an editor grid.
 */
public class MetaObject {

    private String totalProperty = null;
    private String root = null;
    private String id = null;
    private List<ColumnInfo> columns = new ArrayList<ColumnInfo>();

    public String getTotalProperty() {
        return totalProperty;
    }

    public void setTotalProperty(String totalProperty) {
        this.totalProperty = totalProperty;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ColumnInfo> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnInfo> columns) {
        this.columns = columns;
    }

}