package com.cannontech.web.picker.v2;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause.Occur;

import com.cannontech.common.search.FilterType;
import com.cannontech.common.search.YukonObjectCriteria;
import com.cannontech.user.YukonUserContext;

public class FilterablePointPicker extends PointPicker {

    public YukonObjectCriteria combineCriteria(YukonObjectCriteria baseCriteria, YukonUserContext userContext, String extraArgs) {
    	
    	FilterType filterType = FilterType.valueOf(extraArgs);
    	final BooleanQuery query = new BooleanQuery(false);
    	
    	if (baseCriteria != null) {
    		query.add(baseCriteria.getCriteria(), Occur.MUST);
    	}
    	
    	LuceneQueryHelper.buildQueryByFilterType(query, filterType);
    	
    	return new YukonObjectCriteria() {
			@Override
			public Query getCriteria() {
				return query;
			}
		};
    }
}
