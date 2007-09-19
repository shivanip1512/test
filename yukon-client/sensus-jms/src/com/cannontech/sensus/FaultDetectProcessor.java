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

public class FaultDetectProcessor extends SensusMessageHandlerBase {
    private Logger log = YukonLogManager.getLogger(FaultDetectProcessor.class);
    private MessageEncoder messageEncoder;
    private YukonDeviceLookup yukonDeviceLookup;
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
    
    private class tgbStrength {
        int tgbId = 0;
        int sigDelta = 0;
    }

    private  Map<Integer, tgbStrength> tgbMap = new HashMap<Integer, tgbStrength>();
    
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

    protected void processStatusMessage(AppMessageType22 message) {
        int repId = message.getRepId();
        log.debug("Processing message for repId=" + repId + ": " + message);
        
        LiteYukonPAObject device = yukonDeviceLookup.getDeviceForRepId(repId);
        Date toi = message.getTimestampOfIntercept();
        
        String csvStr = processDataMessage(message);
        
        if ((message.isStatusEventTransBit() || isIgnoreEventBit()) && message.getLastEvent().isPopulated()) {
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
    }

    protected void processBindingMessage(AppMessageType5 message) {
        String iconSerialNumber = message.getIconSerialNumber();
        if (!iconSerialNumber.matches(getBindingKeyRegEx())) {
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
    }

    @Override
	protected void processSetupMessage(AppMessageType1 message) {
        int repId = message.getRepId();
        LiteYukonPAObject device = getYukonDeviceLookup().getDeviceForRepId(repId);
        if (device != null) {
            log.info("Received setup message for known serial number.  Device=" + device + ", repId=" + repId);
        }

        if (message.getDeviceType() == 12 && getNotificationGroup() != 0) {
			String subject = getNameOfAppInstance() + " FCI Setup Message: Id = " + repId;
			String body = "Setup/Commission message received at Yukon server:\n\r\n\r" + 
			getFileHeaders(0x01) + "\n\r" + dataMessageToCSVString(message) + 
	        ", \"" + getTxMode(message.getTransmitOperationalMode()) + "\"" + 
	      	", \"" + getSuprRate(message.getSupervisoryTransmitMultiple()) + "\"" +
	    	", " + DataMessage.cleanHex(message.getRawMessage());

			getNotificationProxy().sendNotification(getNotificationGroup(), subject, body);
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

    public void setFaultLatchGenerator(PointValueUpdater faultLatchGenerator) {
        this.faultLatchGenerator = faultLatchGenerator;
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
}
