package com.cannontech.common.device.data.collection.message;

import java.io.Serializable;

import org.joda.time.Instant;

public class RecalculationRequest implements Serializable{
    
    private static final long serialVersionUID = 1L;
    private Instant collectionTime;
    public RecalculationRequest(Instant collectionTime) {
        this.collectionTime = collectionTime;
    }
    public Instant getCollectionTime() {
        return collectionTime;
    }
}
