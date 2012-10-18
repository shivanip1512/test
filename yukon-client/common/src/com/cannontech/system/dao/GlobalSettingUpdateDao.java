package com.cannontech.system.dao;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.model.GlobalSetting;

/**
 *
 * The class handles Yukon System wide settings which were previously associated 
 * with Yukon Grp role properties.
 * 
 * This is primarily intended for updating the settings and not for reading settings.
 * To read Yukon settings there is GlobalSettingsDao
 * 
 */
public interface GlobalSettingUpdateDao {
    
    /**
     * Updates the specified setting in GlobalSetting DB table to the new value.
     * If the setting is actually changing, the event will be logged in EventLog.
     * If the user is null the user will be 'yukon'.
     */
    public void updateSettingValue(GlobalSettingType type, Object newVal, LiteYukonUser user);

    /**
     * Updates the specified setting in GlobalSetting DB table to the new value.
     * If the setting is actually changing, the event will be logged in EventLog.
     * If the user is null the user will be 'yukon'.
     */
    public void updateSetting(GlobalSetting setting, LiteYukonUser user);

    /**
     * Updates the specified settings in GlobalSetting DB table to the new value.
     * If the setting is actually changing, the event will be logged in EventLog.
     * If the user is null the user will be 'yukon'.
     */
    public void updateSettings(Iterable<GlobalSetting> settings, LiteYukonUser user);

}
