package com.cannontech.common.pao.model;

import com.cannontech.common.device.DeviceWindowType;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.cannontech.common.pao.annotation.YukonPaoPart;
import com.google.common.base.Objects;

@YukonPaoPart(idColumnName = "deviceId")
public class CompleteDeviceWindow {
    private DeviceWindowType type = DeviceWindowType.SCAN;
    private int windowOpen = 0;
    private int windowClose = 0;
    private int alternateOpen = 0;
    private int alternateClose = 0;

    @YukonPaoField
    public DeviceWindowType getType() {
        return type;
    }

    public void setType(DeviceWindowType type) {
        this.type = type;
    }

    @YukonPaoField(columnName = "WinOpen")
    public int getWindowOpen() {
        return windowOpen;
    }

    public void setWindowOpen(int windowOpen) {
        this.windowOpen = windowOpen;
    }

    @YukonPaoField(columnName = "WinClose")
    public int getWindowClose() {
        return windowClose;
    }

    public void setWindowClose(int windowClose) {
        this.windowClose = windowClose;
    }

    @YukonPaoField
    public int getAlternateOpen() {
        return alternateOpen;
    }

    public void setAlternateOpen(int alternateOpen) {
        this.alternateOpen = alternateOpen;
    }

    @YukonPaoField
    public int getAlternateClose() {
        return alternateClose;
    }

    public void setAlternateClose(int alternateClose) {
        this.alternateClose = alternateClose;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type, windowOpen, windowClose, alternateOpen, alternateClose);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() != CompleteDeviceWindow.class) {
            return false;
        }
        CompleteDeviceWindow that = (CompleteDeviceWindow) other;
        return type == that.type && windowOpen == that.windowOpen && windowClose == that.windowClose
            && alternateOpen == that.alternateOpen && alternateClose == that.alternateClose;
    }

    @Override
    public String toString() {
        return "CompleteDeviceWindow [type=" + type + ", windowOpen=" + windowOpen + ", windowClose=" + windowClose
            + ", alternateOpen=" + alternateOpen + ", alternateClose=" + alternateClose + "]";
    }
}
