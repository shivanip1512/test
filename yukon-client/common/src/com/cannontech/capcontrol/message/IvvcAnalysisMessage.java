package com.cannontech.capcontrol.message;

import java.util.List;

import com.cannontech.capcontrol.service.IvvcAnalysisScenarioType;

public class IvvcAnalysisMessage {
    private int subBusId;
    private long timeStamp;
    private int scenarioId;
    private IvvcAnalysisScenarioType type; // not sent from ivvc
    private int numIntData;
    private int numFloatData;
    private List<Integer> intData;
    private List<Float> floatData;

    public IvvcAnalysisMessage(int subBusId, long timeStamp, int scenarioId, IvvcAnalysisScenarioType type,
                               int numIntData, int numFloatData,
                                List<Integer> intData, List<Float> floatData) {
        this.subBusId = subBusId;
        this.timeStamp = timeStamp;
        this.scenarioId = scenarioId;
        this.type = type;
        this.numIntData = numIntData;
        this.numFloatData = numFloatData;
        this.intData = intData;
        this.floatData = floatData;
    }

    public int getSubBusId() {
        return subBusId;
    }

    public void setSubBusId(int subBusId) {
        this.subBusId = subBusId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(int scenarioId) {
        this.scenarioId = scenarioId;
    }

    public IvvcAnalysisScenarioType getType() {
        return type;
    }

    public void setType(IvvcAnalysisScenarioType type) {
        this.type = type;
    }

    public int getNumIntData() {
        return numIntData;
    }

    public void setNumIntData(int numIntData) {
        this.numIntData = numIntData;
    }

    public int getNumFloatData() {
        return numFloatData;
    }

    public void setNumFloatData(int numFloatData) {
        this.numFloatData = numFloatData;
    }

    public List<Integer> getIntData() {
        return intData;
    }

    public void setIntData(List<Integer> intData) {
        this.intData = intData;
    }

    public List<Float> getFloatData() {
        return floatData;
    }

    public void setFloatData(List<Float> floatData) {
        this.floatData = floatData;
    }

}