package com.cannontech.stars.dr.jms.message;

import org.joda.time.Instant;

public class OptOutOptInJmsMessage extends DrJmsMessage {

    private static final long serialVersionUID = 1L;
    private int inventoryId;
    private Instant stopDate;
    private Instant startDate;

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

}
