package com.cannontech.messaging.message.dispatch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cannontech.messaging.message.BaseMessage;

public class PointRegistrationMessage extends BaseMessage {

    public static final int REG_NOTHING = 0x00000000;
    public static final int REG_ALL_STATUS = 0x00000001;
    public static final int REG_ALL_ANALOG = 0x00000002;
    public static final int REG_ALL_ACCUMULATOR = 0x00000004;
    public static final int REG_ALL__CALCULATED = 0x00000008;
    public static final int REG_EVENTS = 0x00000010;
    public static final int REG_ALARMS = 0x00000020;
    public static final int REG_ALL_PTS_MASK = 0x0000000f;
    public static final int REG_ADD_POINTS = 0x00000100;
    public static final int REG_REMOVE_POINTS = 0x00000200;
    public static final int REG_LOAD_PROFILE = 0x00000040;
    public static final int REG_NO_UPLOAD = 0x00010000;

    private int regFlags;
    private Set<Integer> pointIds;

    public int getRegFlags() {
        return regFlags;
    }

    public void setPointIds(Set<Integer> pointIds) {
        this.pointIds = pointIds;
    }

    public Set<Integer> getPointIds() {
        return pointIds;
    }

    public void setRegFlags(int newValue) {
        this.regFlags = newValue;
    }

    /**
     * @deprecated
     */
    public void setPointList(List<Integer> newValue) {
        pointIds = new HashSet(newValue);
    }

    /**
     * @deprecated
     */
    public List<Integer> getPointList() {
        return new ArrayList<Integer>(pointIds);
    }

}
