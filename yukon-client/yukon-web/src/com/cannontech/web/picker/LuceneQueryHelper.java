package com.cannontech.web.picker;

import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;

import com.cannontech.common.search.FilterType;
import com.cannontech.core.dao.NotFoundException;

public class LuceneQueryHelper {

    public static void buildQueryByFilterType(BooleanQuery.Builder query, FilterType filterType) {
        
        BooleanQuery.Builder pointTypeQuery = new BooleanQuery.Builder();
        
        switch (filterType) {
            case ANALOGPOINT:
                pointTypeQuery.add(buildQuery("pointtype","Analog"),BooleanClause.Occur.SHOULD);
                pointTypeQuery.add(buildQuery("pointtype","CalcAnalog"),BooleanClause.Occur.SHOULD);
                break;
            case STATUSPOINT:
                pointTypeQuery.add(buildQuery("pointtype","Status"),BooleanClause.Occur.SHOULD);
                pointTypeQuery.add(buildQuery("pointtype","CalcStatus"),BooleanClause.Occur.SHOULD);
                break;
            default:
                throw new NotFoundException("Could not build Query for unknown filtertype: " + filterType.name());
        }
    
        query.add(pointTypeQuery.build(), BooleanClause.Occur.MUST);
    }
    
    public static void buildQueryByEnergyCompanyIds(BooleanQuery.Builder query, Set<Integer> energyCompanyIds) {
        BooleanQuery.Builder energyCompanyIdQuery = new BooleanQuery.Builder();
        
        for (Integer energyCompanyId : energyCompanyIds) {
            energyCompanyIdQuery.add(buildQuery("energyCompanyId", Integer.toString(energyCompanyId)), 
                    BooleanClause.Occur.SHOULD);
        }
        
        query.add(energyCompanyIdQuery.build(), BooleanClause.Occur.MUST);
    }
    
    private static TermQuery buildQuery(String field, String value) {
        TermQuery termQuery = new TermQuery(new Term(field, value));
        
        return termQuery;
    }
    
}