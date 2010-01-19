package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.pao.PAOGroups;

public class LMGroupCriteria extends YukonObjectCriteriaHelper {

	private static final String[] TYPES = {PAOGroups.STRING_EXPRESSCOMM_GROUP[0], 
		   								   PAOGroups.STRING_VERSACOM_GROUP[0]};
	
	public LMGroupCriteria() {

		super();

		for (int i = 0; i < TYPES.length; i++) {
			addCriteria("type", TYPES[i], BooleanClause.Occur.SHOULD);
		}
	}
}
