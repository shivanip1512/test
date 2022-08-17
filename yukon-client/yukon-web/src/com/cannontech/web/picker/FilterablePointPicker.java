package com.cannontech.web.picker;

import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

import com.cannontech.common.search.FilterType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.search.lucene.criteria.YukonObjectCriteria;

public class FilterablePointPicker extends PointPicker {
    @Override
    public YukonObjectCriteria combineCriteria(YukonObjectCriteria criteria, YukonUserContext userContext, String extraArgs) {
        
        FilterType filterType = FilterType.valueOf(extraArgs);
        final BooleanQuery.Builder query = new BooleanQuery.Builder().setDisableCoord(false);
        
        if (criteria != null) {
            query.add(criteria.getCriteria(), Occur.MUST);
        }
        
        LuceneQueryHelper.buildQueryByFilterType(query, filterType);
        
        return new YukonObjectCriteria() {
            @Override
            public Query getCriteria() {
                return query.build();
            }
        };
    }
    
}