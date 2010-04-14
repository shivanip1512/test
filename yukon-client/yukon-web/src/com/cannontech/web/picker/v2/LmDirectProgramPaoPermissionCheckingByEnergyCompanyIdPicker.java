package com.cannontech.web.picker.v2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.search.UltraLightPao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.picker.v2.service.LmPaoPermissionCheckingPostProcessingFilterFactory;
import com.cannontech.web.picker.v2.service.LmProgramForEnergyCompanyIdFilterFactory;
import com.google.common.collect.Lists;

public class LmDirectProgramPaoPermissionCheckingByEnergyCompanyIdPicker extends FilterPaoPicker {
	
	private LmProgramForEnergyCompanyIdFilterFactory energyCompanyFlterFactory;
	private LmPaoPermissionCheckingPostProcessingFilterFactory paoPermissionFilterFactory;

	@Override
    public SearchResult<UltraLightPao> search(String ss, int start, int count, String energyCompanyIdExtraArg, YukonUserContext userContext) {

		// sql filter
		List<SqlFilter> sqlFilters = Lists.newArrayList(energyCompanyFlterFactory.getFilterForEnergyCompanyIdExtraArg(energyCompanyIdExtraArg));
		
		// post processing filter
		List<PostProcessingFilter<UltraLightPao>> postProcessingFilters = Lists.newArrayList();
		PostProcessingFilter<UltraLightPao> postProcessingFilter = paoPermissionFilterFactory.getFilterForUser(userContext.getYukonUser());
		postProcessingFilters.add(postProcessingFilter);
        
        return super.search(ss, start, count, sqlFilters, postProcessingFilters, userContext);
    }
	
	@Autowired
	public void setFilterFactory(LmProgramForEnergyCompanyIdFilterFactory energyCompanyFlterFactory) {
		this.energyCompanyFlterFactory = energyCompanyFlterFactory;
	}
	
	@Autowired
	public void setPaoPermissionFactory(LmPaoPermissionCheckingPostProcessingFilterFactory paoPermissionFilterFactory) {
		this.paoPermissionFilterFactory = paoPermissionFilterFactory;
	}
}
