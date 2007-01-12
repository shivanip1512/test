package com.amdswireless.messages.tx;



public class TxCITableWriteMsg extends TxMsg {
	private static final long serialVersionUID = 1L;
	private short tableNumber;
    private int tableOffset;
    private short bytesSent;
    private char[] charArr;

    public TxCITableWriteMsg() {
        super();
        try {
            super.setCommand((short)0x54);
            super.setLength((short)0x24);
            super.setAppCode((short)0x7);
            super.setMiscTx((short)0x1);
            super.setCommandType((short)0x10);
            super.setNcId((short)0x01);
            super.setToi(new java.util.Date().getTime()/1000);
        } catch ( Exception ex ) {
        }
        tableNumber=0x0;
        tableOffset=0x0;
        bytesSent=0x0;
        charArr = null;
    }

    public void setTableNumber(short t)  {
        tableNumber=t;
    }
    public short getTableNumber() {
        return tableNumber;
    }
    public void setTableOffset(int i) {
        this.tableOffset=i;
    }
    public int getTableOffset() {
        return tableOffset;
    }
    public void setBytesSent(short s) {
        if ( s > 27 ) {
            s = 27;
            System.err.println("Too many bytes requested!");
        }
        this.bytesSent=s;
    }
    public short getBytesSent() {
        return this.bytesSent;
    }

    public void setRawMessage(String s) {
    	if ( s.equals(null) ) {
    		return;
    	}
        String clean = super.cleanHex(s);
        charArr = super.byteArrayFromString(clean);
        this.setBytesSent((short)charArr.length);
    }
    
    public void setMessageBytes( int[] bytes ) {
    	charArr = new char[bytes.length];
    	for ( int i=0; i<bytes.length; i++ ) {
    		charArr[i] = (char)bytes[i];
    	}
    	this.setBytesSent((short)bytes.length);
    }

    public String getMsgText() {
        super.updateMsg((short)(tableNumber&0xFF), 29);
        super.updateMsg((short)((tableNumber&0xFF00)>>>8), 30);
        super.updateMsg((short)((tableOffset&0xFF)), 31);
        super.updateMsg((short)((tableOffset&0xFF00)>>>8), 32);
        super.updateMsg((short)((tableOffset&0xFF0000)>>>16),33);
        super.updateMsg((short)(bytesSent), 34);
        for ( int i=0; i<bytesSent; i++ ) {
            super.updateMsg((short)charArr[i], 35+i);
        }
        return super.getMsgText();
    }
}
