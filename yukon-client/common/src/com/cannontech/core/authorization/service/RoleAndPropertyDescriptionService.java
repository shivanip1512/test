package com.cannontech.core.authorization.service;

import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ReflectivePropertySearcher;

public class RoleAndPropertyDescriptionService {
    private static final String ROLEID_SUFFIX = ".ROLEID";
    private final ReflectivePropertySearcher propertySearcher = ReflectivePropertySearcher.getRoleProperty();
    private AuthDao authDao;

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
     * of the role or property checks a boolean value of
     * true is returned.
     */
    public boolean checkIfAtLeaseOneExists(final String rolePropDescription, 
            final LiteYukonUser user) {
        
        if (rolePropDescription.equals("*")) return true; // Match All

        // split value
        String[] valueArray = rolePropDescription.split("[\\s,\\n]+");
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
                int intForFQN = propertySearcher.getIntForName(roleIdFqn);
                boolean hasRole = authDao.checkRole(user, intForFQN);
                if (hasRole != inverted) {
                    return true;
                }

                continue;

            } catch (IllegalArgumentException e) {
            }

            // not a role, check if it is a property
            try {
                String propertyIdFqn = classOrFieldName;
                int intForFQN = propertySearcher.getIntForName(propertyIdFqn);
                boolean hasProperty = authDao.checkRoleProperty(user, intForFQN);
                if (hasProperty != inverted) {
                    return true;
                }

                continue;

            } catch (IllegalArgumentException ignore) { }

            // if we get here, we must not have a valid role or property
            throw new IllegalArgumentException("Can't recognize: " + classOrFieldName);
        }
        // if we get here, nothing matched
        return false;  
    }

    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }

}
