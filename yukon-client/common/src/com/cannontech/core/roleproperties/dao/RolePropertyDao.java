package com.cannontech.core.roleproperties.dao;

import java.util.Set;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.roleproperties.AccessLevel;
import com.cannontech.core.roleproperties.CapControlCommandsAccessLevel;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.UserNotInRoleException;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleCategory;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.ImmutableSet;

/**
 * This DAO is used to access role and property information for users.
 * 
 * Some of the methods indicate they will "never return null". This refers
 * to the fact that some values (either the group value or the default value)
 * can be set to " ", "", or "(none)". These values are considered to be undefined.
 * Internally, these values are all converted to null. The various get and check 
 * methods may then convert this to a different value (e.g. "" or false) before 
 * returning it.
 * 
 * The check* and verify* methods are specifically designed for boolean properties
 * that are being used for security.
 * 
 * @see com.cannontech.core.roleproperties.dao.impl.RolePropertyDaoImpl.convertPropertyValue(InputType<?>, String)
 */
public interface RolePropertyDao {
    /**
     * Returns the user's value of the specified role property as a String.
     * 
     * This method will never return null. Undefined values are returned as "".
     * 
     * Unlike the other methods, this can be called regardless of the type
     * of the property, it essentially calls a toString on the value and
     * returns that.
     * 
     * @param property any property
     * @param user a valid user, may be null when accessing System properties
     * @return  "" or the value of the property
     * @throws UserNotInRoleException user must be in role to access a role property
     */
    public String getPropertyStringValue(YukonRoleProperty property, LiteYukonUser user) throws UserNotInRoleException;
    
    /**
     * Returns the user's value of the specified role property as a boolean.
     * 
     * Undefined values are returned as false.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Boolean.
     * 
     * @param property any property with a Boolean return type
     * @param user a valid user, may be null when accessing System properties
     * @return  the value of the property or false if undefined
     * @throws UserNotInRoleException user must be in role to access a role property
     */
    public boolean getPropertyBooleanValue(YukonRoleProperty property, LiteYukonUser user) throws UserNotInRoleException;
    
    /**
     * Returns the user's value of the specified role property as a long.
     * 
     * Undefined values are returned as 0.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Number. Rounding or truncation may occur to return an long.
     * 
     * @param property any property with a Number return type
     * @param user a valid user, may be null when accessing System properties
     * @return  the value of the property or 0 if undefined
     * @throws UserNotInRoleException user must be in role to access a role property
     */
    public long getPropertyLongValue(YukonRoleProperty property, LiteYukonUser user) throws UserNotInRoleException;
    
    /**
     * Returns the user's value of the specified role property as an integer.
     * 
     * Undefined values are returned as 0.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Number. Rounding or truncation may occur to return an int.
     * 
     * @param property any property with a Number return type
     * @param user a valid user, may be null when accessing System properties
     * @return  the value of the property or 0 if undefined
     * @throws UserNotInRoleException user must be in role to access a role property
     */
    public int getPropertyIntegerValue(YukonRoleProperty property, LiteYukonUser user) throws UserNotInRoleException;

    /**
     * Returns the user's value of the specified role property as an enum.
     * 
     * Undefined values are returned as null.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to the specified enumClass.
     * 
     * @param property any property with a return type of enumClass
     * @param user a valid user, may be null when accessing System properties
     * @return  the value of the property or null if undefined
     * @throws UserNotInRoleException user must be in role to access a role property
     */
    public <E extends Enum<E>> E getPropertyEnumValue(YukonRoleProperty property, Class<E> enumClass, LiteYukonUser user) throws UserNotInRoleException;
    
    /**
     * This method returns true if it is valid to call checkProperty, checkFalseProperty,
     * checkAllProperties, checkAnyProperties, verifyProperty, or verifyFalseProperty 
     * with the supplied property.
     * 
     * This essentially checks if the property's type has a return type 
     * that can be cast to Boolean.
     * 
     * @param property any property with a Boolean return type
     * @param user a valid user, may be null when accessing System properties
     * @return  the value of the property or false if undefined
     */
    public boolean isCheckPropertyCompatible(YukonRoleProperty property);
    
