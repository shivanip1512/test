package com.cannontech.stars.dr.jms.service;

public interface DrProgramStatusService {
    /**
     * Sending program status in 5 min interval time with initial delay of 5 min
     */
    public void sendProgramStatus();
}
