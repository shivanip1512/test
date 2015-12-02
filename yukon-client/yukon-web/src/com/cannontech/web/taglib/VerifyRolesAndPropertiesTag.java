package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;

/**
 * This will check that the user has the given roles
 * or has a true value for the given role properties.
 * Roles and properties are specified as a comma-separated
 * list of partially qualified class and field names. 
 * Roles can be specified as simply the name of the class
 * or the class name followed by a ".ROLEID". Properties
 * will always be specified in a "ClassName.FIELD_CONSTANT"
 * manner. "Partially qualified" refers to the fact that
 * the leading part of the fully qualified class name
 * may be dropped as described in the ReflectivePropertySearcher.
 * 
 * This works in an OR fashion. If the user passes any
 * of the role or property checks, the page will render.
 * 
 * To get AND behavior, this tag should be repeated
 * multiple times on the page.
 * 
 * To invert any role or property (i.e. to check for the
 * absence of a role or the false value of a role property)
 * preceded the name of the role or property with a ! character.
 */
@Configurable("verifyRolesAndPropertiesTagPrototype")
public class VerifyRolesAndPropertiesTag extends YukonTagSupport {

    private String value;
    
    // injected dependencies
    private RoleAndPropertyDescriptionService descriptionService;
    
    @Override
    public void doTag() throws JspException, IOException {
        
        boolean checkIfAtLeaseOneExists = descriptionService.checkIfAtLeastOneExists(value, getYukonUser());
        if (!checkIfAtLeaseOneExists) {
            throw new NotAuthorizedException("Missing a required role or property to view this page: " + value);
        }
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }

    public void setDescriptionService(RoleAndPropertyDescriptionService descriptionService) {
        this.descriptionService = descriptionService;
    }

}
