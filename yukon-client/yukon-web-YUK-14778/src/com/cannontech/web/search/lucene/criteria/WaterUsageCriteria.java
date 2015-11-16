package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.common.pao.PaoType;

public class WaterUsageCriteria extends YukonObjectCriteriaHelper {
    
    public WaterUsageCriteria() {
        addCriteria("type", PaoType.RFWMETER.getDbString(), BooleanClause.Occur.SHOULD);
    }
    
}