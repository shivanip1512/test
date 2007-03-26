package com.cannontech.analysis.controller;

import java.util.HashSet;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.ServletRequestUtils;
import com.cannontech.analysis.tablemodel.CapControlFilterable;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.database.model.ModelFactory;

public abstract class CapControlReportControllerBase extends ReportControllerBase {
    @Override
    public void setRequestParameters(HttpServletRequest request) {
        CapControlFilterable filterableModel = (CapControlFilterable) model;
        super.setRequestParameters(request);
        int idsArray[] = ServletRequestUtils.getIntParameters(request, ReportModelBase.ATT_FILTER_MODEL_VALUES);
        HashSet<Integer> idsSet = new HashSet<Integer>();
        for (int id : idsArray) {
            idsSet.add(id);
        }

        int filterModelType = ServletRequestUtils.getIntParameter(request, ReportModelBase.ATT_FILTER_MODEL_TYPE, -1);
        
        switch(filterModelType) {
        
        case ModelFactory.CAPCONTROLFEEDER:
            filterableModel.setCapBankIdsFilter(null);
            filterableModel.setSubbusIdsFilter(null);
            filterableModel.setFeederIdsFilter(idsSet);
            break;
        case ModelFactory.CAPBANK:
            filterableModel.setFeederIdsFilter(null);
            filterableModel.setSubbusIdsFilter(null);
            filterableModel.setCapBankIdsFilter(idsSet);
            break;
        case ModelFactory.CAPCONTROLSTRATEGY:
            filterableModel.setCapBankIdsFilter(null);
            filterableModel.setFeederIdsFilter(null);
            filterableModel.setSubbusIdsFilter(idsSet);
            break;
        }
    }
    
}
