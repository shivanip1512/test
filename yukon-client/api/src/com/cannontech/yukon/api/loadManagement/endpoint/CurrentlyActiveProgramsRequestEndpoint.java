package com.cannontech.yukon.api.loadManagement.endpoint;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.LoadControlService;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.message.util.BadServerResponseException;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class CurrentlyActiveProgramsRequestEndpoint {

    private LoadControlService loadControlService;
    private RolePropertyDao rolePropertyDao;

    private Namespace ns = YukonXml.getYukonNamespace();

    @PostConstruct
    public void initialize() throws JDOMException {
    }

    @PayloadRoot(namespace = "http://yukon.cannontech.com/api", localPart = "currentlyActiveProgramsRequest")
    public Element invoke(Element currentlyActiveProgramsRequest, LiteYukonUser user) throws Exception {

        XmlVersionUtils.verifyYukonMessageVersion(currentlyActiveProgramsRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);

        // init response
        Element resp = new Element("currentlyActiveProgramsResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);

        // build response
        Element programStatuses = new Element("programStatuses", ns);

        // run service
        try {

            // Check authorization
            rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_WS_LM_DATA_ACCESS, user);

            List<ProgramStatus> allCurrentlyActivePrograms = loadControlService.getAllCurrentlyActivePrograms(user);

            for (ProgramStatus programStatus : allCurrentlyActivePrograms) {

                Element programStatusElement = new Element("programStatus", ns);
                programStatusElement.addContent(XmlUtils.createStringElement("programName", ns,
                    programStatus.getProgramName()));
                if (programStatus.isActive()) {
                    programStatusElement.addContent(XmlUtils.createStringElement("currentStatus", ns, "Active"));
                } else {
                    programStatusElement.addContent(XmlUtils.createStringElement("currentStatus", ns, "Inactive"));
                }
                programStatusElement.addContent(XmlUtils.createDateElement("startDateTime", ns,
                    programStatus.getStartTime()));
                if (programStatus.getStopTime() != null) {
                    programStatusElement.addContent(XmlUtils.createDateElement("stopDateTime", ns,
                        programStatus.getStopTime()));
                }
                programStatusElement.addContent(XmlUtils.createStringElement("gearName", ns,
                    programStatus.getGearName()));

                programStatuses.addContent(programStatusElement);
            }

        } catch (NotAuthorizedException e) {
            programStatuses =
                XMLFailureGenerator.generateFailure(currentlyActiveProgramsRequest, e, "UserNotAuthorized",
                    "The user is not authorized to request currently active programs.");
        } catch (BadServerResponseException e) {
            Element fe =
                XMLFailureGenerator.generateFailure(currentlyActiveProgramsRequest, e, "ServerCommunicationError",
                    e.getMessage());
            resp.addContent(fe);
            return resp;
        } catch (ConnectionException e) {
            Element fe =
                XMLFailureGenerator.generateFailure(currentlyActiveProgramsRequest, e, "ServerCommunicationError",
                    e.getMessage());
            resp.addContent(fe);
            return resp;
        }

        resp.addContent(programStatuses);

        return resp;
    }

    @Autowired
    public void setLoadControlService(LoadControlService loadControlService) {
        this.loadControlService = loadControlService;
    }

    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }

}