package com.cannontech.yukon.api.stars.endpointMappers;

import org.jdom.Element;
import org.jdom.Namespace;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.model.EnrollmentResponse;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;

public class ProgramElementResponseMapper implements ObjectMapper<Element, EnrollmentResponse> {

    @Override
    public EnrollmentResponse map(Element enrollmentResponseElement) throws ObjectMappingException {
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(enrollmentResponseElement);
        
        EnrollmentResponse enrollmentResponse = new EnrollmentResponse();
        enrollmentResponse.setAccountNumber(template.evaluateAsString("//y:accountNumber"));
        enrollmentResponse.setSerialNumber(template.evaluateAsString("//y:serialNumber"));
        enrollmentResponse.setLoadProgramName(template.evaluateAsString("//y:loadProgramName"));
        enrollmentResponse.setLoadGroupName(template.evaluateAsString("//y:loadGroupName"));
        enrollmentResponse.setSuccess(template.evaluateAsBoolean("//y:success"));
        enrollmentResponse.setFailureErrorCode(template.evaluateAsString("//y:failure/y:errorCode"));
        enrollmentResponse.setFailureErrorDescription(template.evaluateAsString("//y:failure/y:errorDescription"));
        enrollmentResponse.setFailureErrorReference(template.evaluateAsString("//y:failure/y:errorReference"));
        
        return enrollmentResponse;

    }
    
    public static Element buildElement(Namespace ns,
                                       Element enrollmentResponseBase, 
                                       EnrollmentHelper enrollmentHelper){
        Element programEnrollmentResult = new Element("programEnrollmentResult", ns);
        enrollmentResponseBase.addContent(programEnrollmentResult);

        programEnrollmentResult.addContent(XmlUtils.createStringElement("accountNumber", ns, enrollmentHelper.getAccountNumber()));
        programEnrollmentResult.addContent(XmlUtils.createStringElement("serialNumber", ns, enrollmentHelper.getSerialNumber()));
        programEnrollmentResult.addContent(XmlUtils.createStringElement("loadProgramName", ns, enrollmentHelper.getProgramName()));
        programEnrollmentResult.addContent(XmlUtils.createStringElement("loadGroupName", ns, enrollmentHelper.getLoadGroupName()));
        return programEnrollmentResult;
    }
}