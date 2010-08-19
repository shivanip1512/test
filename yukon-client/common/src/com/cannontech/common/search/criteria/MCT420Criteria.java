package com.cannontech.common.search.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.pao.DeviceTypes;

public class MCT420Criteria extends YukonObjectCriteriaHelper {
    
    public static final String[] MCT_420_TYPES = {
        DeviceTypes.STRING_MCT_420FL[0],
        DeviceTypes.STRING_MCT_420FLD[0],
        DeviceTypes.STRING_MCT_420CL[0],
        DeviceTypes.STRING_MCT_420CLD[0]
        };
    
    public MCT420Criteria() {
        for (String type : MCT420Criteria.MCT_420_TYPES){
            addCriteria("type", type, BooleanClause.Occur.SHOULD);
        }
    }
}
