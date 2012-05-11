package com.cannontech.yukon.api.capcontrol.endpoint;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.capcontrol.creation.model.ImportAction;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.requests.runnable.YukonJobRunnable;
import com.cannontech.common.requests.runnable.capcontrol.CapControlRunnableFactory;
import com.cannontech.common.requests.service.JobManagementService;
import com.cannontech.common.token.Token;
import com.cannontech.common.token.TokenType;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class CapControlAddRequestEndpoint {
    private static final Logger log = YukonLogManager.getLogger(CapControlAddRequestEndpoint.class);
    
    private final static Namespace ns = YukonXml.getYukonNamespace();
    
    @Autowired private JobManagementService jobManagementService;
    @Autowired private CapControlRunnableFactory capControlRunnableFactory;
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="capControlAddRequest")
    public Element invoke(Element capControlAddRequest, LiteYukonUser user) {
        log.debug("cap control import job request received");
        
        XmlVersionUtils.verifyYukonMessageVersion(capControlAddRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        Element response = new Element("capControlAddResponse", ns);
        response.setAttribute("version", XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        YukonJobRunnable runnable = capControlRunnableFactory.createRunnable(ImportAction.ADD, capControlAddRequest);
        
        // We're all parsed, time to schedule a job!
        Token token = jobManagementService.createJob(TokenType.CAP_CONTROL_IMPORT, runnable);
        
        Element tokenElem = new Element("token", ns);
        tokenElem.setAttribute("value", token.getString());
        response.addContent(tokenElem);
        
        log.debug("returning response");
        return response;
    }
}
