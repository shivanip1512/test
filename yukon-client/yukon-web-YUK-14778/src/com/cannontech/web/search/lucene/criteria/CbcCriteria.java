package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.pao.PAOGroups;
import com.google.common.collect.ImmutableSet;

/** 
 * Filter to points belonging to CBCs or RTUs
 */
public class CbcCriteria extends YukonObjectCriteriaHelper {
    
    public CbcCriteria() {
        
        super();
        
        ImmutableSet<PaoType> cbcTypes = PaoType.getCbcTypes();
        ImmutableSet<PaoType> rtuTypes = PaoType.getRtuTypes();
        
        /** CBC Types */
        for (PaoType type : cbcTypes) {
            addCriteria("type", type.getDbString(), BooleanClause.Occur.SHOULD);
        }
        
        // Add the strings that are not referenced in PaoType.
        // "CBC", this one is not in cbcTypes.
        addCriteria("type", PAOGroups.STRING_CAP_BANK_CONTROLLER[1], BooleanClause.Occur.SHOULD);
        // "CAPBANKCONTROLLER", this one is not in cbcTypes.
        addCriteria("type", PAOGroups.STRING_CAP_BANK_CONTROLLER[2], BooleanClause.Occur.SHOULD);
        // "CBC FP 2800", this one is in cbcTypes but the string has a dash '-' in it.
        // TODO do we need this?
        addCriteria("type", PAOGroups.STRING_CBC_FP_2800[1], BooleanClause.Occur.SHOULD);
        
        /** RTU Types */
        for (PaoType type : rtuTypes) {
            addCriteria("type", type.getDbString(), BooleanClause.Occur.SHOULD);
        }
        
        // Add the string that are not referenced in PaoType.
        // All these are in the enum buthe the strings have dashes in them.
        // TODO Do we need these?
        addCriteria("type", PAOGroups.STRING_RTU_DART[1], BooleanClause.Occur.SHOULD);
        addCriteria("type", PAOGroups.STRING_RTU_DNP[1], BooleanClause.Occur.SHOULD);
        addCriteria("type", PAOGroups.STRING_RTU_ILEX[1], BooleanClause.Occur.SHOULD);
        addCriteria("type", PAOGroups.STRING_RTU_MODBUS[1], BooleanClause.Occur.SHOULD);
        addCriteria("type", PAOGroups.STRING_RTU_WELCO[1], BooleanClause.Occur.SHOULD);
    }
    
}