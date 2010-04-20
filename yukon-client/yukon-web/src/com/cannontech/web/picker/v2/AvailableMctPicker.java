package com.cannontech.web.picker.v2;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.search.UltraLightPao;
import com.cannontech.common.search.pao.db.AvailableMctFilter;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class AvailableMctPicker extends FilterPaoPicker {
    
    private StarsDatabaseCache starsDatabaseCache;
    private ECMappingDao ecMappingDao;

    @Override
    public SearchResult<UltraLightPao> search(String ss, int start, int count, String energyCompanyIdExtraArg, YukonUserContext userContext) {
        List<SqlFilter> extraFilters = Lists.newArrayList();
        
        if (energyCompanyIdExtraArg != null) {
        
            int energyCompanyId = NumberUtils.toInt(energyCompanyIdExtraArg);
            LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
            
            // gather parents energyCompanyIds
            Set<Integer> energyCompanyIds = ecMappingDao.getInheritedEnergyCompanyIds(energyCompany);
            
            extraFilters = Lists.newArrayList();
            AvailableMctFilter energyCompanyIdsFilter = new AvailableMctFilter(energyCompanyIds);
            extraFilters.add(energyCompanyIdsFilter);
            
        }
        
        return super.search(ss, start, count, extraFilters, null, userContext);
    }
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
		this.ecMappingDao = ecMappingDao;
	}
}