package com.cannontech.stars.dr.displayable.dao;

import java.util.List;

import com.cannontech.stars.dr.displayable.model.DisplayableInventoryEnrollment;

public interface DisplayableInventoryEnrollmentDao {
    /**
     * Get a list of all programs assigned to the given inventory on the given
     * account.
     */
    public List<DisplayableInventoryEnrollment> find(int accountId,
            int inventoryId);

    /**
     * Get program enrollment information for a specific piece of inventory on
     * an account. If the customer is not enrolled in the specified program
     * using the specified hardware, null is returned.
     */
    public DisplayableInventoryEnrollment find(int accountId, int inventoryId,
            int assignedProgramId);
}
