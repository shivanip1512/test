package com.cannontech.sensus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.amdswireless.common.MessageEncoder;
import com.amdswireless.messages.rx.AppMessageType1;
import com.amdswireless.messages.rx.AppMessageType22;
import com.amdswireless.messages.rx.AppMessageType5;
import com.amdswireless.messages.rx.DataMessage;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.INotifConnection;

public class FaultDetectProcessor implements SensusMessageHandler {
    private Logger log = YukonLogManager.getLogger(FaultDetectProcessor.class);
    private MessageEncoder messageEncoder;
    private YukonDeviceLookup yukonDeviceLookup;
    private String bindingKeyRegEx = "";
    private String nameOfAppInstance = "SensusMessage";
    private String handlerTimeZone = "UTC";  
    private boolean ignoreEventBit = false;
    private PointValueUpdater faultGenerator = new NullPointValueUpdater();
    private PointValueUpdater no60Generator = new NullPointValueUpdater();
    private PointValueUpdater latGenerator = new NullPointValueUpdater();
    private PointValueUpdater longGenerator = new NullPointValueUpdater();
    private PointValueUpdater batteryVoltageGenerator = new NullPointValueUpdater();
    private PointValueUpdater deviceTemperatureGenerator = new NullPointValueUpdater();
    private PointValueUpdater batteryLowGenerator = new NullPointValueUpdater();
    private PointValueUpdater faultLatchGenerator = new NullPointValueUpdater();
    private PointValueUpdater tgbIdGenerator = new NullPointValueUpdater();
    private PointValueUpdater sigStrengthGenerator = new NullPointValueUpdater();
    private INotifConnection notificationProxy;
    private Integer notificationGroup;
    
    private class tgbStrength {
        int tgbId = 0;
        int sigDelta = 0;
    }

    private  Map<Integer, tgbStrength> tgbMap = new HashMap<Integer, tgbStrength>();
   

    private  Map<Integer, String> txMode = new HashMap<Integer, String>();
    {      
    	txMode.put(0,	"Normal Mode");
    	txMode.put(1,	"Message Pass");
    	txMode.put(2,	"Boost Mode");
    	txMode.put(3,	"Normal, ½ Baud Rate");
    	txMode.put(4,	"mPass / Normal Mix (1:1)");
    	txMode.put(5,	"mPass / Normal Mix (1:2)");
    	txMode.put(6,	"mPass / Normal Mix (1:3)");
    	txMode.put(7,	"mPass / Normal Mix (1:4)");
    	txMode.put(8,	"mPass / Normal Mix (1:5)");
    	txMode.put(9,	"mPass / Normal Mix (1:8)");
    	txMode.put(10,	"mPass / Normal Mix (1:16)");
    	txMode.put(11,	"Boost / Normal Mix (1:1)");
    	txMode.put(12,	"Boost / Normal Mix (1:2)");
    	txMode.put(13,	"Boost / Normal Mix (1:4)");
    	txMode.put(14,	"Boost / Normal Mix (1:8)");
    	txMode.put(15,  "Tri-Mode (N-N-N-M-B)");
    }
       
    private Map<Integer, String> suprRate = new HashMap<Integer, String>();
    {      
    	suprRate.put(0,   "1 minute");
    	suprRate.put(1,   "5 minutes");
    	suprRate.put(2,   "15 minutes");
    	suprRate.put(3,   "30 minutes");
    	suprRate.put(4,   "45 minutes");
    	suprRate.put(5,   "1 hour");
    	suprRate.put(6,   "1.5 hours");
    	suprRate.put(7,   "2 hours");
    	suprRate.put(8,   "2.5 hours");
    	suprRate.put(9,   "3 hours");
    	suprRate.put(10,  "3.5 hours");
    	suprRate.put(11,  "4 hours");
    	suprRate.put(12,  "4.5 hours");
    	suprRate.put(13,  "5 hours");
    	suprRate.put(14,  "5.5 hours");
    	suprRate.put(15,  "6 hours");
    	suprRate.put(16,  "7 hours");
    	suprRate.put(17,  "8 hours");
    	suprRate.put(18,  "9 hours");
    	suprRate.put(19,  "10 hours");
    	suprRate.put(20,  "12 hours");
    	suprRate.put(21,  "18 hours");
    	suprRate.put(22,  "24 hours (1 day)");
    	suprRate.put(23,  "36 hours (1.5 days)");
    	suprRate.put(24,  "48 hours (2 days)");
    	suprRate.put(25,  "60 hours (2.5 days)");
    	suprRate.put(26,  "72 hours (3 days)");
    	suprRate.put(27,  "86 hours (3.5 days)");
    	suprRate.put(28,  "Factory Sleep");
    	suprRate.put(29,  "CW Mode");
    	suprRate.put(30,  "FCC Mode");
    	suprRate.put(31,  "Fast Mode");
    }
    
