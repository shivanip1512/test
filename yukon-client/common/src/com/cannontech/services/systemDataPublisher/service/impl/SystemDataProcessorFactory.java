package com.cannontech.services.systemDataPublisher.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.services.systemDataPublisher.processor.SystemDataProcessor;
import com.cannontech.services.systemDataPublisher.processor.impl.NetworkManagerDataProcessor;
import com.cannontech.services.systemDataPublisher.processor.impl.OtherDataProcessor;
import com.cannontech.services.systemDataPublisher.processor.impl.YukonDataProcessor;
import com.cannontech.services.systemDataPublisher.service.SystemDataPublisher;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfiguration;

/**
 * Factory Class to return the processor.
 */
@Service
public class SystemDataProcessorFactory {

    private List<SystemDataProcessor> processors;
    
    @Autowired private YukonDataProcessor yukonDataProcessor;
    @Autowired private NetworkManagerDataProcessor networkManagerDataProcessor;
    @Autowired private OtherDataProcessor otherDataProcessor;

    /**
     * This method will return the processor based on the passed configuration.
     * Processors will be picked based on fields, if no field specific processor are found. 
     * Then generic publisher specific processors will be called.
     */
    public SystemDataProcessor getProcessor(CloudDataConfiguration dictionary) {
        // Find field specific processors.
        SystemDataProcessor selectedProcessor = null;
        for (SystemDataProcessor processor : processors) {
            if (processor.supportsField(dictionary.getField())) {
                return processor;
            }
        }
        // If field specific processor is not found, use generic processors.
        if (dictionary.getDataPublisher() == SystemDataPublisher.YUKON) {
            selectedProcessor = yukonDataProcessor;
        } else if (dictionary.getDataPublisher() == SystemDataPublisher.NETWORK_MANAGER) {
            selectedProcessor = networkManagerDataProcessor;
        } else if (dictionary.getDataPublisher()== SystemDataPublisher.OTHER) {
            selectedProcessor = otherDataProcessor;
        }
        return selectedProcessor;
    }

    @Autowired
    void setProcessor(List<SystemDataProcessor> processors) {
        this.processors = processors;
    }
}
