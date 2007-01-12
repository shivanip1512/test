package com.amdswireless.messages.tx;

public class TxSetCompanyMeterNumber extends TxMsg {

	private static final long serialVersionUID = 1L;
	private String companyMeterNumber;

    public TxSetCompanyMeterNumber() {
        super();
        try {
            super.setCommand((short)0x54);
            super.setLength((short)0x24);
            super.setAppCode((short)0x7);
            super.setMiscTx((short)0x1);
            super.setCommandType((short)13);
            super.setNcId((short)0x01);
            super.setToi(new java.util.Date().getTime()/1000);
            super.setTgbId(0x0E);
        } catch ( Exception ex ) {
        }
        this.companyMeterNumber = new String();
    }

    public String getMsgText() {
    	char[] charsToWrite = new char[13];
    	char[] newMeterId = companyMeterNumber.toCharArray();
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

	public String getCompanyMeterNumber() {
		return companyMeterNumber;
	}

	public void setCompanyMeterNumber(String companyMeterNumber) {
		this.companyMeterNumber = companyMeterNumber;
	}
}
