package com.cannontech.messaging.message.capcontrol;

import java.util.List;

import com.cannontech.messaging.message.capcontrol.streamable.SpecialArea;

/**
 */
public class SpecialAreasMessage extends CapControlMessage {

    // contains Strings
    private List<SpecialArea> areas;

    public SpecialArea getArea(int index) {
        return getAreas().get(index);
    }

    public List<SpecialArea> getAreas() {
        return areas;
    }

    public int getNumberOfAreas() {
        return areas.size();
    }

    public void setAreas(List<SpecialArea> a) {
        areas = a;
    }
}
