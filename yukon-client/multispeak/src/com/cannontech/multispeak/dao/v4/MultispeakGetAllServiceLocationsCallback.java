package com.cannontech.multispeak.dao.v4;

import java.util.List;

import com.cannontech.msp.beans.v4.ServiceLocation;


public interface MultispeakGetAllServiceLocationsCallback {

    public void processServiceLocations(List<ServiceLocation> mspServiceLocations);

    public boolean isCanceled();

    public void finish();

}
