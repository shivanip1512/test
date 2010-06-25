package com.cannontech.dr.controlarea.service;

import java.util.Comparator;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.DatedObject;
import com.cannontech.dr.controlarea.model.ControlArea;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.user.YukonUserContext;

public interface ControlAreaService {

    
    public LMControlArea getControlAreaForPao(YukonPao from);
    
    public DatedObject<LMControlArea> getDatedControlArea(int controlAreaId);

    public DisplayablePao findControlAreaForProgram(YukonUserContext userContext, int programId);

    public SearchResult<DisplayablePao> filterControlAreas(UiFilter<DisplayablePao> filter,
                                                        Comparator<DisplayablePao> sorter, 
                                                        int startIndex, int count,
                                                        YukonUserContext userContext);

    public ControlArea getControlArea(int controlAreaId);
    
    public void setEnabled(int controlAreaId, boolean isEnabled);
    public void resetPeak(int controlAreaId);
    
    /**
     * Method to change trigger offset and threshold values for a given control area
     * @param controlAreaId - Control area to change triggers for
     * @param offset1 - Trigger1 offset value
     * @param threshold1 - Trigger1 threshold value
     * @param offset2 - Trigger2 offset value
     * @param threshold2 - Trigger2 threshold value
     */
    public void changeTriggers(int controlAreaId, Double offset1, Double threshold1, Double offset2, 
                               Double threshold2);
    /**
     * Method to set the time window for the control area
     * @param controlAreaId - Control Area to change
     * @param startSeconds - Seconds after midnight for start
     * @param stopSeconds - Seconds after midnight for stop
     */
    public void changeTimeWindow(int controlAreaId, Integer startSeconds, Integer stopSeconds);
    
    public boolean isEnabled(int controlAreaId);
}
