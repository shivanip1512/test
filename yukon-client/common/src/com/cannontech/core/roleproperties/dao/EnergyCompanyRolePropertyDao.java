package com.cannontech.core.roleproperties.dao;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.roleproperties.UserNotInRoleException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

public interface EnergyCompanyRolePropertyDao {
    
    /**
     * Returns the energy company's value of the specified role property as a String.
     * 
     * This method will never return null. Undefined values are returned as "".
     * 
     * Unlike the other methods, this can be called regardless of the type
     * of the property, it essentially calls a toString on the value and
     * returns that.
     * 
     * @param property any property
     * @return  "" or the value of the property
     * @throws UserNotInRoleException user must be in role to access a role property
     */
    public String getPropertyStringValue(YukonRoleProperty property, YukonEnergyCompany energyCompany) throws UserNotInRoleException;
    
    /**
     * Returns the energy company's value of the specified role property as a boolean.
     * 
     * Undefined values are returned as false.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Boolean.
     * 
     * @param property any property with a Boolean return type
     * @return  the value of the property or false if undefined
     * @throws UserNotInRoleException user must be in role to access a role property
     */
    public boolean getPropertyBooleanValue(YukonRoleProperty property, YukonEnergyCompany energyCompany) throws UserNotInRoleException;
    
    /**
     * Returns the energy company's value of the specified role property as a long.
     * 
     * Undefined values are returned as 0.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Number. Rounding or truncation may occur to return an long.
     * 
     * @param property any property with a Number return type
     * @return  the value of the property or 0 if undefined
     * @throws UserNotInRoleException user must be in role to access a role property
     */
    public long getPropertyLongValue(YukonRoleProperty property, YukonEnergyCompany energyCompany) throws UserNotInRoleException;
    
    /**
     * Returns the energy company's value of the specified role property as an integer.
     * 
     * Undefined values are returned as 0.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Number. Rounding or truncation may occur to return an int.
     * 
     * @param property any property with a Number return type
     * @return  the value of the property or 0 if undefined
     * @throws UserNotInRoleException user must be in role to access a role property
     */
    public int getPropertyIntegerValue(YukonRoleProperty property, YukonEnergyCompany energyCompany) throws UserNotInRoleException;
    
    /**
     * Returns the energy company's value of the specified role property as a float.
     * 
     * Undefined values are returned as 0.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Number. Rounding or truncation may occur to return an float.
     * 
     * @param property any property with a Number return type
     * @return  the value of the property or 0 if undefined
     * @throws UserNotInRoleException user must be in role to access a role property
     */
    public float getPropertyFloatValue(YukonRoleProperty property, YukonEnergyCompany energyCompany) throws UserNotInRoleException;
    
    /**
     * Returns the energy company's value of the specified role property as a double.
     * 
     * Undefined values are returned as 0.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Number. Rounding or truncation may occur to return a double.
     * 
     * @param property any property with a Number return type
     * @return  the value of the property or 0 if undefined
     * @throws UserNotInRoleException user must be in role to access a role property
     */
    public double getPropertyDoubleValue(YukonRoleProperty property, YukonEnergyCompany energyCompany) throws UserNotInRoleException;
    
    /**
     * Returns the energy company's value of the specified role property as an enum.
     * 
     * Undefined values are returned as null.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to the specified enumClass.
     * 
     * @param property any property with a return type of enumClass
     * @return  the value of the property or null if undefined
     * @throws UserNotInRoleException user must be in role to access a role property
     */
    public <E extends Enum<E>> E getPropertyEnumValue(YukonRoleProperty property, Class<E> enumClass, YukonEnergyCompany energyCompany) throws UserNotInRoleException;
    
    /**
     * This method returns true if it is valid to call checkProperty, checkFalseProperty,
     * checkAllProperties, checkAnyProperties, verifyProperty, or verifyFalseProperty 
     * with the supplied property.
     * 
     * This essentially checks if the property's type has a return type 
     * that can be cast to Boolean.
     * 
     * @param property any property with a Boolean return type
     * @return  the value of the property or false if undefined
     */
    public boolean isCheckPropertyCompatible(YukonRoleProperty property);
    
    /**
     * This method returns the value of a boolean property. Unlike getPropertyBooleanValue,
     * this method will never throw a UserNotInRoleException, if the energy company is not
     * in the property's role, this method will simply return false.
     * 
     * Undefined values are returned as false.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Boolean.
     * 
     * @param property any property with a Boolean return type
     * @return  the value of the property or false if undefined
     */
    public boolean checkProperty(YukonRoleProperty property, YukonEnergyCompany energyCompany);
    
    /**
     * This method returns true if a call to checkProperty for all of the properties
     * would have returned true. This is equivalent to:
     * 
     * <pre>
     * for (YukonRoleProperty property : properties) {
     *     if (!checkProperty(property)) return false;
     * }
     * return true;
     * </pre>
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Boolean.
     * 
     * @param firstProperty any property with a Boolean return type
     * @param otherProperties any properties with a Boolean return type
     * @return  the AND'd value of each of the properties
     */
    public boolean checkAllProperties(YukonEnergyCompany energyCompany, YukonRoleProperty firstProperty, YukonRoleProperty... otherProperties);
    
    /**
     * This method returns true if a call to checkProperty for any of the properties
     * would have returned true. This is equivalent to:
     * 
     * <pre>
     * for (YukonRoleProperty property : properties) {
     *     if (checkProperty(property)) return true;
     * }
     * return false;
     * </pre>
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Boolean.
     * 
     * @param firstProperty any property with a Boolean return type
     * @param otherProperties any properties with a Boolean return type
     * @return  the OR'd value of each of the properties
     */
    public boolean checkAnyProperties(YukonEnergyCompany energyCompany, YukonRoleProperty firstProperty, YukonRoleProperty... otherProperties);
    
    /**
     * This method returns the negated value of a boolean property. Unlike getPropertyBooleanValue,
     * this method will never throw a UserNotInRoleException, if the energy company is not
     * in the properties role, this method will simply return false.
     * 
     * Undefined values are returned as false.
     * 
     * Note, this is NOT the same as calling !checkProperty(). This method makes most
     * sense when accessing properties that are negated by name (e.g. HIDE_XXX).
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Boolean.
     * 
     * @param property any property with a Boolean return type
     * @return  the value of the property or false if undefined
     */
    public boolean checkFalseProperty(YukonRoleProperty property, YukonEnergyCompany energyCompany);
    
    /**
     * Equivalent to calling checkProperty for each property and returning if any are true, and 
     * throwing a NotAuthorizedException if none are true.
     * 
     * @param properties any properties with a Boolean return type
     */
    public void verifyAnyProperties(YukonEnergyCompany energyCompany, YukonRoleProperty firstProperty,
            YukonRoleProperty... otherProperties) throws NotAuthorizedException;

    /**
     * Equivalent to calling checkProperty and returning if true, and throwing
     * a NotAuthorizedException if false.
     * 
     * @param property any property with a Boolean return type
     * @throws NotAuthorizedException
     */
    public void verifyProperty(YukonRoleProperty property, YukonEnergyCompany energyCompany) throws NotAuthorizedException;

    /**
     * Equivalent to calling checkFalseProperty and returning if true, and throwing
     * a NotAuthorizedException if false.
     * 
     * @param property any property with a Boolean return type
     * @throws NotAuthorizedException
     */
    public void verifyFalseProperty(YukonRoleProperty property, YukonEnergyCompany energyCompany) throws NotAuthorizedException;
    
}