package com.cannontech.web.picker.v2.service;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.search.UltraLightPao;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface LmPaoPermissionCheckingPostProcessingFilterFactory {

	public PostProcessingFilter<UltraLightPao> getFilterForUser(LiteYukonUser user);
}
