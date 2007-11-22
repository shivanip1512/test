package com.cannontech.simplereport;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.jobs.support.YukonFactoryBeanDefinition;
import com.cannontech.simplereport.reportlayoutdata.ReportLayoutData;

public interface YukonReportDefinition<T extends BareReportModel> extends YukonFactoryBeanDefinition<T> {
    public ReportLayoutData getReportLayoutData();
}
