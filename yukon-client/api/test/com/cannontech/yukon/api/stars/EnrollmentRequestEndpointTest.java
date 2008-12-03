package com.cannontech.yukon.api.stars;

import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.model.EnrollmentResponse;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.api.stars.endpointMappers.ProgramElementRequestMapper;
import com.cannontech.yukon.api.stars.endpointMappers.ProgramElementResponseMapper;
import com.cannontech.yukon.api.util.NodeToElementMapperWrapper;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.YukonXml;

public class EnrollmentRequestEndpointTest {

    private EnrollmentRequestEndpoint impl;
    private EnrollmentHelperEndpointServiceMock enrollmentTestService;
    private Namespace ns; 
    
    @Before
    public void setUp() throws Exception {
        ns = YukonXml.getYukonNamespace();    
        enrollmentTestService = new EnrollmentHelperEndpointServiceMock();
        
        impl = new EnrollmentRequestEndpoint();
        impl.setEnrollmentHelperService(enrollmentTestService);
        impl.initialize();
    }

    @Test
    public void testInvokeNotFoundException() throws Exception {
        
        // init
        Element responseElement = null;
        SimpleXPathTemplate outputTemplate = null;
        YukonUserContext userContext = null;

        Element failRequest = buildFailRequest("NOT_FOUND");
        XmlUtils.printElement(failRequest, "REQUEST (NOT_FOUND)");
        
        responseElement = impl.invoke(failRequest, userContext);
        XmlUtils.printElement(responseElement, "RESPONSE (NOT_FOUND)");
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        // Check the response
        List<EnrollmentResponse> enrollmentResponses = 
            outputTemplate.evaluate("//y:enrollmentResponse/y:enrollmentResultList/y:programEnrollmentResult", 
                              new NodeToElementMapperWrapper<EnrollmentResponse>(new ProgramElementResponseMapper()));

        
        Assert.assertEquals("1234567", enrollmentResponses.get(0).getAccountNumber());
        Assert.assertEquals("1234567890", enrollmentResponses.get(0).getSerialNumber());
        Assert.assertEquals("NOT_FOUND", enrollmentResponses.get(0).getLoadProgramName());
        Assert.assertEquals("load group name", enrollmentResponses.get(0).getLoadGroupName());
        Assert.assertEquals("NotFoundException", enrollmentResponses.get(0).getFailureErrorCode());
        Assert.assertEquals("The program name supplied does not exist.", enrollmentResponses.get(0).getFailureErrorDescription());

    }
    
    @Test
    public void testInvokeIllegalArgument() throws Exception {
     
        // init
        Element responseElement = null;
        SimpleXPathTemplate outputTemplate = null;
        YukonUserContext userContext = null;

        Element failRequest = buildFailRequest("ILLEGAL_ARGUMENT");
        XmlUtils.printElement(failRequest, "REQUEST (ILLEGAL_ARGUMENT)");
        
        responseElement = impl.invoke(failRequest, userContext);
        XmlUtils.printElement(responseElement, "RESPONSE (ILLEGAL_ARGUMENT)");
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        // Check the response
        List<EnrollmentResponse> enrollmentResponses = 
            outputTemplate.evaluate("//y:enrollmentResponse/y:enrollmentResultList/y:programEnrollmentResult", 
                              new NodeToElementMapperWrapper<EnrollmentResponse>(new ProgramElementResponseMapper()));

        
        Assert.assertEquals("1234567", enrollmentResponses.get(0).getAccountNumber());
        Assert.assertEquals("1234567890", enrollmentResponses.get(0).getSerialNumber());
        Assert.assertEquals("ILLEGAL_ARGUMENT", enrollmentResponses.get(0).getLoadProgramName());
        Assert.assertEquals("load group name", enrollmentResponses.get(0).getLoadGroupName());
        Assert.assertEquals("NotFoundException", enrollmentResponses.get(0).getFailureErrorCode());
        Assert.assertEquals("The load group supplies does not belong to the program supplied.", enrollmentResponses.get(0).getFailureErrorDescription());

    }
    
