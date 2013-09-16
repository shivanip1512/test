package com.cannontech.common.search.searcher;

import com.cannontech.common.search.criteria.YukonObjectCriteria;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.search.result.UltraLightPoint;


public interface PointDeviceSearcher extends Searcher<UltraLightPoint> {
    public SearchResults<UltraLightPoint> search(String queryString, YukonObjectCriteria criteria);
    public SearchResults<UltraLightPoint> sameDevicePoints(int currentPointId, YukonObjectCriteria criteria, int start, int count);
}
