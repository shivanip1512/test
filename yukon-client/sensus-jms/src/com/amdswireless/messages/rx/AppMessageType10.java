package com.amdswireless.messages.rx;

//import java.util.BitSet;

/*
   App Data Bytes Data
   0             Voltage Phase A
   1             Voltage Phase B
   2             Voltage Phase C
   3             Click Count
   4             Time Since Event 
   5-6           Extended Time Since Event
   7-9           Current Reading
   10            Device Temperature
   11            uP Errors
   12            Lock Errors
   13-14         Meter Alarm Flags
   15            # Manual Demand Resets
   16-19         Time of Last Power Fail
   20-23         Time of Last Power Restore
   24            Total # of Outages
   */
public class AppMessageType10 extends AlarmMessage implements AppMessage, java.io.Serializable {

    private transient static final long serialVersionUID = 2L;
    private final String msgClass = "ALARM";
    private int phaseAVoltage, phaseBVoltage, phaseCVoltage;
    private int clickCount;
    private java.util.Date timestampOfEvent;
    private int relativeTimestamp;
    private int timeSinceEvent;
    private int extendedTimeSinceEvent;
    private int currentReading;
    private int deviceTemperature;
    private boolean ramTestFailed;
    private boolean romTestFailed;
    private int processorRequests;
    private int receiverCalibrationErrors;
    private int synthesizerLockFailures;
    private boolean meterUnprogrammed;
    private boolean configurationError;
    private boolean selfCheckError;
    private boolean ramFailureError;
    private boolean romFailureError;
    private boolean nonvolError;
    private boolean clockError;
    private boolean measurementError;
    private boolean lowBatteryError;
    private boolean lowLossPotential;
    private boolean demandOverload;
    private boolean powerFailure;
    private int manualDemandResetCount;
    private String lastPowerFailTime;
    private String lastPowerRestoreTime;
    private int totalOutageCount;

    public AppMessageType10( char[] msg ) {
        super(msg);
        super.setMessageType(0x10);
        int offset=31;
        this.phaseAVoltage=(int)((msg[0+offset])*2);
        this.phaseBVoltage=(int)((msg[1+offset])*2);
        this.phaseCVoltage=(int)((msg[2+offset])*2);
        this.clickCount=(int)msg[3+offset];
        this.timeSinceEvent = (int)msg[4+offset] * 6;
        this.extendedTimeSinceEvent = (int)( ((msg[5+offset]) + (msg[6+offset]<<8)) * 1536) + this.timeSinceEvent;
        this.currentReading=(int)(msg[7+offset])+(int)(msg[8+offset]<<8)+(int)(msg[9+offset]<<16);
        this.deviceTemperature=(int)msg[10+offset];
        this.ramTestFailed=( (msg[11+offset] & 0x20 ) == 0x20 )  ? true : false;
        this.romTestFailed=( (msg[11+offset] & 0x10 ) == 0x10 )  ? true : false;
        this.processorRequests=(int)((msg[11+offset] & 0xF) >>> 4 );
        this.receiverCalibrationErrors = (int)((msg[12+offset] & 0xF0 ) >>> 4 );
        this.synthesizerLockFailures = (int)(msg[12+offset] & 0xF );
        this.meterUnprogrammed = ( (msg[13+offset] & 0x1 ) == 0x1 ) ? true : false;
        this.configurationError = ( (msg[13+offset] & 0x2 ) == 0x2 ) ? true : false;
        this.selfCheckError = ( (msg[13+offset] & 0x4 ) == 0x4 ) ? true : false;
        this.ramFailureError = ( (msg[13+offset] & 0x8 ) == 0x8 ) ? true : false;
        this.romFailureError = ( (msg[13+offset] & 0x10 ) == 0x10 ) ? true : false;
        this.nonvolError = ( (msg[13+offset] & 0x20 ) == 0x20 ) ? true : false;
        this.clockError = ( (msg[13+offset] & 0x40 ) == 0x40 ) ? true : false;
        this.measurementError = ( (msg[13+offset] & 0x80 ) == 0x80 ) ? true : false;
        this.lowBatteryError = ( (msg[14+offset] & 0x1 ) == 0x1 ) ? true : false;
        this.lowLossPotential = ( (msg[14+offset] & 0x2 ) == 0x2 ) ? true : false;
        this.demandOverload = ( (msg[14+offset] & 0x4 ) == 0x4 ) ? true : false;
        this.powerFailure = ( (msg[14+offset] & 0x8 ) == 0x8 ) ? true : false;
        this.manualDemandResetCount = (int)msg[15+offset];
        this.timestampOfEvent = new java.util.Date((this.getToi()-extendedTimeSinceEvent)*1000);
        int month = (int)msg[16+offset];
        int day = (int)msg[17+offset];
        int hour = (int)msg[18+offset];
        int minute = (int)msg[19+offset];
        String monString = Integer.toString(month);
        if ( month  < 10 ) monString = "0"+Integer.toString(month);
        String dayString = Integer.toString(day);
        if ( day < 10 ) dayString = "0"+Integer.toString(day);
        String hourString = Integer.toString(hour);
        if ( hour < 10 ) hourString = "0"+Integer.toString(hour);
        String minString = Integer.toString(minute);
        if ( minute < 10 ) minString = "0"+Integer.toString(minute);
        this.lastPowerFailTime = monString+"/"+dayString+" "+hourString+":"+minString;
        month = (int)msg[20+offset];
        monString = Integer.toString(month);
        day = (int)msg[21+offset];
        dayString = Integer.toString(day);
        hour = (int)msg[22+offset];
        hourString = Integer.toString(hour);
        minute = (int)msg[23+offset];
        minString = Integer.toString(minute);
        this.lastPowerRestoreTime = monString+"/"+dayString+" "+hourString+":"+minString;
        this.totalOutageCount = (int)msg[24+offset];
    }

