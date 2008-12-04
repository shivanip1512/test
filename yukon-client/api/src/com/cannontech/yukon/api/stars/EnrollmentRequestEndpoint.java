package com.cannontech.yukon.api.stars;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.exception.DuplicateEnrollmentException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.api.stars.endpointMappers.ProgramElementRequestMapper;
import com.cannontech.yukon.api.stars.endpointMappers.ProgramElementResponseMapper;
import com.cannontech.yukon.api.util.NodeToElementMapperWrapper;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class EnrollmentRequestEndpoint {

    private EnrollmentHelperService enrollmentHelperService;
    
    @PostConstruct
    public void initialize() throws JDOMException {}
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="enrollmentRequest")
    public Element invoke(Element enrollmentRequest, YukonUserContext yukonUserContext) throws Exception {
        XmlVersionUtils.verifyYukonMessageVersion(enrollmentRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0); 
        
        Namespace ns = YukonXml.getYukonNamespaceForDefault();
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(enrollmentRequest);
        List<EnrollmentHelper> programEnrollments = 
            template.evaluate("//y:enrollmentList/y:programEnrollment", 
                              new NodeToElementMapperWrapper<EnrollmentHelper>(new ProgramElementRequestMapper()));

        Element enrollmentResponseBase = new Element("enrollmentResponse", ns);
        Attribute versionAttribute = new Attribute("version", "1.0"); 
        enrollmentResponseBase.setAttribute(versionAttribute); 
        
        Element enrollmentResultList = new Element("enrollmentResultList", ns);
        enrollmentResponseBase.addContent(enrollmentResultList);

        for (EnrollmentHelper enrollmentHelper : programEnrollments) {
            Element programEnrollmentResult = ProgramElementResponseMapper.buildElement(ns,
                                                                                        enrollmentResultList, 
                                                                                        enrollmentHelper);
            try {
                enrollmentHelperService.doEnrollment(enrollmentHelper, EnrollmentEnum.ENROLL, yukonUserContext);
            } catch(NotFoundException e) {
                Element failureElement = XMLFailureGenerator.generateFailure(enrollmentRequest, e, "NotFoundException", e.getMessage(), ns);
                programEnrollmentResult.addContent(failureElement);
                continue;
            } catch(IllegalArgumentException e) {
                Element failureElement = XMLFailureGenerator.generateFailure(enrollmentRequest, e, "IllegalArgumentException", e.getMessage(), ns);
                programEnrollmentResult.addContent(failureElement);
                continue;
            } catch(DuplicateEnrollmentException e) {
                Element failureElement = XMLFailureGenerator.generateFailure(enrollmentRequest, e, "DuplicateEnrollmentException", e.getMessage(), ns);
                programEnrollmentResult.addContent(failureElement);
                continue;
            }
    
            programEnrollmentResult.addContent(new Element("success", ns));
        }
        return enrollmentResponseBase;
    }
    
    @Autowired
    public void setEnrollmentHelperService(
            EnrollmentHelperService enrollmentHelperService) {
        this.enrollmentHelperService = enrollmentHelperService;
    }
    
}






