package com.cannontech.user.checker;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * This is both a base class and a factory for OptionPropertyCheckers.
 * An UserChecker is an object that has a single check() method
 * that takes a LiteYukonUser as its argument. The provided factory methods
 * create UserChecker for the most common cases (to check a role
 * and to check the value of a boolean role property).
 */
public class RolePropertyUserCheckerFactory {
    private static final UserCheckerBase nullChecker = new NullUserChecker();
    private AuthDao authDao;
    private RoleAndPropertyDescriptionService descriptionService;
    
    /**
     * @see RoleAndPropertyDescriptionService.checkIfAtLeaseOneExists()
     * @param descriptions of RoleID's and RoleProperty's
     *        to base the UserChecker on
     * @return an UserChecker
     */
    public UserChecker createRoleAndPropertyDescriptionChecker(final String descriptions) {
        
        if (StringUtils.isBlank(descriptions)) {
            return nullChecker;
        }
        
        UserChecker checker = new UserCheckerBase() {
            @Override
            public boolean check(LiteYukonUser user) {
                if (user == null) return false;

                boolean isValidCheck = 
                    descriptionService.checkIfAtLeaseOneExists(descriptions, user);
                return isValidCheck;
            }
        };
        return checker;
    }
    
    /**
     * Create an OptionPropertyChecker that will return true if the
     * user has the indicated role property and it is set to true.
     * @param property to base the OptionPropertyChecker on
     * @return an OptionPropertyChecker
     */
    public UserChecker createPropertyChecker(final int propertyId) {
        
        UserCheckerBase checker = new UserCheckerBase() {
            @Override
            public boolean check(LiteYukonUser user) {
                return getBooleanForRoleProperty(propertyId, user);
            };
        };
        return checker;
    }
    
    private boolean getBooleanForRoleProperty(final int propertyId, LiteYukonUser user) {
        if (user == null) {
            return false;
        }
        
        String val = authDao.getRolePropertyValue(user,
                                                    propertyId );
        return BooleanUtils.toBoolean(val);
    }

    @Required
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }

    @Required
    public void setDescriptionService(
            RoleAndPropertyDescriptionService descriptionService) {
        this.descriptionService = descriptionService;
    }

}
