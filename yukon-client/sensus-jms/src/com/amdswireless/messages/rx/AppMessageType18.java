/**
 * Copyright (C) 2006 Sensus MS, all rights reserved.
 */

package com.amdswireless.messages.rx;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;

/**
 * @author Xuming.Chen
 * @since: 11/15/2006
 * 
 * @version: AppMessageType18.java v 1.0  11/20/2006   xumingc
 */

/* 
 * This is a parser for message type 18 (App Code 24)  
 * The bit field looks like this:
 *  Byte	 Field
 *  ------------------------------------------------
 *  0-1		 Relative Time Stamp	
 *  2-5		 Delta Data Type (bits 0-2 of byte 2)
 *  		 Compression Enabled (bit 3 of byte 2)
 *           Current Meter Reading (20 bits, bits 4-7 of byte 2 and all of bytes 3 and 4)
 *  6-8      Avg, Max, Min Meter Voltage and Click Count 
 *           (Byte 2, and bits 0-3 of byte 3)
 *  9-27     History Samples 
 *  ------------------------------------------------
 */

public class AppMessageType18 extends ReadMessage implements AppMessage, Serializable 
{
	/**
	 *
	 */
	private transient static final long serialVersionUID = -2877720172526362109L;	
	private transient final int[] timestampScale = { 5, 15, 60, 360, 720, 1440 };
    private transient final int[] bitsPerBin = { 10, 12, 14, 16, 17, 18 };
    private transient final int[] binsPerMsg = { 16, 13, 11, 10, 9, 8 };
    private transient static final int offset = 31;
    
    private static int voltageOffset;
    
	private int relativeTimestamp;
	private Date readTime;
	private int ddt = 2; // defaults to DDT of 2 (RTS = 15 seconds)
    private boolean compression;
	private int currentReading;
    private int avgVoltage, maxVoltage, minVoltage, clickCount;
    
    private transient BitSet bs;
    private int[] bins;
    
	/**
	 *  Default constructor for encoding.
	 */
	public AppMessageType18() {		
		super();
		super.setAppCode(0x18);
		super.setMessageType(0x18);	
	}
    
	/**
	 *  Default constructor for decoding. Extract information from Andorian message.
	 *  @param msg
	 */
	public AppMessageType18( char[] msg ) 
	{		
		super(msg);
		super.setMessageType(0x18);
		
		// Pass it with the entire Andorian message; offset the starting byte by "offset".		
		this.ddt = (int) (msg[2 + offset] & 0x07);
		// Continues to decode only if ddt <=5
		if (this.ddt <= 5)
		{
			this.relativeTimestamp = (int) msg[0 + offset] + ((int) msg[1 + offset] << 8);
			this.readTime = new Date( (this.getToi() - this.relativeTimestamp) * 1000 );
			
			this.compression = (msg[2 + offset] & 0x08) == 0x08;
			this.currentReading = (int) (msg[2 + offset] >>> 4) + (int) (msg[3 + offset] << 8) + (int) (msg[4 + offset] << 16);
        
			getVoltageOffset();
        
			this.minVoltage = (int) (msg[5 + offset] & 0x3F) * 2 + voltageOffset;
			this.maxVoltage = (int) ((msg[5 + offset] >>> 6) + (msg[6 + offset] & 0xF) << 2) * 2 + voltageOffset;       
			this.avgVoltage = (int) (((msg[6 + offset]) >>> 4) + ((msg[7 + offset] & 0x3) << 4)) * 2 + voltageOffset;            

			this.clickCount = (int) ((msg[7 + offset] >>> 2));
   	 
			bs = new BitSet();
			// Step through the remaining 19 bytes in the message and convert to bits.
			int cnt = 0;		
			for (int j= 9; j <= 27; j++) {
				for (int i = 0; i < 8; i++) {               
					bs.set(cnt, (((int) (msg[j + offset] >>> i)) & 0x1) == 0x1);                
					cnt++;
				}
			} 
    	
			// Choice time. if the compression bit is set, we need to decode the bins using the Huffman Binary Tree 
			//algorithm described in the spec. If not, we can just step through in known step sizes and extract the bins.
			if (this.compression == true)
			{ 
				// this is a compressed message, decode it.
				bins = super.decompress(bs);
			}
			else 
			{
				int binLength = bitsPerBin[ddt];
				bins = new int[binsPerMsg[ddt]];
				for (int j = 0; j < binsPerMsg[ddt]; j++) 
				{
					int binVal = 0;
					for (int i = 0; i < binLength; i++) 
					{
						if (bs.get(j * binLength + i)) 
						{
							binVal +=  Math.pow(2, i);
						}
					}
					bins[j] = binVal;
				}			   
			}   
		}
	}
	
