package com.cannontech.web.search.lucene.criteria;

import java.util.Set;

import com.cannontech.common.pao.definition.model.PaoTag;
import com.google.common.collect.ImmutableSet;

public class LMProgramOrScenarioCriteria extends YukonPaoTagCriteriaHelper {

	public LMProgramOrScenarioCriteria() {
		super();
	}
	
	@Override
	protected Set<PaoTag> getPaoTags() {
		return ImmutableSet.of(PaoTag.LM_PROGRAM, PaoTag.LM_SCENARIO);
	}
}
