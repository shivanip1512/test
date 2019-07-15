package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;

import com.cannontech.common.pao.PaoType;


public class YukonObjectCriteriaHelper implements YukonObjectCriteria {
    
    BooleanQuery.Builder query = new BooleanQuery.Builder().setDisableCoord(false);
    
    public YukonObjectCriteriaHelper() {
        query.setMinimumNumberShouldMatch(1);
    }
    
    @Override
    public Query getCriteria() {
        return query.build();
    }
    
    protected void addCriteria(String field, String value, BooleanClause.Occur clause) {
        TermQuery termQuery = new TermQuery(new Term(field, value));
   
        query.add(termQuery, clause);
    }
    
    protected void addCriteria(String field, Integer value, BooleanClause.Occur clause) {
        TermQuery termQuery = new TermQuery(new Term(field, value.toString()));
        query.add(termQuery, clause);
    }
    
    protected void addCriteria(String field, String lowerTerm, String upperTerm, boolean includeLower,
                               boolean includeUpper, BooleanClause.Occur clause) {
        TermRangeQuery rangeQuery = TermRangeQuery.newStringRange(field, lowerTerm, upperTerm, includeLower, includeUpper);
        query.add(rangeQuery, clause);
    }
 
    protected void typeShouldOccur(PaoType type) {
        addCriteria("type", type.getDbString(), BooleanClause.Occur.SHOULD);
    }
}