package com.cannontech.yukon.api.amr.endpoint.helper.parsers;

import java.util.Date;

import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.yukon.api.amr.endpoint.helper.PointValueSelector;
import com.cannontech.yukon.api.amr.endpoint.helper.PointValueSelector.OrderHelper;

public class AllBeforeParser extends ValueParser {
    @Override
    public PointValueSelector parse(SimpleXPathTemplate template) {
        PointValueSelector selector = super.parse(template);

        Date stopDate = template.evaluateAsDate("@date", new Date());
        Integer limit = parseLimit(template);

        Boolean inclusive = template.evaluateAsBoolean("@inclusive", true);
        Clusivity clusivity = Clusivity.getClusivity(true, inclusive);

        String orderString = template.evaluateAsString("@order");
        Order order = OrderHelper.getOrderByName(orderString, Order.REVERSE);

        selector.setStopDate(stopDate);
        selector.setNumberOfRows(limit);
        selector.setClusivity(clusivity);
        selector.setOrder(order);

        return selector;
    }
}
