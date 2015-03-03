package com.cannontech.web.stars.dr.operator.inventory.service;

import com.cannontech.web.stars.dr.operator.inventory.model.AuditSettings;
import com.cannontech.web.stars.dr.operator.inventory.model.ControlAuditResult;

public interface ControlAuditService {

    /**
     * For each inventory in the inventory collection, record whether it had any shed time
     * over the interval specified.
     */
    public ControlAuditResult runAudit(AuditSettings settings);

}