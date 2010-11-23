package com.cannontech.web.stars.dr.operator.inventoryOperations.service;

import java.util.List;
import java.util.Set;

import org.joda.time.DateTimeZone;

import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.web.stars.dr.operator.inventoryOperations.model.FilterMode;
import com.cannontech.web.stars.dr.operator.inventoryOperations.model.RuleModel;

public interface InventoryOperationsFilterService {

    Set<InventoryIdentifier> getInventory(FilterMode filterMode, List<RuleModel> testRules, DateTimeZone timeZone);

}