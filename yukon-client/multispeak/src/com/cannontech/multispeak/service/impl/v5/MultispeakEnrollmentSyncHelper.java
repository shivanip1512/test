package com.cannontech.multispeak.service.impl.v5;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.multispeak.dao.v5.MultispeakEnrollmentDao;
import com.cannontech.multispeak.dao.v5.MultispeakSendEnrollmentCallback;
import com.cannontech.multispeak.service.DrJmsMessageService;
import com.cannontech.stars.dr.jms.message.DrJmsMessageType;
import com.cannontech.stars.dr.jms.message.EnrollmentJmsMessage;

public class MultispeakEnrollmentSyncHelper {
    @Autowired private DrJmsMessageService drJmsMessageService;
    @Autowired private MultispeakEnrollmentDao multispeakEnrollmentDao;
    
    private static Logger log = YukonLogManager.getLogger(MultispeakEnrollmentSyncHelper.class);

    public void startSync(MultispeakSendEnrollmentCallback callback) {
        List<EnrollmentJmsMessage> enrollmentMessages = multispeakEnrollmentDao.getEnrollmentMessagesToSend();
        int enrollmentMessageCount = 0;
        int unenrollmentMessageCount = 0;
        log.info("Sending " + enrollmentMessages.size() + " enrollment message for multispeak sync process.");
        for (EnrollmentJmsMessage msg : enrollmentMessages) {
            if(msg.getMessageType() == DrJmsMessageType.ENROLLMENT) {
                drJmsMessageService.enrollmentNotification(msg);
                enrollmentMessageCount++;
            } else {
                drJmsMessageService.unenrollmentNotification(msg);
                unenrollmentMessageCount++;
            }
            callback.enrollmentMessageSent();
        }
        log.info(enrollmentMessageCount + " enrollment and " + unenrollmentMessageCount + " unenrollment message sent for multispeak sync process.");
        // Call finish if all enrollment call is done.
        callback.finish();
    }
}
