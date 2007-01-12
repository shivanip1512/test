package com.amdswireless.messages.tx;



public class TxSingleTierReadMsg extends TxPingMsg {
	private static final long serialVersionUID = 1L;
	private short tier;
    private short subtier;

    public TxSingleTierReadMsg() {
        super();
	    try {
		    super.setPingType((short)0xB);
	    } catch ( PingTypeInvalid ex ) {
            System.err.println("Ping type is invalid:  "+ex );
        }
        tier=0x0;
        subtier=0x0;
    }

    public void setTier(short t)  {
	    tier=t;
    }

    public void setSubtier(short t) {
        subtier=t;
    }
    
    public int getSubtier() {
	return this.subtier;
    }

    public int getTier() {
	return this.tier;
    }

    public String getMsgText() {
        short b = (short)(tier+(subtier<<3));
        super.updateMsg(b, 30);
        return super.getMsgText();
    }
}
