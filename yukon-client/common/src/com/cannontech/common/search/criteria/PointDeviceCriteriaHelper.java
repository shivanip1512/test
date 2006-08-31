package com.cannontech.common.search.criteria;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import com.cannontech.common.search.PointDeviceCriteria;

public class PointDeviceCriteriaHelper implements PointDeviceCriteria {
    BooleanQuery query = new BooleanQuery(false);
    public PointDeviceCriteriaHelper() {
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
    
}
