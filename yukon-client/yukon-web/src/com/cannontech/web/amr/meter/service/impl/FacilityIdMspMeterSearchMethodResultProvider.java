package com.cannontech.web.amr.meter.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.search.model.MspSearchField;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.msp.beans.v3.Meter;
import com.cannontech.web.amr.meter.service.MspMeterSearchMethodResultProvider;

public class FacilityIdMspMeterSearchMethodResultProvider implements MspMeterSearchMethodResultProvider {
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired protected MultispeakDao multispeakDao;
    @Autowired MultispeakFuncs multispeakFuncs;
    
	@Override
	public MspSearchField getSearchField() {
		return MspSearchField.FACILITY_ID;
	}
	
	@Override
	public List<String> getMeterNumbers(String filterValue) {
		
        List<String> meterNumbers = new ArrayList<>();

        MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(multispeakFuncs.getPrimaryCIS());
		List<Meter> meters = mspObjectDao.getMspMetersByFacilityId(filterValue, mspVendor);
		for (Meter meter : meters) {
			meterNumbers.add(meter.getMeterNo());
		}
		
		return meterNumbers;
	}
	
    @Override
    public MultiSpeakVersion getMspVersion() {
        return MultiSpeakVersion.V3;
    }
}
