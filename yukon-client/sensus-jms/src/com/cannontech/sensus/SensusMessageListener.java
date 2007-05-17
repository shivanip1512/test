package com.cannontech.sensus;

import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.cannontech.clientutils.YukonLogManager;

public class SensusMessageListener implements MessageListener, InitializingBean {
    private Logger log = YukonLogManager.getLogger(SensusMessageListener.class);
    SensusMessageHandler sensusMessageHandler;
    int messageCount = 0;

    public void onMessage(Message msg) {
        try {
            if (msg instanceof ObjectMessage) {
                ObjectMessage om = (ObjectMessage) msg;
                messageCount++;
                //logOutProperties(om);
                Object m = om.getObject();
                if (m instanceof char[]) {
                    char[] bytes = (char[])m;
                    int repId = om.getIntProperty("RepId");
                    int appCode = om.getIntProperty("AppCode");
                    sensusMessageHandler.processMessage(repId, appCode, bytes);
                } else {
                    log.info("payload wasn't a char[]");
                }
            } else {
                log.info("got unknown message: " + msg);
            }
        } catch (JMSException e) {
            log.error("JMS error in listener", e);
        }
    }

    @SuppressWarnings("unused")
    private void logOutProperties(ObjectMessage om) throws JMSException {
        Enumeration propertyNames = om.getPropertyNames();
        while (propertyNames.hasMoreElements()) {
            String prop = (String) propertyNames.nextElement();
            Object objectProperty = om.getObjectProperty(prop);
            log.info("  " + prop + "=" + objectProperty);
        }
    }

    public SensusMessageHandler getSensusMessageHandler() {
        return sensusMessageHandler;
    }

    public void setSensusMessageHandler(SensusMessageHandler sensusMessageHandler) {
        this.sensusMessageHandler = sensusMessageHandler;
    }

    public void afterPropertiesSet() throws Exception {
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {

            public void run() {
                log.debug("Message count = " + messageCount);
            }
        }, 5000, 10000);
        
    }

    
    
    /* Available properties:
       Toi
       Noise
       RfSeq
       Repeat
       RepId
       ReceiverMessageType
       Class
       AppCode
       Signal
       TgbId
     */

}
