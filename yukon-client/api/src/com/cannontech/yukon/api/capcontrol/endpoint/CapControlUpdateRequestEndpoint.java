package com.cannontech.yukon.api.capcontrol.endpoint;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.csvImport.ImportAction;
import com.cannontech.common.requests.runnable.YukonJob;
import com.cannontech.common.requests.runnable.capcontrol.CapControlJobFactory;
import com.cannontech.common.requests.service.JobManagementService;
import com.cannontech.common.token.Token;
import com.cannontech.common.token.TokenType;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class CapControlUpdateRequestEndpoint {
    private static final Logger log = YukonLogManager.getLogger(CapControlUpdateRequestEndpoint.class);
    
    private final static Namespace ns = YukonXml.getYukonNamespace();
    
    @Autowired private JobManagementService jobManagementService;
    @Autowired CapControlJobFactory capControlJobFactory;
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="capControlUpdateRequest")
    public Element invoke(Element capControlUpdateRequest, LiteYukonUser user) {
        log.debug("cap control import job request received");
        
        XmlVersionUtils.verifyYukonMessageVersion(capControlUpdateRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        Element response = new Element("capControlUpdateResponse", ns);
        response.setAttribute("version", XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        YukonJob runnable = capControlJobFactory.createRunnable(ImportAction.UPDATE, capControlUpdateRequest);
        
        Token token = jobManagementService.createJob(TokenType.YUKON_JOB, runnable);
        
        Element tokenElem = new Element("token", ns);
        tokenElem.setAttribute("value", token.getString());
        response.addContent(tokenElem);
        
        log.debug("returning response");
        return response;
    }
}
