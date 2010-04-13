package com.cannontech.web.picker.v2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.search.UltraLightPao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.picker.v2.service.LmProgramForEnergyCompanyIdFilterFactory;

public class LmDirectProgramByEnergyCompanyIdPicker extends FilterPaoPicker {
	
	private LmProgramForEnergyCompanyIdFilterFactory filterFactory;

	@Override
    public SearchResult<UltraLightPao> search(String ss, int start, int count, String energyCompanyIdExtraArg, YukonUserContext userContext) {

		List<SqlFilter> extraFilters = filterFactory.getFilterForEnergyCompanyIdExtraArg(energyCompanyIdExtraArg);
        
        return super.search(ss, start, count, extraFilters, userContext);
    }
	
	@Autowired
	public void setFilterFactory(LmProgramForEnergyCompanyIdFilterFactory filterFactory) {
		this.filterFactory = filterFactory;
	}
}