    // It might be nice if this were dynamically created, but I'm feeling lazy right now.
    private Map<Integer, String> fileHeaders = new HashMap<Integer, String>();
    {      
    	fileHeaders.put(0x01,   "local toi, repId, tgbId, appCode, repeatLevel, SS, NS, appSeq, freq. slot, " + 
    			"txMode, suprRate, " +
    			"raw msg");
    	fileHeaders.put(0x05,   "local toi, repId, tgbId, appCode, repeatLevel, SS, NS, appSeq, freq. slot, " +
    			"serial no, lat, long, " +
    			"raw msg");
    	fileHeaders.put(0x22,   "local toi, repId, tgbId, appCode, repeatLevel, SS, NS, appSeq, freq. slot, " +
    			"no60 Hz, Latched Fault, Event, Cur. Temp, Cur Batt. Volts, Last Tx Temp, Last Tx Volts, " +
    			"populated, restore, no 60 Hz, fault Detected, seconds Since Event, " +
    			"populated, restore, no 60 Hz, fault Detected, seconds Since Event, " +
    			"populated, restore, no 60 Hz, fault Detected, seconds Since Event, " +
    			"populated, restore, no 60 Hz, fault Detected, seconds Since Event, " +
    			"raw msg");
    }
    
    public void processMessage(int repId, int appCode, char[] message) {
        if (appCode == 0x22) {
            if (yukonDeviceLookup.isDeviceConfigured(repId)) {
                log.debug("Received a status message for a known repid.");
                processMessage(message);
            } else {
                log.debug("Received a status message for an unknown repid: " + repId);
            }
        } else if (appCode == 0x05) {
            // we're interested in all of these
            log.debug("Received a binding message for repid: " + repId);
            processMessage(message);
        } else if (appCode == 0x01) {  // Setup message.  Indicates the configured setup.
	        // we're interested in all of these
	        log.debug("Received a configuration/setup message for repid: " + repId);
	        processMessage(message);
	    }
    }

    private void processMessage(char[] bytes) {
        DataMessage message = messageEncoder.encodeMessage(bytes);
        if (message instanceof AppMessageType5) {
            processBindingMessage((AppMessageType5)message);
        } else if (message instanceof AppMessageType22) {
            processStatusMessage((AppMessageType22)message);
        } else if (message instanceof AppMessageType1) {
            processSetupMessage((AppMessageType1)message);
        }
    }

    private String processDataMessage(DataMessage message) {

    	int repId = message.getRepId();
        int tgbId = message.getTgbId();
        int appCode = message.getAppCode();
        int repeatLevel = message.getRepeatLevel();
        int sigStrength = message.getSigStrength();
        int sigNoise = message.getSigNoise();
        int appSeq = message.getAppSeq();
        
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
        df.setTimeZone(TimeZone.getTimeZone(getHandlerTimeZone()));
        
        String csvStr = new String("\"" + df.format(message.getTimestampOfIntercept()) + "\", " 
        		+ repId + ", " 
        		+ tgbId + ", " 
        		+ appCode + ", " 
        		+ repeatLevel + ", " 
        		+ sigStrength + ", " 
        		+ sigNoise + ", "
        		+ appSeq + ", "
        		+ message.getFreqSlot()
        		);  
        return csvStr;
    }

