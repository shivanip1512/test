package com.cannontech.web.search.searcher;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.search.result.UltraLightPoint;
import com.cannontech.web.search.lucene.criteria.YukonObjectCriteria;


public interface PointDeviceSearcher extends Searcher<UltraLightPoint> {
    public SearchResults<UltraLightPoint> search(String queryString, YukonObjectCriteria criteria);
    public SearchResults<UltraLightPoint> sameDevicePoints(int currentPointId, YukonObjectCriteria criteria, int start, int count);
}
