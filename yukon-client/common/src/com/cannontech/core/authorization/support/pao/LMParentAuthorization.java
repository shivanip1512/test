package com.cannontech.core.authorization.support.pao;

import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.Checker;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.DemandResponseDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;

/**
 * Class used to determine if a user has permission to see an LM pao
 */
public class LMParentAuthorization implements PaoAuthorization {

    private PaoAuthorizationService paoAuthorizationService;
    private Checker<YukonPao> objectChecker;
    private Permission permission; 
    private DemandResponseDao demandResponseDao;
    private LoadGroupDao loadGroupDao;

    public AuthorizationResponse isAuthorized(LiteYukonUser user, Permission permission, YukonPao pao) {

        if (objectChecker.check(pao) && this.permission.equals(permission)) {
            
            PaoType type = pao.getPaoIdentifier().getPaoType();
            if(PaoType.LM_CONTROL_AREA.equals(type) || PaoType.LM_SCENARIO.equals(type)) {
                // Control areas and Scenarios don't have parents
                return AuthorizationResponse.UNKNOWN;
            } else if (PaoType.LM_DIRECT_PROGRAM.equals(type)) {
                
                List<YukonPao> parents = demandResponseDao.getControlAreasAndScenariosForProgram(pao);
                for(YukonPao parent : parents) {
                    if(paoAuthorizationService.isAuthorized(user, permission, parent)) {
                        return AuthorizationResponse.AUTHORIZED;
                    }
                }
            }  else {
                
                List<YukonPao> parents = demandResponseDao.getProgramsForGroup(pao);
                List<PaoIdentifier> macroGroupParents = loadGroupDao.getParentMacroGroups(pao);
                parents.addAll(macroGroupParents);
                for(YukonPao parent : parents) {
                    if(paoAuthorizationService.isAuthorized(user, permission, parent)) {
                        return AuthorizationResponse.AUTHORIZED;
                    }
                }
            }

            // None of parents were authorized, don't know if we are authorized
            return AuthorizationResponse.UNKNOWN;
                
        } else {
            // Either object or permission doesn't match - don't know if
            // authorized or not
            return AuthorizationResponse.UNKNOWN;
        }
    }
    
    public void setPaoAuthorizationService(PaoAuthorizationService paoAuthorizationService) {
        this.paoAuthorizationService = paoAuthorizationService;
    }
    
    public void setObjectChecker(Checker<YukonPao> objectChecker) {
        this.objectChecker = objectChecker;
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
        return permission + " and  " + objectChecker + " authorization";
    }

}
