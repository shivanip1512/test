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
            lmValidatorHelper.checkIfFieldRequired("routeID", errors, loadGroup.getRouteID(), "Route");
        }
        
        //If address usage is empty
        lmValidatorHelper.checkIfFieldRequired("addressUsage", errors, loadGroup.getAddressUsage(), "Address Usage");
        
        // SPID is mandatory
        lmValidatorHelper.checkIfFieldRequired("serviceProvider", errors, loadGroup.getServiceProvider(), "Service Provider");
        if (!errors.hasFieldErrors("serviceProvider")) {
            YukonValidationUtils.checkRange(errors, "serviceProvider", loadGroup.getServiceProvider(), 1, 165534, true);
        }

        lmValidatorHelper.checkIfFieldRequired("addressUsage", errors, loadGroup.getAddressUsage(), "Address Usage");
        
        if(loadGroup.getAddressUsage() != null ) {
        if (loadGroup.getAddressUsage().contains(AddressUsage.SERIAL)) {
            // Validate Serial
            lmValidatorHelper.checkIfFieldRequired("serialNumber", errors, loadGroup.getSerialNumber(), "Serial");
        }
        if (loadGroup.getAddressUsage().contains(AddressUsage.GEO)) {
            // Validate Geo
            lmValidatorHelper.checkIfFieldRequired("geo", errors, loadGroup.getGeo(), "Geo");
            if (!errors.hasFieldErrors("geo")) {
                YukonValidationUtils.checkRange(errors, "geo", loadGroup.getGeo(), 1, 165534, true);
            }
        }
        if (loadGroup.getAddressUsage().contains(AddressUsage.SUBSTATION)) {
            // Validate substation
            lmValidatorHelper.checkIfFieldRequired("substation", errors, loadGroup.getSubstation(), "Substation");
            if (!errors.hasFieldErrors("substation")) {
                YukonValidationUtils.checkRange(errors, "substation", loadGroup.getSubstation(), 0, 165534, true);
            }
        }

        if (loadGroup.getAddressUsage().contains(AddressUsage.FEEDER)) {
            // validate Feeder
            lmValidatorHelper.checkIfFieldRequired("feeder", errors, loadGroup.getFeeder(), "Feeder");
            if (!errors.hasFieldErrors("feeder")) {
                YukonValidationUtils.checkExceedsMaxLength(errors, "feeder", loadGroup.getFeeder(), 16);
            }
        }
        if (loadGroup.getAddressUsage().contains(AddressUsage.ZIP)) {
            // validate Zip
            lmValidatorHelper.checkIfFieldRequired( "zip", errors, loadGroup.getZip(), "Zip");
            if (!errors.hasFieldErrors("zip")) {
                YukonValidationUtils.checkRange(errors, "zip", loadGroup.getZip(), 1, 116777214, true);
            }
        }
        if (loadGroup.getAddressUsage().contains(AddressUsage.USER)) {
            // validate User
            lmValidatorHelper.checkIfFieldRequired("user", errors, loadGroup.getUser(), "User");
            if (!errors.hasFieldErrors("user")) {
                YukonValidationUtils.checkRange(errors, "user", loadGroup.getUser(), 0, 65534, true);
            }
        }
        if (loadGroup.getAddressUsage().contains(AddressUsage.PROGRAM)) {
            // validate Program

            lmValidatorHelper.checkIfFieldRequired( "program", errors, loadGroup.getProgram(), "Program");
            if (!errors.hasFieldErrors("program")) {
                YukonValidationUtils.checkRange(errors, "program", loadGroup.getProgram(), 1, 99, true);
            }
        }
        if (loadGroup.getAddressUsage().contains(AddressUsage.SPLINTER)) {
            // validate Splinter
            lmValidatorHelper.checkIfFieldRequired("splinter", errors,  loadGroup.getSplinter(), "Splinter");
            if (!errors.hasFieldErrors("splinter")) {
                YukonValidationUtils.checkRange(errors, "splinter", loadGroup.getSplinter(), 1, 99, true);
            }
        }
        if (loadGroup.getAddressUsage().contains(AddressUsage.LOAD)) {
            // validate Loads
            lmValidatorHelper.checkIfFieldRequired("relayUsage", errors, loadGroup.getRelayUsage(), "Relay Usage" );
        }
        }
    }
}
