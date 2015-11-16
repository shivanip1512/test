package com.cannontech.web.picker.service;

import com.cannontech.common.bulk.filter.SqlFilter;

public interface LmProgramForEnergyCompanyIdFilterFactory {

	public SqlFilter getFilterForEnergyCompanyIdExtraArg(String energyCompanyIdExtraArg);
}
