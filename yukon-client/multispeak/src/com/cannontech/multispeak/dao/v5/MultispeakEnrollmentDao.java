package com.cannontech.multispeak.dao.v5;

import java.util.List;

import com.cannontech.stars.dr.jms.message.EnrollmentJmsMessage;

public interface MultispeakEnrollmentDao {
    public List<EnrollmentJmsMessage> getEnrollmentMessagesToSend();
}
