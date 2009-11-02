package com.cannontech.services.validation.dao;

import java.util.List;

import com.cannontech.services.validation.model.ReviewPoint;
import com.cannontech.services.validation.model.RphTag;

public interface RphTagUiDao {

	public List<ReviewPoint> getReviewPoints(int afterPaoId, int pageCount, List<RphTag> tags, boolean includeOk);
}
