package com.amdswireless.messages.tx;



public class TxCITableReadMsg extends TxMsg {
	private static final long serialVersionUID = 1L;
	private short tableNumber;
    private int tableOffset;
    private short bytesRequested;

    public TxCITableReadMsg() {
        super();
        try {
            super.setCommand((short)0x54);
            super.setLength((short)0x24);
            super.setAppCode((short)0x7);
            super.setMiscTx((short)0x1);
            super.setCommandType((short)0xF);
            super.setNcId((short)0x01);
            super.setToi(new java.util.Date().getTime()/1000);
        } catch ( Exception ex ) {
        }
        tableNumber=0x0;
        tableOffset=0x0;
        bytesRequested=0x0;
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
    public void setBytesRequested(short s) {
        if ( s > 27 ) {
            s = 27;
            System.err.println("Too many bytes requested!");
        }
        this.bytesRequested=s;
    }
    public short getBytesRequested() {
        return this.bytesRequested;
    }
	    

    public String getMsgText() {
        super.updateMsg((short)(tableNumber&0xFF), 29);
        super.updateMsg((short)((tableNumber&0xFF00)>>>8), 30);
        super.updateMsg((short)((tableOffset&0xFF)), 31);
        super.updateMsg((short)((tableOffset&0xFF00)>>>8), 32);
        super.updateMsg((short)((tableOffset&0xFF0000)>>>16),33);
        super.updateMsg((short)(bytesRequested), 34);
        return super.getMsgText();
    }
}
