package com.cannontech.yukon.api.amr.endpoint.helper.parsers;

import java.util.Date;

import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.yukon.api.amr.endpoint.helper.PointValueSelector;

public class AfterParser extends ValueParser {
    @Override
    public PointValueSelector parse(SimpleXPathTemplate template) {
        PointValueSelector selector = super.parse(template);

        Date startDate = template.evaluateAsDate("@date");
        int index = template.evaluateAsInt("@index", 1);

        Boolean inclusive = template.evaluateAsBoolean("@inclusive", true);
        Clusivity clusivity = Clusivity.getClusivity(inclusive, false);

        Order order = Order.REVERSE;

        selector.setStartDate(startDate);
        selector.setStopDate(new Date());
        selector.setNumberOfRows(index);
        selector.setClusivity(clusivity);
        selector.setOrder(order);

        return selector;
    }
}
