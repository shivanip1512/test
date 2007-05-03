package com.cannontech.core.dao;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;


import org.junit.Before;
import org.junit.Test;

import com.cannontech.core.dao.impl.RoleDaoImpl;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;

public class RoleDaoImplTest {

    private RoleDaoImpl roleDao;

    @Before
    public void setUp() {
        roleDao = new RoleDaoImpl();
        roleDao.setDatabaseCache(new IDatabaseCacheAdapter() {
            @Override
            public List<LiteYukonRoleProperty> getAllYukonRoleProperties() {
                LiteYukonRoleProperty roleProp1 = new LiteYukonRoleProperty();
                roleProp1.setRoleID(1);
                roleProp1.setRolePropertyID(1);
                LiteYukonRoleProperty roleProp2 = new LiteYukonRoleProperty();
                roleProp2.setRoleID(1);
                roleProp2.setRolePropertyID(2);
                LiteYukonRoleProperty roleProp3 = new LiteYukonRoleProperty();
                roleProp3.setRoleID(1);
                roleProp3.setRolePropertyID(3);
                List<LiteYukonRoleProperty> rolePropsList = new ArrayList<LiteYukonRoleProperty>();
                rolePropsList.add(roleProp1);
                rolePropsList.add(roleProp2);
                rolePropsList.add(roleProp3);
                return rolePropsList;
            }

            @Override
            public List<LiteYukonRole> getAllYukonRoles() {
                LiteYukonRole role = new LiteYukonRole();
                role.setRoleID(1);
                List<LiteYukonRole> roles = new ArrayList<LiteYukonRole>();
                roles.add(role);
                return roles;
            }
            
        });
        
    }

    @Test
    public void testGetRolePropFromRoleID() {
        //our role id is 1:
        //LiteYukonRole role = new LiteYukonRole();
        //role.setRoleID(1);
        LiteYukonRole roleID = roleDao.getRoleID(3);
        assertTrue(roleID.getRoleID() == 1);
    }

}
