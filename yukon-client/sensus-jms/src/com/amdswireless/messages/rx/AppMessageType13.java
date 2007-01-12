package com.amdswireless.messages.rx;

import java.util.ArrayList;

/* This is a parser for type ten messages
 * The bit field looks like this:
 * Byte		Data
 *  0		Relative Timestamp
 *  1		Meter Scale
 *  2-5		Delta Data Type/Current Reading
 *  		(DDT is bits 0-2 of byte 1)
 *  		(Compression flag is bit 3 of byte 1)
 *  		(Current reading is bits 4-7 of byte 1, all of bytes 2 & 3)
 *  6		Battery Voltage
 *  7-27	MS 6 bits of byte 6, plus all other bytes are historical reads
 *  
*/

/**
 * @author johng
 *
 */
public class AppMessageType13 extends DataMessage implements AppMessage, java.io.Serializable {
	private transient static final long serialVersionUID = 1L;
	private transient final int[] timestampScale = {2,4,15,120,180,360};
	private transient final int[] bitsPerBin = {2,2,3,6,7,8};
	private transient final int[] binsPerMsg = {84,84,56,28,24,21};
	private transient final String[] unitText = {"Gallons","Liters","Cubic Feet","Cubic Meters"};
	private transient final float[] resolutionScale = {0.1f, 1, 10f, 100f, 1000f, 0.01f};
	private transient final float[] historyScaleArr = {1.0f, 0.1f, 0.01f, 0.001f};
	private final String msgClass = "READ";
	
	private java.util.Date readTime;
	private int relativeOffset;
	private int currentReading;
	private int ddt=2; //defaults to DDT of 2 (RTS=15 seconds)
	private int meterUnits;
	private int currentReadingResolution;
	private int historyScale;
    private float batteryVoltage;
	private boolean compressionEnabled;
	private java.util.ArrayList readings;
	private transient java.util.BitSet bs;
	private int[] bins = new int[84];
	
	public AppMessageType13( char[] msg ) {
		super(msg);
		super.setMessageType(0xA);
		// the original class was written just to parse the appData, but now
		// we pass it the entire andorian message, so we need to offset 
		// where we start decoding
		int offset=31;
		this.relativeOffset = (int)msg[0+offset];
		this.meterUnits = msg[2+offset]&0x3;
		this.currentReadingResolution=(msg[2+offset]&0xC)>>>2;
		this.historyScale=(msg[2+offset]&0x30)>>>4;
		this.ddt = (int)(msg[2+offset] & 0x7);
		this.compressionEnabled=( (msg[2+offset]&0x8)==0x8 );
		this.currentReading=(int)(msg[1+offset]>>>4)+(int)(msg[2+offset]<<4)+(int)(msg[3+offset]<<12)+(int)(msg[4+offset]);
                if ( (int)(msg[6+offset] & 80 ) == 0x80 ) {
                    this.batteryVoltage= (int)(msg[6+offset] & 0x7F ) /16 + 4;
                } else {
                    this.batteryVoltage= (int)(msg[6+offset] & 0x7F ) /62 + 2;
                }
		bs = new java.util.BitSet(250);
		int cnt=0;
		// now we just step through the remaining bytes in the message and convert 
		// to bits.
		for (int j=7+offset; j<msg.length; j++ ) {
			for ( int i=0; i<8; i++ ) {
				if ( (((int)(msg[j]>>>i))&0x1) == 0x1 ) { 
					bs.set(cnt,true);
				} else { 
					bs.set(cnt,false);
				}
				cnt++;
			}
		}
        if ( ddt > 6 ) return;
		int binLength=bitsPerBin[ddt];
		for ( int j=0; j<binsPerMsg[ddt]; j++ ) {
			int binVal=0;
			for (int i=0; i<binLength; i++ ) {
				if ( bs.get(j*binLength+i) ) {
					binVal=binVal+((int)Math.pow(2,i));
				}
			}
			bins[j]=binVal;
		}
	}

	public String getBits() {
		return bs.toString();
	}
	
	/**
	 * @return Returns the list of reads in temporal order
	 */
	public ArrayList getBins() {
		ArrayList<Integer> binArr = new ArrayList<Integer>();
		for (int i=0; i<binsPerMsg[ddt]; i++ ) {
			binArr.add(new Integer(bins[i]));
		}
		return binArr;
	}
	public String getInfo() {
		return super.getRepId()+":  "+currentReading+"kWh@ seq#"+super.getAppSeq();
			//minVoltage+"Vmin/"+maxVoltage+"Vmax/"+avgVoltage+"Vavg ";
	}

	/**
	 * @return Returns the binsPerMsg.
	 */
	public int[] getBinsPerMsg() {
		return binsPerMsg;
	}

	/**
	 * @return Returns the bitsPerBin.
	 */
	public int[] getBitsPerBin() {
		return bitsPerBin;
	}

	/**
	 * @return Returns the bs.
	 */
	public java.util.BitSet getBs() {
		return bs;
	}

	/**
	 * @return Returns the compressionEnabled.
	 */
	public boolean isCompressionEnabled() {
		return compressionEnabled;
	}

	/**
	 * @return Returns the currentReading.
	 */
	public int getCurrentReading() {
		return currentReading;
	}

	/**
	 * @return Returns the currentReadingResolution.
	 */
	public int getCurrentReadingResolution() {
		return currentReadingResolution;
	}

	/**
	 * @return Returns the ddt.
	 */
	public int getDdt() {
		return ddt;
	}

	/**
	 * @return Returns the historyScale.
	 */
	public int getHistoryScale() {
		return historyScale;
	}

	/**
	 * @return Returns the historyScaleArr.
	 */
	public float[] getHistoryScaleArr() {
		return historyScaleArr;
	}

	/**
	 * @return Returns the meterUnits.
	 */
	public int getMeterUnits() {
		return meterUnits;
	}

	/**
	 * @return Returns the readings.
	 */
	public java.util.ArrayList getReadings() {
		return readings;
	}

	/**
	 * @return Returns the readTime.
	 */
	public java.util.Date getReadTime() {
		return readTime;
	}

	/**
	 * @return Returns the relativeOffset.
	 */
	public int getRelativeOffset() {
		return relativeOffset;
	}

	/**
	 * @return Returns the resolutionScale.
	 */
	public float[] getResolutionScale() {
		return resolutionScale;
	}

	/**
	 * @return Returns the timestampScale.
	 */
	public int[] getTimestampScale() {
		return timestampScale;
	}

	/**
	 * @return Returns the unitText.
	 */
	public String[] getUnitText() {
		return unitText;
	}
	public String getMsgClass() {
		return this.msgClass;
	}

	public float getBatteryVoltage() {
		return batteryVoltage;
	}

	public void setBatteryVoltage(float batteryVoltage) {
		this.batteryVoltage = batteryVoltage;
	}
}
