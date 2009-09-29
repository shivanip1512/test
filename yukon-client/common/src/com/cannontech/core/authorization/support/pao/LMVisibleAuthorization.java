package com.cannontech.core.authorization.support.pao;

import java.util.Set;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.Checker;
import com.cannontech.core.authorization.service.PaoPermissionService;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Class used to determine if a user has permission to see an LM pao
 */
public class LMVisibleAuthorization implements PaoAuthorization {

    private PaoPermissionService paoPermissionService;
    private Checker<YukonPao> objectChecker = null;
    private Permission permission = Permission.LM_VISIBLE; 

    public AuthorizationResponse isAuthorized(LiteYukonUser user, Permission permission, YukonPao object) {

        if (this.objectChecker.check(object) && permission.equals(permission)) {

            Set<Integer> paoIdsForUserPermission = 
                paoPermissionService.getPaoIdsForUserPermission(user, permission);
            
            // If there are no LM_VISIBLE permissions set for this user or the pao in question is 
            // in the list, ALLOW
            if (paoIdsForUserPermission.size() == 0 || 
                    paoIdsForUserPermission.contains(object.getPaoIdentifier().getPaoId())) {
                return AuthorizationResponse.AUTHORIZED;
            } else {
                return AuthorizationResponse.UNAUTHORIZED;
            }
        } else {
            // Either object or permission doesn't match - don't know if
            // authorized or not
            return AuthorizationResponse.UNKNOWN;
        }
    }

    public void setPaoPermissionService(PaoPermissionService paoPermissionService) {
        this.paoPermissionService = paoPermissionService;
    }
    
    public void setObjectChecker(Checker<YukonPao> objectChecker) {
        this.objectChecker = objectChecker;
    }
    
    @Override
    public String toString() {
        return permission + " and  " + objectChecker + " authorization";
    }

}
