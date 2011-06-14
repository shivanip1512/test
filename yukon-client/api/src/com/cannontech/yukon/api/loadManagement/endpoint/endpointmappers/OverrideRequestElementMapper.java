package com.cannontech.yukon.api.loadManagement.endpoint.endpointmappers;

import org.jdom.Element;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.stars.dr.optout.OptOutHelper;
import com.cannontech.stars.dr.optout.model.OptOutCounts;

public class OverrideRequestElementMapper implements ObjectMapper<Element, OptOutHelper> {

    @Override
    public OptOutHelper map(Element optOutRequest) throws ObjectMappingException {
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(optOutRequest);
        OptOutHelper optOutHelper = new OptOutHelper();

        String accountNumber = template.evaluateAsString("//y:accountNumber");
        String serialNumber = template.evaluateAsString("//y:serialNumber");
        Instant startDate = template.evaluateAsInstant("//y:startDate");
        long durationInHours = template.evaluateAsLong("//y:durationInHours");
        boolean optOutCountsBool = template.evaluateAsBoolean("//y:counts", true);

        optOutHelper.setAccountNumber(accountNumber);
        optOutHelper.setSerialNumber(serialNumber);
        optOutHelper.setDuration(Duration.standardHours(durationInHours));
        optOutHelper.setStartDate(startDate);
        optOutHelper.setOptOutCounts(OptOutCounts.valueOf(optOutCountsBool));
        return optOutHelper;
    }
}
