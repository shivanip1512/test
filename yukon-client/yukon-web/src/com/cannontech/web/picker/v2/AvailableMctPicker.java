package com.cannontech.web.picker.v2;

import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.search.UltraLightPao;
import com.cannontech.common.search.pao.db.AvailableMctFilter;
import com.cannontech.core.roleproperties.YukonEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.service.EnergyCompanyService;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class AvailableMctPicker extends DatabasePaoPicker {
    
    private EnergyCompanyService energyCompanyService;
    private PaoDefinitionDao paoDefinitionDao;

    @Override
    protected void updateFilters(List<SqlFilter> sqlFilters,
            List<PostProcessingFilter<UltraLightPao>> postProcessingFilters,
            String extraArgs, YukonUserContext userContext) {
        if (extraArgs != null) {
            
            int energyCompanyId = NumberUtils.toInt(extraArgs);
            
            // gather parents energyCompanyIds
            List<YukonEnergyCompany> parentEnergyCompanies = 
                energyCompanyService.getAccessibleParentEnergyCompanies(energyCompanyId);
            List<Integer> energyCompanyIds =
                Lists.transform(parentEnergyCompanies, LiteStarsEnergyCompany.getEnergyCompanyToEnergyCompanyIdsFunction());
            
            AvailableMctFilter energyCompanyIdsFilter = 
                new AvailableMctFilter(energyCompanyIds, paoDefinitionDao);
            sqlFilters.add(energyCompanyIdsFilter);
        }
    }

    // DI Setters
    @Autowired
    public void setEnergyCompanyService(EnergyCompanyService energyCompanyService) {
        this.energyCompanyService = energyCompanyService;
    }
    
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
}