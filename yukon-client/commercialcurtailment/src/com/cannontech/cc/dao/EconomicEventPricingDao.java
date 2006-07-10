package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventPricing;
import com.cannontech.database.cache.functions.StandardDaoOperations;

public interface EconomicEventPricingDao extends StandardDaoOperations<EconomicEventPricing> {
    public List<EconomicEventPricing> getPricingForEvent(EconomicEvent event);
    public EconomicEventPricing getPricingForEvent(EconomicEvent event, int offset);
}
