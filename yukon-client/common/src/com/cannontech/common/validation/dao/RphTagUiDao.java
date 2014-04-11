package com.cannontech.common.validation.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.validation.model.ReviewPoint;
import com.cannontech.common.validation.model.RphTag;
import com.cannontech.common.model.PagingParameters;

public interface RphTagUiDao {
    SearchResults<ReviewPoint> getReviewPoints(PagingParameters pagingParameters, List<RphTag> tags);

    Map<RphTag, Integer> getAllValidationTagCounts();
    
    int getTotalValidationTagCounts();

    /**
     * Returns the changeIds where the set of tags associated with the changeId
     * AND'd with the mask is EQUAL to the set.
     * 
     * @param rphTags
     * @return
     */
    List<Long> findMatchingChangeIds(Set<RphTag> set);
}
