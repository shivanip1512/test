package com.cannontech.web.picker.v2;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.search.UltraLightPao;
import com.cannontech.common.search.pao.db.AvailableMctFilter;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class AvailableMctPicker extends FilterPaoPicker {
    
    private StarsDatabaseCache starsDatabaseCache;
    private RolePropertyDao rolePropertyDao;

    @Override
    public SearchResult<UltraLightPao> search(String ss, int start, int count, String energyCompanyIdExtraArg, YukonUserContext userContext) {
        List<SqlFilter> extraFilters = Lists.newArrayList();
        
        if (energyCompanyIdExtraArg != null) {
        
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
            
            extraFilters = Lists.newArrayList();
            AvailableMctFilter energyCompanyIdsFilter = new AvailableMctFilter(energyCompanyIds);
            extraFilters.add(energyCompanyIdsFilter);
            
        }
        
        return super.search(ss, start, count, extraFilters, null, userContext);
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