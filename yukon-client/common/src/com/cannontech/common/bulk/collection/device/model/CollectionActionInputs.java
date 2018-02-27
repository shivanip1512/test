package com.cannontech.common.bulk.collection.device.model;

import java.util.LinkedHashMap;

public class CollectionActionInputs {
    
    //collection actions selected by the user
    private DeviceCollection collection;
    //Inputs selected by the user
    //Example: Attributes(s):Blink Count, Delivered Demand
    private LinkedHashMap<String, String> inputs;
    
    public CollectionActionInputs(DeviceCollection collection, LinkedHashMap<String, String> inputs) {
        this.collection = collection;
        this.inputs = inputs;
    }

    public DeviceCollection getCollection() {
        return collection;
    }

    public LinkedHashMap<String, String> getInputs() {
        return inputs;
    }
  
}
