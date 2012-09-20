package com.cannontech.database.data.user;

import java.sql.SQLException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.naming.ConfigurationException;

import org.apache.log4j.Logger;

import com.cannontech.common.editor.EditorPanel;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.roles.YukonGroupRoleDefs;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class UserGroup extends DBPersistent implements CTIDbChange, EditorPanel {	
    private static final Logger log = Logger.getLogger(UserGroup.class);
    
    private com.cannontech.database.db.user.UserGroup userGroup = new com.cannontech.database.db.user.UserGroup();
    private Multimap<YukonRole, LiteYukonGroup> rolesToGroupMap = HashMultimap.create();

    public com.cannontech.database.db.user.UserGroup getUserGroup() {
        return userGroup;
    }
    public void setUserGroup(com.cannontech.database.db.user.UserGroup userGroup) {
        this.userGroup = userGroup;
    }
    
    public void setUserGroupId(Integer userGroupId) {
        userGroup.setUserGroupId(userGroupId);
    }
    
    public Set<LiteYukonGroup> getRoleGroups() {
        Set<LiteYukonGroup> roleGroups = Sets.newHashSet(rolesToGroupMap.values());
        return roleGroups;
    }
    public void addRoleGroups(LiteYukonGroup roleGroup) throws ConfigurationException {
        RoleDao roleDao = YukonSpringHook.getBean("roleDao", RoleDao.class);
        Set<YukonRole> rolesForRoleGroup = roleDao.getRolesForGroup(roleGroup.getGroupID());

        if (rolesForRoleGroup.isEmpty()) {
            putRolesToGroupMap(null, roleGroup);
        }
        
        for (YukonRole yukonRole : rolesForRoleGroup) {
            putRolesToGroupMap(yukonRole, roleGroup);
        }
    }
    
    // role groups
    public Multimap<YukonRole, LiteYukonGroup> getRolesToGroupMap() {
        return HashMultimap.create(rolesToGroupMap);
    }
    public boolean hasYukonRole(YukonRole yukonRole) {
        return rolesToGroupMap.containsKey(yukonRole);
    }
    public void clearRolesToGroupMap() {
        rolesToGroupMap.clear();
    }
    public void putRolesToGroupMap(YukonRole yukonRole, LiteYukonGroup roleGroup) throws ConfigurationException {
        if (!isAddable(yukonRole)) {
            throw new ConfigurationException("The group settings supplied in "+roleGroup.getGroupName()+" contains a role that already exists for the user group "+ this.userGroup.getUserGroupName() +".");
        }

        this.rolesToGroupMap.put(yukonRole, roleGroup);
    }
    public void putAllRolesToGroupMap(Multimap<YukonRole, LiteYukonGroup> roleToGroupMap) throws ConfigurationException {
        for (Entry<YukonRole, LiteYukonGroup> roleToGroupEntry : roleToGroupMap.entries()) {
            if (!isAddable(roleToGroupEntry.getKey())) {
                throw new ConfigurationException("The role group settings supplied in "+roleToGroupEntry.getValue().getGroupName()+" contains a role "+roleToGroupEntry.getKey() +" that already exists in the user group" +userGroup.getUserGroupName()+
                                                                       ".  ["+rolesToGroupMap+"]");
            }
        }
        
        this.rolesToGroupMap.putAll(roleToGroupMap);
    }

    /**
     * This method checks to see if we are allowed to add the yukon group to this user group.  If the supplied yukon group contains a
     * role that already exists in the yukon groups list this method will return false.
     */
    public boolean isAddable(YukonRole yukonRole) {
        Set<YukonRole> existingRoles = this.rolesToGroupMap.keySet();
        if (yukonRole != null && existingRoles.contains(yukonRole)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return userGroup.getUserGroupName();
    }
    
    public boolean isUserGroupDeletable() {
        return userGroup.getUserGroupId() > 0;
    }
    
    // DBPersistent methods
    @Override
    public void add() throws SQLException {
        userGroup.add();
        
        RoleDao roleDao = YukonSpringHook.getBean("roleDao", RoleDao.class);
        Multimap<YukonRole, LiteYukonGroup> rolesToGroupsMap = roleDao.getRolesAndRoleGroupsForUserGroup(userGroup.getUserGroupId());
        try {
            putAllRolesToGroupMap(rolesToGroupsMap);
        } catch (ConfigurationException e) {
            throw new RoleConflictInUserGroupException("The user group "+userGroup.getUserGroupName()+" has a role group conflict.", e);
        }

        // Add the connections between the yukon group and the user group
        UserGroupDao userGroupDao = YukonSpringHook.getBean("userGroupDao", UserGroupDao.class);
        for (LiteYukonGroup liteYukonGroup : getRoleGroups()) {
            userGroupDao.createUserGroupToYukonGroupMappng(userGroup.getUserGroupId(), liteYukonGroup.getGroupID());
        }
        userGroupDao.createUserGroupToYukonGroupMappng(userGroup.getUserGroupId(), YukonGroupRoleDefs.GRP_YUKON);
    }

    @Override
    public void delete() throws SQLException {
        userGroup.delete();
    }

    @Override
    public void retrieve() throws SQLException {
        userGroup.retrieve();

        RoleDao roleDao = YukonSpringHook.getBean("roleDao", RoleDao.class);
        Multimap<YukonRole, LiteYukonGroup> rolesToGroupsMap = roleDao.getRolesAndRoleGroupsForUserGroup(userGroup.getUserGroupId());
        try {
            putAllRolesToGroupMap(rolesToGroupsMap);
        } catch (ConfigurationException e) {
            log.error("The user group "+userGroup.getUserGroupName()+" has conflicting role groups", e);
        }
    }

    @Override
    public void update() throws SQLException {
        UserGroupDao userGroupDao = YukonSpringHook.getBean("userGroupDao", UserGroupDao.class);
        userGroup.update();

        // Update the connections between the yukon group and the user group
        YukonGroupDao yukonGroupDao = YukonSpringHook.getBean("yukonGroupDao", YukonGroupDao.class);
        List<LiteYukonGroup> existingYukonGroups = yukonGroupDao.getRoleGroupsForUserGroupId(userGroup.getUserGroupId());
        Set<LiteYukonGroup> yukonGroupsToAdd = getRoleGroups();
        yukonGroupsToAdd.removeAll(existingYukonGroups);
        for (LiteYukonGroup newYukonGroup : yukonGroupsToAdd) {
            userGroupDao.createUserGroupToYukonGroupMappng(userGroup.getUserGroupId(), newYukonGroup.getGroupID());
        }

        Set<LiteYukonGroup> yukonGroupsToRemove = Sets.newHashSet(existingYukonGroups);
        yukonGroupsToRemove.removeAll(getRoleGroups());
        for (LiteYukonGroup existingYukonGroup : yukonGroupsToRemove) {
            userGroupDao.deleteUserGroupToYukonGroupMappng(userGroup.getUserGroupId(), existingYukonGroup.getGroupID());
        }
    }
    
    @Override
    public DBChangeMsg[] getDBChangeMsgs(DbChangeType dbChangeType) {
        DBChangeMsg[] msgs = 
            {new DBChangeMsg(userGroup.getUserGroupId(), DBChangeMsg.CHANGE_USER_GROUP_DB,
                             DBChangeMsg.CAT_USER_GROUP, DBChangeMsg.CAT_USER_GROUP,  dbChangeType)};

        return msgs;
    }
}