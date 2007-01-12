package com.amdswireless.messages.rx;

//import java.util.BitSet;


public class AppMessageType4 extends AlarmMessage implements AppMessage, java.io.Serializable {
    /*
     * Byte		Data
     * 0-1		Alarm Data
     * 	0:0	7759 Calibration Error
     * 	0:1	7759 Register Check Error
     * 	0:2	7759 Reset Error
     * 	0:3	RAM Bit Error
     * 	0:4	General CRC Error
     * 	0:5	Soft EEPROM Error
     * 	0:7	Watchdog Restart
     * 	1:0	soft kWh error, data OK
     * 	1:1	Low AC Voltage
     * 	1:2	Current too high
     * 	1:3	Power Failure
     * 	1:4	Hard EEPROM Error
     * 	1:5	Hard kWh error, data Lost
     *	1:6	Configuration Error
     *	1:7	Reverse Power
     * 2-4		Avg, Max, and Min Meter Voltage
     * 5		Actual Voltage
     * 6		Click Count
     * 7		Time Since Event
     * 8-10		Current Reading
     */

    /**
     * 
     */
    private transient static final long serialVersionUID = 1L;
    private final String msgClass = "ALARM";
    private int	actualVoltage;
    private int	avgVoltage, maxVoltage, minVoltage;
    private int	clickCount;
    private int	currentReading;
    private int	timeSinceEvent;
    private int deviceTemperature;
    private int extendedTimeSinceEvent;
    private java.util.Date timestampOfEvent;
    private boolean bitChecksumError;
    private boolean calibrationError;
    private boolean configurationError;
    private boolean currentTooHigh;
    private boolean generalCrcError;
    private boolean hardEepromError;
    private boolean hardKwhError;
    private boolean lowAcVoltage;
    private boolean powerFailure;
    private boolean ramBitError;
    private boolean registerCheckError;
    private boolean resetError;
    private boolean reversePower;
    private boolean softEepromError;
    private boolean softKwhError;
    private boolean watchdogRestart;

    public AppMessageType4( char[] msg ) {
        super(msg);
        super.setMessageType(0x4);
        int offset=31;
        this.calibrationError=( (msg[0+offset] & 0x1) == 1 );
        this.registerCheckError=( (msg[0+offset] & 0x02) == 1);
        this.resetError=( (msg[0+offset] & 0x04) == 1);
        this.ramBitError=( (msg[0+offset] & 0x08) == 1);
        this.generalCrcError=( (msg[0+offset] & 0x10) == 1);
        this.softEepromError=( (msg[0+offset] & 0x20 ) == 1);
        this.watchdogRestart=( (msg[0+offset] & 0x40 ) == 1);
        this.bitChecksumError=( (msg[0+offset] & 0x80 ) == 1);
        this.softKwhError=( (msg[0+offset] & 0x01 ) == 1);
        this.lowAcVoltage=( (msg[0+offset] & 0x02 ) == 1);
        this.currentTooHigh=( (msg[1+offset] & 0x04) ==1 );
        this.powerFailure=( (msg[1+offset] & 0x08) == 1);
        this.hardEepromError=( (msg[1+offset] & 0x10) == 1);
        this.hardKwhError=( (msg[1+offset] & 0x20) == 1);
        this.configurationError=( (msg[1+offset] & 0x40) == 1);
        this.reversePower=( (msg[1+offset] & 0x80) == 1);
        // this is a hack for the voltage range.  If the lowBattery bit
        // is set (refer to the DataMessage header), then the range on the voltage is
        // 40-166V, otherwise it's 166-292V.  Pray we need no more voltage ranges
        int voltageOffset=166;
        if ( this.isLowBattery() ) {
            voltageOffset=40;
        }
        this.avgVoltage=(int)( ( (msg[3+offset])>>>4) + ((msg[4+offset]&0x3)<<4) ) * 2;
        this.minVoltage=(int)( msg[2+offset]&0x3F ) * 2;
        this.maxVoltage=(int)( (msg[2+offset]>>>6) + (msg[3+offset]&0xF)<<2 ) * 2;
        if ( avgVoltage != 0 ) {avgVoltage+=voltageOffset; }
        if ( minVoltage != 0 ) {minVoltage+=voltageOffset; }
        if ( maxVoltage != 0 ) {maxVoltage+=voltageOffset; }
        this.actualVoltage=(int)( msg[5+offset]&0x3F ) * 2+voltageOffset;
        this.clickCount=(int)(msg[6+offset]);
        this.timeSinceEvent=(int)(msg[7+offset])*6;
        this.currentReading = (int) (msg[8 + offset]) + (int) (msg[9 + offset] << 8) + (int) (msg[10 + offset] << 16);
        this.deviceTemperature = (int)msg[11 + offset];
        this.extendedTimeSinceEvent = (int)( (msg[15 + offset] << 8) + (msg[14+offset]) )*1536+this.timeSinceEvent;
        this.timestampOfEvent = new java.util.Date( (this.getToi()-extendedTimeSinceEvent)*1000 );
    }

