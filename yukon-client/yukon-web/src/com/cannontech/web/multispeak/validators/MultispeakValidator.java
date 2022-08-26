package com.cannontech.web.multispeak.validators;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.db.MultispeakInterface;
import com.cannontech.web.editor.MultispeakModel;

@Service
public class MultispeakValidator extends SimpleValidator<MultispeakModel> {
    @Autowired private MultispeakDao multispeakDao;
    public MultispeakValidator() {
        super(MultispeakModel.class);
    }

    @Override
    public void doValidation(MultispeakModel multispeak, Errors errors) {
        MultispeakVendor multispeakVendor=multispeak.getMspVendor();
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "mspVendor.companyName", "yukon.web.error.isBlank");
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "mspVendor.attributes", "yukon.web.error.isBlank");
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "mspVendor.templateNameDefault",
            "yukon.web.error.isBlank");
        if (multispeakVendor.getVendorID() != null
            && multispeakVendor.getVendorID() == MultispeakVendor.CANNON_MSP_VENDORID) {

            if (multispeak.getPaoNameUsesExtension()) { // if using extensions, then must have an extension
                                                        // name
                YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "paoNameAliasExtension",
                    "yukon.web.error.isBlank");
            }
        }
        int index = 0;
        if (multispeak.getMspVendor().getVendorID() != null
            && multispeak.getMspVendor().getVendorID() == MultispeakVendor.CANNON_MSP_VENDORID) {

            for (MultispeakInterface multispeakInterface : multispeak.getMspInterfaceList()) {
                validateInterfaceURL(multispeakInterface.getMspEndpoint(), index, errors);
                index++;
            }
        } else {
            //Cannon is the name used by Yukon. We should not allow to create any other vendor with the same name.
            if (StringUtils.equalsIgnoreCase(multispeakVendor.getCompanyName(), MultispeakVendor.CANNON_MSP_COMPANYNAME)) {
                errors.rejectValue("mspVendor.companyName",
                        "yukon.web.modules.adminSetup.multispeak.companyNameConflict");
            } else {
                boolean nameAvailable =
                        multispeakDao.isUniqueName(multispeakVendor.getCompanyName(), multispeakVendor.getAppName(),
                            multispeak.getMspVendor().getVendorID());
                    
                if (nameAvailable) {
                    errors.rejectValue("mspVendor.appName",
                        "yukon.web.modules.adminSetup.multispeak.companyAppNameConflict");
                    errors.rejectValue("mspVendor.companyName", "yukon.common.blank");
                }
            }
            for (MultispeakInterface multispeakInterface : multispeak.getMspInterfaceList()) {
                if (multispeakInterface.getInterfaceEnabled() != null && multispeakInterface.getInterfaceEnabled()) {
                    validateInterfaceURL(multispeakInterface.getMspEndpoint(), index, errors);
                }
                index++;
            }
        }

        YukonValidationUtils.checkIllegalXmlCharacter(errors, "mspVendor.password", multispeakVendor.getPassword());
        YukonValidationUtils.checkIllegalXmlCharacter(errors, "mspVendor.outPassword", multispeakVendor.getOutPassword());
    }

    private void validateInterfaceURL(String endpoint, int index, Errors errors) {
        try {
            new URL(endpoint);
        } catch (MalformedURLException e) {
            errors.rejectValue("mspInterfaceList[" + index + "].mspEndpoint",
                "yukon.web.modules.adminSetup.multispeak.invalidURL");
        }
    }

}
