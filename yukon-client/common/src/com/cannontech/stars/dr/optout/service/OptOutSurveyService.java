package com.cannontech.stars.dr.optout.service;

import org.joda.time.ReadableInstant;

import com.cannontech.common.search.result.SearchResults;
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

    SearchResults<OptOutSurvey> findSurveys(int energyCompanyId, int startIndex,
            int count);

    /**
     * Counts the total number of survey results added between 2 instants
     */
    public int countAllSurveyResultsBetween(ReadableInstant begin, ReadableInstant end);
}
