package com.cannontech.web.stars.dr.operator.service;

import java.util.List;

import com.cannontech.web.stars.dr.operator.model.DisplayableApplianceListEntry;

public interface DisplayableApplianceService {

    /**
     * This method gets a list of all the appliances for a given account.
     */
    public List<DisplayableApplianceListEntry> getDisplayableApplianceListEntries(int accountId);

}