    /**
     * This method returns the value of a boolean property. Unlike getPropertyBooleanValue,
     * this method will never throw a UserNotInRoleException, if the user is not
     * in the property's role, this method will simply return false.
     * 
     * Undefined values are returned as false.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Boolean.
     * 
     * @param property any property with a Boolean return type
     * @param user a valid user, may be null when accessing System properties
     * @return  the value of the property or false if undefined
     */
    public boolean checkProperty(YukonRoleProperty property, LiteYukonUser user);
    
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
     * @param user a valid user, may be null when accessing System properties
     * @param firstProperty any property with a Boolean return type
     * @param otherProperties any properties with a Boolean return type
     * @return  the AND'd value of each of the properties
     */
    public boolean checkAllProperties(LiteYukonUser user, YukonRoleProperty firstProperty, YukonRoleProperty... otherProperties);

    /**
     * Returns true if and only if all roles are true.
     * 
     * @see #checkAllProperties(LiteYukonUser, YukonRoleProperty, YukonRoleProperty...)
     */
    public boolean checkAllProperties(LiteYukonUser user, Iterable<YukonRoleProperty> properties);

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
     * @param user a valid user, may be null when accessing System properties
     * @param firstProperty any property with a Boolean return type
     * @param otherProperties any properties with a Boolean return type
     * @return  the OR'd value of each of the properties
     */
    public boolean checkAnyProperties(LiteYukonUser user, YukonRoleProperty firstProperty, YukonRoleProperty... otherProperties);

    /**
     * Return true if any of the properties is true.
     * 
     * @see #checkAnyProperties(LiteYukonUser, YukonRoleProperty, YukonRoleProperty...)
     */
    public boolean checkAnyProperties(LiteYukonUser user, Iterable<YukonRoleProperty> properties);

    /**
     * This method returns the negated value of a boolean property. Unlike getPropertyBooleanValue,
     * this method will never throw a UserNotInRoleException, if the user is not
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
     * @param user a valid user, may be null when accessing System properties
     * @return  the value of the property or false if undefined
     */
    public boolean checkFalseProperty(YukonRoleProperty property, LiteYukonUser user);
    
    /**
     * This method returns true if the role group has the specified role.
     * 
     * @param role any valid role, may not be null
     * @param liteYukonGroup any valid liteYukonGroup, may not be null
     * @return true if user or one of the user's groups has the role
     */
    public boolean checkRoleForRoleGroupId(YukonRole role, int roleGroupId);

    /**
     * This method returns true if the user has the specified role.
     * 
     * @param role any valid role, may not be null
     * @param user any valid user, may not be null
     * @return true if user or one of the user's groups has the role
     */
    public boolean checkRole(YukonRole role, LiteYukonUser user);

    
    /**
     * This method returns true if one of the user's roles is in the 
     * specified category.
     * 
     * @param category any valid category, may not be null
     * @param user any valid user, may not be null
     * @return
     */
    public boolean checkCategory(YukonRoleCategory category, LiteYukonUser user);
    
    /**
     * Equivalent to calling checkProperty for each property and returning if any are true, and 
     * throwing a NotAuthorizedException if none are true.
     * 
     * @param user a valid user, may be null when accessing System properties
     * @param properties any properties with a Boolean return type
     */
    public void verifyAnyProperties(LiteYukonUser user, YukonRoleProperty firstProperty,
    		YukonRoleProperty... otherProperties) throws NotAuthorizedException;

    /**
     * Equivalent to calling checkProperty and returning if true, and throwing
     * a NotAuthorizedException if false.
     * 
     * @param property any property with a Boolean return type
     * @param user a valid user, may be null when accessing System properties
     * @throws NotAuthorizedException
     */
    public void verifyProperty(YukonRoleProperty property, LiteYukonUser user) throws NotAuthorizedException;

    /**
     * Equivalent to calling checkRole and returning if true, and throwing
     * a NotAuthorizedException if false.

     * @param role any valid role, may not be null
     * @param user any valid user, may not be null
     * @throws NotAuthorizedException
     */
    public void verifyRole(YukonRole role, LiteYukonUser user) throws NotAuthorizedException;

