package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.deviceConfiguration.enumeration.DeviceConfigurationInputEnumeration.SelectionType;
import com.cannontech.web.input.type.InputOption;
import com.google.common.base.CaseFormat;

@Component
public final class DnpTimeOffset implements DeviceConfigurationInputEnumeration {

    private static final String baseKey = "yukon.web.modules.tools.configs.enum.dnpTimeOffset.";
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    public enum Offsets implements DisplayableEnum {
        UTC,
        LOCAL,
        LOCAL_NO_DST;

        @Override
        public String getFormatKey() {
            return baseKey + name();
        }
    }

    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        MessageSourceAccessor messageAccessor = messageResolver.getMessageSourceAccessor(userContext);

        List<InputOption> timeZones = new ArrayList<>();

        for (Offsets zone : Offsets.values()) {
            timeZones.add( new InputOption( zone.name(), messageAccessor.getMessage(zone)));
        }
        return timeZones;
    }

    @Override
    public String getEnumOptionName() {
        return "DnpTimeOffset";
    }

    @Override
    public SelectionType getSelectionType() {
        return SelectionType.SWITCH;
    }
}