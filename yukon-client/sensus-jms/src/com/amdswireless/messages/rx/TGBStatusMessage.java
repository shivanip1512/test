package com.amdswireless.messages.rx;


public class TGBStatusMessage extends AndorianMessage implements java.io.Serializable {
	private transient static final long serialVersionUID = 1L;
	private final String msgClass = "TGB";
	private boolean doorTamper;
	private boolean overTemperature;
	private boolean underTemperature;
	private boolean rectifierOutOfRange;
	private boolean acPowerFail;
	private boolean lowBattery;
	private boolean[] activeCpcChannels = new boolean[9];
	private int[] cpcCardTemperatures = new int[9];
	private int[] channelNumber = new int[9];
	private int uptime;
	private float load;
	
	public TGBStatusMessage( char[] msg ) {
		super(msg);
		doorTamper = (int)(msg[16] & 0x01) == 0x01;
		overTemperature = (int)(msg[16] & 0x02) == 0x02;
		underTemperature = (int)(msg[16] & 0x04) == 0x04;
		rectifierOutOfRange = (int)(msg[16] & 0x08) == 0x08;
		acPowerFail = (int)(msg[16] & 0x10) == 0x10;
		lowBattery = (int)(msg[16] & 0x40) == 0x40;
		for (int i = 0; i < 8; i++ ) {
			activeCpcChannels[i] = (int)(msg[17] & (i*2)) == (i*2);
		}
		activeCpcChannels[8] = (int)(msg[18] & 0x01) == 0x01;
		for (int i = 0; i<9; i++ ) {
			cpcCardTemperatures[i] = (int)msg[19+i];
		}
		
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public boolean isAcPowerFail() {
		return acPowerFail;
	}

	public boolean[] getActiveCpcChannels() {
		return activeCpcChannels;
	}

	public int[] getChannelNumber() {
		return channelNumber;
	}

	public int[] getCpcCardTemperatures() {
		return cpcCardTemperatures;
	}

	public boolean isDoorTamper() {
		return doorTamper;
	}

	public float getLoad() {
		return load;
	}

	public boolean isLowBattery() {
		return lowBattery;
	}

	public String getMsgClass() {
		return msgClass;
	}

	public boolean isOverTemperature() {
		return overTemperature;
	}

	public boolean isRectifierOutOfRange() {
		return rectifierOutOfRange;
	}

	public boolean isUnderTemperature() {
		return underTemperature;
	}

	public int getUptime() {
		return uptime;
	}

}
