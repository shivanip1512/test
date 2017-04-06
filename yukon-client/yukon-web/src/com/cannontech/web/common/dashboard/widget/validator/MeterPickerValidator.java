package com.cannontech.web.common.dashboard.widget.validator;

import com.cannontech.web.common.dashboard.exception.WidgetParameterValidationException;

/**
 * A validator for meter picker widget inputs. 
 */
public class MeterPickerValidator implements WidgetInputValidator {
    private static final MeterPickerValidator singletonInstance = new MeterPickerValidator();
    
    private MeterPickerValidator() {}
    
    public static MeterPickerValidator get() {
        return singletonInstance;
    }
    
    @Override
    public void validate(String inputName, Object deviceId) throws WidgetParameterValidationException {
        if (deviceId == null || !(deviceId instanceof Integer)) {
            String message = "DeviceId ID is not an integer: " + deviceId;
            throw new WidgetParameterValidationException(message, inputName, "nonIntDeviceId", deviceId);
        }
        //Could also check that a meter with this ID currently exists
    }

}
