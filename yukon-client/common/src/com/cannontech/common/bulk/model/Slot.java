package com.cannontech.common.bulk.model;

import org.joda.time.Instant;

public class Slot {
    private Instant dateTime;
    private int slotId;
    
    public Slot(Instant dateTime, int slotId) {
        this.setDateTime(dateTime);
        this.setSlotId(slotId);
    }

    public void setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public Instant getDateTime() {
        return dateTime;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public int getSlotId() {
        return slotId;
    }
    
}