	public void setReadTime(Date readTime) {
		this.readTime = readTime;
	}
	
	/**
	 * Set relativeTimestamp.
	 * @param relativeTimestamp
	 */
    public void setRelativeTimestamp(int relativeTimestamp) 
    {
        this.relativeTimestamp = relativeTimestamp;
        message[0 + offset] = (char) (relativeTimestamp & 0xFF);
        message[1 + offset] = (char) ((relativeTimestamp & 0xFF00) >>> 8);
    }
    
	/**
	 * Set ddt.
	 * @param ddt
	 */
    public void setDdt(int ddt) 
    {
        this.ddt = ddt;
        message[2 + offset] |= (char) (ddt & 0x07);   
    }

	/**
	 * Set compression.
	 * @param compression
	 */
    public void setCompression(boolean compression) 
    {
        this.compression = compression;        
        
        if ( compression ) {
			message[2 + offset] |= (char) (0x08);  
        }
    }
    
	/**
	 * Set currentReading.
	 * @param currentReading
	 */	   
    public void setCurrentReading(int currentReading) 
    {
    	this.currentReading = currentReading;
    	message[2 + offset] |= (char) ((currentReading & 0xF) << 4);  
    	message[3 + offset] = (char) ((currentReading & 0xFF0) >>> 4); 
    	message[4 + offset] = (char) ((currentReading & 0xFF000) >>> 12);
		  
    }	 
       
	/**
	 * Set minVoltage.
	 * @param minVoltage
	 */	   
    public void setMinVoltage(int minVoltage) 
    {
    	this.minVoltage = minVoltage;
    	minVoltage = (int) ( (minVoltage -voltageOffset)/2.0 );
    	
    	message[5 + offset] |= (char) (avgVoltage & 0x3F);
    }	
    
	/**
	 * Set maxVoltage.
	 * @param maxVoltage
	 */	   
    public void setMaxVoltage(int maxVoltage) 
    {
    	this.maxVoltage = maxVoltage;
    	maxVoltage = (int) ( (maxVoltage -voltageOffset)/2.0 );
    	
    	message[5 + offset] |= (char) ((maxVoltage & 0x3) << 6);
    	message[6 + offset] |= (char) ((maxVoltage & 0xF) >>> 2);
    }	
    
	/**
	 * Set clickCount.
	 * @param clickCount
	 */	   
    public void setClickCount(int clickCount) 
    {
    	this.clickCount = clickCount;
        
        message[7 + offset] |= (char) ((clickCount & 0x3F) << 2);
    }	
    
	/**
	 * Set avgVoltage.
	 * @param avgVoltage
	 */	   
    public void setAvgVoltage(int avgVoltage) 
    {
    	this.avgVoltage = avgVoltage;
   
        avgVoltage = (int) ( (avgVoltage -voltageOffset)/2.0 );
        
        message[6 + offset] |= (char) ((avgVoltage & 0xF) << 4);
        message[7 + offset] |= (char) ((avgVoltage & 0x30) >>> 4);
    }	
      
    public boolean isCompression() 
    {
        return this.compression;
    }
    
