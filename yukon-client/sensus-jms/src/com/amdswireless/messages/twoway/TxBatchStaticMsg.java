package com.amdswireless.messages.twoway;


public class TxBatchStaticMsg extends TxPadCommand {

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

    public boolean hasMeterSampleRate;
    public boolean hasSupervisoryTransmitMultiple;
    public boolean hasBaseFrequency;
    public boolean hasTransmitChannelA;
    public boolean hasTransmitChannelB;
    public boolean hasTransmitChannelC;
    public boolean hasTransmitChannelD;
    public boolean hasReceiverChannel;
    public boolean hasBoostModeChannel;
    public boolean hasBoostModeSubChannel;
    public boolean hasBoostModeHopping;
    public boolean hasTransmitChannelMask;
    public boolean hasTransmitterOperationalMode;
    public boolean hasReceiverOperationalMode;
    public boolean hasEnableEncryption;

    public TxBatchStaticMsg() {
        super();
    	hasMeterSampleRate = false;
    	hasSupervisoryTransmitMultiple = false;
    	hasBaseFrequency = false;
    	hasTransmitChannelA = false;
    	hasTransmitChannelB = false;
    	hasTransmitChannelC = false;
    	hasTransmitChannelD = false;
    	hasReceiverChannel = false;
    	hasBoostModeChannel = false;
    	hasBoostModeSubChannel = false;
    	hasBoostModeHopping = false;
    	hasTransmitChannelMask = false;
    	hasTransmitterOperationalMode = false;
    	hasReceiverOperationalMode = false;
    	hasEnableEncryption = false;
    }

	public boolean isComplete() {
		return hasMeterSampleRate && hasSupervisoryTransmitMultiple && hasBaseFrequency &&
		    	hasTransmitChannelA && hasTransmitChannelB && hasTransmitChannelC && 
				hasTransmitChannelD && hasReceiverChannel && hasBoostModeChannel &&
				hasBoostModeSubChannel && hasBoostModeHopping && hasTransmitChannelMask &&
				hasTransmitterOperationalMode && hasReceiverOperationalMode &&
				hasEnableEncryption;
	}

	//accessors / mutators
    public int getMeterSampleRate() {
		return meterSampleRate;
	}
    public void setMeterSampleRate(int meterSampleRate) {
		this.meterSampleRate = meterSampleRate;
		hasMeterSampleRate = true;
	}
    public int getSupervisoryTransmitMultiple() {
		return supervisoryTransmitMultiple;
	}
    public void setSupervisoryTransmitMultiple(int supervisoryTransmitMultiple) {
		this.supervisoryTransmitMultiple = supervisoryTransmitMultiple;
		hasSupervisoryTransmitMultiple = true;
	}
    
	public int getBaseFrequency() {
		return baseFrequency;
	}

    public void setBaseFrequency(int baseFrequency) {
		this.baseFrequency = baseFrequency;
		hasBaseFrequency = true;
	}

    public int getTransmitChannelA() {
		return transmitChannelA;
	}

    public void setTransmitChannelA(int transmitChannelA) {
		this.transmitChannelA = transmitChannelA;
		hasTransmitChannelA = true;
	}

    public int getTransmitChannelB() {
		return transmitChannelB;
	}

    public void setTransmitChannelB(int transmitChannelB) {
		this.transmitChannelB = transmitChannelB;
		hasTransmitChannelB = true;
	}

    public int getTransmitChannelC() {
		return transmitChannelC;
	}

    public void setTransmitChannelC(int transmitChannelC) {
		this.transmitChannelC = transmitChannelC;
		hasTransmitChannelC = true;
	}

    public int getTransmitChannelD() {
		return transmitChannelD;
	}

    public void setTransmitChannelD(int transmitChannelD) {
		this.transmitChannelD = transmitChannelD;
		hasTransmitChannelD = true;
	}

    public int getReceiverChannel() {
		return receiverChannel;
	}

    public void setReceiverChannel(int receiverChannel) {
		this.receiverChannel = receiverChannel;
		hasReceiverChannel = true;
	}

    public int getBoostModeChannel() {
		return boostModeChannel;
	}

    public void setBoostModeChannel(int boostModeChannel) {
		this.boostModeChannel = boostModeChannel;
		hasBoostModeChannel = true;
	}

    public int getBoostModeSubChannel() {
		return boostModeSubChannel;
	}

    public void setBoostModeSubChannel(int boostModeSubChannel) {
		this.boostModeSubChannel = boostModeSubChannel;
		hasBoostModeSubChannel = true;
	}

    public int getBoostModeHopping() {
		return boostModeHopping;
	}

    public void setBoostModeHopping(int boostModeHopping) {
		this.boostModeHopping = boostModeHopping;
		hasBoostModeHopping = true;
	}

    public int getTransmitChannelMask() {
		return transmitChannelMask;
	}

    public void setTransmitChannelMask(int transmitChannelMask) {
		this.transmitChannelMask = transmitChannelMask;
		hasTransmitChannelMask = true;
	}

    public int getTransmitterOperationalMode() {
		return transmitterOperationalMode;
	}

    public void setTransmitterOperationalMode(int transmitterOperationalMode) {
		this.transmitterOperationalMode = transmitterOperationalMode;
		hasTransmitterOperationalMode = true;
	}

    public int getReceiverOperationalMode() {
		return receiverOperationalMode;
	}

    public void setReceiverOperationalMode(int receiverOperationalMode) {
		this.receiverOperationalMode = receiverOperationalMode;
		hasReceiverOperationalMode = true;
	}

    public boolean getEnableEncryption() {
		return enableEncryption;
	}

    public void setEnableEncryption(boolean enableEncryption) {
		this.enableEncryption = enableEncryption;
		hasEnableEncryption = true;
	}


}

