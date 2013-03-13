package com.cannontech.stars.energyCompany.dao;

import java.util.List;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.model.EnergyCompanySetting;

public interface EnergyCompanySettingDao {
    
    
    /**
     * Returns true if this setting is enabled. This is NOT the boolean value of the setting. 
     */
    public boolean isEnabled(EnergyCompanySettingType setting, int ecId);
    
    /**
     * Returns the setting value of the specified setting as a String.
     * 
     * Unlike the other methods, this can be called regardless of the type
     * of the property, EnergyCompanySettingType calls a toString on the value and
     * returns that.
     */
    public String getString(EnergyCompanySettingType setting, int ecId);
    
    /**
     * Returns the value of the specified setting as an int.
     * 
     * This method may only be called with settings that have a type return type 
     * that can be cast to Integer.
     * 
     * @param setting - any EnergyCompanySettingType setting with a Integer return type
     * 
     * @return value of setting or 0 if undefined (This includes a NULL or '' found in DB)
     */
    public int getInteger(EnergyCompanySettingType setting, int ecId);
    
    /**
     * Returns the value of the specified EC setting as an enum.
     * 
     * This method may only be called with settings that have a type return type 
     * that can be cast to the specified enumClass.
     * 
     * @param setting - any EnergyCompanySettingType setting with an enum return type
     * 
     * @return value of setting or null if undefined
     */
    public <E extends Enum<E>> E getEnum(EnergyCompanySettingType setting, Class<E> enumClass, int ecId);
    
    /**
     * Returns the value of the specified setting as a boolean.
     * 
     * This method may only be called with settings that have a type return type 
     * that can be cast to Boolean.

     * @return value of setting or false if undefined (This includes a NULL or '' found in DB)
     */
    public boolean getBoolean(EnergyCompanySettingType setting, int ecId);
    
    /**
     * Returns the negated value of the specified setting as a boolean.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Boolean.
     * 
     * Note: this is not the same as !checkSetting(). If setting is null in database this will return false.
     * 
     * @return negated value of setting or false if undefined (This includes a NULL or '' found in DB)
     */
    public boolean getFalseBoolean(EnergyCompanySettingType setting, int ecId);
    
    /**
     * Can only be called on settings thaEnergyCompanySettingType have a type return thaEnergyCompanySettingType
     * can be casEnergyCompanySettingType to Boolean.
     * 
     * Equivalent to calling getBoolean() and returning if true, and 
     * throwing a NotAuthorizedException if false.
     */
    public void verifySetting(EnergyCompanySettingType setting, int ecId) throws NotAuthorizedException;
    
    public EnergyCompanySetting getSetting(EnergyCompanySettingType energyCompanySettingType, int ecId);
    
    public List<EnergyCompanySetting> getAllSettings(int ecId);

    /**
     * Let the Dao know that a value has changed. This is useful
     * if the implementation utilizes a cache to clear the stale data.
     */
    public void valueChanged();
    
    /**
     * Updates the specified setting in EnergyCompanySetting DB table to the new value.
     * 
     * If the setting is actually changing, the event will be logged in EventLog.
     * If the user is null the user will be 'yukon'.
     */
    public void updateSetting(EnergyCompanySetting setting, LiteYukonUser user, int ecId);

    /**
     * Updates the specified setting in EnergyCompanySetting DB table to the new value.
     * 
     * If the setting is actually changing, the event will be logged in EventLog.
     * If the user is null the user will be 'yukon'.
     */
    public void updateSettingValue(EnergyCompanySettingType setting, Object value, LiteYukonUser user, int ecId);
    
    /**
     * Updates the specified settings in EnergyCompanySetting DB table to the new value.
     * 
     * If the setting is actually changing, the event will be logged in EventLog.
     * If the user is null the user will be 'yukon'.
     */
    public void updateSettings(Iterable<EnergyCompanySetting> settings, LiteYukonUser user, int ecId);

}
