package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.core.authorization.service.GlobalRoleAndPropertyDescriptionService;


/**
 * If the property is false then the body of the tag is evaluated, otherwise it is skipped.
 * @see CheckProperty
 */
@Configurable("checkGlobalRolesAndPropertiesTagPrototype")
public class CheckGlobalRolesAndPropertiesTag extends YukonTagSupport {
    private String value;
    
    // injected dependencies
    private GlobalRoleAndPropertyDescriptionService descriptionService;
    
    @Override
    public void doTag() throws JspException, IOException {
        
        boolean checkIfAtLeaseOneExists = descriptionService.checkIfAtLeaseOneExists(getValue(), null);
        if (checkIfAtLeaseOneExists) {
            getJspBody().invoke(null);
        }
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setDescriptionService(GlobalRoleAndPropertyDescriptionService descriptionService) {
        this.descriptionService = descriptionService;
    }



}