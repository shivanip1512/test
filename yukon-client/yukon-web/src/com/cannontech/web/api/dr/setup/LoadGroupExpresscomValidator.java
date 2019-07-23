package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.AddressUsage;
import com.cannontech.common.dr.setup.LoadGroupExpresscom;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class LoadGroupExpresscomValidator extends LoadGroupSetupValidator<LoadGroupExpresscom> {
    private final static String key = "yukon.web.modules.dr.setup.loadGroup.error.";

    @Autowired private LMValidatorHelper lmValidatorHelper;

    public LoadGroupExpresscomValidator() {
        super(LoadGroupExpresscom.class);
    }

    @Override
    public boolean supports(Class clazz) {
        return LoadGroupExpresscom.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(LoadGroupExpresscom loadGroup, Errors errors) {

        // Route ID is mandatory for expresscom and should exists
        if (loadGroup.getType() == PaoType.LM_GROUP_EXPRESSCOMM) {
            lmValidatorHelper.validateRoute(errors, loadGroup.getRouteId());
        }

        // Address Usage should have atleast one field
        YukonValidationUtils.checkIfListRequired("addressUsage", errors, loadGroup.getAddressUsage(), "Address Usage");

        // Address Usage should have atleast Load, program or splinter
        if (!errors.hasFieldErrors("addressUsage")) {
            boolean loadAddressUsage = loadGroup.getAddressUsage().stream().anyMatch(e -> e.isLoadAddressUsage());
            if (!loadAddressUsage) {
                errors.rejectValue("addressUsage", key + "loadRequired", new Object[] { "Load Address" }, "");
            }
        }

        // SPID is mandatory and check for range
        lmValidatorHelper.checkIfFieldRequired("serviceProvider", errors, loadGroup.getServiceProvider(),
            "Service Provider");
        if (!errors.hasFieldErrors("serviceProvider")) {
            YukonValidationUtils.checkRange(errors, "serviceProvider", loadGroup.getServiceProvider(), 1, 65534, true);
        }

        if (!errors.hasFieldErrors("addressUsage")) {
            if (loadGroup.getAddressUsage().contains(AddressUsage.SERIAL)) {
                lmValidatorHelper.checkIfFieldRequired("serialNumber", errors, loadGroup.getSerialNumber(),
                    "Serial Number");
                if (!errors.hasFieldErrors("serialNumber")) {
                    // Validate Serial
                    try {
                        Integer serialNumber = Integer.valueOf(loadGroup.getSerialNumber());
                        YukonValidationUtils.checkRange(errors, "serialNumber", serialNumber, 0, 999999999, true);
                    } catch (NumberFormatException e) {
                        // Reject value with invalid format message
                        errors.rejectValue("serialNumber", key + "invalidValue");
                    }
                }
            }
            if (loadGroup.getAddressUsage().contains(AddressUsage.GEO)) {
                // Validate Geo
                lmValidatorHelper.checkIfFieldRequired("geo", errors, loadGroup.getGeo(), "Geo");
                if (!errors.hasFieldErrors("geo")) {
                    YukonValidationUtils.checkRange(errors, "geo", loadGroup.getGeo(), 1, 65534, true);
                }
            } 

            if (loadGroup.getAddressUsage().contains(AddressUsage.SUBSTATION)) {
                // Validate substation
                lmValidatorHelper.checkIfFieldRequired("substation", errors, loadGroup.getSubstation(), "Substation");
                if (!errors.hasFieldErrors("substation")) {
                    YukonValidationUtils.checkRange(errors, "substation", loadGroup.getSubstation(), 1, 65534, true);
                }
            }

            if (loadGroup.getAddressUsage().contains(AddressUsage.FEEDER)) {
                // validate Feeder
                lmValidatorHelper.checkIfFieldRequired("feeder", errors, loadGroup.getFeeder(), "Feeder");
                YukonValidationUtils.checkExactLength("feeder", errors, loadGroup.getFeeder(), "Feeder", 16);
            }

            if (loadGroup.getAddressUsage().contains(AddressUsage.ZIP)) {
                // validate Zip
                lmValidatorHelper.checkIfFieldRequired("zip", errors, loadGroup.getZip(), "Zip");
                if (!errors.hasFieldErrors("zip")) {
                    YukonValidationUtils.checkRange(errors, "zip", loadGroup.getZip(), 1, 16777214, true);
                }
            }
            if (loadGroup.getAddressUsage().contains(AddressUsage.USER)) {
                // validate User
                lmValidatorHelper.checkIfFieldRequired("user", errors, loadGroup.getUser(), "User");
                if (!errors.hasFieldErrors("user")) {
                    YukonValidationUtils.checkRange(errors, "user", loadGroup.getUser(), 1, 65534, true);
                }
            } 
            if (loadGroup.getAddressUsage().contains(AddressUsage.PROGRAM)) {
                // validate Program

                lmValidatorHelper.checkIfFieldRequired("program", errors, loadGroup.getProgram(), "Program");
                if (!errors.hasFieldErrors("program")) {
                    YukonValidationUtils.checkRange(errors, "program", loadGroup.getProgram(), 1, 99, true);
                }
            }
            if (loadGroup.getAddressUsage().contains(AddressUsage.SPLINTER)) {
                // validate Splinter
                lmValidatorHelper.checkIfFieldRequired("splinter", errors, loadGroup.getSplinter(), "Splinter");
                if (!errors.hasFieldErrors("splinter")) {
                    YukonValidationUtils.checkRange(errors, "splinter", loadGroup.getSplinter(), 1, 99, true);
                }
            }
            if (loadGroup.getAddressUsage().contains(AddressUsage.LOAD)) {
                // validate Loads
                YukonValidationUtils.checkIfListRequired("relayUsage", errors, loadGroup.getRelayUsage(), "Relay Usage");
            }
        }
    }
}
