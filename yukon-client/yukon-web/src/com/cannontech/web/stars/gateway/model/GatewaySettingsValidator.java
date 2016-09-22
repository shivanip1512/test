package com.cannontech.web.stars.gateway.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.rfn.model.GatewaySettings;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

public class GatewaySettingsValidator extends SimpleValidator<GatewaySettings> {
    
    private static final String baseKey = "yukon.web.modules.operator.gateways.";
    
    public GatewaySettingsValidator() {
        super(GatewaySettings.class);
    }
    
    @Override
    protected void doValidation(GatewaySettings settings, Errors errors) {
        
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", baseKey + "name.required");
        
        if(!errors.hasFieldErrors("name")){
            if(!PaoUtils.isValidPaoName(settings.getName())){
                errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
            }
        }
        
        YukonValidationUtils.checkExceedsMaxLength(errors, "name", settings.getName(), 60);
        
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "ipAddress", baseKey + "ipAddress.required");
        if (StringUtils.isNoneBlank(settings.getIpAddress())) {
            boolean ipValid = InetAddressValidator.getInstance().isValid(settings.getIpAddress());
            if (!ipValid) {
                errors.rejectValue("ipAddress", baseKey + "ipAddress.invalid");
            }
        }
        
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "admin.username", baseKey + "username.required");
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "superAdmin.username", baseKey + "username.required");
        
        Double latitude = settings.getLatitude();
        Double longitude = settings.getLongitude();
        
        validateLocation(latitude, longitude, errors);
        
        if (settings.isCheckUpdateServer()) {
            validateUpdateServer(settings, errors);
        }
    }
    
    public static void validateUpdateServer(GatewaySettings settings, Errors errors) {
        if (settings.isUseDefault()) {
            // Default settings will be used, no need to validate other fields
        } else {
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "updateServerUrl", baseKey + "updateserver.url.required");
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "updateServerLogin.username", baseKey + "updateserver.username.required");
        }
    }
    
    public static void validateLocation(Double latitude, Double longitude, Errors errors) {
        
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
    
}