package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.pao.DeviceTypes;

/**
 * Criteria used to search for only MCT 470 devices
 */
public class MCT470Criteria extends YukonObjectCriteriaHelper {

    public static final String[] MCT_470_TYPES = { DeviceTypes.STRING_MCT_470[0]};

    public MCT470Criteria() {
        for (String type : MCT470Criteria.MCT_470_TYPES) {
            addCriteria("type", type, BooleanClause.Occur.SHOULD);
        }
    }

}