	/**
	 * @return Returns the voltageOffset.
	 */
    public int getVoltageOffset() 
    {
        // Voltage range. If the lowBattery bit is set, then the range on the voltage is 40-166V; otherwise it's 166-292V.  
    	voltageOffset = 166;
    	
    	if (this.isLowBattery()) {
        	voltageOffset = 40;
        }
        return voltageOffset;
    }
    
	/**
	 * @return Returns the relativeTimestamp.
	 */
    public int getRelativeTimestamp() 
    {
        return this.relativeTimestamp;
    }
    
    /**
     * @return The wallclock time of the last read.
     */
    public Date getReadTime() 
    {
        return this.readTime;
    }
    
    /**
     * @return Returns the Delta Data Type.
     */
    public int getDdt() 
    {
        return this.ddt;
    }
    
	/**
	 * @return Returns the currentReading.
	 */ 
    public int getCurrentReading() 
    {
        return this.currentReading;
    }
    
	/**
	 * @return Returns the minVoltage.
	 */
	public int getMinVoltage() 
	{
        return this.minVoltage;
    }
    
	/**
	 * @return Returns the maxVoltage.
	 */    
    public int getMaxVoltage() 
    {
        return this.maxVoltage;
    }	 
    
	/**
	 * @return Returns the avgVoltage.
	 */    
    public int getAvgVoltage() 
    {
        return this.avgVoltage;
    }	
    
	   /**
     * @return This is the number of times the AC voltage gaps of more than
     *         MinimumClickCount duration have been detected before a power fail
     *         event occurs. This is measured to count the number of transformer
     *         breaker operations detected at the meter.
     */
    public int getClickCount() 
    {
        return this.clickCount;
    }
	
    public int getTimeScale() {
        return timestampScale[this.ddt];
    } 

    public String getBits() 
    {
        return bs.toString();
    }

    public ArrayList getBins() 
    {
        ArrayList<Integer> binArr = new ArrayList<Integer>();
        for (int i = 0; i < binsPerMsg[ddt]; i++) 
        {
            binArr.add(new Integer(bins[i]));
        }
        
        return binArr;
    }

    public String getInfo() 
    {
        return super.getRepId() + ":  " + currentReading + "kWh@ seq#"
            + super.getAppSeq() + "\t" + minVoltage + "Vmin/" + maxVoltage
            + "Vmax/" + avgVoltage + "Vavg ";
    }
    
    public void setBins(int[] bins) {
		this.bins = bins;
		BitSet bs = generateBitSet(bins);
		int messageByte = 8;
		for (int bit=0; bit<152; bit++ ) {
			int bitOfByte = bit % 8;
			if ( bitOfByte == 0 ) {
				messageByte++;
			}
			if ( bs.get(bit) ) {
				message[messageByte + offset] |= (0x01 << bitOfByte);
			}
		}
	}
    
    private BitSet generateBitSet( int[] reads ) {
		BitSet bits = new BitSet(152);
		bits.set(0,152);
		int bit = 0;
		int read = 0;
		while (bit < 152 && read < 152) {
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
	
	public void screenDump() {
		super.screenDump();
        System.out.println("Relative Timestamp-----" + this.getRelativeTimestamp());
        System.out.println("Current Reading--------" + this.getCurrentReading());
        System.out.println("Delta Data Type--------" + this.getDdt());
        System.out.println("Compression------------" + this.isCompression());
        System.out.println("Min Voltage------------" + this.getMinVoltage());
        System.out.println("Max Voltage------------" + this.getMaxVoltage());
        System.out.println("Avg Voltage------------" + this.getAvgVoltage());
        System.out.println("Click Count------------" + this.getClickCount());
        System.out.println("Bin valus--------------");
        for ( int i=0; i<bins.length; i++ ) {
            System.out.print (this.bins[i]+", ");

        }		
        System.out.println();
	}

}