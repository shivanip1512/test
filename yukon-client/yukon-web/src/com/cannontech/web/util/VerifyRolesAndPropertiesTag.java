package com.cannontech.web.util;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.util.ReflectivePropertySearcher;
import com.cannontech.web.taglib.YukonTagSupport;

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
    
    private static final String ROLEID_SUFFIX = ".ROLEID";

    private String value;
    
    // injected dependencies
    private AuthDao authDao;
    
    @Override
    public void doTag() throws JspException, IOException {
        ReflectivePropertySearcher roleProperty = ReflectivePropertySearcher.getRoleProperty();
        // split value
        String[] valueArray = value.split("[\\s,\\n]+");
        for (String classOrFieldName : valueArray) {
            classOrFieldName = classOrFieldName.trim();
            if (classOrFieldName.isEmpty()) continue;
            
            // check if it is inverted
            boolean inverted = false;
            if (classOrFieldName.startsWith("!")) {
                classOrFieldName = classOrFieldName.substring(1);
                inverted = true;
            }
            // see if it is a role
            try {
                String roleIdFqn = classOrFieldName;
                if (!classOrFieldName.endsWith(ROLEID_SUFFIX)) {
                    roleIdFqn += ROLEID_SUFFIX;
                }
                int intForFQN = roleProperty.getIntForName(roleIdFqn);
                boolean hasRole = authDao.checkRole(getYukonUser(), intForFQN);
                if (hasRole != inverted) {
                    return;
                }
                
                continue;
                
            } catch (IllegalArgumentException e) {
            }
            
            // not a role, check if it is a property
            try {
                String propertyIdFqn = classOrFieldName;
                int intForFQN = roleProperty.getIntForName(propertyIdFqn);
                boolean hasProperty = authDao.checkRoleProperty(getYukonUser(), intForFQN);
                if (hasProperty != inverted) {
                    return;
                }
                
                continue;

            } catch (IllegalArgumentException ignore) { }
            
            // if we get here, we must not have a valid role or property
            throw new IllegalArgumentException("Can't recognize: " + classOrFieldName);
        }
        // if we get here, nothing matched
        throw new NotAuthorizedException("Missing a required role or property to view this page: " + StringUtils.join(valueArray));
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }

}
