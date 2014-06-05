package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.List;

import org.springframework.stereotype.Component;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.google.common.collect.ImmutableList.Builder;

@Component
public final class Channel implements DeviceConfigurationInputEnumeration {

    private static final List<DisplayableValue> displayableChannels;

    static {
        Builder<DisplayableValue> typeBuilder = new Builder<>();

        for (BuiltInAttribute channel : BuiltInAttribute.values()) {
            typeBuilder.add(new DisplayableValue(channel.name(), channel.getKey()));
        }

        displayableChannels = typeBuilder.build();
    }

    @Override
    public String getEnumOptionName() {
        return "ChannelType";
    }

    @Override
    public List<DisplayableValue> getDisplayableValues() {
        return displayableChannels;
    }
}
