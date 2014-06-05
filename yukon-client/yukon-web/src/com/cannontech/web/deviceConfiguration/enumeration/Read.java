package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.List;

import org.springframework.stereotype.Component;

import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.ImmutableList.Builder;

@Component
public final class Read implements DeviceConfigurationInputEnumeration {
    private static final String baseKey = "yukon.web.modules.tools.configs.enum.read.";
    private static final List<DisplayableValue> displayableReadTypes;

   public enum ReadType implements DisplayableEnum {
        INTERVAL,
        BILLING,
        DISABLED;

        @Override
        public String getFormatKey() {
            return baseKey + name();
        }

    }

    @Override
    public String getEnumOptionName() {
        return "ReadType";
    }

    static {
        Builder<DisplayableValue> typeBuilder = new Builder<>();

        for (ReadType readType : ReadType.values()) {
            typeBuilder.add(new DisplayableValue(readType.name(),readType.getFormatKey()));
        }

        displayableReadTypes = typeBuilder.build();
    }

    @Override
    public List<DisplayableValue> getDisplayableValues() {
        return displayableReadTypes;
    }
}