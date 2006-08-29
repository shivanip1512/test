package com.cannontech.common.search.criteria;

import java.util.List;

import org.apache.lucene.search.BooleanClause.Occur;

public class CriteriaRule {
    private List<String> criteria;
    private Occur rule;
    private CriteriaRule() {
        super();
    }

    public CriteriaRule (List <String> c, Occur r) {
        criteria = c;
        rule = r;
        
    }

    public List<String> getCriteria() {
        return criteria;
    }

    public Occur getRule() {
        return rule;
    }
}
