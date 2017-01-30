package com.cannontech.web.amr.meter.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.search.model.MspSearchField;
import com.cannontech.web.amr.meter.MspFilterBy;
import com.cannontech.web.amr.meter.service.MspMeterSearchMethodResultProvider;
import com.cannontech.web.amr.meter.service.MspMeterSearchService;

public abstract class MspMeterSearchServiceImpl implements MspMeterSearchService {

    @Autowired List<MspMeterSearchMethodResultProvider> methodResultProviders;

    protected static final Map<MspSearchField, MspMeterSearchMethodResultProvider> methodResultProviderMap =
        new HashMap<>();

    protected static final List<MspSearchField> mspSearchFields = new ArrayList<>();;

    /**
     * Load the Msp Search Fields based on the vendorId.
     */
    abstract void loadMspSearchFields(int vendorId);

    @Override
    public List<MspFilterBy> getMspFilterByList() {

        List<MspFilterBy> msFilterByList = new ArrayList<>();
        for (MspSearchField mspSearchField : mspSearchFields) {
            msFilterByList.add(new MspFilterBy(mspSearchField.name(), methodResultProviderMap.get(mspSearchField)));
        }

        return msFilterByList;
    }

}
