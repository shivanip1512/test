package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.EmetconAddressUsage;
import com.cannontech.common.dr.setup.LoadGroupEmetcon;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class LoadGroupEmetconValidator extends LoadGroupSetupValidator<LoadGroupEmetcon> {

    private final static String key = "yukon.web.modules.dr.setup.loadGroup.error.";
    private final static String validRelayUsageValues = "ABCS";
    @Autowired private LMValidatorHelper lmValidatorHelper;

    public LoadGroupEmetconValidator() {
        super(LoadGroupEmetcon.class);
    }

    @Override
    public boolean supports(Class clazz) {
        return LoadGroupEmetcon.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(LoadGroupEmetcon loadGroup, Errors errors) {

        lmValidatorHelper.checkIfFieldRequired("addressUsage", errors, loadGroup.getAddressUsage(), "Address Usage");

        lmValidatorHelper.checkIfFieldRequired("relayUsage", errors, loadGroup.getRelayUsage(), "Relay Usage");

        lmValidatorHelper.checkIfFieldRequired("goldAddress", errors, loadGroup.getGoldAddress(), "Gold Address");

        lmValidatorHelper.checkIfFieldRequired("silverAddress", errors, loadGroup.getSilverAddress(), "Silver Address" );

        // Validate routeID
        lmValidatorHelper.validateRoute(errors, loadGroup.getRouteId());

        if (!errors.hasFieldErrors("goldAddress")) {
            YukonValidationUtils.checkRange(errors, "goldAddress", loadGroup.getGoldAddress(), 0, 4, true);
        }

        if (!errors.hasFieldErrors("goldAddress") && loadGroup.getAddressUsage() != null) {
            if (loadGroup.getAddressUsage() == EmetconAddressUsage.GOLD && loadGroup.getGoldAddress() == 0) {
                errors.rejectValue("goldAddress", key + "goldAddress.invalidValue");
            }
        }

        if (!errors.hasFieldErrors("silverAddress")) {
            YukonValidationUtils.checkRange(errors, "silverAddress", loadGroup.getSilverAddress(), 0, 60, true);
        }

        if (!errors.hasFieldErrors("silverAddress") && loadGroup.getAddressUsage() != null) {
            if (loadGroup.getAddressUsage() == EmetconAddressUsage.SILVER && loadGroup.getSilverAddress() == 0) {
                errors.rejectValue("silverAddress", key + "silverAddress.invalidValue");
            }
        }

        if (loadGroup.getRelayUsage() != null) {
            if (!validRelayUsageValues.contains(loadGroup.getRelayUsage().toString())) {
                errors.rejectValue("relayUsage", key + "relayUsage.invalidValue");
            }
        }
    }

}
