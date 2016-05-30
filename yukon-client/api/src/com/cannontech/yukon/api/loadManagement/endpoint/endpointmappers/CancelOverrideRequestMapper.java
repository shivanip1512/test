package com.cannontech.yukon.api.loadManagement.endpoint.endpointmappers;

import org.jdom2.Element;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.stars.dr.optout.model.CancelOptOutHelper;


public class CancelOverrideRequestMapper implements ObjectMapper<Element, CancelOptOutHelper> {

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