package com.cannontech.web.picker;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.search.pao.db.AvailableMctFilter;
import com.cannontech.common.search.result.UltraLightPao;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class AvailableMctPicker extends DatabasePaoPicker {
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private EnergyCompanyDao ecDao;

    @Override
    protected void updateFilters(List<SqlFilter> sqlFilters,
            List<PostProcessingFilter<UltraLightPao>> postProcessingFilters, String extraArgs,
            YukonUserContext userContext) {
        if (extraArgs != null) {
            int energyCompanyId = NumberUtils.toInt(extraArgs);
            
            // gather parents energyCompanyIds
            EnergyCompany energyCompany = ecDao.getEnergyCompany(energyCompanyId);
            Set<Integer> parentIds = new HashSet<>(Lists.transform(energyCompany.getAncestors(true),
                                                                   EnergyCompanyDao.TO_ID_FUNCTION));
            
            AvailableMctFilter energyCompanyIdsFilter = new AvailableMctFilter(parentIds, paoDefinitionDao);
            sqlFilters.add(energyCompanyIdsFilter);
        }
    }
}
