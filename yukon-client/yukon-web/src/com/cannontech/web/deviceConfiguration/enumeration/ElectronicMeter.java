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
public final class ElectronicMeter implements DeviceConfigurationInputEnumeration {

    private static final String baseKey = "yukon.web.modules.tools.configs.enum.electronicMeter.";

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    public enum MeterType implements DisplayableEnum {
        NONE("0"),
        S4("1"),
        ALPHA_A3("2"),
        ALPHA_P_PLUS("3"),
        GEKV("4"),
        GEKV2("5"),
        SENTINEL("6"),
        DNP("7"),
        GEKV2C("8");

        private final String dbValue;

        private MeterType(String dbValue) {
            this.dbValue = dbValue;
        }

        @Override
        public String getFormatKey() {
            return baseKey + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name()) ;
        }
    }

    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {

        MessageSourceAccessor messageAccessor = messageResolver.getMessageSourceAccessor(userContext);
        List<InputOption> displayableValues = new ArrayList<>();

        for (MeterType meterType : MeterType.values()) {
            displayableValues.add( new InputOption(meterType.dbValue, messageAccessor.getMessage(meterType)));
        }

        return displayableValues;
    }

    @Override
    public String getEnumOptionName() {
        return "ElectronicMeter";
    }
}