package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

/**
 * Criteria used to search for only MCT 410 devices
 */
public class MCT410Criteria extends YukonObjectCriteriaHelper {

    public static final String[] MCT_410_TYPES = { "MCT-410IL", "MCT410IL", "MCT-410iLE",
            "MCT-410 kWh Only", "MCT-410CL", "MCT410CL", "MCT-410FL", "MCT-410GL" };

    public MCT410Criteria() {
        for (String type : MCT410Criteria.MCT_410_TYPES) {
            addCriteria("type", type, BooleanClause.Occur.SHOULD);
        }
    }

}
