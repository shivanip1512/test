package com.cannontech.web.api.dr.setup.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.LMSetupFilter;
import com.cannontech.common.dr.setup.LmSetupFilterType;
import com.cannontech.common.search.FilterCriteria;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.dr.controlarea.dao.ControlAreaDao;
import com.cannontech.dr.controlarea.model.ControlAreaTrigger;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.dr.setup.dao.impl.LMControlAreaSetupDaoImpl;
import com.cannontech.web.api.dr.setup.model.ControlAreaFilteredResult;
import com.cannontech.web.api.dr.setup.service.LMSetupFilterService;

public class LMControlAreaSetupFilterServiceImpl implements LMSetupFilterService <ControlAreaFilteredResult> {

    @Autowired private LMControlAreaSetupDaoImpl setupDao;
    @Autowired private ControlAreaDao controlAreaDao;

    @Override
    public SearchResults<ControlAreaFilteredResult> filter(FilterCriteria<LMSetupFilter> filterCriteria, YukonUserContext userContext) {
        List<LMDto> controlAreas = setupDao.getDetails(filterCriteria);
        List<ControlAreaFilteredResult> filteredResultList = new ArrayList<>();
        for (LMDto lmDto : controlAreas) {
            ControlAreaFilteredResult controlAreaFilteredResult = new ControlAreaFilteredResult();

            controlAreaFilteredResult.setControlAreaId(lmDto.getId());
            controlAreaFilteredResult.setControlAreaName(lmDto.getName());
            List<LMDto> assignedPrograms = setupDao.getProgramsForControlArea(lmDto.getId());
            controlAreaFilteredResult.setAssignedPrograms(assignedPrograms);
            Map<Integer, List<ControlAreaTrigger>> controlAreaTriggers = controlAreaDao.getControlAreaTriggers(lmDto.getId());
            if (MapUtils.isNotEmpty(controlAreaTriggers)) {
                controlAreaTriggers.entrySet().stream()
                                              .forEach(entry -> controlAreaFilteredResult.setTriggers(entry.getValue()));
            }
            filteredResultList.add(controlAreaFilteredResult);

        }
        int totalHitCount = setupDao.getTotalCount(filterCriteria);
        SearchResults<ControlAreaFilteredResult> searchResults = SearchResults.pageBasedForSublist(filteredResultList,
                                                                                        filterCriteria.getPagingParameters(),
                                                                                        totalHitCount);
        return searchResults;
    }

    @Override
    public LmSetupFilterType getFilterType() {
        return LmSetupFilterType.CONTROL_AREA;
    }
}