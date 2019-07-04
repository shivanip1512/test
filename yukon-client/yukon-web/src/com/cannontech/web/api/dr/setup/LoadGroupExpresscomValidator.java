package com.cannontech.web.api.dr.setup;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.AddressUsage;
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
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "routeID", key + "required",
                new Object[] { "Route" });
        }
        
        //If address usage is empty
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressUsage", key + "required",
            new Object[] { "Address Usage" });
        
        // SPID is mandatory
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "serviceProvider", key + "required",
            new Object[] { "Service Provider" });
        if (!errors.hasFieldErrors("serviceProvider")) {
            YukonValidationUtils.checkRange(errors, "serviceProvider", loadGroup.getServiceProvider(), 1, 165534, true);
        }
        
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressUsage", key + "required",
            new Object[] { "Address Usage" });
        
        if(loadGroup.getAddressUsage() != null ) {
        if (loadGroup.getAddressUsage().contains(AddressUsage.SERIAL)) {
            // Validate Serial
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "serialNumber", key + "required",
                new Object[] { "Serial" });
        }
        if (loadGroup.getAddressUsage().contains(AddressUsage.GEO)) {
            // Validate Geo
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "geo", key + "required", new Object[] { "Geo" });
            if (!errors.hasFieldErrors("geo")) {
                YukonValidationUtils.checkRange(errors, "geo", loadGroup.getGeo(), 1, 165534, true);
            }
        }
        if (loadGroup.getAddressUsage().contains(AddressUsage.SUBSTATION)) {
            // Validate substation
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "substation", key + "required",
                new Object[] { "Substation" });
            if (!errors.hasFieldErrors("substation")) {
                YukonValidationUtils.checkRange(errors, "substation", loadGroup.getSubstation(), 0, 165534, true);
            }
        }

        if (loadGroup.getAddressUsage().contains(AddressUsage.FEEDER)) {
            // validate Feeder
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "feeder", key + "required",
                new Object[] { "Feeder" });
            if (!errors.hasFieldErrors("feeder")) {
                YukonValidationUtils.checkExceedsMaxLength(errors, "feeder", loadGroup.getFeeder(), 16);
            }
        }
        if (loadGroup.getAddressUsage().contains(AddressUsage.ZIP)) {
            // validate Zip
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "zip", key + "zip.required", new Object[] { "Zip" });
            if (!errors.hasFieldErrors("zip")) {
                YukonValidationUtils.checkRange(errors, "zip", loadGroup.getZip(), 1, 116777214, true);
            }
        }
        if (loadGroup.getAddressUsage().contains(AddressUsage.USER)) {
            // validate User
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "user", key + "user.required",
                new Object[] { "User" });
            if (!errors.hasFieldErrors("user")) {
                YukonValidationUtils.checkRange(errors, "user", loadGroup.getUser(), 0, 65534, true);
            }
        }
        if (loadGroup.getAddressUsage().contains(AddressUsage.PROGRAM)) {
            // validate Program
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "program", key + "program.required",
                new Object[] { "Program" });
            if (!errors.hasFieldErrors("program")) {
                YukonValidationUtils.checkRange(errors, "program", loadGroup.getProgram(), 1, 99, true);
            }
        }
        if (loadGroup.getAddressUsage().contains(AddressUsage.SPLINTER)) {
            // validate Splinter
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "splinter", key + "splinter.required",
                new Object[] { "Splinter" });
            if (!errors.hasFieldErrors("splinter")) {
                YukonValidationUtils.checkRange(errors, "splinter", loadGroup.getSplinter(), 1, 99, true);
            }
        }
        if (loadGroup.getAddressUsage().contains(AddressUsage.LOAD)) {
            // validate Loads
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "relayUsage", key + "required",
                new Object[] { "Relay Usage" });
        }
        }
    }
}
