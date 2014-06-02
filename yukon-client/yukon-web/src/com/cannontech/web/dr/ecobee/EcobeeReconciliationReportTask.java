package com.cannontech.web.dr.ecobee;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.ecobee.service.EcobeeReconciliationService;
import com.cannontech.jobs.support.YukonTaskBase;

public class EcobeeReconciliationReportTask extends YukonTaskBase {
    @Autowired private EcobeeReconciliationService reconciliationService;
    
    @Override
    public void start() {
        //TODO error handling?
        reconciliationService.runReconciliationReport();
    }
}
