package com.cannontech.common.search.criteria;

import java.util.Arrays;
import java.util.List;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.common.pao.PaoType;

public class LMProgramCriteria extends YukonPaoTypeCriteriaHelper {

	private static final List<PaoType> TYPES = Arrays.asList(
		PaoType.LM_DIRECT_PROGRAM,
		PaoType.LM_SEP_PROGRAM
	);

	public LMProgramCriteria() {
		super();
        addCriteria(TYPES, BooleanClause.Occur.SHOULD);
	}
}
