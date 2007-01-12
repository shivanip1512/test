package com.amdswireless.messages.tx;

import java.io.Serializable;
//import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

import com.amdswireless.messages.twoway.TxRequest;

// private data is stored one type larger to accomidate 2's compliment
// XXX what other valuse can ASP address/direction have?
// XXX what is the max ASP data field length?
// XXX is ASP data field length 32[spec] or 31[marcs]
// TODO update reciver status flags to individually set
// TODO update control flags to individually set

public class TxMsg extends TxRequest implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String hex = "0123456789ABCDEF";
	private transient final char[] hexArr = "0123456789ABCDEF".toCharArray();

    protected StringBuffer msgText = new StringBuffer("FAAF000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
    private int[] txMsg = new int[61];
    protected short  command;    // byte    2
    private byte   freq;       // nibble  3_low
    private byte   slot;       // nibble  3_high
    private int    tgbId;      // bytes   4-6
    private short  sequence;   // byte    7
    private long   toi;        // bytes   8-11
    private short  reserved;   // byte   12
    private int    ncId;       // bytes  13-14
    private short  length;     // byte   15
    private short  miscTx;     // byte   16 // set to 1 for now
    private int   repId;      // byte   17-20
    private short  repeatLevel;// byte   21
    private short  appSequence;// byte   22 same as byte 7
    private short  appCode;    // byte   23 set to 0x07 (send command)
    private short  commandType;// byte   24 
    private int   buddyId;    // bytes  25-28
    private short  custId;

    public TxMsg() {
        //	"FAAF 00 0 0 000000 00 00000000 00 0000 00 00 000000 00 00 00 00 000000 00 00"
    	txMsg[0] = 0xFA;
    	txMsg[1] = 0xAF;
    	txMsg[2] = 0x54;
    	txMsg[3] = 0x00;
    	txMsg[4] = 0x00; // TGB Id, gets set later
    	txMsg[5] = 0x00; // TGB Id, gets set later
    	txMsg[6] = 0x00; // TGB Id, gets set later
    	txMsg[7] = 0x00; // sequence number
    	txMsg[8] = 0x00; // timestamp, gets set later
    	txMsg[9] = 0x00; // timestamp, gets set later
    	txMsg[10] = 0x00; // timestamp, gets set later
    	txMsg[11] = 0x00; // timestamp, gets set later
    	txMsg[12] = 0x00; // extended timestamp, see above
    	txMsg[13] = 0x00; // NC ID.  Leave as zero, the NC will set
    	txMsg[14] = 0x00; // NC ID.  Leave as zero, the NC will set
    	txMsg[15] = 0x24; // length.  leave it.
    	txMsg[16] = 0x00; // normal mode & miscTx
    	// all of the remaining bytes are set by subclasses
    	for ( int i = 17; i<52; i++ ) {
    		txMsg[i] = 0x00;
    	}
    	command  = 0x0;
        freq     = 0x0;
        slot     = 0x0;
        tgbId    = 0x000;
        sequence = 0x0;
        toi      = 0x000000;
        reserved = 0x0;
        ncId     = 0x00;
        length   = 0x0;

        miscTx   = 0x01;
        repId    = 0x000000;
        repeatLevel = 0x0;
        appSequence = 0x00;
        appCode  = 0x7;
        commandType = 0x0;
        buddyId  = 0x000000; //zero unless we're using buddy mode
        custId = 0x0;
    }

    public String toString() {
        //return new String(msgText);
    	StringBuffer sb= new StringBuffer();
    	for ( int i =0; i<txMsg.length; i++ ) {
    		sb.append((char)txMsg[i]);
    	}
    	return sb.toString();
    }
    
    public String toHexString() {
    	getMsgText();
    	StringBuffer sb= new StringBuffer();
    	for ( int i =0; i<txMsg.length; i++ ) {
    		sb.append(Integer.toHexString(txMsg[i])+" ");
    	}
    	return sb.toString();
    }

    public byte[] getBytes() {
    	getMsgText();
        byte[] retBytes = new byte[txMsg.length];
        for ( int i=0; i<txMsg.length; i++ ) {
            retBytes[i]=(byte)txMsg[i];
        }
        return retBytes;
    }
/*
    public String getBytes() {
        String retStr = "";
        String msg = getMsgText();
        try {
            byte[] foo = decodeHex(msg.toCharArray());
	    retStr = new String(foo);
        } catch ( DecoderException de ) {
            System.err.println(de);
            return null;
        }
        return retStr;
    }
*/
    public String getMsgText() {
        updateMsg(command, 2);
        updateMsg((short)(freq + (slot << 4)), 3);
        updateMsg( (short) (0x0000ff & tgbId)       , 4);
        updateMsg( (short)((0x00ff00 & tgbId) >> 8 ), 5);
        updateMsg( (short)((0xff0000 & tgbId) >> 16), 6);
        updateMsg(sequence, 7);
        updateMsg( (short) (0x000000ff & toi)       , 8 );
        updateMsg( (short)((0x0000ff00 & toi) >> 8 ), 9 );
        updateMsg( (short)((0x00ff0000 & toi) >> 16), 10);
        updateMsg( (short)((0xff000000 & toi) >> 24), 11);
        updateMsg( (short) (0x00ff & ncId)       , 13);
        updateMsg( (short)((0xff00 & ncId) >> 8 ), 14);
        updateMsg(length, 15);
        updateMsg(miscTx, 16);
        short low  = 0;
        short high = 0;
        low  = (short)((0x0000000f & repId)      );
        high = (short)((0x000000f0 & repId)      );
        updateMsg((short)(low + high), 17);
        low  = (short)((0x00000f00 & repId) >>  8);
        high = (short)((0x0000f000 & repId) >>  8);
        updateMsg((short)(low + high), 18);
        low  = (short)((0x000f0000 & repId) >> 16);
        high = (short)((0x00f00000 & repId) >> 16);
        updateMsg((short)(low + high), 19);
        low  = (short)((0x0f000000 & repId) >> 24);
        high = custId;   // could be paranoid and check that it was intialized, but WTH
        updateMsg((short)(low + high), 20);
        updateMsg(repeatLevel, 21);
        updateMsg(sequence, 22);
        updateMsg(appCode, 23);
        updateMsg(commandType, 24);
        low  = 0;
        high = 0;
        low  = (short)((0x0000000f & buddyId)      );
        high = (short)((0x000000f0 & buddyId)      );
        updateMsg((short)(low + high), 25);
        low  = (short)((0x00000f00 & buddyId) >>  8);
        high = (short)((0x0000f000 & buddyId) >>  8);
        updateMsg((short)(low + high), 26);
        low  = (short)((0x000f0000 & buddyId) >> 16);
        high = (short)((0x00f00000 & buddyId) >> 16);
        updateMsg((short)(low + high), 27);
        low  = (short)((0x0f000000 & buddyId) >> 24);
        high = (short)((0xf0000000 & buddyId) >> 24);
        updateMsg((short)(low + high), 28);
        //return msgText.toString();
	return this.toString();
    }

    public void setCommand(short cmd) // throws CommandTypeInvalid {
    {
        switch(cmd) {
            case 0x50: command=0x50; break;  // tgb status response       [from tgb]
            case 0x53: command=0x53; break;  // tgb status request        [to tgb]
            case 0x54: command=0x54; break;  // andorian transmit message [to network]
            case 0x55: command=0x55; break;  // transmit message response [ack/nack]
            case 0x60: command=0x60; break;  // ASP message payload       [from network]
            case 0x64: command=0x64; break;  // transmitter pass          [to exciter]
//            default: throw new CommandTypeInvalid();
        }
        updateMsg(command, 2);
    }

    public void setFreq(byte id) // throws FrequencyOutOfRange {
//        if( (15 >= id) && (0 <= id))  // freq is an index into a mutable table on the TGB 
        { 
            freq = id;
            updateMsg((short)(freq + (slot << 4)), 3);
 //       }
 //       else throw new FrequencyOutOfRange();
    }

    public void setSlot(byte cpc) // throws SlotOutOfRange {
//        if( 9 >= cpc && (0 <= cpc))  // older TGBs go up to 9, most got to 4
        { 
            slot = cpc;
            updateMsg((short)(freq + (slot << 4)), 3);
//        }
//        else throw new SlotOutOfRange();
    }

    public void setTgbId(int tgb) // throws IdOutOfRange {
 //       if( 0xffffff >= tgb && (0 <= tgb))  // TGB Ids are 3 bytes
        { 
            tgbId = tgb;
            updateMsg( (short) (0x0000ff & tgbId)       , 4);
            updateMsg( (short)((0x00ff00 & tgbId) >> 8 ), 5);
            updateMsg( (short)((0xff0000 & tgbId) >> 16), 6);
//        }
//        else throw new IdOutOfRange();
    }

    public void setSeq(short seq) {
        if( 0 <= seq)  {
            sequence = seq;
            appSequence = seq;
            updateMsg(sequence, 7);
            updateMsg(sequence, 22);
        }
    }

    public void setToi(long time) // throws TimeOutOfRange {
        //		if( 788918400 >= time && (0 <= time))  // limit back posting to 1995-01-01 00:00:00
//        if( 0 <= time) 
        { 
            toi = time;
            updateMsg( (short) (0x000000ff & toi)       , 8 );
            updateMsg( (short)((0x0000ff00 & toi) >> 8 ), 9 );
            updateMsg( (short)((0x00ff0000 & toi) >> 16), 10);
            updateMsg( (short)((0xff000000 & toi) >> 24), 11);
//        }
//        else throw new TimeOutOfRange();
    }

    public void setNcId(int nc) // throws IdOutOfRange {
//        if( 0 <= nc) 
        { 
            ncId = nc;
            updateMsg( (short) (0x00ff & ncId)       , 13);
            updateMsg( (short)((0xff00 & ncId) >> 8 ), 14);
//        }
//        else throw new IdOutOfRange();
    }

    public int getNcId() {
        return ncId;
    }

    public void setLength(short len) // throws LengthOutOfRange {
//        if(0x24 == len)
        { 
            length = len;
            updateMsg(length, 15);
//        }
//        else throw new LengthOutOfRange();
    }

    public void setMiscTx(short mt) // throws MiscTxInvalid {
//        if(0x1 == mt)
        { 
            miscTx = mt;
            updateMsg(miscTx, 16);
//        }
//        else throw new MiscTxInvalid();
    }

    // rep id as recived from TGB, in nibbles, 7452301X ( where X is soon to be cust id)
    public void setRepId(int id) // throws RepIdOutOfRange {
//        if( 0 <= id) 
        { 
            short low  = 0;
            short high = 0;

            repId = id;
            /*
               System.out.println("Byte 22 low :" + toHex((short)((0x0000000f & repId)      )) + ":");
               System.out.println("Byte 22 high:" + toHex((short)((0x000000f0 & repId)      )) + ":");
               System.out.println("Byte 23 low :" + toHex((short)((0x00000f00 & repId) >>  8)) + ":");
               System.out.println("Byte 23 high:" + toHex((short)((0x0000f000 & repId) >>  8)) + ":");
               System.out.println("Byte 24 low :" + toHex((short)((0x000f0000 & repId) >> 16)) + ":");
               System.out.println("Byte 24 high:" + toHex((short)((0x00f00000 & repId) >> 16)) + ":");
               System.out.println("Byte 25 low :" + toHex((short)((0x0f000000 & repId) >> 24)) + ":");
            // 25 high is Cust Id
            */
            low  = (short)((0x0000000f & repId)      );
            high = (short)((0x000000f0 & repId)      );
            updateMsg((short)(low + high), 17);
            low  = (short)((0x00000f00 & repId) >>  8);
            high = (short)((0x0000f000 & repId) >>  8);
            updateMsg((short)(low + high), 18);
            low  = (short)((0x000f0000 & repId) >> 16);
            high = (short)((0x00f00000 & repId) >> 16);
            updateMsg((short)(low + high), 19);
            low  = (short)((0x0f000000 & repId) >> 24);
            high = custId;   // could be paranoid and check that it was intialized, but WTH
            updateMsg((short)(low + high), 20);
//       }
//        else throw new RepIdOutOfRange();
    }

    public int getRepId() {
        return this.repId;
    }

    public void setRepeatLevel(short rl) //throws RepeatLevelOutOfRange {
    {
//        if(0x1 == rl)
        { 
            repeatLevel = rl;
            updateMsg(repeatLevel, 21);
        }
//        else throw new RepeatLevelOutOfRange();
    }

    public void setAppSequence(short as) {
        setSeq(as);
    }

    public void setAppCode(short ac) // throws AppCodeOutOfRange {
    {
//        if(0x7 == ac)
//        { 
            appCode= ac;
            updateMsg(appCode, 23);
//        }
//        else throw new AppCodeOutOfRange();
    }

    public void setCommandType(short type) // throws CommandTypeInvalid {
    {
    	this.commandType=type;
    	/* once upon a time, this was a good idea, and I give props to mcampbel
    	 * for doing it this way.  However, I've been burned by it twice in two
    	 * weeks because we're trying to send new command types that weren't 
    	 * enumerated here, so I'm putting the onus on the message requestor
    	 * to not screw up and request a command type that doesn't exist.
    	switch(type) {
            case 0x0: commandType=0x0; break;  // Command Acknowledge
            case 0x1: commandType=0x1; break;  // Set Static Setup
            case 0x2: commandType=0x2; break;  // Set Crystal Offset
            case 0x3: commandType=0x3; break;  // Set Lat/Long
            case 0x4: commandType=0x4; break;  // Set Voltage Quality Settings
            case 0x5: commandType=0x5; break;  // Set Time
            case 0x6: commandType=0x6; break;  // Load Shed
            case 0x7: commandType=0x7; break;  // Demand Read
            case 0x8: commandType=0x8; break;  // Ping
            case 0xE: commandType=0xE; break;  // Message Transmit
            case 0xF: commandType=0xF; break;  // Raw Table Read Transmit
            case 0x10:  commandType=0x10; break; // Raw Table Write Transmit
//            default: throw new CommandTypeInvalid();
        }
        */
        updateMsg(commandType, 24);
    }

    public short getCommandType() {
        return commandType;
    }

    // rep id as recived from TGB, in nibbles, 7452301X ( where X is soon to be cust id)
    public void setBuddyRepId(int id) // throws RepIdOutOfRange {
    {
//        if( 0 <= id) 
//        { 
            short low  = 0;
            short high = 0;

            buddyId = id;
            /*
               System.out.println("Byte 22 low :" + toHex((short)((0x0000000f & repId)      )) + ":");
               System.out.println("Byte 22 high:" + toHex((short)((0x000000f0 & repId)      )) + ":");
               System.out.println("Byte 23 low :" + toHex((short)((0x00000f00 & repId) >>  8)) + ":");
               System.out.println("Byte 23 high:" + toHex((short)((0x0000f000 & repId) >>  8)) + ":");
               System.out.println("Byte 24 low :" + toHex((short)((0x000f0000 & repId) >> 16)) + ":");
               System.out.println("Byte 24 high:" + toHex((short)((0x00f00000 & repId) >> 16)) + ":");
               System.out.println("Byte 25 low :" + toHex((short)((0x0f000000 & repId) >> 24)) + ":");
            // 25 high is Cust Id
            */
            low  = (short)((0x0000000f & buddyId)      );
            high = (short)((0x000000f0 & buddyId)      );
            updateMsg((short)(low + high), 25);
            low  = (short)((0x00000f00 & buddyId) >>  8);
            high = (short)((0x0000f000 & buddyId) >>  8);
            updateMsg((short)(low + high), 26);
            low  = (short)((0x000f0000 & buddyId) >> 16);
            high = (short)((0x00f00000 & buddyId) >> 16);
            updateMsg((short)(low + high), 27);
            low  = (short)((0x0f000000 & buddyId) >> 24);
            high = (short)((0x0f000000 & buddyId) >> 24);
            updateMsg((short)(low + high), 28);

//        }
//        else throw new RepIdOutOfRange();
    }

// the source of great ill - mcampbel 21Feb06
// spiked XXX
    public void setCustId(byte id) //throws CustIdInvalid {
    {
        //		if((15 >= id) && (0 <= id))  // high order nibble
//        if(0 == id)  // currently only 0
/*        { 
            custId = id;
            updateMsg((short)((custId << 4) + ((0x0f000000 & repId) >> 24)), 25);
        }
*/
//        else throw new CustIdInvalid();
    }

    protected void updateMsg(short data, int position) {
        msgText.replace(position * 2, (position * 2) + 2, toHex(data));
    	txMsg[position] = data;
    }

    // only operates on a byte, but have to pass in a short avoid 2's complement overflow
    // the *ToHex functions return -1 on a (2's complement) negative value
    public static String toHex(short data) {
        if( 0 == (data & 0x8000) ) 
        {
            StringBuffer hexVal = new StringBuffer();
            hexVal.append( hex.charAt((data & 0xf0) >> 4) );
            hexVal.append( hex.charAt (data & 0x0f)       );
            return hexVal.toString();
        }
        return "-1";
    }

    public static String toHex(int data) {
        if( 0 == (data & 0x80000000) ) 
        {
            StringBuffer hexVal = new StringBuffer();
            hexVal.append( hex.charAt((data & 0xf000) >> 12) );
            hexVal.append( hex.charAt((data & 0x0f00) >> 8 ) );
            hexVal.append( hex.charAt((data & 0x00f0) >> 4 ) );
            hexVal.append( hex.charAt((data & 0x000f)      ) );
            return hexVal.toString();
        }
        return "-1";
    }

    public static String toHex(long data) {
        if( 0 == (data & 0x8000000000000000l) ) 
        {
            StringBuffer hexVal = new StringBuffer();
            hexVal.append( hex.charAt((byte)((data & 0xf0000000) >> 28)) );
            hexVal.append( hex.charAt((byte)((data & 0x0f000000) >> 24)) );
            hexVal.append( hex.charAt((byte)((data & 0x00f00000) >> 20)) );
            hexVal.append( hex.charAt((byte)((data & 0x000f0000) >> 16)) );
            hexVal.append( hex.charAt((byte)((data & 0x0000f000) >> 12)) );
            hexVal.append( hex.charAt((byte)((data & 0x00000f00) >> 8 )) );
            hexVal.append( hex.charAt((byte)((data & 0x000000f0) >> 4 )) );
            hexVal.append( hex.charAt((byte)((data & 0x0000000f)      )) );
            return hexVal.toString();
        }
        return "-1";
    }

    // the code for decodeHex and toDigit were blatantly lifted from the apache
    // directory project.  Thank you to the hard-working apache source contributors

    /**
     * Converts an array of characters representing hexidecimal values into an
     * array of bytes of those same values. The returned array will be half the
     * length of the passed array, as it takes two characters to represent any
     * given byte. An exception is thrown if the passed char array has an odd
     * number of elements.
     * 
     * @param data An array of characters containing hexidecimal digits
     * @return A byte array containing binary data decoded from
     *         the supplied char array.
     * @throws DecoderException Thrown if an odd number or illegal of characters 
     *         is supplied
     */
    /*
    public static byte[] decodeHex(char[] data) throws DecoderException {

        int len = data.length;

        if ((len & 0x01) != 0) {
            throw new DecoderException("Odd number of characters.");
        }

        byte[] out = new byte[len >> 1];

        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }

        return out;
    }
*/
    /**
     * Converts a hexadecimal character to an integer.
     *  
     * @param ch A character to convert to an integer digit
     * @param index The index of the character in the source
     * @return An integer
     * @throws DecoderException Thrown if ch is an illegal hex character
     */
    protected static int toDigit(char ch, int index) throws DecoderException {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new DecoderException("Illegal hexadecimal charcter " + ch + " at index " + index);
        }
        return digit;
    }
    public char[] byteArrayFromString() {
        return byteArrayFromString(msgText.toString());
    }

    public char[] byteArrayFromString(String hexString) {
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
/*
    protected String hexMsg(char[] buf) {
        StringBuffer s = new StringBuffer();
        int len = buf.length;
        char b;
        for(int i=0;i<len;i+=1) {
            b = buf[i];
            if ( i % 8 == 0 ) {
                s.append("\n");
            }
            s.append(" "+ hexArr[b>>>4] + hexArr[b&0xf] );
        }
        return s.toString();
    }
*/
    protected static String cleanHex(String buf) {
    	if ( buf.equals(null) ) {
    		return "00";
    	}
        StringTokenizer t        = new StringTokenizer(buf);
        String          deSpaced = new String();

        while (t.hasMoreTokens()) { deSpaced += t.nextToken(); }
        return deSpaced;
    }

    public static void main(String[] args) {
        System.out.println("Whatever");
    }

	/**
	 * @return Returns the hex.
	 */
	public static String getHex() {
		return hex;
	}

	/**
	 * @return Returns the appCode.
	 */
	public short getAppCode() {
		return appCode;
	}

	/**
	 * @return Returns the appSequence.
	 */
	public short getAppSequence() {
		return appSequence;
	}

	/**
	 * @return Returns the buddyId.
	 */
	public int getBuddyId() {
		return buddyId;
	}

	/**
	 * @return Returns the command.
	 */
	public short getCommand() {
		return command;
	}

	/**
	 * @return Returns the custId.
	 */
	public short getCustId() {
		return custId;
	}

	/**
	 * @return Returns the freq.
	 */
	public byte getFreq() {
		return freq;
	}

	/**
	 * @return Returns the hexArr.
	 */
	public char[] getHexArr() {
		return hexArr;
	}

	/**
	 * @return Returns the length.
	 */
	public short getLength() {
		return length;
	}

	/**
	 * @return Returns the miscTx.
	 */
	public short getMiscTx() {
		return miscTx;
	}

	/**
	 * @return Returns the repeatLevel.
	 */
	public short getRepeatLevel() {
		return repeatLevel;
	}

	/**
	 * @return Returns the reserved.
	 */
	public short getReserved() {
		return reserved;
	}

	/**
	 * @return Returns the sequence.
	 */
	public short getSequence() {
		return sequence;
	}

	/**
	 * @return Returns the slot.
	 */
	public byte getSlot() {
		return slot;
	}

	/**
	 * @return Returns the tgbId.
	 */
	public int getTgbId() {
		return tgbId;
	}

	/**
	 * @return Returns the toi.
	 */
	public long getToi() {
		return toi;
	}
}	
