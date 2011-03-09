package com.cannontech.yukon.api.stars.endpoint;

import org.jdom.Element;
import org.jdom.Namespace;

import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;

public class EnrollmentEndpointTestUtil {

    public static Element buildEnrollmentRequest(Namespace ns,
                                                 Element enrollmentRequestBase, 
                                                 EnrollmentHelper enrollmentHelper){

        Element programEnrollmentElement = new Element("programEnrollment", ns); 
        enrollmentRequestBase.addContent(programEnrollmentElement);

        programEnrollmentElement.addContent(XmlUtils.createStringElement("accountNumber", 
                                                                         ns, 
                                                                         enrollmentHelper.getAccountNumber()));
        programEnrollmentElement.addContent(XmlUtils.createStringElement("serialNumber", 
                                                                         ns, 
                                                                         enrollmentHelper.getSerialNumber()));
        programEnrollmentElement.addContent(XmlUtils.createStringElement("loadProgramName", 
                                                                         ns, 
                                                                         enrollmentHelper.getProgramName()));
        programEnrollmentElement.addContent(XmlUtils.createStringElement("loadGroupName", 
                                                                         ns, 
                                                                         enrollmentHelper.getLoadGroupName()));
        if (enrollmentHelper.getApplianceCategoryName() != null)
            programEnrollmentElement.addContent(XmlUtils.createStringElement("applianceCategoryName", 
                                                                             ns, 
                                                                             enrollmentHelper.getApplianceCategoryName()));

        if (enrollmentHelper.getApplianceKW() != null)
            programEnrollmentElement.addContent(XmlUtils.createStringElement("appliancekW", 
                                                                             ns, 
                                                                             enrollmentHelper.getApplianceKW().toString()));

        if (enrollmentHelper.getRelay() != null)
            programEnrollmentElement.addContent(XmlUtils.createStringElement("relay", 
                                                                             ns, 
                                                                             enrollmentHelper.getRelay()));

        programEnrollmentElement.addContent(XmlUtils.createBooleanElement("seasonalLoad", 
                                                                          ns, 
                                                                          enrollmentHelper.isSeasonalLoad()));
        return programEnrollmentElement;
    }
    
    public static Element buildUnenrollmentRequest(Namespace ns,
            Element enrollmentRequestBase, 
            EnrollmentHelper enrollmentHelper){

		Element programEnrollmentElement = new Element("programUnenrollment", ns); 
		enrollmentRequestBase.addContent(programEnrollmentElement);
		
		programEnrollmentElement.addContent(XmlUtils.createStringElement("accountNumber", 
		                                    ns, 
		                                    enrollmentHelper.getAccountNumber()));
		programEnrollmentElement.addContent(XmlUtils.createStringElement("serialNumber", 
		                                    ns, 
		                                    enrollmentHelper.getSerialNumber()));
		programEnrollmentElement.addContent(XmlUtils.createStringElement("loadProgramName", 
		                                    ns, 
		                                    enrollmentHelper.getProgramName()));
		programEnrollmentElement.addContent(XmlUtils.createStringElement("loadGroupName", 
		                                    ns, 
		                                    enrollmentHelper.getLoadGroupName()));
		if (enrollmentHelper.getApplianceCategoryName() != null)
		programEnrollmentElement.addContent(XmlUtils.createStringElement("applianceCategoryName", 
		                                        ns, 
		                                        enrollmentHelper.getApplianceCategoryName()));
		
		if (enrollmentHelper.getApplianceKW() != null)
		programEnrollmentElement.addContent(XmlUtils.createStringElement("appliancekW", 
		                                        ns, 
		                                        enrollmentHelper.getApplianceKW().toString()));
		
		if (enrollmentHelper.getRelay() != null)
		programEnrollmentElement.addContent(XmlUtils.createStringElement("relay", 
		                                        ns, 
		                                        enrollmentHelper.getRelay()));
		
		programEnrollmentElement.addContent(XmlUtils.createBooleanElement("seasonalLoad", 
		                                     ns, 
		                                     enrollmentHelper.isSeasonalLoad()));
		return programEnrollmentElement;
	}

}