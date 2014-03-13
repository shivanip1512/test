package com.cannontech.core.authorization.support.pao;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.DemandResponseDao;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;

/**
 * Class used to determine if a user has permission to see an LM group based on the group's parent
 * authorizations
 */
public class LMGroupParentAuthorization extends LMParentBaseAuthorization {
    @Autowired private DemandResponseDao demandResponseDao;
    @Autowired private LoadGroupDao loadGroupDao;

    @Override
    protected boolean checkPaoType(YukonPao pao) {
        PaoType paoType = pao.getPaoIdentifier().getPaoType();
        return paoType.getPaoCategory() == PaoCategory.DEVICE && 
               paoType.getPaoClass() == PaoClass.GROUP;
    }

    @Override
    protected List<SetMultimap<PaoIdentifier, PaoIdentifier>> getParentMaps(Set<PaoIdentifier> paos) {
        List<SetMultimap<PaoIdentifier, PaoIdentifier>> parentMapList = Lists.newArrayList();

        // Get macro group parents map
        SetMultimap<PaoIdentifier, PaoIdentifier> macroGroupToGroupMap = 
            loadGroupDao.getMacroGroupToGroupMappings(paos);
        parentMapList.add(macroGroupToGroupMap);
        
        // Get program parents map
        SetMultimap<PaoIdentifier, PaoIdentifier> programToGroupMap = 
            demandResponseDao.getProgramToGroupMappingForGroups(paos);
        parentMapList.add(programToGroupMap);
        
        return parentMapList;
    }

    @Override
    protected List<? extends YukonPao> getParents(PaoIdentifier pao) {
        List<PaoIdentifier> parents = demandResponseDao.getProgramsForGroup(pao);
        List<PaoIdentifier> macroGroupParents = loadGroupDao.getParentMacroGroups(pao);
        parents.addAll(macroGroupParents);
        
        return parents;
    }

    @Override
    public String toString() {
        return permission + " and group parent authorization";
    }
}
