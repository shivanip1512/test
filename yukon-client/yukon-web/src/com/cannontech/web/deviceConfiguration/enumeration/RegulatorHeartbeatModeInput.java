package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

        return Arrays.stream(RegulatorHeartbeatMode.values())
                .map(mode -> new InputOption(mode.name(), messageAccessor.getMessage(mode)))
                .collect(Collectors.toList());
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
