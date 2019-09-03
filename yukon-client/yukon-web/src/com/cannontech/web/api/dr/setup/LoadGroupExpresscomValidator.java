package com.cannontech.web.api.dr.setup;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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

        // Address Usage should have atleast Load, program or splinter
        if (loadGroup.getAddressUsage() != null || CollectionUtils.isNotEmpty(loadGroup.getAddressUsage())) {
            boolean loadAddressUsage = loadGroup.getAddressUsage().stream().anyMatch(e -> e.isLoadAddressUsage());
            if (!loadAddressUsage) {
                errors.rejectValue("addressUsage", key + "loadRequired", new Object[] { "Load Address" }, "");
            }
        }

        // SPID is mandatory and check for range
        lmValidatorHelper.checkIfFieldRequired("serviceProvider", errors, loadGroup.getServiceProvider(),
            "SPID");
        if (!errors.hasFieldErrors("serviceProvider")) {
            YukonValidationUtils.checkRange(errors, "serviceProvider", loadGroup.getServiceProvider(), 1, 65534, true);
        }

        if (!errors.hasFieldErrors("addressUsage")) {
            if (loadGroup.getAddressUsage().contains(AddressUsage.SERIAL)) {
                if (loadGroup.getAddressUsage().contains(AddressUsage.GEO)
                    || loadGroup.getAddressUsage().contains(AddressUsage.SUBSTATION)
                    || loadGroup.getAddressUsage().contains(AddressUsage.FEEDER)
                    || loadGroup.getAddressUsage().contains(AddressUsage.ZIP)
                    || loadGroup.getAddressUsage().contains(AddressUsage.USER)) {
                    errors.rejectValue("addressUsage", key + "incorrectCombination", new Object[] { "Address Usage" },
                        "");
                } else {
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
            } else {
                loadGroup.setSerialNumber("0");
            }
            if (loadGroup.getAddressUsage().contains(AddressUsage.GEO)) {
                // Validate Geo
                lmValidatorHelper.checkIfFieldRequired("geo", errors, loadGroup.getGeo(), "Geo");
                if (!errors.hasFieldErrors("geo")) {
                    YukonValidationUtils.checkRange(errors, "geo", loadGroup.getGeo(), 1, 65534, true);
                }
            } else {
                loadGroup.setGeo(0);
            }

            if (loadGroup.getAddressUsage().contains(AddressUsage.SUBSTATION)) {
                // Validate substation
                lmValidatorHelper.checkIfFieldRequired("substation", errors, loadGroup.getSubstation(), "Substation");
                if (!errors.hasFieldErrors("substation")) {
                    YukonValidationUtils.checkRange(errors, "substation", loadGroup.getSubstation(), 1, 65534, true);
                }
            } else {
                loadGroup.setSubstation(0);
            }
            if (loadGroup.getAddressUsage().contains(AddressUsage.FEEDER)) {
                // validate Feeder
                if (loadGroup.getFeeder() == null || !StringUtils.hasText(loadGroup.getFeeder().toString())) {
                    errors.rejectValue("feeder", key + "feederAddressRequired");
                }
                if (!errors.hasFieldErrors("feeder")) {
                    YukonValidationUtils.checkExactLength("feeder", errors, loadGroup.getFeeder(), "Feeder", 16);
                }
            } else {
                loadGroup.setFeeder("0");
            }

            if (loadGroup.getAddressUsage().contains(AddressUsage.ZIP)) {
                // validate Zip
                lmValidatorHelper.checkIfFieldRequired("zip", errors, loadGroup.getZip(), "Zip");
                if (!errors.hasFieldErrors("zip")) {
                    YukonValidationUtils.checkRange(errors, "zip", loadGroup.getZip(), 1, 16777214, true);
                }
            } else {
                loadGroup.setZip(0);
            }
            if (loadGroup.getAddressUsage().contains(AddressUsage.USER)) {
                // validate User
                lmValidatorHelper.checkIfFieldRequired("user", errors, loadGroup.getUser(), "User");
                if (!errors.hasFieldErrors("user")) {
                    YukonValidationUtils.checkRange(errors, "user", loadGroup.getUser(), 1, 65534, true);
                }
            } else {
                loadGroup.setUser(0);
            }
            if (loadGroup.getAddressUsage().contains(AddressUsage.PROGRAM)) {
                // validate Program

                lmValidatorHelper.checkIfFieldRequired("program", errors, loadGroup.getProgram(), "Program");
                if (!errors.hasFieldErrors("program")) {
                    YukonValidationUtils.checkRange(errors, "program", loadGroup.getProgram(), 1, 99, true);
                }
            } else {
                loadGroup.setProgram(0);
            }
            if (loadGroup.getAddressUsage().contains(AddressUsage.SPLINTER)) {
                // validate Splinter
                lmValidatorHelper.checkIfFieldRequired("splinter", errors, loadGroup.getSplinter(), "Splinter");
                if (!errors.hasFieldErrors("splinter")) {
                    YukonValidationUtils.checkRange(errors, "splinter", loadGroup.getSplinter(), 1, 99, true);
                }
            } else {
                loadGroup.setSplinter(0);
            }
            if (loadGroup.getAddressUsage().contains(AddressUsage.LOAD)) {
                // validate Loads
                if (loadGroup.getRelayUsage() == null || CollectionUtils.isEmpty(loadGroup.getRelayUsage())) {
                    errors.rejectValue("relayUsage", key + "loadAddressingRequired");
                }
            }
        }
    }
}
