package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.TimeControlledCapBanksModel;
import com.cannontech.simplereport.reportlayoutdata.TimeControlledCapBanksReportLayoutData;
import com.cannontech.spring.YukonSpringHook;

public class TimeControlledCapBanksReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData[] bodyColumns = new TimeControlledCapBanksReportLayoutData().getBodyColumns();

    public TimeControlledCapBanksReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public TimeControlledCapBanksReport() {
        this((TimeControlledCapBanksModel)YukonSpringHook.getBean("timeControlledCapBanksModel"));
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }
    
}
