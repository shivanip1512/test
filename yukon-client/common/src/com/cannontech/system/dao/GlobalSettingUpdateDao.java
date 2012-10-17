package com.cannontech.system.dao;

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
     * Updates the specified setting in GlobalSetting DB table to the new value;
     */
    public void updateSettingValue(GlobalSettingType type, Object newVal);

    public void updateSetting(GlobalSetting setting);

    public void updateSettings(Iterable<GlobalSetting> settings);

}
