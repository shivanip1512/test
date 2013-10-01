package com.cannontech.core.dao;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.cannontech.core.dao.impl.RoleDaoImpl;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
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
                roleProp1.setRoleID(YukonRole.OPERATOR_ADMINISTRATOR.getRoleId());
                roleProp1.setRolePropertyID(YukonRoleProperty.ADMIN_SUPER_USER.getPropertyId());
                LiteYukonRoleProperty roleProp2 = new LiteYukonRoleProperty();
                roleProp2.setRoleID(YukonRole.OPERATOR_ADMINISTRATOR.getRoleId());
                roleProp2.setRolePropertyID(YukonRoleProperty.ADMIN_EDIT_ENERGY_COMPANY.getPropertyId());
                LiteYukonRoleProperty roleProp3 = new LiteYukonRoleProperty();
                roleProp3.setRoleID(YukonRole.OPERATOR_ADMINISTRATOR.getRoleId());
                roleProp3.setRolePropertyID(YukonRoleProperty.ADMIN_MULTISPEAK_SETUP.getPropertyId());
                List<LiteYukonRoleProperty> rolePropsList = new ArrayList<LiteYukonRoleProperty>();
                rolePropsList.add(roleProp1);
                rolePropsList.add(roleProp2);
                rolePropsList.add(roleProp3);
                return rolePropsList;
            }

            @Override
            public List<LiteYukonRole> getAllYukonRoles() {
                LiteYukonRole role = new LiteYukonRole();
                role.setRoleID(YukonRole.OPERATOR_ADMINISTRATOR.getRoleId());
                List<LiteYukonRole> roles = new ArrayList<LiteYukonRole>();
                roles.add(role);
                return roles;
            }
            
        });
        
    }
}
