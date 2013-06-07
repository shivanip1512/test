package com.cannontech.messaging.message.loadcontrol.data;

public class ProgramControlWindow {

    private int yukonId;
    private int windowNumber;
    private int availableStartTime;
    private int availableStopTime;

    public int getAvailableStartTime() {
        return availableStartTime;
    }

    public int getAvailableStopTime() {
        return availableStopTime;
    }

    public int getWindowNumber() {
        return windowNumber;
    }

    public int getYukonId() {
        return yukonId;
    }

    public void setAvailableStartTime(int newAvailableStartTime) {
        availableStartTime = newAvailableStartTime;
    }

    public void setAvailableStopTime(int newAvailableStopTime) {
        availableStopTime = newAvailableStopTime;
    }

    public void setWindowNumber(int newWindowNumber) {
        windowNumber = newWindowNumber;
    }

    public void setYukonId(int newYukonId) {
        yukonId = newYukonId;
    }
}
