package com.cannontech.sensus;

import java.nio.ByteBuffer;
import java.util.Date;

public class GPUFFProtocol {

	private final int FLAG_OFFSET = 2;
	private final int CID_OFFSET = 4;		// Customer Id
	private final int SEQUENCE_OFFSET = 6;
	private final int DEVTYPE_OFFSET = 8;
	private final int DEVREV_OFFSET = 10;
	private final int SERIAL_OFFSET = 11;

	private final int NAME_SIZE = 128;
	private final float DEFAULT_SCALING = 1000.0f;
	private final float GEO_SCALING = 10000.0f;

	static final short DEVICE_TYPE_FCI = 0x0001;
	static final short DEVICE_TYPE_CBNM = 0x0002;
	static final short DEVICE_TYPE_SENSUSFCI = 0x0003;
	
	private short len = 0;
	private short flag_len = 0;
	private short cid = 0;
	private short sequence = 0;
	private short deviceType = 0;
	private byte deviceRevision = 0;
	private int serialNumber = 0;
	private short crc = 0;
	private ByteBuffer bBuf = ByteBuffer.allocate(256);

	// May need a list of more than one buffer to represent the given inbound
	
	// Parameters for the Commissioning Message
	private boolean cmSet = false;
	private byte cmOpValues = 0;
	private float cmLatitude = 0.0f;
	private float cmLongitude = 0.0f;
	private String cmName;
	private short cmAmpT = 0; // Amperage Threshold valid only for neutral current sensor device type.
	
	// Parameters for FCI Device Values Messages.
	// The Sensus FCI has only battery, time, temp, and status.
	private boolean dvSet = false;
	private byte dvFlags = 0;
	private byte dvStatus = 0;
	private Date dvTime = new Date();
	private float dvBattery = 0.0f;
	private short dvTemp = 0; 

	{
		bBuf.put(len++, (byte) 0xa5); // header
		bBuf.put(len++, (byte) 0x96); // header
		bBuf.put(len++, (byte) 0x00); // 2 byte flags/len
		bBuf.put(len++, (byte) 0x00);
		bBuf.put(len++, (byte) 0x00); // 2 byte CID
		bBuf.put(len++, (byte) 0x00);
		bBuf.put(len++, (byte) 0x00); // 2 byte SEQ
		bBuf.put(len++, (byte) 0x00);
		bBuf.put(len++, (byte) 0x00); // 2 byte device type
		bBuf.put(len++, (byte) 0x00);
		bBuf.put(len++, (byte) 0x00); // Device Revision
		bBuf.put(len++, (byte) 0x00); // 4 byte serial number
		bBuf.put(len++, (byte) 0x00);
		bBuf.put(len++, (byte) 0x00);
		bBuf.put(len++, (byte) 0x00);
	}
	
	public void buildDeviceCommissioningInfo() {
		String name = getSerialNumber() + "." + getCmName() + ".SensusFlexNet";

		// TODO: Make this right.
		put((byte) 0x00); 	// This is the START of the GPUFF - FPLD block.  Device Config is a 0x00
		put((byte) 0x00);	// FLAGS
		put((int)(getCmLatitude() * GEO_SCALING));
		put((int)(getCmLongitude()* GEO_SCALING));

		for ( int i=0; i<NAME_SIZE; i++ ) {
        	if(i < name.length()) {
                put((byte)name.charAt(i));
        	} else {
                put((byte)0x00);
        	}
        }
		
		return;
	}

	public void buildDeviceValuesMessage(boolean fault, boolean event, boolean noAC) {
		// 13 bytes are pre-populated.
		put((byte) 0x01); 	// This is the START of the GPUFF - FPLD block.  Device Values is a 0x01
		put((byte) 0x38);	// FLAGS: Time & Voltage included.
		put((byte)((fault ? 0x80 : 0x00) | (event ? 0x40 : 0x00) | (noAC? 0x20 : 0x00)));
		put((int)(dvTime.getTime()/1000));
		put((short)(getDvBattery() * DEFAULT_SCALING));
		put(getDvTemp());
		
		return;
	}

	public byte[] getBytes() {
        byte[] retBytes = new byte[len];
        
        int fl = getFlag_len();
        fl = (fl & 0x0000fc00) | ((len-4) & 0x000003ff);
		bBuf.put(FLAG_OFFSET,   (byte)((0x0000ff00 & fl) >> 8 ));
		bBuf.put(FLAG_OFFSET+1, (byte) (0x000000ff & fl));
        
        for ( int i=0; i<len; i++ ) {
            retBytes[i]=(byte)bBuf.get(i);
        }
		
		return retBytes;
	}

	public short getCrc() {
		return crc;
	}

	public void setCrc(short crc) {
		this.crc = crc;
	}

	public byte getDeviceRevision() {
		return deviceRevision;
	}

	public void setDeviceRevision(byte deviceRevision) {
		this.deviceRevision = deviceRevision;
		bBuf.put(DEVREV_OFFSET,   (byte) (0x000000ff & deviceRevision));
	}

