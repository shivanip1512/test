package com.cannontech.yukon.api.amr.endpoint.helper.parsers;

import org.joda.time.Instant;

import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.yukon.api.amr.endpoint.helper.PointValueSelector;
import com.cannontech.yukon.api.amr.endpoint.helper.PointValueSelector.OrderHelper;

public class AllBeforeParser extends ValueParser {
    @Override
    public void parseOther(SimpleXPathTemplate template, PointValueSelector selector) {
        Instant stopDate = template.evaluateAsInstant("@date", new Instant());
        Integer limit = parseLimit(template);

        Boolean inclusive = template.evaluateAsBoolean("@inclusive", true);
        Clusivity clusivity = Clusivity.getClusivity(true, inclusive);

        String orderString = template.evaluateAsString("@order");
        Order order = OrderHelper.getOrderByName(orderString, Order.REVERSE);

        selector.setStopDate(stopDate);
        selector.setNumberOfRows(limit);
        selector.setClusivity(clusivity);
        selector.setOrder(order);
    }
}
