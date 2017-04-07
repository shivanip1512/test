package com.cannontech.web.multispeak.validators;

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.db.MultispeakInterface;
import com.cannontech.web.editor.MultispeakModel;

@Service
public class MultispeakValidator extends SimpleValidator<MultispeakModel> {

    public MultispeakValidator() {
        super(MultispeakModel.class);
    }

    @Override
    public void doValidation(MultispeakModel multispeak, Errors errors) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "mspVendor.companyName", "yukon.web.error.isBlank");

        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "mspVendor.templateNameDefault",
            "yukon.web.error.isBlank");
        MultispeakVendor multispeakVendor = multispeak.getMspVendor();
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
            for (MultispeakInterface multispeakInterface : multispeak.getMspInterfaceList()) {
                if (multispeakInterface.getInterfaceEnabled() != null && multispeakInterface.getInterfaceEnabled()) {
                    validateInterfaceURL(multispeakInterface.getMspEndpoint(), index, errors);
                }
                index++;
            }
        }
    }

    private void validateInterfaceURL(String endpoint, int index, Errors errors) {
        try {
            new URL(endpoint);
        } catch (MalformedURLException e) {
            errors.rejectValue("mspInterfaceList[" + index + "].mspEndpoint",
                "yukon.web.modules.adminSetup.interfaces.invalidURL");
        }
    }

}
