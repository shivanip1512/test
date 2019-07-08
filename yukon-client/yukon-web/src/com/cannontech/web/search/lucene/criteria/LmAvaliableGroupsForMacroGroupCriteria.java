package com.cannontech.web.search.lucene.criteria;

import java.util.List;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.common.pao.PaoType;

public class LmAvaliableGroupsForMacroGroupCriteria extends YukonObjectCriteriaHelper {
    
    public LmAvaliableGroupsForMacroGroupCriteria () {
        super();
        List<PaoType> loadGroupTypes = PaoType.getAllLMGroupTypes();
        for (PaoType paoType : loadGroupTypes) {
            if (paoType.supportsMacroGroup()) {
                addCriteria("type", paoType.getDbString(), BooleanClause.Occur.SHOULD);
            }
        }
    }
    
}
