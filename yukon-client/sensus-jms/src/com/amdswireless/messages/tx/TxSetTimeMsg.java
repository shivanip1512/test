package com.amdswireless.messages.tx;

import java.util.Calendar;

public class TxSetTimeMsg extends TxMsg {

	private static final long serialVersionUID = 1L;
	private int secondsPastHour;
    private int hoursPastMidnight;

    public TxSetTimeMsg() {
        super();
        try {
            super.setCommand((short)0x54);
            super.setLength((short)0x24);
            super.setAppCode((short)0x7);
            super.setMiscTx((short)0x1);
            super.setCommandType((short)0x05);
            super.setNcId((short)0x01);
            super.setToi(new java.util.Date().getTime()/1000);
            super.setTgbId(0x0E);
        } catch ( Exception ex ) {
        }
        // we need to get the current time and set the values
        // of the message based on that
        Calendar rightNow = Calendar.getInstance();
        this.secondsPastHour = rightNow.get(Calendar.MINUTE)*60;
        this.secondsPastHour += rightNow.get(Calendar.SECOND);
        this.hoursPastMidnight = rightNow.get(Calendar.HOUR_OF_DAY);
    }

    public void setSecondsPastHour( int s ) {
        this.secondsPastHour=s;
    }

    public void setHoursPastMidnight( int h ) {
        this.hoursPastMidnight=h;
    }

    public String getMsgText() {
        // set elapsed time (in seconds) past the top of the hour
        // don't forget, LSB...
        super.updateMsg((short)(secondsPastHour & 0xFF),29 );
        super.updateMsg((short)((secondsPastHour & 0xFF00 ) >>>8),30 );
        // set hour of the day
        super.updateMsg((short)hoursPastMidnight,31);
        // set the read hour (this will almost always be 0)
        super.updateMsg((short)0x00,32);
        return super.getMsgText();
    }
}
