package com.cannontech.yukon.api.common.endpoint;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.requests.runnable.YukonJob;
import com.cannontech.common.requests.service.JobManagementService;
import com.cannontech.common.token.Token;
import com.cannontech.common.token.TokenStatus;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class JobStatusRequestEndpoint {
    private static final Logger log = YukonLogManager.getLogger(JobStatusRequestEndpoint.class);
    
    private final static Namespace ns = YukonXml.getYukonNamespace();
    
    @Autowired private JobManagementService jobManagementService;
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="jobStatusRequest")
    public Element invoke(Element jobStatusRequest, LiteYukonUser user) {
        log.debug("job status request received");
        
        XmlVersionUtils.verifyYukonMessageVersion(jobStatusRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        Element response = new Element("jobStatusResponse", ns);
        response.setAttribute("version", XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(jobStatusRequest);
        
        String tokenStr = requestTemplate.evaluateAsString("/y:jobStatusRequest/y:token/@value");

        Token token = new Token(tokenStr);
        
        TokenStatus status = jobManagementService.getStatus(token);
        
        if (status == null) {
            // Unknown token.       
            Element fe = XMLFailureGenerator.generateFailure(jobStatusRequest, "UnknownToken", "Token " + tokenStr + " is invalid or has expired.");
            response.addContent(fe);
        } else if (status.isFinished()) {
            response.addContent(new Element("complete", ns));
        } else {
            YukonJob job = (YukonJob) status;
            Element progress = new Element("inProgress", ns);
            if (status instanceof YukonJob) {
                progress.setAttribute(new Attribute("percentComplete", Double.toString(job.getProgress())));
                Element description = new Element("description", ns);
                description.addContent("Job " + tokenStr + " is " + job.getProgress() + "% complete");
                progress.addContent(description);
            }
            response.addContent(progress);
        }
        
        log.debug("returning response");
        return response;
    }
    
}
