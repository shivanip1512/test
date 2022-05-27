package com.cannontech.web.api.der.edge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;

@Service
public class DerEdgeBroadcastValidator extends SimpleValidator<EdgeBroadcastRequest> {

    @Autowired YukonApiValidationUtils validationUtils;

    public DerEdgeBroadcastValidator() {
        super(EdgeBroadcastRequest.class);
    }

    @Override
    protected void doValidation(EdgeBroadcastRequest request, Errors errors) {
        validationUtils.checkIsValidHexByteString(errors, "payload", request.getPayload(), "Payload", 54);
    }
}
