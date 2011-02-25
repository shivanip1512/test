package com.cannontech.stars.web.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.authorization.service.PaoPermissionService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteLMProgScenario;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteLMProgramWebPublishing;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.xml.serialize.StarsApplianceCategory;
import com.cannontech.stars.xml.serialize.StarsLMProgram;
import com.cannontech.stars.xml.serialize.StarsLMPrograms;


public class StarsLMPermissionBean 
{
    private LiteStarsEnergyCompany energyCompany = null;
    private LiteYukonUser currentUser = null;
    /*I'm so sick of objects like these, but one must play the game*/
    StarsLMPrograms starsEnrolledLMPrograms = null;
    List<Integer> permittedStarsProgramIDs = null;
    
    public LiteStarsEnergyCompany getEnergyCompany() {
        return energyCompany;
    }
    
    public void setEnergyCompany(LiteStarsEnergyCompany company) {
        energyCompany = company;
    }
    
    public LiteYukonUser getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(LiteYukonUser user) {
        currentUser = user;
    }

    public StarsLMPrograms getStarsEnrolledLMPrograms() {
        return starsEnrolledLMPrograms;
    }

    public void setStarsEnrolledLMPrograms(StarsLMPrograms programs) {
        starsEnrolledLMPrograms = determineVisibilityOfEnrolledPrograms(programs);
    }
    
    private StarsLMPrograms determineVisibilityOfEnrolledPrograms(StarsLMPrograms programs) {
        StarsLMPrograms allowedPrograms = new StarsLMPrograms();
        List<Integer> starsPermittedProgramIds = loadPermittedStarsProgramIDs();
        if(starsPermittedProgramIds.size() < 1)
            return programs;
        for (int i = 0; i < programs.getStarsLMProgramCount(); i++) {
            StarsLMProgram prog = programs.getStarsLMProgram(i);
            if(permittedStarsProgramIDs.contains(prog.getProgramID())) {
                    allowedPrograms.addStarsLMProgram(prog);
            }
        }
        
        return allowedPrograms;
    }
    
    private Set<Integer> getPermittedPaoIDs() {
        PaoPermissionService pService = (PaoPermissionService) YukonSpringHook.getBean("paoPermissionService");
        return pService.getPaoIdsForUserPermission(currentUser, Permission.LM_VISIBLE);
    }

    /*Obtain all stars programIDs that are allowed to be shown*/
    private List<Integer> loadPermittedStarsProgramIDs() {
        Set<Integer> originalPermittedPaoIDs = getPermittedPaoIDs();
        List <Integer> permittedStarsProgramIDs = new ArrayList<Integer>();
        for(Integer paoId : originalPermittedPaoIDs) {
            permittedStarsProgramIDs = determineTypeAndReturnChildIDs(paoId, permittedStarsProgramIDs);
        }
        this.permittedStarsProgramIDs = permittedStarsProgramIDs;
        return permittedStarsProgramIDs;
    }
    
    public List<Integer> getPermittedStarsProgramIDs() {
        return permittedStarsProgramIDs;
    }
    
    private List<Integer> determineTypeAndReturnChildIDs(Integer paoID, List<Integer> currentList) {
        LiteYukonPAObject litePao = DaoFactory.getPaoDao().getLiteYukonPAO(paoID);
        List<Integer> unconvertedYukonPaoIDs = new ArrayList<Integer>();
        PaoDefinitionDao paoDefinitionDao = YukonSpringHook.getBean("paoDefinitionDao", PaoDefinitionDao.class);
        if(paoDefinitionDao.isTagSupported(PaoType.getForId(litePao.getType()), PaoTag.LM_PROGRAM)) {
            unconvertedYukonPaoIDs.add(paoID);
        }
        else if(litePao.getType() == DeviceTypes.LM_CONTROL_AREA) {
            List<Integer> areaProgs = DaoFactory.getLmDao().getProgramsForControlArea(paoID);
            for(Integer progID : areaProgs) {
                unconvertedYukonPaoIDs.add(progID);
            }
        }
        else if(litePao.getType() == DeviceTypes.LM_SCENARIO) {
            LiteLMProgScenario[] scenarioProgs = DaoFactory.getLmDao().getLMScenarioProgs(paoID);
            for(LiteLMProgScenario prog : scenarioProgs) {
                unconvertedYukonPaoIDs.add(prog.getProgramID());
            }
        }
        
        /*need the full lite object to go from the true yukon paobjectID to the stars program id*/
        Iterable<LiteLMProgramWebPublishing> litePrograms = energyCompany.getAllPrograms();
        for(LiteLMProgramWebPublishing fullProg : litePrograms) {
            if(unconvertedYukonPaoIDs.contains(fullProg.getDeviceID()) && !currentList.contains(paoID)) {
                currentList.add(fullProg.getProgramID());
            }
        }
            
        return currentList;
    }
    
    public boolean programAllowed(Integer starsProgID) {
        for(Integer allowedProgID : permittedStarsProgramIDs) {
            if(allowedProgID.intValue() == starsProgID.intValue())
                return true;
        }
        
        
        return false;
    }
    
    public boolean applianceHasAllowedPrograms(StarsApplianceCategory category) {
        /*Remember, even though the name sounds like it is, StarsEnrLMPrograms are NOT
         * just the enrolled programs under that category; they are ALL programs listed 
         * under that category.
         */
        for(int j = 0; j < category.getStarsEnrLMProgramCount(); j++) {
            if(permittedStarsProgramIDs.contains(category.getStarsEnrLMProgram(j).getProgramID()))
                return true;
        }
        
        return false;
    }

}
