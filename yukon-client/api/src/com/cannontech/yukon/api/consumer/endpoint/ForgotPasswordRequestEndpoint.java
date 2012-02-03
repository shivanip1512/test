package com.cannontech.yukon.api.consumer.endpoint;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.model.RequestPword;
import com.cannontech.stars.core.model.StarsRequestPword;
import com.cannontech.yukon.api.stars.endpoint.RunThermostatProgramEndpoint;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class ForgotPasswordRequestEndpoint {
    private Namespace ns = YukonXml.getYukonNamespace();
    private Logger log = YukonLogManager.getLogger(RunThermostatProgramEndpoint.class);
    
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private StarsCustAccountInformationDao starsCustAccountInformationDao;

    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="forgotPasswordRequest")
    public Element invoke(Element forgotPasswordRequest) throws Exception {
        XmlVersionUtils.verifyYukonMessageVersion(forgotPasswordRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(forgotPasswordRequest);
        
        //build response
        Element response = new Element("forgotPasswordResponse", ns);
        response.setAttribute(new Attribute("version", "1.0"));
        
        try {
            //see if resetting the password even makes sense.  this assumes that the authentication
            //for the user is done through the corresponding UserLoginRequest webservice
            if(!rolePropertyDao.checkProperty(YukonRoleProperty.ENABLE_PASSWORD_RECOVERY, null)){
                throw new NotAuthorizedException("Password recovery is not available.");
            }
            
            String userName = requestTemplate.evaluateAsString("//y:username");
            String email = requestTemplate.evaluateAsString("//y:email");
            String fName = requestTemplate.evaluateAsString("//y:firstName");
            String lName = requestTemplate.evaluateAsString("//y:lastName");
            String accNum = requestTemplate.evaluateAsString("//y:accountNumber");      
            String notes = requestTemplate.evaluateAsString("//y:notes");
            String energyComp = requestTemplate.evaluateAsString("//y:energyProvider");
            
            //try to recover the password
            StarsRequestPword reqPword = new StarsRequestPword(userName, email, fName, lName, accNum, starsCustAccountInformationDao);
            reqPword.setNotes(notes);
            reqPword.setEnergyCompany(energyComp);
            
            if(!reqPword.isValidParams()){
                //not enough info, return error
                throw new NotFoundException("Not enough information.");
            }else{
                //do the work
                reqPword.doRequest();
                if(reqPword.getState() == RequestPword.RET_SUCCESS){
                    response.addContent(XmlUtils.createStringElement("success", ns, ""));
                }else{
                    throw new NotFoundException(reqPword.getResultString());
                }
            }
        }catch(NotAuthorizedException e){
            response.addContent(XMLFailureGenerator.generateFailure(forgotPasswordRequest, e, "NotAuthorized", e.getMessage()));
        }catch(NotFoundException e){
            response.addContent(XMLFailureGenerator.generateFailure(forgotPasswordRequest, e, "NotFound", e.getMessage()));
        }catch(Exception e){
            Element fe = XMLFailureGenerator.generateFailure(forgotPasswordRequest, e, "OtherException", e.getMessage());
            response.addContent(fe);
            log.error(e.getMessage(), e);
        }
        
        return response;
    }
}