    @Test
    public void testInvokeDuplicateEnrollment() throws Exception {

        // init
        Element responseElement = null;
        SimpleXPathTemplate outputTemplate = null;
        YukonUserContext userContext = null;

        Element failRequest = buildFailRequest("DUPLICATE_ENROLLMENT");
        XmlUtils.printElement(failRequest, "REQUEST (DUPLICATE_ENROLLMENT)");
        
        responseElement = impl.invoke(failRequest, userContext);
        XmlUtils.printElement(responseElement, "RESPONSE (DUPLICATE_ENROLLMENT)");
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        // Check the response
        List<EnrollmentResponse> enrollmentResponses = 
            outputTemplate.evaluate("//y:enrollmentResponse/y:enrollmentResultList/y:programEnrollmentResult", 
                              new NodeToElementMapperWrapper<EnrollmentResponse>(new ProgramElementResponseMapper()));

        
        Assert.assertEquals("1234567", enrollmentResponses.get(0).getAccountNumber());
        Assert.assertEquals("1234567890", enrollmentResponses.get(0).getSerialNumber());
        Assert.assertEquals("DUPLICATE_ENROLLMENT", enrollmentResponses.get(0).getLoadProgramName());
        Assert.assertEquals("load group name", enrollmentResponses.get(0).getLoadGroupName());
        Assert.assertEquals("NotFoundException", enrollmentResponses.get(0).getFailureErrorCode());
        Assert.assertEquals("The enrollment name supplied causes a duplicate enrollment.", enrollmentResponses.get(0).getFailureErrorDescription());

    }

    @Test
    public void testInvokeSingle() throws Exception {
        
        // init
        Element responseElement = null;
        SimpleXPathTemplate outputTemplate = null;
        YukonUserContext userContext = null;
        
        Element successRequest = buildSuccessSingleRequest();
        XmlUtils.printElement(successRequest, "REQUEST (Single Enrollment)");
        
        responseElement = impl.invoke(successRequest, userContext);
        XmlUtils.printElement(responseElement, "RESPONSE (Single Enrollment)");
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        // Check the response
        List<EnrollmentResponse> enrollmentResponses = 
            outputTemplate.evaluate("//y:enrollmentResponse/y:enrollmentResultList/y:programEnrollmentResult", 
                                    new NodeToElementMapperWrapper<EnrollmentResponse>(new ProgramElementResponseMapper()));
        
        
        Assert.assertEquals("1234567", enrollmentResponses.get(0).getAccountNumber());
        Assert.assertEquals("1234567890", enrollmentResponses.get(0).getSerialNumber());
        Assert.assertEquals("load program name", enrollmentResponses.get(0).getLoadProgramName());
        Assert.assertEquals("load group name", enrollmentResponses.get(0).getLoadGroupName());
    }
    
    @Test
    public void testInvokeMutliple() throws Exception {

        // init
        Element responseElement = null;
        SimpleXPathTemplate outputTemplate = null;
        YukonUserContext userContext = null;

        Element successRequest = buildSuccessMultipleRequest();
        XmlUtils.printElement(successRequest, "REQUEST (Multiple Enrollment)");
        
        responseElement = impl.invoke(successRequest, userContext);
        XmlUtils.printElement(responseElement, "RESPONSE (Multiple Enrollment)");
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        // Check the response
        List<EnrollmentResponse> enrollmentResponses = 
            outputTemplate.evaluate("//y:enrollmentResponse/y:enrollmentResultList/y:programEnrollmentResult", 
                              new NodeToElementMapperWrapper<EnrollmentResponse>(new ProgramElementResponseMapper()));

        
        Assert.assertEquals("1234567", enrollmentResponses.get(0).getAccountNumber());
        Assert.assertEquals("1234567890", enrollmentResponses.get(0).getSerialNumber());
        Assert.assertEquals("load program name 1", enrollmentResponses.get(0).getLoadProgramName());
        Assert.assertEquals("load group name 1", enrollmentResponses.get(0).getLoadGroupName());

        Assert.assertEquals("7654321", enrollmentResponses.get(1).getAccountNumber());
        Assert.assertEquals("0987654321", enrollmentResponses.get(1).getSerialNumber());
        Assert.assertEquals("load program name 2", enrollmentResponses.get(1).getLoadProgramName());
        Assert.assertEquals("load group name 2", enrollmentResponses.get(1).getLoadGroupName());
    
    }

