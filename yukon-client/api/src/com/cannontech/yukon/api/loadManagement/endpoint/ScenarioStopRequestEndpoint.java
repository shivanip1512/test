package com.cannontech.yukon.api.loadManagement.endpoint;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.model.ProgramOriginSource;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.loadcontrol.dao.LoadControlProgramDao;
import com.cannontech.message.util.BadServerResponseException;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class ScenarioStopRequestEndpoint {

    @Autowired private ProgramService programService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private LoadControlProgramDao loadControlProgramDao;

    private final Namespace ns = YukonXml.getYukonNamespace();
    private final String scenarioNameExpressionStr = "/y:scenarioStopRequest/y:scenarioName";
    private final String stopTimeExpressionStr = "/y:scenarioStopRequest/y:stopDateTime";
    private final String waitForResponseExpressionStr = "/y:scenarioStopRequest/y:waitForResponse";

    private final Logger log = YukonLogManager.getLogger(ScenarioStopRequestEndpoint.class);

    @PostConstruct
    public void initialize() throws JDOMException {
    }
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="scenarioStopRequest")
    public Element invoke(Element scenarioStopRequest, LiteYukonUser user) throws Exception {
        
        XmlVersionUtils.verifyYukonMessageVersion(scenarioStopRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);

        // create template and parse data
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(scenarioStopRequest);
        
        String scenarioName = requestTemplate.evaluateAsString(scenarioNameExpressionStr);
        Date stopTime = requestTemplate.evaluateAsDate(stopTimeExpressionStr);
        Boolean waitForResponse = requestTemplate.evaluateAsBoolean(waitForResponseExpressionStr);

        // init response
        Element resp = new Element("scenarioStopResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // run service
        try {
            
            // Check authorization
            rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_WS_LM_CONTROL_ACCESS, user);
            int scenarioId = loadControlProgramDao.getScenarioIdForScenarioName(scenarioName);

            if (waitForResponse) {
                programService.stopScenarioBlocking(scenarioId, stopTime, false, true, user, ProgramOriginSource.EIM);
            } else {
                programService.stopScenario(scenarioId, stopTime, false, true, user, ProgramOriginSource.EIM);
            }
            // build response
            resp.addContent(new Element("success", ns));
            
        } catch (NotFoundException e) {
            Element fe = XMLFailureGenerator.generateFailure(scenarioStopRequest, e, "InvalidScenarioName", "No scenario named: " + scenarioName);
            resp.addContent(fe);
        } catch (TimeoutException e) {
            Element fe = XMLFailureGenerator.generateFailure(scenarioStopRequest, e, "Timeout", "Timeout wating for program update response.");
            resp.addContent(fe);
        } catch (NotAuthorizedException e) {
            Element fe = XMLFailureGenerator.generateFailure(scenarioStopRequest, e, "UserNotAuthorized", "The user is not authorized to stop scenario.");
            resp.addContent(fe);
        } catch (BadServerResponseException | ConnectionException e) {
            Element fe = XMLFailureGenerator.generateFailure(scenarioStopRequest, e, "ServerCommunicationError", e.getMessage());
            resp.addContent(fe);
        } catch (Exception e) {
            Element fe = XMLFailureGenerator.generateFailure(scenarioStopRequest, e, "OtherException", "An exception has been caught.");
            resp.addContent(fe);
            log.error(e.getMessage(), e);
        }
                
        return resp;
    }
}