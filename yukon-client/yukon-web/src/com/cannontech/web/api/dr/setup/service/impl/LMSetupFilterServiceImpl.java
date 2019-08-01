package com.cannontech.web.api.dr.setup.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.dr.setup.LMPaoDto;
import com.cannontech.common.dr.setup.LMSetupFilter;
import com.cannontech.common.dr.setup.LmSetupFilterType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.FilterCriteria;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.web.api.dr.setup.dao.impl.LMSetupDaoImpl;
import com.cannontech.web.api.dr.setup.service.LMSetupFilterService;

public class LMSetupFilterServiceImpl implements LMSetupFilterService {
    @Autowired private LMSetupDaoImpl dao;

    public SearchResults<LMPaoDto> filter(FilterCriteria<LMSetupFilter> filterCriteria) {
        SearchResults<LMPaoDto> filteredResults = null;
        if (filterCriteria.getFilteringParameters().getFilterByType() == LmSetupFilterType.PROGRAM_CONSTRAINT) {
            filteredResults = dao.getProgramConstraint(filterCriteria);
        } else {
            List<PaoType> paoTypes = getTypes(filterCriteria.getFilteringParameters());
            filteredResults = dao.getPaoDetails(filterCriteria, paoTypes);
        }
        return filteredResults;
    }

    /**
     * Build the list of PaoTypes based on Filter type option. 
     */
    private List<PaoType> getTypes(LMSetupFilter setupFilter) {
        List<PaoType> types = new ArrayList<>();
        switch (setupFilter.getFilterByType()) {
        case CONTROL_AREA:
            types.add(PaoType.LM_CONTROL_AREA);
            break;
        case CONTROL_SCENARIO:
            types.add(PaoType.LM_SCENARIO);
            break;
        case LOAD_GROUP:
            if (setupFilter.getTypes() != null && !setupFilter.getTypes().isEmpty()) {
                types.addAll(setupFilter.getTypes());
            } else {
                types.addAll(PaoType.getAllLMGroupTypes());
            }
            break;
        case LOAD_PROGRAM:
            if (setupFilter.getTypes() != null && !setupFilter.getTypes().isEmpty()) {
                types.addAll(setupFilter.getTypes());
            } else {
                types.addAll(PaoType.getDirectLMProgramTypes());
            }
            break;
        case MACRO_LOAD_GROUP:
            types.add(PaoType.MACRO_GROUP);
            break;
        }
        return types;
    }
}
