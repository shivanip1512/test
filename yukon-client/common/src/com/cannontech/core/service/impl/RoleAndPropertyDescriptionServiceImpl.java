package com.cannontech.core.service.impl;

import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.service.RoleAndPropertyDescriptionService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ReflectivePropertySearcher;

public class RoleAndPropertyDescriptionServiceImpl implements RoleAndPropertyDescriptionService {
    private static final String ROLEID_SUFFIX = ".ROLEID";
    private final ReflectivePropertySearcher propertySearcher = ReflectivePropertySearcher.getRoleProperty();
    private AuthDao authDao;

    @Override
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
