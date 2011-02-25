package com.cannontech.common.search.criteria;

import java.util.Arrays;
import java.util.List;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.common.pao.PaoType;

public class LMDeviceCriteria extends YukonPaoTypeCriteriaHelper {
    private static final List<PaoType> TYPES = Arrays.asList(
    	PaoType.LM_CONTROL_AREA,
    	PaoType.LM_SCENARIO,
    	PaoType.SIMPLE_SCHEDULE,
    	PaoType.SCRIPT,
    	PaoType.LM_DIRECT_PROGRAM,
    	PaoType.LM_SEP_PROGRAM,
    	PaoType.LM_GROUP_EXPRESSCOMM, 
    	PaoType.LM_GROUP_VERSACOM,
    	PaoType.LM_GROUP_EMETCON,
    	PaoType.MACRO_GROUP,
    	PaoType.LM_GROUP_DIGI_SEP
    );

    public LMDeviceCriteria() {
        super();
        //create all the rules for this criteria
        addCriteria(TYPES, BooleanClause.Occur.SHOULD);
    }
}
