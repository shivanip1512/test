package com.cannontech.web.stars.virtualDevice;

import org.apache.logging.log4j.Logger;
import org.springframework.core.convert.converter.Converter;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.PaoModelFactory;
import com.cannontech.common.device.virtualDevice.VirtualDeviceBaseModel;
import com.cannontech.common.pao.PaoType;

/**
 * Converter class for Virtual Device Base Model.
 * This will take virtual device type as string and return the appropriate object.
 * Converter is required when a inherited class have to be passed for a base class input.
 */
public class VirtualDeviceBaseConverter implements Converter<String, VirtualDeviceBaseModel> {
    private static final Logger log = YukonLogManager.getLogger(VirtualDeviceBaseConverter.class);

    @Override
    public VirtualDeviceBaseModel convert(String deviceType) {
        PaoType paoType = null;
        try {
            paoType = PaoType.valueOf(deviceType);
        } catch (IllegalArgumentException e) {
            log.error(deviceType + " pao type doesn't match with existing pao types", e);
        }
        return (VirtualDeviceBaseModel) PaoModelFactory.getModel(paoType);
    }
}