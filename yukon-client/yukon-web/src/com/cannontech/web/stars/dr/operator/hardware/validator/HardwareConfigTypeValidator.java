package com.cannontech.web.stars.dr.operator.hardware.validator;

import java.util.Map;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.stars.dr.hardware.model.HardwareConfigType;
import com.cannontech.web.stars.dr.operator.hardware.model.HardwareConfigurationDto;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public abstract class HardwareConfigTypeValidator extends
        SimpleValidator<HardwareConfigurationDto> {

    private static final Map<HardwareConfigType, HardwareConfigTypeValidator> validators;
    static {
        Builder<HardwareConfigType, HardwareConfigTypeValidator> builder = ImmutableMap.builder();

        builder.put(HardwareConfigType.EXPRESSCOM, new HardwareConfigTypeValidator(){
            @Override
            protected void doValidation(HardwareConfigurationDto target,
                    Errors errors) {
                YukonValidationUtils.checkRange(errors, "serviceProvider",
                                                target.getServiceProvider(),
                                                0, 65534, true);
                YukonValidationUtils.checkRange(errors, "geo", target.getGeo(),
                                                0, 65534, false);
                YukonValidationUtils.checkRange(errors, "substation",
                                                target.getSubstation(), 0, 65534,
                                                false);
                YukonValidationUtils.checkRange(errors, "zip", target.getZip(),
                                                0, 16777214, false);
                YukonValidationUtils.checkRange(errors, "userAddress",
                                                target.getUserAddress(),
                                                0, 65534, false);
            }});

        builder.put(HardwareConfigType.VERSACOM, new HardwareConfigTypeValidator(){
            @Override
            protected void doValidation(HardwareConfigurationDto target,
                    Errors errors) {
                YukonValidationUtils.checkRange(errors, "utility",
                                                target.getUtility(),
                                                0, 255, true);
                YukonValidationUtils.checkRange(errors, "section", target.getSection(),
                                                1, 254, false);
            }});

        builder.put(HardwareConfigType.SA205, new HardwareConfigTypeValidator(){
            @Override
            protected void doValidation(HardwareConfigurationDto target,
                    Errors errors) {
                Integer[] slots = target.getSlots();
                for (int index = 0; index < slots.length; index++) {
                    YukonValidationUtils.checkRange(errors, "slots[" + index + "]",
                                                    slots[index],
                                                    0, 4095, false);
                }
            }});

        builder.put(HardwareConfigType.SA305, new HardwareConfigTypeValidator(){
            @Override
            protected void doValidation(HardwareConfigurationDto target,
                    Errors errors) {
                YukonValidationUtils.checkRange(errors, "utility",
                                                target.getUtility(),
                                                0, 15, true);
                YukonValidationUtils.checkRange(errors, "group",
                                                target.getGroup(),
                                                0, 63, false);
                YukonValidationUtils.checkRange(errors, "division",
                                                target.getDivision(),
                                                0, 63, false);
                YukonValidationUtils.checkRange(errors, "substation",
                                                target.getSubstation(),
                                                0, 1023, false);
                YukonValidationUtils.checkRange(errors, "rate",
                                                target.getRate(),
                                                0, 127, false);
            }});

        builder.put(HardwareConfigType.SA_SIMPLE, new HardwareConfigTypeValidator(){
            @Override
            protected void doValidation(HardwareConfigurationDto target,
                    Errors errors) {
                YukonValidationUtils.checkExceedsMaxLength(errors, "operationalAddress",
                                                           target.getOperationalAddress(), 8);
            }});

        validators = builder.build();
    }

    private HardwareConfigTypeValidator() {
        super(HardwareConfigurationDto.class);
    }

    public static HardwareConfigTypeValidator getInstance(HardwareConfigType hardwareConfigType) {
        return validators.get(hardwareConfigType);
    }
}
