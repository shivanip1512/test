package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.type.InputOption;
import com.google.common.base.CaseFormat;

@Component
public final class DisconnectMode implements DeviceConfigurationInputEnumeration {

    private static final String baseKey = "yukon.web.modules.tools.configs.enum.disconnectMode.";

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    public enum ConfigurationType implements DisplayableEnum {
        ON_DEMAND,
        DEMAND_THRESHOLD,
        CYCLING;


        @Override
        public String getFormatKey() {
            return baseKey + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name()) ;
        }
    }

    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {

        MessageSourceAccessor messageAccessor = messageResolver.getMessageSourceAccessor(userContext);

        List<InputOption> configTypes = new ArrayList<>();

        for (ConfigurationType configType : ConfigurationType.values()) {
            configTypes.add(new InputOption(configType.name(), messageAccessor.getMessage(configType)));
        }
        return configTypes;
    }

    @Override
    public String getEnumOptionName() {
        return "DisconnectMode";
    }

    @Override
    public SelectionType getSelectionType() {
        return SelectionType.SWITCH;
    }
}