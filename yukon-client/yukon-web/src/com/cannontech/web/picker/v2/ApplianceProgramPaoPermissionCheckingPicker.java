package com.cannontech.web.picker.v2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.search.UltraLightPao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.picker.v2.service.LmPaoPermissionCheckingPostProcessingFilterFactory;
import com.google.common.collect.Lists;

public class ApplianceProgramPaoPermissionCheckingPicker extends FilterPaoPicker {

	private LmPaoPermissionCheckingPostProcessingFilterFactory paoPermissionFilterFactory;

	@Override
    public SearchResult<UltraLightPao> search(String ss, int start, int count, String energyCompanyIdExtraArg, YukonUserContext userContext) {

		List<PostProcessingFilter<UltraLightPao>> extraPostProcessingFilters = Lists.newArrayList();
		PostProcessingFilter<UltraLightPao> extraPostProcessingFilter = paoPermissionFilterFactory.getFilterForUser(userContext.getYukonUser());
		extraPostProcessingFilters.add(extraPostProcessingFilter);
        
        return super.search(ss, start, count, null, extraPostProcessingFilters, userContext);
    }
	
	@Autowired
	public void setPaoPermissionFactory(LmPaoPermissionCheckingPostProcessingFilterFactory paoPermissionFilterFactory) {
		this.paoPermissionFilterFactory = paoPermissionFilterFactory;
	}
}
