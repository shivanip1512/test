package com.cannontech.yukon.api.stars.endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.model.EnrollmentResponse;
import com.cannontech.yukon.api.loadManagement.mocks.MockAccountEventLogService;
import com.cannontech.yukon.api.stars.endpoint.endpointMappers.ProgramEnrollmentElementResponseMapper;
import com.cannontech.yukon.api.util.NodeToElementMapperWrapper;

public class UnenrollmentRequestEndpointTest {

    private UnenrollmentRequestEndpoint impl;
    private EnrollmentHelperEndpointServiceMock enrollmentTestService;
    private Namespace ns;
    private String childNodePath = "//y:unenrollmentResponse/y:enrollmentResultList/y:programEnrollmentResult";
    
    @BeforeEach
    public void setUp() throws Exception {
        ns = YukonXml.getYukonNamespaceForDefault();    
        enrollmentTestService = new EnrollmentHelperEndpointServiceMock();
        
        impl = new UnenrollmentRequestEndpoint();
        impl.setAccountEventLogService(new MockAccountEventLogService());
        impl.setEnrollmentHelperService(enrollmentTestService);
    }
    
    @Test
    public void testInvokeNotFoundException() throws Exception {
        
        // init
        Element responseElement = null;
        SimpleXPathTemplate outputTemplate = null;

        Element failRequest = buildFailRequest("NOT_FOUND");
        responseElement = impl.invoke(failRequest, null);
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        // Check the response
        List<EnrollmentResponse> enrollmentResponses = 
            outputTemplate.evaluate(childNodePath, 
                                    new NodeToElementMapperWrapper<EnrollmentResponse>(new ProgramEnrollmentElementResponseMapper()));

        
        assertEquals("1234567", enrollmentResponses.get(0).getAccountNumber());
        assertEquals("1234567890", enrollmentResponses.get(0).getSerialNumber());
        assertEquals("NOT_FOUND", enrollmentResponses.get(0).getLoadProgramName());
        assertEquals("load group name", enrollmentResponses.get(0).getLoadGroupName());
        assertEquals("NotFoundException", enrollmentResponses.get(0).getFailureErrorCode());
        assertEquals("The program name supplied does not exist.", enrollmentResponses.get(0).getFailureErrorDescription());

    }
    
    @Test
    public void testInvokeIllegalArgument() throws Exception {
     
        // init
        Element responseElement = null;
        SimpleXPathTemplate outputTemplate = null;

        Element failRequest = buildFailRequest("ILLEGAL_ARGUMENT");
        responseElement = impl.invoke(failRequest, null);
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        // Check the response
        List<EnrollmentResponse> enrollmentResponses = 
            outputTemplate.evaluate(childNodePath,
                                    new NodeToElementMapperWrapper<EnrollmentResponse>(new ProgramEnrollmentElementResponseMapper()));

        
        assertEquals("1234567", enrollmentResponses.get(0).getAccountNumber());
        assertEquals("1234567890", enrollmentResponses.get(0).getSerialNumber());
        assertEquals("ILLEGAL_ARGUMENT", enrollmentResponses.get(0).getLoadProgramName());
        assertEquals("load group name", enrollmentResponses.get(0).getLoadGroupName());
        assertEquals("IllegalArgumentException", enrollmentResponses.get(0).getFailureErrorCode());
        assertEquals("The load group supplied does not belong to the program supplied.", enrollmentResponses.get(0).getFailureErrorDescription());

    }

    @Test
    public void testInvokeSingle() throws Exception {
        
        // init
        Element responseElement = null;
        SimpleXPathTemplate outputTemplate = null;
        
        Element successRequest = buildSuccessSingleRequest();
        responseElement = impl.invoke(successRequest, null);
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        // Check the response
        List<EnrollmentResponse> enrollmentResponses = 
            outputTemplate.evaluate(childNodePath,
                                    new NodeToElementMapperWrapper<EnrollmentResponse>(new ProgramEnrollmentElementResponseMapper()));
        
        
        assertEquals("1234567", enrollmentResponses.get(0).getAccountNumber());
        assertEquals("1234567890", enrollmentResponses.get(0).getSerialNumber());
        assertEquals("load program name", enrollmentResponses.get(0).getLoadProgramName());
        assertEquals("load group name", enrollmentResponses.get(0).getLoadGroupName());
    }
    
