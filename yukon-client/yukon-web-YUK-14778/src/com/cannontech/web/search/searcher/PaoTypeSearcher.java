package com.cannontech.web.search.searcher;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.search.result.UltraLightPao;
import com.cannontech.web.search.lucene.criteria.YukonObjectCriteria;


public interface PaoTypeSearcher extends Searcher<UltraLightPao> {
    public SearchResults<UltraLightPao> search(String queryString, YukonObjectCriteria criteria);
    public SearchResults<UltraLightPao> sameTypePaos(int currentPaoId, YukonObjectCriteria criteria, int start, int count);
}