	public short getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(short deviceType) {
		this.deviceType = deviceType;
		bBuf.put(DEVTYPE_OFFSET,   (byte)((0x0000ff00 & deviceType) >> 8 ));
		bBuf.put(DEVTYPE_OFFSET+1, (byte) (0x000000ff & deviceType));
	}

	public short getFlag_len() {
		return flag_len;
	}

	public void setFlag_len(short flag_len) {
		this.flag_len = flag_len;
		bBuf.put(FLAG_OFFSET,   (byte)((0x0000ff00 & flag_len) >> 8 ));
		bBuf.put(FLAG_OFFSET+1, (byte) (0x000000ff & flag_len));
	}

	public short getSequence() {
		return sequence;
	}

	public void setSequence(short sequence) {
		this.sequence = sequence;
		bBuf.put(SEQUENCE_OFFSET,   (byte)((0x0000ff00 & sequence) >> 8 ));
		bBuf.put(SEQUENCE_OFFSET+1, (byte) (0x000000ff & sequence));
	}

	public int getSerialNumer() {
		return serialNumber;
	}

	public void setSerialNumber(int serialNum) {
		this.serialNumber = serialNum;
		bBuf.put(SERIAL_OFFSET,   (byte)((0xff000000 & serialNum) >> 24));		
		bBuf.put(SERIAL_OFFSET+1, (byte)((0x00ff0000 & serialNum) >> 16));
		bBuf.put(SERIAL_OFFSET+2, (byte)((0x0000ff00 & serialNum) >> 8 ));
		bBuf.put(SERIAL_OFFSET+3, (byte) (0x000000ff & serialNum));
	}

	private void put(long val) {
		bBuf.put(len++, (byte)((0xff000000 & val) >> 24));		
		bBuf.put(len++, (byte)((0x00ff0000 & val) >> 16));
		bBuf.put(len++, (byte)((0x0000ff00 & val) >> 8 ));
		bBuf.put(len++, (byte) (0x000000ff & val));
		return;
	}	
	private void put(int val) {
		bBuf.put(len++, (byte)((0xff000000 & val) >> 24));		
		bBuf.put(len++, (byte)((0x00ff0000 & val) >> 16));
		bBuf.put(len++, (byte)((0x0000ff00 & val) >> 8 ));
		bBuf.put(len++, (byte) (0x000000ff & val));
		return;
	}	
	@SuppressWarnings("unused")
	private void put(short val) {
		bBuf.put(len++, (byte)((0x0000ff00 & val) >> 8 ));		
		bBuf.put(len++, (byte) (0x000000ff & val));
		return;
	}
	private void put(byte val) {
		bBuf.put(len++, (byte) (0x000000ff & val));
		return;
	}

	public short getCmAmpT() {
		return cmAmpT;
	}

	public void setCmAmpT(short cmAmpT) {
		this.cmAmpT = cmAmpT;
	}

	public float getCmLatitude() {
		return cmLatitude;
	}

	public void setCmLatitude(float cmLatitude) {
		this.cmLatitude = cmLatitude;
	}

	public float getCmLongitude() {
		return cmLongitude;
	}

	public void setCmLongitude(float cmLongitude) {
		this.cmLongitude = cmLongitude;
	}

	public String getCmName() {
		return cmName;
	}

	public void setCmName(String cmName) {
		this.cmName = cmName;
	}

	public byte getCmOpValues() {
		return cmOpValues;
	}

	public void setCmOpValues(byte cmOpValues) {
		this.cmOpValues = cmOpValues;
	}

	public boolean isCmSet() {
		return cmSet;
	}

	public void setCmSet(boolean cmSet) {
		this.cmSet = cmSet;
	}

	public float getDvBattery() {
		return dvBattery;
	}

	public void setDvBattery(float dvBattery) {
		this.dvBattery = dvBattery;
	}

	public byte getDvFlags() {
		return dvFlags;
	}

	public void setDvFlags(byte dvFlags) {
		this.dvFlags = dvFlags;
	}

	public boolean isDvSet() {
		return dvSet;
	}

	public void setDvSet(boolean dvSet) {
		this.dvSet = dvSet;
	}

	public byte getDvStatus() {
		return dvStatus;
	}

	public void setDvStatus(byte dvStatus) {
		this.dvStatus = dvStatus;
	}

	public Date getDvTime() {
		return dvTime;
	}

	public void setDvTime(Date dvTime) {
		this.dvTime = dvTime;
	}

	public int getSerialNumber() {
		return serialNumber;
	}

	public short getCid() {
		return cid;
	}

	public void setCid(short cid) {
		this.cid = cid;
		bBuf.put(CID_OFFSET,   (byte)((0x0000ff00 & cid) >> 8 ));
		bBuf.put(CID_OFFSET+1, (byte) (0x000000ff & cid));
	}

	short getDvTemp() {
		return dvTemp;
	}

	void setDvTemp(short dvTemp) {
		this.dvTemp = dvTemp;
	}
}
