package com.cannontech.messaging.message.capcontrol;

import java.util.List;

import com.cannontech.messaging.message.capcontrol.streamable.Area;

public class SubAreasMessage extends CapControlMessage {

    public static final int AREA_ALL = 0x00000001;
    public static final int AREA_DEL = 0x00000002;
    public static final int AREA_ADD = 0x00000004;
    public static final int AREA_UPDATE = 0x00000008;

    private List<Area> areas;
    private int msgInfoBitMask = 0;

    public int getMsgInfoBitMask() {
        return msgInfoBitMask;
    }

    public void setMsgInfoBitMask(int msgInfoBitMask) {
        this.msgInfoBitMask = msgInfoBitMask;
    }

    public Area getArea(int index) {
        return getAreas().get(index);
    }

    public List<Area> getAreas() {
        return areas;
    }

    public boolean isAllAreas() {
        return (msgInfoBitMask & AREA_ALL) > 0;
    }

    public boolean isAddAreas() {
        return (msgInfoBitMask & AREA_ADD) > 0;
    }

    public boolean isUpdateAreas() {
        return (msgInfoBitMask & AREA_UPDATE) > 0;
    }

    public int getNumberOfAreas() {
        return areas.size();
    }

    public void setAreas(List<Area> a) {
        areas = a;
    }
}
