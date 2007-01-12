package com.amdswireless.messages.rx;
import java.util.StringTokenizer;

/**
 The Andorian Message is the super-class of all messages that arrive from a TGB in the 
 AMDS Network.  Each message represents a discrete package of data, with several layers.
 All messages from the TGB are Andorian Messages.  

  @author johng
 */
public class AndorianMessage implements java.io.Serializable {

    private transient static final long serialVersionUID = 1L;
	protected transient char[] message;
    private transient final char[] hex = "0123456789ABCDEF".toCharArray();

    private transient String msgTxt;
    private int    command;
    private int    freqSlot;
    private int    frequencyId;
    private int    cpcSlotNumber;
    private int    tgbId;
    private int    andorianSeq;
    private long   toi;
    private int    ncId;
    private int    msgLength;
    private int    msgType;
    private java.util.Date timestampOfIntercept;
    private String rawMessage;
    
    public AndorianMessage() {
    	message = new char[66];
    	message[0] = (char)0xfa;
    	message[1] = (char)0xaf;
    }

    public AndorianMessage(char[] buf) {
        _init(buf);
		message = new char[buf.length];
		for ( int i=0; i<buf.length; i++ ) {
			message[i] = buf[i];
		}

    }

    public AndorianMessage(String buf) {
        msgTxt = buf;
        _init(byteArrayFromString(buf));
    }

    private void _init(char[] buf) {
        try {
            this.command=(int)buf[2];
            this.freqSlot=(int)buf[3];
            this.frequencyId=(int)(buf[3]&0xF);
            this.cpcSlotNumber=(int)((buf[3]&0xF0) >>> 4);
            this.tgbId=(int)(buf[6]<<16)+(int)(buf[5]<<8)+(int)(buf[4]);
            this.andorianSeq=buf[7];
            // calculate time of intercept
            this.toi=(long)(buf[11]<<24)+(long)(buf[10]<<16)+(long)(buf[9]<<8)+(long)(buf[8]);
            this.ncId=buf[13]+buf[14]*256;
            this.msgLength=buf[15];
            this.msgType=buf[21];
            this.timestampOfIntercept=new java.util.Date(toi*1000);
        } catch ( Exception ex ) {
            System.out.println("Found a problem:  "+ex );
            System.out.println("Length of char[] buf is "+buf.length );
        }
        this.rawMessage=hexMsg(buf);
    }

	public String toHexString() {
    	StringBuffer sb= new StringBuffer();
    	for ( int i =0; i<message.length; i++ ) {
    		if ( (i+1) % 8 == 0 ) {
    			sb.append(Integer.toHexString(message[i])+"\n");
    		} else {
        		sb.append(Integer.toHexString(message[i])+" ");
    		}
    	}
    	return sb.toString();
    }
	
	public char[] getMessage() {
		return this.message;
	}

    /**
     * @return Returns the Andorian Command found in this message.
     * Current Andorian Commands include:
     * 0x53 - Status Query Message
     * 0x54 - Andorian Transmit Message
     * 0x55	- Transmit Message Report
     * 0x60 - Message from Endpoint
     * 0x64	- TGB Transmitter Pass
     * The vast majority of Andorian Messages in a normal system are type 0x60.
     */
    public int getCommand() {
        return command;
    }

    /**
     * The freqSlot byte is a combined byte containing the Frequency Identifier
     * (located in the Least significant 4 bits) and CPC Slot Number (in the
     * Most significant 4 bits).  Use 
     * @return Integer formed by all 8 bits of the frequency/slotID byte
     */
    public int getFreqSlot() {
        return freqSlot;
    }

    /** TGB ID is the network-unique code asssigned to each Tower Gateway Base-station.
     * @return The unique ID code assigned to this TGB
     */
    public int getTgbId() {
        return tgbId;
    }

    /** The Andorian Sequence is part of the TGB-NC ack handshake
     * By using Andorian Sequence in combination with timestamps, the TGB
     * ensures that no duplicate messages are transmitted to the NC.  Each message
     * send between the TGB and the NC increments this 8 bit digit until it rolls
     * over.
     * @return The Andorian Sequence of this message
     */
    public int getAndorianSeq() {
        return andorianSeq;
    }

    /**
     * @return the Network Controller ID that this TGB is communicating with.
     */
    public int getNcId() {
        return ncId;
    }

    /**
     * @return Length of this Andorian message.  Should always be 0x32.
     */
    public int getLength() {
        return msgLength;
    }

    /** TOI stands for Time of Intercept.  This is a 4 byte, epoch-based timestamp
     * that is given to each message to indicate when it was received at the TGB.
     * Note that this number has 1 second resolution, whereas most timestamps in Java 
     * have 1ms resolution.  To convert this number to a java.util.Date, use
     * 	java.util.Date d = new java.util.Date( m.getToi() * 1000 );
     * 
     * @return the epoch Time of Intercept for this message
     */
    public long getToi() {
        return toi;
    }