    /**
     * Returns the set of categories that are assigned to the user. This includes
     * both user and group roles.
     * 
     * @param user any valid user, may not be null
     * @return an unmodifiable Set, never null
     */
    public Set<YukonRoleCategory> getRoleCategoriesForUser(LiteYukonUser user);

    /**
     * Returns the set of role properties that do not exist in the database
     * @return
     */
    public ImmutableSet<YukonRoleProperty> getMissingProperties();

    /**
     * Returns the liteYukonGroup's value of the specified role property as a String.
     * 
     * This method will never return null. Undefined values are returned as "".
     * 
     * Unlike the other methods, this can be called regardless of the type
     * of the property, it essentially calls a toString on the value and
     * returns that.
     * 
     * @param property any property
     * @param liteYukonGroup 
     * @return  "" or the value of the property
     * @throws UserNotInRoleException user must be in role to access a role property
     */
    public String getPropertyStringValue(LiteYukonGroup liteYukonGroup, YukonRoleProperty property);
    
    /**
     * Returns the liteYukonGroup's value of the specified role property as an integer.
     * 
     * Undefined values are returned as 0.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Number. Rounding or truncation may occur to return an int.
     * 
     * @param property any property with a Number return type
     * @param liteYukonGroup a valid liteYukonGroup, may be null when accessing System properties
     * @return  the value of the property or 0 if undefined
     * @throws UserNotInRoleException user must be in role to access a role property
     */
	public int getPropertyIntegerValue(LiteYukonGroup liteYukonGroup, YukonRoleProperty property);

    /**
     * Returns the liteYukonGroup's value of the specified role property as a boolean.
     * 
     * Undefined values are returned as false.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Boolean.
     * 
     * @param property any property with a Boolean return type
     * @param liteYukonGroup a valid liteYukonGroup, may be null when accessing System properties
     * @return  the value of the property or false if undefined
     * @throws UserNotInRoleException user must be in role to access a role property
     */
	public boolean getPropertyBooleanValue(LiteYukonGroup liteYukonGroup, YukonRoleProperty property);

    Number getPropertyNumberValue(LiteYukonGroup roleGroup, YukonRoleProperty property)
            throws UserNotInRoleException;

    /**
     * Returns true if the user access level is less or equal the minimum level.
     * Examples:
     * If the user level is UPDATE , the user is allowed access to UPDATE, LIMITED and RESTRICTED.
     * 
     * If the user level is UPDATE and minLevel is LIMITED, UPDATE(3) <=  LIMITED(4) = true, the user is allowed access.
     * If the user level is LIMITED  and minLevel is UPDATE, LIMITED(4) <= UPDATE(3) = false, the user is not allowed access.
     * @param property - property to check
     * @param minLevel - minimum level that grants access
     * @param user
     * @return
     */
    
    boolean checkLevel(YukonRoleProperty property, HierarchyPermissionLevel minLevel, LiteYukonUser user);
    boolean checkLevel(YukonRoleProperty property, AccessLevel minLevel, LiteYukonUser user);

    /**
     * Equivalent to calling checkProperty and returning if true, and throwing
     * a NotAuthorizedException if false.
     * 
     * 
     * @param property - property to check
     * @param minLevel - minimum level that grants access
     * @param user a valid user
     * @throws NotAuthorizedException
     */

    void verifyLevel(YukonRoleProperty property, HierarchyPermissionLevel minLevel, LiteYukonUser user)
            throws NotAuthorizedException;

    /**
     * Returns true if the user cap control commands access level is equal to the given level.
     * @param property - property to check
     * @param level - level that grants access
     * @param user
     * @return boolean true if access is allowed
     */
    boolean checkLevel(YukonRoleProperty property, CapControlCommandsAccessLevel level,
                       LiteYukonUser user);
    
    /**
     * Returns true if the user cap control commands access level is equal to any of the given levels.
     * @param property - property to check
     * @param levels - levels that grants access
     * @param user
     * @return boolean true if access is allowed
     */
    boolean checkAnyLevel(YukonRoleProperty property, CapControlCommandsAccessLevel[] levels,
                       LiteYukonUser user);
}