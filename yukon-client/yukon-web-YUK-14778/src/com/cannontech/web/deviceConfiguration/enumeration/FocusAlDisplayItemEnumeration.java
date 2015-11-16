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
public final class FocusAlDisplayItemEnumeration implements DeviceConfigurationInputEnumeration {

    private static final String baseKey = "yukon.web.modules.tools.configs.enum.focusAlDisplayItem.";

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    public enum FocusAlDisplayItemEnumerationType implements DisplayableEnum {
        SLOT_DISABLED,
        DELIVERED_KWH,
        REVERSE_KWH,
        TOTAL_KWH,
        NET_KWH,
        DIAGNOSTIC_FLAGS,
        ALL_SEGMENTS,
        FIRMWARE_VERSION;

        private final String messageKey;

        private FocusAlDisplayItemEnumerationType() {
            this.messageKey = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.name());
        }

        @Override
        public String getFormatKey() {
            return baseKey + messageKey ;
        }
    }

    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        MessageSourceAccessor messageAccessor = messageResolver.getMessageSourceAccessor(userContext);

        List<InputOption> focusAlDisplayItems = new ArrayList<>();
        for (FocusAlDisplayItemEnumerationType item : FocusAlDisplayItemEnumerationType.values()) {
            focusAlDisplayItems.add(new InputOption(item.name(), messageAccessor.getMessage(item)));
        }

        return focusAlDisplayItems;
    }

    @Override
    public String getEnumOptionName() {
        return "FocusAlDisplayItem";
    }
}