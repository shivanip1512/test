package com.cannontech.analysis.tablemodel;

import com.cannontech.analysis.ColumnProperties;

/**
 * This class is currently used to tie the BareReportModel, a BareReportModelAdapter,
 * and a SimpleYukonReportBase.
 */
public interface ReportModelLayout {

    ColumnProperties getColumnProperties(int i);

}
