package com.cannontech.database.data.user;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.ConfigurationException;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.editor.EditorPanel;
import com.cannontech.core.authorization.dao.PaoPermissionDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.user.YukonGroupRole;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class YukonGroup extends DBPersistent implements CTIDbChange, EditorPanel {
    private Logger log = YukonLogManager.getLogger(YukonGroup.class);
    
	private com.cannontech.database.db.user.YukonGroup yukonGroup;
	private List<YukonGroupRole> yukonGroupRoles = Lists.newArrayList();
	
	public YukonGroup() {
		super();
	}
	
	public YukonGroup(Integer groupId, String groupName) {
	    yukonGroup = new com.cannontech.database.db.user.YukonGroup();
        yukonGroup.setGroupID(groupId);
        yukonGroup.setGroupName(groupName);
    }

    public void setDbConnection(java.sql.Connection conn) {
		super.setDbConnection( conn );
		getYukonGroup().setDbConnection( conn );
		
		for (YukonGroupRole yukonGroupRole : getYukonGroupRoles()) {
			yukonGroupRole.setDbConnection(conn);
		}
	}


	public Integer getID() {
		return getGroupID();
	}
	public ImmutableList<YukonGroupRole> getYukonRoles() {
	    return getYukonGroupRoles();
	}
	
	public ImmutableList<YukonGroupRole> getYukonGroupRoles() {
        return ImmutableList.copyOf(yukonGroupRoles);
    }

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException 
	{
		if( getYukonGroup().getGroupID() == null ) {
			setGroupID(com.cannontech.database.db.user.YukonGroup.getNextGroupID());
		}

		getYukonGroup().add();

		for (com.cannontech.database.db.user.YukonGroupRole groupRole : yukonGroupRoles) {
			groupRole.setGroupID( getYukonGroup().getGroupID() );
			groupRole.add();
		}
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		delete( YukonGroupRole.TABLE_NAME, "GroupID", getYukonGroup().getGroupID() );
        PaoPermissionDao<LiteYukonGroup> paoPermissionDao = (PaoPermissionDao<LiteYukonGroup>) YukonSpringHook.getBean("groupPaoPermissionDao");
        paoPermissionDao.removeAllPermissions(getGroupID());
        
		getYukonGroup().delete();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
	    getYukonGroup().retrieve();

		//add the roles this user has
		YukonGroupRole[] roles = YukonGroupRole.getYukonGroupRoles( getGroupID().intValue(), getDbConnection() );
 
        for( int i = 0; i < roles.length; i++ ) {
            try {
                addYukonGroupRole(roles[i]);
            } catch (ConfigurationException e) {
                log.error("Could not retrieve the yukon group supplied do to a conflict with it's roles", e);
            }
        }
    }

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {

	    setGroupID( getYukonGroup().getGroupID() );
		getYukonGroup().update();
		
		//first delete the current userRoles
		delete( YukonGroupRole.TABLE_NAME, "GroupID", getYukonGroup().getGroupID() );		
		
		// Insert the old entries (entries with ID set) first to avoid primary key conflict
		List<YukonGroupRole> grpRolesNoID = Lists.newArrayList();
		for (YukonGroupRole grpRole : getYukonGroupRoles()) {
			if (grpRole.getGroupRoleID() != null) {
				grpRole.add();
			} else {
				grpRolesNoID.add(grpRole);
			}
		}
		for (YukonGroupRole yukonGroupRole : grpRolesNoID) {
			yukonGroupRole.add();
		}
	}


	/**
	 * Returns the yukonUser.
	 * @return com.cannontech.database.db.user.YukonUser
	 */
	public com.cannontech.database.db.user.YukonGroup getYukonGroup() 
	{
		if(yukonGroup == null) {
			yukonGroup = new com.cannontech.database.db.user.YukonGroup();
		}

		return yukonGroup;
	}

	/**
	 * Sets the yukonUser.
	 * @param yukonUser The yukonUser to set
	 */
	public void setYukonGroup(
		com.cannontech.database.db.user.YukonGroup yukonGroup) {
		this.yukonGroup = yukonGroup;
	}

	/**
	 * Returns the userID.
	 * @return Integer
	 */
	public Integer getGroupID() 
	{
		return getYukonGroup().getGroupID();
	}

	/**
	 * Returns the userID.
	 */
	public void setGroupID( Integer groupId) {
		getYukonGroup().setGroupID(groupId);

		for (YukonGroupRole yukonGroupRole : getYukonGroupRoles()) {
		    yukonGroupRole.setGroupID(groupId);
		}
	}

	
	public String toString()
	{
		return getYukonGroup().getGroupName();
	}


	/**
	 * @see com.cannontech.database.db.CTIDbChange#getDBChangeMsgs(int)
	 */
	public DBChangeMsg[] getDBChangeMsgs(DbChangeType dbChangeType) {
		
	    DBChangeMsg[] msgs = {
	            new DBChangeMsg(getYukonGroup().getGroupID().intValue(), DBChangeMsg.CHANGE_YUKON_USER_DB,
	                            DBChangeMsg.CAT_YUKON_USER_GROUP, DBChangeMsg.CAT_YUKON_USER, dbChangeType)
		};

		return msgs;
	}

	/**
	 * @throws ConfigurationException - Thrown if a role already exists on one of the user groups that uses this role group.
	 */
    public void addYukonGroupRole(YukonGroupRole yukonGroupRole) throws ConfigurationException {
        YukonRole newRole = YukonRole.getForId(yukonGroupRole.getRoleID());
        if (yukonGroupRole.getGroupID() == null || isYukonGroupRoleAddable(yukonGroupRole.getGroupID(), newRole)) {
            yukonGroupRoles.add(yukonGroupRole);
            return;
        }

        // The role that is being added already exists on one of the group's user groups.  Get some more detailed information and throw a configuration exception.
        YukonGroupDao yukonGroupDao = YukonSpringHook.getBean("yukonGroupDao", YukonGroupDao.class);
        LiteYukonGroup yukonGroup = yukonGroupDao.getLiteYukonGroup(yukonGroupRole.getGroupID());
        throw new ConfigurationException("The "+newRole+" role already exists on one of the user groups that use the group "+yukonGroup.getGroupName());
    }
    
    /**
     * This method checks to see if the supplied role is addable to the yukon group 
     */
    public boolean isYukonGroupRoleAddable(int roleGroupId, YukonRole newRole) {
        UserGroupDao userGroupDao = YukonSpringHook.getBean("userGroupDao", UserGroupDao.class); 
        List<UserGroup> userGroups = userGroupDao.getUserGroupsByRoleGroupId(roleGroupId);
        
        // Check to see if the role being added already exists on one of the user groups attached to this role group.
        for (UserGroup userGroup : userGroups) {
            Map<YukonRole, LiteYukonGroup> existingRoles = userGroup.getRolesToGroupMap();
            for (Entry<YukonRole, LiteYukonGroup> entry : existingRoles.entrySet()) {
                if (entry.getKey() == newRole && entry.getValue().getGroupID() != roleGroupId) {
                    return false;
                }
            }
        }
        
        return true;
    }

    /**
     * This method clears out the yukonGroupRoles list
     */
    public void removeAllYukonRoles() {
        yukonGroupRoles.clear();
    }
}