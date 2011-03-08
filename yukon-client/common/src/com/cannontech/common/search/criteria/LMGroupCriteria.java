package com.cannontech.common.search.criteria;

import java.util.Set;

import com.cannontech.common.pao.definition.model.PaoTag;
import com.google.common.collect.ImmutableSet;

public class LMGroupCriteria extends YukonPaoTagCriteriaHelper {

	public LMGroupCriteria() {
		super();
	}
	
	@Override
	protected Set<PaoTag> getPaoTags() {
		return ImmutableSet.of(PaoTag.LM_GROUP);
	}
}
