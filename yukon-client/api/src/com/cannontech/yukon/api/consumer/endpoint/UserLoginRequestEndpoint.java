package com.cannontech.yukon.api.consumer.endpoint;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.yukon.api.stars.endpoint.RunThermostatProgramEndpoint;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class UserLoginRequestEndpoint {
    private Namespace ns = YukonXml.getYukonNamespace();
    private Logger log = YukonLogManager.getLogger(RunThermostatProgramEndpoint.class);
    
    @Autowired private AuthenticationService authenticationService;
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="userLoginRequest")
    public Element invoke(Element updateAccountsRequest) throws Exception {
        XmlVersionUtils.verifyYukonMessageVersion(updateAccountsRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(updateAccountsRequest);
        
        //build response
        Element response = new Element("userLoginResponse", ns);
        response.setAttribute(new Attribute("version", "1.0"));

        try{
            //get their credentials
            String username = requestTemplate.evaluateAsString("//y:username");
            String password = requestTemplate.evaluateAsString("//y:password");
            
            authenticationService.login(username, password);
            
        } catch (BadAuthenticationException e) {
            Element fe = XMLFailureGenerator.generateFailure(updateAccountsRequest, e, "UserNotAuthenticated", e.getMessage());
            response.addContent(fe);
            return response;
        }  catch (Exception e) {
            Element fe = XMLFailureGenerator.generateFailure(updateAccountsRequest, e, "OtherException", e.getMessage());
            response.addContent(fe);
            log.error(e.getMessage(), e);
        }
        
        //success!
        response.addContent(XmlUtils.createStringElement("success", ns, ""));
        return response;
    }
}
