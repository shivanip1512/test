package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.EconomicEventPricing;
import com.cannontech.cc.model.EconomicEventPricingWindow;
import com.cannontech.database.cache.functions.StandardDaoOperations;

public interface EconomicEventPricingWindowDao extends StandardDaoOperations<EconomicEventPricingWindow> {

    List<EconomicEventPricingWindow> getForRevision(EconomicEventPricing revision);
}
