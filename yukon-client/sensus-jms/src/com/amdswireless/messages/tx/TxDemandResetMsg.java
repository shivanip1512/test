package com.amdswireless.messages.tx;



public class TxDemandResetMsg extends TxMsg {
	private static final long serialVersionUID = 1L;

	public TxDemandResetMsg() {
        super();
        try {
            super.setCommand((short)0x54);
            super.setLength((short)0x24);
            super.setAppCode((short)0x7);
            super.setMiscTx((short)0x1);
            super.setCommandType((short)0x11);
            super.setNcId((short)0x01);
            super.setToi(new java.util.Date().getTime()/1000);
            super.setTgbId(0x0E);
        } catch ( Exception ex ) {
        }
    }

    public String getMsgText() {
        return super.getMsgText();
    }
}
