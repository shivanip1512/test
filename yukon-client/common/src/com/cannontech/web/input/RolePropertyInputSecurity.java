package com.cannontech.web.input;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ReflectivePropertySearcher;

/**
 * InputSecutrity implementation that checks a role property to determine
 * security settings
 */
public class RolePropertyInputSecurity implements InputSecurity {

    private String rolePropertyString = null;
    @Autowired public RolePropertyDao rolePropertyDao = null;

    public boolean isEditable(LiteYukonUser user) {
        int propId = ReflectivePropertySearcher.getRoleProperty().getIntForName(rolePropertyString);
        String value = rolePropertyDao.getPropertyStringValue(
                                                                          YukonRoleProperty.getForId(propId), user);

        return Boolean.valueOf(value);
    }

    public void setRoleProperty(String roleProperty) {
        this.rolePropertyString = roleProperty;
    }

}
