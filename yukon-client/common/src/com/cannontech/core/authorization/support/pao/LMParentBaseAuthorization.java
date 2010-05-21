package com.cannontech.core.authorization.support.pao;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * Abstract base class for LM parent authorization
 */
public abstract class LMParentBaseAuthorization implements PaoAuthorization {
    
    private PaoAuthorizationService paoAuthorizationService;
    protected Permission permission; 
    
    protected abstract boolean checkPaoType(YukonPao pao);
    /**
     * Method to get a list of parent paos for the given pao 
     * @param pao - Pao to get parents for
     * @return Parent paos
     */
    protected abstract List<YukonPao> getParents(PaoIdentifier pao);
    
    /**
     * Method to get a list of parent pao to pao mappings for the given list of paos
     * @param paos - List of paos to get parent mappings for
     * @return List of parent to pao mappings
     */
    protected abstract List<SetMultimap<PaoIdentifier, PaoIdentifier>> getParentMaps(Set<PaoIdentifier> paos);
    
    public AuthorizationResponse isAuthorized(LiteYukonUser user, 
                                              Permission permission, 
                                              PaoIdentifier pao) {
        
        Collection<PaoIdentifier> unknownObjects = Lists.newArrayList();
        Set<PaoIdentifier> authorizedObjects = Sets.newHashSet();
        this.process(Collections.singletonList(pao), 
                     unknownObjects, 
                     authorizedObjects, 
                     user, 
                     permission);
        
        if(authorizedObjects.size() == 1) {
            return AuthorizationResponse.AUTHORIZED;
        } else {
            return AuthorizationResponse.UNKNOWN;
        }
    }
    
    public void process(Collection<PaoIdentifier> inputObjects,
                        Collection<PaoIdentifier> unknownObjects,
                        Set<PaoIdentifier> authorizedObjects,
                        LiteYukonUser user, Permission permission) {
        
        if(!this.permission.equals(permission)) {
            unknownObjects.addAll(inputObjects);
            return;
        }

        Set<PaoIdentifier> objectSet = Sets.newHashSet();
        // Filter out anything that isn't the right type
        for(PaoIdentifier pao : inputObjects) {
            if (checkPaoType(pao)) {
                objectSet.add(pao);
            } else {
                unknownObjects.add(pao);
            }
        }

        Set<PaoIdentifier> authorizedPrograms = Sets.newHashSet();

        // Get a list of parent to object mappings.
        List<SetMultimap<PaoIdentifier, PaoIdentifier>> parentMaps = getParentMaps(objectSet);
        for(SetMultimap<PaoIdentifier, PaoIdentifier> parentMap : parentMaps) {
            
            // Process authorizations for each type of parent mapping
            Set<PaoIdentifier> parentSet = parentMap.keySet();
            if(parentSet.size() > 0) {
                List<PaoIdentifier> authorizedParents = 
                    paoAuthorizationService.filterAuthorized(user, parentSet, permission);
        
                for(PaoIdentifier parent : authorizedParents) {
                    Set<PaoIdentifier> programsAuthorizedByParent = parentMap.get(parent);
                    authorizedPrograms.addAll(programsAuthorizedByParent);
                }
            }
        }
        
        authorizedObjects.addAll(authorizedPrograms);
        
        // Any objects that weren't authorized by a parent are unknown
        SetView<PaoIdentifier> unknownPrograms = Sets.difference(objectSet, authorizedPrograms);
        unknownObjects.addAll(unknownPrograms);

    }
    
    public void setPaoAuthorizationService(PaoAuthorizationService paoAuthorizationService) {
        this.paoAuthorizationService = paoAuthorizationService;
    }
    
    public void setPermission(Permission permission) {
        this.permission = permission;
    }
}
