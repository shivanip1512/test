package com.cannontech.stars.dr.optout.service;

import com.cannontech.common.search.SearchResult;
import com.cannontech.stars.dr.optout.model.OptOutSurvey;
import com.google.common.collect.Multimap;

public interface OptOutSurveyService {
    /**
     * Returns a multimap mapping inventory ids to a list of ids of survey which
     * should be taken for programs the inventory is enrolled in. A survey may
     * be listed for multiple inventories so care should be taken to no present
     * that survey multiple times.
     */
    Multimap<Integer, Integer> getActiveSurveyIdsByInventoryId(
            Iterable<Integer> inventoryIds);

    SearchResult<OptOutSurvey> findSurveys(int energyCompanyId, int startIndex,
            int count);
}
