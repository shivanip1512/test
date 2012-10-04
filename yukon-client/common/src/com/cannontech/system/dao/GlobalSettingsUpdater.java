package com.cannontech.system.dao;

import com.cannontech.system.GlobalSetting;

/**
 *
 * The class handles Yukon System wide settings which were previously associated 
 * with Yukon Grp role properties.
 * 
 * This is primarily intended for updating the settings and not for reading settings.
 * To read Yukon settings there is GlobalSettingsDaoImpl
 * 
 */
public interface GlobalSettingsUpdater {
    
    /**
     * Updates the specified setting in GlobalSetting DB table to the value newVal;
     * 
     * @param setting
     * @param newVal
     */
    public void updateSetting(GlobalSetting setting, String newVal);
}
