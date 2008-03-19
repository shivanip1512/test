package com.cannontech.web.editor.data;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.core.dao.DaoFactory;

public class CBCAreaData {

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

    public static List<CBCAreaData> toList(List<Integer> list) {
        List<CBCAreaData> returnList = new ArrayList<CBCAreaData>();
        for (Integer id : list) {
            CBCAreaData cbcData = new CBCAreaData();
            cbcData.setSubID(id);
            String paoName = DaoFactory.getPaoDao()
                                       .getLiteYukonPAO(id)
                                       .getPaoName();
            cbcData.setSubName(paoName);
            // Start display order at 1 for display purposes.
            // The database still starts with 0.
            cbcData.setDisplayOrder(list.indexOf(id) + 1);
            returnList.add(cbcData);
        }
        return returnList;
    }

    public static List<Integer> toIntegerList(List<CBCAreaData> assigned) {
        List<Integer> returnList = new ArrayList<Integer>();
        for (CBCAreaData data : assigned) {
            returnList.add(data.getSubID());
        }
        return returnList;
    }

}
