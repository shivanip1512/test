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
public final class ProfileResolution implements DeviceConfigurationInputEnumeration {

    private static final String baseKey = "yukon.web.modules.tools.configs.enum.whResolution.";

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    /**
     * Enum values represent the order of magnitude of the resolution in Wh.
     */
    public enum Resolution implements DisplayableEnum {
        MINUS_ONE("0.1"),
        ZERO("1.0"),
        ONE("10.0");

        private final String dbValue; // represented as string because of poor double representation.

        private Resolution(String dbValue) {
            this.dbValue = dbValue;
        }

        @Override
        public String getFormatKey() {
            return baseKey + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.name()) ;
        }
    }

    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        MessageSourceAccessor messageAccessor = messageResolver.getMessageSourceAccessor(userContext);

        List<InputOption> resolutions = new ArrayList<>();

        for (Resolution resolution : Resolution.values()) {
            resolutions.add( new InputOption( resolution.dbValue, messageAccessor.getMessage(resolution)));
        }
        return resolutions;
    }

    @Override
    public String getEnumOptionName() {
        return "ProfileResolution";
    }

    @Override
    public SelectionType getSelectionType() {
        return SelectionType.SWITCH;
    }
}