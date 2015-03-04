package com.cannontech.yukon.api.amr.endpoint.helper.parsers;

import org.joda.time.Instant;

import com.cannontech.common.util.Range;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.yukon.api.amr.endpoint.helper.PointValueSelector;

public class LatestParser extends ValueParser {
    @Override
    public void parseOther(SimpleXPathTemplate template, PointValueSelector selector) {
        selector.setNumberOfRows(1);
        Range<Instant> dateRange = new Range<Instant>(null, false, new Instant(), true);
        selector.setInstantRange(dateRange);
        selector.setOrder(Order.REVERSE);
    }
}
