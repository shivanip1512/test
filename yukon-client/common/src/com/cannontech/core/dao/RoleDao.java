package com.cannontech.core.dao;

import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;

public interface RoleDao {

    /**
     * Returns the LiteYukonRoleProperty[] that are owned by the given roleID_.
     * Returns a zero length array if the role_ is null. 
     * @param LiteYukonRole
     * @return LiteYukonRoleProperty[]
     */
    public LiteYukonRoleProperty[] getRoleProperties(int roleID_);

    /**
     * Returns the LiteYukonRoleProperty specified by the roleProeprtyID_.
     * This global value is a property that belongs to the Yukon Group.
     *  
     * @param LiteYukonRole
     * @return String
     * @deprecated use RolePropertyDao.getPropertyStringValue()
     */
    @Deprecated
    public String getGlobalPropertyValue(int rolePropertyID_);

    /**
     * Return a particular role property given a role property id.
     * @param propid
     * @return
     */
    public LiteYukonRoleProperty getRoleProperty(int propid);

    /**
     * retrieves a role based on the role property id
     * throws NotFoundException 
     * @param rolePropID
     * @return
     */
    public LiteYukonRole getLiteRole (Integer rolePropID);


    /**
     * Returns the value for a given group and role property.
     * If no value is found then defaultValue is returned for convenience.
     * @param group
     * @param roleProperty
     * @return String
     * @deprecated no equivalent, defaultValue isn't supported
     */
    @Deprecated
    public String getRolePropValueGroup(LiteYukonGroup group_,
            int rolePropertyID, String defaultValue);

    /**
     * Returns the value for a given group and role property.
     * If no value is found then defaultValue is returned for convenience.
     * @param groupId
     * @param roleProperty
     * @return String
     * @deprecated no equivalent, defaultValue isn't supported
     */
    @Deprecated
    public String getRolePropValueGroup(int groupId,
                                        int rolePropertyId, String defaultValue);

    /**
     * Return a particular lite yukon group given the group name
     * @param groupName
     * @return LiteYukonGroup
     */
    public LiteYukonGroup getGroup(String groupName);

    /**
     * Return a particular lite yukon group given the group ID
     * @param groupName
     * @return LiteYukonGroup
     */
    public LiteYukonGroup getGroup(int grpID_);
    

    /**
     * 
     * @param group
     * @param roleID
     * @param rolePropertyID
     * @param newVal
     * @return
     */
    public boolean updateGroupRoleProperty(LiteYukonGroup group, 
                                           int roleID, 
                                           int rolePropertyID, 
                                           String newVal);
    
    /**
     * @deprecated use RolePropertyDao.getPropertyEnumValue()
     */
    @Deprecated
    public <E extends Enum<E>> E getGlobalRolePropertyValue(Class<E> class1, int rolePropertyID);

    /**
     * Returns true if the value of the role property is true, false otherwise.
     * @param rolePropertyID
     * @return
     * @deprecated use RolePropertyDao.checkProperty()
     */
    @Deprecated
    public boolean checkGlobalRoleProperty(int rolePropertyID);
    
}