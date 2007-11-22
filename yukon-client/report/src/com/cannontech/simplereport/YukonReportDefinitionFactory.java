package com.cannontech.simplereport;

import com.cannontech.analysis.tablemodel.BareReportModel;

public interface YukonReportDefinitionFactory<T extends BareReportModel> {
    public YukonReportDefinition<T> getReportDefinition(String reportDefinition);
}
