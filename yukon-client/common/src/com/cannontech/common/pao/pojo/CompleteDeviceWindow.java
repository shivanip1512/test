package com.cannontech.common.pao.pojo;

import com.cannontech.common.device.DeviceWindowType;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.cannontech.common.pao.annotation.YukonPaoPart;

@YukonPaoPart(tableName="DeviceWindow", idColumnName="deviceId")
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

    @YukonPaoField(columnName="WinOpen")
    public int getWindowOpen() {
        return windowOpen;
    }

    public void setWindowOpen(int windowOpen) {
        this.windowOpen = windowOpen;
    }

    @YukonPaoField(columnName="WinClose")
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
        final int prime = 31;
        int result = 1;
        result = prime * result + alternateClose;
        result = prime * result + alternateOpen;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + windowClose;
        result = prime * result + windowOpen;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CompleteDeviceWindow other = (CompleteDeviceWindow) obj;
        if (alternateClose != other.alternateClose)
            return false;
        if (alternateOpen != other.alternateOpen)
            return false;
        if (type != other.type)
            return false;
        if (windowClose != other.windowClose)
            return false;
        if (windowOpen != other.windowOpen)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CompleteDeviceWindow [type=" + type + ", windowOpen=" + windowOpen
               + ", windowClose=" + windowClose + ", alternateOpen=" + alternateOpen
               + ", alternateClose=" + alternateClose + "]";
    }
}
