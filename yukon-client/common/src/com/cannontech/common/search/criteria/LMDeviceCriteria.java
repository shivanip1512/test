package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.message.macs.message.Schedule;

public class LMDeviceCriteria extends YukonObjectCriteriaHelper {
    private static final String[] TYPES =  {PAOGroups.STRING_LM_CONTROL_AREA[0], PAOGroups.STRING_LM_SCENARIO[0],
                                            Schedule.SIMPLE_TYPE, Schedule.SCRIPT_TYPE, PAOGroups.STRING_LM_DIRECT_PROGRAM[0],
                                            PAOGroups.STRING_EXPRESSCOMM_GROUP[0], PAOGroups.STRING_VERSACOM_GROUP[0]};

    public LMDeviceCriteria() {
        super();
        //create all the rules for this criteria
        for (int i = 0; i < TYPES.length; i++) {
            addCriteria("type", TYPES[i], BooleanClause.Occur.SHOULD);
        }
    }

}
