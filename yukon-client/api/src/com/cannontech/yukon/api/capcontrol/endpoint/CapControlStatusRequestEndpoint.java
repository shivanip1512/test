package com.cannontech.yukon.api.capcontrol.endpoint;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.requests.runnable.YukonJobRunnable;
import com.cannontech.common.requests.service.JobManagementService;
import com.cannontech.common.token.Token;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class CapControlStatusRequestEndpoint {
    private static final Logger log = YukonLogManager.getLogger(CapControlStatusRequestEndpoint.class);
    
    private final static Namespace ns = YukonXml.getYukonNamespace();
    
    @Autowired private JobManagementService jobManagementService;
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="capControlStatusRequest")
    public Element invoke(Element capControlStatusRequest, LiteYukonUser user) {
        log.debug("cap control status request received");
        
        XmlVersionUtils.verifyYukonMessageVersion(capControlStatusRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        Element response = new Element("capControlStatusResponse", ns);
        response.setAttribute("version", XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(capControlStatusRequest);
        
        String tokenStr = requestTemplate.evaluateAsString("/y:capControlStatusRequest/y:token/@value");

        Token token = new Token(tokenStr);
        
        YukonJobRunnable runnable = jobManagementService.findJob(token);
        
        if (runnable == null) {
            // Unknown token.
            Element errorElem = new Element("failure", ns);
            errorElem.addContent("Token " + tokenStr + " is invalid or has expired.");
            response.addContent(errorElem);
        } else if (runnable.isFinished()) {
            response.addContent(new Element("complete", ns));
        } else {
            Element progress = new Element("inProgress", ns);
            progress.addContent("Job " + tokenStr + " is " + runnable.getProgress() + "% complete");
            response.addContent(progress);
        }
        
        log.debug("returning response");
        return response;
    }
    
}
