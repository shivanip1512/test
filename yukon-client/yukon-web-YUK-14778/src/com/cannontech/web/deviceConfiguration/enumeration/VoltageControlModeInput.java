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
public class VoltageControlModeInput implements DeviceConfigurationInputEnumeration {

    private static final String baseKey = "yukon.web.modules.tools.configs.enum.voltageControlMode.";
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    public enum VoltageControlMode implements DisplayableEnum {
        DIRECT_TAP,
        SET_POINT,
        ;

        @Override
        public String getFormatKey() {
            return baseKey + name();
        }
    }

    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        MessageSourceAccessor messageAccessor = messageResolver.getMessageSourceAccessor(userContext);

        List<InputOption> timeZones = new ArrayList<>();

        for (VoltageControlMode zone : VoltageControlMode.values()) {
            timeZones.add( new InputOption( zone.name(), messageAccessor.getMessage(zone)));
        }
        return timeZones;
    }

    @Override
    public String getEnumOptionName() {
        return "VoltageControlMode";
    }

    @Override
    public SelectionType getSelectionType() {
        return SelectionType.SWITCH;
    }
}
