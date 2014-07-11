package com.cannontech.sensus;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.sms.common.MessageEncoder;
import com.sms.common.UnknownAppCodeException;
import com.sms.messages.rx.AndorianMessage;
import com.sms.messages.rx.AppMessageType1;
import com.sms.messages.rx.AppMessageType22;
import com.sms.messages.rx.AppMessageType5;
import com.sms.messages.rx.DataMessage;

public abstract class SensusMessageHandlerBase implements SensusMessageHandler, SensusMessageObjectHandler {

	private Logger log = Logger.getLogger(SensusMessageHandlerBase.class);

	private MessageEncoder messageEncoder;

	private String bindingKeyRegEx = "";

	private String nameOfAppInstance = "SensusMessage";
	
	private String logFilePath = "C:/Yukon/Server/Log/";

	private String handlerTimeZone = "UTC";

	private boolean ignoreEventBit = false;

	private short customerId;
	
	private static Map<Integer, String> txMode = new HashMap<Integer, String>();
	static {
		txMode.put(0, "Normal Mode");
		txMode.put(1, "Message Pass");
		txMode.put(2, "Boost Mode");
		txMode.put(3, "Normal, ½ Baud Rate");
		txMode.put(4, "mPass / Normal Mix (1:1)");
		txMode.put(5, "mPass / Normal Mix (1:2)");
		txMode.put(6, "mPass / Normal Mix (1:3)");
		txMode.put(7, "mPass / Normal Mix (1:4)");
		txMode.put(8, "mPass / Normal Mix (1:5)");
		txMode.put(9, "mPass / Normal Mix (1:8)");
		txMode.put(10, "mPass / Normal Mix (1:16)");
		txMode.put(11, "Boost / Normal Mix (1:1)");
		txMode.put(12, "Boost / Normal Mix (1:2)");
		txMode.put(13, "Boost / Normal Mix (1:4)");
		txMode.put(14, "Boost / Normal Mix (1:8)");
		txMode.put(15, "Tri-Mode (N-N-N-M-B)");
	}

	private static Map<Integer, String> suprRate = new HashMap<Integer, String>();
	static {
		suprRate.put(0, "1 minute");
		suprRate.put(1, "5 minutes");
		suprRate.put(2, "15 minutes");
		suprRate.put(3, "30 minutes");
		suprRate.put(4, "45 minutes");
		suprRate.put(5, "1 hour");
		suprRate.put(6, "1.5 hours");
		suprRate.put(7, "2 hours");
		suprRate.put(8, "2.5 hours");
		suprRate.put(9, "3 hours");
		suprRate.put(10, "3.5 hours");
		suprRate.put(11, "4 hours");
		suprRate.put(12, "4.5 hours");
		suprRate.put(13, "5 hours");
		suprRate.put(14, "5.5 hours");
		suprRate.put(15, "6 hours");
		suprRate.put(16, "7 hours");
		suprRate.put(17, "8 hours");
		suprRate.put(18, "9 hours");
		suprRate.put(19, "10 hours");
		suprRate.put(20, "12 hours");
		suprRate.put(21, "18 hours");
		suprRate.put(22, "24 hours (1 day)");
		suprRate.put(23, "36 hours (1.5 days)");
		suprRate.put(24, "48 hours (2 days)");
		suprRate.put(25, "60 hours (2.5 days)");
		suprRate.put(26, "72 hours (3 days)");
		suprRate.put(27, "86 hours (3.5 days)");
		suprRate.put(28, "Factory Sleep");
		suprRate.put(29, "CW Mode");
		suprRate.put(30, "FCC Mode");
		suprRate.put(31, "Fast Mode");
	}

	// It might be nice if this were dynamically created, but I'm feeling lazy
	// right now.
	private static Map<Integer, String> fileHeaders = new HashMap<Integer, String>();
	static {
		fileHeaders.put(0x01,
				"local toi, repId, tgbId, appCode, repeatLevel, SS, NS, appSeq, freq. slot, "
						+ "txMode, suprRate, " + "raw msg");
		fileHeaders.put(0x05,
				"local toi, repId, tgbId, appCode, repeatLevel, SS, NS, appSeq, freq. slot, "
						+ "serial no, lat, long, " + "raw msg");
		fileHeaders
				.put(
						0x22,
						"local toi, repId, tgbId, appCode, repeatLevel, SS, NS, appSeq, freq. slot, "
								+ "no60 Hz, Latched Fault, Event, Cur. Temp, Cur Batt. Volts, Last Tx Temp, Last Tx Volts, "
								+ "populated, restore, no 60 Hz, fault Detected, seconds Since Event, "
								+ "populated, restore, no 60 Hz, fault Detected, seconds Since Event, "
								+ "populated, restore, no 60 Hz, fault Detected, seconds Since Event, "
								+ "populated, restore, no 60 Hz, fault Detected, seconds Since Event, "
								+ "raw msg");
	}
	
