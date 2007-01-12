package com.amdswireless.messages.rx;
/**
 The Type C message is a READ message that 
 * @author johng
 *
 */

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;

/* This is a parser for type three messages
 * The bit field looks like this:
 * Byte		Data
 *  0-1		Relative Timestamp
 *  2-4		Delta Data Type/Current Reading
 *  		(DDT is bits 0-2 of byte 2)
 *  		(Compression flag is bit 3 of byte 2)
 *  		(Current reading is bits 4-7 of byte 2, all of bytes 3 & 4)
 *  5-7		Average (bits 0-5)
 *  		Maximum (bits 6-11)
 *  		Minimum (bits 12-18) meter voltage
 *  8-27	historical reads
 *  
 */


public class AppMessageTypeC extends ReadMessage implements AppMessage,
		java.io.Serializable {

	private transient static final long serialVersionUID = 1L;
	private transient final int[] timestampScale = { 2, 4, 15, 120, 180, 360 };
	private transient final int[] bitsPerBin = { 5, 7, 9, 11, 12, 13 };
	private transient final int[] binsPerMsg = { 32, 22, 17, 14, 13, 12 };
        private final String msgClass = "READ";
	private transient final int offset=31;
	private int ddt = 2; // defaults to DDT of 2 (RTS=15 seconds)
	private boolean compression;
	private int maxVoltage, minVoltage, avgVoltage, clickCount;
	private transient java.util.BitSet bs;
	private int[] bins;

	public AppMessageTypeC() {
		super();
		super.setAppCode(0xC);
		super.setMessageType(0xC);
	}

	public AppMessageTypeC(char[] msg) {
		super(msg);
		super.setMessageType(0xC);
		this.relativeTimestamp = (int) msg[0 + offset]
				+ ((int) msg[1 + offset] << 8);
		this.readTime = new Date(
				(this.getToi() - this.relativeTimestamp) * 1000);
		this.ddt = (int) (msg[2 + offset] & 0x03);
		this.compression = ((msg[2 + offset] & 0x08) == 0x08) ? true : false;
		this.currentReading = (int) (msg[2 + offset] >>> 4)
				+ (int) (msg[3 + offset] << 4) + (int) (msg[4 + offset] << 12);
		// this is a hack for the voltage range. If the lowBattery bit
		// is set (refer to the DataMessage header), then the range on the
		// voltage is
		// 40-166V, otherwise it's 166-292V. Pray we need no more voltage ranges
		int voltageOffset = 166;
		if (this.isLowBattery()) {
			voltageOffset = 40;
		}
		this.avgVoltage = (int) (((msg[6 + offset]) >>> 4) + ((msg[7 + offset] & 0x3) << 4))
				* 2 + voltageOffset;
		this.minVoltage = (int) (msg[5 + offset] & 0x3F) * 2 + voltageOffset;
		this.maxVoltage = (int) ((msg[5 + offset] >>> 6)
				+ (msg[6 + offset] & 0xF) << 2)
				* 2 + voltageOffset;
		this.clickCount = (int) ((msg[7 + offset] >>> 2));
		bs = new java.util.BitSet(250);
		// now we just step through the remaining bytes in the message and
		// convert to bits.
		int cnt = 0;
		for (int j = (8 + offset); j < msg.length; j++) {
			for (int i = 0; i < 8; i++) {
				if ((((int) (msg[j] >>> i)) & 0x1) == 0x1) {
					bs.set(cnt, true);
				} else {
					bs.set(cnt, false);
				}
				cnt++;
			}
		}
		// choice time. if the compression bit is set, we need to decode the
		// bins using the
		// Huffman Binary Tree algorithm described in the spec. If not, we can
		// just step throug
		// in known step sizes and extract the bins.
		if (compression == false) {
			int binLength = bitsPerBin[ddt];
			bins = new int[binsPerMsg[ddt]];
			for (int j = 0; j < binsPerMsg[ddt]; j++) {
				int binVal = 0;
				for (int i = 0; i < binLength; i++) {
					if (bs.get(j * binLength + i)) {
						binVal = binVal + ((int) Math.pow(2, i));
					}
				}
				bins[j] = binVal;
			}
		} else { // this is a compressed message, decode it.
			bins = super.decompress(bs);
		}
	}
	
	
	private BitSet generateBitSet( int[] reads ) {
		BitSet bits = new BitSet(182);
		bits.set(0,160);
		int bit = 0;
		int read = 0;
		while (bit < 160 && read < 160) {
			int oneRead = reads[read];
			if ( oneRead < 6) {
				for ( int i=0; i<oneRead; i++ ) {
					bits.set(bit,true);
					bit++;
				}
				bits.set(bit,false);
				bit++;
			}
			if ( oneRead > 5 && oneRead < 38) {
				for ( int i=0; i<6; i++ ) {
					bits.set(bit,true);
					bit++;
				}
				bits.set(bit,false);
				bit++;
				int tempRead = oneRead-6;
				bits.set(bit,((tempRead & 0x10) == 0x10));
				bit++;
				bits.set(bit,((tempRead & 0x8) == 0x8));
				bit++;
				bits.set(bit,((tempRead & 0x4) == 0x4));
				bit++;
				bits.set(bit,((tempRead & 0x2) == 0x2));
				bit++;
				bits.set(bit,((tempRead & 0x1) == 0x1));
				bit++;
			}
			if ( oneRead > 38 && oneRead <8214 ) {
				for ( int i=0; i<7; i++ ) {
					bits.set(bit,true);
					bit++;
				}
				bits.set(bit,false);
				bit++;
				int tempRead = oneRead-38;
				for ( int i=12; i>=0; i-- ) {
					bits.set(bit, ((tempRead & ( 0x1 << i))  == (0x1 << i)) );
					bit++;
				}
			}
			read++;
		}
		return bits;
	}
	
	
	public int getMinVoltage() {
		return this.minVoltage;
	}

	public int getMaxVoltage() {
		return this.maxVoltage;
	}

	public int getAvgVoltage() {
		return this.avgVoltage;
	}

	public int getTimeScale() {
		return timestampScale[this.ddt];
	}


	public String getBits() {
		return bs.toString();
	}

	public ArrayList getBins() {
		ArrayList<Integer> binArr = new ArrayList<Integer>();
		for (int i = 0; i < binsPerMsg[ddt]; i++) {
			binArr.add(new Integer(bins[i]));
		}
		return binArr;
	}

	public boolean isCompression() {
		return this.compression;
	}

	public String getInfo() {
		return super.getRepId() + ":  " + currentReading + "kWh@ seq#"
				+ super.getAppSeq() + "\t" + minVoltage + "Vmin/" + maxVoltage
				+ "Vmax/" + avgVoltage + "Vavg ";
	}

	/**
	 * @return This is the number of times the AC voltage gaps of more than
	 *         MinimumClickCount duration have been detected before a power fail
	 *         event occurs. This is measured to count the number of transformer
	 *         breaker operations detected at the meter.
	 */
	public int getClickCount() {
		return this.clickCount;
	}

	/**
	 * @return The wallclock time of the last read.
	 */
	public Date getReadTime() {
		return this.readTime;
	}

	public int getDdt() {
		return ddt;
	}

	public void setDdt(int ddt) {
		this.ddt = ddt;
		message[2+offset] |= (char)(this.ddt & 0x7);
	}

	public int[] getTimestampScale() {
		return timestampScale;
	}

	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
		message[7+offset] |= (char)(this.clickCount<<2);
	}

	public void setCompression(boolean compression) {
		this.compression = compression;
		if ( this.compression ) {
			message[2+offset] |= (char)(0x8);
		}
	}

	public void setCurrentReading(int currentReading) {
		this.currentReading = currentReading;
		message[2+offset] |= (char)((this.currentReading & 0xf) << 4);
		message[3+offset] = (char)((this.currentReading & 0xFF0) >>> 4);
		message[4+offset] = (char)((this.currentReading & 0xFF000) >>> 12);
	}

	public void setMaxVoltage(int maxVoltage) {
		this.maxVoltage = maxVoltage;
		maxVoltage=(int)((maxVoltage-166)/2);
		message[5+offset] |= (char)((maxVoltage&0x3)<<6);
		message[6+offset] |= (char)((maxVoltage&0x3C)>>>2);
	}

	public void setMinVoltage(int minVoltage) {
		this.minVoltage = minVoltage;
		minVoltage=(int)((minVoltage-166)/2);
		message[5+offset] |= (char)(minVoltage & 0x3F);
	}

	public void setReadTime(java.util.Date readTime) {
		this.readTime = readTime;
	}

	public void setRelativeTimestamp(int relativeTimestamp) {
		this.relativeTimestamp = relativeTimestamp;
		message[1+offset] = (char)((this.relativeTimestamp & 0xFF00) >>> 8);
		message[0+offset] = (char)(this.relativeTimestamp & 0xFF);
	}

	public void setAvgVoltage(int avgVoltage) {
		this.avgVoltage = avgVoltage;
		avgVoltage=(int)((avgVoltage-166)/2);
		message[6+offset] |= (char)((avgVoltage&0xF)<<4);
		message[7+offset] |= (char)(avgVoltage>>>4);
	}

	public void setBins(int[] bins) {
		this.bins = bins;
		BitSet bs = generateBitSet(bins);
		int messageByte = 7;
		for (int bit=0; bit<160; bit++ ) {
			int bitOfByte = bit % 8;
			if ( bitOfByte == 0 ) {
				messageByte++;
			}
			if ( bs.get(bit) ) {
				message[messageByte+offset] |= (0x01 << bitOfByte);
			}
		}
	}
	
	public void screenDump() {
		super.screenDump();
        System.out.println("Relative Timestamp-----" + this.getRelativeTimestamp());
        System.out.println("Current Reading--------" + this.getCurrentReading());
        System.out.println("Delta Data Type--------" + this.getDdt());
        System.out.println("Compression------------" + this.isCompression());
        System.out.println("Max Voltage------------" + this.getMaxVoltage());
        System.out.println("Min Voltage------------" + this.getMinVoltage());
        System.out.println("Avg Voltage------------" + this.getAvgVoltage());
        System.out.println("Click Count------------" + this.getClickCount());
        System.out.println("Bin valus--------------");
        for ( int i=0; i<bins.length; i++ ) {
            System.out.print (this.bins[i]+", ");

        }		
        System.out.println();
	}

        public String getMsgClass() {
            return this.msgClass;
        }
}
