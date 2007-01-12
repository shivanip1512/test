package com.amdswireless.messages.tx;

public class TxVoltageQualityMsg extends TxMsg {

    private static final long serialVersionUID = 1L;
    private final int appbase=29; 
    private int minimumClickCountDuration;
    private int minimumVoltageThreshold;
    private int voltageAveragingWindow;
    private int outageTimeThreshold;
    private int restoredTimeThreshold;
    /*
     * 8        Minimum Click Count Duration
     * 9 (0:5)  Minimum Voltage Threshold
     * 9 (6:7)  Voltage Averaging Window
     * 10 (0:3) Outage Time Threshold
     * 10 (4:7) Restored Time Threshold
       */

    public TxVoltageQualityMsg() {
        super();
        try {
            super.setCommand((short)0x54);
            super.setLength((short)0x24);
            super.setAppCode((short)0x7);
            super.setMiscTx((short)0x1);
            super.setCommandType((short)0x4);
            super.setNcId((short)0x01);
            super.setToi(new java.util.Date().getTime()/1000);
        } catch ( Exception ex ) {
            System.err.println("Error initializing TxDisplayMsg:  "+ex );
        }
    }

    public String getMsgText() {
        super.updateMsg((short)minimumClickCountDuration, appbase );
        super.updateMsg((short)((voltageAveragingWindow<<6) + minimumVoltageThreshold), appbase+1 );
        super.updateMsg((short)((restoredTimeThreshold<<4) + outageTimeThreshold), appbase+2 );
        return super.getMsgText();
    }


    public int getMinimumClickCountDuration() {
		return minimumClickCountDuration;
	}

    public void setMinimumClickCountDuration(int minimumClickCountDuration) {
		this.minimumClickCountDuration = minimumClickCountDuration;
	}

    public int getMinimumVoltageThreshold() {
		return minimumVoltageThreshold;
	}

	public void setMinimumVoltageThreshold(int minimumVoltageThreshold) {
		this.minimumVoltageThreshold = minimumVoltageThreshold;
	}

    public int getVoltageAveragingWindow() {
		return voltageAveragingWindow;
	}

	public void setVoltageAveragingWindow(int voltageAveragingWindow) {
		this.voltageAveragingWindow = voltageAveragingWindow;
	}

    public int getOutageTimeThreshold() {
		return outageTimeThreshold;
	}

	public void setOutageTimeThreshold(int outageTimeThreshold) {
		this.outageTimeThreshold = outageTimeThreshold;
	}

    public int getRestoredTimeThreshold() {
		return restoredTimeThreshold;
	}

	public void setRestoredTimeThreshold(int restoredTimeThreshold) {
		this.restoredTimeThreshold = restoredTimeThreshold;
	}
}
