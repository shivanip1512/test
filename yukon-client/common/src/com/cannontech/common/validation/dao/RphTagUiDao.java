package com.cannontech.common.validation.dao;

import java.util.List;

import com.cannontech.common.validation.model.ReviewPoint;
import com.cannontech.common.validation.model.RphTag;

public interface RphTagUiDao {

	public List<ReviewPoint> getReviewPoints(int afterPaoId, int pageCount, List<RphTag> tags, boolean includeOk);
}
