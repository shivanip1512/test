package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.dr.setup.LoadGroupVersacom;
import com.cannontech.common.dr.setup.VersacomAddressUsage;
import com.cannontech.common.util.StringUtils;
import com.cannontech.common.validator.YukonApiValidationUtils;

@Service
public class LoadGroupVersacomValidator extends LoadGroupSetupValidator<LoadGroupVersacom> {
    

    @Autowired private LMApiValidatorHelper lmApiValidatorHelper;


    public LoadGroupVersacomValidator() {
        super(LoadGroupVersacom.class);
    }

    @Override
    public boolean supports(Class clazz) {
        return LoadGroupVersacom.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(LoadGroupVersacom loadGroup, Errors errors) {

        lmApiValidatorHelper.validateRoute(errors, loadGroup.getRouteId());

        // Validate addressUsage string contains other that U, S, C and D
        YukonApiValidationUtils.checkIfFieldRequired("utilityAddress", errors, loadGroup.getUtilityAddress(), "Utility Address");
        if (!errors.hasFieldErrors("utilityAddress")) {
            YukonApiValidationUtils.checkRange(errors, "utilityAddress", loadGroup.getUtilityAddress(), 1, 254, true);
        }
        
        if (loadGroup.getAddressUsage().contains(VersacomAddressUsage.SECTION)) {
            YukonApiValidationUtils.checkIfFieldRequired("sectionAddress", errors, loadGroup.getSectionAddress(), "Section Address");
            if (!errors.hasFieldErrors("sectionAddress")) {
                YukonApiValidationUtils.checkRange(errors, "sectionAddress", loadGroup.getSectionAddress(), 0, 256, true);
            }
        }
        
        // classAddress (0 to 2^16 -1)
        if (loadGroup.getAddressUsage().contains(VersacomAddressUsage.CLASS)) {
            // Add Check for null and empty.
            Integer classAddress = null;
            if (loadGroup.getClassAddress() != null) {
                classAddress = StringUtils.convertBinaryToInteger(loadGroup.getClassAddress());
            }
            YukonApiValidationUtils.checkIfFieldRequired("classAddress", errors, classAddress, "Class Address" );
            if (!errors.hasFieldErrors("classAddress")) {
                YukonApiValidationUtils.checkRange(errors, "classAddress", classAddress, 0, 65535, true);
            }
        }
        
        // divisionAddress (0 to 2^16 -1)
        if (loadGroup.getAddressUsage().contains(VersacomAddressUsage.DIVISION)) {
            // Add Check for null and empty.
            Integer divisionAddress = null;
            if (loadGroup.getDivisionAddress() != null) {
                divisionAddress = StringUtils.convertBinaryToInteger(loadGroup.getDivisionAddress());
            }
            YukonApiValidationUtils.checkIfFieldRequired("divisionAddress", errors,  divisionAddress, "Division Address");
            if (!errors.hasFieldErrors("divisionAddress")) {
                YukonApiValidationUtils.checkRange(errors, "divisionAddress", divisionAddress, 0, 65535, true);
            }
        }
        
        // serialAddress
        if (loadGroup.getAddressUsage().contains(VersacomAddressUsage.SERIAL)) {
            YukonApiValidationUtils.checkIfFieldRequired("serialAddress", errors, loadGroup.getSerialAddress(),
                "Serial Address");
            if (!errors.hasFieldErrors("serialAddress")) {
                try {
                    Integer serialAddress = Integer.valueOf(loadGroup.getSerialAddress());
                    YukonApiValidationUtils.checkRange(errors, "serialAddress", serialAddress, 1, 99999, true);
                } catch (NumberFormatException e) {
                    // Reject value with invalid format message
                    errors.rejectValue("serialAddress", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(), new Object[] {"Serial Address"},"");
                }
            }
        }
    }
}

