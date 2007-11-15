package com.cannontech.stars.dr.hardware.dao;

import java.util.Date;
import java.util.List;
import org.springframework.dao.DataAccessException;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;

public interface LMHardwareControlGroupDao {

    public void add(LMHardwareControlGroup hardwareControlGroup) throws Exception;
    
    public boolean remove(LMHardwareControlGroup hardwareControlGroup);
    
    public void update(LMHardwareControlGroup hardwareControlGroup) throws Exception;
    
    public LMHardwareControlGroup getById(int controlEntryId) throws DataAccessException;
    
    public List<LMHardwareControlGroup> getByLMGroupId(int groupId);
    
    public List<LMHardwareControlGroup> getByInventoryId(int inventoryId);
    
    public List<LMHardwareControlGroup> getByAccountId(int accountId);
    
    public List<LMHardwareControlGroup> getByEnrollmentStartDateRange(Date first, Date second);
    
    public List<LMHardwareControlGroup> getByEnrollmentStopDateRange(Date first, Date second);
    
    public List<LMHardwareControlGroup> getByOptOutStartDateRange(Date first, Date second);
    
    public List<LMHardwareControlGroup> getByOptOutStopDateRange(Date first, Date second);
    
    public List<LMHardwareControlGroup> getByInventoryIdAndGroupIdAndAccountId(int inventoryId, int lmGroupId, int accountId);
    
    public List<LMHardwareControlGroup> getByInventoryIdAndGroupIdAndAccountIdAndType(int inventoryId, int lmGroupId, int accountId, int type);
    
    public List<LMHardwareControlGroup> getAll();
    
}
