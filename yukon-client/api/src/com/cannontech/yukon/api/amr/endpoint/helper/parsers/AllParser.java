package com.cannontech.yukon.api.amr.endpoint.helper.parsers;

import org.joda.time.Instant;

import com.cannontech.common.util.Range;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.yukon.api.amr.endpoint.helper.PointValueSelector;

public class AllParser extends ValueParser {
    @Override
    public void parseOther(SimpleXPathTemplate template, PointValueSelector selector) {
        Range<Instant> dateRange = new Range<Instant>(null, false, new Instant(), true);
        selector.setInstantRange(dateRange);
        selector.setNumberOfRows(null);
        selector.setOrder(Order.FORWARD);
    }
}
