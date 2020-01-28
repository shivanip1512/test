package com.cannontech.multispeak.dao.v5;

import java.util.List;

import com.cannontech.msp.beans.v5.multispeak.ServiceLocation;

public interface MultispeakGetAllServiceLocationsCallback extends MultispeakSynsProcessCallback {

    public void processServiceLocations(List<ServiceLocation> mspServiceLocations);
}
