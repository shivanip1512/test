package com.cannontech.web.picker.v2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.search.UltraLightPao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.picker.v2.service.LmProgramForEnergyCompanyIdFilterFactory;

public class LmProgramPicker extends PaoPermissionCheckingPicker {
	private LmProgramForEnergyCompanyIdFilterFactory energyCompanyFlterFactory;

	@Override
    protected void updateFilters(List<SqlFilter> sqlFilters,
            List<PostProcessingFilter<UltraLightPao>> postProcessingFilters,
            String extraArgs, YukonUserContext userContext) {
        sqlFilters.add(energyCompanyFlterFactory.getFilterForEnergyCompanyIdExtraArg(extraArgs));
        super.updateFilters(sqlFilters, postProcessingFilters, extraArgs, userContext);
    }

	@Autowired
	public void setFilterFactory(LmProgramForEnergyCompanyIdFilterFactory energyCompanyFlterFactory) {
		this.energyCompanyFlterFactory = energyCompanyFlterFactory;
	}
}
