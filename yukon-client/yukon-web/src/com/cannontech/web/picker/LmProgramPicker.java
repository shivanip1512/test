package com.cannontech.web.picker;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.search.result.UltraLightPao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.picker.service.LmProgramForEnergyCompanyIdFilterFactory;

public class LmProgramPicker extends PaoPermissionCheckingPicker {
    @Autowired private LmProgramForEnergyCompanyIdFilterFactory energyCompanyFlterFactory;

	@Override
    protected void updateFilters(List<SqlFilter> sqlFilters,
            List<PostProcessingFilter<UltraLightPao>> postProcessingFilters,
            String extraArgs, YukonUserContext userContext) {
        sqlFilters.add(energyCompanyFlterFactory.getFilterForEnergyCompanyIdExtraArg(extraArgs));
        super.updateFilters(sqlFilters, postProcessingFilters, extraArgs, userContext);
    }
}
