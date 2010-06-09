package com.cannontech.stars.dr.hardware.dao;

import java.util.Date;
import java.util.List;

import org.joda.time.ReadableInstant;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.hardware.model.HardwareConfigAction;
import com.cannontech.stars.dr.hardware.model.LMHardwareConfiguration;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;

public interface LMHardwareControlGroupDao {

    public void add(LMHardwareControlGroup hardwareControlGroup);
    
    public boolean remove(LMHardwareControlGroup hardwareControlGroup);
    
    public void update(LMHardwareControlGroup hardwareControlGroup);
    
    public void unenrollHardware(int inventoryId);
    
    public void stopOptOut(int inventoryId, int accountId, LiteYukonUser currentUser, 
                             ReadableInstant stopDate);    
    
    public void resetEntriesForProgram(int programId, LiteYukonUser user);    
    
    public LMHardwareControlGroup getById(int controlEntryId);
    
    public List<LMHardwareControlGroup> getByLMGroupId(int groupId);
    
    public List<LMHardwareControlGroup> getByInventoryId(int inventoryId);
    
    public List<LMHardwareControlGroup> getByAccountId(int accountId);
    
    public List<Integer> getDistinctGroupIdsByAccountId(final int accountId);
    
    public List<LMHardwareControlGroup> getByLMGroupIdAndAccountIdAndType(int lmGroupId, int accountId, int type);
    
    public List<LMHardwareControlGroup> getByEnrollmentStartDateRange(Date first, Date second);
    
    public List<LMHardwareControlGroup> getByEnrollmentStopDateRange(Date first, Date second);
    
    public List<LMHardwareControlGroup> getByOptOutStartDateRange(Date first, Date second);
    
    public List<LMHardwareControlGroup> getByOptOutStopDateRange(Date first, Date second);
    
    public List<LMHardwareControlGroup> getCurrentOptOutByProgramIdAndAccountId(int programId, int accountId);
    
    public List<LMHardwareControlGroup> getCurrentOptOutByInventoryIdProgramIdAndAccountId(int inventoryId, int programId, int accountId);
    
    public List<LMHardwareControlGroup> getCurrentEnrollmentByAccountId(int accountId);
    
    public List<LMHardwareControlGroup> getCurrentEnrollmentByInventoryIdAndAccountId(int inventoryId, int accountId);
    
    public List<LMHardwareControlGroup> getCurrentEnrollmentByProgramIdAndAccountId(int programId, int accountId);
    
    public List<LMHardwareControlGroup> getCurrentEnrollmentByInventoryIdAndProgramIdAndAccountId(int inventoryId, int programId, int accountId);    
    
    public List<LMHardwareControlGroup> getByInventoryIdAndAccountIdAndType(int inventoryId, int accountId, int type);

    public List<LMHardwareControlGroup> getByInventoryIdAndGroupIdAndAccountId(int inventoryId, int lmGroupId, int accountId);
    
    public List<LMHardwareControlGroup> getByInventoryIdAndGroupIdAndAccountIdAndType(int inventoryId, int lmGroupId, int accountId, int type);
    
    public List<LMHardwareControlGroup> getAll();
    
    public List<LMHardwareControlGroup> getAllByEnergyCompanyId(int energyCompanyId);
    
    public List<LMHardwareConfiguration> getOldConfigDataByInventoryId(int inventoryId);
    
    public List<LMHardwareConfiguration> getOldConfigDataByInventoryIdAndGroupId(int inventoryId, int lmGroupId);

    public List<HardwareConfigAction> getHardwareConfigActions(int accountId);
}
