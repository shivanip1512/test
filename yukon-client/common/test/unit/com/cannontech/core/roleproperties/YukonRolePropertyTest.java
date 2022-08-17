package com.cannontech.core.roleproperties;


import static org.junit.Assert.assertEquals;

import java.util.HashSet;

import org.junit.Test;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.authentication.service.impl.MockRolePropertyDaoImpl;
import com.google.common.collect.Sets;

public class YukonRolePropertyTest {
    
    private static final MockRolePropertyDaoImpl rolePropertyDaoMock = new MockRolePropertyDaoImpl();

    {
        rolePropertyDaoMock.setupRolesFor(MockRolePropertyDaoImpl.LEVEL_OWNER_USER).withRoleProperty(
            YukonRoleProperty.ENDPOINT_PERMISSION, HierarchyPermissionLevel.OWNER);

        rolePropertyDaoMock.setupRolesFor(MockRolePropertyDaoImpl.LEVEL_UPDATE_USER).withRoleProperty(
            YukonRoleProperty.ENDPOINT_PERMISSION, HierarchyPermissionLevel.UPDATE);

        rolePropertyDaoMock.setupRolesFor(MockRolePropertyDaoImpl.LEVEL_RESTRICTED_USER).withRoleProperty(
            YukonRoleProperty.ENDPOINT_PERMISSION, HierarchyPermissionLevel.VIEW);
    }

    /**
     * Verifies that user with an "OWNER" level has access to "UPDATE" functionality.
     */
    @Test
    public void checkOwnerAccessToUpdate() {
        boolean hasAccess = rolePropertyDaoMock.checkLevel(YukonRoleProperty.ENDPOINT_PERMISSION,
            HierarchyPermissionLevel.UPDATE, MockRolePropertyDaoImpl.LEVEL_OWNER_USER);
        assertEquals(true, hasAccess);
    }

    /**
     * Verifies that user with an "UPDATE" level doesn't have access to "OWNER" functionality.
     */
    @Test
    public void checkUpdateAccessToOwner() {
        boolean hasAccess = rolePropertyDaoMock.checkLevel(YukonRoleProperty.ENDPOINT_PERMISSION,
            HierarchyPermissionLevel.OWNER, MockRolePropertyDaoImpl.LEVEL_UPDATE_USER);
        assertEquals(false, hasAccess);
    }

    /**
     * Verifies that user with an "VIEW" level doesn't have access to "UPDATED" functionality.
     */
    @Test
    public void checkRestrictedAccessToUpdate() {
        boolean hasAccess = rolePropertyDaoMock.checkLevel(YukonRoleProperty.ENDPOINT_PERMISSION,
            HierarchyPermissionLevel.UPDATE, MockRolePropertyDaoImpl.LEVEL_RESTRICTED_USER);
        assertEquals(false, hasAccess);
    }

    /**
     * Verifies that user that with no Hierarchy Permission role property has no access.
     */
    @Test(expected = NotAuthorizedException.class)
    public void checkNotInRole() {
        rolePropertyDaoMock.verifyLevel(YukonRoleProperty.ENDPOINT_PERMISSION, HierarchyPermissionLevel.VIEW,
            MockRolePropertyDaoImpl.LEVEL_UNKNOWN_USER);
    }

    /**
     * Verifies that user with an "VIEW" level doesn't have access to "UPDATE" functionality.
     */
    @Test(expected = NotAuthorizedException.class)
    public void verifyRestrictedAccessToUpdate() {
        rolePropertyDaoMock.verifyLevel(YukonRoleProperty.ENDPOINT_PERMISSION, HierarchyPermissionLevel.UPDATE,
            MockRolePropertyDaoImpl.LEVEL_RESTRICTED_USER);
    }

    /**
     * Verifies that user with an "OWNER" level has access to "UPDATE" functionality.
     */
    public void verifyOwnerAccessToUpdate() {
        rolePropertyDaoMock.verifyLevel(YukonRoleProperty.ENDPOINT_PERMISSION, HierarchyPermissionLevel.UPDATE,
            MockRolePropertyDaoImpl.LEVEL_OWNER_USER);
    }
   
    @Test
    public void checkNameForCollisions() {
        HashSet<String> enumText = Sets.newHashSet();
        
        addAll(enumText, YukonRoleProperty.class);
        addAll(enumText, YukonRole.class);
        addAll(enumText, YukonRoleCategory.class);
        addAll(enumText, MasterConfigBoolean.class);
    }

    private <T extends Enum<T>> void addAll(HashSet<String> enumText,
            Class<T> class1) {
        T[] enumConstants = class1.getEnumConstants();
        for (T t : enumConstants) {
            boolean add = enumText.add(t.name());
            if (!add) throw new RuntimeException(t + " is a duplicate");
        }
    }
    
    @Test
    public void checkPropertyIdForCollisions() {
        // the current implementation of YukonRoleProperty prevents this, but just in case
        HashSet<Integer> propertyIds = Sets.newHashSet();
        
        YukonRoleProperty[] values = YukonRoleProperty.values();
        for (YukonRoleProperty yukonRoleProperty : values) {
            boolean add = propertyIds.add(yukonRoleProperty.getPropertyId());
            if (!add) throw new RuntimeException(yukonRoleProperty + " has a duplicate id");
        }
        
    }
    
    @Test
    public void checkRoleIdForCollisions() {
        // the current implementation of YukonRole prevents this, but just in case
        HashSet<Integer> roleIds = Sets.newHashSet();
        
        YukonRole[] values = YukonRole.values();
        for (YukonRole yukonRole : values) {
            boolean add = roleIds.add(yukonRole.getRoleId());
            if (!add) throw new RuntimeException(yukonRole + " has a duplicate id");
        }
        
    }
    
}
