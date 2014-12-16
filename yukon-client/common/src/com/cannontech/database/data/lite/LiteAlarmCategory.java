package com.cannontech.database.data.lite;

public class LiteAlarmCategory extends LiteBase {
    private String categoryName = null;
    private int notificationGroupId;

    public LiteAlarmCategory(int alarmCategoryId) {
        super();
        setAlarmCategoryId(alarmCategoryId);
        setLiteType(LiteTypes.ALARM_CATEGORIES);
    }

    public LiteAlarmCategory(int alID, String alName) {
        super();
        setAlarmCategoryId(alID);
        setCategoryName(alName);
        setLiteType(LiteTypes.ALARM_CATEGORIES);
    }

    public int getAlarmCategoryId() {
        return getLiteID();
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getNotificationGroupID() {
        return notificationGroupId;
    }

    public void setAlarmCategoryId(int alarmCategoryId) {
        setLiteID(alarmCategoryId);
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setNotificationGroupID(int newNotificationGroupId) {
        this.notificationGroupId = newNotificationGroupId;
    }

    @Override
    public String toString() {
        return getCategoryName();
    }
}