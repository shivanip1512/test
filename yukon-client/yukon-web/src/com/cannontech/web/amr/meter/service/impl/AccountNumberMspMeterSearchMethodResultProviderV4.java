package com.cannontech.web.amr.meter.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.search.model.MspSearchField;
import com.cannontech.msp.beans.v4.MspMeter;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.dao.v4.MspObjectDao;
import com.cannontech.web.amr.meter.service.MspMeterSearchMethodResultProvider;

public class AccountNumberMspMeterSearchMethodResultProviderV4 implements MspMeterSearchMethodResultProvider  {
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired protected MultispeakDao multispeakDao;
    @Autowired MultispeakFuncs multispeakFuncs;
    
    @Override
    public MspSearchField getSearchField() {
        return MspSearchField.ACCOUNT_NUMBER;
    }

    @Override
    public List<String> getMeterNumbers(String filterValue) {

        List<String> meterNumbers = new ArrayList<>();

        MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(multispeakFuncs.getPrimaryCIS());
        List<MspMeter> meters = mspObjectDao.getMspMetersByAccountNumber(filterValue, mspVendor);
        for (MspMeter meter : meters) {
            meterNumbers.add(meter.getMeterNo());
        }

        return meterNumbers;
    }

    @Override
    public MultiSpeakVersion version() {
        return MultiSpeakVersion.V4;
    }
}
