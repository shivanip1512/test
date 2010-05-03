package com.cannontech.core.authorization.support.pao;

import java.util.Collection;
import java.util.Queue;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.authorization.service.PaoPermissionService;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Multimap;

public class PaoPermissionAuthorization implements PaoAuthorization {

    private PaoPermissionService paoPermissionService = null;

    public void setPaoPermissionService(PaoPermissionService paoPermissionService) {
        this.paoPermissionService = paoPermissionService;
    }

    public AuthorizationResponse isAuthorized(LiteYukonUser user, Permission permission,
            YukonPao pao) {
        return paoPermissionService.hasPermission(user, pao, permission);

    }
    
    @Override
    public void process(Queue<YukonPao> inputQueue,
                        Queue<YukonPao> unknownQueue,
                        Collection<YukonPao> authorizedObjects,
                        LiteYukonUser user, Permission permission) {
     
        Multimap<AuthorizationResponse, YukonPao> paoAuthorizations = 
            paoPermissionService.getPaoAuthorizations(inputQueue, user, permission);
        
        authorizedObjects.addAll(paoAuthorizations.get(AuthorizationResponse.AUTHORIZED));
        unknownQueue.addAll(paoAuthorizations.get(AuthorizationResponse.UNKNOWN));
        // unauthorized objects are ignored
    }
    
    @Override
    public String toString() {
        return "pao permission service authorization";
    }

}
