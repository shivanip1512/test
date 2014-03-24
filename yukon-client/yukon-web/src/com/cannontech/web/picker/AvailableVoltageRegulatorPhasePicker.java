package com.cannontech.web.picker;

import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.search.pao.db.AvailableRegulatorFilter;
import com.cannontech.common.search.pao.db.VoltageRegulatorPhaseFilter;
import com.cannontech.common.search.result.UltraLightPao;
import com.cannontech.user.YukonUserContext;

public class AvailableVoltageRegulatorPhasePicker extends DatabasePaoPicker {
    @Override
    protected void updateFilters(List<SqlFilter> sqlFilters,
            List<PostProcessingFilter<UltraLightPao>> postProcessingFilters,
            String extraArgs, YukonUserContext userContext) {
        
        VoltageRegulatorPhaseFilter regFilter = new VoltageRegulatorPhaseFilter();
        Integer zoneId = extraArgs == null ? null : Integer.parseInt(extraArgs);
        AvailableRegulatorFilter availableFilter = new AvailableRegulatorFilter(zoneId);
        
        sqlFilters.add(regFilter);
        sqlFilters.add(availableFilter);
    }
}
