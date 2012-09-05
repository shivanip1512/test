package com.cannontech.database.model;

import java.util.Collections;
import java.util.List;

import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.spring.YukonSpringHook;

public class UserGroupTreeModel extends DBTreeModel {
    private UserGroupDao userGroupDao = YukonSpringHook.getBean("userGroupDao", UserGroupDao.class);

    public UserGroupTreeModel()  {
    	super( new DBTreeNode("User Groups") );
    }
    
    @Override
    public boolean isLiteTypeSupported( int liteType ) {
    	return ( liteType == com.cannontech.database.data.lite.LiteTypes.USER_GROUP );
    }

    /**
     * This method builds up the list of the user groups that show up in the left hand menu in database editor
     */
    @Override
    public void update() {
        // alphabetize the list of user groups
        List<LiteUserGroup> allUserGroups = userGroupDao.getAllLiteUserGroups();
        Collections.sort(allUserGroups, LiteUserGroup.userGroupNameComparator());

        // Clear the children already in the panel
        DBTreeNode rootNode = (DBTreeNode) getRoot();
        rootNode.removeAllChildren();

        // Add all of the user groups to the left panel
        for(LiteUserGroup userGroup : allUserGroups) {
            DBTreeNode userGroupNode = new DBTreeNode(userGroup);

            userGroupNode.setIsSystemReserved(userGroup.getUserGroupId() < 0);
            rootNode.add( userGroupNode );
        }

        reload();
    }
}