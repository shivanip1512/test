package com.cannontech.web.picker.v2;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.search.SearchResult;
import com.cannontech.common.search.Searcher;
import com.cannontech.common.search.YukonObjectCriteria;
import com.cannontech.user.YukonUserContext;

public abstract class LucenePicker<T> extends BasePicker<T> {
    protected YukonObjectCriteria criteria = null;
    protected Searcher<T> searcher;

    public YukonObjectCriteria getCriteria() {
        return criteria;
    }


    @Override
    public SearchResult<T> search(String ss, int start, int count,
            String extraArgs, YukonUserContext userContext) {
        SearchResult<T> hits;
        
        YukonObjectCriteria combinedCriteria = combineCriteria(criteria, extraArgs);
		if (StringUtils.isBlank(ss)) {
            hits = searcher.all(combinedCriteria, start, count);
        } else {
            hits = searcher.search(ss, combinedCriteria, start , count);
        }
        return hits;
    }

    public YukonObjectCriteria combineCriteria(YukonObjectCriteria baseCriteria, String extraArgs) {
    	return baseCriteria;
    }
    
    public void setCriteria(YukonObjectCriteria criteria) {
        this.criteria = criteria;
    }

    public void setSearcher(Searcher<T> searcher) {
        this.searcher = searcher;
    }
}
