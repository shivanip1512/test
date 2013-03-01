package com.cannontech.stars.energyCompany.dao;

import java.util.List;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.model.EnergyCompanySetting;

public interface EnergyCompanySettingDao {
    
    
    /**
     * Returns true if this setting is enabled. This is NOT the boolean value of the setting. 
     * 
     * @return true if this setting is ENABLED || ALWAYS_ENBALED
     */
    public boolean isSet(EnergyCompanySettingType setting, int ecId);
    
    /**
     * Returns the setting value of the specified setting as a String.
     * 
     * Unlike the other methods, this can be called regardless of the type
     * of the property, EnergyCompanySettingType calls a toString on the value and
     * returns that.
     * 
     * @param setting - any EnergyCompanySettingType setting
     * 
     * @return value of property
     */
    public String getString(EnergyCompanySettingType setting, int ecId);
    
    /**
     * Returns the setting value of the specified EC setting as a Boolean.
     * 
     * Undefined values are returned null. 
     * 
     * This method may only be called with properties thaEnergyCompanySettingType have a type return type 
     * thaEnergyCompanySettingType can be casEnergyCompanySettingType to Boolean.
     * 
     * @param setting - any EnergyCompanySettingType setting with a Boolean return type
     * 
     * @return value of property or null if undefined
     */
    public Boolean getBoolean(EnergyCompanySettingType setting, int ecId);
    
    /**
     * Returns the setting value of the specified EC setting as a Integer.
     * 
     * Undefined values are returned null. 
     * 
     * This method may only be called with settings that have a type return type 
     * that can be cast to Integer.
     * 
     * @param setting - any EnergyCompanySettingType setting with a Integer return type
     * 
     * @return value of property or null if undefined
     */
    public Integer getInteger(EnergyCompanySettingType setting, int ecId);
    
    /**
     * Returns the setting value of the specified EC setting as an enum.
     * 
     * Undefined values are returned as null.
     * 
     * This method may only be called with settings that have a type return type 
     * that can be cast to the specified enumClass.
     * 
     * @param setting - any EnergyCompanySettingType setting with an enum return type
     * 
     * @return value of property or null if undefined
     */
    public <E extends Enum<E>> E getEnum(EnergyCompanySettingType setting, Class<E> enumClass, int ecId);
    
    /**
     * This method returns the value of an EC Setting. Unlike getSettingBooleanValue,
     * this method will return false if undefined.
     * 
     * This method may only be called with settings that have a type return type 
     * that can be cast to Boolean.

     * @return the value of the setting, false if undefined
     */
    public boolean checkSetting(EnergyCompanySettingType setting, int ecId);
    
    /**
     * This method returns the value of a EnergyCOmpany Setting. Unlike getSettingBooleanValue,
     * this method will return false if undefined.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Boolean.
     * 
     * Note: this is not the same as !checkSetting(). If setting is null in database this will return false.
     * 
     * @return the value of the setting, false if undefined
     */
    public boolean checkFalseSetting(EnergyCompanySettingType setting, int ecId);

    
    /**
     * 
     * Can only be called on settings thaEnergyCompanySettingType have a type return thaEnergyCompanySettingType
     * can be casEnergyCompanySettingType to Boolean.
     * 
     * EquivalenEnergyCompanySettingType to calling checkSetting and returning if true, and 
     * throwing a NotAuthorizedException if false.
     */
    public void verifySetting(EnergyCompanySettingType setting, int ecId) throws NotAuthorizedException;
    
    public EnergyCompanySetting getSetting(EnergyCompanySettingType energyCompanySettingType, int ecId);
    
    /**
     * Returns all energyCompanySetting's for energy company with id ecId
     */
    public List<EnergyCompanySetting> getAllSettings(int ecId);

    /**
     * 
     * Let the Dao know that a value has changed. This is useful
     * if the implementation utilizes a cache to clear the stale data.
     * 
     */
    public void valueChanged();
    
    /**
     * Updates the specified setting in EnergyCompanySetting DB table to the new value.
     * If the setting is actually changing, the event will be logged in EventLog.
     * If the user is null the user will be 'yukon'.
     */
    public void updateSetting(EnergyCompanySetting setting, LiteYukonUser user, int ecId);

    /**
     * Updates the specified setting in EnergyCompanySetting DB table to the new value.
     * If the setting is actually changing, the event will be logged in EventLog.
     * If the user is null the user will be 'yukon'.
     */
    public void updateSettingValue(EnergyCompanySettingType setting, Object value, LiteYukonUser user, int ecId);
    
    /**
     * Updates the specified settings in EnergyCompanySetting DB table to the new value.
     * If the setting is actually changing, the event will be logged in EventLog.
     * If the user is null the user will be 'yukon'.
     */
    public void updateSettings(Iterable<EnergyCompanySetting> settings, LiteYukonUser user, int ecId);

}
