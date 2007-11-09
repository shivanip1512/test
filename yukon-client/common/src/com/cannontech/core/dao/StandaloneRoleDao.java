package com.cannontech.core.dao;

/**
 * This class is stupid. Yes. But, it has no dependencies on the cache. This makes it
 * perfect to use during startup when you need those role properties really bad but 
 * don't want to create a huge circular reference 
 * (cache -> dispatch connection -> role dao -> cache).
 * I didn't want to swap it in completely for the RoleDao because that _might_ have
 * some speed benefits over this class (although it goes through so many loops,
 * I don't really see how).
 */
public interface StandaloneRoleDao {
    /**
     * Returns the LiteYukonRoleProperty specified by the roleProeprtyID_.
     * This global value is a property that belongs to the Yukon Group.
     *
     * @param LiteYukonRole
     * @return String
     */
    public String getGlobalPropertyValue(int rolePropertyID_);


}
