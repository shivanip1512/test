package com.cannontech.yukon.api.stars.endpoint.endpointMappers;

import org.jdom.Element;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.stars.dr.enrollment.model.EnrollmentResponse;

public class ProgramEnrollmentElementResponseMapper implements ObjectMapper<Element, EnrollmentResponse> {

    @Override
    public EnrollmentResponse map(Element enrollmentResponseElement) throws ObjectMappingException {
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(enrollmentResponseElement);
        
        EnrollmentResponse enrollmentResponse = new EnrollmentResponse();
        enrollmentResponse.setAccountNumber(template.evaluateAsString("//y:accountNumber"));
        enrollmentResponse.setSerialNumber(template.evaluateAsString("//y:serialNumber"));
        enrollmentResponse.setLoadProgramName(template.evaluateAsString("//y:loadProgramName"));
        enrollmentResponse.setLoadGroupName(template.evaluateAsString("//y:loadGroupName"));
        enrollmentResponse.setSuccess(template.evaluateAsBoolean("//y:success", false));
        enrollmentResponse.setFailureErrorCode(template.evaluateAsString("//y:failure/y:errorCode"));
        enrollmentResponse.setFailureErrorDescription(template.evaluateAsString("//y:failure/y:errorDescription"));
        enrollmentResponse.setFailureErrorReference(template.evaluateAsString("//y:failure/y:errorReference"));
        
        return enrollmentResponse;

    }
}