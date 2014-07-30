package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.pao.DeviceTypes;

public class TwoWayLcrCriteria extends YukonObjectCriteriaHelper {
    
    public TwoWayLcrCriteria() {
        
        for (String type : DeviceTypes.STRING_LCR_3102) {
            addCriteria("type", type, BooleanClause.Occur.SHOULD);
        }
    }
    
}