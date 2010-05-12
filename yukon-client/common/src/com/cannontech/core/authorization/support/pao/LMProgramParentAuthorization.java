package com.cannontech.core.authorization.support.pao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.DemandResponseDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * Class used to determine if a user has permission to see an LM program based on the program's 
 * parent authorizations
 */
public class LMProgramParentAuthorization implements PaoAuthorization {

    private PaoAuthorizationService paoAuthorizationService;
    private Permission permission; 
    private DemandResponseDao demandResponseDao;
    
    public AuthorizationResponse isAuthorized(LiteYukonUser user, Permission permission, PaoIdentifier pao) {

        if (checkPaoType(pao) && this.permission.equals(permission)) {

            // Check parent control areas and scenarios
            List<YukonPao> parents = demandResponseDao.getControlAreasAndScenariosForProgram(pao);
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

        Set<PaoIdentifier> programSet = Sets.newHashSet();
        // Filter out anything that isn't a program
        for(PaoIdentifier pao : inputObjects) {
            if (checkPaoType(pao)) {
                programSet.add(pao);
            } else {
                unknownObjects.add(pao);
            }
        }

        Set<PaoIdentifier> authorizedPrograms = Sets.newHashSet();

        // Get control area mappings
        SetMultimap<PaoIdentifier, PaoIdentifier> controlAreaToProgramMap = 
            demandResponseDao.getControlAreaToProgramMappingForPrograms(programSet);
        
        // Process parent control area authorizations (if any)
        Set<PaoIdentifier> controlAreaSet = controlAreaToProgramMap.keySet();
        if(controlAreaSet.size() > 0) {
            List<PaoIdentifier> authorizedControlAreas = 
                paoAuthorizationService.filterAuthorized(user, controlAreaSet, permission);
    
            for(PaoIdentifier controlArea : authorizedControlAreas) {
                Set<PaoIdentifier> programsAuthorizedByParentControlArea = 
                    controlAreaToProgramMap.get(controlArea);
                authorizedPrograms.addAll(programsAuthorizedByParentControlArea);
            }
        }
        
        // Get scenario mappings
        SetMultimap<PaoIdentifier, PaoIdentifier> scenarioToProgramMap = 
            demandResponseDao.getScenarioToProgramMappingForPrograms(programSet);
        
        // Process parent scenario authorizations (if any)
        Set<PaoIdentifier> scenarioSet = scenarioToProgramMap.keySet();
        if(scenarioSet.size() > 0) {
            List<PaoIdentifier> authorizedScenarios = 
                paoAuthorizationService.filterAuthorized(user, scenarioSet, permission);
            
            for(PaoIdentifier scenario : authorizedScenarios) {
                Set<PaoIdentifier> programsAuthorizedByParentScenario = 
                    scenarioToProgramMap.removeAll(scenario.getPaoIdentifier());
                authorizedPrograms.addAll(programsAuthorizedByParentScenario);
            }
        }

        authorizedObjects.addAll(authorizedPrograms);
        
        // Any groups that weren't authorized by a parent are unknown
        SetView<PaoIdentifier> unknownPrograms = Sets.difference(programSet, authorizedPrograms);
        unknownObjects.addAll(unknownPrograms);

    }
    
    private boolean checkPaoType(YukonPao pao) {
        return pao.getPaoIdentifier().getPaoType() == PaoType.LM_DIRECT_PROGRAM;
    }
    
    public void setPaoAuthorizationService(PaoAuthorizationService paoAuthorizationService) {
        this.paoAuthorizationService = paoAuthorizationService;
    }
    
    public void setDemandResponseDao(DemandResponseDao demandResponseDao) {
        this.demandResponseDao = demandResponseDao;
    }
    
    public void setPermission(Permission permission) {
        this.permission = permission;
    }
    
    @Override
    public String toString() {
        return permission + " and  program parent authorization";
    }

}
