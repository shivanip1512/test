package com.cannontech.broker.logging;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.broker.message.request.EventType;
import com.cannontech.broker.message.request.LoggingDbChangeRequest;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.system.dao.GlobalSettingDao;

public class LoggingDbChangeListener implements MessageListener {
    private final static Logger log = YukonLogManager.getLogger(LoggingDbChangeListener.class);
    @Autowired YukonBrokerLoggingReloader reloader;
    @Autowired GlobalSettingDao globalSettingDao;

    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                Serializable object = objMessage.getObject();
                if (object instanceof LoggingDbChangeRequest) {
                    globalSettingDao.valueChanged();
                    LoggingDbChangeRequest request = (LoggingDbChangeRequest) object;
                    if (request.getType() == EventType.LOG_RETENTION_DAYS) {
                        reloader.reloadAppenderForLogRetentionDays();
                    } else if (request.getType() == EventType.MAX_LOG_FILE_SIZE) {
                        reloader.reloadAppenderForMaxFileSize(true);
                    }
                }
            } catch (JMSException e) {
                log.error("Error while parsing message", e);
            }
        }
    }
}