	protected abstract void processSetupMessage(AppMessageType1 message);

	protected abstract void processBindingMessage(AppMessageType5 message);

	protected abstract void processStatusMessage(AppMessageType22 message);
	
	protected boolean newSequence;  // This variable is set if the sequence in process has NOT been processed before. 

	@Override
    public void processMessage(int repId, int appCode, boolean isSequenceNew, char[] message) {
		newSequence = isSequenceNew;  // Store it at this level, so other methods can make decisions on it. 
		
		if (appCode == 0x22) {
			processMessage(message);
		} else if (appCode == 0x05) {
			processMessage(message);
		} else if (appCode == 0x01) { // Setup message. Indicates the
			processMessage(message);
		}
	}

	private void processMessage(char[] bytes) {
		try {
            AndorianMessage andorianMessage = getMessageEncoder().encodeMessage(bytes);
            if (andorianMessage instanceof DataMessage) {
                DataMessage dataMessage = (DataMessage) andorianMessage;
                processMessageObject(dataMessage);
            } else {
                log.debug("Received non-DataMessage: " + andorianMessage.getClass());
            }
        } catch (UnknownAppCodeException e) {
            log.debug("Unable to encode message", e);
        }
	}

    @Override
    public void processMessageObject(DataMessage message) {
        if (message instanceof AppMessageType5) {
			processBindingMessage((AppMessageType5) message);
		} else if (message instanceof AppMessageType22) {
			processStatusMessage((AppMessageType22) message);
		} else if (message instanceof AppMessageType1) {
			processSetupMessage((AppMessageType1) message);
		}
    }

	public String getBindingKeyRegEx() {
		return bindingKeyRegEx;
	}

	public void setBindingKeyRegEx(String bindingKeyRegEx) {
		this.bindingKeyRegEx = bindingKeyRegEx;
	}

	public String getHandlerTimeZone() {
		return handlerTimeZone;
	}

	public void setHandlerTimeZone(String handlerTimeZone) {
		this.handlerTimeZone = handlerTimeZone;
	}

	public boolean isIgnoreEventBit() {
		return ignoreEventBit;
	}

	public void setIgnoreEventBit(boolean ignoreEventBit) {
		this.ignoreEventBit = ignoreEventBit;
	}

	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}

	public MessageEncoder getMessageEncoder() {
		return messageEncoder;
	}

	public void setMessageEncoder(MessageEncoder messageEncoder) {
		this.messageEncoder = messageEncoder;
	}

	public String getNameOfAppInstance() {
		return nameOfAppInstance;
	}

	public void setNameOfAppInstance(String nameOfAppInstance) {
		this.nameOfAppInstance = nameOfAppInstance;
	}

	public static String getFileHeaders(Integer key) {
		return fileHeaders.get(key);
	}

	public static String getSuprRate(Integer key) {
		return suprRate.get(key);
	}

	public static String getTxMode(Integer key) {
		return txMode.get(key);
	}

	public short getCustomerId() {
		return customerId;
	}

	public void setCustomerId(short customerId) {
		this.customerId = customerId;
	}

	protected String dataMessageToCSVString(DataMessage message) {

		int repId = message.getRepId();
		int tgbId = message.getTgbId();
		int appCode = message.getAppCode();
		int repeatLevel = message.getRepeatLevel();
		int sigStrength = message.getSigStrength();
		int sigNoise = message.getSigNoise();
		int appSeq = message.getAppSeq();

		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT,
				DateFormat.MEDIUM);
		df.setTimeZone(TimeZone.getTimeZone(getHandlerTimeZone()));

		String csvStr = new String("\""
				+ df.format(message.getTimestampOfIntercept()) + "\", " + repId
				+ ", " + tgbId + ", " + appCode + ", " + repeatLevel + ", "
				+ sigStrength + ", " + sigNoise + ", " + appSeq + ", "
				+ message.getFreqSlot());
		return csvStr;
	}

	protected boolean isNewSequence() {
		return newSequence;
	}

	protected void setNewSequence(boolean newSequence) {
		this.newSequence = newSequence;
	}

	public String getLogFilePath() {
		return logFilePath;
	}

	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}
}
