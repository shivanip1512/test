package com.cannontech.web.picker.v2;

import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;

import com.cannontech.common.search.FilterType;
import com.cannontech.core.dao.NotFoundException;

public class LuceneQueryHelper {

	public static void buildQueryByFilterType(BooleanQuery query, FilterType filterType) {
		
		switch (filterType) {
			case ANALOGPOINT:
				query.add(buildQuery("pointtype","Analog"),BooleanClause.Occur.SHOULD);
				query.add(buildQuery("pointtype","CalcAnalog"),BooleanClause.Occur.SHOULD);
				break;
			case STATUSPOINT:
				query.add(buildQuery("pointtype","Status"),BooleanClause.Occur.SHOULD);
				query.add(buildQuery("pointtype","CalcStatus"),BooleanClause.Occur.SHOULD);
				break;
			default:
				throw new NotFoundException("Could not build Query for unknown filtertype: " + filterType.name());
		}
			
	}
	
	public static void buildQueryByEnergyCompanyIds(BooleanQuery query, Set<Integer> energyCompanyIds) {
        
	    for (Integer energyCompanyId : energyCompanyIds) {
	        query.add(buildQuery("energyCompanyId", Integer.toString(energyCompanyId)), BooleanClause.Occur.SHOULD);
        }
	    
    }
	
	private static TermQuery buildQuery(String field, String value) {
		TermQuery termQuery = new TermQuery(new Term(field, value));
		
		return termQuery;
	}
}
