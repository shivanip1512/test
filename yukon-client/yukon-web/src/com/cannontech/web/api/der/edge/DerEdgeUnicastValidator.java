package com.cannontech.web.api.der.edge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;

@Service
public class DerEdgeUnicastValidator extends SimpleValidator<EdgeUnicastRequest> {

    @Autowired YukonApiValidationUtils validationUtils;

    public DerEdgeUnicastValidator() {
        super(EdgeUnicastRequest.class);
    }

    @Override
    protected void doValidation(EdgeUnicastRequest request, Errors errors) {
        validationUtils.checkIsValidHexByteString(errors, "payload", request.getPayload(), "Payload", 1280);
    }

}
