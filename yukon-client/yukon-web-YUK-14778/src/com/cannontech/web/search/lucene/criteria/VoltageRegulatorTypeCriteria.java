package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.common.pao.PaoType;

public class VoltageRegulatorTypeCriteria extends YukonObjectCriteriaHelper {
    
    public VoltageRegulatorTypeCriteria() {
        super();
        addCriteria("type", PaoType.LOAD_TAP_CHANGER.getDbString(), BooleanClause.Occur.SHOULD);
        addCriteria("type", PaoType.GANG_OPERATED.getDbString(), BooleanClause.Occur.SHOULD);
        addCriteria("type", PaoType.PHASE_OPERATED.getDbString(), BooleanClause.Occur.SHOULD);
    }
    
}