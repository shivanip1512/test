package com.cannontech.web.api.dr.setup;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.EmetconAddressUsage;
import com.cannontech.common.dr.setup.LoadGroupEmetcon;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.yukon.IDatabaseCache;

@Service
public class LoadGroupEmetconValidator extends LoadGroupSetupValidator<LoadGroupEmetcon> {

    private final static String key = "yukon.web.modules.dr.setup.loadGroup.error.";
    private final static String validRelayUsageValues = "ABCS";
    @Autowired private IDatabaseCache serverDatabaseCache;
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

        lmValidatorHelper.checkIfEmptyOrWhitespaceName("addressUsage", errors, "Address Usage");

        lmValidatorHelper.checkIfEmptyOrWhitespaceName("relayUsage", errors, "Relay Usage");

        lmValidatorHelper.checkIfEmptyOrWhitespaceName("goldAddress", errors, "Gold Address");

        lmValidatorHelper.checkIfEmptyOrWhitespaceName("silverAddress", errors, "Silver Address" );

        // Validate routeID
        Integer routeId = loadGroup.getRouteID();
        if (routeId == null) {
            lmValidatorHelper.checkIfEmptyOrWhitespaceName("routeID", errors, "Route Id");
        } else {
            Set<Integer> routeIds = serverDatabaseCache.getAllRoutesMap().keySet();
            if (!routeIds.contains(routeId)) {
                errors.rejectValue("routeID", key + "routeId.doesNotExist");
            }
        }

        if (!errors.hasFieldErrors("goldAddress")) {
            YukonValidationUtils.checkIsPositiveInt(errors, "goldAddress", loadGroup.getGoldAddress());
            if (!errors.hasFieldErrors("goldAddress")) {
                YukonValidationUtils.checkRange(errors, "goldAddress", loadGroup.getGoldAddress(), 0, 4, true);
            }
        }
        if (!errors.hasFieldErrors("goldAddress") && loadGroup.getAddressUsage() != null) {
            if (loadGroup.getAddressUsage().equals(EmetconAddressUsage.GOLD) && loadGroup.getGoldAddress() == 0) {
                errors.rejectValue("goldAddress", key + "goldAddress.invalidValue");
            }
        }
        if (!errors.hasFieldErrors("silverAddress")) {
            YukonValidationUtils.checkIsPositiveInt(errors, "silverAddress", loadGroup.getSilverAddress());
            if (!errors.hasFieldErrors("silverAddress")) {
                YukonValidationUtils.checkRange(errors, "silverAddress", loadGroup.getSilverAddress(), 0, 60, true);

            }
        }
        if (!errors.hasFieldErrors("silverAddress") && loadGroup.getAddressUsage() != null) {
            if (loadGroup.getAddressUsage().equals(EmetconAddressUsage.SILVER) && loadGroup.getSilverAddress() == 0) {
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
