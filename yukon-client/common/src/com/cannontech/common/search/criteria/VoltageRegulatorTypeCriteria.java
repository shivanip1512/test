package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.pao.VoltageRegulatorType;

public class VoltageRegulatorTypeCriteria extends YukonObjectCriteriaHelper {

    public VoltageRegulatorTypeCriteria() {
        super();
        addCriteria("type", VoltageRegulatorType.LOAD_TAP_CHANGER.getDbValue(), BooleanClause.Occur.SHOULD);
        addCriteria("type", VoltageRegulatorType.GANG_OPERATED.getDbValue(), BooleanClause.Occur.SHOULD);
        addCriteria("type", VoltageRegulatorType.PHASE_OPERATED.getDbValue(), BooleanClause.Occur.SHOULD);
    }

}