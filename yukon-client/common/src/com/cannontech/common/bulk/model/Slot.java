package com.cannontech.common.bulk.model;

import org.joda.time.Instant;

public class Slot {
    private Instant instant;
    private int slotId;
    
    public Slot(Instant instant, int slotId) {
        this.instant = instant;
        this.slotId = slotId;
    }

    public Instant getInstant() {
        return instant;
    }

    public int getSlotId() {
        return slotId;
    }
    
}
