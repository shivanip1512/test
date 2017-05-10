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
public class RegulatorHeartbeatModeInput implements DeviceConfigurationInputEnumeration {

    private static final String baseKey = "yukon.web.modules.tools.configs.enum.regulatorHeartbeatMode.";
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    public enum RegulatorHeartbeatMode implements DisplayableEnum {
        NONE,
        INCREMENT,
        COUNTDOWN
        ;

        @Override
        public String getFormatKey() {
            return baseKey + name();
        }
    }

    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        MessageSourceAccessor messageAccessor = messageResolver.getMessageSourceAccessor(userContext);

        List<InputOption> modes = new ArrayList<>();

        for (RegulatorHeartbeatMode mode : RegulatorHeartbeatMode.values()) {
            modes.add( new InputOption( mode.name(), messageAccessor.getMessage(mode)));
        }
        return modes;
    }

    @Override
    public String getEnumOptionName() {
        return "RegulatorHeartbeatMode";
    }

    @Override
    public SelectionType getSelectionType() {
        return SelectionType.SWITCH;
    }
}
