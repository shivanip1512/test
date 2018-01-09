package com.cannontech.web.common.dashboard.widget.validator;

import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.spring.YukonSpringHook;
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
        DeviceGroupService deviceGroupService = YukonSpringHook.getBean("deviceGroupService", DeviceGroupService.class);
        if (deviceGroupService.findGroupName(inputValue.toString()) == null) {
            String message = "Valid Device group selection is required.";
            throw new WidgetParameterValidationException(message, inputName, "deviceGroup.required", inputName);
        }
    }

}
