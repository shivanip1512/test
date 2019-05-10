package com.cannontech.yukon.api.stars.endpoint;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.AccountNotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.enrollment.model.EnrolledDevicePrograms;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class EnrolledDevicesByAccountNumberRequestEndpoint {

    private Namespace ns = YukonXml.getYukonNamespace();
    private EnrollmentHelperService enrollmentHelperService;

    private String accountNumberExpressionStr = "/y:enrolledDevicesByAccountNumberRequest/y:accountNumber";
    private String startDateTimeExpressionStr = "/y:enrolledDevicesByAccountNumberRequest/y:startDateTime";
    private String stopDateTimeExpressionStr = "/y:enrolledDevicesByAccountNumberRequest/y:stopDateTime";

    @PayloadRoot(namespace = "http://yukon.cannontech.com/api", localPart = "enrolledDevicesByAccountNumberRequest")
    public Element invoke(Element enrolledDevicesByAccountNumberRequest, LiteYukonUser user) throws Exception {

        XmlVersionUtils.verifyYukonMessageVersion(enrolledDevicesByAccountNumberRequest,
            XmlVersionUtils.YUKON_MSG_VERSION_1_0, XmlVersionUtils.YUKON_MSG_VERSION_1_1);
        String version = XmlVersionUtils.getYukonMessageVersion(enrolledDevicesByAccountNumberRequest);

        SimpleXPathTemplate requestTemplate =
            YukonXml.getXPathTemplateForElement(enrolledDevicesByAccountNumberRequest);
        String accountNumber = requestTemplate.evaluateAsString(accountNumberExpressionStr);
        Date startTime = requestTemplate.evaluateAsDate(startDateTimeExpressionStr);
        Date stopTime = requestTemplate.evaluateAsDate(stopDateTimeExpressionStr);

        // init response
        Element resp = new Element("enrolledDevicesByAccountNumberResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, version);

        // run service
        try {

            List<EnrolledDevicePrograms> enrolledDeviceProgramsList =
                enrollmentHelperService.getEnrolledDeviceProgramsByAccountNumber(accountNumber, startTime, stopTime,
                    user);
            if (XmlVersionUtils.YUKON_MSG_VERSION_1_0.equals(version)) {
                resp.addContent(buildEnrolledDevicesWithoutRelayElement(enrolledDeviceProgramsList));
            } else if (XmlVersionUtils.YUKON_MSG_VERSION_1_1.equals(version)) {
                resp.addContent(buildEnrolledDevicesWithRelayElement(enrolledDeviceProgramsList));
            } else {
                throw new IllegalArgumentException("Invalid request version: " + version);
            }

        } catch (NotAuthorizedException e) {
            Element fe = XMLFailureGenerator.generateFailure(enrolledDevicesByAccountNumberRequest, e,
                "UserNotAuthorized", "The user is not authorized to view enrolled device by account number.");
            resp.addContent(fe);
            return resp;
        } catch (AccountNotFoundException e) {
            Element fe = XMLFailureGenerator.generateFailure(enrolledDevicesByAccountNumberRequest, e,
                "InvalidAccountNumber", "No account with account number: " + accountNumber);
            resp.addContent(fe);
            return resp;
        }

        return resp;
    }

    /**
     * Build enrolled devices with Relay element.
     * This method is for building elements for enrolled devices having program enrolled
     * with Relay Element.
     */
    private Element buildEnrolledDevicesWithRelayElement(List<EnrolledDevicePrograms> enrolledDeviceProgramsList) {
        // Enrolled Device List
        Element enrolledDevicesList = new Element("enrolledDevicesList", ns);
        for (EnrolledDevicePrograms enrolledDeviceProgram : enrolledDeviceProgramsList) {

            Element enrolledDevice = new Element("enrolledDevice", ns);
            // serial number
            Element serialNumberElement =
                XmlUtils.createStringElement("serialNumber", ns, enrolledDeviceProgram.getSerialNumber());
            enrolledDevice.addContent(serialNumberElement);

            // programs
            Element programsList = new Element("programsList", ns);

            Map<String, Integer> enrolledDevicesPrograms = enrolledDeviceProgram.getEnrolledDevicePrograms();
            for (String programName : enrolledDevicesPrograms.keySet()) {
                // Program
                Element program = new Element("program", ns);
                Element programNameElement = XmlUtils.createStringElement("programName", ns, programName);
                Element relayElement =
                    XmlUtils.createIntegerElement("relay", ns, enrolledDevicesPrograms.get(programName));
                program.addContent(programNameElement);
                program.addContent(relayElement);
                programsList.addContent(program);
            }

            enrolledDevice.addContent(programsList);
            enrolledDevicesList.addContent(enrolledDevice);
        }

        return enrolledDevicesList;
    }

    /**
     * Build enrolled devices without Relay element.
     * This method is for building elements for enrolled devices having program enrolled
     * with out having Relay Element in response.
     */
    private Element buildEnrolledDevicesWithoutRelayElement(List<EnrolledDevicePrograms> enrolledDeviceProgramsList) {

        Element enrolledDevicesList = new Element("enrolledDevicesList", ns);

        for (EnrolledDevicePrograms enrolledDevicePrograms : enrolledDeviceProgramsList) {

            Element enrolledDevice = new Element("enrolledDevice", ns);
            // serial number
            Element serialNumberElement =
                XmlUtils.createStringElement("serialNumber", ns, enrolledDevicePrograms.getSerialNumber());
            enrolledDevice.addContent(serialNumberElement);

            // programs
            Element programsList = new Element("programsList", ns);
            Map<String, Integer> enrolledDevicesProgram = enrolledDevicePrograms.getEnrolledDevicePrograms();
            for (String programName : enrolledDevicesProgram.keySet()) {
                Element programNameElement = XmlUtils.createStringElement("programName", ns, programName);
                programsList.addContent(programNameElement);
            }
            enrolledDevice.addContent(programsList);
            enrolledDevicesList.addContent(enrolledDevice);
        }

        return enrolledDevicesList;
    }

    @Autowired
    public void setEnrollmentHelperService(EnrollmentHelperService enrollmentHelperService) {
		this.enrollmentHelperService = enrollmentHelperService;
	}
}
