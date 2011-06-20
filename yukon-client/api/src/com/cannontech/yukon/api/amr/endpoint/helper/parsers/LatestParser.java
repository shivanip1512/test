package com.cannontech.yukon.api.amr.endpoint.helper.parsers;

import org.joda.time.Instant;

import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.yukon.api.amr.endpoint.helper.PointValueSelector;

public class LatestParser extends ValueParser {
    @Override
    public void parseOther(SimpleXPathTemplate template, PointValueSelector selector) {
        selector.setStartDate(null);
        selector.setStopDate(new Instant());
        selector.setNumberOfRows(1);
        selector.setClusivity(Clusivity.EXCLUSIVE_INCLUSIVE);
        selector.setOrder(Order.REVERSE);
    }
}
