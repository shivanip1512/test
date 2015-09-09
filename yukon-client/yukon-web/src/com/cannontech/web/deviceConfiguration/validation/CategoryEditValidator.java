package com.cannontech.web.deviceConfiguration.validation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.validation.Errors;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.config.model.jaxb.CategoryType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.deviceConfiguration.enumeration.DisconnectMode.ConfigurationType;
import com.cannontech.web.deviceConfiguration.enumeration.ReconnectParameter.ReconnectType;
import com.cannontech.web.deviceConfiguration.model.CategoryEditBean;
import com.cannontech.web.deviceConfiguration.model.CategoryEditBean.RateBackingBean;
import com.cannontech.web.deviceConfiguration.model.CategoryTemplate;
import com.cannontech.web.deviceConfiguration.model.ChannelField;
import com.cannontech.web.deviceConfiguration.model.ChannelInput;
import com.cannontech.web.deviceConfiguration.model.Field;
import com.cannontech.web.deviceConfiguration.model.FloatField;
import com.cannontech.web.deviceConfiguration.model.IntegerField;
import com.cannontech.web.deviceConfiguration.model.RateInput;
import com.cannontech.web.input.validate.InputValidator;

public class CategoryEditValidator extends SimpleValidator<CategoryEditBean> {

    private static final Logger log = YukonLogManager.getLogger(CategoryEditValidator.class);

    private final CategoryTemplate categoryTemplate;
    private static final String baseKey = "yukon.web.modules.tools.configs.category";

    public CategoryEditValidator(CategoryTemplate categoryTemplate) {
        super(CategoryEditBean.class);
        this.categoryTemplate = categoryTemplate;
    }

    @Override
    protected void doValidation(CategoryEditBean target, Errors errors) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "categoryName", baseKey + ".emptyName");

        List<Field<?>> fields = categoryTemplate.getFields();

        if (CategoryType.CENTRON_420_DISPLAY_ITEMS.value().equals(categoryTemplate.getCategoryType()) 
                || CategoryType.CENTRON_410_DISPLAY.value().equals(categoryTemplate.getCategoryType())) {
            // Handle this guy differently.
            boolean slotDisabledHit = false;
            for (Field<?> field : fields) {
                if (Integer.parseInt(target.getCategoryInputs().get(field.getFieldName())) == 0) {
                    slotDisabledHit = true;
                } else if (slotDisabledHit) {
                    // Error
                    errors.rejectValue("categoryInputs[" + field.getFieldName() + "]", baseKey + ".postDisabled");
                }
            }
        } else {
            for (Field<?> field : fields) {
                if ( field.getInputType().getTypeClass().equals(RateInput.class)) {

                    RateBackingBean rateBackingBean = target.getScheduleInputs().get(field.getFieldName());

                    int prevTime = 0; // Start at midnight.
                    boolean midnightHit = false; // Keep track of if we've encountered a midnight entry.

                    for (Entry<String, RateInput> entry : rateBackingBean.getRateInputs().entrySet()) {
                        if (!"time0".equals(entry.getKey())) {
                            int thisTime = Integer.parseInt(entry.getValue().getTime().replace(":", ""));

                            if (thisTime <= prevTime) {
                                if (thisTime == 0) {
                                    // As long as the rest are midnights, this is okay. 
                                    midnightHit = true;
                                    prevTime = thisTime;
                                } else {
                                    // Problem. This is never okay 
                                    String path = "scheduleInputs[" + field.getFieldName() + "].rateInputs[" + entry.getKey() + "].time";
                                    errors.rejectValue(path, baseKey + ".invalidTime");
                                }
                            } else {
                                if (midnightHit) {
                                    // Problem. We've already hit midnight, the list should be over.
                                    String path = "scheduleInputs[" + field.getFieldName() + "].rateInputs[" + entry.getKey() + "].time";
                                    errors.rejectValue(path, baseKey + ".invalidTime");
                                } else {
                                    prevTime = thisTime;
                                }
                            }
                        }
                    }
                } else if ( field.getInputType().getTypeClass().equals(List.class)) {
                    ChannelField channelField = (ChannelField) field;

                    InputValidator<List<ChannelInput>> validator = channelField.getValidator();
                    validator.validate("channelInputs", "Channel Errors", target.getChannelInputs(), errors);

                } else {
                    String value = target.getCategoryInputs().get(field.getFieldName());

                    final String path = "categoryInputs[" + field.getFieldName() + "]";

                    if (StringUtils.isBlank(value)) {
                        errors.rejectValue(path, baseKey + ".emptyValue");
                        continue;
                    }

                    if (field.getValidator() != null) {
                        try {
                            if (field.getClass() == IntegerField.class) {
                                IntegerField intField = (IntegerField) field;
                                InputValidator<Integer> validator = intField.getValidator();
                                validator.validate(path, field.getDisplayName(), Integer.valueOf(value), errors);
                            } else if (field.getClass() == FloatField.class) {
                                FloatField floatField = (FloatField) field;
                                InputValidator<Float> validator = floatField.getValidator();
                                validator.validate(path, field.getDisplayName(), Float.valueOf(value), errors);

                                if (floatField.isDigitsLimited()) {
                                    BigDecimal decimal = new BigDecimal(value);
                                    int numDigits = floatField.getDecimalDigits();
                                    if (decimal.scale() > numDigits) {
                                        Object[] args = {Integer.toString(numDigits)};
                                        errors.rejectValue(path, baseKey + ".decimalDigits", args, "");
                                    }
                                }
                            } else {
                                log.error("Received a validator for an unsupported type: " + field.getClass().getSimpleName());
                            }
                        } catch (NumberFormatException nfe) {
                            // The value wasn't a number
                            errors.rejectValue(path, baseKey + ".nonNumber");
                        }
                    }
                }
            }
        }

        if ( ! errors.hasErrors() && CategoryType.RFN_DISCONNECT_CONFIGURATION.value().equals(categoryTemplate.getCategoryType())) {

            ConfigurationType mode = ConfigurationType.valueOf(target.getCategoryInputs().get("disconnectMode"));
            ReconnectType reconnectType = ReconnectType.valueOf(target.getCategoryInputs().get("reconnectParam"));
            int delay = Integer.valueOf(target.getCategoryInputs().get("disconnectLoadLimitConnectDelay"));

            if (mode == ConfigurationType.DEMAND_THRESHOLD && reconnectType == ReconnectType.IMMEDIATE && delay == 0) {
                errors.rejectValue("categoryInputs[reconnectParam]", baseKey + ".error.immediateDisconnect");
                errors.rejectValue("categoryInputs[disconnectLoadLimitConnectDelay]", baseKey + ".error.immediateDisconnect");
            }
        }
    }
}