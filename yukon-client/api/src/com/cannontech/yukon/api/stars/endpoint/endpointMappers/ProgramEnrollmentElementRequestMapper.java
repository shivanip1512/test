package com.cannontech.yukon.api.stars.endpoint.endpointMappers;

import org.jdom.Element;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;

public class ProgramEnrollmentElementRequestMapper implements ObjectMapper<Element, EnrollmentHelper> {

    @Override
    public EnrollmentHelper map(Element enrollmentRequestElement) throws ObjectMappingException {
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(enrollmentRequestElement);
        
        EnrollmentHelper enrollmentHelper = new EnrollmentHelper();
        enrollmentHelper.setAccountNumber(template.evaluateAsString("//y:accountNumber"));
        enrollmentHelper.setSerialNumber(template.evaluateAsString("//y:serialNumber"));
        enrollmentHelper.setProgramName(template.evaluateAsString("//y:loadProgramName"));
        enrollmentHelper.setLoadGroupName(template.evaluateAsString("//y:loadGroupName"));
        enrollmentHelper.setApplianceCategoryName(template.evaluateAsString("//y:applianceCategoryName"));
        enrollmentHelper.setApplianceKW(template.evaluateAsFloat("//y:appliancekW"));
        enrollmentHelper.setRelay(template.evaluateAsString("//y:relay"));
        enrollmentHelper.setSeasonalLoad(template.evaluateAsBoolean("//y:seasonalLoad", false));
        
        return enrollmentHelper;

    }
}