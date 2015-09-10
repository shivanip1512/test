package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.List;

import org.springframework.stereotype.Component;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.type.InputOption;
import com.google.common.collect.Lists;

@Component
public final class Centron420DisplayItemEnumeration extends CentronDisplayItemEnumeration {

    private static final List<Item> supportedItems = Lists.newArrayList(Item.values());  // 420 supports all metrics, no need to filter
    
    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        return getDisplayableValues(userContext, supportedItems);
    }

    @Override
    public String getEnumOptionName() {
        return "Centron420DisplayItem";
    }
}