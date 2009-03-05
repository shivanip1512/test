package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.pao.DeviceTypes;

public class LCR3102Criteria extends YukonObjectCriteriaHelper {

	public static final String[] LCR_3102_TYPES = DeviceTypes.STRING_LCR_3102;

	public LCR3102Criteria() {
	    for (String type : LCR3102Criteria.LCR_3102_TYPES) {
	        addCriteria("type", type, BooleanClause.Occur.SHOULD);
	    }
	}
}
