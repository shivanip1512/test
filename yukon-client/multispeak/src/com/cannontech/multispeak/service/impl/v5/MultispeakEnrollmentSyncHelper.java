package com.cannontech.multispeak.service.impl.v5;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.multispeak.dao.v5.MultispeakSendEnrollmentCallback;
import com.cannontech.multispeak.service.DrJmsMessageService;
import com.cannontech.stars.dr.jms.message.EnrollmentJmsMessage;

public class MultispeakEnrollmentSyncHelper {
    @Autowired private DrJmsMessageService drJmsMessageService;
    
    private static Logger log = YukonLogManager.getLogger(MultispeakEnrollmentSyncHelper.class);

    public void startSync(MultispeakSendEnrollmentCallback callback) {
        // TODO : Add dao call here after YUK-21332. Enrollment/Unenrollment call needs to be added.
        List<EnrollmentJmsMessage> enrollmentMessages = buildEnrollmentMessage();
        log.info("Sending " + enrollmentMessages.size() + " enrollment message");
        for (EnrollmentJmsMessage msg : enrollmentMessages) {
            drJmsMessageService.enrollmentNotification(msg);
            callback.enrollmentMessageSent();
        }
        // Call finish if all enrollment call is done.
        callback.finish();
    }

    // TODO : This needs to be deleted after making dao changes.
    private List<EnrollmentJmsMessage> buildEnrollmentMessage() {
        List<EnrollmentJmsMessage> list = new ArrayList<>();
        EnrollmentJmsMessage m1 = new EnrollmentJmsMessage();
        m1.setAccountId(1);
        list.add(m1);
        EnrollmentJmsMessage m2 = new EnrollmentJmsMessage();
        m2.setAccountId(2);
        list.add(m2);
        EnrollmentJmsMessage m3 = new EnrollmentJmsMessage();
        m3.setAccountId(3);
        list.add(m3);
        return list;
    }
}
