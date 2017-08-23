package com.cannontech.web.common.dashboard.widget.validator;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.web.common.dashboard.exception.WidgetMissingParameterException;
import com.cannontech.web.common.dashboard.exception.WidgetParameterValidationException;
import com.cannontech.web.common.dashboard.model.WidgetInputType;

/**
 * A validator for device group picker widget inputs.
 */
public class DeviceGroupPickerValidator implements WidgetInputValidator {
    private static final DeviceGroupPickerValidator singletonInstance = new DeviceGroupPickerValidator();

    private DeviceGroupPickerValidator() {
    }

    public static DeviceGroupPickerValidator get() {
        return singletonInstance;
    }

    @Override
    public void validate(String inputName, Object inputValue, WidgetInputType type)
            throws WidgetParameterValidationException, WidgetMissingParameterException {
        if (StringUtils.isEmpty(inputValue.toString())) {
            String message = "Device group not selected.";
            throw new WidgetParameterValidationException(message, inputName, "deviceGroup.required", inputName);
        }
    }

}
