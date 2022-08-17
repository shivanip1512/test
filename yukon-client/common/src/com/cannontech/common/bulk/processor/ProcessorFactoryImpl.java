package com.cannontech.common.bulk.processor;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.service.DeviceConfigurationService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Default implementation of ProcessorFactory
 */
public class ProcessorFactoryImpl implements ProcessorFactory {

    @Autowired private DeviceConfigurationService deviceConfigurationService = null;
    @Autowired private IDatabaseCache dbCache;

    @Override
    public Processor<SimpleDevice> createAssignConfigurationToYukonDeviceProcessor(final DeviceConfiguration configuration, LiteYukonUser user) {
        return new SingleProcessor<SimpleDevice>() {
            @Override
            public void process(SimpleDevice device) throws ProcessingException {
                try {
                    String deviceName = dbCache.getAllPaosMap().get(device.getPaoIdentifier().getPaoId()).getPaoName();
                    deviceConfigurationService.assignConfigToDevice(configuration, device, user, deviceName);
                } catch (InvalidDeviceTypeException e) {
                    throw new ProcessingException(e.getMessage(), "invalidDeviceType", e, device.getDeviceType(),
                        dbCache.getAllPaosMap().get(device.getPaoIdentifier().getPaoId()).getPaoName());
                }
            }
        };
    }
    
    @Override
    public Processor<SimpleDevice> createUnassignConfigurationToYukonDeviceProcessor(LiteYukonUser user) {
        return new SingleProcessor<SimpleDevice>() {
            @Override
            public void process(SimpleDevice device) throws ProcessingException {
                try {
                    String deviceName = dbCache.getAllPaosMap().get(device.getPaoIdentifier().getPaoId()).getPaoName();
                    deviceConfigurationService.unassignConfig(device, user, deviceName);
                } catch (InvalidDeviceTypeException e) {
                    throw new ProcessingException(e.getMessage(), "deviceRequiresConfig", e, device.getDeviceType(),
                        dbCache.getAllPaosMap().get(device.getPaoIdentifier().getPaoId()).getPaoName());
                }
            }
        };
    }
}
