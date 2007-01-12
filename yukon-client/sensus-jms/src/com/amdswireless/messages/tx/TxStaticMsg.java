package com.amdswireless.messages.tx;



public class TxStaticMsg extends TxMsg {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final int appbase=29; 
    private int meterSampleRate;
    private int supervisoryTransmitMultiple;
    private int baseFrequency;
    private int transmitChannelA;
    private int transmitChannelB;
    private int transmitChannelC;
    private int transmitChannelD;
    private int receiverChannel;
    private int boostModeChannel;
    private int boostModeSubChannel;
    private int boostModeHopping;
    private int transmitChannelMask;
    private int transmitterOperationalMode;
    private int receiverOperationalMode;
    private boolean enableEncryption;
    private int programmerId = 0;
    /*
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
       25 Minimum Click Duration
       26(0:5) Minimum Voltage Threshold
       26(6:7) Voltage Averaging Window
       27(0:3) Outage Time Threshold
     */

    public TxStaticMsg() {
        super();
        try {
            super.setCommand((short)0x54);
            super.setLength((short)0x24);
            super.setAppCode((short)0x7);
            super.setMiscTx((short)0x1);
            super.setCommandType((short)0x1);
            super.setNcId((short)0x01);
            super.setToi(new java.util.Date().getTime()/1000);
        } catch ( Exception ex ) {
            System.err.println("Error initializing TxDisplayMsg:  "+ex );
        }
    }

    public String getMsgText() {
        super.updateMsg((short)((supervisoryTransmitMultiple<<3) + meterSampleRate), appbase );
        super.updateMsg((short)baseFrequency, appbase+1 );
        super.updateMsg((short)transmitChannelA, appbase+2);
        super.updateMsg((short)(transmitChannelA>>>8), appbase+3);
        super.updateMsg((short)transmitChannelB, appbase+4);
        super.updateMsg((short)(transmitChannelB>>>8), appbase+5);
        super.updateMsg((short)transmitChannelC, appbase+6);
        super.updateMsg((short)(transmitChannelC>>>8), appbase+7);
        super.updateMsg((short)transmitChannelD, appbase+8);
        super.updateMsg((short)(transmitChannelD>>>8), appbase+9);
        super.updateMsg((short)receiverChannel, appbase+10);
        super.updateMsg((short)(receiverChannel>>>8), appbase+11);
        super.updateMsg((short)boostModeChannel, appbase+12);
        super.updateMsg((short)(boostModeChannel>>>8), appbase+13);
        int Byte18 = boostModeSubChannel + (transmitChannelMask<<5) + (boostModeHopping << 4);
        super.updateMsg((short)Byte18, appbase+14);
        int Byte19 = transmitterOperationalMode + ( receiverOperationalMode<<4 );
        if ( enableEncryption ) { Byte19 += 0xF; }
        super.updateMsg((short)Byte19, appbase+15);
        super.updateMsg((short)programmerId, appbase+21 );
        super.updateMsg((short)(programmerId>>>8), appbase+20 );
        return super.getMsgText();
    }

	//accessors / mutators
    public int getMeterSampleRate() {
		return meterSampleRate;
	}
    public void setMeterSampleRate(int meterSampleRate) {
		this.meterSampleRate = meterSampleRate;
	}
    public int getSupervisoryTransmitMultiple() {
		return supervisoryTransmitMultiple;
	}
    public void setSupervisoryTransmitMultiple(int supervisoryTransmitMultiple) {
		this.supervisoryTransmitMultiple = supervisoryTransmitMultiple;
	}
    
	public int getBaseFrequency() {
		return baseFrequency;
	}

    public void setBaseFrequency(int baseFrequency) {
		this.baseFrequency = baseFrequency;
	}

    public int getTransmitChannelA() {
		return transmitChannelA;
	}

    public void setTransmitChannelA(int transmitChannelA) {
		this.transmitChannelA = transmitChannelA;
	}

    public int getTransmitChannelB() {
		return transmitChannelB;
	}

    public void setTransmitChannelB(int transmitChannelB) {
		this.transmitChannelB = transmitChannelB;
	}

    public int getTransmitChannelC() {
		return transmitChannelC;
	}

    public void setTransmitChannelC(int transmitChannelC) {
		this.transmitChannelC = transmitChannelC;
	}

    public int getTransmitChannelD() {
		return transmitChannelD;
	}

    public void setTransmitChannelD(int transmitChannelD) {
		this.transmitChannelD = transmitChannelD;
	}

    public int getReceiverChannel() {
		return receiverChannel;
	}

    public void setReceiverChannel(int receiverChannel) {
		this.receiverChannel = receiverChannel;
	}

    public int getBoostModeChannel() {
		return boostModeChannel;
	}

    public void setBoostModeChannel(int boostModeChannel) {
		this.boostModeChannel = boostModeChannel;
	}

    public int getBoostModeSubChannel() {
		return boostModeSubChannel;
	}

    public void setBoostModeSubChannel(int boostModeSubChannel) {
		this.boostModeSubChannel = boostModeSubChannel;
	}

    public int getBoostModeHopping() {
		return boostModeHopping;
	}

    public void setBoostModeHopping(int boostModeHopping) {
		this.boostModeHopping = boostModeHopping;
	}

    public int getTransmitChannelMask() {
		return transmitChannelMask;
	}

    public void setTransmitChannelMask(int transmitChannelMask) {
		this.transmitChannelMask = transmitChannelMask;
	}

    public int getTransmitterOperationalMode() {
		return transmitterOperationalMode;
	}

    public void setTransmitterOperationalMode(int transmitterOperationalMode) {
		this.transmitterOperationalMode = transmitterOperationalMode;
	}

    public int getReceiverOperationalMode() {
		return receiverOperationalMode;
	}

    public void setReceiverOperationalMode(int receiverOperationalMode) {
		this.receiverOperationalMode = receiverOperationalMode;
	}

    public boolean getEnableEncryption() {
		return enableEncryption;
	}

    public void setEnableEncryption(boolean enableEncryption) {
		this.enableEncryption = enableEncryption;
	}

    public static void main( String[] args ) {
        // This is just a static test mamma-jamma
        TxStaticMsg s = new TxStaticMsg();
        s.supervisoryTransmitMultiple=2;
        s.meterSampleRate=2;
        s.baseFrequency=38;
        s.transmitChannelA=818;
        s.transmitChannelB=840;
        s.transmitChannelC=832;
        s.transmitChannelD=840;
        s.receiverChannel=7327;
        s.boostModeChannel=814;
        s.boostModeSubChannel=0;
        s.transmitChannelMask=0;
        s.transmitterOperationalMode=0;
        s.receiverOperationalMode=0;
        s.transmitChannelMask=0;
        s.programmerId=0;
        System.out.println(s.getMsgText());
    }
}
