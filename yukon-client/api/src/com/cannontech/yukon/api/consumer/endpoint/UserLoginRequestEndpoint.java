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
import com.cannontech.common.exception.PasswordExpiredException;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.api.stars.endpoint.RunThermostatProgramEndpoint;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class UserLoginRequestEndpoint {
    private Namespace ns = YukonXml.getYukonNamespace();
    private Logger log = YukonLogManager.getLogger(RunThermostatProgramEndpoint.class);
    
    @Autowired private AuthenticationService authenticationService;
    @Autowired private RolePropertyDao rolePropertyDao;
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="userLoginRequest")
    public Element invoke(Element userLoginRequest) throws Exception {
        XmlVersionUtils.verifyYukonMessageVersion(userLoginRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(userLoginRequest);
        
        //build response
        Element response = new Element("userLoginResponse", ns);
        XmlVersionUtils.addVersionAttribute(response, XmlVersionUtils.YUKON_MSG_VERSION_1_0);

        try{
            //get their credentials
            String username = requestTemplate.evaluateAsString("//y:username");
            String password = requestTemplate.evaluateAsString("//y:password");
            
            LiteYukonUser user = authenticationService.login(username, password);
            //let the callee know how long a session should last
            response.addContent(XmlUtils.createIntegerElement("sessionTimeoutLength", ns, rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.SESSION_TIMEOUT, user)));

            //success!
            response.addContent(new Element("success", ns));
        } catch (PasswordExpiredException e) {
            Element fe = XMLFailureGenerator.generateFailure(userLoginRequest, e, "PasswordExpired", e.getMessage());
            response.addContent(fe);
        } catch (BadAuthenticationException e) {
            Element fe = XMLFailureGenerator.generateFailure(userLoginRequest, e, "UserNotAuthenticated", e.getMessage());
            response.addContent(fe);
        }  catch (Exception e) {
            Element fe = XMLFailureGenerator.generateFailure(userLoginRequest, e, "OtherException", e.getMessage());
            response.addContent(fe);
            log.error(e.getMessage(), e);
        }

        return response;
    }
}
