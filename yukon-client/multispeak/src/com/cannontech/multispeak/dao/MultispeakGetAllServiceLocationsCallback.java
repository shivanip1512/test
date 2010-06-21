package com.cannontech.multispeak.dao;

import java.util.List;

import com.cannontech.multispeak.deploy.service.ServiceLocation;

public interface MultispeakGetAllServiceLocationsCallback {

	public void processServiceLocations(List<ServiceLocation> mspServiceLocations);
	public boolean isCanceled();
	public void finish();
}
