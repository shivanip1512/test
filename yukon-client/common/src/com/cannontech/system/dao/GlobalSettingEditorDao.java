package com.cannontech.system.dao;

import java.util.Map;

import com.cannontech.common.util.Pair;
import com.cannontech.system.GlobalSettingSubCategory;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.model.GlobalSetting;

public interface GlobalSettingEditorDao {

    /**
     * Returns all settings for the specified category.  Settings that are missing from the database
     * will be included with values set to the default for that setting.
     */
    public Map<GlobalSettingType, GlobalSetting> getSettingsForCategory(GlobalSettingSubCategory category);

    /**
     * Returns all values for each setting type in the specified category.  Settings that are missing from the database
     * will be included with values set to the default for that setting.  This method internally uses {@link #getSettingsForCategory(GlobalSettingSubCategory)}
     * any changes to that method can affect the outcome of this method.
     */
    public Map<GlobalSettingType, Pair<Object, String>> getValuesAndCommentsForCategory(GlobalSettingSubCategory category);
    
}