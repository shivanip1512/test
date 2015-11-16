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
public final class ReconnectParameter implements DeviceConfigurationInputEnumeration {

    private static final String baseKey = "yukon.web.modules.tools.configs.enum.reconnectType.";

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    public enum ReconnectType implements DisplayableEnum {
        ARM,
        IMMEDIATE;

        @Override
        public String getFormatKey() {
            return baseKey + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.name()) ;
        }
    }

    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        MessageSourceAccessor messageAccessor = messageResolver.getMessageSourceAccessor(userContext);

        List<InputOption> reconnectTypes = new ArrayList<>();

        for (ReconnectType recconnectType : ReconnectType.values()) {
            reconnectTypes.add( new InputOption( recconnectType.name(), messageAccessor.getMessage(recconnectType)));
        }
        return reconnectTypes;
    }

    @Override
    public String getEnumOptionName() {
        return "ReconnectParameter";
    }

    @Override
    public SelectionType getSelectionType() {
        return SelectionType.SWITCH;
    }
}