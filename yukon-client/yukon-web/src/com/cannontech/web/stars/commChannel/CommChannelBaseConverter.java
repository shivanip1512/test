package com.cannontech.web.stars.commChannel;

import org.apache.logging.log4j.Logger;
import org.springframework.core.convert.converter.Converter;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.PaoModelFactory;
import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.pao.PaoType;

/**
 * Converter class for port base.
 * This will take port type as string and return the appropriate object.
 * Converter is required when a inherited class have to be passed for a base class input.
 */
public class CommChannelBaseConverter implements Converter<String, PortBase> {
    private static final Logger log = YukonLogManager.getLogger(CommChannelBaseConverter.class);

    @Override
    public PortBase convert(String portType) {
        PaoType paoType = null;
        try {
            paoType = PaoType.valueOf(portType);
        } catch (IllegalArgumentException e) {
            log.error(portType + " pao type doesn't match with existing pao types", e);
        }
        return (PortBase) PaoModelFactory.getModel(paoType);
    }
}