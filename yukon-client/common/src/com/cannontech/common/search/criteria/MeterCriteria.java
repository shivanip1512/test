package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

/**
 * Criteria used to filter devices and return only devices that are meters
 * 
 */
public class MeterCriteria extends YukonObjectCriteriaHelper {

	public MeterCriteria() {
		addCriteria("isMeter", "true", BooleanClause.Occur.SHOULD);
	}

}
