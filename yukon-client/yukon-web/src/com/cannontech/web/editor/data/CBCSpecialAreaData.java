package com.cannontech.web.editor.data;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.core.dao.DaoFactory;

public class CBCSpecialAreaData {

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

    public static List<CBCSpecialAreaData> toList(List<Integer> list) {
        List<CBCSpecialAreaData> returnList = new ArrayList<CBCSpecialAreaData>();
        for (Integer id : list) {
            CBCSpecialAreaData cbcData = new CBCSpecialAreaData();
            cbcData.setSubID(id);
            String paoName = DaoFactory.getPaoDao()
                                       .getLiteYukonPAO(id)
                                       .getPaoName();
            cbcData.setSubName(paoName);
            cbcData.setDisplayOrder(list.indexOf(id));
            returnList.add(cbcData);
        }
        return returnList;
    }

    public static List<Integer> toIntegerList(List<CBCSpecialAreaData> assigned) {
        List<Integer> returnList = new ArrayList<Integer>();
        for (CBCSpecialAreaData data : assigned) {
            returnList.add(data.getSubID());
        }
        return returnList;
    }

}
