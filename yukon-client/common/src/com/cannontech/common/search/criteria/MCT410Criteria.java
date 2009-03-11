package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.pao.DeviceTypes;

/**
 * Criteria used to search for only MCT 410 devices
 */
public class MCT410Criteria extends YukonObjectCriteriaHelper {

    public static final String[] MCT_410_TYPES = { 
    		DeviceTypes.STRING_MCT_410CL[0],
    		DeviceTypes.STRING_MCT_410IL[0],
    		DeviceTypes.STRING_MCT_410FL[0],
    		DeviceTypes.STRING_MCT_410GL[0] };

    public MCT410Criteria() {
        for (String type : MCT410Criteria.MCT_410_TYPES) {
            addCriteria("type", type, BooleanClause.Occur.SHOULD);
        }
    }

}
