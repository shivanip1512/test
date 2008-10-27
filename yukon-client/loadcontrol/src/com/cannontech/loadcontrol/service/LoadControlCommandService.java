package com.cannontech.loadcontrol.service;

import java.util.List;

import com.cannontech.loadcontrol.messages.LMManualControlRequest;

public interface LoadControlCommandService {

    public List<String> executeManualCommand(LMManualControlRequest request);
}
