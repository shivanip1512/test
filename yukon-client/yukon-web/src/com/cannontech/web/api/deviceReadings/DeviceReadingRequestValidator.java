package com.cannontech.web.api.deviceReadings;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.deviceReadings.model.DeviceReadingRequest;
import com.cannontech.deviceReadings.model.DeviceReadingsSelector;

public class DeviceReadingRequestValidator extends SimpleValidator<DeviceReadingRequest> {

    private final static String key = "yukon.api.deviceReading.error.";

    public DeviceReadingRequestValidator() {
        super(DeviceReadingRequest.class);
    }

    @Override
    protected void doValidation(DeviceReadingRequest deviceReadingRequest, Errors errors) {
        if (CollectionUtils.isNotEmpty(deviceReadingRequest.getDeviceReadingsSelectors())) {
            for (int i = 0; i < deviceReadingRequest.getDeviceReadingsSelectors().size(); i++) {
                errors.pushNestedPath("deviceReadingsSelectors[" + i + "]");
                DeviceReadingsSelector deviceReadingsSelector = deviceReadingRequest.getDeviceReadingsSelectors().get(i);

                checkIfFieldRequired("attributes", errors, deviceReadingsSelector.getAttributes(), "Attributes");
                if (!errors.hasFieldErrors("attributes") && CollectionUtils.isEmpty(deviceReadingsSelector.getAttributes())) {
                    errors.rejectValue("attributes", key + "attribute.required", new Object[] { "Attributes" }, "");
                }

                if (deviceReadingsSelector.getIdentifier() != null) {
                    checkIfFieldRequired("identifier.identifierType", errors, deviceReadingsSelector.getIdentifier().getIdentifierType(), "Identifier Type");
                    checkIfFieldRequired("identifier.value", errors, deviceReadingsSelector.getIdentifier().getValue(), "Identifier Value");
                } else {
                    checkIfFieldRequired("identifier", errors, deviceReadingsSelector.getIdentifier(), "Identifier");
                }
                errors.popNestedPath();
            }
        }
    }

    public void checkIfFieldRequired(String field, Errors errors, Object fieldValue, String fieldName) {
        if (fieldValue == null || !StringUtils.hasText(fieldValue.toString())) {
            errors.rejectValue(field, key + "required", new Object[] { fieldName }, "");
        }
    }
}
