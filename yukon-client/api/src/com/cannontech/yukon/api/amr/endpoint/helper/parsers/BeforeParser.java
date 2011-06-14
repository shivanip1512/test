package com.cannontech.yukon.api.amr.endpoint.helper.parsers;

import java.util.Date;

import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.yukon.api.amr.endpoint.helper.PointValueSelector;

public class BeforeParser extends ValueParser {
    @Override
    public PointValueSelector parse(SimpleXPathTemplate template) {
        PointValueSelector selector = super.parse(template);

        Date stopDate = template.evaluateAsDate("@date", new Date());
        int index = template.evaluateAsInt("@index", 1);

        Boolean inclusive = template.evaluateAsBoolean("@inclusive", true);
        Clusivity clusivity = Clusivity.getClusivity(true, inclusive);

        Order order = Order.FORWARD;

        selector.setStopDate(stopDate);
        selector.setNumberOfRows(index);
        selector.setClusivity(clusivity);
        selector.setOrder(order);

        return selector;
    }
}
