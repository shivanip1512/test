package com.amdswireless.messages.tx;



public class TxConfigMsg extends TxMsg {
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		public int tgbId = 0;

    public TxConfigMsg() {
        super();
        try {
            super.setCommand((short)0x54);
            super.setLength((short)0x24);
            super.setAppCode((short)0x7);
            super.setMiscTx((short)0x1);
            super.setCommandType((short)0x1);
            super.setNcId((short)0x01);
            super.setToi(new java.util.Date().getTime()/1000);
            super.setTgbId(0x0E);
        } catch ( Exception ex ) {
        }
    }


    public String getMsgText() {
        super.updateMsg((short)0x5, 29);
        super.updateMsg((short)0xa, 30);
        super.updateMsg((short)0x2, 31);
        super.updateMsg((short)0x6, 32);
        super.updateMsg((short)0x3, 33);
        super.updateMsg((short)0x2, 34);
        super.updateMsg((short)0x0, 35);
        super.updateMsg((short)0x3, 36);
        super.updateMsg((short)0x2, 37);
        super.updateMsg((short)0xe, 38);
        super.updateMsg((short)0x0, 39);
        super.updateMsg((short)0x3, 40);
        super.updateMsg((short)0x3, 41);
        super.updateMsg((short)0x6, 42);
        super.updateMsg((short)0x0, 43);
        super.updateMsg((short)0x3, 44);
        super.updateMsg((short)0x3, 45);
        super.updateMsg((short)0xa, 46);
        super.updateMsg((short)0x0, 47);
        super.updateMsg((short)0x3, 48);
        super.updateMsg((short)0x9, 49);
        super.updateMsg((short)0x2, 50);
        super.updateMsg((short)0x1, 51);
        super.updateMsg((short)0xb, 52);
        super.updateMsg((short)0x3, 53);
        super.updateMsg((short)0x6, 54);
        super.updateMsg((short)0x0, 55);
        super.updateMsg((short)0x3, 56);
        super.updateMsg((short)0x0, 57);
        super.updateMsg((short)0x0, 58);
        super.updateMsg((short)0x0, 59); // xmit rate, 00 - normal
        super.updateMsg((short)0x0, 60); // xmit rate, 00 - normal
        return super.getMsgText();
    }

    public int getTgbId() {
        return tgbId;
    }

    public void setTgbId() {
				try{
          super.setTgbId(tgbId);
        } catch ( Exception ex ) {
        }
    }
}