    private void processStatusMessage(AppMessageType22 message) {
        int repId = message.getRepId();
        log.debug("Processing message for repId=" + repId + ": " + message);
        
        LiteYukonPAObject device = yukonDeviceLookup.getDeviceForRepId(repId);
        Date toi = message.getTimestampOfIntercept();
        
        String csvStr = processDataMessage(message);
        
        if ((message.isStatusEventTransBit() || ignoreEventBit) && message.getLastEvent().isPopulated()) {
            boolean fault = message.getLastEvent().isFaultDetected();
            long millis = toi.getTime() - message.getLastEvent().getSecondsSinceEvent() * 1000;
            Date eventDate = new Date(millis);
            faultGenerator.writePointDataMessage(device, fault, eventDate);
        } else {
            log.info("Got supervisory message or event message without lastEvent populated");

            // CGP: This is the best info we have on the curent fault status.
            boolean latchedFault = message.isStatusLatchedFault();
            faultGenerator.writePointDataMessage(device, latchedFault, toi);
        }

        boolean latchedFault = message.isStatusLatchedFault();
        faultLatchGenerator.writePointDataMessage(device, latchedFault, toi);

        boolean no60 = message.isStatusNo60HzOrUnderLineCurrent();
        no60Generator.writePointDataMessage(device, no60, toi);

        float lastBatteryVoltage = message.getLastTxBatteryVoltage();
        batteryVoltageGenerator.writePointDataMessage(device, lastBatteryVoltage, toi);

        int currentDeviceTemperature = message.getCurrentDeviceTemperature();
        deviceTemperatureGenerator.writePointDataMessage(device, currentDeviceTemperature, toi);

        boolean batterLow = message.isLowBattery();
        batteryLowGenerator.writePointDataMessage(device, batterLow, toi);
        
        // Attempt to record and report the best signal strength seen.  Only if in Normal Mode.
        if(message.getRepeatLevel() == 0){
            tgbStrength strength = tgbMap.get(repId);
            if(strength != null){
            	int newDelta = message.getSigStrength() - message.getSigNoise();
            	if(strength.sigDelta < newDelta) {
            		// the newDelta is larger.  Update the map.
            		strength.tgbId = message.getTgbId();
            		strength.sigDelta = newDelta;
            		// now get them to Dispatch.
            		tgbIdGenerator.writePointDataMessage(device, strength.tgbId, toi);
            		sigStrengthGenerator.writePointDataMessage(device, strength.sigDelta, toi);
            	}
            } else {
            	strength = new tgbStrength();
            	strength.tgbId = message.getTgbId();
            	strength.sigDelta = message.getSigStrength() - message.getSigNoise();
            	tgbMap.put(repId, strength);
            }
        }
                
        csvStr += ", " + message.toCSV();
        log.info(csvStr);
        logMessage("0x22", csvStr, fileHeaders.get(message.getAppCode()));
    }

    private void processBindingMessage(AppMessageType5 message) {
        String iconSerialNumber = message.getIconSerialNumber();
        if (!iconSerialNumber.matches(bindingKeyRegEx)) {
            log.debug("Ignoring binding message with iconSerialNumber='" + iconSerialNumber + "'");
            return;
        }
        int repId = message.getRepId();
        LiteYukonPAObject device = yukonDeviceLookup.getDeviceForRepId(repId);
        if (device == null) {
            log.info("Received binding message for unconfigured device: " + repId);
            return;
        }
        log.info("Received binding message for known serial number. iconSerialNumber="
                 + iconSerialNumber + ", device=" + device + ", repId=" + repId);

        // process lat/long
        float latitude = message.getLatitude();
        float longitude = message.getLongitude();

        if (latitude != 0 || longitude != 0) {
            latGenerator.writePointDataMessage(device, latitude, message.getTimestampOfIntercept());
            longGenerator.writePointDataMessage(device, longitude, message.getTimestampOfIntercept());
        }
        
        String csvStr = processDataMessage(message);
        csvStr += ", " + iconSerialNumber + ", " + latitude + ", " + longitude +
    	", " + DataMessage.cleanHex(message.getRawMessage());
        
        log.info(csvStr);
        logMessage("0x05", csvStr, fileHeaders.get(message.getAppCode()));
    }

    private void processSetupMessage(AppMessageType1 message) {
        int repId = message.getRepId();
        int deviceType = message.getDeviceType();

        int txOpMode = message.getTransmitOperationalMode();
        int supTxMult = message.getSupervisoryTransmitMultiple();

        String csvStr = processDataMessage(message) + 
        ", \"" + txMode.get(txOpMode) + "\"" + 
      	", \"" + suprRate.get(supTxMult) + "\"" +
    	", " + DataMessage.cleanHex(message.getRawMessage());
  
        LiteYukonPAObject device = yukonDeviceLookup.getDeviceForRepId(repId);
        if (device != null) {
            log.info("Received setup message for known serial number.  Device=" + device + ", repId=" + repId);
        }
        if (deviceType == 12) {
            log.info(csvStr);
            logMessage("0x01", csvStr, fileHeaders.get(message.getAppCode()));
            
            String subject = "Grid Advisor Setup Message: Id = " + repId;
            String body = "Setup/Commission message received at Yukon server:\n\r\n\r" + csvStr;                        
            notificationProxy.sendNotification(getNotificationGroup(), subject, body);
        } else {
            log.debug(csvStr);
        }
    }

