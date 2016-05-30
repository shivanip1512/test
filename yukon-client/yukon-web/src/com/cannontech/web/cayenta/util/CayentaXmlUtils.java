package com.cannontech.web.cayenta.util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.xml.xpath.XPathException;
import org.w3c.dom.Node;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.web.cayenta.model.CayentaLocationInfo;
import com.cannontech.web.cayenta.model.CayentaMeterInfo;
import com.cannontech.web.cayenta.model.CayentaPhoneInfo;

public class CayentaXmlUtils {

	private static Logger log = YukonLogManager.getLogger(CayentaXmlUtils.class);
	private static XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
	
	/**
	 * Get the method name from the request or reply.
	 * The method name will always be the name of the first element in the request/reply.
	 * @param element The root element of the request/reply.
	 * @return
	 * @throws IOException
	 * @throws JDOMException
	 * @throws CayentaRequestException
	 */
	@SuppressWarnings("unchecked")
	public static String getMethodName(Element element) throws CayentaRequestException  {
		
		String requestName = null;
		List<Element> children = element.getChildren();
		if (children.size() > 0) {
			Element requestTypeElement = children.get(0);
			requestName = requestTypeElement.getName();
		} else {
			log.debug("Element does not contain a method name: " + xmlOutputter.outputString(element));
			throw new CayentaRequestException("Unable to determine reply type.");
		}
		
		return requestName;
	}
	
	// REPLY STATUS
	public static int getReplyStatusValue(String replyTypeName, Element replyElement) throws CayentaRequestException  {
		
		SimpleXPathTemplate replyTemplate = YukonXml.getXPathTemplateForElement(replyElement);
		String expression = "/Reply/" + replyTypeName + "/STATUS";
        Long requestStatus = replyTemplate.evaluateAsLong(expression);
        if (requestStatus == null) {
        	log.debug("Reply does not contain a value for status: " + xmlOutputter.outputString(replyElement) + " replyTypeName: " + replyTypeName);
			throw new CayentaRequestException("Reply does not contain a value for status.");
        }
		return requestStatus.intValue();
	}
	
	public static String getReplyStatusDescription(String replyTypeName, Element replyElement) throws CayentaRequestException {
		
		SimpleXPathTemplate replyTemplate = YukonXml.getXPathTemplateForElement(replyElement);
		String expression = "/Reply/" + replyTypeName + "/STATUS_DESC";
        String requestStatusDesc = replyTemplate.evaluateAsString(expression);
        if (requestStatusDesc == null) {
        	log.debug("Reply does not contain a value for system status description: " + xmlOutputter.outputString(replyElement) + " replyTypeName: " + replyTypeName);
			throw new CayentaRequestException("Reply does not contain a value for system status description.");
        }
		return requestStatusDesc;
	}
	
	// FUNCTION STATUS
	public static int getReplyFunctionStatusValue(String replyTypeName, Element replyElement) throws CayentaRequestException  {
		
		SimpleXPathTemplate replyTemplate = YukonXml.getXPathTemplateForElement(replyElement);
		String expression = "/Reply/" + replyTypeName + "/Params/STATUS";
        Long functionStatus = replyTemplate.evaluateAsLong(expression);
        if (functionStatus == null) {
        	log.debug("Reply does not contain a value for function status: " + xmlOutputter.outputString(replyElement) + " replyTypeName: " + replyTypeName);
			throw new CayentaRequestException("Reply does not contain a value for function status.");
        }
		return functionStatus.intValue();
	}
	
	public static String getReplyFunctionStatusDescription(String replyTypeName, Element replyElement) throws CayentaRequestException {
		
		SimpleXPathTemplate replyTemplate = YukonXml.getXPathTemplateForElement(replyElement);
		String expression = "/Reply/" + replyTypeName + "/Params/STATUS_DESC";
        String requestStatusDesc = replyTemplate.evaluateAsString(expression);
        if (requestStatusDesc == null) {
        	log.debug("Reply does not contain a value for function status description: " + xmlOutputter.outputString(replyElement) + " replyTypeName: " + replyTypeName);
			throw new CayentaRequestException("Reply does not contain a value for function status description.");
        }
		return requestStatusDesc;
	}
	
	// APPLY GetLocation DATA
	public static void applyGetLocationData(CayentaLocationInfo info, SimpleXPathTemplate replyTemplate) throws XPathException {
		
		String locationCity = replyTemplate.evaluateAsString("/Reply/GetLocation/Params/LOCATION/CITY");
		String locationZipCode = replyTemplate.evaluateAsString("/Reply/GetLocation/Params/LOCATION/POSTAL_CODE");
		String locationState = replyTemplate.evaluateAsString("/Reply/GetLocation/Params/LOCATION/PROVINCE_CD");
		String mapNumber = replyTemplate.evaluateAsString("/Reply/GetLocation/Params/LOCATION/LOCATION_ID_CUSTOM");
		
		info.setLocationCity(locationCity);
		info.setLocationZipCode(locationZipCode);
		info.setLocationState(locationState);
		info.setMapNumber(mapNumber);
	}
	
	// APPLY GetInstalledMeters DATA
	public static void applyGetMeterData(CayentaMeterInfo info, SimpleXPathTemplate replyTemplate) throws XPathException, CayentaRequestException {
		
		List<Node> installedMeters = replyTemplate.evaluateAsNodeList("/Reply/GetInstalledMeters/Params/INSTALLEDMETERS/INSTALLEDMETER");
		if (installedMeters.size() < 1) {
			throw new CayentaMeterNotFoundException("");
		}
		
		String accountNumber = replyTemplate.evaluateAsString("/Reply/GetInstalledMeters/Params/INSTALLEDMETERS/INSTALLEDMETER[1]/ACCOUNT_NO");
		String name = replyTemplate.evaluateAsString("/Reply/GetInstalledMeters/Params/INSTALLEDMETERS/INSTALLEDMETER[1]/V_NAME");
		String locationNumber = replyTemplate.evaluateAsString("/Reply/GetInstalledMeters/Params/INSTALLEDMETERS/INSTALLEDMETER[1]/LOCATION_NO");
		String serialNumber = replyTemplate.evaluateAsString("/Reply/GetInstalledMeters/Params/INSTALLEDMETERS/INSTALLEDMETER[1]/SERIAL_NO");
		String address = replyTemplate.evaluateAsString("/Reply/GetInstalledMeters/Params/INSTALLEDMETERS/INSTALLEDMETER[1]/V_ADDRESS");
		
		info.setAccountNumber(accountNumber);
		info.setName(name);
		info.setLocationNumber(locationNumber);
		info.setSerialNumber(serialNumber);
		info.setAddress(address);
	}
	
	// APPLY GetAccountPhone DATA
	public static void applyGetAccountPhoneData(CayentaPhoneInfo info, SimpleXPathTemplate replyTemplate) throws XPathException {
		
		String phoneNumber = replyTemplate.evaluateAsString("/Reply/GetAccountPhone/Params/PHONE_NO");
		
		info.setPhoneNumber(phoneNumber);
	}
	
	public static Date parseDate(String date) throws ParseException {
		
		if (date == null) {
			return null;
		}
		
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		return df.parse(date);
	}
	
	public static Element getElementForXmlString(String xml) throws IOException, JDOMException {
		
		Resource resource = new ByteArrayResource(xml.getBytes());
		return XmlUtils.createElementFromResource(resource);
	}
	
}
