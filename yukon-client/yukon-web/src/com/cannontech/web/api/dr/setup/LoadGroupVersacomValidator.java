package com.cannontech.web.api.dr.setup;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LoadGroupVersacom;
import com.cannontech.common.util.StringUtils;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.yukon.IDatabaseCache;

@Service
public class LoadGroupVersacomValidator extends LoadGroupSetupValidator<LoadGroupVersacom> {
    
    @Autowired private IDatabaseCache serverDatabaseCache;
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
        
        // Validate routeId
        Integer routeId = loadGroup.getRouteId();
        if (routeId == null) {
            lmValidatorHelper.checkIfEmptyOrWhitespaceName("routeId", errors, "Route Id");
        } else {
            Set<Integer> routeIds = serverDatabaseCache.getAllRoutesMap().keySet();
            if (!routeIds.contains(routeId)) {
                errors.rejectValue("routeId", key + "routeId.doesNotExist");
            }
        }
        
        // Validate addressUsage string contains other that U, S, C and D
        if (loadGroup.getAddressUsage().contains("U")) {
            lmValidatorHelper.checkIfEmptyOrWhitespaceName("utilityAddress", errors, "Utility Address");
            if (!errors.hasFieldErrors("utilityAddress")) {
                YukonValidationUtils.checkRange(errors, "utilityAddress", loadGroup.getUtilityAddress(), 1, 254, true);
            }
        } else {
            errors.rejectValue("addressUsage", key + "utilityAddress.doesNotExist");
        }
        
        if (loadGroup.getAddressUsage().contains("S")) {
            lmValidatorHelper.checkIfEmptyOrWhitespaceName("sectionAddress", errors, "Section Address");
            if (!errors.hasFieldErrors("sectionAddress")) {
                YukonValidationUtils.checkRange(errors, "sectionAddress", loadGroup.getSectionAddress(), 0, 256, true);
            }
        }
        
        // classAddress (0 to 2^16 -1)
        if (loadGroup.getAddressUsage().contains("C")) {
            lmValidatorHelper.checkIfEmptyOrWhitespaceName("classAddress", errors, "Class Address" );
            if (!errors.hasFieldErrors("classAddress")) {
                YukonValidationUtils.checkRange(errors, "classAddress", loadGroup.getClassAddress(), 0, 65535, true);
            }
        }
        
        // divisionAddress (0 to 2^16 -1)
        if (loadGroup.getAddressUsage().contains("D")) {
            lmValidatorHelper.checkIfEmptyOrWhitespaceName("divisionAddress", errors,  "Division Address");
            if (!errors.hasFieldErrors("divisionAddress")) {
                YukonValidationUtils.checkRange(errors, "divisionAddress", loadGroup.getClassAddress(), 0, 65535, true);
            }
        }
        
        // serialAddress
        try {
            Integer serialAddress = Integer.valueOf(loadGroup.getSerialAddress());
            YukonValidationUtils.checkRange(errors, "serialAddress", serialAddress, 0, 99999, true);
        } catch (NumberFormatException e) {
            // Reject value with invalid format message
            errors.rejectValue("serialAddress", key + "invalidValue");
        }
        
        // relayUsage (check all the chars are valid or not. Should not contain other than 1,2,3,4)
        String relayUsage = loadGroup.getRelayUsage();
        if (StringUtils.isStringMatchesWithPattern(relayUsage, "1234", false)) {
            String formattedRelayUsage = StringUtils.formatStringWithPattern(relayUsage, "1234");
            loadGroup.setRelayUsage(formattedRelayUsage);
        } else {
            errors.rejectValue("relayUsage", key + "invalidValue");
        }
        
        // Address Usage (Should not contain other than U, S, C & D)
        String addressUsage = loadGroup.getAddressUsage();
        if (StringUtils.isStringMatchesWithPattern(addressUsage, "USCD", false)) {
            String formattedAddress = StringUtils.formatStringWithPattern(addressUsage, "USCD");
            loadGroup.setAddressUsage(formattedAddress);
        } else {
            errors.rejectValue("addressUsage", key + "invalidValue");
        }
    }
}

