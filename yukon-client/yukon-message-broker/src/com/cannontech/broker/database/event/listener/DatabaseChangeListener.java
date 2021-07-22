package com.cannontech.broker.database.event.listener;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.broker.logging.YukonBrokerLoggingReloader;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class DatabaseChangeListener implements MessageListener {
    private final static Logger log = YukonLogManager.getLogger(DatabaseChangeListener.class);
    @Autowired private YukonBrokerLoggingReloader reloader;
    @Autowired private GlobalSettingDao globalSettingDao;

    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                Serializable object = objMessage.getObject();
                if (object instanceof DatabaseChangeEvent) {
                    DatabaseChangeEvent event = (DatabaseChangeEvent) object;
                    if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.LOG_RETENTION_DAYS)) {
                        globalSettingDao.valueChanged();
                        reloader.reloadAppenderForLogRetentionDays();
                    } else if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.MAX_LOG_FILE_SIZE)) {
                        globalSettingDao.valueChanged();
                        reloader.reloadAppenderForMaxFileSize(true);
                    } else if (DbChangeCategory.isDbChangeForLogger(event)) {
                        reloader.reloadYukonLoggers(event.getChangeType(), event.getPrimaryKey());
                    }
                }
            } catch (JMSException e) {
                log.error("Error while parsing message", e);
            }
        }
    }
}