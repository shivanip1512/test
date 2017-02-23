package com.cannontech.web.stars.gateway.model;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

public class LocationValidator extends SimpleValidator<Location> {

    private static final String baseKey = "yukon.web.modules.operator.gateways.";
    
    public LocationValidator() {
        super(Location.class);
    }
    
    @Override
    protected void doValidation(Location target, Errors errors) {
        Double latitude = target.getLatitude(), longitude = target.getLongitude();
        if (latitude != null || longitude != null) {
            if (latitude != null) {
                if (latitude > 90 || latitude < -90) {
                    if (errors instanceof BindException) {
                        errors.reject(baseKey + "latitude.invalid"); 
                    } else {
                        errors.rejectValue("latitude", baseKey + "latitude.invalid");
                    }
                }
            } else {
                if (errors instanceof BindException) {
                    errors.reject(baseKey + "latitude.required");
                } else {
                    YukonValidationUtils.rejectValues(errors, baseKey + "latitude.required", "latitude");
                }
            }
            if (longitude != null) {
                if (longitude > 180 || longitude < -180) {
                    if (errors instanceof BindException) {
                        errors.reject(baseKey + "longitude.invalid");
                    }
                    else {
                        errors.rejectValue("longitude", baseKey + "longitude.invalid");
                    }
                }
            } else {
                if (errors instanceof BindException) {
                    errors.reject(baseKey + "longitude.required");
                } else {
                    YukonValidationUtils.rejectValues(errors, baseKey + "longitude.required", "longitude");
                }
            }
        }
    }
}
