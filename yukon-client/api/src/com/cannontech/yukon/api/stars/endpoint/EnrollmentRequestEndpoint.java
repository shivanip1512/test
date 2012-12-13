package com.cannontech.yukon.api.stars.endpoint;

import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.exception.DuplicateEnrollmentException;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.yukon.api.stars.endpoint.endpointMappers.ProgramEnrollmentElementRequestMapper;
import com.cannontech.yukon.api.util.NodeToElementMapperWrapper;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class EnrollmentRequestEndpoint {

    private AccountEventLogService accountEventLogService;
    private EnrollmentHelperService enrollmentHelperService;
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="enrollmentRequest")
    public Element invoke(Element enrollmentRequest, LiteYukonUser user) throws Exception {
        XmlVersionUtils.verifyYukonMessageVersion(enrollmentRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0); 
        
        Namespace ns = YukonXml.getYukonNamespaceForDefault();
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(enrollmentRequest);
        List<EnrollmentHelper> programEnrollments = 
            template.evaluate("//y:enrollmentList/y:programEnrollment", 
                              new NodeToElementMapperWrapper<EnrollmentHelper>(new ProgramEnrollmentElementRequestMapper()));

        Element enrollmentResponseBase = new Element("enrollmentResponse", ns);
        XmlVersionUtils.addVersionAttribute(enrollmentResponseBase, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        Element enrollmentResultList = new Element("enrollmentResultList", ns);
        enrollmentResponseBase.addContent(enrollmentResultList);

        for (EnrollmentHelper enrollmentHelper : programEnrollments) {
            Element programEnrollmentResult = EnrollmentEndpointUtil.buildEnrollmentResponse(ns,
                                                                                             enrollmentResultList, 
                                                                                             enrollmentHelper);

            accountEventLogService.enrollmentAttempted(user,
                                                       enrollmentHelper.getAccountNumber(),
                                                       enrollmentHelper.getSerialNumber(),
                                                       enrollmentHelper.getProgramName(),
                                                       enrollmentHelper.getLoadGroupName(),
                                                       EventSource.API);
            
            Element resultElement;
            try {
                enrollmentHelperService.doEnrollment(enrollmentHelper, EnrollmentEnum.ENROLL, user);
                resultElement = new Element("success", ns);
            } catch(NotFoundException e) {
                resultElement = XMLFailureGenerator.generateFailure(enrollmentRequest, e, "NotFoundException", e.getMessage(), ns);
            } catch(IllegalArgumentException e) {
                resultElement = XMLFailureGenerator.generateFailure(enrollmentRequest, e, "IllegalArgumentException", e.getMessage(), ns);
            } catch(DuplicateEnrollmentException e) {
                resultElement = XMLFailureGenerator.generateFailure(enrollmentRequest, e, "DuplicateEnrollmentException", e.getMessage(), ns);
            }
            programEnrollmentResult.addContent(resultElement);
        }

        return enrollmentResponseBase;
    }
    
    @Autowired
    public void setAccountEventLogService(AccountEventLogService accountEventLogService) {
        this.accountEventLogService = accountEventLogService;
    }
    
    @Autowired
    public void setEnrollmentHelperService(EnrollmentHelperService enrollmentHelperService) {
        this.enrollmentHelperService = enrollmentHelperService;
    }
}