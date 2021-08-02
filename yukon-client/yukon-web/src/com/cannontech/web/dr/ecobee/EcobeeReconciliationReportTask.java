package com.cannontech.web.dr.ecobee;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.service.EcobeeZeusReconciliationService;
import com.cannontech.jobs.support.YukonTaskBase;

public class EcobeeReconciliationReportTask extends YukonTaskBase {
    private static final Logger log = YukonLogManager.getLogger(EcobeeReconciliationReportTask.class);
    @Autowired private EcobeeZeusReconciliationService ecobeeZeusReconciliationService;
    
    @Override
    public void start() {
        log.info("Building ecobee reconciliation report.");
        try {
            ecobeeZeusReconciliationService.runReconciliationReport();
        } catch (EcobeeCommunicationException e) {
            log.error("Unable to complete the ecobee reconciliation report.", e);
        }
        log.info("Ecobee reconciliation report finished.");
    }
}
