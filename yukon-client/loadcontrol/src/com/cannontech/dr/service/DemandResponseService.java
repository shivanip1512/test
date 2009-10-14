package com.cannontech.dr.service;

import java.util.Date;

public interface DemandResponseService {
    public int getTimeSlotsForTargetCycle(Date stopTime,
            Date startTime, Integer period);
}
