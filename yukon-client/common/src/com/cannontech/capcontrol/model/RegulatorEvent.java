package com.cannontech.capcontrol.model;

import org.joda.time.Instant;

import com.cannontech.common.model.Phase;

public class RegulatorEvent {

    public static enum EventType {
        TAP_UP,
        TAP_DOWN,
        INCREASE_SETPOINT,
        DECREASE_SETPOINT,
        INTEGRITY_SCAN,
        ENABLE_REMOTE_CONTROL,
        DISABLE_REMOTE_CONTROL,
        ;
    }

    private final int id;
    private final int regulatorId;
    private final Instant timestamp;
    private final EventType type;
    private final Phase phase;
    private final int tapPosition;
    private final double setPointValue;
    private final String userName;

    protected RegulatorEvent(int id, int regulatorId, Instant timestamp, EventType type, Phase phase, int tapPosition,
            double setPointValue, String userName) {
        this.id = id;
        this.regulatorId = regulatorId;
        this.timestamp = timestamp;
        this.type = type;
        this.phase = phase;
        this.setPointValue = setPointValue;
        this.tapPosition = tapPosition;
        this.userName = userName;
    }

    public static RegulatorEvent of(int id, int regulatorId, Instant timestamp, EventType type, Phase phase,
            int tapPosition, double setPointValue, String userName) {
        return new RegulatorEvent(id, regulatorId, timestamp, type, phase, tapPosition, setPointValue, userName);
    }

    public int getId() {
        return id;
    }
    public int getRegulatorId() {
        return regulatorId;
    }
    public Instant getTimestamp() {
        return timestamp;
    }
    public EventType getType() {
        return type;
    }
    public Phase getPhase() {
        return phase;
    }
    public int getTapPosition() {
        return tapPosition;
    }
    public double getSetPointValue() {
        return setPointValue;
    }
    public String getUserName() {
        return userName;
    }
}
