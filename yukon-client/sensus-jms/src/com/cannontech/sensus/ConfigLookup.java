package com.cannontech.sensus;


public interface ConfigLookup {

    // Returns the configuration that matches the serialNumber or null if no such record exists.
    public GpuffConfig getConfigForSerial(int serialNum);
    
    // reset is called to suggest to the interface that it should refresh its backing store. 
    public void reset();
}

