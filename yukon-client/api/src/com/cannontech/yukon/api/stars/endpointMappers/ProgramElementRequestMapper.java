package com.cannontech.yukon.api.stars.endpointMappers;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jdom.Namespace;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;

public class ProgramElementRequestMapper implements ObjectMapper<Element, EnrollmentHelper> {

    @Override
    public EnrollmentHelper map(Element enrollmentRequestElement) throws ObjectMappingException {
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(enrollmentRequestElement);
        
        EnrollmentHelper enrollmentHelper = new EnrollmentHelper();
        enrollmentHelper.setAccountNumber(template.evaluateAsString("//y:accountNumber"));
        enrollmentHelper.setSerialNumber(template.evaluateAsString("//y:serialNumber"));
        enrollmentHelper.setProgramName(template.evaluateAsString("//y:loadProgramName"));
        enrollmentHelper.setLoadGroupName(template.evaluateAsString("//y:loadGroupName"));
        enrollmentHelper.setApplianceCategoryName(template.evaluateAsString("//y:applianceCategoryName"));
        String applianceKWStr = template.evaluateAsString("//y:appliancekW");
        if (!StringUtils.isBlank(applianceKWStr)) {
            enrollmentHelper.setApplianceKW(Float.valueOf(applianceKWStr));
        }
        enrollmentHelper.setRelay(template.evaluateAsString("//y:appliancekW"));
        enrollmentHelper.setSeasonalLoad(template.evaluateAsBoolean("//y:seasonalLoad"));
        
        return enrollmentHelper;

    }

    public static Element buildElement(Namespace ns,
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

        if (enrollmentHelper.getSerialNumber() != null)
            programEnrollmentElement.addContent(XmlUtils.createStringElement("seasonalLoad", 
                                                                             ns, 
                                                                             enrollmentHelper.getSerialNumber()));
        return programEnrollmentElement;
    }

}