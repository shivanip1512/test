package com.cannontech.web.picker.v2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.search.UltraLightPao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.picker.v2.service.LmPaoPermissionCheckingPostProcessingFilterFactory;
import com.google.common.collect.Lists;

public class LmScenarioPaoPermissionCheckingPicker extends FilterPaoPicker {
	
	private LmPaoPermissionCheckingPostProcessingFilterFactory paoPermissionFilterFactory;

	@Override
    public SearchResult<UltraLightPao> search(String ss, int start, int count, String energyCompanyIdExtraArg, YukonUserContext userContext) {
		
		List<PostProcessingFilter<UltraLightPao>> postProcessingFilters = Lists.newArrayList();
		PostProcessingFilter<UltraLightPao> filter = paoPermissionFilterFactory.getFilterForUser(userContext.getYukonUser());
		postProcessingFilters.add(filter);
		
		return super.search(ss, start, count, null, postProcessingFilters, userContext);
	}
	
	@Autowired
	public void setPaoPermissionFactory(LmPaoPermissionCheckingPostProcessingFilterFactory paoPermissionFilterFactory) {
		this.paoPermissionFilterFactory = paoPermissionFilterFactory;
	}
}
