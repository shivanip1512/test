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
public class CapControlReportRequestEndpoint {
    private static final Logger log = YukonLogManager.getLogger(CapControlReportRequestEndpoint.class);

    private final static Namespace ns = YukonXml.getYukonNamespace();
    
    @Autowired private JobManagementService jobManagementService;
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="capControlReportRequest")
    public Element invoke(Element capControlReportRequest, LiteYukonUser user) {
        log.debug("cap control report request received");
        
        XmlVersionUtils.verifyYukonMessageVersion(capControlReportRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        Element response = new Element("capControlReportResponse", ns);
        response.setAttribute("version", XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(capControlReportRequest);

        String tokenStr = requestTemplate.evaluateAsString("/y:capControlReportRequest/y:token/@value");
        
        Token token = new Token(tokenStr);
        
        YukonJobRunnable runnable = jobManagementService.findJob(token);
        
        if (runnable == null) {
            // Unknown token.
            Element errorElem = new Element("failure", ns);
            errorElem.addContent("Token " + tokenStr + " is invalid or has expired.");
            response.addContent(errorElem);
        } else {
            // This will add a failure to the response if the job isn't finished.
            runnable.reportResults(response);
        }
        
        log.debug("returning response");
        return response;
    }
}
