/**
 * Copyright (C) 2006 Sensus MS, all rights reserved.
 */

package com.amdswireless.messages.rx;

import java.io.Serializable;
import java.util.BitSet;

/**
 * @author Xuming.Chen
 * @since: 11/10/2006
 * 
 * @version: AppMessageType17.java v 1.0  11/14/2006   xumingc
 */

/* 
 * This is a parser for message type 17 (App Code 23)  
 * The bit field looks like this:
 *  Byte	 Field
 *  ------------------------------------------------
 *  0		 Message Number	
 *  1		 Number of Readings in Message
 *  2-3(0:3) Address in EEPROM of most recent reading in this messsage 
 *           (Byte 2, and bits 0-3 of byte 3)
 *  3(4:7)   Interval Data Type (bits 4:7 of byte 3, 
 *  			0 - simple Net Metering
 *  			1 - Net Metering Forward Reading
 *  			2 - Net Metering Reverse Reading
 *  			3 - Testing, Uncompressed Samples)
 *  4-27	Compressed Deltas (24 bytes)
 *  ------------------------------------------------
 */

public class AppMessageType17 extends ReadMessage implements AppMessage, Serializable 
{
	/**
	 *
	 */
	private transient static final long serialVersionUID = -2877720172526362109L;	
	private transient static final int offset = 31;

	private int messageNumber;
	private int numberOfReadingInMessage;
	private int addressInEeprom;
	private int intervalDataType;
	//private int compressedDeltas;
    private transient BitSet bs;
    private int[] bins;
 
	/**
	 *  Default constructor for encoding.
	 */
	public AppMessageType17() {		
		super();
		super.setAppCode(0x17);
		super.setMessageType(0x17);	
	}
	
	/**
	 *  Default constructor for decoding. Extract information from Andorian message.
	 *  @param msg
	 */
	public AppMessageType17( char[] msg ) 
	{		
		super(msg);
		super.setMessageType(0x17);
		
		// Pass it with the entire Andorian message; offset the starting byte by "offset".				
		this.messageNumber = (int) (msg[0 + offset]);	
		this.numberOfReadingInMessage =(int) (msg[1 + offset]);			
		this.addressInEeprom = (int) (msg[2 + offset] + msg[3 & 0xF] << 8);					
		this.intervalDataType = (int) (msg[3 + offset] >>> 4);	
	
		// Step through the remaining 24 bytes (from 4 to 27) in the message.
		//this.compressedDeltas = (int) (msg[4 + offset]);
		//for (int j = 5; j <= 27; j++) 
		//{			              
			//this.compressedDeltas += (int) (msg[j + offset] << (8 * (j-4)));		 
		//}
		
		bs = new BitSet();
		// Step through the remaining 24 bytes in the message and convert to bits.
		int cnt = 0;		
		for (int j= 4; j <= 27; j++) {
			for (int i = 0; i < 8; i++) {               
				bs.set(cnt, (((int) (msg[j + + offset] >>> i)) & 0x1) == 0x1);                
				cnt++;
			}
		} 
	
		// We need to decode the bits using the Huffman Binary Tree algorithm described in the spec. 
		bins = super.decompress(bs); 		
	}

	/**
	 * Set messageNumber.
	 * @param messageNumber
	 */
	public void setMessageNumber(int messageNumber) 
	{		
		this.messageNumber = messageNumber;	
		message[0 + offset] = (char) (messageNumber & 0xFF);
	}

	/**
	 * Set numberOfReadingInMessage.
	 * @param numberOfReadingInMessage
	 */
	public void setNumberOfReadingInMessage(int numberOfReadingInMessage) 
	{		
		this.numberOfReadingInMessage = numberOfReadingInMessage;	
		message[1 + offset] = (char) (numberOfReadingInMessage & 0xFF);	
	}
	
	/**
	 * Set addressInEeprom.
	 * @param addressInEeprom
	 */
	public void setAddressInEeprom(int addressInEeprom) 
	{		
		this.addressInEeprom = addressInEeprom;	
		message[2 + offset] = (char) (addressInEeprom & 0xFF);
		message[3 + offset] |= (char) ((addressInEeprom & 0xF00) >>> 8);		
	}
	
	/**
	 * Set intervalDataType.
	 * @param intervalDataType
	 */
	public void setIntervalDataType(int intervalDataType) 
	{		
		this.intervalDataType = intervalDataType;	
		message[3 + offset] |= (char) ((intervalDataType & 0xF) << 4);
	}
 
	/**
	 * Set compressedDeltas.
	 * @param compressedDeltas
	 */
/*	public void setCompressedDeltas(int compressedDeltas) 
	{		
		this.compressedDeltas = compressedDeltas;	
		
		//Step through for the remaining 24 bytes (from 4 to 27)
		message[4 + offset] = (char) (compressedDeltas & 0xFF);

		int numberBitsShift = 0;
		for (int j = 5; j <= 27; j++) 
		{		
			numberBitsShift += 8 ;
			message[5 + offset] = (char) ( (compressedDeltas & (0xFF << numberBitsShift)) >>> numberBitsShift);		 
		}
	}*/
	
	
	/**
	 * @return Returns the numberOfReadingInMessage.
	 */
	public int getMessageNumber() 
	{
		return messageNumber;
	}
	
	/**
	 * @return Returns the numberOfReadingInMessage.
	 */
	public int getNumberOfReadingInMessage() 
	{
		return numberOfReadingInMessage;
	}
	
	/**
	 * @return Returns the addressInEeprom.
	 */
	public int getAddressInEeprom() 
	{
		return addressInEeprom;
	}
	
	/**
	 * @return Returns the intervalDataType.
	 */
	public int getIntervalDataType() 
	{
		return intervalDataType;
	}
	
	/**
	 * @return Returns the compressedDeltas.
	 */
/*	public int getCompressedDeltas() 
	{
		return compressedDeltas;
	}*/
	
    public void setBins(int[] bins) {
		this.bins = bins;
		BitSet bs = generateBitSet(bins);
		int messageByte = 3;
		for (int bit=0; bit<192; bit++ ) {
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
		BitSet bits = new BitSet(192);
		bits.set(0,192);
		int bit = 0;
		int read = 0;
		while (bit < 192 && read < 192) {
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
        System.out.println("Message Number---------" + this.getMessageNumber());
        System.out.println("Number of Readings-----" + this.getNumberOfReadingInMessage());
        System.out.println("Address in EEPROM------" + this.getAddressInEeprom());
        System.out.println("Interval Data Type-----" + this.getIntervalDataType());
        System.out.println("Bin valus--------------");
        for ( int i=0; i<bins.length; i++ ) {
            System.out.print (this.bins[i]+", ");

        }		
        System.out.println();
	}
	
}
