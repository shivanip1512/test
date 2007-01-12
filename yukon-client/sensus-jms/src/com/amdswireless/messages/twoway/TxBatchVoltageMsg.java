package com.amdswireless.messages.twoway;

public class TxBatchVoltageMsg extends TxPadCommand {

    private int minimumClickCountDuration;
    private int minimumVoltageThreshold;
    private int voltageAveragingWindow;
    private int outageTimeThreshold;
    private int restoredTimeThreshold;
    
    public boolean hasMinimumClickCountDuration;
    public boolean hasMinimumVoltageThreshold;
    public boolean hasVoltageAveragingWindow;
    public boolean hasOutageTimeThreshold;
    public boolean hasRestoredTimeThreshold;
    

	public TxBatchVoltageMsg() {
        super();
    	hasMinimumClickCountDuration = false;
	    hasMinimumVoltageThreshold = false;
	    hasVoltageAveragingWindow = false;
	    hasOutageTimeThreshold = false;
	    hasRestoredTimeThreshold = false;
    }

	public boolean isComplete() {
		return hasMinimumClickCountDuration && hasMinimumVoltageThreshold && hasOutageTimeThreshold && hasRestoredTimeThreshold;
	}

    public int getMinimumClickCountDuration() {
		return minimumClickCountDuration;
	}

    public void setMinimumClickCountDuration(int minimumClickCountDuration) {
		this.minimumClickCountDuration = minimumClickCountDuration;
		hasMinimumClickCountDuration = true;
	}

    public int getMinimumVoltageThreshold() {
		return minimumVoltageThreshold;
	}

	public void setMinimumVoltageThreshold(int minimumVoltageThreshold) {
		this.minimumVoltageThreshold = minimumVoltageThreshold;
		hasMinimumVoltageThreshold = true;
	}

    public int getVoltageAveragingWindow() {
		return voltageAveragingWindow;
	}

	public void setVoltageAveragingWindow(int voltageAveragingWindow) {
		this.voltageAveragingWindow = voltageAveragingWindow;
		hasVoltageAveragingWindow = true;
	}

    public int getOutageTimeThreshold() {
		return outageTimeThreshold;
	}

	public void setOutageTimeThreshold(int outageTimeThreshold) {
		this.outageTimeThreshold = outageTimeThreshold;
		hasOutageTimeThreshold = true;
	}

    public int getRestoredTimeThreshold() {
		return restoredTimeThreshold;
	}

	public void setRestoredTimeThreshold(int restoredTimeThreshold) {
		this.restoredTimeThreshold = restoredTimeThreshold;
		hasRestoredTimeThreshold = true;
	}
}
