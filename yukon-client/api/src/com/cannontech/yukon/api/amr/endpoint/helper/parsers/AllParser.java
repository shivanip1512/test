package com.cannontech.yukon.api.amr.endpoint.helper.parsers;

import org.joda.time.Instant;

import com.cannontech.common.util.Range;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.yukon.api.amr.endpoint.helper.PointValueSelector;

public class AllParser extends ValueParser {
    @Override
    public void parseOther(SimpleXPathTemplate template, PointValueSelector selector) {
        selector.setStopDate(new Instant());
        Range<Instant> dateRange = new Range<Instant>(selector.getStartDate(), false, selector.getStopDate(), true);
        selector.setInstantRange(dateRange);
        selector.setNumberOfRows(null);
        selector.setOrder(Order.FORWARD);
    }
}
