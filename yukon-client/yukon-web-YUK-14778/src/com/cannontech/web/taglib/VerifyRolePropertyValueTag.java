package com.cannontech.web.taglib;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;

/**
 * Checks that the value of the specified 
 * YukonRoleProperty matches the expected String value.
 */
@Configurable("verifyRolePropertyValueTagPrototype")
public class VerifyRolePropertyValueTag extends YukonTagSupport {
    private String property;
    private String expectedValue;
    
    //injected dependency
    private RolePropertyDao rolePropertyDao;
    
    @Override
    public void doTag(){
        YukonRoleProperty roleProperty = YukonRoleProperty.valueOf(property);
        String value = rolePropertyDao.getPropertyStringValue(roleProperty, getYukonUser());
        if(!value.equals(expectedValue)){
            throw new NotAuthorizedException("Incorrect value for property " + property);
        }
    }
    
    public String getProperty(){
        return property;
    }
    
    public void setProperty(String property){
        this.property = property;
    }
    
    public String getExpectedValue(){
        return expectedValue;
    }
    
    public void setExpectedValue(String expectedValue){
        this.expectedValue = expectedValue;
    }
    
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao){
        this.rolePropertyDao = rolePropertyDao;
    }
}
