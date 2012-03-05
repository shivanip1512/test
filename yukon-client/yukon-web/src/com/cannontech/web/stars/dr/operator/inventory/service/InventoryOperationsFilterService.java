package com.cannontech.web.stars.dr.operator.inventory.service;

import java.text.ParseException;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTimeZone;

import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventory.model.FilterMode;
import com.cannontech.web.stars.dr.operator.inventory.model.RuleModel;

public interface InventoryOperationsFilterService {

    /**
     * Retrieves inventory in the energy company of the user in userContext, filtered by the set of rules.
     * @throws ParseException
     */
    public Set<InventoryIdentifier> getInventory(FilterMode filterMode, List<RuleModel> rules, DateTimeZone timeZone, YukonUserContext userContext) throws ParseException;

}