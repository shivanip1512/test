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
public final class Mct470MeterType implements DeviceConfigurationInputEnumeration {

    private static final String baseKey = "yukon.web.modules.tools.configs.enum.meterType.";

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    public enum MeterType implements DisplayableEnum {
        CHANNEL_NOT_USED("0"),
        ELECTRONIC_METER("1"),
        TWO_WIRE_KYZ_FORM_A("2"),
        THREE_WIRE_KYZ_FORM_C("3");

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

        List<InputOption> meterTypes = new ArrayList<>();

        for (MeterType meterType : MeterType.values()) {
            meterTypes.add( new InputOption( meterType.dbValue, messageAccessor.getMessage(meterType)));
        }
        return meterTypes;
    }

    @Override
    public String getEnumOptionName() {
        return "Mct470MeterType";
    }

    @Override
    public SelectionType getSelectionType() {
        return SelectionType.SWITCH;
    }
}
