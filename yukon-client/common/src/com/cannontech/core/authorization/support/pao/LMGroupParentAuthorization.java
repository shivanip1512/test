package com.cannontech.core.authorization.support.pao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.DemandResponseDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * Class used to determine if a user has permission to see an LM group based on the group's parent
 * authorizations
 */
public class LMGroupParentAuthorization implements PaoAuthorization {

    private PaoAuthorizationService paoAuthorizationService;
    private Permission permission; 
    private DemandResponseDao demandResponseDao;
    private LoadGroupDao loadGroupDao;
    
    public AuthorizationResponse isAuthorized(LiteYukonUser user, Permission permission, PaoIdentifier pao) {

        if (checkPaoType(pao) && this.permission.equals(permission)) {

            // Check parent macro groups and programs
            List<YukonPao> parents = demandResponseDao.getProgramsForGroup(pao);
            List<PaoIdentifier> macroGroupParents = loadGroupDao.getParentMacroGroups(pao);
            parents.addAll(macroGroupParents);
            for(YukonPao parent : parents) {
                if(paoAuthorizationService.isAuthorized(user, permission, parent)) {
                    return AuthorizationResponse.AUTHORIZED;
                }
            }

            // None of parents were authorized - authorization unknown
            return AuthorizationResponse.UNKNOWN;

        } else {
            // Either object or permission doesn't match - authorization unknown
            return AuthorizationResponse.UNKNOWN;
        }
    }
    
    @Override
    public void process(Collection<PaoIdentifier> inputObjects,
                        Collection<PaoIdentifier> unknownObjects,
                        Set<PaoIdentifier> authorizedObjects,
                        LiteYukonUser user, Permission permission) {
        
        if(!this.permission.equals(permission)) {
            unknownObjects.addAll(inputObjects);
            return;
        }

        Set<PaoIdentifier> groupSet = Sets.newHashSet();
        // Filter out anything that isn't a group
        for(PaoIdentifier pao : inputObjects) {
            if (checkPaoType(pao)) {
                groupSet.add(pao);
            } else {
                unknownObjects.add(pao);
            }
        }

        Set<PaoIdentifier> authorizedGroups = Sets.newHashSet();

        // Get Macro Group mappings
        SetMultimap<PaoIdentifier, PaoIdentifier> macroGroupToGroupMap = 
            loadGroupDao.getMacroGroupToGroupMappings(groupSet);
        
        // Process parent macro group authorizations (if any)
        Set<PaoIdentifier> macroGroupSet = macroGroupToGroupMap.keySet();
        if(macroGroupSet.size() > 0) {
            List<PaoIdentifier> authorizedMacroGroups = 
                paoAuthorizationService.filterAuthorized(user, macroGroupSet, permission);
    
            for(PaoIdentifier macroGroup : authorizedMacroGroups) {
                Set<PaoIdentifier> groupsAuthorizedByParentMacro = 
                    macroGroupToGroupMap.get(macroGroup);
                authorizedGroups.addAll(groupsAuthorizedByParentMacro);
            }
        }
        
        // Get Program mappings
        SetMultimap<PaoIdentifier, PaoIdentifier> programToGroupMap = 
            demandResponseDao.getProgramToGroupMappingForGroups(groupSet);
        
        // Process parent program authorizations (if any)
        Set<PaoIdentifier> programSet = programToGroupMap.keySet();
        if(programSet.size() > 0) {
            List<PaoIdentifier> authorizedPrograms = 
                paoAuthorizationService.filterAuthorized(user, programSet, permission);
            
            for(PaoIdentifier program : authorizedPrograms) {
                Set<PaoIdentifier> groupsAuthorizedByParentProgram = 
                    programToGroupMap.get(program.getPaoIdentifier());
                authorizedGroups.addAll(groupsAuthorizedByParentProgram);
            }
        }

        authorizedObjects.addAll(authorizedGroups);
        
        // Any groups that weren't authorized by a parent are unknown
        SetView<PaoIdentifier> unknownGroups = Sets.difference(groupSet, authorizedGroups);
        unknownObjects.addAll(unknownGroups);
    }
    
    private boolean checkPaoType(YukonPao pao) {
        PaoType paoType = pao.getPaoIdentifier().getPaoType();
        return paoType.getPaoCategory() == PaoCategory.DEVICE && 
               paoType.getPaoClass() == PaoClass.GROUP;
    }
    
    public void setPaoAuthorizationService(PaoAuthorizationService paoAuthorizationService) {
        this.paoAuthorizationService = paoAuthorizationService;
    }
    
    public void setDemandResponseDao(DemandResponseDao demandResponseDao) {
        this.demandResponseDao = demandResponseDao;
    }
    
    public void setLoadGroupDao(LoadGroupDao loadGroupDao){
        this.loadGroupDao = loadGroupDao;        
    }
    
    public void setPermission(Permission permission) {
        this.permission = permission;
    }
    
    @Override
    public String toString() {
        return permission + " and group parent authorization";
    }

}
