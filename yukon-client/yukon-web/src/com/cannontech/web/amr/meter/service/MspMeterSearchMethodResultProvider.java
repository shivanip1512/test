package com.cannontech.web.amr.meter.service;

import java.util.List;

import com.cannontech.amr.meter.search.model.MspSearchField;
import com.cannontech.multispeak.client.MultiSpeakVersionable;

public interface MspMeterSearchMethodResultProvider extends MultiSpeakVersionable {

    public List<String> getMeterNumbers(String filterValue);

    public MspSearchField getSearchField();
}
