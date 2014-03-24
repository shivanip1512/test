package com.cannontech.web.picker;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.search.pao.db.DrUntrackedMctFilter;
import com.cannontech.common.search.result.UltraLightPao;
import com.cannontech.user.YukonUserContext;

public class DrUntrackedMctPicker extends DatabasePaoPicker {
    
    @Autowired private PaoDefinitionDao paoDefinitionDao;

    @Override
    protected void updateFilters(List<SqlFilter> sqlFilters,
            List<PostProcessingFilter<UltraLightPao>> postProcessingFilters,
            String extraArgs, YukonUserContext userContext) {
        
            DrUntrackedMctFilter untrackedMcts = new DrUntrackedMctFilter(paoDefinitionDao);
            sqlFilters.add(untrackedMcts);
    }

}