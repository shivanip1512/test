package com.cannontech.web.api.dr.setup;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LoadGroupEmetcon;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.yukon.IDatabaseCache;

@Service
public class LoadGroupEmetconValidator extends LoadGroupSetupValidator<LoadGroupEmetcon> {

    private final static String key = "yukon.web.modules.dr.setup.loadGroup.error.";
    private final static String validRelayUsageValues = "ABCS";
    private final static String validAddressUsageValues = "GS";
    @Autowired private IDatabaseCache serverDatabaseCache;

    public LoadGroupEmetconValidator() {
        super(LoadGroupEmetcon.class);
    }

    @Override
    public boolean supports(Class clazz) {
        return LoadGroupEmetcon.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(LoadGroupEmetcon loadGroup, Errors errors) {

        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressUsage", key + "required",
            new Object[] { "Address Usage" });

        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "relayUsage", key + "required",
            new Object[] { "Relay Usage" });

        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "goldAddress", key + "required",
            new Object[] { "Gold Address" });

        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "silverAddress", key + "required",
            new Object[] { "Silver Address" });

        // Validate routeID
        Integer routeId = loadGroup.getRouteID();
        if (routeId == null) {
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "routeID", key + "required",
                new Object[] { "Route Id" });
        } else {
            Set<Integer> routeIds = serverDatabaseCache.getAllRoutesMap().keySet();
            if (!routeIds.contains(routeId)) {
                errors.rejectValue("routeID", key + "routeId.doesNotExist");
            }
        }

        if (!errors.hasFieldErrors("addressUsage")) {
            if (!validAddressUsageValues.contains(loadGroup.getAddressUsage().toString())) {
                errors.rejectValue("addressUsage", key + "addressUsage.invalidValue");
            }
        }
        if (!errors.hasFieldErrors("goldAddress")) {
            YukonValidationUtils.checkIsPositiveInt(errors, "goldAddress", loadGroup.getGoldAddress());
            if (!errors.hasFieldErrors("goldAddress")) {
                YukonValidationUtils.checkRange(errors, "goldAddress", loadGroup.getGoldAddress(), 0, 4, true);
            }
        }
        if (!errors.hasFieldErrors("goldAddress") && loadGroup.getAddressUsage() != null) {
            if (loadGroup.getAddressUsage().equals(LoadGroupEmetcon.addressUsageGold)
                && loadGroup.getGoldAddress() == 0) {
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
            if (loadGroup.getAddressUsage().equals(LoadGroupEmetcon.addressUsageSilver)
                && loadGroup.getSilverAddress() == 0) {
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
