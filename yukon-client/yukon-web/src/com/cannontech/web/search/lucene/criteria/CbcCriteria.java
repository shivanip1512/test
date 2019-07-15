package com.cannontech.web.search.lucene.criteria;

import com.cannontech.common.pao.PaoType;

/** 
 * Filter to points belonging to CBCs or RTUs
 */
public class CbcCriteria extends YukonObjectCriteriaHelper {

    public CbcCriteria() {
        
        super();
        
        PaoType.getCbcTypes().forEach(this::typeShouldOccur);
        PaoType.getRtuTypes().forEach(this::typeShouldOccur);
    }
    
}