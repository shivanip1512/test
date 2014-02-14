package com.cannontech.common.validation.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.validation.model.ReviewPoint;
import com.cannontech.common.validation.model.RphTag;

public interface RphTagUiDao {
    SearchResults<ReviewPoint> getReviewPoints(int page, int itemsPerPage, List<RphTag> tags);

    Map<RphTag, Integer> getAllValidationTagCounts();

    /**
     * Returns the changeIds where the set of tags associated with the changeId
     * AND'd with the mask is EQUAL to the set.
     * 
     * @param rphTags
     * @return
     */
    List<Long> findMatchingChangeIds(Set<RphTag> set, Set<RphTag> mask);
}
