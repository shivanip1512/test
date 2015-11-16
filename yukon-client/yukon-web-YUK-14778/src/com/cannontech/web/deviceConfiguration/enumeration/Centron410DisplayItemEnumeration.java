package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.List;

import org.springframework.stereotype.Component;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.type.InputOption;

@Component
public final class Centron410DisplayItemEnumeration extends CentronDisplayItemEnumeration {

    private static final List<Item> supportedItems = Item.get410Values();
    
    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        return getDisplayableValues(userContext, supportedItems);
    }

    @Override
    public String getEnumOptionName() {
        return "Centron410DisplayItem";
    }
}