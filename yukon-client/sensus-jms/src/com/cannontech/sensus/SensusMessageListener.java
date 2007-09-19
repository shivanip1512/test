package com.cannontech.sensus;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.cannontech.clientutils.YukonLogManager;

public class SensusMessageListener implements MessageListener, InitializingBean {
    private Logger log = YukonLogManager.getLogger(SensusMessageListener.class);
    Set<SensusMessageHandler> sensusMessageHandlerSet;
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
                    String repIdObj = om.getStringProperty("RepId");
                    if (!NumberUtils.isDigits(repIdObj)) {
                        log.warn("Got message with non-numeric RepId: " + repIdObj);
                        return;
                    }
                    String appCodeObj = om.getStringProperty("AppCode");
                    if (!NumberUtils.isDigits(appCodeObj)) {
                        log.warn("Got message with non-numeric AppCode: " + appCodeObj);
                    }
                    int repId = Integer.parseInt(repIdObj);
                    int appCode = Integer.parseInt(appCodeObj);
                    
                    for (SensusMessageHandler handler : sensusMessageHandlerSet) {
                    	handler.processMessage(repId, appCode, bytes);                    	
                    }
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

    public void setSensusMessageHandlerSet(Set<SensusMessageHandler> sensusHandlers) {
        this.sensusMessageHandlerSet = sensusHandlers;
    }

    public void afterPropertiesSet() throws Exception {
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
        	public void run() {
                log.info("Message count = " + messageCount);
            }
        }, 5000, 300000);
        
    }
}
