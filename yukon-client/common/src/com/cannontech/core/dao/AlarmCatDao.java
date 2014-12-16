package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteAlarmCategory;

public interface AlarmCatDao {

    /**
     * Loads from database directly.
     */
    public abstract LiteAlarmCategory getAlarmCategory(int alarmCategoryId);
    /**
     * Loads from databaseCache
     */
    public abstract LiteAlarmCategory getAlarmCategoryFromCache(int alarmCategoryId);

    /**
     * Loads from databaseCache
     */
    public abstract int getAlarmCategoryIdFromCache(String categoryName);

    /**
     * Loads from databaseCache
     */
    public abstract List<LiteAlarmCategory> getAlarmCategories();
}