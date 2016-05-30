package com.cannontech.yukon.api.consumer.endpoint;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.model.RequestPword;
import com.cannontech.stars.core.model.StarsRequestPword;
import com.cannontech.stars.core.service.StarsSearchService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.yukon.api.stars.endpoint.RunThermostatProgramEndpoint;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class ForgotPasswordRequestEndpoint {
    private final Namespace ns = YukonXml.getYukonNamespace();
    private final Logger log = YukonLogManager.getLogger(RunThermostatProgramEndpoint.class);

    @Autowired private SystemEventLogService systemEventLogService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private StarsCustAccountInformationDao starsCustAccountInformationDao;
    @Autowired private StarsSearchService starsSearchService;

    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="forgotPasswordRequest")
    public Element invoke(Element forgotPasswordRequest) throws Exception {
        XmlVersionUtils.verifyYukonMessageVersion(forgotPasswordRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(forgotPasswordRequest);

        //build response
        Element response = new Element("forgotPasswordResponse", ns);
        XmlVersionUtils.addVersionAttribute(response, XmlVersionUtils.YUKON_MSG_VERSION_1_0);

        try {
            
            String userName = requestTemplate.evaluateAsString("//y:username");
            String email = requestTemplate.evaluateAsString("//y:email");
            String fName = requestTemplate.evaluateAsString("//y:firstName");
            String lName = requestTemplate.evaluateAsString("//y:lastName");
            String accNum = requestTemplate.evaluateAsString("//y:accountNumber");
            String notes = requestTemplate.evaluateAsString("//y:notes");
            String energyComp = requestTemplate.evaluateAsString("//y:energyProvider");
            
            // Event logging for password request attempted
            systemEventLogService.passwordRequestAttempted(userName, email, accNum, EventSource.API);
            
            //see if resetting the password even makes sense.  this assumes that the authentication
            //for the user is done through the corresponding UserLoginRequest webservice
            if(!globalSettingDao.getBoolean(GlobalSettingType.ENABLE_PASSWORD_RECOVERY)){
                throw new NotAuthorizedException("Password recovery is not available.");
            }            

            //try to recover the password
            StarsRequestPword reqPword = new StarsRequestPword(userName, email, fName, lName, accNum, 
                                                               starsCustAccountInformationDao,
                                                               starsSearchService);
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
