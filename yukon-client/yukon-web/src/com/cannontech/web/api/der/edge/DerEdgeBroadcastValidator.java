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
        // Check if Payload is NULL
        validationUtils.checkIfFieldRequired("payload", errors, request.getPayload(), "Payload");
        // If Payload is not null or blank, check if Payload is valid Hex Byte String
        if (request.getPayload() != null && !request.getPayload().isBlank()) {
            validationUtils.checkIsValidHexByteString(errors, "payload", request.getPayload(), "Payload", 54);
        }
    }
}
