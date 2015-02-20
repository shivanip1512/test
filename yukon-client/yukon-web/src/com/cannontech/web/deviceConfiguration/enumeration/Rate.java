package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.type.InputOption;

@Component
public final class Rate implements DeviceConfigurationInputEnumeration {

    private enum RateType {
        A,
        B,
        C,
        D;
    }

    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        List<InputOption> rates = new ArrayList<>();

        for (RateType rateType : RateType.values()) {
            rates.add( new InputOption(rateType.name(), rateType.name()));
        }
        return rates;
    }

    @Override
    public String getEnumOptionName() {
        return "Rate";
    }

    @Override
    public SelectionType getSelectionType() {
        return SelectionType.SWITCH;
    }
}