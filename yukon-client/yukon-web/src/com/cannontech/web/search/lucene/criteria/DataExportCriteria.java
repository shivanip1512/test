package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;
import com.cannontech.common.pao.PaoClass;

public class DataExportCriteria extends YukonObjectCriteriaHelper {
    
    public DataExportCriteria() {        
        addCriteria("isDevice", "true", BooleanClause.Occur.SHOULD);
        addCriteria("paoclass", PaoClass.LOADMANAGEMENT.getDbString(), BooleanClause.Occur.SHOULD);
        addCriteria("paoid", "1", null, true, false, BooleanClause.Occur.MUST);
    }
}
