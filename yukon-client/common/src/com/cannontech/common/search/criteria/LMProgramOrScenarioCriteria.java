package com.cannontech.common.search.criteria;

import java.util.Arrays;
import java.util.List;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.common.pao.PaoType;

public class LMProgramOrScenarioCriteria extends YukonPaoTypeCriteriaHelper {
	private static final List<PaoType> TYPES = Arrays.asList(
    	PaoType.LM_DIRECT_PROGRAM,
    	PaoType.LM_SEP_PROGRAM,
    	PaoType.LM_SCENARIO
	);

    public LMProgramOrScenarioCriteria() {
        super();
        //create all the rules for this criteria
        addCriteria(TYPES, BooleanClause.Occur.SHOULD);
    }
}
