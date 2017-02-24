package com.cannontech.web.stars.gateway.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.rfn.model.GatewaySettings;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

public class GatewaySettingsValidator extends SimpleValidator<GatewaySettings> {
    
    @Autowired private LocationValidator locationValidator;

    
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
        
        Location target = new Location();
        target.setLatitude(settings.getLatitude());
        target.setLongitude(settings.getLongitude());
        locationValidator.validate(target, errors);
        
        validateUpdateServer(settings, errors);
    }
    
    public static void validateUpdateServer(GatewaySettings settings, Errors errors) {
        if (settings.isUseDefault()) {
            // Default settings will be used, no need to validate other fields
        } else {
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "updateServerUrl", baseKey + "updateserver.url.required");
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "updateServerLogin.username", baseKey + "updateserver.username.required");
        }
    }
}