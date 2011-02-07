package com.cannontech.web.picker.v2;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.search.UltraLightPao;
import com.cannontech.common.search.pao.db.AvailableMctFilter;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.user.YukonUserContext;

public class AvailableMctPicker extends DatabasePaoPicker {
    
    private ECMappingDao ecMappingDao;
    private PaoDefinitionDao paoDefinitionDao;

    @Override
    protected void updateFilters(List<SqlFilter> sqlFilters,
            List<PostProcessingFilter<UltraLightPao>> postProcessingFilters,
            String extraArgs, YukonUserContext userContext) {
        if (extraArgs != null) {
            
            int energyCompanyId = NumberUtils.toInt(extraArgs);
            
            // gather parents energyCompanyIds
            Set<Integer> parentEnergyCompanies = ecMappingDao.getParentEnergyCompanyIds(energyCompanyId);
            
            AvailableMctFilter energyCompanyIdsFilter = 
                new AvailableMctFilter(parentEnergyCompanies, paoDefinitionDao);
            sqlFilters.add(energyCompanyIdsFilter);
        }
    }

    // DI Setters
    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }
    
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
}