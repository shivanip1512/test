package com.cannontech.sensus;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.sms.common.MessageEncoder;
import com.sms.common.UnknownAppCodeException;
import com.sms.messages.rx.AndorianMessage;
import com.sms.messages.rx.DataMessage;



public class SensusMessageListener implements MessageListener, InitializingBean {
    private Logger log = Logger.getLogger(SensusMessageListener.class);
    Set<SensusMessageObjectHandler> sensusMessageObjectHandlerSet;
    int messageCount = 0;
	private Integer minRepId = 30000000;
	private Integer maxRepId = 31000000;
    private MessageEncoder messageEncoder;

	private Map<Integer, Integer>  repIdSeqMap = new HashMap<Integer, Integer>();

    public void onMessage(Message msg) {
        try {
            if (msg instanceof ObjectMessage) {
                ObjectMessage om = (ObjectMessage) msg;
                messageCount++;
                //logOutProperties(om);
                Object m = null;
                m = om.getObject();

                if(m instanceof char[]) {
                    char[] bytes = (char[])m;
                    try {
                        AndorianMessage andorianMessage = getMessageEncoder().encodeMessage(bytes);
                        if (andorianMessage instanceof DataMessage) {
                            m = andorianMessage;
                        } else {
                            log.debug("Received non-DataMessage: " + andorianMessage.getClass());
                        }
                    } catch (UnknownAppCodeException e) {
                        log.debug("Unable to encode message", e);
                    }                    
                } // Now process the encoded bytes.
                
                if (m instanceof DataMessage) {
                    DataMessage dataMessage = (DataMessage) m;
                    String repIdObj = om.getStringProperty("RepId");
                    String appCodeObj = om.getStringProperty("AppCode");
                    String sequenceObj = om.getStringProperty("RfSeq");

                    if (!NumberUtils.isDigits(repIdObj) || !NumberUtils.isDigits(appCodeObj) || !NumberUtils.isDigits(sequenceObj) ) {
                    	// if(messageCount < 1000) logOutProperties(om);
                        return;
                    }

                    int repId = Integer.parseInt(repIdObj);
                	int appCode = Integer.parseInt(appCodeObj);

                	if(isMessageInteresting(appCode) && getMinRepId() <= repId && repId <= getMaxRepId()) {		// Only process this range of radio Id.			

                        //logOutProperties(om);

                        if (appCode == 0x22) {
                			log.debug("Received a status message for repid: " + repId);
                		} else if (appCode == 0x05) {
                			log.debug("Received a binding message for repid: " + repId);
                		} else if (appCode == 0x01) { 
                			log.debug("Received a configuration/setup message for repid: " + repId);
                		} else {
                		    log.debug("Received a " + appCode + " message for repid: " + repId);
                		}
                		
                		for (SensusMessageObjectHandler handler : sensusMessageObjectHandlerSet) {
                			handler.processMessageObject(dataMessage);                    	
                		}                    	
                	}
                } else {
                    log.debug("payload wasn't a char[]" + m.toString());
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
            log.info("  " + prop + " = " + objectProperty);
        }
    }

    public void setSensusMessageObjectHandlerSet(Set<SensusMessageObjectHandler> sensusHandlers) {
        this.sensusMessageObjectHandlerSet = sensusHandlers;
    }

    public void afterPropertiesSet() throws Exception {
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
        	@Override
            public void run() {
                log.info("Message count = " + messageCount);
            }
        }, 5000, 300000);
        
    }

    public static boolean isMessageInteresting(int appCode) {
    	boolean interested = false;
    	
    	switch (appCode) {
    	case 0x01:
    	case 0x05:
    	case 0x22:
    		interested = true;
    		break;
    	default:
    	}

    	return interested;
    }

    public boolean isMessageSequenceNumberNew(int repId, int sequence) {
    	boolean newSequence = false;		// Determine if this sequence has already been through the system for this repId?
    	Integer lastSeq = repIdSeqMap.get(repId);

    	if(lastSeq == null) {
    		// Unknown repId.  Add it to the list with this sequence
    		newSequence = true;
    		repIdSeqMap.put(repId, sequence);
    	} else if(lastSeq.intValue() < sequence) {
    		// it is a new sequence number. Update the repId to this sequence.
    		newSequence = true;
    		repIdSeqMap.put(repId, sequence);
    	} else {
    		// We've already processed this sequence once.
    		log.debug("Sequence number " + sequence + " already processed for repId " + repId);
    	}
    	
    	return newSequence;
    }

	public Integer getMaxRepId() {
		return maxRepId;
	}

	public void setMaxRepId(Integer maxRepId) {
		this.maxRepId = maxRepId;
	}

	public Integer getMinRepId() {
		return minRepId;
	}

	public void setMinRepId(Integer minRepId) {
		this.minRepId = minRepId;
	}

    public void setMessageEncoder(MessageEncoder messageEncoder) {
        this.messageEncoder = messageEncoder;
    }

    public MessageEncoder getMessageEncoder() {
        return messageEncoder;
    }

}


