package com.cannontech.web.api.dr.setup;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LoadGroupEmetcon;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class LoadGroupEmetconValidator extends LoadGroupSetupValidator<LoadGroupEmetcon> {

    private final static String key = "yukon.web.modules.dr.setup.loadGroup.error.";
    private final static String validRelayUsageValues = "ABCS";
    private final static String validAddressUsageValues = "GS";

    public LoadGroupEmetconValidator() {
        super(LoadGroupEmetcon.class);
    }

    @Override
    public boolean supports(Class clazz) {
        return LoadGroupEmetcon.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(LoadGroupEmetcon loadGroup, Errors errors) {

        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "routeID", key + "required", new Object[] { "Route Id" });

        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressUsage", key + "required", new Object[] { "Address Usage" });
        
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "relayUsage", key + "required", new Object[] { "Relay Usage" });

        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "goldAddress", key + "required", new Object[] { "Gold Address" });
        
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "silverAddress", key + "required", new Object[] { "Silver Address" });
        

        if (!errors.hasFieldErrors("addressUsage")) {
            if (!validAddressUsageValues.contains(loadGroup.getAddressUsage().toString())) {
                errors.rejectValue("addressUsage", key + "addressUsage.invalidValue");
            }
        }
        if (!errors.hasFieldErrors("goldAddress")) {
            YukonValidationUtils.checkIsPositiveInt(errors, "goldAddress", loadGroup.getGoldAddress());
            if (loadGroup.getAddressUsage().equals('G') && loadGroup.getGoldAddress() == 0) {
                errors.rejectValue("goldAddress", key + "goldAddress.invalidValue");
            } else {
                YukonValidationUtils.checkRange(errors, "goldAddress", loadGroup.getGoldAddress(), 0, 4, true);
            }
        }

        if (!errors.hasFieldErrors("silverAddress")) {
            YukonValidationUtils.checkIsPositiveInt(errors, "silverAddress", loadGroup.getSilverAddress());
            if (loadGroup.getAddressUsage().equals('S') && loadGroup.getSilverAddress() == 0) {
                errors.rejectValue("silverAddress", key + "silverAddress.invalidValue");
            } else {
                YukonValidationUtils.checkRange(errors, "silverAddress", loadGroup.getSilverAddress(), 0, 60, true);
            }
        }

        if (loadGroup.getRelayUsage() != null) {
            if (!validRelayUsageValues.contains(loadGroup.getRelayUsage().toString())) {
                errors.rejectValue("relayUsage", key + "relayUsage.invalidValue");
            }
        }
    }

}
