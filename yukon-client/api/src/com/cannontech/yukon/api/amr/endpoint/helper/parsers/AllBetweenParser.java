package com.cannontech.yukon.api.amr.endpoint.helper.parsers;

import java.util.Date;

import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.yukon.api.amr.endpoint.helper.PointValueSelector;
import com.cannontech.yukon.api.amr.endpoint.helper.PointValueSelector.OrderHelper;

public class AllBetweenParser extends ValueParser {
    @Override
    public PointValueSelector parse(SimpleXPathTemplate template) {
        PointValueSelector selector = super.parse(template);

        Date startDate = template.evaluateAsDate("@from", new Date());
        Date stopDate = template.evaluateAsDate("@to");
        if (startDate.after(stopDate)) {
            throw new RuntimeException("from cannot be after to for allBetween");
        }

        Boolean fromInclusive = template.evaluateAsBoolean("@fromInclusive", false);
        Boolean toInclusive = template.evaluateAsBoolean("@toInclusive", true);
        Clusivity clusivity = Clusivity.getClusivity(fromInclusive, toInclusive);

        Integer limit = parseLimit(template);

        String orderString = template.evaluateAsString("@order");
        Order order = OrderHelper.getOrderByName(orderString, Order.FORWARD);

        selector.setStartDate(startDate);
        selector.setStopDate(stopDate);
        selector.setClusivity(clusivity);
        selector.setNumberOfRows(limit);
        selector.setOrder(order);

        return selector;
    }
}
