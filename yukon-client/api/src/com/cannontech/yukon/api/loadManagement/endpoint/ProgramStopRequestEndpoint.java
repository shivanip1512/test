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
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
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
public class ProgramStopRequestEndpoint {

    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ProgramService programService;
    @Autowired private LoadControlProgramDao loadControlProgramDao;
    
    private final Namespace ns = YukonXml.getYukonNamespace();
    private final String programNameExpressionStr = "/y:programStopRequest/y:programName";
    private final String stopTimeExpressionStr = "/y:programStopRequest/y:stopDateTime";
    
    private static Logger log = YukonLogManager.getLogger(ProgramStopRequestEndpoint.class);
    
    @PostConstruct
    public void initialize() throws JDOMException {
    }
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="programStopRequest")
    public Element invoke(Element programStopRequest, LiteYukonUser user) throws Exception {

        XmlVersionUtils.verifyYukonMessageVersion(programStopRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);

        // create template and parse data
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(programStopRequest);

        String programName = requestTemplate.evaluateAsString(programNameExpressionStr);
        Date stopTime = requestTemplate.evaluateAsDate(stopTimeExpressionStr);
        if (stopTime == null) {
            stopTime = CtiUtilities.get1990GregCalendar().getTime();
        }

        // init response
        Element resp = new Element("programStopResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);

        // run service
        try {
            // Check authorization
            rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_WS_LM_CONTROL_ACCESS, user);
            boolean overrideConstraints = false;
            boolean observeConstraints = true;
            int programId = loadControlProgramDao.getProgramIdByProgramName(programName);
            programService.stopProgram(programId, stopTime, overrideConstraints, observeConstraints, ProgramOriginSource.EIM);
        } catch (NotFoundException e) {
            Element fe = XMLFailureGenerator.generateFailure(programStopRequest, e, "InvalidProgramName", "No program named: " + programName);
            resp.addContent(fe);
            return resp;
        } catch (TimeoutException e) {
            Element fe = XMLFailureGenerator.generateFailure(programStopRequest, e, "Timeout", "Timeout wating for program update response.");
            resp.addContent(fe);
            return resp;
        } catch (NotAuthorizedException e) {
            Element fe = XMLFailureGenerator.generateFailure(programStopRequest, e, "UserNotAuthorized", "The user is not authorized to stop program.");
            resp.addContent(fe);
            return resp;
        } catch (BadServerResponseException e) {
            Element fe = XMLFailureGenerator.generateFailure(programStopRequest, e, "ServerCommunicationError", e.getMessage());
            resp.addContent(fe);
            return resp;
        } catch (ConnectionException e) {
            Element fe = XMLFailureGenerator.generateFailure(programStopRequest, e, "ServerCommunicationError", e.getMessage());
            resp.addContent(fe);
            return resp;
        } catch (Exception e) {
            Element fe = XMLFailureGenerator.generateFailure(programStopRequest, e, "OtherException", "An exception has been caught.");
            resp.addContent(fe);
            log.error(e.getMessage(), e);
        }
        
        // build response
        resp.addContent(XmlUtils.createStringElement("success", ns, ""));
        
        return resp;
    }
}