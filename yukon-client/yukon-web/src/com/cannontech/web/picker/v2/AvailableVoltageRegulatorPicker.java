package com.cannontech.web.picker.v2;

import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.search.UltraLightPao;
import com.cannontech.common.search.pao.db.AvailableRegulatorFilter;
import com.cannontech.common.search.pao.db.VoltageRegulatorFilter;
import com.cannontech.user.YukonUserContext;

public class AvailableVoltageRegulatorPicker extends DatabasePaoPicker {
    @Override
    protected void updateFilters(List<SqlFilter> sqlFilters,
            List<PostProcessingFilter<UltraLightPao>> postProcessingFilters,
            String extraArgs, YukonUserContext userContext) {
        
        VoltageRegulatorFilter regFilter = new VoltageRegulatorFilter();
        AvailableRegulatorFilter availableFilter = new AvailableRegulatorFilter();
        
        sqlFilters.add(regFilter);
        sqlFilters.add(availableFilter);
    }
}
