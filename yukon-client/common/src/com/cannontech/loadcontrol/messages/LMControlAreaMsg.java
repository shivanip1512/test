package com.cannontech.loadcontrol.messages;

import java.util.List;

import com.cannontech.loadcontrol.data.LMControlArea;

public class LMControlAreaMsg extends LMMessage {
    private Integer msgInfoBitMask;
    private List<LMControlArea> lmControlAreaVector;

    public static final int AREA_ALL = 0x00000001;
    public static final int AREA_DELETE = 0x00000002;

    public LMControlAreaMsg() {
        super();
    }

    public boolean isDeletedCntrlArea() {
        return (getMsgInfoBitMask().intValue() & AREA_DELETE) > 0;
    }

    public LMControlArea getLMControlArea(int index) {
        return (LMControlArea) lmControlAreaVector.get(index);
    }

    public List<LMControlArea> getLMControlAreaVector() {
        return lmControlAreaVector;
    }

    public int getNumberOfLMControlAreas() {
        return lmControlAreaVector.size();
    }

    public void setLMControlAreaVector(List<LMControlArea> lmContAreas) {
        lmControlAreaVector = lmContAreas;
    }

    public Integer getMsgInfoBitMask() {
        return msgInfoBitMask;
    }

    public void setMsgInfoBitMask(Integer msgInfoBitMask) {
        this.msgInfoBitMask = msgInfoBitMask;
    }
}
