package com.cannontech.yukon.api.amr.endpoint.helper.parsers;

import org.joda.time.Instant;

import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.yukon.api.amr.endpoint.helper.PointValueSelector;

public class AfterParser extends ValueParser {
    @Override
    public void parseOther(SimpleXPathTemplate template, PointValueSelector selector) {
        Instant startDate = template.evaluateAsInstant("@date");
        int valueCount = template.evaluateAsInt("@index", 1);

        Boolean inclusive = template.evaluateAsBoolean("@inclusive");
        Clusivity clusivity = Clusivity.getClusivity(inclusive, true);

        Order order = Order.FORWARD;

        selector.setStartDate(startDate);
        selector.setStopDate(new Instant());
        selector.setNumberOfRows(valueCount);
        selector.setClusivity(clusivity);
        selector.setOrder(order);
    }
}
