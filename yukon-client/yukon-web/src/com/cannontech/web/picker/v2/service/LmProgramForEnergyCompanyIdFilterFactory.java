package com.cannontech.web.picker.v2.service;

import com.cannontech.common.bulk.filter.SqlFilter;

public interface LmProgramForEnergyCompanyIdFilterFactory {

	public SqlFilter getFilterForEnergyCompanyIdExtraArg(String energyCompanyIdExtraArg);
}
