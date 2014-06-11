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
public final class TimeZoneInput implements DeviceConfigurationInputEnumeration {

    private static final String baseKey = "yukon.web.modules.tools.configs.enum.timeZone";
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    public enum TimeZone implements DisplayableEnum {
        NORONHA("-2", ".noronha"),
        SAO_PAULO("-3", ".saoPaulo"),
        MANAUS("-4", ".manaus"),
        EST("-5", ".newYork"),
        CST("-6", ".chicago"),
        MST("-7", ".denver"),
        PST("-8", ".losAngeles"),
        ALASKA("-9", ".anchorage"),
        HAWAII("-10", ".honolulu");

        private final String dbValue;
        private final String messageKey;

        private TimeZone(String dbValue, String messageKey) {
            this.dbValue = dbValue;
            this.messageKey = messageKey;
        }

        @Override
        public String getFormatKey() {
            return baseKey + messageKey;
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
}