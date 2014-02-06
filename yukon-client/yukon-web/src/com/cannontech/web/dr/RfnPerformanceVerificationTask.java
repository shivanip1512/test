package com.cannontech.web.dr;

import static com.cannontech.system.GlobalSettingType.RF_BROADCAST_PERFORMANCE;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.model.PerformanceVerificationEventMessage;
import com.cannontech.dr.rfn.dao.PerformanceVerificationDao;
import com.cannontech.jobs.support.YukonTaskBase;
import com.cannontech.stars.dr.hardware.model.LmCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandParam;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.cannontech.stars.dr.hardware.service.impl.RfCommandStrategy;
import com.cannontech.system.PreferenceOnOff;
import com.cannontech.system.dao.GlobalSettingDao;

public class RfnPerformanceVerificationTask extends YukonTaskBase {
    private static final Logger log = YukonLogManager.getLogger(RfnPerformanceVerificationTask.class);
    
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private PerformanceVerificationDao performanceVerificationDao;
    @Autowired private RfCommandStrategy rfCommandStrategy;
    
    @Override
    public void start() {
        PreferenceOnOff preference = globalSettingDao.getEnum(RF_BROADCAST_PERFORMANCE, PreferenceOnOff.class);
        
        if (preference == PreferenceOnOff.ON) {
            try {
                log.debug("Using broadcast messaging for performance verification command.");

                // Create the event.
                PerformanceVerificationEventMessage verificationEvent = performanceVerificationDao.createVerificationEvent();
                
                // Log the enrolled devices we care about.
                performanceVerificationDao.writeNewVerificationEventForEnrolledDevices(verificationEvent.getMessageId());
                
                LmCommand command = new LmCommand();
                command.setType(LmHardwareCommandType.PERFORMANCE_VERIFICATION);
                command.getParams().put(LmHardwareCommandParam.UNIQUE_MESSAGE_ID, verificationEvent.getMessageId());
                
                rfCommandStrategy.sendBroadcastCommand(command);
            } catch (Exception e) {
                log.error("Error occurred during the sending of the RFN performance verification message.", e);
            }
        } else {
            log.debug("RF Broadcast Performance is disabled, not sending broadcast performance verification message.");
        }
    }
}
