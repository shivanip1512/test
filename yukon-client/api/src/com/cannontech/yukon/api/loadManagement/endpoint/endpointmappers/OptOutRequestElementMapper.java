package com.cannontech.yukon.api.loadManagement.endpoint.endpointmappers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jdom.Element;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.stars.dr.optout.OptOutHelper;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;

public class OptOutRequestElementMapper implements ObjectMapper<Element, OptOutHelper> {

    @Override
    public OptOutHelper map(Element optOutRequest) throws ObjectMappingException{
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(optOutRequest);
        
        OptOutHelper optOutHelper = new OptOutHelper();
        
        String accountNumber = template.evaluateAsString("//y:accountNumber");
        String serialNumber = template.evaluateAsString("//y:serialNumber");
        String startDateStr = template.evaluateAsString("//y:startDate");
        long durationInDays = template.evaluateAsLong("//y:durationInDays");
        
        optOutHelper.setAccountNumber(accountNumber);
        optOutHelper.setSerialNumber(serialNumber);
        optOutHelper.setDurationInDays(durationInDays);
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try{
            Date startDate = df.parse(startDateStr);
            optOutHelper.setStartDate(startDate);
        }
        catch (ParseException e){
            throw new ObjectMappingException(e.getMessage());
        }

        return optOutHelper;
    }

}
