package com.cannontech.web.input;

import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ReflectivePropertySearcher;

/**
 * InputSecutrity implementation that checks a role property to determine
 * security settings
 */
public class RolePropertyInputSecurity implements InputSecurity {

    private String roleProperty = null;
    private AuthDao authDao = null;

    public boolean isEditable(LiteYukonUser user) {
        int propId = ReflectivePropertySearcher.getRoleProperty().getIntForName(roleProperty);
        String value = authDao.getRolePropertyValue(user, propId);

        return Boolean.valueOf(value);
    }

    public void setRoleProperty(String roleProperty) {
        this.roleProperty = roleProperty;
    }

    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }

}
