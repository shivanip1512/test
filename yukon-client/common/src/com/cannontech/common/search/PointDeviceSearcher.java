package com.cannontech.common.search;


public interface PointDeviceSearcher {
    public SearchResult<UltraLightPoint> search(String queryString, YukonObjectCriteria criteria);
    public SearchResult<UltraLightPoint> search(String queryString, YukonObjectCriteria criteria, int start, int count);
    public SearchResult<UltraLightPoint> sameDevicePoints(int currentPointId, YukonObjectCriteria criteria, int start, int count);
    public SearchResult<UltraLightPoint> allPoints(YukonObjectCriteria criteria, int start, int count);
}
