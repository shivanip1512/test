package com.cannontech.system.dao;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.system.YukonSetting;


/**
 * This DAO is used to access Yukon system settings (Previously Yukon Grp role properties)
 * 
 */
public interface YukonSettingsDao {
    
    /**
     * Returns the setting value of the specified Yukon setting as a String.
     * 
     * Unlike the other methods, this can be called regardless of the type
     * of the property, it essentially calls a toString on the value and
     * returns that.
     * 
     * @param setting - any YukonSetting setting
     * @return value of property
     */
    public String getSettingStringValue(YukonSetting setting);
    
    /**
     * Returns the setting value of the specified Yukon setting as a Boolean.
     * 
     * Undefined values are returned null. 
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Boolean.
     * 
     * @param setting - any YukonSetting setting with a Boolean return type
     * @return 
     */
    public Boolean getSettingBooleanValue(YukonSetting setting);
    
    /**
     * Returns the setting value of the specified Yukon setting as a Integer.
     * 
     * Undefined values are returned null. 
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Integer.
     * 
     * @param setting - any YukonSetting setting with a Integer return type
     * @return value of property or null if undefined
     */
    public Integer getSettingIntegerValue(YukonSetting setting);
    
    /**
     * Returns the setting value of the specified Yukon setting as an enum.
     * 
     * Undefined values are returned as null.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to the specified enumClass.
     * 
     * @param setting - any YukonSetting setting with an Enum return type
     * @param enumClass
     * @return
     */
    public <E extends Enum<E>> E getSettingEnumValue(YukonSetting setting, Class<E> enumClass);
    
    /**
     * This method returns the value of a Yukon Setting. Unlike getSettingBooleanValue,
     * this method will return false if undefined.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Boolean.
     * @param setting
     * @return the value of the setting, false if undefined
     */
    public boolean checkSetting(YukonSetting setting);
    
    /**
     * 
     * Can only be called on settings that have a type return that can be cast to Boolean.
     * 
     * Equivalent to calling checkSetting and returning if true, and throwing a NotAuthorizedException if false.
     * 
     * @param setting
     * @throws NotAuthorizedException
     */
    public void verifySetting(YukonSetting setting) throws NotAuthorizedException;
}
