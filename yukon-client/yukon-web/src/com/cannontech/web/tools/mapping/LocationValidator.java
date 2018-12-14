package com.cannontech.web.tools.mapping;

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
                boolean isLatitudeValid = YukonValidationUtils.isLatitudeInRange(latitude);
                if (!isLatitudeValid) {
                    errors.rejectValue("latitude", baseKey + "latitude.invalid");
                }
            } else {
                YukonValidationUtils.rejectValues(errors, baseKey + "latitude.required", "latitude");
            }

            if (longitude != null) {
                boolean isLongitudeValid = YukonValidationUtils.isLongitudeInRange(longitude);
                if (!isLongitudeValid) {
                    errors.rejectValue("longitude", baseKey + "longitude.invalid");
                }
            } else {
                YukonValidationUtils.rejectValues(errors, baseKey + "longitude.required", "longitude");
            }
        }

    }
}
