package com.cannontech.dr.scenario.model;

import org.joda.time.Duration;

public class ScenarioProgram {
    private int scenarioId;
    private int programId;
    private Duration startOffset;
    private Duration stopOffset;
    private int startGear;

    public ScenarioProgram(int scenarioId, int programId,
            int startOffsetInSeconds, int stopOffsetInSeconds, int startGear) {
        this.scenarioId = scenarioId;
        this.programId = programId;
        this.startOffset = Duration.standardSeconds(startOffsetInSeconds);
        this.stopOffset = Duration.standardSeconds(stopOffsetInSeconds);
        this.startGear = startGear;
    }

    public int getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(int scenarioId) {
        this.scenarioId = scenarioId;
    }

    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

    public Duration getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(Duration startOffset) {
        this.startOffset = startOffset;
    }

    public Duration getStopOffset() {
        return stopOffset;
    }

    public void setStopOffset(Duration stopOffset) {
        this.stopOffset = stopOffset;
    }

    public int getStartGear() {
        return startGear;
    }

    public void setStartGear(int startGear) {
        this.startGear = startGear;
    }
}
