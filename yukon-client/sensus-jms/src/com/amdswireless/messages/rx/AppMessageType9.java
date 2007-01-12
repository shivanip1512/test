package com.amdswireless.messages.rx;


public class AppMessageType9 extends DataMessage implements AppMessage, java.io.Serializable {
    private transient static final long serialVersionUID = -2877720172526362109L;
    private transient static final int vawString[] = {1, 6, 12, 24};
    private final String msgClass = "CONFIG";
    private int firmwareVersion;
    private int deviceType;
    private int meterSampleRate;
    private int supervisoryTransmitMultiple;
    private int baseFrequency;
    private int transmitChannelA;
    private int transmitChannelB;
    private int transmitChannelC;
    private int transmitChannelD;
    private int receiveChannel;
    private int boostModeChannel;
    private int boostModeSubChannel;
    private int boostHopping;
    private int transmitChannelMask;
    private int transmitOperationalMode;
    private int receiveOperationalMode;
    private boolean encryption;
    private int rtc;
    private int txcoCorrection;
    private int adCalibration;
    private int interfaceMeterA;
    private int scaleMeterA;
    private int leakSettingsMeterA;

    /*
       0 Firmware Version
       1 Device Type
       2 (0:2) Meter Sample Rate
       2 (3:7) Supervisory Transmit Rate
       3 Base Frequency
       4-5 Transmit Channel A
       6-7 Transmit Channel B
       8-9 Transmit Channel C
       10-11 Transmit Channel D
       12-13 Receiver Channel
       14-15 Boost Mode Channel
       16 (0:3) Boost Mode Sub-Channel
       16 (4) Boost Mode Hopping
       16 (5:7) Transmit Channel Mask
       17 (0:3) Transmitter Operational Mode
       17 (4:6) Receiver Operational Mode
       17(7) Enable Encryption
       18-19 Real Time Clock
       20-21 TXCO Correction
       22-24 A/D Calibration
       25    Interface Meter A
       26    Scale Meter A
       27    Leak Settings Meter A
       */
    public AppMessageType9( char[] msg ) {
        super(msg);
        super.setMessageType(0x9);
        int offset=31;
        this.firmwareVersion=(int)msg[0+offset];
        this.deviceType=(int)msg[1+offset];
        this.meterSampleRate=(int)(msg[2+offset]&0x7);
        this.supervisoryTransmitMultiple=(int)((msg[2+offset]&0xF8))>>>3;
        this.baseFrequency=(int)(msg[3+offset]);
        this.transmitChannelA=(int)((msg[5+offset]<<0x8)+msg[4+offset]);
        this.transmitChannelB=(int)((msg[7+offset]<<0x8)+msg[6+offset]);
        this.transmitChannelC=(int)((msg[9+offset]<<0x8)+msg[8+offset]);
        this.transmitChannelD=(int)((msg[11+offset]<<0x8)+msg[10+offset]);
        this.receiveChannel=(int)((msg[13+offset]<<0x8)+msg[12+offset]);
        this.boostModeChannel=(int)((msg[15+offset]<<0x8)+msg[14+offset]);
        this.boostModeSubChannel=(int)(msg[16+offset]&0xF0);
        this.boostHopping=(int)(msg[16+offset]&0xF)>>>4;
        this.transmitChannelMask=(int)(msg[16+offset]&0x7>>>5);
        this.transmitOperationalMode=(int)(msg[17+offset]&0xF0);
        this.receiveOperationalMode=(int)(msg[17+offset]&0x38)>>>4;
        this.encryption=( (msg[17+offset]&0x80) == 0x80 );
        this.rtc=(int)((msg[19+offset]<<8)+msg[18+offset]);
        this.txcoCorrection=(int)((msg[21+offset]<<8)+msg[20+offset]);
        this.adCalibration=(int)((msg[24+offset]<<16)+(msg[23+offset]<<8)+msg[22+offset]);
        this.interfaceMeterA=(int)(msg[25+offset]);
        this.scaleMeterA=(int)msg[26+offset];
        this.leakSettingsMeterA=(int)msg[27+offset];
    }
    /**
     * @return Returns the serialVersionUID.
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
    /**
     * @return Returns the vawString.
     */
    public static int[] getVawString() {
        return vawString;
    }
    /**
     * @return Returns the adCalibration.
     */
    public int getAdCalibration() {
        return adCalibration;
    }
    /**
     * @return Returns the baseFrequency.
     */
    public int getBaseFrequency() {
        return baseFrequency;
    }
    /**
     * @return Returns the boostHopping.
     */
    public int getBoostHopping() {
        return boostHopping;
    }
    /**
     * @return Returns the boostModeChannel.
     */
    public int getBoostModeChannel() {
        return boostModeChannel;
    }
    /**
     * @return Returns the boostModeSubChannel.
     */
    public int getBoostModeSubChannel() {
        return boostModeSubChannel;
    }
    /**
     * @return Returns the deviceType.
     */
    public int getDeviceType() {
        return deviceType;
    }
    /**
     * @return Returns the encryption.
     */
    public boolean isEncryption() {
        return encryption;
    }
    /**
     * @return Returns the firmwareVersion.
     */
    public int getFirmwareVersion() {
        return firmwareVersion;
    }
    /**
     * @return Returns the meterSampleRate.
     */
    public int getMeterSampleRate() {
        return meterSampleRate;
    }
    /**
     * @return Returns the receiveChannel.
     */
    public int getReceiveChannel() {
        return receiveChannel;
    }
    /**
     * @return Returns the receiveOperationalMode.
     */
    public int getReceiveOperationalMode() {
        return receiveOperationalMode;
    }
    /**
     * @return Returns the rtc.
     */
    public int getRtc() {
        return rtc;
    }
    /**
     * @return Returns the supervisoryTransmitMultiple.
     */
    public int getSupervisoryTransmitMultiple() {
        return supervisoryTransmitMultiple;
    }
    /**
     * @return Returns the transmitChannelA.
     */
    public int getTransmitChannelA() {
        return transmitChannelA;
    }
    /**
     * @return Returns the transmitChannelB.
     */
    public int getTransmitChannelB() {
        return transmitChannelB;
    }
    /**
     * @return Returns the transmitChannelC.
     */
    public int getTransmitChannelC() {
        return transmitChannelC;
    }
    /**
     * @return Returns the transmitChannelD.
     */
    public int getTransmitChannelD() {
        return transmitChannelD;
    }
    /**
     * @return Returns the transmitChannelMask.
     */
    public int getTransmitChannelMask() {
        return transmitChannelMask;
    }
    /**
     * @return Returns the transmitOperationalMode.
     */
    public int getTransmitOperationalMode() {
        return transmitOperationalMode;
    }
    /**
     * @return Returns the txcoCorrection.
     */
    public int getTxcoCorrection() {
        return txcoCorrection;
    }
    /**
     * @return Returns the voltageAvgWindow.
     */
    /**
     * @return Returns the interfaceMeterA.
     */
    public int getInterfaceMeterA() {
        return interfaceMeterA;
    }
    /**
     * @return Returns the leakSettingsMeterA.
     */
    public int getLeakSettingsMeterA() {
        return leakSettingsMeterA;
    }
    /**
     * @return Returns the scaleMeterA.
     */
    public int getScaleMeterA() {
        return scaleMeterA;
    }
    public String getMsgClass() {
        return this.msgClass;
    }
}
