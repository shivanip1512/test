package com.cannontech.core.dao;

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
     */
    public String getGlobalPropertyValue(int rolePropertyID_);

    /**
     * Returns the value for a given user and role property.
     * If no value is found then defaultValue is returned for convenience.
     * @param user
     * @param roleProperty
     * @return String
     */
    public String getRolePropertyValue(int userID, int rolePropertyID,
            String defaultValue);

    /**
     * Returns the value for a given user and role property.
     * If no value is found then defaultValue is returned for convenience.
     * @param user
     * @param roleProperty
     * @return String
     */
    public String getRolePropValueGroup(int groupID, int rolePropertyID,
            String defaultValue);

    /**
     * We can only use the properties if the global Yukon Group is loaded
     * @return
     */
    public boolean hasLoadedGlobals();
    
    /**
     * retrieves a role based on the role property id
     * throws NotFoundException 
     * @param rolePropID
     * @return
     */
    public LiteYukonRole getLiteRole (Integer rolePropID);

}