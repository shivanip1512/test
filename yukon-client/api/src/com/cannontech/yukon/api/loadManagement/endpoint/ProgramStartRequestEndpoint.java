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
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.GearNotFoundException;
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
public class ProgramStartRequestEndpoint {

    @Autowired private LoadControlProgramDao loadControlProgramDao;
    @Autowired private ProgramService programService;
    @Autowired private RolePropertyDao rolePropertyDao;

    private final Logger log = YukonLogManager.getLogger(ProgramStartRequestEndpoint.class);

    private final Namespace ns = YukonXml.getYukonNamespace();
    private final String programNameExpressionStr = "/y:programStartRequest/y:programName";
    private final String startTimeExpressionStr = "/y:programStartRequest/y:startDateTime";
    private final String stopTimeExpressionStr = "/y:programStartRequest/y:stopDateTime";
    private final String gearNameExpressionStr = "/y:programStartRequest/y:gearName";

    @PostConstruct
    public void initialize() throws JDOMException {
    }

    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="programStartRequest")
    public Element invoke(Element programStartRequest, LiteYukonUser user) throws Exception {

        XmlVersionUtils.verifyYukonMessageVersion(programStartRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);

        // create template and parse data
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(programStartRequest);
        
        String programName = requestTemplate.evaluateAsString(programNameExpressionStr);
        Date startTime = requestTemplate.evaluateAsDate(startTimeExpressionStr);
        Date stopTime = requestTemplate.evaluateAsDate(stopTimeExpressionStr);
        String gearName = requestTemplate.evaluateAsString(gearNameExpressionStr);
        
        // init response
        Element resp = new Element("programStartResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // run service
        try {
            // Check authorization
            rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_WS_LM_CONTROL_ACCESS, user);
            boolean overrideConstraints = false;
            boolean observeConstaints = true;
            int programId = loadControlProgramDao.getProgramIdByProgramName(programName);
            programService.startProgram(programId, startTime, stopTime, gearName, overrideConstraints, observeConstaints, user, ProgramOriginSource.EIM);
        } catch (GearNotFoundException e) {
            Element fe = XMLFailureGenerator.generateFailure(programStartRequest, e, "InvalidGearName", "No gear named: " + gearName);
            resp.addContent(fe);
            return resp;
        } catch (NotFoundException e) {
            // the startControlByProgramName() without gearName simply throws a NotFoundException when program is not found
            Element fe = XMLFailureGenerator.generateFailure(programStartRequest, e, "InvalidProgramName", "No program named: " + programName);
            resp.addContent(fe);
            return resp;
        } catch (TimeoutException e) {
            Element fe = XMLFailureGenerator.generateFailure(programStartRequest, e, "Timeout", "Timeout wating for program update response.");
            resp.addContent(fe);
            return resp;
        } catch (NotAuthorizedException e) {
            Element fe = XMLFailureGenerator.generateFailure(programStartRequest, e, "UserNotAuthorized", "The user is not authorized to start program.");
            resp.addContent(fe);
            return resp;
        } catch (BadServerResponseException | ConnectionException e) {
            Element fe = XMLFailureGenerator.generateFailure(programStartRequest, e, "ServerCommunicationError", e.getMessage());
            resp.addContent(fe);
            return resp;
        } catch (Exception e) {
            Element fe = XMLFailureGenerator.generateFailure(programStartRequest, e, "OtherException", "An exception has been caught.");
            resp.addContent(fe);
            log.error(e.getMessage(), e);
        }
        
        // build response
        resp.addContent(XmlUtils.createStringElement("success", ns, ""));
        
        return resp;
    }
}