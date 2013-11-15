package com.cannontech.web.search.lucene.criteria;

import java.util.Set;

import com.cannontech.common.pao.definition.model.PaoTag;
import com.google.common.collect.ImmutableSet;

public class LMDeviceCriteria extends YukonPaoTagCriteriaHelper {

	public LMDeviceCriteria() {
		super();
	}
	
	@Override
	protected Set<PaoTag> getPaoTags() {
		return ImmutableSet.of(PaoTag.LM_GROUP, 
								PaoTag.LM_PROGRAM, 
								PaoTag.LM_CONTROL_AREA, 
								PaoTag.LM_SCENARIO,
								PaoTag.LM_SCHEDULE);
	}
}
