package com.cannontech.common.search.searcher;

import com.cannontech.common.search.criteria.YukonObjectCriteria;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.search.result.UltraLightPao;


public interface PaoTypeSearcher extends Searcher<UltraLightPao> {
    public SearchResults<UltraLightPao> search(String queryString, YukonObjectCriteria criteria);
    public SearchResults<UltraLightPao> sameTypePaos(int currentPaoId, YukonObjectCriteria criteria, int start, int count);
}
