package com.cannontech.analysis.tablemodel;

/**
 * If a class implements this interface in addition to the BareReportModel interface,
 * the loadData method will be called after initialization.
 */
public interface LoadableModel {
    public void loadData();
}
