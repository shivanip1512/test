package com.cannontech.dr.eatonCloud.model;

import java.util.Arrays;

public enum EatonCloudEventStatus {
    RECEIVED(1),
    STARTED(2),
    COMPLETE(3),
    CANCELED(6)
    ;
    
    private final int id;
    
    EatonCloudEventStatus(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
    
    public static EatonCloudEventStatus getById(int id) {
        return Arrays.stream(values())
                .filter(cloudStatus -> cloudStatus.getId() == id)
                .findAny()
                .orElse(null);
    }
}