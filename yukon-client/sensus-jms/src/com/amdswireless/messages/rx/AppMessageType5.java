package com.amdswireless.messages.rx;

import java.io.UnsupportedEncodingException;


//  Andorian Data Message, App Code 5

public class AppMessageType5 extends DataMessage implements AppMessage, java.io.Serializable {
	private transient static final long serialVersionUID = -5411936557524870135L;
	private transient static int offset=31;
	private final String msgClass="BINDING";
	private boolean justProgrammed;
	private boolean setId;
	private boolean staticSetup;
	private boolean setCrystalOffset;
	private boolean setLatLong;
	private boolean setMeterReading;
	private boolean setVoltageQualityLevels;
	private boolean setEncryptionKey;
	private boolean setRealTime;
	private String  iconSerialNumber;
	private float latitude;
	private float longitude;
	private int programmerId;

	public AppMessageType5() {
		super();
		super.setAppCode(0x5);
		super.setMessageType(0x5);

	}

	public AppMessageType5( char[] msg ) {
		super(msg);
		super.setMessageType(5);
		this.justProgrammed = ((msg[0+offset]&0x1) == 1);
		//this.iconSerialNumber=new String(msg,1+offset,13);
        byte[] asciiString = new byte[13];
        int i = 0;
        while (i < 13 && msg[1+offset+i] != 0) {
            asciiString[i] = (byte)(msg[1+offset+i] & 0x7f);
            i++;
        }
        try {
            this.iconSerialNumber = new String(asciiString, 0, i, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unknown characters in iconSerialNumber field", e);
        }
		int bits = msg[14+offset]+(msg[15+offset]<<8)+(msg[16+offset]<<16)+(msg[17+offset]<<24);
		this.latitude = Float.intBitsToFloat(bits);
		bits = msg[18+offset]+(msg[19+offset]<<8)+(msg[20+offset]<<16)+(msg[21+offset]<<24);
		this.longitude = Float.intBitsToFloat(bits);
		this.programmerId=(msg[22+offset]<<8)+(msg[23+offset]);
		this.setId = ((msg[24+offset]&0x01) == 1);
		this.staticSetup = ((msg[24+offset]&0x2) == 1);
		this.setCrystalOffset = ((msg[24+offset]&0x4) == 1);
		this.setLatLong = ((msg[24+offset]&0x8) == 1);
		this.setMeterReading = ((msg[24+offset]&0x10) == 1);
		this.setVoltageQualityLevels = ((msg[24+offset]&0x20) == 1);
		this.setEncryptionKey = ((msg[24+offset]&0x40) == 1);
		this.setRealTime = ((msg[24+offset]&0x80) == 1);
	}

	public float getLatitude() {
		if ( latitude>90.0 ) {
			return this.latitude-90.0f;
		} else {
			return this.latitude;
		}
	}
	public float getLongitude() {
		if ( this.longitude > 180 ) {
			return this.longitude-180.0f;
		} else {
			return this.longitude;
		}
	}
	public boolean isJustProgrammed() {
		return this.justProgrammed;
	}
	public String getIconSerialNumber() {
		return this.iconSerialNumber;
	}
	public int getProgrammerId() {
		return this.programmerId;
	}
	public boolean isSetId() {
		return this.setId;
	}
	public boolean isStaticSetup() {
		return this.staticSetup;
	}
	public boolean isSetCrystalOffset() {
		return this.setCrystalOffset;
	}
	public boolean isSetLatLong() {
		return this.setLatLong;
	}
	public boolean isSetMeterReading() {
		return this.setMeterReading;
	}
	public boolean isSetVoltageQualityLevels() {
		return this.setVoltageQualityLevels;
	}
	public boolean isSetEncryptionKey() {
		return this.setEncryptionKey;
	}
	public boolean isSetRealTime() {
		return this.setRealTime;
	}
	public String getMsgClass() {
		return this.msgClass;
	}

	public void setIconSerialNumber(String iconSerialNumber) {
		char[] chars = iconSerialNumber.toCharArray();
		for ( int i=0; i<chars.length && i < 13; i++ ) {
			message[offset+1+i] = chars[i];
		}
		for ( int i=chars.length; i<13; i++ ) {
			message[offset+1+i] = (char)0x00;
		}
		this.iconSerialNumber = iconSerialNumber;
	}

	public void setJustProgrammed(boolean justProgrammed) {
		this.justProgrammed = justProgrammed;
		if ( justProgrammed ) {
			message[offset] |= (char)0x1;
		} else { 
			message[offset] ^= (char)(0x1);
		}
	}

	public void setLatitude(float latitude) {
		int bits = Float.floatToIntBits(latitude);
		message[14+offset] = (char)(bits & 0xFF);
		message[15+offset] = (char)((bits >> 8 ) & 0xFF );
		message[16+offset] = (char)((bits >> 16 ) & 0xFF );
		message[17+offset] = (char)((bits >> 24 ) & 0xFF );
		this.latitude = latitude;
	}

	public void setLongitude(float longitude) {
		int bits = Float.floatToIntBits(longitude);
		message[18+offset] = (char)(bits & 0xFF);
		message[19+offset] = (char)((bits >> 8 ) & 0xFF );
		message[20+offset] = (char)((bits >> 16 ) & 0xFF );
		message[21+offset] = (char)((bits >> 24 ) & 0xFF );
		this.longitude = longitude;
	}

	public void setProgrammerId(int programmerId) {
		message[22] = (char)(programmerId & 0xFF);
		message[23] = (char)((programmerId >> 8 ) & 0xFF);
		this.programmerId = programmerId;
	}

	public void setSetCrystalOffset(boolean setCrystalOffset) {
		if ( setCrystalOffset ) {
			message[offset+24] |= (char)0x4;
		} else { 
			message[offset+24] ^= (char)(0x4);
		}
		this.setCrystalOffset = setCrystalOffset;
	}

	public void setSetEncryptionKey(boolean setEncryptionKey) {
		this.setEncryptionKey = setEncryptionKey;
		if ( setEncryptionKey ) {
			message[offset+24] |= (char)0x40;
		} else { 
			message[offset+24] ^= (char)(0x40);
		}

	}
	public void setSetId(boolean setId) {
		if ( setId ) {
			message[offset+24] |= (char)0x1;
		} else { 
			message[offset+24] ^= (char)(0x1);
		}
		this.setId = setId;
	}

	public void setSetLatLong(boolean setLatLong) {
		this.setLatLong = setLatLong;
		if ( setLatLong ) {
			message[offset+24] |= (char)0x8;
		} else { 
			message[offset+24] ^= (char)(0x8);
		}

	}

	public void setSetMeterReading(boolean setMeterReading) {
		this.setMeterReading = setMeterReading;
		if ( setMeterReading ) {
			message[offset+24] |= (char)0x10;
		} else { 
			message[offset+24] ^= (char)(0x10);
		}

	}

	public void setSetRealTime(boolean setRealTime) {
		this.setRealTime = setRealTime;
	}

	public void setSetVoltageQualityLevels(boolean setVoltageQualityLevels) {
		this.setVoltageQualityLevels = setVoltageQualityLevels;
		if ( setVoltageQualityLevels ) {
			message[offset+24] |= (char)0x20;
		} else { 
			message[offset+24] ^= (char)(0x20);
		}

	}

	public void setStaticSetup(boolean staticSetup) {
		if ( staticSetup ) {
			message[offset+24] |= (char)0x2;
		} else { 
			message[offset+24] ^= (char)(0x2);
		}
		this.staticSetup = staticSetup;
	}
}
