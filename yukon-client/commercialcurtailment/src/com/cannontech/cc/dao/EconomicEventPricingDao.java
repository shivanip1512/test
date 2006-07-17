package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventPricing;
import com.cannontech.core.dao.support.StandardDaoOperations;

public interface EconomicEventPricingDao extends StandardDaoOperations<EconomicEventPricing> {
    public List<EconomicEventPricing> getPricingForEvent(EconomicEvent event);
    public EconomicEventPricing getPricingForEvent(EconomicEvent event, int offset);
}