    public void setMessageEncoder(MessageEncoder messageEncoder) {
        this.messageEncoder = messageEncoder;
    }

    public void setYukonDeviceLookup(YukonDeviceLookup yukonDeviceLookup) {
        this.yukonDeviceLookup = yukonDeviceLookup;
    }

    public void setFaultGenerator(PointValueUpdater faultGenerator) {
        this.faultGenerator = faultGenerator;
    }

    public void setLatGenerator(PointValueUpdater latGenerator) {
        this.latGenerator = latGenerator;
    }

    public void setLongGenerator(PointValueUpdater longGenerator) {
        this.longGenerator = longGenerator;
    }

    public void setBindingKeyRegEx(String bindingKeyRegEx) {
        this.bindingKeyRegEx = bindingKeyRegEx;
    }

    public void setBatteryVoltageGenerator(PointValueUpdater batteryVoltageGenerator) {
        this.batteryVoltageGenerator = batteryVoltageGenerator;
    }

    public void setDeviceTemperatureGenerator(PointValueUpdater deviceTemperatureGenerator) {
        this.deviceTemperatureGenerator = deviceTemperatureGenerator;
    }

    public void setNo60Generator(PointValueUpdater no60Generator) {
        this.no60Generator = no60Generator;
    }

    public void setBatteryLowGenerator(PointValueUpdater batteryLowGenerator) {
        this.batteryLowGenerator = batteryLowGenerator;
    }

    public void setIgnoreEventBit(boolean ignoreEventBit) {
        this.ignoreEventBit = ignoreEventBit;
    }

    public void setFaultLatchGenerator(PointValueUpdater faultLatchGenerator) {
        this.faultLatchGenerator = faultLatchGenerator;
    }

	public String getNameOfAppInstance() {
		return nameOfAppInstance;
	}

	public void setNameOfAppInstance(String nameOfAppInstance) {
		this.nameOfAppInstance = nameOfAppInstance;
	}

	private void logMessage(String file, String text, String header) {
        //String fileName = CtiUtilities.getYukonBase() + "/Server/Log/" + nameOfAppInstance + "01.csv";
        String fileName = CtiUtilities.getYukonBase() + "/Server/Log/" + getNameOfAppInstance() + file + ".log";
        File tempFile = new File(fileName);
        
        if(header != null && !tempFile.exists()) {
            try {
                FileWriter fw = new FileWriter(tempFile, true);
                fw.write(header);
                fw.write("\r\n");
                fw.close();
            } catch (IOException e) {
            	log.info("Unable to write.",e);
            }        
        }
        
        try {
            FileWriter fw = new FileWriter(tempFile, true);
            fw.write(text);
            fw.write("\r\n");
            fw.close();
        } catch (IOException e) {
        	log.info("Unable to write.",e);
        }        
	}

	public String getHandlerTimeZone() {
		return handlerTimeZone;
	}

	public void setHandlerTimeZone(String handlerTimezone) {
		this.handlerTimeZone = handlerTimezone;
	}

	public PointValueUpdater getSigStrengthGenerator() {
		return sigStrengthGenerator;
	}

	public void setSigStrengthGenerator(PointValueUpdater sigStrengthGenerator) {
		this.sigStrengthGenerator = sigStrengthGenerator;
	}

	public PointValueUpdater getTgbIdGenerator() {
		return tgbIdGenerator;
	}

	public void setTgbIdGenerator(PointValueUpdater tgbIdGenerator) {
		this.tgbIdGenerator = tgbIdGenerator;
	}

	public INotifConnection getNotificationProxy() {
		return notificationProxy;
	}

	public void setNotificationProxy(INotifConnection notificationProxy) {
		this.notificationProxy = notificationProxy;
	}

	public Integer getNotificationGroup() {
		return notificationGroup;
	}

	public void setNotificationGroup(Integer notificationGroup) {
		this.notificationGroup = notificationGroup;
	}
	

}
