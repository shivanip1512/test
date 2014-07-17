package com.cannontech.sensus;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.INotifConnection;
import com.sms.messages.rx.AndorianMessage;
import com.sms.messages.rx.AppMessageType1;
import com.sms.messages.rx.AppMessageType22;
import com.sms.messages.rx.AppMessageType5;

public class SensusFaultDetectProcessor extends SensusMessageHandlerBase {
    private Logger log = Logger.getLogger(SensusFaultDetectProcessor.class);
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
	private INotifConnection notificationProxy;
	private Integer notificationGroup;
       
    @Override
    protected void processStatusMessage(AppMessageType22 message) {
        int repId = message.getRepId();
        
        if(yukonDeviceLookup.isDeviceConfigured(repId)) {
        
        	log.debug("Processing message for repId=" + repId + ": " + message);
            int sigStrength = message.getSigStrength();
            int sigNoise = message.getSigNoise();
            LiteYukonPAObject device = yukonDeviceLookup.getDeviceForRepId(repId);
            Date toi = message.getTimestampOfIntercept();
            Date tooOldDate = new Date();
            tooOldDate.setTime( tooOldDate.getTime() - (86400 * 1000) );

            if(toi.compareTo(tooOldDate) < 0) {
            	// toi is too old.
                log.info("Got an event message for " + repId + " which is in the past: " + toi);
                return;
            }
            
            log.info("Device=" + device + ", repId=" + repId + ": S/N=" + sigNoise + " Sig=" + sigStrength);        

            if ((message.isStatusEventTransBit() || isIgnoreEventBit()) && message.getLastEvent().isPopulated()) {
                boolean fault = message.getLastEvent().isFaultDetected();
                long millis = toi.getTime() - message.getLastEvent().getSecondsSinceEvent() * 1000;
                Date eventDate = new Date(millis);
                faultGenerator.writePointDataMessage(device, fault, eventDate);
            } else {
                log.info("Got supervisory message or event message without lastEvent populated");

                // CGP: This is the best info we have on the current fault status.
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
    }

    @Override
	protected void processBindingMessage(AppMessageType5 message) {
        String iconSerialNumber = message.getCustomerMeterNumber(); 
        if (!iconSerialNumber.matches(getBindingKeyRegEx())) {
            log.debug("Ignoring binding message with iconSerialNumber='" + iconSerialNumber + "'");
            // return;
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
        LiteYukonPAObject device = yukonDeviceLookup.getDeviceForRepId(repId);
        if (device != null) {
            log.info("Received setup message for known serial number.  Device=" + device + ", repId=" + repId);
        }

        if (message.getDeviceType() == 12 && getNotificationGroup() != 0) {
			String subject = getNameOfAppInstance() + " FCI Setup Message: Id = " + repId;
			String body = "Setup/Commission message received at Yukon server:\n\r\n\r" + 
			getFileHeaders(0x01) + "\n\r" + dataMessageToCSVString(message) + 
	        ", \"" + getTxMode(message.getTransmitOperationalMode()) + "\"" + 
	      	", \"" + getSuprRate(message.getSupervisoryTransmitMultiple()) + "\"" +
	    	", " + AndorianMessage.cleanHex(message.getRawMessage());

			getNotificationProxy().sendNotification(getNotificationGroup(), subject, body);
		}
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
