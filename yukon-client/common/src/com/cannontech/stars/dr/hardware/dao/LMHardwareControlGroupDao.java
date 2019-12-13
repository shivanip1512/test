package com.cannontech.stars.dr.hardware.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

import com.cannontech.common.util.OpenInterval;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.hardware.dao.impl.LMHardwareControlGroupDaoImpl.DistinctEnrollment;
import com.cannontech.stars.dr.hardware.model.HardwareConfigAction;
import com.cannontech.stars.dr.hardware.model.LMHardwareConfiguration;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.google.common.collect.Multimap;

public interface LMHardwareControlGroupDao {

    public void add(LMHardwareControlGroup hardwareControlGroup);
    
    public boolean remove(LMHardwareControlGroup hardwareControlGroup);
    
    public void update(LMHardwareControlGroup hardwareControlGroup);
    
    public void unenrollHardware(int inventoryId, Instant groupEnrollStop);
    
    /** 
     * This method stops any opt out that matches the inventoryId and accountId supplied.
     */
    public void stopOptOut(int inventoryId, LiteYukonUser currentUser, ReadableInstant stopDate);

    /** 
     * This method stops any opt out that matches the inventoryId, accountId, 
     * and assignedProgramId supplied.
     */
    public void stopOptOut(int inventoryId, int assignedProgramId, LiteYukonUser currentUser, ReadableInstant stopDate);    
    
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
    
    public Multimap<Integer, LMHardwareControlGroup> getCurrentEnrollmentByInventoryIds(Iterable<Integer> inventoryIds);

    public List<LMHardwareControlGroup> getCurrentEnrollmentByInventoryIdAndAccountId(int inventoryId, int accountId);
    
    public List<LMHardwareControlGroup> getCurrentEnrollmentByProgramIdAndAccountId(int programId, int accountId);
    
    public LMHardwareControlGroup findCurrentEnrollmentByInventoryIdAndProgramIdAndAccountId(int inventoryId, int programId, int accountId);    
    
    public List<LMHardwareControlGroup> getByInventoryIdAndAccountIdAndType(int inventoryId, int accountId, int type);

    public List<LMHardwareControlGroup> getByInventoryIdAndGroupIdAndAccountId(int inventoryId, int lmGroupId, int accountId);
    
    public List<LMHardwareControlGroup> getByInventoryIdAndGroupIdAndAccountIdAndType(int inventoryId, int lmGroupId, int accountId, int type);

    public List<LMHardwareControlGroup> getByInventoryIdAndGroupIdAndType(int inventoryId, int lmGroupId, int type);

    public List<LMHardwareControlGroup> getAll();

    public List<LMHardwareConfiguration> getOldConfigDataByInventoryId(int inventoryId);
    
    public List<LMHardwareConfiguration> getOldConfigDataByInventoryIdAndGroupId(int inventoryId, int lmGroupId);

    public List<HardwareConfigAction> getHardwareConfigActions(int accountId);

    /**
     * Returns a List<LMHardwareControlGroup> of past enrollments of an account. 
     * @param accountId Account to get previously enrolled control groups for.
     * @return List<LMHardwareControlGroup> of past enrollments.
     */
    public List<LMHardwareControlGroup> getForPastEnrollments(int accountId);

    /**
     * Returns a List<LMHardwareControlGroup> of all active enrollments of an account, inventory, and load group. 
     */
    public List<LMHardwareControlGroup> getForActiveEnrollments(int accountId, int inventoryId, int lmGroupId);

    /**
     * Returns a List<LMHardwareControlGroup> of past enrollments of an account, inventory, and load group. 
     */
    public List<LMHardwareControlGroup> getForPastEnrollments(int accountId, int inventoryId, int lmGroupId);

    
    /**
     * Retrieves a list of program id's that the account was previously enrolled in.
     * @param accountId Account id to check for past enrollments.
     * @return The list of program id's.
     */
    public List<Integer> getPastEnrollmentProgramIds(int accountId);

    /**
     * Retrieves enrollement data for all dintinct enrollments an account has had.
     * IE: If an account has had a hardware that has been enrolled,  unenrolled and re-enrolled
     * in the same program, this will only return one object, not two.
     * @param accountId
     * @param past if false only current enrollments will be returned.
     * @return
     */
    public List<DistinctEnrollment> getDistinctEnrollments(int accountId, boolean past);

    /**
     * This method retrieves all enrollments that intersect with the supplied interval.  This
     * also includes active enrollments that would intersect with the intersection.
     */
    public List<LMHardwareControlGroup> getIntersectingEnrollments(Collection<Integer> energyCompanyIds, List<Integer> loadGroupIds, OpenInterval enrollmentInterval);

    /**
     * This method retrieves all enrollments that intersect with the supplied interval.  This
     * also includes active enrollments that would intersect with the intersection.
     */
    public List<LMHardwareControlGroup> getIntersectingEnrollments(int accountId, int inventoryId, int loadGroupId, OpenInterval enrollmentInterval);

    /**
     * This method retrieves all opt outs that intersect with the supplied interval.  This
     * also includes active opt outs that would intersect with the intersection.
     */
    public List<LMHardwareControlGroup> getIntersectingOptOuts(int accountId, int inventoryId, int loadGroupId, OpenInterval optOutInterval);

    /**
     * This method retrieves a current enrollment for a given InventoryId AccountId and Relay
     */
    LMHardwareControlGroup findCurrentEnrollmentByInventoryIdAndRelayAndAccountId(int inventoryId, int relay,
            int accountId);

}