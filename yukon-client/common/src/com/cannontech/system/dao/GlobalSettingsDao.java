package com.cannontech.system.dao;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.database.db.setting.GlobalSettingDb;
import com.cannontech.system.GlobalSetting;


/**
 * This DAO is used to access Yukon system settings (Previously Yukon Grp role properties)
 * 
 */
public interface GlobalSettingsDao {
    
    /**
     * Returns the setting value of the specified Yukon setting as a String.
     * 
     * Unlike the other methods, this can be called regardless of the type
     * of the property, it essentially calls a toString on the value and
     * returns that.
     * 
     * @param setting - any GlobalSetting setting
     * @return value of property
     */
    public String getString(GlobalSetting setting);
    
    /**
     * Returns the setting value of the specified Yukon setting as a Boolean.
     * 
     * Undefined values are returned null. 
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Boolean.
     * 
     * @param setting - any GlobalSetting setting with a Boolean return type
     * @return 
     */
    public Boolean getBoolean(GlobalSetting setting);
    
    /**
     * Returns the setting value of the specified Yukon setting as a Integer.
     * 
     * Undefined values are returned null. 
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Integer.
     * 
     * @param setting - any GlobalSetting setting with a Integer return type
     * @return value of property or null if undefined
     */
    public Integer getInteger(GlobalSetting setting);
    
    /**
     * Returns the setting value of the specified Yukon setting as an enum.
     * 
     * Undefined values are returned as null.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to the specified enumClass.
     * 
     * @param setting - any GlobalSetting setting with an Enum return type
     * @param enumClass
     * @return
     */
    public <E extends Enum<E>> E getEnum(GlobalSetting setting, Class<E> enumClass);
    
    /**
     * This method returns the value of a Yukon Setting. Unlike getSettingBooleanValue,
     * this method will return false if undefined.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Boolean.
     * @param setting
     * @return the value of the setting, false if undefined
     */
    public boolean checkSetting(GlobalSetting setting);
    
    /**
     * 
     * Can only be called on settings that have a type return that can be cast to Boolean.
     * 
     * Equivalent to calling checkSetting and returning if true, and throwing a NotAuthorizedException if false.
     * 
     * @param setting
     * @throws NotAuthorizedException
     */
    public void verifySetting(GlobalSetting setting) throws NotAuthorizedException;
    
    public GlobalSettingDb getSettingDb(GlobalSetting globalSetting);
}
