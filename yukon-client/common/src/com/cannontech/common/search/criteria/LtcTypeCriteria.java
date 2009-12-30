package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.pao.CapControlTypes;

public class LtcTypeCriteria extends YukonObjectCriteriaHelper {

    public LtcTypeCriteria() {
        super();
        addCriteria("type", CapControlTypes.STRING_CAPCONTROL_LTC, BooleanClause.Occur.MUST);
    }

}