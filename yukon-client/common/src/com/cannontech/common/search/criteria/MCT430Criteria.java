package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

/**
 * Criteria used to search for only MCT 430 devices
 */
public class MCT430Criteria extends YukonObjectCriteriaHelper {

    public static final String[] MCT_430_TYPES = { "MCT-430A", "MCT-430S4", "MCT-430SL" };

    public MCT430Criteria() {
        for (String type : MCT430Criteria.MCT_430_TYPES) {
            addCriteria("type", type, BooleanClause.Occur.SHOULD);
        }
    }

}
