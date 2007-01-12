package com.amdswireless.messages.twoway;

import java.util.Date;



public class TxLPMsg  extends TxPadCommand {
    private static final long serialVersionUID = 1L;
    private int blocksToRead;
    private Date startTime;
    private Date endTime;
    private int pastHours;
    
    public TxLPMsg() {
        blocksToRead=0x0;
    }

    public void setBlocksToRead(int i)  {
        blocksToRead=i;
    }
    public int getBlocksToRead() {
        return blocksToRead;
    }

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public int getPastHours() {
		return pastHours;
	}

	public void setPastHours(int pastHours) {
		this.pastHours = pastHours;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}


}
