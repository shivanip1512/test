package com.cannontech.yukon.api.loadManagement.endpoint;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.joda.time.Duration;
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
import com.cannontech.core.dao.ProgramNotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.program.service.ConstraintContainer;
import com.cannontech.dr.program.service.ConstraintViolations;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.loadcontrol.dao.LoadControlProgramDao;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirect;
import com.cannontech.message.util.BadServerResponseException;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class ProgramStartRequestEndpoint {

    private LoadControlProgramDao loadControlProgramDao;
    private ProgramService programService;
    private RolePropertyDao rolePropertyDao;
    
    private Logger log = YukonLogManager.getLogger(ProgramStartRequestEndpoint.class);
    
    private Namespace ns = YukonXml.getYukonNamespace();
    private String programNameExpressionStr = "/y:programStartRequest/y:programName";
    private String startTimeExpressionStr = "/y:programStartRequest/y:startDateTime";
    private String stopTimeExpressionStr = "/y:programStartRequest/y:stopDateTime";
    private String gearNameExpressionStr = "/y:programStartRequest/y:gearName";
    
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
            boolean stopScheduled = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.SCHEDULE_STOP_CHECKED_BY_DEFAULT, user);
        	boolean overrideConstraints = false;
        	
            int programId;
            try {
                programId = loadControlProgramDao.getProgramIdByProgramName(programName);
            } catch (NotFoundException e) {
                throw new ProgramNotFoundException(e.getMessage(), e);
            }

            int gearNumber;
            if (gearName != null) {
                try {
                    gearNumber = loadControlProgramDao.getGearNumberForGearName(programId, gearName);
                } catch (NotFoundException e) {
                    throw new GearNotFoundException(e.getMessage(), e);
                }
            } else {
                LMProgramBase program = programService.getProgramSafe(programId);
                gearNumber = ((LMProgramDirect)program).getCurrentGearNumber();
            }

            ConstraintViolations checkViolations = programService.getConstraintViolationForStartProgram(programId, gearNumber, startTime, Duration.ZERO, stopTime, Duration.ZERO, null);
            if (checkViolations.isViolated()) {
                for (ConstraintContainer violation : checkViolations.getConstraintContainers()) {
                    log.info("Constraint Violation: " + violation.toString() + " for request");
                }
            } else {
                log.info("No constraint violations for request.");
                programService.startProgramBlocking(programId, gearNumber, startTime, Duration.ZERO, stopScheduled, stopTime, Duration.ZERO, overrideConstraints, null);
            }
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
    
    @Autowired
    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setLoadControlProgramDao(LoadControlProgramDao loadControlProgramDao) {
        this.loadControlProgramDao = loadControlProgramDao;
    }
}