package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.EconomicEventPricing;
import com.cannontech.cc.model.EconomicEventPricingWindow;

public interface EconomicEventPricingWindowDao extends StandardDaoOperations<EconomicEventPricingWindow> {

    List<EconomicEventPricingWindow> getForRevision(EconomicEventPricing revision);
}
