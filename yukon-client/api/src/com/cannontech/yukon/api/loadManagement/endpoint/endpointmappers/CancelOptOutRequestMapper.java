package com.cannontech.yukon.api.loadManagement.endpoint.endpointmappers;

import org.jdom.Element;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.stars.dr.optout.CancelOptOutHelper;
import com.cannontech.yukon.api.util.YukonXml;


public class CancelOptOutRequestMapper implements ObjectMapper<Element, CancelOptOutHelper> {

    @Override
    public CancelOptOutHelper map(Element cancelOptOutRequest) throws ObjectMappingException {
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(cancelOptOutRequest);

        CancelOptOutHelper cancelOptOutHelper = new CancelOptOutHelper();

        String accountNumber = template.evaluateAsString("//y:accountNumber");
        String serialNumber = template.evaluateAsString("//y:serialNumber");
       
        cancelOptOutHelper.setAccountNumber(accountNumber);
        cancelOptOutHelper.setSerialNumber(serialNumber);
        return cancelOptOutHelper;
    }
}