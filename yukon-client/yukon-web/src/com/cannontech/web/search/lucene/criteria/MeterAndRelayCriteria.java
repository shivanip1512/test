package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.common.pao.PaoType;

/**
 * Criteria used to filter devices and return only devices that are meters or relays
 * 
 */
public class MeterAndRelayCriteria extends YukonObjectCriteriaHelper {
    
    public MeterAndRelayCriteria() {
        addCriteria("isMeter", "true", BooleanClause.Occur.SHOULD);
        for (PaoType relay : PaoType.getRfRelayTypes()) {
            addCriteria("type", relay.getDbString(), BooleanClause.Occur.SHOULD);
        }
    }
}