    /** Each CPC card has two "slots."  This identifies the physical CPC channel that handled
     * the message.  This field has meaning for received on-air messages only.  It is not used
     * for transmitted messages or other message types.
     * @return The CPC slot which received this message.
     */
    public int getCPCSlotNumber() {
        return cpcSlotNumber;
    }

    /** This fieldc ontains the lookup table index on the specific TGB that the message is
     * originating from, which identifies the frequency channel upon which the message was
     * transmitted.  It is possible that the same numbers represent different actual frequencies
     * on different TGBs.  Any software that needs to convert this number to an actual frequency
     * needs to have access to a conversion table for that TGB
     * @return The index for the frequency that this message was received on 
     */
    public int getFrequencyId() {
        return frequencyId;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return msgTxt;
    }

    public java.util.Date getTimestampOfIntercept() {
        return this.timestampOfIntercept;
    }

    public String getRawMessage() {
        return this.rawMessage;
    }

    private char[] byteArrayFromString(String hexString) {
        int len = hexString.length();
        char[] byteArray = new char[len/2];

        for(int i=0, j=0; i < len; i+=2, j++) {
            char high = hexString.charAt(i);
            char low  = hexString.charAt(i+1);
            int  high_order = high - (char)'0';
            int  low_order  = low  - (char)'0';

            if(10 <= high_order) { high_order -=7; }
            if(10 <= low_order)  { low_order  -=7; }
            byteArray[j] = (char)((high_order << 4) + low_order);
        }
        return byteArray;
    }

    private String hexMsg(char[] buf) {
        StringBuffer s = new StringBuffer();
        int len = buf.length;
        char b;
        if ( len > 200 ) {
            System.err.println("Exceptionally large buffer received by AndorianMessage.  Setting size to 200.\n");
            len=200;
        }
        for(int i=0;i<len;i+=1) {
            b = buf[i];
            if ( i % 8 == 0 ) {
                s.append("\n");
            }
            try {
                s.append(" "+ hex[b>>>4] + hex[b&0xf] );
            } catch ( ArrayIndexOutOfBoundsException ex ) {
                return "Unable to decode hex string at byte "+i+" ("+ (int)b +")";
            }
        }
        return s.toString();
    }

    public String dumpPayload( char[] buf) {
        StringBuffer s = new StringBuffer();
        int len = buf.length;
        char b;
        if ( len > 200 ) {
            System.err.println("Exceptionally large buffer received by AndorianMessage.  Setting size to 200.\n");
            len=200;
        }
        for(int i=31;i<len;i+=1) {
            b = buf[i];
            try {
                s.append( hex[b>>>4] + hex[b&0xf] );
            } catch ( ArrayIndexOutOfBoundsException ex ) {
                return "Unable to decode hex string at byte "+i+" ("+ (int)b +")";
            }
        }
        return s.toString();
    }

    public static String cleanHex(String buf) {
        StringTokenizer t        = new StringTokenizer(buf);
        String          deSpaced = new String();

        while (t.hasMoreTokens()) { deSpaced += t.nextToken(); }
        return deSpaced;
    }

    /**
     * @return Returns the Andorian Message Type
     */
    public int getMsgType() {
        return msgType;
    }

	public int getCpcSlotNumber() {
		return cpcSlotNumber;
	}

	public void setCpcSlotNumber(int cpcSlotNumber) {
		this.cpcSlotNumber = cpcSlotNumber;
		message[3] |= (char)((cpcSlotNumber & 0xF) << 4);
	}

	public void setFrequencyId(int frequencyId) {
		this.frequencyId = frequencyId;
		message[3] |= (char)(frequencyId & 0xF);
	}

	public void setNcId(int ncId) {
		this.ncId = ncId;
		message[13] = (char)(ncId >>> 8);
		message[14] = (char)(ncId * 0xFF);
	}

	public void setTgbId(int tgbId) {
		this.tgbId = tgbId;
		message[6] = (char)(tgbId >>> 16);
		message[5] = (char)((tgbId >>> 8 ) & 0xFF );
		message[4] = (char)(tgbId & 0xFF);
	}

	public void setTimestampOfIntercept(java.util.Date timestampOfIntercept) {
		this.timestampOfIntercept = timestampOfIntercept;
		this.toi = (int)timestampOfIntercept.getTime() / 1000;
	}

	public void setToi(long toi) {
		this.toi = toi;
		message[11] = (char)(toi >>> 24);
		message[10] = (char)((toi >>> 16 ) & 0xFF );
		message[9] =(char)((toi >> 8 ) & 0xFF);
		message[8] = (char)(toi & 0xFF);
		this.timestampOfIntercept = new java.util.Date(toi*1000);
	}

	public void setAndorianSeq(int andorianSeq) {
		this.andorianSeq = andorianSeq;
		message[7] = (char)andorianSeq;
	}

	public int getMsgLength() {
		return msgLength;
	}

	public void setMsgLength(int msgLength) {
		this.msgLength = msgLength;
		message[15] = (char)(msgLength & 0xFF);
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
		message[21] = (char)(msgType & 0xFF);
	}

	public void setCommand(int command) {
		this.command = command;
		message[2] = (char)(command & 0xFF);
	}

}
