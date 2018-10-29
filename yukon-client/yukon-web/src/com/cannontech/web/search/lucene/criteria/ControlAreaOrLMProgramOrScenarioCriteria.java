package com.cannontech.web.search.lucene.criteria;

import java.util.Set;

import com.cannontech.common.pao.definition.model.PaoTag;
import com.google.common.collect.ImmutableSet;

public class ControlAreaOrLMProgramOrScenarioCriteria extends YukonPaoTagCriteriaHelper {

    @Override
    protected Set<PaoTag> getPaoTags() {
        return ImmutableSet.of(PaoTag.LM_PROGRAM, PaoTag.LM_SCENARIO, PaoTag.LM_CONTROL_AREA);
    }

}
