package com.cannontech.yukon.api.amr.endpoint.helper.parsers;

import java.util.Date;

import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.yukon.api.amr.endpoint.helper.PointValueSelector;

public class AllParser extends ValueParser {
    @Override
    public PointValueSelector parse(SimpleXPathTemplate template) {
        PointValueSelector selector = super.parse(template);

        selector.setStopDate(new Date());
        selector.setClusivity(Clusivity.EXCLUSIVE_INCLUSIVE);
        selector.setNumberOfRows(null);
        selector.setOrder(Order.FORWARD);

        return selector;
    }
}
