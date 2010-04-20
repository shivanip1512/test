package com.cannontech.web.picker.v2.service.impl;

import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.search.pao.db.LmProgramForEnergyCompanyIdFilter;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.web.picker.v2.service.LmProgramForEnergyCompanyIdFilterFactory;

public class LmProgramForEnergyCompanyIdFilterFactoryImpl implements LmProgramForEnergyCompanyIdFilterFactory {

	private ECMappingDao ecMappingDao;
	private StarsDatabaseCache starsDatabaseCache;
	
	@Override
	public SqlFilter getFilterForEnergyCompanyIdExtraArg(String energyCompanyIdExtraArg) {

		if (energyCompanyIdExtraArg == null) {
			throw new IllegalArgumentException("extraArgs for energyCompanyId required");
		}
		
		int energyCompanyId = NumberUtils.toInt(energyCompanyIdExtraArg);
		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
		
		// gather parents energyCompanyIds
		Set<Integer> energyCompanyIds = ecMappingDao.getInheritedEnergyCompanyIds(energyCompany);
		
        // use the LmProgramForEnergyCompanyIdsFilter filter
        LmProgramForEnergyCompanyIdFilter filter = new LmProgramForEnergyCompanyIdFilter(energyCompanyIds);
        
        return filter;
	}
	
	@Autowired
	public void setEcMappingDao(ECMappingDao ecMappingDao) {
		this.ecMappingDao = ecMappingDao;
	}
	
	@Autowired
	public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
		this.starsDatabaseCache = starsDatabaseCache;
	}
}