    public int getPhaseAVoltage() {
        return this.phaseAVoltage;
    }
    public int getPhaseBVoltage() {
        return this.phaseBVoltage;
    }
    public int getPhaseCVoltage() {
        return this.phaseCVoltage;
    }
    public int getRelativeTimestamp() {
        return this.relativeTimestamp;
    }
    public int getCurrentReading() {
        return this.currentReading;
    }

    /**
     * @return Returns the clickCount.
     */
    public int getClickCount() {
        return clickCount;
    }

    /**
     * @return Returns the clockError.
     */
    public boolean isClockError() {
        return clockError;
    }

    /**
     * @return Returns the configurationError.
     */
    public boolean isConfigurationError() {
        return configurationError;
    }

    /**
     * @return Returns the demandOverload.
     */
    public boolean isDemandOverload() {
        return demandOverload;
    }

    /**
     * @return Returns the deviceTemperature.
     */
    public int getDeviceTemperature() {
        return deviceTemperature;
    }

    /**
     * @return Returns the eventTime.
     */
    public java.util.Date getEventTime() {
        return timestampOfEvent;
    }

    /**
     * @return Returns the eventTime.
     */
    public java.util.Date getTimestampOfEvent() {
        return timestampOfEvent;
    }

    /**
     * @return Returns the extendedTimeSinceEvent.
     */
    public int getExtendedTimeSinceEvent() {
        return extendedTimeSinceEvent;
    }

    /**
     * @return Returns the lastPowerFailTime.
     */
    public String getLastPowerFailTime() {
        return lastPowerFailTime;
    }

    /**
     * @return Returns the lastPowerRestoreTime.
     */
    public String getLastPowerRestoreTime() {
        return lastPowerRestoreTime;
    }

    /**
     * @return Returns the lowBatteryError.
     */
    public boolean isLowBatteryError() {
        return lowBatteryError;
    }

    /**
     * @return Returns the lowLossPotential.
     */
    public boolean isLowLossPotential() {
        return lowLossPotential;
    }

    /**
     * @return Returns the manualDemandResetCount.
     */
    public int getManualDemandResetCount() {
        return manualDemandResetCount;
    }

    /**
     * @return Returns the measurementError.
     */
    public boolean isMeasurementError() {
        return measurementError;
    }

    /**
     * @return Returns the meterUnprogrammed.
     */
    public boolean isMeterUnprogrammed() {
        return meterUnprogrammed;
    }

    /**
     * @return Returns the nonvolError.
     */
    public boolean isNonvolError() {
        return nonvolError;
    }

    /**
     * @return Returns the powerFailure.
     */
    public boolean isPowerFailure() {
        return powerFailure;
    }

    /**
     * @return Returns the processorRequests.
     */
    public int getProcessorRequests() {
        return processorRequests;
    }

    /**
     * @return Returns the ramFailureError.
     */
    public boolean isRamFailureError() {
        return ramFailureError;
    }

    /**
     * @return Returns the ramTestFailed.
     */
    public boolean isRamTestFailed() {
        return ramTestFailed;
    }

    /**
     * @return Returns the receiverCalibrationErrors.
     */
    public int getReceiverCalibrationErrors() {
        return receiverCalibrationErrors;
    }

    /**
     * @return Returns the romFailureError.
     */
    public boolean isRomFailureError() {
        return romFailureError;
    }

    /**
     * @return Returns the romTestFailed.
     */
    public boolean isRomTestFailed() {
        return romTestFailed;
    }

    /**
     * @return Returns the selfCheckError.
     */
    public boolean isSelfCheckError() {
        return selfCheckError;
    }

    /**
     * @return Returns the synthesizerLockFailures.
     */
    public int getSynthesizerLockFailures() {
        return synthesizerLockFailures;
    }

    /**
     * @return Returns the timeSinceEvent.
     */
    public int getTimeSinceEvent() {
        return timeSinceEvent;
    }

    /**
     * @return Returns the totalOutageCount.
     */
    public int getTotalOutageCount() {
        return totalOutageCount;
    }

    public String getMsgClass() {
        return this.msgClass;
    }
}