    @Test
    public void testInvokeMutliple() throws Exception {

        // init
        Element responseElement = null;
        SimpleXPathTemplate outputTemplate = null;

        Element successRequest = buildSuccessMultipleRequest();
        responseElement = impl.invoke(successRequest, null);
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        // Check the response
        List<EnrollmentResponse> enrollmentResponses = 
            outputTemplate.evaluate(childNodePath,
                                    new NodeToElementMapperWrapper<EnrollmentResponse>(new ProgramEnrollmentElementResponseMapper()));

        
        assertEquals("1234567", enrollmentResponses.get(0).getAccountNumber());
        assertEquals("1234567890", enrollmentResponses.get(0).getSerialNumber());
        assertEquals("load program name 1", enrollmentResponses.get(0).getLoadProgramName());
        assertEquals("load group name 1", enrollmentResponses.get(0).getLoadGroupName());

        assertEquals("7654321", enrollmentResponses.get(1).getAccountNumber());
        assertEquals("0987654321", enrollmentResponses.get(1).getSerialNumber());
        assertEquals("load program name 2", enrollmentResponses.get(1).getLoadProgramName());
        assertEquals("load group name 2", enrollmentResponses.get(1).getLoadGroupName());
    
    }

    @Test
    public void testInvokeOptionalRequirements() throws Exception {

        // init
        Element responseElement = null;
        SimpleXPathTemplate outputTemplate = null;

        Element successRequest = buildSuccessOptionalRequirementsRequest();
        responseElement = impl.invoke(successRequest, null);
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        // Check the response
        List<EnrollmentResponse> enrollmentResponses = 
            outputTemplate.evaluate(childNodePath,
                                    new NodeToElementMapperWrapper<EnrollmentResponse>(new ProgramEnrollmentElementResponseMapper()));

        
        assertEquals("1234567", enrollmentResponses.get(0).getAccountNumber());
        assertEquals("1234567890", enrollmentResponses.get(0).getSerialNumber());
        assertEquals("load program name", enrollmentResponses.get(0).getLoadProgramName());
        assertEquals("load group name", enrollmentResponses.get(0).getLoadGroupName());

    }

    
    private Element buildFailRequest(String exceptionType){
        EnrollmentHelper enrollmentHelper = 
            new EnrollmentHelper("1234567", "load group name", exceptionType, "1234567890");
        
        Element requestElement = new Element("unenrollmentRequest", ns);
        Attribute versionAttribute = new Attribute("version", "1.0"); 
        requestElement.setAttribute(versionAttribute); 

        Element enrollmentListElement = new Element("enrollmentList", ns); 
        requestElement.addContent(enrollmentListElement);
        EnrollmentEndpointTestUtil.buildUnenrollmentRequest(ns,
                                                          enrollmentListElement, 
                                                          enrollmentHelper);

        return requestElement;
    }

    private Element buildSuccessSingleRequest(){
        EnrollmentHelper enrollmentHelper = 
            new EnrollmentHelper("1234567", "load group name", "load program name", "1234567890");
        
        Element requestElement = new Element("unenrollmentRequest", ns);
        Attribute versionAttribute = new Attribute("version", "1.0"); 
        requestElement.setAttribute(versionAttribute); 

        Element enrollmentListElement = new Element("enrollmentList", ns); 
        requestElement.addContent(enrollmentListElement);
        EnrollmentEndpointTestUtil.buildUnenrollmentRequest(ns,
                                                          enrollmentListElement, 
                                                          enrollmentHelper);
        return requestElement;
    }
    
    private Element buildSuccessMultipleRequest(){
        EnrollmentHelper enrollmentHelper1 = 
            new EnrollmentHelper("1234567", "load group name 1", "load program name 1", "1234567890");
        EnrollmentHelper enrollmentHelper2 = 
            new EnrollmentHelper("7654321", "load group name 2", "load program name 2", "0987654321");
        
        Element requestElement = new Element("unenrollmentRequest", ns);
        Attribute versionAttribute = new Attribute("version", "1.0"); 
        requestElement.setAttribute(versionAttribute); 

        Element enrollmentListElement = new Element("enrollmentList", ns); 
        requestElement.addContent(enrollmentListElement);
        EnrollmentEndpointTestUtil.buildUnenrollmentRequest(ns,
                                                          enrollmentListElement, 
                                                          enrollmentHelper1);
        
        EnrollmentEndpointTestUtil.buildUnenrollmentRequest(ns,
                                                          enrollmentListElement, 
                                                          enrollmentHelper2);

        return requestElement;
    }

    
    private Element buildSuccessOptionalRequirementsRequest(){
        EnrollmentHelper enrollmentHelper = 
            new EnrollmentHelper("1234567", "appliance category name", 24.3f, "load group name", 
                                 "load program name", "1", true, "1234567890");
        
        Element requestElement = new Element("unenrollmentRequest", ns);
        Attribute versionAttribute = new Attribute("version", "1.0"); 
        requestElement.setAttribute(versionAttribute); 

        Element enrollmentListElement = new Element("enrollmentList", ns); 
        requestElement.addContent(enrollmentListElement);
        EnrollmentEndpointTestUtil.buildUnenrollmentRequest(ns,
                                                          enrollmentListElement, 
                                                          enrollmentHelper);
        
        return requestElement;
    }
}
