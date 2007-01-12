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
 *  		(Forward reading is bits 4-7 of byte 2, all of bytes 3 & 4)
 *  5-7		Average (bits 0-5)
 *  		Maximum (bits 6-11)
 *  		Minimum (bits 12-18) meter voltage
 *  8-10	Reverse reading
 *  11-20	Forward historical reads
 *  21-27	Reverse historical reads
 *  
 */

public class AppMessageType15 extends ReadMessage implements AppMessage,
       java.io.Serializable {

           private transient static final long serialVersionUID = 1L;
           private transient final int[] timestampScale = { 5, 15, 60, 360, 720, 1440 };
           private transient final int[] bitsPerBin = { 5, 7, 9, 11, 12, 13 };
           private transient final int[] binsPerMsg = { 32, 22, 17, 14, 13, 12 };
           private final String msgClass = "READ";
           private java.util.Date readTime;
           private int relativeTimestamp;
           private int forwardReading;
           private int reverseReading;
           private int ddt = 2; // defaults to DDT of 2 (RTS=15 seconds)
           private boolean compression;
           private int maxVoltage, minVoltage, avgVoltage, clickCount;
           private transient java.util.BitSet fbs;
           private transient java.util.BitSet rbs;
           private ArrayList<IntervalReading> fwdBins;
		   private ArrayList<IntervalReading> revBins;

           public AppMessageType15(char[] msg) {
               super(msg);
               super.setMessageType(0x15);
               int offset = 31;
               this.relativeTimestamp = (int) msg[0 + offset]
                   + ((int) msg[1 + offset] << 8);
               this.readTime = new Date(
                       (this.getToi() - this.relativeTimestamp) * 1000);
               
               //Change "this.ddt = (int) (msg[2 + offset] & 0x03);" to "&0x7" since DDT is bits 0-2 of byte 2
               this.ddt = (int) (msg[2 + offset] & 0x07);
               this.compression = ((msg[2 + offset] & 0x08) == 0x08) ? true : false;
               this.forwardReading = (int) (msg[2 + offset] >>> 4)
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
               this.maxVoltage = (int) ((msg[5 + offset] >>> 6) + (msg[6 + offset] & 0xF) << 2) * 2 + voltageOffset;
               this.clickCount = (int) ((msg[7 + offset] >>> 2));
               this.reverseReading = (int) (msg[8 + offset])
                   + (int) (msg[9 + offset] << 8) + (int) (msg[10 + offset] << 16);

			   
               fbs = new java.util.BitSet(250);
               rbs = new java.util.BitSet(250);
               // now we just step through the remaining bytes in the message and
               // convert to two bitsets.
               int cnt = 0;
               for (int j = (11 + offset); j < (21 + offset); j++) {
                   for (int i = 0; i < 8; i++) {
                       if ((((int) (msg[j] >>> i)) & 0x1) == 0x1) {
                           fbs.set(cnt, true);
                       } else {
                           fbs.set(cnt, false);
                       }
                       cnt++;
                   }
               }
               cnt = 0;
               for (int j = (21 + offset); j < msg.length; j++) {
                   for (int i = 0; i < 8; i++) {
                       if ((((int) (msg[j] >>> i)) & 0x1) == 0x1) {
                           rbs.set(cnt, true);
                       } else {
                           rbs.set(cnt, false);
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
                   int fbins[] = new int[binsPerMsg[ddt]];
                   for (int j = 0; j < binsPerMsg[ddt]; j++) {
                       int binVal = 0;
                       for (int i = 0; i < binLength; i++) {
                           if (fbs.get(j * binLength + i)) {
                               binVal = binVal + ((int) Math.pow(2, i));
                           }
                       }
                       fbins[j] = binVal;
                   }
                   int rbins[] = new int[binsPerMsg[ddt]];
                   for (int j = 0; j < binsPerMsg[ddt]; j++) {
                       int binVal = 0;
                       for (int i = 0; i < binLength; i++) {
                           if (rbs.get(j * binLength + i)) {
                               binVal = binVal + ((int) Math.pow(2, i));
                           }
                       }
                       rbins[j] = binVal;
                   }

				   fwdBins = super.getIntervalTimestamps(fbins, this.getToi(), this.relativeTimestamp, ddt);
				   revBins = super.getIntervalTimestamps(rbins, this.getToi(), this.relativeTimestamp, ddt);
				   
               } else { // this is a compressed message, decode it.
				   fwdBins = super.getIntervalTimestamps(super.decompress(fbs), this.getToi(), this.relativeTimestamp, ddt);
				   revBins = super.getIntervalTimestamps(super.decompress(rbs), this.getToi(), this.relativeTimestamp, ddt);
               }
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

           public int getReadOffset() {
               return this.relativeTimestamp;
           }

           public int getRelativeTimestamp() {
               return this.relativeTimestamp;
           }

           public int getForwardReading() {
               return this.forwardReading;
           }

           public int getReverseReading() {
               return this.reverseReading;
           }

           public String getForwardBits() {
               return fbs.toString();
           }

           public String getReverseBits() {
               return rbs.toString();
           }

           public ArrayList getForwardBins() {
               return fwdBins;
           }

           public ArrayList getReverseBins() {
               return revBins;
           }

           public boolean isCompression() {
               return this.compression;
           }

           public String getInfo() {
               return super.getRepId() + ":  " + forwardReading + "kWh / " + reverseReading + "kWh@ seq#"
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
           public String getMsgClass() {
               return this.msgClass;
           }
}
