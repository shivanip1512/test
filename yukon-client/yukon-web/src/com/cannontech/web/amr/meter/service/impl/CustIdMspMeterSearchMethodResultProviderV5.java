package com.cannontech.web.amr.meter.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.search.model.MspSearchField;
import com.cannontech.msp.beans.v5.multispeak.ElectricMeter;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.dao.v5.MspObjectDao;
import com.cannontech.web.amr.meter.service.MspMeterSearchMethodResultProvider;

public class CustIdMspMeterSearchMethodResultProviderV5 implements MspMeterSearchMethodResultProvider {
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired protected MultispeakDao multispeakDao;
    @Autowired MultispeakFuncs multispeakFuncs;

    @Override
    public MspSearchField getSearchField() {
        return MspSearchField.CUSTOMER_ID;
    }

    @Override
    public List<String> getMeterNumbers(String filterValue) {

        List<String> meterNumbers = new ArrayList<>();
        List<String> customerIDs = new ArrayList<>();
        customerIDs.add(filterValue);
        MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(multispeakFuncs.getPrimaryCIS());
        List<ElectricMeter> meters = mspObjectDao.getMetersByCustomerIDs(customerIDs, mspVendor);
        for (ElectricMeter meter : meters) {
            meterNumbers.add(meter.getPrimaryIdentifier().getValue());
        }

        return meterNumbers;
    }

    @Override
    public MultiSpeakVersion getMspVersion() {
        return MultiSpeakVersion.V5;
    }
}
