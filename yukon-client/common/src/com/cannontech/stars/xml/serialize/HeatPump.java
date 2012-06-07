package com.cannontech.stars.xml.serialize;

public class HeatPump {
    private PumpType pumpType;
    private PumpSize pumpSize;
    private StandbySource standbySource;
    private int restartDelaySeconds;
    private boolean hasRestartDelaySeconds;

    public HeatPump() {
        this.pumpType = new PumpType();
        this.pumpSize = new PumpSize();
        this.standbySource = new StandbySource();
    }

    public void deleteRestartDelaySeconds() {
        this.hasRestartDelaySeconds= false;
    }

    public PumpSize getPumpSize() {
        return this.pumpSize;
    }

    public PumpType getPumpType() {
        return this.pumpType;
    }

    public int getRestartDelaySeconds() {
        return this.restartDelaySeconds;
    }

    public StandbySource getStandbySource() {
        return this.standbySource;
    }

    public boolean hasRestartDelaySeconds() {
        return this.hasRestartDelaySeconds;
    }

    public void setPumpSize(PumpSize pumpSize) {
        this.pumpSize = pumpSize;
    }

    public void setPumpType(PumpType pumpType) {
        this.pumpType = pumpType;
    }

    public void setRestartDelaySeconds(int restartDelaySeconds) {
        this.restartDelaySeconds = restartDelaySeconds;
        this.hasRestartDelaySeconds = true;
    }

    public void setStandbySource(StandbySource standbySource) {
        this.standbySource = standbySource;
    }

}
