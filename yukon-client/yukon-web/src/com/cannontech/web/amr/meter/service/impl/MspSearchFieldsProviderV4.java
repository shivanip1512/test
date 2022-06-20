package com.cannontech.web.amr.meter.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.search.model.MspSearchField;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.v4.MspObjectDao;
import com.cannontech.web.amr.meter.service.MspSearchFieldsProvider;

public class MspSearchFieldsProviderV4 implements MspSearchFieldsProvider {
    @Autowired private MspObjectDao mspObjectDao;

    @Override
    public Set<MspSearchField> loadMspSearchFields(MultispeakVendor mspVendor,
            Set<MspSearchField> supportedMspSearchFields) {
        Set<MspSearchField> mspSearchFields = new HashSet<>();
        List<String> mspMethodNames = mspObjectDao.findMethods(MultispeakDefines.CB_Server_STR, mspVendor);
        MspSearchField[] allMspSearchFields = MspSearchField.values();
        for (MspSearchField mspSearchField : allMspSearchFields) {

            for (String mspMethodName : mspMethodNames) {

                if (mspSearchField.getRequiredMspMethodNameV4().equalsIgnoreCase(mspMethodName)) {

                    if (!supportedMspSearchFields.contains(mspSearchField)) {
                        throw new IllegalArgumentException("MspSearchField (" + mspSearchField
                                + ") has no associated MspMeterSearchMethodResultProvider");
                    }
                    mspSearchFields.add(mspSearchField);
                    break;
                }
            }
        }
        return mspSearchFields;
    }
}