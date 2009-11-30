package com.cannontech.yukon.api.stars.endpoint;

import java.util.Date;
import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.dao.AccountNotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.enrollment.model.EnrolledDevicePrograms;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class EnrolledDevicesByAccountNumberRequestEndpoint {

    private Namespace ns = YukonXml.getYukonNamespace();
	private RolePropertyDao rolePropertyDao;
	private EnrollmentHelperService enrollmentHelperService;
	
	private String accountNumberExpressionStr = "/y:enrolledDevicesByAccountNumberRequest/y:accountNumber";
	private String startDateTimeExpressionStr = "/y:enrolledDevicesByAccountNumberRequest/y:startDateTime";
	private String stopDateTimeExpressionStr = "/y:enrolledDevicesByAccountNumberRequest/y:stopDateTime";
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="enrolledDevicesByAccountNumberRequest")
    public Element invoke(Element enrolledDevicesByAccountNumberRequest, LiteYukonUser user) throws Exception {
        
    	XmlVersionUtils.verifyYukonMessageVersion(enrolledDevicesByAccountNumberRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
    	
        SimpleXPathTemplate requestTemplate = XmlUtils.getXPathTemplateForElement(enrolledDevicesByAccountNumberRequest);
        String accountNumber = requestTemplate.evaluateAsString(accountNumberExpressionStr);
        Date startTime = requestTemplate.evaluateAsDate(startDateTimeExpressionStr);
        Date stopTime = requestTemplate.evaluateAsDate(stopDateTimeExpressionStr);
        
    	// init response
        Element resp = new Element("enrolledDevicesByAccountNumberResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // run service
        try {
            
            rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_ACCOUNT_GENERAL, user);
        	
            List<EnrolledDevicePrograms> enrolledDeviceProgramsList = enrollmentHelperService.getEnrolledDeviceProgramsByAccountNumber(accountNumber, startTime, stopTime, user);
            
            Element enrolledDevicesList = new Element("enrolledDevicesList", ns);
            
            for (EnrolledDevicePrograms enrolledDevicePrograms : enrolledDeviceProgramsList) {
            	
            	Element enrolledDevice = new Element("enrolledDevice", ns);
            	
            	// serial number
            	Element serialNumberElement = XmlUtils.createStringElement("serialNumber", ns, enrolledDevicePrograms.getSerialNumber());
            	enrolledDevice.addContent(serialNumberElement);
            	
            	// programs
            	Element programsList = new Element("programsList", ns);
            	for (String programName : enrolledDevicePrograms.getProgramNames()) {
            		
            		Element programNameElement = XmlUtils.createStringElement("programName", ns, programName);
            		programsList.addContent(programNameElement);
            	}
            	enrolledDevice.addContent(programsList);
            	
            	enrolledDevicesList.addContent(enrolledDevice);
            }
            
            resp.addContent(enrolledDevicesList);
            
            
		} catch (NotAuthorizedException e) {
			Element fe = XMLFailureGenerator.generateFailure(enrolledDevicesByAccountNumberRequest, e, "UserNotAuthorized", "The user is not authorized to view enrolled device by account number.");
			resp.addContent(fe);
			return resp;
		} catch (AccountNotFoundException e) {
			Element fe = XMLFailureGenerator.generateFailure(enrolledDevicesByAccountNumberRequest, e, "InvalidAccountNumber", "No account with account number: " + accountNumber);
			resp.addContent(fe);
			return resp;
		}

        return resp;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
    
    @Autowired
    public void setEnrollmentHelperService(EnrollmentHelperService enrollmentHelperService) {
		this.enrollmentHelperService = enrollmentHelperService;
	}
}
