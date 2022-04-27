package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.dr.setup.EmetconAddressUsage;
import com.cannontech.common.dr.setup.LoadGroupEmetcon;
import com.cannontech.common.validator.YukonApiValidationUtils;

@Service
public class LoadGroupEmetconValidator extends LoadGroupSetupValidator<LoadGroupEmetcon> {

    @Autowired private LMApiValidatorHelper lmApiValidatorHelper;
    @Autowired private YukonApiValidationUtils yukonApiValidationUtils;

    public LoadGroupEmetconValidator() {
        super(LoadGroupEmetcon.class);
    }

    @Override
    public boolean supports(Class clazz) {
        return LoadGroupEmetcon.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(LoadGroupEmetcon loadGroup, Errors errors) {

        yukonApiValidationUtils.checkIfFieldRequired("addressUsage", errors, loadGroup.getAddressUsage(), "Address Usage");

        yukonApiValidationUtils.checkIfFieldRequired("relayUsage", errors, loadGroup.getRelayUsage(), "Relay Usage");

        yukonApiValidationUtils.checkIfFieldRequired("goldAddress", errors, loadGroup.getGoldAddress(), "Gold Address");

        yukonApiValidationUtils.checkIfFieldRequired("silverAddress", errors, loadGroup.getSilverAddress(), "Silver Address");

        // Validate routeID
        lmApiValidatorHelper.validateRoute(errors, loadGroup.getRouteId());

        if (!errors.hasFieldErrors("goldAddress")) {
            yukonApiValidationUtils.checkRange(errors, "goldAddress", loadGroup.getGoldAddress(), 0, 4, true);
        }

        if (!errors.hasFieldErrors("goldAddress") && loadGroup.getAddressUsage() != null) {
            if (loadGroup.getAddressUsage() == EmetconAddressUsage.GOLD && loadGroup.getGoldAddress() == 0) {
                errors.rejectValue("goldAddress", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] {" 1 - 4 "}, "");
            }
        }

        if (!errors.hasFieldErrors("silverAddress")) {
            yukonApiValidationUtils.checkRange(errors, "silverAddress", loadGroup.getSilverAddress(), 0, 60, true);
        }

        if (!errors.hasFieldErrors("silverAddress") && loadGroup.getAddressUsage() != null) {
            if (loadGroup.getAddressUsage() == EmetconAddressUsage.SILVER && loadGroup.getSilverAddress() == 0) {
                errors.rejectValue("silverAddress", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] {" 1 - 60 "}, "");
            }
        }
    }

}
