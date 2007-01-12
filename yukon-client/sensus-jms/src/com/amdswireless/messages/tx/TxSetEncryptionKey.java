package com.amdswireless.messages.tx;

public class TxSetEncryptionKey extends TxMsg {

	private static final long serialVersionUID = 1L;
	private String encryptionKey;

    public TxSetEncryptionKey() {
        super();
        try {
            super.setCommand((short)0x54);
            super.setLength((short)0x24);
            super.setAppCode((short)0x7);
            super.setMiscTx((short)0x1);
            super.setCommandType((short)11);
            super.setNcId((short)0x01);
            super.setToi(new java.util.Date().getTime()/1000);
            super.setTgbId(0x0E);
        } catch ( Exception ex ) {
        }
        this.encryptionKey = new String();
    }

    public String getMsgText() {
    	char[] charsToWrite = new char[16];
    	char[] newMeterId = encryptionKey.toCharArray();
    	for ( int i=0; i<newMeterId.length; i++ ) {
    		charsToWrite[i] = newMeterId[i];
    	}
		for ( int i=newMeterId.length; i < charsToWrite.length; i++ ) {
			charsToWrite[i] = 0;
		}
    	int offset = 29;
    	for ( int i=0; i<charsToWrite.length; i++ ) {
    		super.updateMsg((short)(charsToWrite[i]),offset+i );
    	}
    	return super.getMsgText();
    }

	public String getEncrytionKey() {
		return encryptionKey;
	}

	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}
}
