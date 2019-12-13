package com.cannontech.stars.dr.jms.notification.message;

import java.io.Serializable;

import org.joda.time.Instant;

public class OptOutInNotificationMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    private int inventoryId;
    private Instant stopDate;
    private Instant startDate;
    private DRNotificationMessageType messageType;

    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Instant getStopDate() {
        return stopDate;
    }

    public void setStopDate(Instant stopDate) {
        this.stopDate = stopDate;
    }

    public Instant getStartDate() {
		return startDate;
	}

	public void setStartDate(Instant startDate) {
		this.startDate = startDate;
	}

	public DRNotificationMessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(DRNotificationMessageType messageType) {
        this.messageType = messageType;
    }

}
