package com.cannontech.web.picker;

import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.search.pao.db.UnassignedProgramFilter;
import com.cannontech.common.search.result.UltraLightPao;
import com.cannontech.user.YukonUserContext;

public class UnassignedProgramPicker extends DatabasePaoPicker {
    @Override
    protected void updateFilters(List<SqlFilter> sqlFilters,
            List<PostProcessingFilter<UltraLightPao>> postProcessingFilters,
            String extraArgs, YukonUserContext userContext) {
        UnassignedProgramFilter programFilter = new UnassignedProgramFilter();
        if (extraArgs != null) {
            programFilter.setControlAreaId(Integer.parseInt(extraArgs));
        }
        sqlFilters.add(programFilter);
    }
}
