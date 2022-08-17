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
import com.cannontech.multispeak.db.MultispeakInterface;
import com.cannontech.web.amr.meter.MspFilterBy;
import com.cannontech.web.amr.meter.service.MspMeterSearchMethodResultProvider;
import com.cannontech.web.amr.meter.service.MspMeterSearchService;

public class MspMeterSearchServiceImpl implements MspMeterSearchService {

    @Autowired private MspSearchFieldsProviderV3 fieldsProviderV3;
    @Autowired private MspSearchFieldsProviderV5 fieldsProviderV5;
    @Autowired private MultispeakDao multispeakDao;
    @Autowired private MultispeakFuncs multispeakFuncs;

    List<MspMeterSearchMethodResultProvider> methodResultProviders;
    private Map<MspSearchField, MspMeterSearchMethodResultProvider> methodResultProviderMapV3 = new HashMap<>();
    private Map<MspSearchField, MspMeterSearchMethodResultProvider> methodResultProviderMapV5 = new HashMap<>();
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

            MultiSpeakVersion cisVersion = multispeakFuncs.getPrimaryCISVersion(mspVendor);
            if (cisVersion != null) {
                if (cisVersion == MultiSpeakVersion.V3) {
                    mspSearchFields =
                        fieldsProviderV3.loadMspSearchFields(mspVendor, methodResultProviderMapV3.keySet());

                } else if (cisVersion == MultiSpeakVersion.V5) {
                    mspSearchFields =
                        fieldsProviderV5.loadMspSearchFields(mspVendor, methodResultProviderMapV5.keySet());
                }
            }
            
            // another example of the alternate solution for the map
            /*MultispeakInterface primaryCISInterface = 
                    mspVendor.getMspInterfaceMap_NotCannon().get(MultispeakDefines.CB_Server_STR);
            
            if (primaryCISInterface != null) {
                if (primaryCISInterface.getVersion() == MultiSpeakVersion.V3) {
                    mspSearchFields = fieldsProviderV3.loadMspSearchFields(mspVendor, methodResultProviderMap.keySet());
                } else if (primaryCISInterface.getVersion() == MultiSpeakVersion.V5) {
                    mspSearchFields = fieldsProviderV5.loadMspSearchFields(mspVendor, methodResultProviderMap.keySet());
                }
            }*/
        }
    }

    @Override
    public List<MspFilterBy> getMspFilterByList() {
        int vendorId = multispeakFuncs.getPrimaryCIS();
        List<MspFilterBy> msFilterByList = new ArrayList<>();
        if (vendorId > 0) {
            MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(vendorId);

            MultispeakInterface cb_server_v3 =
                mspVendor.getMspInterfaceMap().get(
                    MultispeakVendor.buildMapKey(MultispeakDefines.CB_Server_STR, MultiSpeakVersion.V3));

            if (cb_server_v3 != null) {
                msFilterByList = loadFilterList(MultiSpeakVersion.V3);

            } else {
                msFilterByList = loadFilterList(MultiSpeakVersion.V5);
            }
        }
        return msFilterByList;
    }
    
    private List<MspFilterBy> loadFilterList(MultiSpeakVersion multiSpeakVersion) {
        List<MspFilterBy> msFilterByList = new ArrayList<>();
        if (multiSpeakVersion == MultiSpeakVersion.V3) {
            for (MspSearchField mspSearchField : mspSearchFields) {
                msFilterByList.add(new MspFilterBy(mspSearchField.name(), methodResultProviderMapV3.get(mspSearchField)));
            }
        } else {
            for (MspSearchField mspSearchField : mspSearchFields) {
                msFilterByList.add(new MspFilterBy(mspSearchField.name(), methodResultProviderMapV5.get(mspSearchField)));
            }
        }
        return msFilterByList;
    }

    @Autowired
    public void setMethodResultProviders(List<MspMeterSearchMethodResultProvider> methodResultProviders) {

        for (MspMeterSearchMethodResultProvider methodResultProvider : methodResultProviders) {
            if (methodResultProvider.version() == MultiSpeakVersion.V3) {
                methodResultProviderMapV3.put(methodResultProvider.getSearchField(), methodResultProvider);
            } else {
                methodResultProviderMapV5.put(methodResultProvider.getSearchField(), methodResultProvider);
            }
        }
    }

}
