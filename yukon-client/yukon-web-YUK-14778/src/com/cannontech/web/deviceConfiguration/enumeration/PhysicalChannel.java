package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.type.InputOption;

@Component
public final class PhysicalChannel implements DeviceConfigurationInputEnumeration {

    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        List<InputOption> channels = new ArrayList<>();

        for (int i=1; i <= 16; i++) {
            channels.add( new InputOption(Integer.toString(i-1), Integer.toString(i)));
        }
        return channels;
    }

    @Override
    public String getEnumOptionName() {
        return "PhysicalChannel";
    }
}