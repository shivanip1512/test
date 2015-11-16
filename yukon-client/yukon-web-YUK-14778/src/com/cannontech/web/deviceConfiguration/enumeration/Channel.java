package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.type.InputOption;

@Component
public final class Channel implements DeviceConfigurationInputEnumeration {

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        MessageSourceAccessor messageAccessor = messageResolver.getMessageSourceAccessor(userContext);

        List<InputOption> displayableChannels = new ArrayList<>();

        for (BuiltInAttribute attribute : BuiltInAttribute.values()) {
            displayableChannels.add(new InputOption(attribute.name(), messageAccessor.getMessage(attribute)));
        }

        return displayableChannels;
    }

    @Override
    public String getEnumOptionName() {
        return "ChannelType";
    }
}