    @Test
    public void testInvokeOptionalRequirements() throws Exception {

        // init
        Element responseElement = null;
        SimpleXPathTemplate outputTemplate = null;
        YukonUserContext userContext = null;

        Element successRequest = buildSuccessOptionalRequirementsRequest();
        XmlUtils.printElement(successRequest, "REQUEST (Optional Requirements Enrollment)");
        
        responseElement = impl.invoke(successRequest, userContext);
        XmlUtils.printElement(responseElement, "RESPONSE (Optional Requirements Enrollment)");
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        // Check the response
        List<EnrollmentResponse> enrollmentResponses = 
            outputTemplate.evaluate("//y:enrollmentResponse/y:enrollmentResultList/y:programEnrollmentResult", 
                              new NodeToElementMapperWrapper<EnrollmentResponse>(new ProgramElementResponseMapper()));

        
        Assert.assertEquals("1234567", enrollmentResponses.get(0).getAccountNumber());
        Assert.assertEquals("1234567890", enrollmentResponses.get(0).getSerialNumber());
        Assert.assertEquals("load program name", enrollmentResponses.get(0).getLoadProgramName());
        Assert.assertEquals("load group name", enrollmentResponses.get(0).getLoadGroupName());

    }

    
    private Element buildFailRequest(String exceptionType){
        EnrollmentHelper enrollmentHelper = 
            new EnrollmentHelper("1234567", "load group name", exceptionType, "1234567890");
        
        Element requestElement = new Element("enrollmentRequest", ns);
        Element enrollmentListElement = new Element("enrollmentList", ns); 
        requestElement.addContent(enrollmentListElement);
        ProgramElementRequestMapper.buildElement(ns, 
                                                 enrollmentListElement, 
                                                 enrollmentHelper);

        return requestElement;
    }

    private Element buildSuccessSingleRequest(){
        EnrollmentHelper enrollmentHelper = 
            new EnrollmentHelper("1234567", "load group name", "load program name", "1234567890");
        
        Element requestElement = new Element("enrollmentRequest", ns);
        Element enrollmentListElement = new Element("enrollmentList", ns); 
        requestElement.addContent(enrollmentListElement);
        ProgramElementRequestMapper.buildElement(ns, 
                                                 enrollmentListElement, 
                                                 enrollmentHelper);
        return requestElement;
    }
    
    private Element buildSuccessMultipleRequest(){
        EnrollmentHelper enrollmentHelper1 = 
            new EnrollmentHelper("1234567", "load group name 1", "load program name 1", "1234567890");
        EnrollmentHelper enrollmentHelper2 = 
            new EnrollmentHelper("7654321", "load group name 2", "load program name 2", "0987654321");
        
        Element requestElement = new Element("enrollmentRequest", ns);
        Element enrollmentListElement = new Element("enrollmentList", ns); 
        requestElement.addContent(enrollmentListElement);
        ProgramElementRequestMapper.buildElement(ns, 
                                                 enrollmentListElement, 
                                                 enrollmentHelper1);
        
        ProgramElementRequestMapper.buildElement(ns, 
                                                 enrollmentListElement, 
                                                 enrollmentHelper2);

        return requestElement;
    }

    
    private Element buildSuccessOptionalRequirementsRequest(){
        EnrollmentHelper enrollmentHelper = 
            new EnrollmentHelper("1234567", "appliance category name", 24.3f, "load group name", 
                                 "load program name", "1", true, "1234567890");
        
        Element requestElement = new Element("enrollmentRequest", ns);
        Element enrollmentListElement = new Element("enrollmentList", ns); 
        requestElement.addContent(enrollmentListElement);
        ProgramElementRequestMapper.buildElement(ns, 
                                                 enrollmentListElement, 
                                                 enrollmentHelper);
        
        return requestElement;
    }
}
