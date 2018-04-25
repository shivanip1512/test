package com.cannontech.multispeak.service;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.monitors.message.OutageJmsMessage;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;

public abstract class OutageJmsMessageService implements MessageListener {
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    private static final Logger log = YukonLogManager.getLogger(OutageJmsMessageService.class);

    abstract public void handleMessage(OutageJmsMessage outageJmsMessage);

    public boolean isMeter(YukonPao paoIdentifier) {
        if (paoDefinitionDao.isTagSupported(paoIdentifier.getPaoIdentifier().getPaoType(),
            PaoTag.USES_METER_NUMBER_FOR_MSP)) {
            return true;
        }
        return false;
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                Serializable object = objMessage.getObject();
                if (object instanceof OutageJmsMessage) {
                    OutageJmsMessage outageJmsMessage = (OutageJmsMessage) object;
                    handleMessage(outageJmsMessage);
                }
            } catch (JMSException e) {
                log.warn("Unable to extract OutageJmsMessage from message", e);
            }
        }
    }
}
