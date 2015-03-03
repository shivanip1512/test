package com.cannontech.yukon.api.amr.endpoint.helper.parsers;

import org.joda.time.Instant;

import com.cannontech.common.util.Range;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.yukon.api.amr.endpoint.helper.PointValueSelector;

public class BeforeParser extends ValueParser {
    @Override
    public void parseOther(SimpleXPathTemplate template, PointValueSelector selector) {
        Instant stopDate = template.evaluateAsInstant("@date", new Instant());
        int valueCount = template.evaluateAsInt("@index", 1);

        Boolean inclusive = template.evaluateAsBoolean("@inclusive", true);
        Order order = Order.REVERSE;
        selector.setStopDate(stopDate);
        selector.setNumberOfRows(valueCount);
        Range<Instant> dateRange = new Range<Instant>(selector.getStartDate(), true, selector.getStopDate(), inclusive);
        selector.setInstantRange(dateRange);
        selector.setOrder(order);
    }
}