    public String getFlags() {
        StringBuffer msg=new StringBuffer();
        if (this.calibrationError) msg.append("Calibration Error ");
        if (this.registerCheckError) msg.append("Register Check Error ");
        if (this.resetError) msg.append( "Reset Error ");
        if (this.ramBitError) msg.append( "RAM bit error ");
        if (this.softEepromError) msg.append( "Soft EEPROM Error ");
        if (this.watchdogRestart) msg.append( "Watchdog Restart ");
        if (this.bitChecksumError) msg.append( "Bit Checksum Error ");
        if (this.softKwhError) msg.append( "Soft KWh Error ");
        if (this.lowAcVoltage) msg.append( "Low AC Voltage ");
        if (this.currentTooHigh) msg.append( "Current too High ");
        if (this.powerFailure) msg.append( "Power Failure ");
        if (this.hardEepromError) msg.append( "Hard EEPROM Error ");
        if (this.hardKwhError) msg.append( "Configuration Error ");
        if (this.reversePower) msg.append( "Reverse Power ");
        return msg.toString();
    }

    public int getMaxVoltage() {
        return this.maxVoltage;
    }
    public int getMinVoltage() {
        return this.minVoltage;
    }
    public int getAvgVoltage() {
        return this.avgVoltage;
    }
    public int getActualVoltage() {
        return this.actualVoltage;
    }
    public int getClickCount() {
        return this.clickCount;
    }
    public int getCurrentReading() {
        return this.currentReading;
    }
    public int getTimeSinceEvent() {
        return this.timeSinceEvent;
    }
    public java.util.Date getTimestampOfEvent() {
        return this.timestampOfEvent;
    }
    public boolean isBitChecksumError() {
        return bitChecksumError;
    }
    public boolean isCalibrationError() {
        return calibrationError;
    }
    public boolean isConfigurationError() {
        return configurationError;
    }
    public boolean isCurrentTooHigh() {
        return currentTooHigh;
    }
    public boolean isGeneralCrcError() {
        return generalCrcError;
    }
    public boolean isHardEepromError() {
        return hardEepromError;
    }
    public boolean isHardKwhError() {
        return hardKwhError;
    }
    public boolean isLowAcVoltage() {
        return lowAcVoltage;
    }
    public boolean isPowerFailure() {
        return powerFailure;
    }
    public boolean isRamBitError() {
        return ramBitError;
    }
    public boolean isRegisterCheckError() {
        return registerCheckError;
    }
    public boolean isResetError() {
        return resetError;
    }
    public boolean isReversePower() {
        return reversePower;
    }
    public boolean isSoftEepromError() {
        return softEepromError;
    }
    public boolean isSoftKwhError() {
        return softKwhError;
    }
    public boolean isWatchdogRestart() {
        return watchdogRestart;
    }
    public int getDeviceTemperature() {
        return this.deviceTemperature;
    }
    public String getMsgClass() {
        return this.msgClass;
    }
    public int getExtendedTimeSinceEvent() {
        return this.extendedTimeSinceEvent;
    }
}
