package com.cannontech.services.iot.processor.impl;

import java.util.List;
import org.springframework.stereotype.Service;

import com.cannontech.services.iot.processor.IOTProcessor;
import com.cannontech.services.iot.yaml.model.DictionariesField;

@Service
public class IOTNetworkManagerProcessor implements IOTProcessor {

    @Override
    public void execute(List<DictionariesField> dictionaries) {
        // TODO Depends on if we follow the approach where we will send the query to NM using Broker 
        // to fetch the data from NM database .The method implementation can involve how to send the 
        // queries to NM using queue and based on response form Json to be sent to IOT service.
        // If we will follow the approach of having a Network manager publisher at NM side, then
        // we don't need this class.
    }

}
