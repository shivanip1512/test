package com.cannontech.web.amr.meter.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.amr.meter.search.model.MspSearchField;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.deploy.service.Meter;

public class AccountNumberMspMeterSearchMethodResultProvider extends MspMeterSearchMethodResultProviderBase {

	@Override
	public MspSearchField getSearchField() {
		return MspSearchField.ACCOUNT_NUMBER;
	}
	
	@Override
	public List<String> getMeterNumbers(String filterValue) {
		
		List<String> meterNumbers = new ArrayList<String>();
		
		MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(multispeakFuncs.getPrimaryCIS());
		List<Meter> meters = mspObjectDao.getMspMetersByAccountNumber(filterValue, mspVendor);
		for (Meter meter : meters) {
			meterNumbers.add(meter.getMeterNo());
		}
		
		return meterNumbers;
	}
}
