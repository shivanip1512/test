package com.cannontech.web.common.dashboard.widget.validator;

import com.cannontech.common.device.groups.util.DeviceGroupUtil;
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

        if (!DeviceGroupUtil.checkIsValidGroupName(inputValue.toString())) {
            String message = "Device Group selection required.";
            throw new WidgetParameterValidationException(message, inputName, "deviceGroup.invalid", inputName);
        }
    }

}
