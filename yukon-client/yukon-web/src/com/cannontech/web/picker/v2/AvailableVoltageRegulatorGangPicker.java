package com.cannontech.web.picker.v2;

import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.search.UltraLightPao;
import com.cannontech.common.search.pao.db.AvailableRegulatorFilter;
import com.cannontech.common.search.pao.db.VoltageRegulatorGangFilter;
import com.cannontech.user.YukonUserContext;

public class AvailableVoltageRegulatorGangPicker extends DatabasePaoPicker {
    @Override
    protected void updateFilters(List<SqlFilter> sqlFilters,
            List<PostProcessingFilter<UltraLightPao>> postProcessingFilters,
            String extraArgs, YukonUserContext userContext) {
        
        VoltageRegulatorGangFilter regFilter = new VoltageRegulatorGangFilter();
        Integer zoneId = extraArgs == null ? null : Integer.parseInt(extraArgs);
        AvailableRegulatorFilter availableFilter = new AvailableRegulatorFilter(zoneId);
        
        sqlFilters.add(regFilter);
        sqlFilters.add(availableFilter);
    }
}
