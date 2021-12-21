package com.cannontech.web.stars.gateway;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationHelper;
import com.cannontech.common.validator.YukonValidationUtils;

public class GatewayBulkUpdateValidator extends SimpleValidator<GatewayBulkUpdateModel> {
    
    @Autowired private YukonValidationHelper yukonValidationHelper;
    
    private static final String baseKey = "yukon.web.modules.operator.gateways.";
    
    public GatewayBulkUpdateValidator() {
        super(GatewayBulkUpdateModel.class);
    }
    
    @Override
    protected void doValidation(GatewayBulkUpdateModel settings, Errors errors) {
    	
    	if (settings.getGatewayIds() == null || settings.getGatewayIds().size() < 1) {
    		errors.rejectValue("gatewayIds", baseKey + "bulkUpdate.gateways.required");
    	}
        
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "nmIpAddress", baseKey + "ipAddress.required");
        if (StringUtils.isNoneBlank(settings.getNmIpAddress())) {
            boolean nmIpValid = InetAddressValidator.getInstance().isValid(settings.getNmIpAddress());
            if (!nmIpValid) {
                errors.rejectValue("nmIpAddress", baseKey + "ipAddress.invalid");
            }
        }
        YukonValidationUtils.validatePort(errors, "nmPort", yukonValidationHelper.getMessage(baseKey + "default.port"), String.valueOf(settings.getNmPort()));
    }
    
}