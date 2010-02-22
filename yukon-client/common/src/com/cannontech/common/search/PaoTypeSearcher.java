package com.cannontech.common.search;


public interface PaoTypeSearcher extends Searcher<UltraLightPao> {
    public SearchResult<UltraLightPao> search(String queryString, YukonObjectCriteria criteria);
    public SearchResult<UltraLightPao> sameTypePaos(int currentPaoId, YukonObjectCriteria criteria, int start, int count);
}
