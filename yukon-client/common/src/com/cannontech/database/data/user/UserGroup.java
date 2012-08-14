package com.cannontech.database.data.user;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.naming.ConfigurationException;

import org.jfree.util.Log;

import com.cannontech.common.editor.EditorPanel;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class UserGroup extends DBPersistent implements CTIDbChange, EditorPanel {	

    private LiteUserGroup liteUserGroup = new LiteUserGroup();
    private Map<YukonRole, LiteYukonGroup> rolesToGroupMap = Maps.newHashMap();

    public LiteUserGroup getLiteUserGroup() {
        return liteUserGroup;
    }
    public void setLiteUserGroup(LiteUserGroup liteUserGroup) {
        this.liteUserGroup = liteUserGroup;
    }
    public void setUserGroupId(int userGroupId) {
        liteUserGroup.setUserGroupId(userGroupId);
    }
    
    public Set<LiteYukonGroup> getRoleGroups() {
        Set<LiteYukonGroup> roleGroups = Sets.newHashSet(rolesToGroupMap.values());
        return roleGroups;
    }
    public void addRoleGroups(LiteYukonGroup liteYukonGroup) throws ConfigurationException {
        RoleDao roleDao = YukonSpringHook.getBean("roleDao", RoleDao.class);
        Set<YukonRole> rolesForGroup = roleDao.getRolesForGroup(liteYukonGroup.getGroupID());

        for (YukonRole yukonRole : rolesForGroup) {
            putRolesToGroupMap(yukonRole, liteYukonGroup);
        }
    }
    
    // loginGroups
    public Map<YukonRole, LiteYukonGroup> getRolesToGroupMap() {
        return ImmutableMap.copyOf(rolesToGroupMap);
    }
    public boolean hasYukonRole(YukonRole yukonRole) {
        return rolesToGroupMap.containsKey(yukonRole);
    }
    public void clearRolesToGroupMap() {
        rolesToGroupMap.clear();
    }
    public void putRolesToGroupMap(YukonRole yukonRole, LiteYukonGroup yukonGroup) throws ConfigurationException {
        if (!isAddable(yukonRole)) {
            throw new ConfigurationException("The group settings supplied in "+yukonGroup.getGroupName()+" contains a role that already exists for the user group "+ this.liteUserGroup.getUserGroupName() +".");
        }

        this.rolesToGroupMap.put(yukonRole, yukonGroup);
    }
    public void putAllRolesToGroupMap(Map<YukonRole, LiteYukonGroup> roleToGroupMap) throws ConfigurationException {
        for (Entry<YukonRole, LiteYukonGroup> roleToGroupEntry : roleToGroupMap.entrySet()) {
            if (!isAddable(roleToGroupEntry.getKey())) {
                throw new ConfigurationException("The role group settings supplied in "+roleToGroupEntry.getValue().getGroupName()+" contains a role "+roleToGroupEntry.getKey() +" that already exists in the user group" +liteUserGroup.getUserGroupName()+
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
        if (existingRoles.contains(yukonRole)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return liteUserGroup.getUserGroupName();
    }
    
    public boolean isUserGroupDeletable() {
        return liteUserGroup.getUserGroupId() > 0;
    }
    
    // DBPersistent methods
    @Override
    public void add() throws SQLException {
        UserGroupDao userGroupDao = YukonSpringHook.getBean("userGroupDao", UserGroupDao.class);
        userGroupDao.create(liteUserGroup);
        
        RoleDao roleDao = YukonSpringHook.getBean("roleDao", RoleDao.class);
        Map<YukonRole, LiteYukonGroup> rolesToGroupsMap = roleDao.getRolesAndRoleGroupsForUserGroup(liteUserGroup.getUserGroupId());
        try {
            putAllRolesToGroupMap(rolesToGroupsMap);
        } catch (ConfigurationException e) {
            Log.error("The user group "+getLiteUserGroup().getUserGroupName()+" has a role group conflict.", e);
        }

        // Add the connections between the yukon group and the user group
        for (LiteYukonGroup liteYukonGroup : getRoleGroups()) {
            userGroupDao.createUserGroupToYukonGroupMappng(liteUserGroup.getUserGroupId(), liteYukonGroup.getGroupID());
        }
    }

    @Override
    public void delete() throws SQLException {
        UserGroupDao userGroupDao = YukonSpringHook.getBean("userGroupDao", UserGroupDao.class);
        userGroupDao.delete(liteUserGroup);
    }

    @Override
    public void retrieve() throws SQLException {
        UserGroupDao userGroupDao = YukonSpringHook.getBean("userGroupDao", UserGroupDao.class);
        UserGroup userGroup = userGroupDao.getUserGroup(this.liteUserGroup.getUserGroupId());
        this.liteUserGroup = userGroup.liteUserGroup;
        this.rolesToGroupMap = userGroup.rolesToGroupMap;
    }

    @Override
    public void update() throws SQLException {
        UserGroupDao userGroupDao = YukonSpringHook.getBean("userGroupDao", UserGroupDao.class);
        userGroupDao.update(liteUserGroup);

        // Update the connections between the yukon group and the user group
        YukonGroupDao yukonGroupDao = YukonSpringHook.getBean("yukonGroupDao", YukonGroupDao.class);
        List<LiteYukonGroup> existingYukonGroups = yukonGroupDao.getRoleGroupsForUserGroupId(liteUserGroup.getUserGroupId());
        Set<LiteYukonGroup> yukonGroupsToAdd = getRoleGroups();
        yukonGroupsToAdd.removeAll(existingYukonGroups);
        for (LiteYukonGroup newYukonGroup : yukonGroupsToAdd) {
            userGroupDao.createUserGroupToYukonGroupMappng(liteUserGroup.getUserGroupId(), newYukonGroup.getGroupID());
        }

        Set<LiteYukonGroup> yukonGroupsToRemove = Sets.newHashSet(existingYukonGroups);
        yukonGroupsToRemove.removeAll(getRoleGroups());
        for (LiteYukonGroup existingYukonGroup : yukonGroupsToRemove) {
            userGroupDao.deleteUserGroupToYukonGroupMappng(liteUserGroup.getUserGroupId(), existingYukonGroup.getGroupID());
        }
    }
    
    @Override
    public DBChangeMsg[] getDBChangeMsgs(DbChangeType dbChangeType) {
        DBChangeMsg[] msgs = 
            {new DBChangeMsg(getLiteUserGroup().getUserGroupId(), DBChangeMsg.CHANGE_USER_GROUP_DB,
                             DBChangeMsg.CAT_USER_GROUP, DBChangeMsg.CAT_USER_GROUP,  dbChangeType)};

        return msgs;
    }
}