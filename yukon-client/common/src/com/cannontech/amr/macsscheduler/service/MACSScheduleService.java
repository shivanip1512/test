package com.cannontech.amr.macsscheduler.service;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.user.YukonUserContext;

public interface MACSScheduleService<E> {
    
    public void start(E schedule, Date startDate, Date stopDate) throws IOException;
    
    public void stop(E schedule, Date stopDate) throws IOException;
    
    public void enable(E schedule) throws IOException;
    
    public void disable(E schedule) throws IOException;
    
    public E getById(int scheduleId) throws NotFoundException,IOException;
    
    public List<E> getAll() throws IOException;
 
    public SearchResults<DisplayablePao> filterScripts(UiFilter<DisplayablePao> filter,
                                                       Comparator<DisplayablePao> sorter, 
                                                       int startIndex, int count,
                                                       YukonUserContext userContext); 
}
