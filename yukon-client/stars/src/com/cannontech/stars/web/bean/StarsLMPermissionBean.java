package com.cannontech.stars.web.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.Pair;
import com.cannontech.core.authorization.service.PaoPermissionService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LiteLMProgScenario;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteLMProgramWebPublishing;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.stars.hardware.MeterHardwareBase;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMProgram;
import com.cannontech.stars.xml.serialize.StarsLMPrograms;


public class StarsLMPermissionBean 
{
    private LiteStarsEnergyCompany energyCompany = null;
    private LiteYukonUser currentUser = null;
    /*I'm so sick of objects like these, but one must play the game*/
    StarsLMPrograms starsLMPrograms = null;
    
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

    public StarsLMPrograms getStarsLMPrograms() {
        return starsLMPrograms;
    }

    public void setStarsLMPrograms(StarsLMPrograms programs) {
        this.starsLMPrograms = determineVisibility(programs);
    }
    
    private StarsLMPrograms determineVisibility(StarsLMPrograms programs) {
        StarsLMPrograms allowedPrograms = new StarsLMPrograms();
        List<Integer> starsPermittedProgramIds = getPermittedStarsProgramIDs();
        if(starsPermittedProgramIds.size() < 1)
            return programs;
        for (int i = 0; i < programs.getStarsLMProgramCount(); i++) {
            StarsLMProgram prog = programs.getStarsLMProgram(i);
            /*need the full lite object to find the true yukon paobjectid*/
            List<LiteLMProgramWebPublishing> litePrograms = energyCompany.getAllPrograms();
            for(LiteLMProgramWebPublishing fullProg : litePrograms) {
                if(prog.getProgramID() == fullProg.getProgramID() 
                        && starsPermittedProgramIds.contains(fullProg.getDeviceID())) {
                    allowedPrograms.addStarsLMProgram(prog);
                }
            }
        }
        
        return allowedPrograms;
    }

    private Set<Integer> getPermittedPaoIDs() {
        PaoPermissionService pService = (PaoPermissionService) YukonSpringHook.getBean("paoPermissionService");
        return pService.getPaoIdsForUserPermission(currentUser, Permission.LM_VISIBLE);
    }

    /*Obtain all stars programIDs that are allowed to be shown*/
    private List<Integer> getPermittedStarsProgramIDs() {
        Set<Integer> originalPermittedPaoIDs = getPermittedPaoIDs();
        List <Integer> permittedStarsProgramIDs = new ArrayList<Integer>();
        for(Integer paoId : originalPermittedPaoIDs) {
            permittedStarsProgramIDs = determineTypeAndReturnChildIDs(paoId, permittedStarsProgramIDs);
        }
        return permittedStarsProgramIDs;
    }
    
    private List<Integer> determineTypeAndReturnChildIDs(Integer paoID, List<Integer> currentList) {
        LiteYukonPAObject litePao = DaoFactory.getPaoDao().getLiteYukonPAO(paoID);
        if(litePao.getType() == DeviceTypes.LM_DIRECT_PROGRAM) {
            if(!currentList.contains(paoID))
                currentList.add(paoID);
        }
        else if(litePao.getType() == DeviceTypes.LM_CONTROL_AREA) {
            List<Integer> areaProgs = DaoFactory.getLmDao().getProgramsForControlArea(paoID);
            for(Integer progID : areaProgs) {
                if(!currentList.contains(progID))
                    currentList.add(progID);
            }
        }
        else if(litePao.getType() == DeviceTypes.LM_SCENARIO) {
            LiteLMProgScenario[] scenarioProgs = DaoFactory.getLmDao().getLMScenarioProgs(paoID);
            for(LiteLMProgScenario prog : scenarioProgs) {
                if(!currentList.contains(prog.getProgramID()))
                    currentList.add(prog.getProgramID());
            }
        }
        
        return currentList;
    }
}
