package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteAlarmCategory;

public interface AlarmCatDao {

    public abstract LiteAlarmCategory getAlarmCategory(int alarmCategoryId);

    public abstract int getAlarmCategoryId(String categoryName);

    /**
     * Returns a sorted (by AlarmCategoryId) list of alarm categories
     */
    public abstract List<LiteAlarmCategory> getAlarmCategories();
}