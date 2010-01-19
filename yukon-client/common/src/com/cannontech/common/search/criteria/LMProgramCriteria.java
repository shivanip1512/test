package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.pao.PAOGroups;

public class LMProgramCriteria extends YukonObjectCriteriaHelper {

	private static final String[] TYPES = {PAOGroups.STRING_LM_DIRECT_PROGRAM[0]};

	public LMProgramCriteria() {

		super();

		for (int i = 0; i < TYPES.length; i++) {
			addCriteria("type", TYPES[i], BooleanClause.Occur.SHOULD);
		}
	}
}
