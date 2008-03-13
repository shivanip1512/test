package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteAlarmCategory;

public interface AlarmCatDao {

    /* Insert the method's description here.
     * Creation date: (3/26/2001 9:41:59 AM)
     * @return com.cannontech.database.data.lite.LitePoint
     * @param pointID int
     */
    public abstract String getAlarmCategoryName(int alarmCatID);

    public abstract LiteAlarmCategory getAlarmCategory(int id);

    public abstract int getAlarmCategoryId(String categoryName);

    // method to return a List of LiteAlarmCategories
    public abstract List<LiteAlarmCategory> getAlarmCategories();

}