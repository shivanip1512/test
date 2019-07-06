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

        // Route ID is mandatory for expresscom
        if (loadGroup.getType() == PaoType.LM_GROUP_EXPRESSCOMM) {
            lmValidatorHelper.checkIfEmptyOrWhitespaceName("routeID", errors, "Route");
        }
        
        //If address usage is empty
        lmValidatorHelper.checkIfEmptyOrWhitespaceName("addressUsage", errors, "Address Usage");
        
        // SPID is mandatory
        lmValidatorHelper.checkIfEmptyOrWhitespaceName("serviceProvider", errors, "Service Provider");
        if (!errors.hasFieldErrors("serviceProvider")) {
            YukonValidationUtils.checkRange(errors, "serviceProvider", loadGroup.getServiceProvider(), 1, 165534, true);
        }

        lmValidatorHelper.checkIfEmptyOrWhitespaceName("addressUsage", errors, "Address Usage");
        
        if(loadGroup.getAddressUsage() != null ) {
        if (loadGroup.getAddressUsage().contains(AddressUsage.SERIAL)) {
            // Validate Serial
            lmValidatorHelper.checkIfEmptyOrWhitespaceName("serialNumber", errors, "Serial");
        }
        if (loadGroup.getAddressUsage().contains(AddressUsage.GEO)) {
            // Validate Geo
            lmValidatorHelper.checkIfEmptyOrWhitespaceName("geo", errors, "Geo");
            if (!errors.hasFieldErrors("geo")) {
                YukonValidationUtils.checkRange(errors, "geo", loadGroup.getGeo(), 1, 165534, true);
            }
        }
        if (loadGroup.getAddressUsage().contains(AddressUsage.SUBSTATION)) {
            // Validate substation
            lmValidatorHelper.checkIfEmptyOrWhitespaceName("substation", errors, "Substation");
            if (!errors.hasFieldErrors("substation")) {
                YukonValidationUtils.checkRange(errors, "substation", loadGroup.getSubstation(), 0, 165534, true);
            }
        }

        if (loadGroup.getAddressUsage().contains(AddressUsage.FEEDER)) {
            // validate Feeder
            lmValidatorHelper.checkIfEmptyOrWhitespaceName("feeder", errors, "Feeder");
            if (!errors.hasFieldErrors("feeder")) {
                YukonValidationUtils.checkExceedsMaxLength(errors, "feeder", loadGroup.getFeeder(), 16);
            }
        }
        if (loadGroup.getAddressUsage().contains(AddressUsage.ZIP)) {
            // validate Zip
            lmValidatorHelper.checkIfEmptyOrWhitespaceName( "zip", errors, "Zip");
            if (!errors.hasFieldErrors("zip")) {
                YukonValidationUtils.checkRange(errors, "zip", loadGroup.getZip(), 1, 116777214, true);
            }
        }
        if (loadGroup.getAddressUsage().contains(AddressUsage.USER)) {
            // validate User
            lmValidatorHelper.checkIfEmptyOrWhitespaceName("user", errors, "User");
            if (!errors.hasFieldErrors("user")) {
                YukonValidationUtils.checkRange(errors, "user", loadGroup.getUser(), 0, 65534, true);
            }
        }
        if (loadGroup.getAddressUsage().contains(AddressUsage.PROGRAM)) {
            // validate Program

            lmValidatorHelper.checkIfEmptyOrWhitespaceName( "program", errors, "Program");
            if (!errors.hasFieldErrors("program")) {
                YukonValidationUtils.checkRange(errors, "program", loadGroup.getProgram(), 1, 99, true);
            }
        }
        if (loadGroup.getAddressUsage().contains(AddressUsage.SPLINTER)) {
            // validate Splinter
            lmValidatorHelper.checkIfEmptyOrWhitespaceName("splinter", errors, "Splinter");
            if (!errors.hasFieldErrors("splinter")) {
                YukonValidationUtils.checkRange(errors, "splinter", loadGroup.getSplinter(), 1, 99, true);
            }
        }
        if (loadGroup.getAddressUsage().contains(AddressUsage.LOAD)) {
            // validate Loads
            lmValidatorHelper.checkIfEmptyOrWhitespaceName("relayUsage", errors, "Relay Usage" );
        }
        }
    }
}
