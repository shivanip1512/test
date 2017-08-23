package com.cannontech.web.common.dashboard.widget.validator;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.web.common.dashboard.exception.WidgetParameterValidationException;
import com.cannontech.web.common.dashboard.model.WidgetInputType;

/**
 * A validator for meter picker widget inputs.
 */
public class MeterPickerValidator implements WidgetInputValidator {
    private static final MeterPickerValidator singletonInstance = new MeterPickerValidator();

    private MeterPickerValidator() {
    }

    public static MeterPickerValidator get() {
        return singletonInstance;
    }

    @Override
    public void validate(String inputName, Object deviceId, WidgetInputType type)
            throws WidgetParameterValidationException {

        if (StringUtils.isEmpty(deviceId.toString())) {
            throw new WidgetParameterValidationException("No Meter selected.", inputName, "meter.required");
        }
        try {
            if (type.getInputClass().equals(Integer.class)) {
                Integer.valueOf(deviceId.toString()).intValue();
            }
        } catch (NumberFormatException e) {
            String message = "DeviceId ID is not an integer: " + deviceId;
            throw new WidgetParameterValidationException(message, inputName, "nonIntDeviceId", deviceId);
        }
        //Could also check that a meter with this ID currently exists
    }

}
