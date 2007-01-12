package com.amdswireless.messages.rx;

import java.util.Arrays;

/**
 * Type 1 Application Messages are Configuration Messages sent by iCon and Elster
 * electric meters.  
 * @author johng
 *
 */

public class AppMessageType1 extends DataMessage implements AppMessage, java.io.Serializable {
	/**
	 * 
	 */
	private transient static final long serialVersionUID = -2877720172526362109L;
	private transient static final int vawString[] = {1, 6, 12, 24};
	private transient static final int offset=31;
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
	private int minClickDuration;
	private int minVoltageThreshold;
	private int voltageAvgWindow;
	private int outageTimeThreshold;
	private int restoredTimeThreshold;
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
25 Minimum Click Duration
26(0:5) Minimum Voltage Threshold
26(6:7) Voltage Averaging Window
27(0:3) Outage Time Threshold
27(4:7) Restored Time Threshold
*/
	public AppMessageType1() {
		super();
		super.setAppCode(1);
		super.setMessageType(1);
	}
	
	public AppMessageType1( char[] msg ) {
		super(msg);
		super.setMessageType(1);
		this.firmwareVersion=(int)msg[offset]; //msg[0+offset] 
		this.deviceType=(int)msg[1+offset];
		this.meterSampleRate=(int)(msg[2+offset]&0x7);
		this.supervisoryTransmitMultiple=(int)((msg[2+offset]&0xF8))>>>3;
		this.baseFrequency=(int)(msg[3+offset]);
		this.transmitChannelA=(int)((msg[5+offset]<< 8)+msg[4+offset]);
		this.transmitChannelB=(int)((msg[7+offset]<< 8)+msg[6+offset]);
		this.transmitChannelC=(int)((msg[9+offset]<< 8)+msg[8+offset]);
		this.transmitChannelD=(int)((msg[11+offset]<< 8)+msg[10+offset]);
		this.receiveChannel=(int)((msg[13+offset]<< 8)+msg[12+offset]);
		this.boostModeChannel=(int)((msg[15+offset]<< 8)+msg[14+offset]);
		this.boostModeSubChannel=(int)(msg[16+offset]&0xF);
		this.boostHopping=(int)(msg[16+offset]&0x10)>>>4;
		this.transmitChannelMask=(int)(msg[16+offset]&0x7>>>5);
		this.transmitOperationalMode=(int)(msg[17+offset]&0xF);
		this.receiveOperationalMode=(int)(msg[17+offset]&0x70)>>>4;
		this.encryption=( (msg[17+offset]&0x80) == 0x80 );
		this.rtc=(int)((msg[19+offset]<<8)+msg[18+offset]);
		this.txcoCorrection=(int)((msg[21+offset]<< 8)+msg[20+offset]);
		this.adCalibration=(int)((msg[24+offset]<< 16)+(msg[23+offset]<< 8)+msg[22+offset]);
		this.minClickDuration=(int)(msg[25+offset]);
		this.minVoltageThreshold=(int)(msg[26+offset] & 0x3F)*2+166;
		this.voltageAvgWindow=vawString[(int)((msg[26+offset] & 0xC0) >>> 6 )];
		this.outageTimeThreshold=(int)(msg[27+offset] & 0xF) * 8;
		this.restoredTimeThreshold=(int)((msg[27+offset] & 0xF0)>>> 4) * 8;
	}
	/**
	 * @return Returns the serialVersionUID, indicating the version of serialization
	 * used by this message.
	 */
	public static long getSerialVersionUID() {
		return serialVersionUID;
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
	 * @return Returns the minClickDuration.
	 */
	public int getMinClickDuration() {
		return minClickDuration;
	}
	/**
	 * @return Returns the minVoltageThreshold.
	 */
	public int getMinVoltageThreshold() {
		return minVoltageThreshold;
	}
	/**
	 * @return Returns the outageTimeThreshold.
	 */
	public int getOutageTimeThreshold() {
		return outageTimeThreshold;
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
	 * @return Returns the restoredTimeThreshold.
	 */
	public int getRestoredTimeThreshold() {
		return restoredTimeThreshold;
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
	public int getVoltageAvgWindow() {
		return voltageAvgWindow;
	}

	public String getMsgClass() {
		return msgClass;
	}

	/**
	 * @param firmwareVersion The firmwareVersion to set.
	 */
	public void setFirmwareVersion(int firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
		
		message[offset] = (char) (firmwareVersion);
	}

	/**
	 * @param deviceType The deviceType to set.
	 */
	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
		
		message[1 + offset] = (char) (deviceType);
	}
	
	/**
	 * @param meterSampleRate The meterSampleRate to set.
	 */
	public void setMeterSampleRate(int meterSampleRate) {
		this.meterSampleRate = meterSampleRate;
		
		message[2 + offset] = (char) (meterSampleRate & 0x7);		 
	}
	
	/**
	 * @param supervisoryTransmitMultiple The supervisoryTransmitMultiple to set.
	 */
	public void setSupervisoryTransmitMultiple(int supervisoryTransmitMultiple) {
		this.supervisoryTransmitMultiple = supervisoryTransmitMultiple;
		
		message[2 + offset] |= (char) ((supervisoryTransmitMultiple & 0xF8) << 3);
	}

	/**
	 * @param baseFrequency The baseFrequency to set.
	 */
	public void setBaseFrequency(int baseFrequency) {
		this.baseFrequency = baseFrequency;
		
		message[3 + offset] = (char) (baseFrequency);
	}

	/**
	 * @param transmitChannelA The transmitChannelA to set.
	 */
	public void setTransmitChannelA(int transmitChannelA) {
		this.transmitChannelA = transmitChannelA;
		
		message[4 + offset] = (char) (transmitChannelA & 0xFF);
		message[5 + offset] = (char) (transmitChannelA & 0xFF00 >>> 8);
	}

	/**
	 * @param transmitChannelB The transmitChannelB to set.
	 */
	public void setTransmitChannelB(int transmitChannelB) {
		this.transmitChannelB = transmitChannelB;
		
		message[6 + offset] = (char) (transmitChannelB & 0xFF);
		message[7 + offset] = (char) ((transmitChannelB & 0xFF00) >>> 8);
	}

	/**
	 * @param transmitChannelC The transmitChannelC to set.
	 */
	public void setTransmitChannelC(int transmitChannelC) {
		this.transmitChannelC = transmitChannelC;
		
		message[8 + offset] = (char) (transmitChannelC & 0xFF);
		message[9 + offset] = (char) ((transmitChannelC & 0xFF00) >>> 8);
	}

	/**
	 * @param transmitChannelD The transmitChannelD to set.
	 */
	public void setTransmitChannelD(int transmitChannelD) {
		this.transmitChannelD = transmitChannelD;
		
		message[10 + offset] = (char) (transmitChannelD & 0xFF);
		message[11 + offset] = (char) ((transmitChannelD & 0xFF00) >>> 8);
	}

	/**
	 * @param receiveChannel The receiveChannel to set.
	 */
	public void setReceiveChannel(int receiveChannel) {
		this.receiveChannel = receiveChannel;
		
		message[12 + offset] = (char) (receiveChannel & 0xFF);
		message[13 + offset] = (char) (receiveChannel & 0xFF00 >>> 8);
	}
	
	/**
	 * @param boostModeChannel The boostModeChannel to set.
	 */
	public void setBoostModeChannel(int boostModeChannel) {
		this.boostModeChannel = boostModeChannel;
		
		message[14 + offset] = (char) (boostModeChannel & 0xFF);
		message[15 + offset] = (char) (boostModeChannel & 0XFF00 >>> 8);
	}

	/**
	 * @param boostModeSubChannel The boostModeSubChannel to set.
	 */
	public void setBoostModeSubChannel(int boostModeSubChannel) {
		this.boostModeSubChannel = boostModeSubChannel;
		
		message[16 + offset] |= (char) (boostModeSubChannel & 0XF);
	}

	/**
	 * @param transmitChannelMask The transmitChannelMask to set.
	 */
	public void setTransmitChannelMask(int transmitChannelMask) {
		this.transmitChannelMask = transmitChannelMask;
		
		message[16 + offset] |= (char) ((transmitChannelMask & 0x7) << 5);
	}

	/**
	 * @param boostHopping The boostHopping to set.
	 */
	public void setBoostHopping(int boostHopping) {
		this.boostHopping = boostHopping;
		
		message[16 + offset] |= (char) (boostHopping & 0x10 << 4);
	}
	
	/**
	 * @param transmitOperationalMode The transmitOperationalMode to set.
	 */
	public void setTransmitOperationalMode(int transmitOperationalMode) {
		this.transmitOperationalMode = transmitOperationalMode;
		
		message[17 + offset] |= (char) ((transmitOperationalMode & 0xF));
	}
	
	/**
	 * @param receiveOperationalMode The receiveOperationalMode to set.
	 */
	public void setReceiveOperationalMode(int receiveOperationalMode) {
		this.receiveOperationalMode = receiveOperationalMode;
		
		message[17 + offset] |= (char) (receiveOperationalMode & 0x70 << 4);
	}
	
	/**
	 * @param encryption The encryption to set.
	 */
	public void setEncryption(boolean encryption) {
		this.encryption = encryption;	
		
		if ( this.encryption ) {
			message[17 + offset] |= (char) (0x80);
		}
	}
	
	/**
	 * @param rtc The rtc to set.
	 */
	public void setRtc(int rtc) {
		this.rtc = rtc;
		
		message[18 + offset] = (char) (rtc & 0xFF);
		message[19 + offset] = (char) ((rtc & 0xFF00) >>> 8);
	}

	/**
	 * @param txcoCorrection The txcoCorrection to set.
	 */
	public void setTxcoCorrection(int txcoCorrection) {
		this.txcoCorrection = txcoCorrection;
		
		message[20 + offset] = (char) (txcoCorrection & 0xFF);
		message[21 + offset] = (char) ((txcoCorrection & 0xFF00) >>> 8);
	}
	
	/**
	 * @param adCalibration The adCalibration to set.
	 */
	public void setAdCalibration(int adCalibration) {
		this.adCalibration = adCalibration;
		
		message[22 + offset] = (char) (adCalibration & 0xFF);
		message[23 + offset] = (char) (adCalibration & 0XFF00 >>> 8);
		message[24 + offset] = (char) (adCalibration & 0XFF0000 >>> 16);		
	}
	
	/**
	 * @param minClickDuration The minClickDuration to set.
	 */
	public void setMinClickDuration(int minClickDuration) {
		this.minClickDuration = minClickDuration;
		
		message[25 + offset] = (char) (minClickDuration);		
	}

	/**
	 * @param minVoltageThreshold The minVoltageThreshold to set.
	 */
	public void setMinVoltageThreshold(int minVoltageThreshold) {
		this.minVoltageThreshold = minVoltageThreshold;
		
		minVoltageThreshold = (minVoltageThreshold - 166) / 2;
		message[26 + offset] |= (char) (minVoltageThreshold & 0x3F);
	}

	/**
	 * @param voltageAvgWindow The voltageAvgWindow to set.
	 */
	public void setVoltageAvgWindow(int voltageAvgWindow) {
		this.voltageAvgWindow = voltageAvgWindow;
		
		int indexOfvoltageAvgWindow = Arrays.binarySearch(vawString, voltageAvgWindow);
		message[26 + offset] |= (char) ((indexOfvoltageAvgWindow & 0xC0) << 6);
		
	}
	
	/**
	 * @param outageTimeThreshold The outageTimeThreshold to set.
	 */
	public void setOutageTimeThreshold(int outageTimeThreshold) {
		this.outageTimeThreshold = outageTimeThreshold;
		
		outageTimeThreshold /= 8; 
		message[27 + offset] |= (char) (minVoltageThreshold & 0xF);
	}

	/**
	 * @param restoredTimeThreshold The restoredTimeThreshold to set.
	 */
	public void setRestoredTimeThreshold(int restoredTimeThreshold) {
		this.restoredTimeThreshold = restoredTimeThreshold;
		
		restoredTimeThreshold /= 8;
		message[27 + offset] |= (char) (restoredTimeThreshold & 0xF0 << 4);
	}
}
