package com.cannontech.message.porter.message;

import java.util.Collection;
import java.util.Set;

public class DynamicPaoInfoRequest {
    
    private int deviceID;
    private Set<DynamicPaoInfoKeyEnum> keys;

    public DynamicPaoInfoRequest() { }
    
    public DynamicPaoInfoRequest(int deviceID, DynamicPaoInfoKeyEnum key) {
        this.deviceID = deviceID;
        keys.add(key);
    }
    
    public DynamicPaoInfoRequest(int deviceID, Collection<DynamicPaoInfoKeyEnum> keys) {
        this.deviceID = deviceID;
        this.keys.addAll(keys);
    }
    
    public int getDeviceID() {
        return deviceID;
    }
    
    public void setDeviceID(int deviceID) {
        this.deviceID = deviceID;
    }
    
    public Collection<DynamicPaoInfoKeyEnum> getKeys() {
        return keys;
    }
    
    public void setKeys(Collection<DynamicPaoInfoKeyEnum> keys) {
        this.keys.clear();
        this.keys.addAll(keys);
    }
    
    @Override
    public String toString() {
        return String.format("Request [deviceID=%s, keys=%s]", deviceID, keys);
    }
    
}