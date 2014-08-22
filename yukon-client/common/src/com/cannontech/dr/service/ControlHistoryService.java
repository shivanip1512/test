package com.cannontech.dr.service;

import org.joda.time.Instant;

/**
 * Service for sending control history messages to dispatch.
 * 
 * Most types of control, including RFN and PLC, send control messages through Porter. In these cases, Porter will
 * handle control history. For other types of control that do not go through Porter, use this service.
 */
public interface ControlHistoryService {
    
    /**
     * Send a control history message to dispatch for a shed.
     * @param associationId May be null if there is no event id to associate.
     */
    public void sendControlHistoryShedMessage(int groupId, Instant startTime, ControlType controlType,
                                              Integer associationId, int controlDurationSeconds, int reductionRatio);
    
    /**
     * Send a control history message to dispatch for a restore.
     */
    public void sendControlHistoryRestoreMessage(int groupId, Instant time);
}
