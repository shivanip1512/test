package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.KVarWattRPHModel;
import com.cannontech.simplereport.reportlayoutdata.KVarWattRPHReportLayoutData;
import com.cannontech.spring.YukonSpringHook;

public class KVarWattRPHReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData[] bodyColumns = new KVarWattRPHReportLayoutData().getBodyColumns();

    public KVarWattRPHReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public KVarWattRPHReport() {
        this((KVarWattRPHModel)YukonSpringHook.getBean("kVarWattModel"));
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }

}

