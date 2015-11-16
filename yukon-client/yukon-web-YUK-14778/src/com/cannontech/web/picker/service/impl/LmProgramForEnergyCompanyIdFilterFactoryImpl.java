package com.cannontech.web.picker.service.impl;

import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.search.pao.db.LmProgramForEnergyCompanyIdFilter;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.web.picker.service.LmProgramForEnergyCompanyIdFilterFactory;

public class LmProgramForEnergyCompanyIdFilterFactoryImpl implements LmProgramForEnergyCompanyIdFilterFactory {

    @Autowired private ApplianceCategoryDao applianceCategoryDao;
    @Autowired private EnergyCompanyDao ecDao;
	
	@Override
	public SqlFilter getFilterForEnergyCompanyIdExtraArg(String energyCompanyIdExtraArg) {

		if (energyCompanyIdExtraArg == null) {
			throw new IllegalArgumentException("extraArgs for energyCompanyId required");
		}
		
		int energyCompanyId = NumberUtils.toInt(energyCompanyIdExtraArg);
		
		// gather parents energyCompanyIds
		EnergyCompany energyCompany = ecDao.getEnergyCompany(energyCompanyId);
		
		Set<Integer> appCatEnergyCompanyIds = applianceCategoryDao.getAppCatEnergyCompanyIds(energyCompany);
		
        // use the LmProgramForEnergyCompanyIdsFilter filter
        LmProgramForEnergyCompanyIdFilter filter = new LmProgramForEnergyCompanyIdFilter(appCatEnergyCompanyIds);
        
        return filter;
	}

}
