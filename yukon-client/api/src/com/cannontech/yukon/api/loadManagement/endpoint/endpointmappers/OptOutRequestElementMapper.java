package com.cannontech.yukon.api.loadManagement.endpoint.endpointmappers;

import java.util.Date;

import org.jdom.Element;
import org.joda.time.Instant;
import org.joda.time.Period;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.stars.dr.optout.OptOutHelper;
import com.cannontech.stars.dr.optout.model.OptOutCounts;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;

public class OptOutRequestElementMapper implements ObjectMapper<Element, OptOutHelper> {

    @Override
    public OptOutHelper map(Element optOutRequest) throws ObjectMappingException {
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(optOutRequest);

        OptOutHelper optOutHelper = new OptOutHelper();

        String accountNumber = template.evaluateAsString("//y:accountNumber");
        String serialNumber = template.evaluateAsString("//y:serialNumber");
        Date startDate = template.evaluateAsDate("//y:startDate");
        long durationInHours = template.evaluateAsLong("//y:durationInHours");
        boolean optOutCountsBool = template.evaluateAsBooleanWithDefault("//y:counts", true);

        optOutHelper.setAccountNumber(accountNumber);
        optOutHelper.setSerialNumber(serialNumber);
        optOutHelper.setDuration(Period.hours((int) durationInHours));
        if (startDate != null) {
            optOutHelper.setStartDate(new Instant(startDate));
        }
        optOutHelper.setOptOutCounts(OptOutCounts.valueOf(optOutCountsBool));
        return optOutHelper;
    }
}
