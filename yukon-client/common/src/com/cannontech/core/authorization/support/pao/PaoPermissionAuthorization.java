package com.cannontech.core.authorization.support.pao;

import java.util.Collection;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.authorization.service.PaoPermissionService;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Multimap;

public class PaoPermissionAuthorization implements PaoAuthorization {
    @Autowired private PaoPermissionService paoPermissionService;

    @Override
    public AuthorizationResponse isAuthorized(LiteYukonUser user, Permission permission, PaoIdentifier pao) {
        return paoPermissionService.hasPermission(user, pao, permission);
    }
    
    @Override
    public void process(Collection<PaoIdentifier> inputObjects, Collection<PaoIdentifier> unknownObjects,
            Set<PaoIdentifier> authorizedObjects, LiteYukonUser user, Permission permission) {
        Multimap<AuthorizationResponse, PaoIdentifier> paoAuthorizations = 
            paoPermissionService.getPaoAuthorizations(inputObjects, user, permission);

        authorizedObjects.addAll(paoAuthorizations.get(AuthorizationResponse.AUTHORIZED));
        unknownObjects.addAll(paoAuthorizations.get(AuthorizationResponse.UNKNOWN));
        // unauthorized objects are ignored - these objects are now
        // filtered OUT of our list of objects
    }
    
    @Override
    public String toString() {
        return "pao permission service authorization";
    }
}
