package com.cannontech.common.search;


public interface PointDeviceSearcher extends Searcher<UltraLightPoint> {
    public SearchResult<UltraLightPoint> search(String queryString, YukonObjectCriteria criteria);
    public SearchResult<UltraLightPoint> sameDevicePoints(int currentPointId, YukonObjectCriteria criteria, int start, int count);
}
