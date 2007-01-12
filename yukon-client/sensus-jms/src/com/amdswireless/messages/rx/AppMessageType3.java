package com.amdswireless.messages.rx;

import java.util.ArrayList;

/* This is a parser for type three messages
 * The bit field looks like this:
 * Byte		Data
 *  0		Relative Timestamp
 *  1-3		Delta Data Type/Current Reading
 *  		(DDT is bits 0-2 of byte 1)
 *  		(Compression flag is bit 3 of byte 1)
 *  		(Current reading is bits 4-7 of byte 1, all of bytes 2 & 3)
 *  4-6		Average (bits 0-5)
 *  		Maximum (bits 6-11)
 *  		Minimum (bits 12-18) meter voltage
 *  6-27	MS 6 bits of byte 6, plus all other bytes are historical reads
 *  
*/

public class AppMessageType3 extends DataMessage implements AppMessage, java.io.Serializable {
	private transient static final long serialVersionUID = 1L;
	private transient final int[] timestampScale = {2,4,15,120,180,360};
	private transient final int[] bitsPerBin = {5,7,9,11,12,13};
	private transient final int[] binsPerMsg = {34,24,19,15,14,13};
	private final String msgClass = "READ";
	
	private java.util.Date readTime;
	private int readOffset;
	private int currentReading;
	private int ddt=2; //defaults to DDT of 2 (RTS=15 seconds)
	private boolean compressed;
	private int maxVoltage, minVoltage, avgVoltage;
	private transient java.util.BitSet bs;
	private int[] bins;
	
	public AppMessageType3( char[] msg ) {
		super(msg);
		super.setMessageType(3);
		int offset=31;
		this.readOffset = (int)msg[0+offset];
		this.readTime = new java.util.Date((this.getToi()-this.readOffset)*1000);
		this.ddt = (int)(msg[1]+offset & 0x03);
		this.currentReading=(int)(msg[1+offset]>>>4)+(int)(msg[2+offset]<<4)+(int)(msg[3+offset]<<12);
		this.avgVoltage=(int)( ( (msg[5+offset])>>>4) + ((msg[6+offset]&0x3)<<4) ) * 2+166;
		this.minVoltage=(int)(msg[4+offset]&0x3F) *2+166;
		this.maxVoltage=(int)( ((msg[4+offset]>>>6) + (msg[5+offset]&0xF)<<2) * 2+166);
		bs = new java.util.BitSet(250);
		//filling the first 6 bits is a little tricky.
		int cnt=0;
		for ( int i=2; i<8; i++ ) {
			if ( (((int)(msg[6+offset]>>>i))&0x1) == 0x1 ) { 
				bs.set(cnt,true);
			} else { 
				bs.set(cnt,false);
			}
			cnt++;
		}
		// now we just step through the remaining bytes in the message and convert 
		// to bits.
		for (int j=(7+offset); j<msg.length; j++ ) {
			for ( int i=0; i<8; i++ ) {
				if ( (((int)(msg[j]>>>i))&0x1) == 0x1 ) { 
					bs.set(cnt,true);
				} else { 
					bs.set(cnt,false);
				}
				cnt++;
			}
		}
		int binLength=bitsPerBin[ddt];
		bins = new int[binsPerMsg[ddt]];
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

	/**
	 * @return Returns the Minimum Voltage.
	 */
	public int getMinVoltage() {
		return this.minVoltage;
	}
	
	/**
	 * @return Returns the Maximum Voltage.
	 */
	public int getMaxVoltage() {
		return this.maxVoltage;
	}
	
	/**
	 * @return Returns the Average Voltage.
	 */
	public int getAvgVoltage() {
		return this.avgVoltage;
	}
	
	/**
	 * @return Returns the Timestamp Scale.
	 */
	public int getTimeScale() {
		return timestampScale[this.ddt];
	}
	
	/**
	 * @return Returns the read offset.
	 */
	public int getReadOffset() {
		return this.readOffset;
	}
	
	/**
	 * @return Returns the Current Reading of the meter.
	 */
	public int getCurrentReading() {
		return this.currentReading;
	}
	
	/**
	 * @return Returns a string representation of the bitset used to calculate the
	 * bin values in this message.  If you find yourself using this, you are thinking
	 * way too hard.
	 */
	public String getBits() {
		return bs.toString();
	}
	
	/**
	 * @return Returns the historical reads for this meter in temporal order, most
	 * recent read first.
	 */
	public ArrayList getBins() {
		ArrayList<Integer> binArr = new ArrayList<Integer>();
		for (int i=0; i<binsPerMsg[ddt]; i++ ) {
			binArr.add(new Integer(bins[i]));
		}
		return binArr;
	}
	
	/**
	 * @return Returns some diagnostic info about this message.
	 */
	public String getInfo() {
		return super.getRepId()+":  "+currentReading+"kWh@ seq#"+super.getAppSeq()+"\t"+
			minVoltage+"Vmin/"+maxVoltage+"Vmax/"+avgVoltage+"Vavg ";
	}

	/**
	 * @return Returns the compression flag.
	 */
	public boolean isCompressed() {
		return compressed;
	}

	/**
	 * @return Returns the Delta Data Timestamp for this message.
	 */
	public int getDdt() {
		return ddt;
	}

	/**
	 * @return Returns the actual read time for this read..
	 */
	public java.util.Date getReadTime() {
		return readTime;
	}
	
	public String getMsgClass() {
		return msgClass;
	}
}
