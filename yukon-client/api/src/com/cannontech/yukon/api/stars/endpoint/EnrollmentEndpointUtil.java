package com.cannontech.yukon.api.stars.endpoint;

import org.jdom2.Element;
import org.jdom2.Namespace;

import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;

public class EnrollmentEndpointUtil {

    public static Element buildEnrollmentResponse(Namespace ns,
                                                  Element enrollmentResponseBase, 
                                                  EnrollmentHelper enrollmentHelper){

        Element programEnrollmentResult = new Element("programEnrollmentResult", ns);
        enrollmentResponseBase.addContent(programEnrollmentResult);

        programEnrollmentResult.addContent(XmlUtils.createStringElement("accountNumber", ns, enrollmentHelper.getAccountNumber()));
        programEnrollmentResult.addContent(XmlUtils.createStringElement("serialNumber", ns, enrollmentHelper.getSerialNumber()));
        programEnrollmentResult.addContent(XmlUtils.createStringElement("loadProgramName", ns, enrollmentHelper.getProgramName()));
        programEnrollmentResult.addContent(XmlUtils.createStringElement("loadGroupName", ns, enrollmentHelper.getLoadGroupName()));
        return programEnrollmentResult;
    }
}