package com.cannontech.system.dao;

import com.cannontech.system.YukonSetting;

/**
 *
 * The class handles Yukon System wide settings which were previously associated 
 * with Yukon Grp role properties.
 * 
 * This is primarily intended for updating the settings and not for reading settings.
 * To read Yukon settings there is YukonSettingsDaoImpl
 * 
 */
public interface YukonSettingsUpdater {
    
    /**
     * Updates the specified setting in YukonSetting DB table to the value newVal;
     * 
     * @param setting
     * @param newVal
     */
    public void updateSetting(YukonSetting setting, String newVal);
}
