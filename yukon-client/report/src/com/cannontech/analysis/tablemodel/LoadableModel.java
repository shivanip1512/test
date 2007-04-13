package com.cannontech.analysis.tablemodel;

import java.util.Date;

/**
 * If a class implements this interface in addition to the BareReportModel interface,
 * the loadData method will be called after initialization.
 */
public interface LoadableModel {
    public void loadData();
    public Date getLoadDate();
}
