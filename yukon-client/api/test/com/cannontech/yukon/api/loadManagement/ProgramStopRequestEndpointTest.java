package com.cannontech.yukon.api.loadManagement;

import java.util.Date;

import org.jdom.Element;
import org.jdom.Namespace;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.YukonXml;

public class ProgramStopRequestEndpointTest {

    private ProgramStopRequestEndpoint impl;
    private LoadControlServiceTest testService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    
    @Before
    public void setUp() throws Exception {
        
        testService = new LoadControlServiceTest();
        
        impl = new ProgramStopRequestEndpoint();
        impl.setLoadControlService(testService);
        impl.initialize();
    }
    
    private class LoadControlServiceTest extends LoadControlServiceAdapter {
        
        private String programName;
        private Date stopTime;
        
        @Override
        public ProgramStatus stopControlByProgramName(String programName, Date stopTime, boolean forceStop, boolean observeConstraintsAndExecute) throws NotFoundException, TimeoutException {

            this.programName = programName;
            this.stopTime = stopTime;

            if (programName.equals("NOT_FOUND")) {
                throw new NotFoundException("");
            } else if (programName.equals("TIMEOUT")) {
                throw new TimeoutException();
            }
            
            return null;
        }
        
        public String getProgramName() {
            return programName;
        }
        public Date getStopTime() {
            return stopTime;
        }
    }
   
    @Test
    public void testInvoke() throws Exception {
        
        // init
        Element requestElement = null;
        Element responseElement = null;
        Element tmpElement = null;
        SimpleXPathTemplate outputTemplate = null;
        
        
        // stop time
        //==========================================================================================
        requestElement = new Element("programStopRequest", ns);
        tmpElement = XmlUtils.createStringElement("programName", ns, "Program1");
        requestElement.addContent(tmpElement);
        tmpElement = XmlUtils.createStringElement("stopDateTime", ns, "2008-10-13T21:49:01Z");
        requestElement.addContent(tmpElement);
        XmlUtils.printElement(requestElement, "REQUEST (stop time)");
        
        responseElement = impl.invoke(requestElement);
        XmlUtils.printElement(responseElement, "RESPONSE (stop time)");
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        // inputs
        Assert.assertEquals("Program1", testService.getProgramName());
        Assert.assertEquals("2008-10-13T21:49:01Z", XmlUtils.formatDate(testService.getStopTime()));
        
        // outputs
        Assert.assertNotNull(outputTemplate.evaluateAsNode("/y:programStopResponse/y:success"));
        
        // no stop time
        //==========================================================================================
        requestElement = new Element("programStopRequest", ns);
        tmpElement = XmlUtils.createStringElement("programName", ns, "Program2");
        requestElement.addContent(tmpElement);
        XmlUtils.printElement(requestElement, "REQUEST (no stop time)");
        
        responseElement = impl.invoke(requestElement);
        XmlUtils.printElement(responseElement, "RESPONSE (no stop time)");
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        // inputs
        Assert.assertEquals("Program2", testService.getProgramName());
        Assert.assertEquals(null, testService.getStopTime());
        
        // outputs
        Assert.assertNotNull(outputTemplate.evaluateAsNode("/y:programStopResponse/y:success"));
        
        // not found
        //==========================================================================================
        requestElement = new Element("programStopRequest", ns);
        tmpElement = XmlUtils.createStringElement("programName", ns, "NOT_FOUND");
        requestElement.addContent(tmpElement);
        XmlUtils.printElement(requestElement, "REQUEST (not found)");
        
        responseElement = impl.invoke(requestElement);
        XmlUtils.printElement(responseElement, "RESPONSE (not found)");
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        // outputs
        Assert.assertNull(outputTemplate.evaluateAsNode("/y:programStopResponse/y:success"));
        Assert.assertNotNull(outputTemplate.evaluateAsNode("/y:programStopResponse/y:failure"));
        Assert.assertEquals("InvalidProgramName", outputTemplate.evaluateAsString("/y:programStopResponse/y:failure/y:errorCode"));
        
        // timeout
        //==========================================================================================
        requestElement = new Element("programStopRequest", ns);
        tmpElement = XmlUtils.createStringElement("programName", ns, "TIMEOUT");
        requestElement.addContent(tmpElement);
        XmlUtils.printElement(requestElement, "REQUEST (timeout)");
        
        responseElement = impl.invoke(requestElement);
        XmlUtils.printElement(responseElement, "RESPONSE (timeout)");
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        // outputs
        Assert.assertNull(outputTemplate.evaluateAsNode("/y:programStopResponse/y:success"));
        Assert.assertNotNull(outputTemplate.evaluateAsNode("/y:programStopResponse/y:failure"));
        Assert.assertEquals("Timeout", outputTemplate.evaluateAsString("/y:programStopResponse/y:failure/y:errorCode"));
    }

}
