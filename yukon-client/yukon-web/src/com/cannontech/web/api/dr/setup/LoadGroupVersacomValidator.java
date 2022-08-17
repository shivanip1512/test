package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LoadGroupVersacom;
import com.cannontech.common.dr.setup.VersacomAddressUsage;
import com.cannontech.common.util.StringUtils;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class LoadGroupVersacomValidator extends LoadGroupSetupValidator<LoadGroupVersacom> {
    

    @Autowired private LMValidatorHelper lmValidatorHelper;

    private final static String key = "yukon.web.modules.dr.setup.loadGroup.error.";

    public LoadGroupVersacomValidator() {
        super(LoadGroupVersacom.class);
    }

    @Override
    public boolean supports(Class clazz) {
        return LoadGroupVersacom.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(LoadGroupVersacom loadGroup, Errors errors) {

        lmValidatorHelper.validateRoute(errors, loadGroup.getRouteId());

        // Validate addressUsage string contains other that U, S, C and D
        lmValidatorHelper.checkIfFieldRequired("utilityAddress", errors, loadGroup.getUtilityAddress(), "Utility Address");
        if (!errors.hasFieldErrors("utilityAddress")) {
            YukonValidationUtils.checkRange(errors, "utilityAddress", loadGroup.getUtilityAddress(), 1, 254, true);
        }
        
        if (loadGroup.getAddressUsage().contains(VersacomAddressUsage.SECTION)) {
            lmValidatorHelper.checkIfFieldRequired("sectionAddress", errors, loadGroup.getSectionAddress(), "Section Address");
            if (!errors.hasFieldErrors("sectionAddress")) {
                YukonValidationUtils.checkRange(errors, "sectionAddress", loadGroup.getSectionAddress(), 0, 256, true);
            }
        }
        
        // classAddress (0 to 2^16 -1)
        if (loadGroup.getAddressUsage().contains(VersacomAddressUsage.CLASS)) {
            // Add Check for null and empty.
            Integer classAddress = null;
            if (loadGroup.getClassAddress() != null) {
                classAddress = StringUtils.convertBinaryToInteger(loadGroup.getClassAddress());
            }
            lmValidatorHelper.checkIfFieldRequired("classAddress", errors, classAddress, "Class Address" );
            if (!errors.hasFieldErrors("classAddress")) {
                YukonValidationUtils.checkRange(errors, "classAddress", classAddress, 0, 65535, true);
            }
        }
        
        // divisionAddress (0 to 2^16 -1)
        if (loadGroup.getAddressUsage().contains(VersacomAddressUsage.DIVISION)) {
            // Add Check for null and empty.
            Integer divisionAddress = null;
            if (loadGroup.getDivisionAddress() != null) {
                divisionAddress = StringUtils.convertBinaryToInteger(loadGroup.getDivisionAddress());
            }
            lmValidatorHelper.checkIfFieldRequired("divisionAddress", errors,  divisionAddress, "Division Address");
            if (!errors.hasFieldErrors("divisionAddress")) {
                YukonValidationUtils.checkRange(errors, "divisionAddress", divisionAddress, 0, 65535, true);
            }
        }
        
        // serialAddress
        if (loadGroup.getAddressUsage().contains(VersacomAddressUsage.SERIAL)) {
            lmValidatorHelper.checkIfFieldRequired("serialAddress", errors, loadGroup.getSerialAddress(),
                "Serial Address");
            if (!errors.hasFieldErrors("serialAddress")) {
                try {
                    Integer serialAddress = Integer.valueOf(loadGroup.getSerialAddress());
                    YukonValidationUtils.checkRange(errors, "serialAddress", serialAddress, 1, 99999, true);
                } catch (NumberFormatException e) {
                    // Reject value with invalid format message
                    errors.rejectValue("serialAddress", key + "invalidValue");
                }
            }
        }
    }
}

