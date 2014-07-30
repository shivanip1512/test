package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;


public class YukonObjectCriteriaHelper implements YukonObjectCriteria {
    
    BooleanQuery query = new BooleanQuery(false);
    
    public YukonObjectCriteriaHelper() {
        query.setMinimumNumberShouldMatch(1);
    }
    
    public Query getCriteria() {
        return query;
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
        TermRangeQuery rangeQuery = new TermRangeQuery(field, lowerTerm, upperTerm, includeLower, includeUpper);
        query.add(rangeQuery, clause);
    }
    
}