package com.cannontech.common.search;


public interface PaoTypeSearcher {
    public SearchResult<UltraLightPao> search(String queryString, YukonObjectCriteria criteria);
    public SearchResult<UltraLightPao> search(String queryString, YukonObjectCriteria criteria, int start, int count);
    public SearchResult<UltraLightPao> sameTypePaos(int currentPaoId, YukonObjectCriteria criteria, int start, int count);
    public SearchResult<UltraLightPao> allPaos(YukonObjectCriteria criteria, int start, int count);
}
