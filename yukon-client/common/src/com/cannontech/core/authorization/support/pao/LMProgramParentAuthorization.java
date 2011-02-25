package com.cannontech.core.authorization.support.pao;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.dao.DemandResponseDao;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;

/**
 * Class used to determine if a user has permission to see an LM program based on the program's 
 * parent authorizations
 */
public class LMProgramParentAuthorization extends LMParentBaseAuthorization {

    private DemandResponseDao demandResponseDao;
    private PaoDefinitionDao paoDefinitionDao;
    
    @Override
    protected boolean checkPaoType(YukonPao pao) {
    	return paoDefinitionDao.isTagSupported(pao.getPaoIdentifier().getPaoType(), PaoTag.LM_PROGRAM);
    }

    @Override
    protected List<SetMultimap<PaoIdentifier, PaoIdentifier>> getParentMaps(Set<PaoIdentifier> paos) {

        List<SetMultimap<PaoIdentifier, PaoIdentifier>> parentMapList = Lists.newArrayList();

        // Get control area parents map
        SetMultimap<PaoIdentifier, PaoIdentifier> controlAreaToProgramMap = 
            demandResponseDao.getControlAreaToProgramMappingForPrograms(paos);
        parentMapList.add(controlAreaToProgramMap);
        
        // Get scenario parents map
        SetMultimap<PaoIdentifier, PaoIdentifier> scenarioToProgramMap = 
            demandResponseDao.getScenarioToProgramMappingForPrograms(paos);
        parentMapList.add(scenarioToProgramMap);
        
        return parentMapList;
    }

    @Override
    protected List<YukonPao> getParents(PaoIdentifier pao) {
        return demandResponseDao.getControlAreasAndScenariosForProgram(pao);
    }
    
    @Override
    public String toString() {
        return permission + " and  program parent authorization";
    }

    public void setDemandResponseDao(DemandResponseDao demandResponseDao) {
        this.demandResponseDao = demandResponseDao;
    }
    
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
		this.paoDefinitionDao = paoDefinitionDao;
	}
}
