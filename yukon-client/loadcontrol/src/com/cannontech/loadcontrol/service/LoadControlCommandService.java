package com.cannontech.loadcontrol.service;

import java.util.List;

import com.cannontech.dr.program.service.ConstraintContainer;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;

public interface LoadControlCommandService {

    public List<ConstraintContainer> executeManualCommand(LMManualControlRequest request);
}
