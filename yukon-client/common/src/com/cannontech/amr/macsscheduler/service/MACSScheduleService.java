package com.cannontech.amr.macsscheduler.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.cannontech.core.dao.NotFoundException;

public interface MACSScheduleService<E> {
    
    public void start(E schedule, Date startDate, Date stopDate) throws IOException;
    
    public void stop(E schedule, Date stopDate) throws IOException;
    
    public void enable(E schedule) throws IOException;
    
    public void disable(E schedule) throws IOException;
    
    public E getById(int scheduleId) throws NotFoundException,IOException;
    
    public List<E> getAll() throws IOException;
 
}
