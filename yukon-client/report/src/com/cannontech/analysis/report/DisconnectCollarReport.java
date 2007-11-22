package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.DisconnectCollarModel;
import com.cannontech.simplereport.reportlayoutdata.DisconnectCollarReportLayoutData;
import com.cannontech.spring.YukonSpringHook;

public class DisconnectCollarReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData[] bodyColumns = new DisconnectCollarReportLayoutData().getBodyColumns();

    public DisconnectCollarReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public DisconnectCollarReport() {
        this((DisconnectCollarModel)YukonSpringHook.getBean("disconnectCollarModel"));
        //this(new DisconnectCollarModel());
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }

}

