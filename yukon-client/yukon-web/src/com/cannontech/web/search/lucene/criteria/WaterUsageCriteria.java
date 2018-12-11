package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.common.pao.PaoType;

public class WaterUsageCriteria extends YukonObjectCriteriaHelper {
    
    public WaterUsageCriteria() {
        for (PaoType paoType : PaoType.getWaterMeterTypes()) {
            addCriteria("type", paoType.getDbString(), BooleanClause.Occur.SHOULD);
        }
    }
}