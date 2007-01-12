package com.amdswireless.messages.rx;

import java.util.ArrayList;
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

public class AppMessageTypeD extends DataMessage implements AppMessage, java.io.Serializable {
	private transient static final long serialVersionUID = 1L;
	private transient final int[] timestampScale = {2,4,15,120,180,360};
    private transient final int[] bitsPerBin = {5,7,9,11,12,13};
    private transient final int[] binsPerMsg = {27,19,15,12,11,10};
    private final String msgClass = "READ";
    
    private java.util.Date readTime;
    private int relativeTimestamp;
    private int currentReading;
    private int ddt=2; //defaults to DDT of 2 (RTS=15 seconds)
    private boolean compression;
    private float peakDemandReading;
    private int phaseAVoltage, phaseBVoltage, phaseCVoltage;
    private transient java.util.BitSet bs;
    private transient ArrayList<Integer> binBuffer = new ArrayList<Integer>();
    private int[] bins;

    public AppMessageTypeD( char[] msg ) {
        super(msg);
        super.setMessageType(0xD);
        int offset=31;
        relativeTimestamp = (int)msg[0+offset]+((int)msg[1+offset]<<8) * 2;
        this.readTime = new Date(
				(this.getToi() - this.relativeTimestamp) * 1000);
        this.ddt = (int)(msg[2+offset] & 0x07);
        this.compression = ( ( msg[2+offset] & 0x08  ) == 0x08 ) ? true : false;
        this.currentReading=(int)(msg[2+offset]>>>4)+(int)(msg[3+offset]<<4)+(int)(msg[4+offset]<<12);
        this.peakDemandReading=Float.intBitsToFloat((int)(msg[5+offset])+(int)(msg[6+offset]<<8)+(int)(msg[7+offset]<<16)+(int)(msg[8+offset]<<24))/1000f;
        this.phaseAVoltage=(int)((msg[9+offset])*2);
        this.phaseBVoltage=(int)((msg[10+offset])*2);
        this.phaseCVoltage=(int)((msg[11+offset])*2);
        bs = new java.util.BitSet(250);
        // now we just step through the remaining bytes in the message and convert 
        // to bits.
        int cnt=0;
        for (int j=(12+offset); j<msg.length; j++ ) {
            for ( int i=0; i<8; i++ ) {
                if ( (((int)(msg[j]>>>i))&0x1) == 0x1 ) { 
                    bs.set(cnt,true);
                } else { 
                    bs.set(cnt,false);
                }
                cnt++;
            }
        }
        // choice time.  if the compression bit is set, we need to decode the bins using the
        // Huffman Binary Tree algorithm described in the spec.  If not, we can just step throug
        // in known step sizes and extract the bins.
        if ( compression == false ) {
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
        } else { // this is a compressed message, decode it.
            int i = 0;
            boolean cont = true;
            while ( cont == true && i < 160 ) {
                int ones = 0;
                while ( bs.get(i) ) { // while the next bit is 1 (true)
                    i++;          //increment the overall counter
                    ones++;       // and the local count of the number of conseq 1's
                }
                i++; // skip the zero that kicked us out of the while loop
                // if the number of consecutive 1's is 0-5, then that is the value
                // of this bin
                // remember binBuffer is just an ArrayList
                if ( ones < 6 ) {
                    binBuffer.add(new Integer(ones));
                    // ok, special case #1, values 6-37
                } else if ( ones == 6 ) {
                    int reading = 0;
                    for ( int j=0; j<5; j++ ) { // read the next 5 bits
                        reading += ((bs.get(i) ? 1 : 0 )<<j);
                        i++;
                    }
                    binBuffer.add(new Integer(reading+6));
                    // special case #2, values greater than 37
                } else if ( ones == 7 ) {
                    int reading = 0;
                    for ( int j=0; j<13; j++ ) { // read the next 5 bits
                        reading += ((bs.get(i) ? 1 : 0 )<<j);
                        i++;
                    }
                    binBuffer.add(new Integer(reading+38));
                    // finally, that magic case where we're at the end of our rope
                    // and we get more than 7 1's in a row...
                } else if ( ones >= 8 ) {
                    cont = false;
                }
            }
            bins = new int[binBuffer.size()];
            for ( i=0; i<binBuffer.size(); i++ ) {
                bins[i] = ((Integer)(binBuffer.get(i))).intValue();
            }
        }
    }

    public int getPhaseAVoltage() {
        return this.phaseAVoltage;
    }
    public int getPhaseBVoltage() {
        return this.phaseBVoltage;
    }
    public int getPhaseCVoltage() {
        return this.phaseCVoltage;
    }
    public int getTimeScale() {
        return timestampScale[this.ddt];
    }
    public int getReadOffset() {
        return this.relativeTimestamp;
    }
    public int getRelativeTimestamp() {
        return this.relativeTimestamp;
    }
    public int getCurrentReading() {
        return this.currentReading;
    }
    public float getPeakDemandReading() {
        return this.peakDemandReading;
    }
    public String getBits() {
        return bs.toString();
    }
    public ArrayList getBins() {
        ArrayList<Integer> binArr = new ArrayList<Integer>();
        for (int i=0; i<binsPerMsg[ddt]; i++ ) {
            binArr.add(new Integer(bins[i]));
        }
        return binArr;
    }
    public boolean isCompression() {
        return this.compression;
    }
    public String getInfo() {
        return super.getRepId()+":  "+currentReading+"kWh@ seq#"+super.getAppSeq()+"\t"+
            phaseAVoltage+"Phase A Voltage/"+phaseBVoltage+"Phase B Voltage/"+phaseCVoltage+"Phase C Voltage ";
    }
	/**
	 * @return The wallclock time of the last read.
	 */
	public Date getReadTime() {
		return this.readTime;
	}
	public String getMsgClass() {
		return this.msgClass;
	}
}
