package com.cannontech.web.amr.meter.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.amr.meter.search.model.MspSearchField;
import com.cannontech.msp.beans.v3.Meter;
import com.cannontech.multispeak.client.MultispeakVendor;


public class EaLocationMspMeterSearchMethodResultProvider extends MspMeterSearchMethodResultProviderBase {

	@Override
	public MspSearchField getSearchField() {
		return MspSearchField.EA_LOCATION;
	}
	
	@Override
	public List<String> getMeterNumbers(String filterValue) {
		
		List<String> meterNumbers = new ArrayList<String>();
		
		MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(multispeakFuncs.getPrimaryCIS());
		List<Meter> meters = mspObjectDao.getMspMetersByEALocation(filterValue, mspVendor);
		for (Meter meter : meters) {
			meterNumbers.add(meter.getMeterNo());
		}
		
		return meterNumbers;
	}
}
