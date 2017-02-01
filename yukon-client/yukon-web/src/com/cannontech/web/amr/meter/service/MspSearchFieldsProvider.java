package com.cannontech.web.amr.meter.service;

import java.util.Set;

import com.cannontech.amr.meter.search.model.MspSearchField;
import com.cannontech.multispeak.client.MultispeakVendor;

public interface MspSearchFieldsProvider {
    public Set<MspSearchField> loadMspSearchFields(MultispeakVendor mspVendor, Set<MspSearchField> MspSearchFields);
}
