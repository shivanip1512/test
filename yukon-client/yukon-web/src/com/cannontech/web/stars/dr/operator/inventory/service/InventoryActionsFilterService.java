package com.cannontech.web.stars.dr.operator.inventory.service;

import java.util.Set;

import org.joda.time.DateTimeZone;

import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.stars.dr.operator.inventory.model.FilterModel;

public interface InventoryActionsFilterService {

    /** Retrieves inventory in the energy company of the user , filtered by the set of rules. */
    Set<InventoryIdentifier> getInventory(FilterModel filter, DateTimeZone timeZone, LiteYukonUser user);
    
}