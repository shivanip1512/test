package com.cannontech.messaging.message.loadcontrol;

import java.util.List;

import com.cannontech.messaging.message.loadcontrol.data.ControlAreaItem;

public class ControlAreaMessage extends LmMessage {

    private Integer msgInfoBitMask;
    private List<ControlAreaItem> lmControlAreaVector;

    public static final int AREA_ALL = 0x00000001;
    public static final int AREA_DELETE = 0x00000002;

    public boolean isDeletedCntrlArea() {
        return (getMsgInfoBitMask() & AREA_DELETE) > 0;
    }

    public ControlAreaItem getLMControlArea(int index) {
        return (ControlAreaItem) lmControlAreaVector.get(index);
    }

    public List<ControlAreaItem> getLMControlAreaVector() {
        return lmControlAreaVector;
    }

    public int getNumberOfLMControlAreas() {
        return lmControlAreaVector.size();
    }

    public void setLMControlAreaVector(List<ControlAreaItem> lmContAreas) {
        lmControlAreaVector = lmContAreas;
    }

    public Integer getMsgInfoBitMask() {
        return msgInfoBitMask;
    }

    public void setMsgInfoBitMask(Integer msgInfoBitMask) {
        this.msgInfoBitMask = msgInfoBitMask;
    }
}
