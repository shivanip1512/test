package com.cannontech.web.amr.meter.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.search.model.MspSearchField;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.web.amr.meter.MspFilterBy;
import com.cannontech.web.amr.meter.service.MspMeterSearchMethodResultProvider;
import com.cannontech.web.amr.meter.service.MspMeterSearchService;

public class MspMeterSearchServiceImpl implements MspMeterSearchService {

    @Autowired private MspSearchFieldsProviderV3 fieldsProviderV3;
    @Autowired private MspSearchFieldsProviderV5 fieldsProviderV5;
    @Autowired private MultispeakDao multispeakDao;
    @Autowired private MultispeakFuncs multispeakFuncs;

    List<MspMeterSearchMethodResultProvider> methodResultProviders;
    private Map<MspSearchField, MspMeterSearchMethodResultProvider> methodResultProviderMap = new HashMap<>();
    private Set<MspSearchField> mspSearchFields = new HashSet<>();

    @PostConstruct
    public void init() throws Exception {
        int vendorId = multispeakFuncs.getPrimaryCIS();
        loadMspSearchFields(vendorId);

    }

    @Override
    public void loadMspSearchFields(int vendorId) {
        if (vendorId > 0) {
            MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(vendorId);
            if (mspVendor.getMspInterfaceMap().containsKey(MultispeakDefines.CB_Server_STR)) {
                if (mspVendor.getMspInterfaceMap().get(MultispeakDefines.CB_Server_STR).getVersion().equals(
                    MultiSpeakVersion.V3.getVersion())) {
                    mspSearchFields = fieldsProviderV3.loadMspSearchFields(mspVendor, methodResultProviderMap.keySet());
                } else {
                    mspSearchFields = fieldsProviderV5.loadMspSearchFields(mspVendor, methodResultProviderMap.keySet());
                }
            }
        }
    }

    @Override
    public List<MspFilterBy> getMspFilterByList() {

        List<MspFilterBy> msFilterByList = new ArrayList<>();
        for (MspSearchField mspSearchField : mspSearchFields) {
            msFilterByList.add(new MspFilterBy(mspSearchField.name(), methodResultProviderMap.get(mspSearchField)));
        }

        return msFilterByList;
    }

    @Autowired
    public void setMethodResultProviders(List<MspMeterSearchMethodResultProvider> methodResultProviders) {

        for (MspMeterSearchMethodResultProvider methodResultProvider : methodResultProviders) {
            if (methodResultProvider.getMspVersion() == MultiSpeakVersion.V3) {
                methodResultProviderMap.put(methodResultProvider.getSearchField(), methodResultProvider);
            } else {
                methodResultProviderMap.put(methodResultProvider.getSearchField(), methodResultProvider);
            }
        }
    }

}
