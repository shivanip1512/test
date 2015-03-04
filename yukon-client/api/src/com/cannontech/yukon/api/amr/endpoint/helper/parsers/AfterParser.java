package com.cannontech.yukon.api.amr.endpoint.helper.parsers;

import org.joda.time.Instant;

import com.cannontech.common.util.Range;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.yukon.api.amr.endpoint.helper.PointValueSelector;

public class AfterParser extends ValueParser {
    @Override
    public void parseOther(SimpleXPathTemplate template, PointValueSelector selector) {
        Instant startDate = template.evaluateAsInstant("@date");
        int valueCount = template.evaluateAsInt("@index", 1);

        Boolean inclusive = template.evaluateAsBoolean("@inclusive");     

        Order order = Order.FORWARD;

        selector.setNumberOfRows(valueCount);
        Range<Instant> dateRange = new Range<Instant>(startDate, inclusive, new Instant(), true);
        selector.setInstantRange(dateRange);
        selector.setOrder(order);
    }
}
