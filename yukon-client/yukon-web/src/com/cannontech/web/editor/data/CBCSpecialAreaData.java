package com.cannontech.web.editor.data;

import java.util.ArrayList;
import java.util.List;

public class CBCSpecialAreaData {
    
    public CBCSpecialAreaData() {
        super();
    }
    
    public CBCSpecialAreaData(Integer subId, String subName, Integer displayOrder) {
        super();
        this.subID = subId;
        this.subName = subName;
        this.displayOrder = displayOrder;
    }

    private Integer subID;
    private String subName;
    private int displayOrder;

    public void setSubID(Integer id) {
        subID = id;
    }

    public void setSubName(String paoName) {
        subName = paoName;
    }

    public void setDisplayOrder(int i) {
        displayOrder = i;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public Integer getSubID() {
        return subID;
    }

    public String getSubName() {
        return subName;
    }

    public static List<Integer> toIntegerList(List<CBCSpecialAreaData> assigned) {
        List<Integer> returnList = new ArrayList<Integer>();
        for (CBCSpecialAreaData data : assigned) {
            returnList.add(data.getSubID());
        }
        return returnList;
    }

}
