package com.cannontech.web.picker.v2.service;

import java.util.List;

import com.cannontech.common.bulk.filter.SqlFilter;

public interface LmProgramForEnergyCompanyIdFilterFactory {

	public List<SqlFilter> getFilterForEnergyCompanyIdExtraArg(String energyCompanyIdExtraArg);
}
