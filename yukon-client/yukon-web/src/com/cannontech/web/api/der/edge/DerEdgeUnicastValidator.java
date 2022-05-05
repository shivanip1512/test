package com.cannontech.web.api.der.edge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
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
        
        //TODO validate paoName, paoType, or pao?
        
        String payload = request.getPayload();
        validationUtils.checkIsBlank(errors, "payload", payload, false, "Payload");
        validationUtils.checkHexOnlyCharacter(errors, "payload", payload, "Payload"); //^[a-zA-Z0-9]+$
        checkIfLengthDivisibleByTwo(errors, payload); //(hex bytes should each be 2 characters)
        
        //TODO payload Max length - 1280 - Does this need to be shorter for E2E packet limit? Does OSCORE affect size?
        int maxLengthInBytes = 1280;
        validationUtils.checkExceedsMaxLength(errors, "payload", payload, maxLengthInBytes * 2);
    }
    
    private void checkIfLengthDivisibleByTwo(Errors errors, String payload) {
        if (payload.length() % 2 != 0) {
            errors.rejectValue("payload", ApiErrorDetails.INVALID_FIELD_LENGTH.getCodeString(),
                    new Object[] { payload }, "");
        }
    }
}
