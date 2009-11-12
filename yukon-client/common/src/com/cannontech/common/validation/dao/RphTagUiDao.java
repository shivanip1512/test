package com.cannontech.common.validation.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.validation.model.ReviewPoint;
import com.cannontech.common.validation.model.RphTag;

public interface RphTagUiDao {

	public List<ReviewPoint> getReviewPoints(int afterPaoId, int pageCount, List<RphTag> tags, boolean includeOk);
	
	public Map<RphTag, Integer> getTagCounts();

    public List<Integer> findMatchingChangeIds(Set<RphTag> rphTags);
}
