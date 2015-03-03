package com.cannontech.yukon.api.amr.endpoint.helper.parsers;

import org.joda.time.Instant;

import com.cannontech.common.util.Range;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.yukon.api.amr.endpoint.helper.PointValueSelector;
import com.cannontech.yukon.api.amr.endpoint.helper.PointValueSelector.OrderHelper;

public class AllBeforeParser extends ValueParser {
    @Override
    public void parseOther(SimpleXPathTemplate template, PointValueSelector selector) {
        Instant stopDate = template.evaluateAsInstant("@date", new Instant());
        Integer limit = parseLimit(template);

        Boolean inclusive = template.evaluateAsBoolean("@inclusive", true);
        String orderString = template.evaluateAsString("@order");
        Order order = OrderHelper.getOrderByName(orderString, Order.REVERSE);

        selector.setStopDate(stopDate);
        selector.setNumberOfRows(limit);
        Range<Instant> dateRange = new Range<Instant>(selector.getStartDate(), true, selector.getStopDate(), inclusive);
        selector.setInstantRange(dateRange);
        selector.setOrder(order);
    }
}
