package com.cannontech.stars.dr.hardware.dao;

import java.util.Date;
import java.util.List;
import org.springframework.dao.DataAccessException;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;

public interface LMHardwareControlGroupDao {

    public boolean add(LMHardwareControlGroup hardwareControlGroup);
    
    public boolean remove(LMHardwareControlGroup hardwareControlGroup);
    
    public boolean update(LMHardwareControlGroup hardwareControlGroup);
    
    public LMHardwareControlGroup getById(int controlEntryId) throws DataAccessException;
    
    public List<LMHardwareControlGroup> getByLMGroupId(int groupId);
    
    public List<LMHardwareControlGroup> getByInventoryId(int inventoryId);
    
    public List<LMHardwareControlGroup> getByAccountId(int accountId);
    
    public List<LMHardwareControlGroup> getByEnrollmentStartDateRange(Date first, Date second);
    
    public List<LMHardwareControlGroup> getByEnrollmentStopDateRange(Date first, Date second);
    
    public List<LMHardwareControlGroup> getByOptOutStartDateRange(Date first, Date second);
    
    public List<LMHardwareControlGroup> getByOptOutStopDateRange(Date first, Date second);
    
    public LMHardwareControlGroup getByInventoryIdAndGroupIdAndAccountId(int inventoryId, int lmGroupId, int accountId);
    
    public List<LMHardwareControlGroup> getAll();
    
}
