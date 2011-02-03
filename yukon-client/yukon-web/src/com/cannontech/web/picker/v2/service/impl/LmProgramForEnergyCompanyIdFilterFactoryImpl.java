package com.cannontech.web.picker.v2.service.impl;

import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.search.pao.db.LmProgramForEnergyCompanyIdFilter;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.service.EnergyCompanyService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.web.picker.v2.service.LmProgramForEnergyCompanyIdFilterFactory;
import com.google.common.collect.Lists;

public class LmProgramForEnergyCompanyIdFilterFactoryImpl implements LmProgramForEnergyCompanyIdFilterFactory {

	private EnergyCompanyService energyCompanyService;
	
	@Override
	public SqlFilter getFilterForEnergyCompanyIdExtraArg(String energyCompanyIdExtraArg) {

		if (energyCompanyIdExtraArg == null) {
			throw new IllegalArgumentException("extraArgs for energyCompanyId required");
		}
		
		int energyCompanyId = NumberUtils.toInt(energyCompanyIdExtraArg);
		
		// gather parents energyCompanyIds
		List<YukonEnergyCompany> parentEnergyCompanies = 
		    energyCompanyService.getAccessibleParentEnergyCompanies(energyCompanyId);
		List<Integer> energyCompanyIds = 
		    Lists.transform(parentEnergyCompanies, LiteStarsEnergyCompany.getEnergyCompanyToEnergyCompanyIdsFunction());
		
        // use the LmProgramForEnergyCompanyIdsFilter filter
        LmProgramForEnergyCompanyIdFilter filter = new LmProgramForEnergyCompanyIdFilter(energyCompanyIds);
        
        return filter;
	}
	
	@Autowired
	public void setEnergyCompanyService(EnergyCompanyService energyCompanyService) {
        this.energyCompanyService = energyCompanyService;
    }
	
}
