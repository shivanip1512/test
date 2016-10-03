package com.cannontech.yukon.api.loadManagement.endpoint;

import java.util.List;
import java.util.Set;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.xml.xpath.XPathException;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.LoadControlService;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.loadcontrol.service.data.ProgramStatusEnum;
import com.cannontech.message.util.BadServerResponseException;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class ListAllProgramsByStatusRequestEndpoint {

    @Autowired private LoadControlService loadControlService;
    @Autowired private RolePropertyDao rolePropertyDao;

    private Namespace ns = YukonXml.getYukonNamespace();

    @PayloadRoot(namespace = "http://yukon.cannontech.com/api", localPart = "listAllProgramsByStatusRequest")
    public Element invoke(Element listAllProgramsByStatusRequest, LiteYukonUser user) throws Exception {

        XmlVersionUtils.verifyYukonMessageVersion(listAllProgramsByStatusRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        // create template and parse data
        YukonXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(listAllProgramsByStatusRequest);
        Element programStatuses = new Element("programStatuses", ns);
        Element resp = new Element("listAllProgramsByStatusResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);

        try {
            
            Set<ProgramStatusEnum> programStatusEnums = requestTemplate.evaluateAsEnumSet("//y:programStatus", ProgramStatusEnum.class);
            // Check authorization
            rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_WS_LM_DATA_ACCESS, user);

            List<ProgramStatus> allProgramStatus = loadControlService.getAllProgramStatus(user, programStatusEnums);
           
                for (ProgramStatus programStatus : allProgramStatus) {

                    Element programStatusElement = new Element("programStatus", ns);
                    programStatusElement.addContent(XmlUtils.createStringElement("programName", ns, programStatus.getProgramName()));

                    programStatusElement.addContent(XmlUtils.createStringElement("currentStatus", ns, ProgramStatusEnum.getStatus(programStatus)));

                    if (programStatus.getStartTime() != null) {
                        programStatusElement.addContent(XmlUtils.createDateElement("startDateTime", ns, programStatus.getStartTime()));
                    }
                    if (programStatus.getStopTime() != null) {
                        programStatusElement.addContent(XmlUtils.createDateElement("stopDateTime", ns, programStatus.getStopTime()));
                    }
                    programStatusElement.addContent(XmlUtils.createStringElement("gearName", ns, programStatus.getGearName()));
                    programStatuses.addContent(programStatusElement);
            }
        } catch (NotAuthorizedException e) {
            programStatuses = XMLFailureGenerator.generateFailure(listAllProgramsByStatusRequest, e, "UserNotAuthorized",
                    "The user is not authorized to request programs.");
        } catch (XPathException e) {
            Element fe = XMLFailureGenerator.generateFailure(listAllProgramsByStatusRequest, e, "InvalidProgramStatus", e.getMessage());
            resp.addContent(fe);
            return resp;
        } catch (BadServerResponseException e) {
            Element fe = XMLFailureGenerator.generateFailure(listAllProgramsByStatusRequest, e, "ServerCommunicationError", e.getMessage());
            resp.addContent(fe);
            return resp;
        } catch (ConnectionException e) {
            Element fe = XMLFailureGenerator.generateFailure(listAllProgramsByStatusRequest, e, "ServerCommunicationError", e.getMessage());
            resp.addContent(fe);
            return resp;
        }
        resp.addContent(programStatuses);
        return resp;
    }
}
