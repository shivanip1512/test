package com.cannontech.services.systemDataPublisher.processor.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cannontech.services.systemDataPublisher.processor.SystemDataProcessor;
import com.cannontech.services.systemDataPublisher.yaml.model.DictionariesField;

@Service
public class NetworkManagerDataProcessor implements SystemDataProcessor {

    @Override
    public void process(List<DictionariesField> dictionaries) {
        // TODO Depends on if we follow the approach where we will send the query to NM using Broker 
        // to fetch the data from NM database .The method implementation can involve how to send the 
        // queries to NM using queue and based on response form JSON to be sent to interested consumers.
        // If we will follow the approach of having a Network manager publisher at NM side, then
        // we don't need this class.
    }

}
