package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.pao.DeviceTypes;

/**
 * Criteria used to search for only MCT 430 devices
 */
public class MCT430Criteria extends YukonObjectCriteriaHelper {

    public static final String[] MCT_430_TYPES = { 
    	DeviceTypes.STRING_MCT_430A[0],
    	DeviceTypes.STRING_MCT_430S4[0],
    	DeviceTypes.STRING_MCT_430SL[0],
    	DeviceTypes.STRING_MCT_430A3[0]};

    public MCT430Criteria() {
        for (String type : MCT430Criteria.MCT_430_TYPES) {
            addCriteria("type", type, BooleanClause.Occur.SHOULD);
        }
    }

}
