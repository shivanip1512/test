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

@Component
public final class Read implements DeviceConfigurationInputEnumeration {

    private static final String baseKey = "yukon.web.modules.tools.configs.enum.read.";

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    public enum ReadType implements DisplayableEnum {
        INTERVAL,
        MIDNIGHT,
        DISABLED;

        @Override
        public String getFormatKey() {
            return baseKey + name();
        }

    }

    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        MessageSourceAccessor messageAccessor = messageResolver.getMessageSourceAccessor(userContext);

        List<InputOption> displayableReadTypes = new ArrayList<>();

        for (ReadType readType : ReadType.values()) {
            displayableReadTypes.add( new InputOption( readType.name(), messageAccessor.getMessage(readType)));
        }
        return displayableReadTypes;
    }

    @Override
    public String getEnumOptionName() {
        return "ReadType";
    }

    @Override
    public SelectionType getSelectionType() {
        return SelectionType.SWITCH;
    }
}