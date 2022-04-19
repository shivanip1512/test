package com.cannontech.web.api.dr.setup;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.dr.setup.AddressUsage;
import com.cannontech.common.dr.setup.LoadGroupRFNExpresscom;
import com.cannontech.common.validator.YukonApiValidationUtils;

@Service
public class LoadGroupRFNExpresscomValidator extends LoadGroupSetupValidator<LoadGroupRFNExpresscom> {
    @Autowired private YukonApiValidationUtils yukonApiValidationUtils;

    public LoadGroupRFNExpresscomValidator() {
        super(LoadGroupRFNExpresscom.class);
    }

    @Override
    public boolean supports(Class clazz) {
        return LoadGroupRFNExpresscom.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(LoadGroupRFNExpresscom loadGroup, Errors errors) {

        // Address Usage should have atleast Load, program or splinter
        if (loadGroup.getAddressUsage() != null || CollectionUtils.isNotEmpty(loadGroup.getAddressUsage())) {
            boolean loadAddressUsage = loadGroup.getAddressUsage().stream().anyMatch(e -> e.isLoadAddressUsage());
            if (!loadAddressUsage) {
                errors.rejectValue("addressUsage", ApiErrorDetails.FIELD_REQUIRED.getCodeString(), new Object[] { "LOAD, SPLINTER or PROGRAM" }, "");
            }
        }

        // SPID is mandatory and check for range
        yukonApiValidationUtils.checkIfFieldRequired("serviceProvider", errors, loadGroup.getServiceProvider(),
            "SPID");
        if (!errors.hasFieldErrors("serviceProvider")) {
            yukonApiValidationUtils.checkRange(errors, "serviceProvider", loadGroup.getServiceProvider(), 1, 65534, true);
        }

        if (!errors.hasFieldErrors("addressUsage")) {
            if (loadGroup.getAddressUsage().contains(AddressUsage.SERIAL)) {
                if (loadGroup.getAddressUsage().contains(AddressUsage.GEO)
                    || loadGroup.getAddressUsage().contains(AddressUsage.SUBSTATION)
                    || loadGroup.getAddressUsage().contains(AddressUsage.FEEDER)
                    || loadGroup.getAddressUsage().contains(AddressUsage.ZIP)
                    || loadGroup.getAddressUsage().contains(AddressUsage.USER)) {
                    errors.rejectValue("addressUsage", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { "SERIAL" },
                        "");
                } else {
                    yukonApiValidationUtils.checkIfFieldRequired("serialNumber", errors, loadGroup.getSerialNumber(),
                        "Serial Number");
                    if (!errors.hasFieldErrors("serialNumber")) {
                        // Validate Serial
                        try {
                            Integer serialNumber = Integer.valueOf(loadGroup.getSerialNumber());
                            yukonApiValidationUtils.checkRange(errors, "serialNumber", serialNumber, 0, 999999999, true);
                        } catch (NumberFormatException e) {
                            // Reject value with invalid format message
                            errors.rejectValue("serialNumber", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(), new Object[] {"Serial Number"}, "");
                        }
                    }
                }
            } else {
                loadGroup.setSerialNumber("0");
            }
            if (loadGroup.getAddressUsage().contains(AddressUsage.GEO)) {
                // Validate Geo
                yukonApiValidationUtils.checkIfFieldRequired("geo", errors, loadGroup.getGeo(), "Geo");
                if (!errors.hasFieldErrors("geo")) {
                    yukonApiValidationUtils.checkRange(errors, "geo", loadGroup.getGeo(), 1, 65534, true);
                }
            } else {
                loadGroup.setGeo(0);
            }

            if (loadGroup.getAddressUsage().contains(AddressUsage.SUBSTATION)) {
                // Validate substation
                yukonApiValidationUtils.checkIfFieldRequired("substation", errors, loadGroup.getSubstation(), "Substation");
                if (!errors.hasFieldErrors("substation")) {
                    yukonApiValidationUtils.checkRange(errors, "substation", loadGroup.getSubstation(), 1, 65534, true);
                }
            } else {
                loadGroup.setSubstation(0);
            }
            if (loadGroup.getAddressUsage().contains(AddressUsage.FEEDER)) {
                // validate Feeder
                if (loadGroup.getFeeder() == null || !StringUtils.hasText(loadGroup.getFeeder().toString())) {
                    errors.rejectValue("feeder", ApiErrorDetails.FIELD_REQUIRED.getCodeString(), new Object[] {"Atleast 1 feeder"}, "");
                }
                if (!errors.hasFieldErrors("feeder")) {
                    yukonApiValidationUtils.checkExactLength("feeder", errors, loadGroup.getFeeder(), "Feeder", 16);
                }
            } else {
                loadGroup.setFeeder("0");
            }

            if (loadGroup.getAddressUsage().contains(AddressUsage.ZIP)) {
                // validate Zip
                yukonApiValidationUtils.checkIfFieldRequired("zip", errors, loadGroup.getZip(), "Zip");
                if (!errors.hasFieldErrors("zip")) {
                    yukonApiValidationUtils.checkRange(errors, "zip", loadGroup.getZip(), 1, 16777214, true);
                }
            } else {
                loadGroup.setZip(0);
            }
            if (loadGroup.getAddressUsage().contains(AddressUsage.USER)) {
                // validate User
                yukonApiValidationUtils.checkIfFieldRequired("user", errors, loadGroup.getUser(), "User");
                if (!errors.hasFieldErrors("user")) {
                    yukonApiValidationUtils.checkRange(errors, "user", loadGroup.getUser(), 1, 65534, true);
                }
            } else {
                loadGroup.setUser(0);
            }
            if (loadGroup.getAddressUsage().contains(AddressUsage.PROGRAM)) {
                // validate Program

                yukonApiValidationUtils.checkIfFieldRequired("program", errors, loadGroup.getProgram(), "Program");
                if (!errors.hasFieldErrors("program")) {
                    yukonApiValidationUtils.checkRange(errors, "program", loadGroup.getProgram(), 1, 254, true);
                }
            } else {
                loadGroup.setProgram(0);
            }
            if (loadGroup.getAddressUsage().contains(AddressUsage.SPLINTER)) {
                // validate Splinter
                yukonApiValidationUtils.checkIfFieldRequired("splinter", errors, loadGroup.getSplinter(), "Splinter");
                if (!errors.hasFieldErrors("splinter")) {
                    yukonApiValidationUtils.checkRange(errors, "splinter", loadGroup.getSplinter(), 1, 254, true);
                }
            } else {
                loadGroup.setSplinter(0);
            }
            if (loadGroup.getAddressUsage().contains(AddressUsage.LOAD)) {
                // validate Loads
                if (loadGroup.getRelayUsage() == null || CollectionUtils.isEmpty(loadGroup.getRelayUsage())) {
                    errors.rejectValue("relayUsage", ApiErrorDetails.FIELD_REQUIRED.getCodeString(), new Object[] {"At least 1 load group"}, "");
                }
            }
        }
    }
}
