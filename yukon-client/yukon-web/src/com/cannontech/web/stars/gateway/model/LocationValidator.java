package com.cannontech.web.stars.gateway.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.Errors;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

public class LocationValidator extends SimpleValidator<Location> {

    private static final String baseKey = "yukon.web.modules.operator.gateways.";
    
    public LocationValidator() {
        super(Location.class);
    }
    
    @Override
    protected void doValidation(Location target, Errors errors) {
        Double latitude = target.getLatitude(), longitude = target.getLatitude();
        validate(latitude, longitude, errors);
        
    }
    
    public static void validate(Double latitude, Double longitude, Errors errors) {
        if (latitude != null || longitude != null) {
            if (latitude != null) {
                if (latitude > 90 || latitude < -90) {
                    errors.rejectValue("latitude", baseKey + "latitude.invalid");
                }
            } else {
                YukonValidationUtils.rejectValues(errors, baseKey + "latitude.required", "latitude");
            }
            if (longitude != null) {
                if (longitude > 180 || longitude < -180) {
                    errors.rejectValue("longitude", baseKey + "longitude.invalid");
                }
            } else {
                YukonValidationUtils.rejectValues(errors, baseKey + "longitude.required", "longitude");
            }
        }
    }
    
    public static List<String> getErrorMessages(Double latitude, Double longitude, MessageSourceAccessor accessor) {
        List<String> errorMessages = new ArrayList<>();
        if (latitude != null || longitude != null) {
            if (latitude != null) {
                if (latitude > 90 || latitude < -90) {
                    errorMessages.add(accessor.getMessage(baseKey + "latitude.invalid"));
                }
            } else {
                errorMessages.add(accessor.getMessage(baseKey + "latitude.required", "latitude"));
            }
            if (longitude != null) {
                if (longitude > 180 || longitude < -180) {
                    errorMessages.add(accessor.getMessage(baseKey + "longitude.invalid"));
                }
            } else {
                errorMessages.add(accessor.getMessage(baseKey + "longitude.required", "longitude"));
            }
        }
        
        return errorMessages;
    }
}
