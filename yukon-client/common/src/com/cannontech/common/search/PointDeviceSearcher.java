package com.cannontech.common.search;


public interface PointDeviceSearcher {
    public SearchResult<UltraLightPoint> search(String queryString, PointDeviceCriteria criteria);
    public SearchResult<UltraLightPoint> search(String queryString, PointDeviceCriteria criteria, int start, int count);
    public SearchResult<UltraLightPoint> sameDevicePoints(int currentPointId, PointDeviceCriteria criteria, int start, int count);
    public SearchResult<UltraLightPoint> allPoints(PointDeviceCriteria criteria, int start, int count);
}
