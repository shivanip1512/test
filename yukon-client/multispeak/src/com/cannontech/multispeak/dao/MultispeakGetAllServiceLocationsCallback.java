package com.cannontech.multispeak.dao;

import java.util.List;

import com.cannontech.msp.beans.v3.ServiceLocation;

public interface MultispeakGetAllServiceLocationsCallback {

    /**
     * processServiceLocations
     * 
     * @param mspServiceLocations
     */
    public void processServiceLocations(List<ServiceLocation> mspServiceLocations);

    /**
     * Check if Canceled
     * 
     * @return
     */
    public boolean isCanceled();

    /**
     * finish
     * 
     */
    public void finish();
}
