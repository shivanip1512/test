package com.cannontech.web.api.dr.setup;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LoadGroupExpresscom;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class LoadGroupExpresscomValidator extends LoadGroupSetupValidator<LoadGroupExpresscom> {

    private final static String key = "yukon.web.modules.dr.setup.loadGroup.error.";

    public LoadGroupExpresscomValidator() {
        super(LoadGroupExpresscom.class);
    }

    @Override
    public boolean supports(Class clazz) {
        return LoadGroupExpresscom.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(LoadGroupExpresscom loadGroup, Errors errors) {

        // Route ID is mandatory for expresscom
        if (loadGroup.getType() == PaoType.LM_GROUP_EXPRESSCOMM) {
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "routeID", key + "routeID.required");
        }
        // SPID is mandatory
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "serviceProvider", key + "serviceProvider.required");
        if (!errors.hasFieldErrors("serviceProvider")) {
            YukonValidationUtils.checkRange(errors, "serviceProvider", loadGroup.getServiceProvider(), 0, 165534, true);
        }

        if (loadGroup.getAddressUsage().contains("T")) {
            // Validate Serial
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "serialNumber", key + "serial.required");
        }
        if (loadGroup.getAddressUsage().contains("G")) {
            // Validate Geo
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "geo", key + "geo.required");
            if (!errors.hasFieldErrors("geo")) {
                YukonValidationUtils.checkRange(errors, "geo", loadGroup.getGeo(), 0, 165534, true);
            }
        }
        if (loadGroup.getAddressUsage().contains("B")) {
            // Validate substation
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "substation", key + "substation.required");
            if (!errors.hasFieldErrors("substation")) {
                YukonValidationUtils.checkRange(errors, "substation", loadGroup.getSubstation(), 0, 165534, true);
            }
        }

        if (loadGroup.getAddressUsage().contains("F")) {
            // validate Feeder
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "feeder", key + "feeder.required");
            if (!errors.hasFieldErrors("feeder")) {
                YukonValidationUtils.checkRange(errors, "feeder", loadGroup.getFeeder(), 0, 165534, true);
            }
        }
        if (loadGroup.getAddressUsage().contains("Z")) {
            // validate Zip
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "zip", key + "zip.required");
            if (!errors.hasFieldErrors("zip")) {
                YukonValidationUtils.checkRange(errors, "zip", loadGroup.getZip(), 1, 116777214, true);
            }
        }
        if (loadGroup.getAddressUsage().contains("U")) {
            // validate User
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "user", key + "user.required");
            if (!errors.hasFieldErrors("user")) {
                YukonValidationUtils.checkRange(errors, "user", loadGroup.getUser(), 0, 65534, true);
            }
        }
        if (loadGroup.getAddressUsage().contains("P")) {
            // validate Program
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "program", key + "program.required");
            if (!errors.hasFieldErrors("program")) {
                YukonValidationUtils.checkRange(errors, "program", loadGroup.getProgram(), 1, 99, true);
            }
        }
        if (loadGroup.getAddressUsage().contains("R")) {
            // validate Splinter
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "splinter", key + "splinter.required");
            if (!errors.hasFieldErrors("splinter")) {
                YukonValidationUtils.checkRange(errors, "splinter", loadGroup.getSplinter(), 1, 99, true);
            }
        }
        if (loadGroup.getAddressUsage().contains("L")) {
            // validate Loads
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "relayUsage", key + "relayUsage.required");
        }
    }
}
