package com.amdswireless.messages.tx;

public class TxSetPreferredBuddy extends TxMsg {

	private static final long serialVersionUID = 1L;
	private int preferredBuddyAddress;

    public TxSetPreferredBuddy() {
        super();
        try {
            super.setCommand((short)0x54);
            super.setLength((short)0x24);
            super.setAppCode((short)0x7);
            super.setMiscTx((short)0x1);
            super.setCommandType((short)12);
            super.setNcId((short)0x01);
            super.setToi(new java.util.Date().getTime()/1000);
            super.setTgbId(0x0E);
        } catch ( Exception ex ) {
        }
        this.preferredBuddyAddress = 0;
    }

    public void setPreferredBuddyAddress( int s ) {
        this.preferredBuddyAddress=s;
    }


    public String getMsgText() {
        super.updateMsg((short)(preferredBuddyAddress & 0xFF),29 );
        super.updateMsg((short)((preferredBuddyAddress & 0xFF00 ) >>>8),30 );
        super.updateMsg((short)((preferredBuddyAddress & 0xFF0000 ) >>>16),31 );
        super.updateMsg((short)((preferredBuddyAddress & 0xFF000000 ) >>>24),32 );
        return super.getMsgText();
    }
}
