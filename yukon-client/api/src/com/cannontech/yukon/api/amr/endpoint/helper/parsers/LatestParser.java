package com.cannontech.yukon.api.amr.endpoint.helper.parsers;

import java.util.Date;

import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.yukon.api.amr.endpoint.helper.PointValueSelector;

public class LatestParser extends ValueParser {
    @Override
    public PointValueSelector parse(SimpleXPathTemplate template) {
        PointValueSelector selector = super.parse(template);

        selector.setStartDate(null);
        selector.setStopDate(new Date());
        selector.setNumberOfRows(1);
        selector.setClusivity(Clusivity.EXCLUSIVE_INCLUSIVE);
        selector.setOrder(Order.REVERSE);

        return selector;
    }
}
