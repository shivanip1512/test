package com.cannontech.common.dr.setup;

public class ControlRawState {

    public ControlRawState() {

    }

    public ControlRawState(Integer rawState, String stateText) {
        this.rawState = rawState;
        this.stateText = stateText;
    }

    private Integer rawState;
    private String stateText;

    public Integer getRawState() {
        return rawState;
    }

    public void setRawState(Integer rawState) {
        this.rawState = rawState;
    }

    public String getStateText() {
        return stateText;
    }

    public void setStateText(String stateText) {
        this.stateText = stateText;
    }

}
