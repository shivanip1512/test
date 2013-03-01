package com.cannontech.stars.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.core.authentication.service.impl.MockRolePropertyDaoImpl;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.database.cache.MockStarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.user.checker.UserChecker;

public class EnergyCompanyServiceImplTest {

    private final EnergyCompanyServiceImpl impl = new EnergyCompanyServiceImpl();

    private final LiteYukonUser user_false_1 = new LiteYukonUser(1);
    private final LiteYukonUser user_false_2 = new LiteYukonUser(2);
    private final LiteYukonUser user_false_3 = new LiteYukonUser(3);
    
    private final LiteYukonUser user_true_1 = new LiteYukonUser(4);
    private final LiteYukonUser user_true_2 = new LiteYukonUser(5);
    private final LiteYukonUser user_true_3 = new LiteYukonUser(6);
    private final LiteYukonUser user_true_4 = new LiteYukonUser(7);
    private final LiteYukonUser user_true_5 = new LiteYukonUser(8);

    // Maps used for mock role property look ups.
    private final Map<LiteYukonUser, Boolean> adminSuperUsers = new HashMap<LiteYukonUser, Boolean>() {{
        put(user_false_1, false); put(user_false_2, false); put(user_false_3, false); put(user_true_1, false);
        put(user_true_2, true); put(user_true_3, true); put(user_true_4, true); put(user_true_5, true);
    }};

    private final Map<LiteYukonUser, Boolean> adminEditEnergyCompanyUsers = new HashMap<LiteYukonUser, Boolean>() {{
        put(user_false_1, false); put(user_false_2, false); put(user_false_3, true); put(user_true_1, true);
        put(user_true_2, false); put(user_true_3, false); put(user_true_4, true); put(user_true_5, true);
    }};

    @Before
    public void setUp() {
        
        ReflectionTestUtils.setField(impl, "rolePropertyDao", new MockRolePropertyDaoImpl() {
            @Override
            public boolean getPropertyBooleanValue(YukonRoleProperty property, LiteYukonUser user) {
                if (property == YukonRoleProperty.ADMIN_SUPER_USER) {
                    return adminSuperUsers.get(user);
                } else if (property == YukonRoleProperty.ADMIN_EDIT_ENERGY_COMPANY) {
                    return adminEditEnergyCompanyUsers.get(user);
                }
                return false;
            }
        });
        
        ReflectionTestUtils.setField(impl, "starsDatabaseCache", new MockStarsDatabaseCache(){
            @Override
            public List<LiteStarsEnergyCompany> getAllEnergyCompanies() {
                List<LiteStarsEnergyCompany> allECs = new ArrayList<>();
                allECs.add(new MockYukonEnergyCompany());
                return allECs;
            }
        });
    }
    
    @Test
    public void test_createCanEditEnergyCompany() {
        UserChecker userChecker = impl.createCanEditEnergyCompany();
        
        // Based on superUser || (edit && isEcOperator)
        
        // 1,2,3, & 4 Not Super Users
        Assert.assertFalse(userChecker.check(user_false_1));
        Assert.assertFalse(userChecker.check(user_false_2));
        // 3 & 4 have role edit EC role property set true
        Assert.assertFalse(userChecker.check(user_false_3));
        // 4 also is an operator
        Assert.assertTrue(userChecker.check(user_true_1));
        
        // 4,5,6, & 8 Super Users
        Assert.assertTrue(userChecker.check(user_true_2));
        Assert.assertTrue(userChecker.check(user_true_3));
        Assert.assertTrue(userChecker.check(user_true_4));
        Assert.assertTrue(userChecker.check(user_true_5));
    }

    class MockYukonEnergyCompany extends LiteStarsEnergyCompany {
        @Override
        public List<Integer> getOperatorLoginIDs() {
            return new ArrayList<Integer>() {{add(2);add(4);add(6);add(8);}}; 
        }
    }
}
