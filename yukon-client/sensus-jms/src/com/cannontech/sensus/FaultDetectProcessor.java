package com.cannontech.sensus;

import java.util.Date;

import org.apache.log4j.Logger;

import com.amdswireless.common.MessageEncoder;
import com.amdswireless.messages.rx.AppMessageType22;
import com.amdswireless.messages.rx.AppMessageType5;
import com.amdswireless.messages.rx.DataMessage;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public class FaultDetectProcessor implements SensusMessageHandler {
    private Logger log = YukonLogManager.getLogger(FaultDetectProcessor.class);
    private MessageEncoder messageEncoder;
    private YukonDeviceLookup yukonDeviceLookup;
    private String bindingKeyRegEx = "";
    private boolean ignoreEventBit = false;
    private PointValueUpdater faultGenerator = new NullPointValueUpdater();
    private PointValueUpdater no60Generator = new NullPointValueUpdater();
    private PointValueUpdater latGenerator = new NullPointValueUpdater();
    private PointValueUpdater longGenerator = new NullPointValueUpdater();
    private PointValueUpdater batteryVoltageGenerator = new NullPointValueUpdater();
    private PointValueUpdater deviceTemperatureGenerator = new NullPointValueUpdater();
    private PointValueUpdater batteryLowGenerator = new NullPointValueUpdater();
    private PointValueUpdater faultLatchGenerator = new NullPointValueUpdater();

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
        }
    }

    private void processMessage(char[] bytes) {
        DataMessage message = messageEncoder.encodeMessage(bytes);
        if (message instanceof AppMessageType5) {
            processBindingMessage((AppMessageType5)message);
        } else if (message instanceof AppMessageType22) {
            processStatusMessage((AppMessageType22)message);
        }
    }

    private void processStatusMessage(AppMessageType22 message) {
        int repId = message.getRepId();
        log.debug("Processing message for repId=" + repId + ": " + message);

        Date toi = message.getTimestampOfIntercept();
        if ((message.isStatusEventTransBit() || ignoreEventBit) && message.getLastEvent().isPopulated()) {
            boolean fault = message.getLastEvent().isFaultDetected();
            long millis = toi.getTime() - message.getLastEvent().getSecondsSinceEvent() * 1000;
            Date eventDate = new Date(millis);
            faultGenerator.writePointDataMessage(repId, fault, eventDate);
        } else {
            log.info("Got supervisory message or event message without lastEvent populated");

            // CGP: This is the best info we have on the curent fault status.
            boolean latchedFault = message.isStatusLatchedFault();
            faultGenerator.writePointDataMessage(repId, latchedFault, toi);
        }

        boolean latchedFault = message.isStatusLatchedFault();
        faultLatchGenerator.writePointDataMessage(repId, latchedFault, toi);

        boolean no60 = message.isStatusNo60HzOrUnderLineCurrent();
        no60Generator.writePointDataMessage(repId, no60, toi);

        float lastBatteryVoltage = message.getLastTxBatteryVoltage();
        batteryVoltageGenerator.writePointDataMessage(repId, lastBatteryVoltage, toi);

        int currentDeviceTemperature = message.getCurrentDeviceTemperature();
        deviceTemperatureGenerator.writePointDataMessage(repId, currentDeviceTemperature, toi);

        boolean batterLow = message.isLowBattery();
        batteryLowGenerator.writePointDataMessage(repId, batterLow, toi);
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
            latGenerator.writePointDataMessage(repId, latitude, message.getTimestampOfIntercept());
            longGenerator.writePointDataMessage(repId, longitude, message.getTimestampOfIntercept());
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


}
