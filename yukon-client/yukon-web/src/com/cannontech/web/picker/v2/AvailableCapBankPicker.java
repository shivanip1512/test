package com.cannontech.web.picker.v2;

import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.search.UltraLightPao;
import com.cannontech.common.search.pao.db.AvailableCapBankBySubBusFilter;
import com.cannontech.common.search.pao.db.AvailableCapBankForZoneFilter;
import com.cannontech.user.YukonUserContext;

public class AvailableCapBankPicker extends DatabasePaoPicker {
    @Override
    protected void updateFilters(List<SqlFilter> sqlFilters,
            List<PostProcessingFilter<UltraLightPao>> postProcessingFilters,
            String extraArgs, YukonUserContext userContext) {
        AvailableCapBankBySubBusFilter busFilter = new AvailableCapBankBySubBusFilter();
        busFilter.setSubBusId(Integer.parseInt(extraArgs));
        AvailableCapBankForZoneFilter zoneFilter = new AvailableCapBankForZoneFilter();
        
        sqlFilters.add(busFilter);
        sqlFilters.add(zoneFilter);
    }
}
