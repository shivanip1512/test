package com.cannontech.web.search.searcher;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.search.result.UltraLightMonitor;
import com.cannontech.web.search.lucene.criteria.YukonObjectCriteria;

public interface MonitorSearcher extends Searcher<UltraLightMonitor> {
    public SearchResults<UltraLightMonitor> search(String queryString, YukonObjectCriteria criteria);

}
