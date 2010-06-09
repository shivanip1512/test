package com.cannontech.web.amr.meter.service;

import java.util.List;

import com.cannontech.web.amr.meter.MspFilterBy;

public interface MspMeterSearchService {

	public List<MspFilterBy> getMspFilterByList();
	
	/**
	 * Load the Msp Search Fields based on the vendorId.
	 */
	public void loadMspSearchFields(int vendorId);
}
