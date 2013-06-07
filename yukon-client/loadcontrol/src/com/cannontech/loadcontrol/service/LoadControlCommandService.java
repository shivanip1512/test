package com.cannontech.loadcontrol.service;

import com.cannontech.dr.program.service.ConstraintViolations;
import com.cannontech.messaging.message.loadcontrol.ManualControlRequestMessage;
import com.cannontech.messaging.util.BadServerResponseException;

public interface LoadControlCommandService {

    public ConstraintViolations executeManualCommand(ManualControlRequestMessage request)  throws BadServerResponseException;
}
