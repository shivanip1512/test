package com.cannontech.common.device.programming.service;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface MeterProgramValidationService {
    boolean isMeterProgramValid(UUID guid) throws InterruptedException, ExecutionException, TimeoutException;
}
