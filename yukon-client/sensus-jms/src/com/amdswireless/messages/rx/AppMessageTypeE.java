package com.amdswireless.messages.rx;


public class AppMessageTypeE extends DataMessage implements AppMessage, java.io.Serializable {
	private transient static final long serialVersionUID = 1L;
	private final String msgClass="TIER";
	private int tier;

	public AppMessageTypeE( char[] msg ) {
		super(msg);
		super.setMessageType(0xE);
		int offset=31;
		this.tier = ((int)(msg[0+offset] & 0x7 ));
	}

	public int getTier() {
		return this.tier;
	}

    public int getSubtier() {
        return 0;
    }
    public String getMsgClass() {
    	return this.msgClass;
    }

}
