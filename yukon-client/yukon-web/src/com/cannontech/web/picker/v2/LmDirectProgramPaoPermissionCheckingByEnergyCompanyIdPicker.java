package com.cannontech.web.picker.v2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.search.UltraLightPao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.picker.v2.service.LmProgramForEnergyCompanyIdFilterFactory;
import com.google.common.collect.Lists;

public class LmDirectProgramPaoPermissionCheckingByEnergyCompanyIdPicker extends LmPaoPermissionCheckingBasePicker {
	
	private LmProgramForEnergyCompanyIdFilterFactory energyCompanyFlterFactory;

	@Override
    public SearchResult<UltraLightPao> search(String ss, int start, int count, String energyCompanyIdExtraArg, YukonUserContext userContext) {

		List<SqlFilter> sqlFilters = Lists.newArrayList(energyCompanyFlterFactory.getFilterForEnergyCompanyIdExtraArg(energyCompanyIdExtraArg));
        
        return super.search(ss, start, count, sqlFilters, null, userContext);
    }
	
	@Autowired
	public void setFilterFactory(LmProgramForEnergyCompanyIdFilterFactory energyCompanyFlterFactory) {
		this.energyCompanyFlterFactory = energyCompanyFlterFactory;
	}
}
