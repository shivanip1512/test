package com.cannontech.web.picker.v2.service.impl;

import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.search.pao.db.LmProgramForEnergyCompanyIdFilter;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.web.picker.v2.service.LmProgramForEnergyCompanyIdFilterFactory;

public class LmProgramForEnergyCompanyIdFilterFactoryImpl implements LmProgramForEnergyCompanyIdFilterFactory {

    private ApplianceCategoryDao applianceCategoryDao;
    private StarsDatabaseCache starsDatabaseCache;
	
	@Override
	public SqlFilter getFilterForEnergyCompanyIdExtraArg(String energyCompanyIdExtraArg) {

		if (energyCompanyIdExtraArg == null) {
			throw new IllegalArgumentException("extraArgs for energyCompanyId required");
		}
		
		int energyCompanyId = NumberUtils.toInt(energyCompanyIdExtraArg);
		
		// gather parents energyCompanyIds
		YukonEnergyCompany yukonEnergyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
		Set<Integer> appCatEnergyCompanyIds = applianceCategoryDao.getAppCatEnergyCompanyIds(yukonEnergyCompany);
		
        // use the LmProgramForEnergyCompanyIdsFilter filter
        LmProgramForEnergyCompanyIdFilter filter = new LmProgramForEnergyCompanyIdFilter(appCatEnergyCompanyIds);
        
        return filter;
	}
	
	// DI Setter
	@Autowired
	public void setApplianceCategoryDao(ApplianceCategoryDao applianceCategoryDao) {
        this.applianceCategoryDao = applianceCategoryDao;
    }
	
	@Autowired
	public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
	
}
