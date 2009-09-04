package com.cannontech.dr.controlarea.service;

import java.util.Comparator;
import java.util.List;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.DatedObject;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.dr.controlarea.model.ControlArea;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.user.YukonUserContext;

public interface ControlAreaService
    extends ObjectMapper<DisplayablePao, LMControlArea> {

    public DatedObject<LMControlArea> getDatedControlArea(int controlAreaId);

    public ControlArea findControlAreaForProgram(YukonUserContext userContext, int programId);

    public SearchResult<ControlArea> filterControlAreas(
            YukonUserContext userContext,
            List<UiFilter<DisplayablePao>> filters,
            Comparator<DisplayablePao> sorter, int startIndex, int count);

    public ControlArea getControlArea(int controlAreaId);
}
