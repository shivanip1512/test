package com.cannontech.web.picker.v2.service.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.search.pao.db.LmProgramForEnergyCompanyIdFilter;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.web.picker.v2.service.LmProgramForEnergyCompanyIdFilterFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class LmProgramForEnergyCompanyIdFilterFactoryImpl implements LmProgramForEnergyCompanyIdFilterFactory {

	private RolePropertyDao rolePropertyDao;
	private StarsDatabaseCache starsDatabaseCache;
	
	@Override
	public List<SqlFilter> getFilterForEnergyCompanyIdExtraArg(String energyCompanyIdExtraArg) {

		if (energyCompanyIdExtraArg == null) {
			throw new IllegalArgumentException("extraArgs for energyCompanyId required");
		}
		
		int energyCompanyId = NumberUtils.toInt(energyCompanyIdExtraArg);
		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
		
		// gather parents energyCompanyIds
		Set<Integer> energyCompanyIds = Sets.newHashSet(energyCompanyId);
        if (rolePropertyDao.checkProperty(YukonRoleProperty.INHERIT_PARENT_APP_CATS, energyCompany.getUser())) {
            List<LiteStarsEnergyCompany> allAscendants = ECUtils.getAllAscendants(energyCompany);
            for (LiteStarsEnergyCompany ec : allAscendants) {
                energyCompanyIds.add(ec.getEnergyCompanyID());
            }
        }
		
        // use the LmProgramForEnergyCompanyIdsFilter filter
        List<SqlFilter> extraFilters = Lists.newArrayList();
        LmProgramForEnergyCompanyIdFilter energyCompanyIdsFilter = new LmProgramForEnergyCompanyIdFilter(energyCompanyIds);
        extraFilters.add(energyCompanyIdsFilter);
        
        return extraFilters;
	}
	
	@Autowired
	public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
	
	@Autowired
	public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
		this.starsDatabaseCache = starsDatabaseCache;
	}
}
