package com.cannontech.common.search.criteria;

import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.TermQuery;

import com.cannontech.common.pao.PaoType;

public class YukonPaoTypeCriteriaHelper extends YukonObjectCriteriaHelper {
    public YukonPaoTypeCriteriaHelper() {
    	super();
    }

    /** Helper method to add criteria for all PaoTypes (dbString values). */
    protected void addCriteria(List<PaoType> paoTypes, BooleanClause.Occur clause) {
    	for (PaoType paoType : paoTypes) {
            addCriteria(paoType, clause);
        }
    }

    /** Helper method to convert PaoType to string value. */
    protected void addCriteria(PaoType paoType, BooleanClause.Occur clause) {
        TermQuery termQuery = new TermQuery(new Term("type", paoType.getDbString()));
        query.add(termQuery, clause);
    }
}
