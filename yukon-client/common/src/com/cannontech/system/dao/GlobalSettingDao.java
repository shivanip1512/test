package com.cannontech.system.dao;

import java.util.Optional;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.model.GlobalSetting;


/**
 * This DAO is used to access Yukon system settings (Previously Yukon Grp role properties)
 *
 */
public interface GlobalSettingDao {
    
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
    public String getString(GlobalSettingType setting);
    
    /**
     * Returns the setting value of the specified Yukon setting as a String.
     * 
     * Unlike the other methods, this can be called regardless of the type
     * of the property, it essentially calls a toString on the value and
     * returns that.
     * 
     * @param setting - any GlobalSetting setting
     * @param defaultValue - the value to use if the setting is missing or empty
     * @return value of property
     */
    public String getString(GlobalSettingType setting, String defaultValue);
    
    /**
     * Returns the setting value of the specified Yukon setting as a String.
     * 
     * Unlike the other methods, this can be called regardless of the type
     * of the property, it essentially calls a toString on the value and
     * returns that.
     * 
     * @param setting - any GlobalSetting setting
     * @return Optional containing the value of the property, if present
     */
    public Optional<String> getOptionalString(GlobalSettingType setting);
    
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
    public boolean getBoolean(GlobalSettingType setting);

    /** 
     * Returns the setting value of the specified Yukon setting as a Integer. 
     * 
     * NOTE: Undefined (null) values are returned 0. 
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Integer. 
     * 
     * @param setting - any GlobalSetting setting with a Integer return type 
     * @return value of property or 0 if undefined 
     */
    public int getInteger(GlobalSettingType setting);
    
    /** 
     * Returns the setting value of the specified Yukon setting as a Integer. 
     * 
     * NOTE: Undefined values are returned null. 
     *  
     * This method may only be called with properties that have a type return type
     * that can be cast to Integer.
     * 
     * @param setting - any GlobalSetting setting with a Integer return type
     * @return value of property or 0 if undefined
     */
    public Integer getNullableInteger(GlobalSettingType type);

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
    public <E extends Enum<E>> E getEnum(GlobalSettingType setting, Class<E> enumClass);
    
    /**
     * 
     * Can only be called on settings that have a type return that can be cast to Boolean.
     * 
     * Equivalent to calling checkSetting and returning if true, and throwing a NotAuthorizedException if false.
     * 
     * @param setting
     * @throws NotAuthorizedException
     */
    public void verifySetting(GlobalSettingType setting) throws NotAuthorizedException;
    
    /**
     * Returns the setting value of the specified Yukon setting based on different conditions.  
     * It first attempts to fetch from cache, then attempts to fetch from the database, 
     * and otherwise returns the default value. 
     * @param setting - any GlobalSetting setting with an GlobalSetting return type
     * @return Object of GlobalSetting
     */

    public GlobalSetting getSetting(GlobalSettingType globalSetting);

    /**
     * Returns whether the setting value is stored in the database.
     * @param setting The setting to find
     * @return Whether the setting exists in the database 
     */
    boolean hasDatabaseEntry(GlobalSettingType setting);

    /**
     * 
     * Let the Dao know that a value has changed. This is useful
     * if the implementation utilizes a cache to clear the stale data.
     * 
     */
    public void valueChanged();

}
