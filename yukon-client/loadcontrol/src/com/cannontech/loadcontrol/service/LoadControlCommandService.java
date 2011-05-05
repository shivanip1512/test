package com.cannontech.loadcontrol.service;

import com.cannontech.dr.program.service.ConstraintViolations;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;
import com.cannontech.message.util.BadServerResponseException;

public interface LoadControlCommandService {

    public ConstraintViolations executeManualCommand(LMManualControlRequest request)  throws BadServerResponseException;
}
