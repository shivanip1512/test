package com.cannontech.web.dr.ecobee;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.service.EcobeeReconciliationService;
import com.cannontech.dr.ecobee.service.EcobeeZeusReconciliationService;
import com.cannontech.jobs.support.YukonTaskBase;

public class EcobeeReconciliationReportTask extends YukonTaskBase {
    private static final Logger log = YukonLogManager.getLogger(EcobeeReconciliationReportTask.class);
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private EcobeeReconciliationService reconciliationService;
    @Autowired private EcobeeZeusReconciliationService ecobeeZeusReconciliationService;
    
    @Override
    public void start() {
        log.info("Building ecobee reconciliation report.");
        try {
            if (configurationSource.getBoolean(MasterConfigBoolean.ECOBEE_ZEUS_ENABLED)) {
                ecobeeZeusReconciliationService.runReconciliationReport();
            } else {
                reconciliationService.runReconciliationReport();
            }
        } catch (EcobeeCommunicationException e) {
            log.error("Unable to complete the ecobee reconciliation report.", e);
        }
        log.info("Ecobee reconciliation report finished.");
    }
}
