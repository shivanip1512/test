package com.cannontech.web.amr.meter.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.search.model.MspSearchField;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.web.amr.meter.service.MspMeterSearchMethodResultProvider;

public class MspMeterSearchServiceImplV3 extends MspMeterSearchServiceImpl {

    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MultispeakDao multispeakDao;

    @PostConstruct
    public void init() throws Exception {
        int vendorId = multispeakFuncs.getPrimaryCIS();
        if (vendorId > 0 && multispeakFuncs.getEndPointInterfaceVersion(vendorId, MultispeakDefines.CB_Server_STR).equals(
            MultiSpeakVersion.V3.getVersion())) {
            for (MspMeterSearchMethodResultProvider methodResultProvider : methodResultProviders) {
                if (methodResultProvider.getMspVersion() == MultiSpeakVersion.V3) {
                    methodResultProviderMap.put(methodResultProvider.getSearchField(), methodResultProvider);
                }
            }
            loadMspSearchFields(vendorId);
        }
    }

    @Override
    public void loadMspSearchFields(int vendorId) {
        MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(vendorId);
        mspSearchFields.clear();
        if (vendorId > 0) {
            if (mspVendor.getMspInterfaceMap().containsKey(MultispeakDefines.CB_Server_STR)) {
                List<String> mspMethodNames = mspObjectDao.findMethods(MultispeakDefines.CB_Server_STR, mspVendor);
                MspSearchField[] allMspSearchFields = MspSearchField.values();
                for (MspSearchField mspSearchField : allMspSearchFields) {

                    for (String mspMethodName : mspMethodNames) {

                        if (mspSearchField.getRequiredMspMethodName().equalsIgnoreCase(mspMethodName)) {

                            if (!methodResultProviderMap.keySet().contains(mspSearchField)) {
                                throw new IllegalArgumentException("MspSearchField (" + mspSearchField
                                    + ") has no associated MspMeterSearchMethodResultProvider");
                            }

                            mspSearchFields.add(mspSearchField);
                            break;
                        }
                    }
                }
            }
        }
    }
}
