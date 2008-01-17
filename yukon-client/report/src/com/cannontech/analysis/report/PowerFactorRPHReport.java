package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.PowerFactorRPHModel;
import com.cannontech.simplereport.reportlayoutdata.PowerFactorRPHReportLayoutData;
import com.cannontech.spring.YukonSpringHook;

public class PowerFactorRPHReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData[] bodyColumns = new PowerFactorRPHReportLayoutData().getBodyColumns();

    public PowerFactorRPHReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public PowerFactorRPHReport() {
        this((PowerFactorRPHModel)YukonSpringHook.getBean("powerFactorModel"));
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }

}

