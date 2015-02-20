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
public final class TimeZoneInput implements DeviceConfigurationInputEnumeration {

    private static final String baseKey = "yukon.web.modules.tools.configs.enum.timeZone.";
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    public enum TimeZone implements DisplayableEnum {
        NORONHA("-2"),
        SAO_PAULO("-3"),
        MANAUS("-4"),
        NEW_YORK("-5"),
        CHICAGO("-6"),
        DENVER("-7"),
        LOS_ANGELES("-8"),
        ANCHORAGE("-9"),
        HONOLULU("-10");

        private final String dbValue;

        private TimeZone(String dbValue) {
            this.dbValue = dbValue;
        }

        @Override
        public String getFormatKey() {
            return baseKey + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.name());
        }
    }

    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        MessageSourceAccessor messageAccessor = messageResolver.getMessageSourceAccessor(userContext);

        List<InputOption> timeZones = new ArrayList<>();

        for (TimeZone zone : TimeZone.values()) {
            timeZones.add( new InputOption( zone.dbValue, messageAccessor.getMessage(zone)));
        }
        return timeZones;
    }

    @Override
    public String getEnumOptionName() {
        return "TimeZoneInput";
    }

    @Override
    public SelectionType getSelectionType() {
        return SelectionType.CHOSEN;
    }
}