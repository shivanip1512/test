package com.cannontech.web.cayenta.service.impl;

import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.xml.xpath.XPathException;

import unit.cayenta.MockSimpleHttpPostServiceFactory;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.web.cayenta.model.CayentaLocationInfo;
import com.cannontech.web.cayenta.model.CayentaMeterInfo;
import com.cannontech.web.cayenta.model.CayentaPhoneInfo;
import com.cannontech.web.cayenta.service.CayentaApiService;
import com.cannontech.web.cayenta.util.CayentaMeterNotFoundException;
import com.cannontech.web.cayenta.util.CayentaRequestException;
import com.cannontech.web.cayenta.util.CayentaXmlUtils;
import com.cannontech.web.simplePost.SimpleHttpPostService;
import com.cannontech.web.simplePost.SimpleHttpPostServiceFactory;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;

public class CayentaApiServiceImpl implements CayentaApiService {

	private static Logger log = YukonLogManager.getLogger(CayentaApiServiceImpl.class);
	private static XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
	private SimpleHttpPostServiceFactory simpleHttpPostServiceFactory;
	
	private static String postElementName = "XMLREQUEST";
	
	// GET LOCATION
	public CayentaLocationInfo getLocationInfoForMeterNumber(String meterNumber) throws CayentaRequestException {
		
		CayentaLocationInfo info = new CayentaLocationInfo();
		String getLocationReply = "";
		
		try {
			getLocationReply = doRequestWithMeterNumber("GetLocation", meterNumber);
			SimpleXPathTemplate getLocationTemplate = getReplyTemplate(getLocationReply);
			CayentaXmlUtils.applyGetLocationData(info, getLocationTemplate);
		} catch (JDOMException e) {
			log.debug("Unable to parse GetLocation reply: " + getLocationReply, e);
			throw new CayentaRequestException("Unable to read reply.", e);
		} catch (IOException e) {
			log.debug("Unable to parse GetLocation reply: " + getLocationReply, e);
			throw new CayentaRequestException("Unable to read reply.", e);
		} catch (XPathException e) {
			log.debug("Unable to parse GetLocation reply: " + getLocationReply, e);
			throw new CayentaRequestException("Unable to read reply.", e);
		}
		
		return info;
	}
	
	// GET METER
	public CayentaMeterInfo getMeterInfoForMeterNumber(String meterNumber) throws CayentaRequestException {
		
		CayentaMeterInfo info = new CayentaMeterInfo();
		String getMeterReply = "";
		
		try {
			getMeterReply = doRequestWithMeterNumber("GetInstalledMeters", meterNumber);
			SimpleXPathTemplate getMeterReplyTemplate = getReplyTemplate(getMeterReply);
			CayentaXmlUtils.applyGetMeterData(info, getMeterReplyTemplate);
		} catch (JDOMException e) {
			log.debug("Unable to parse GetInstalledMeters reply: " + getMeterReply, e);
			throw new CayentaRequestException("Unable to read reply.", e);
		} catch (IOException e) {
			log.debug("Unable to parse GetInstalledMeters reply: " + getMeterReply, e);
			throw new CayentaRequestException("Unable to read reply.", e);
		} catch (XPathException e) {
			log.debug("Unable to parse GetInstalledMeters reply: " + getMeterReply, e);
			throw new CayentaRequestException("Unable to read reply.", e);
		}
		
		return info;
	}
	
	// GET PHONE
	public CayentaPhoneInfo getPhoneInfoForAccountNumber(String accountNumber) throws CayentaRequestException {
		
		CayentaPhoneInfo info = new CayentaPhoneInfo();
		String getAccountPhoneReply = "";
		
		try {
			getAccountPhoneReply = doRequestWithAccountNumber("GetAccountPhone", accountNumber);
			SimpleXPathTemplate getAccountPhoneReplyTemplate = getReplyTemplate(getAccountPhoneReply);
			CayentaXmlUtils.applyGetAccountPhoneData(info, getAccountPhoneReplyTemplate);
		} catch (JDOMException e) {
			log.debug("Unable to parse GetAccountPhone reply: " + getAccountPhoneReply, e);
			throw new CayentaRequestException("Unable to read reply.", e);
		} catch (IOException e) {
			log.debug("Unable to parse GetAccountPhone reply: " + getAccountPhoneReply, e);
			throw new CayentaRequestException("Unable to read reply.", e);
		} catch (XPathException e) {
			log.debug("Unable to parse GetAccountPhone reply: " + getAccountPhoneReply, e);
			throw new CayentaRequestException("Unable to read reply.", e);
		}
		
		return info;
	}
	
	// BUILD POST
	private String doRequestWithMeterNumber(String methodName, String meterNumber) throws CayentaRequestException {
		
		// build account request
		Element requestElement = new Element("Request");
		Element methodNameElement = new Element(methodName);
		Element paramsElement = new Element("Params");
		Element meterNumberElement = new Element("METER_NO");
		meterNumberElement.addContent(meterNumber);
		paramsElement.addContent(meterNumberElement);
		methodNameElement.addContent(paramsElement);
		requestElement.addContent(methodNameElement);
		
		// post request, get response
		String resp = doRequestPost(requestElement);
			
		return resp;
	}
	
	private String doRequestWithAccountNumber(String methodName, String accountNumber) throws CayentaRequestException {
		
		// build account request
		Element requestElement = new Element("Request");
		Element methodNameElement = new Element(methodName);
		Element paramsElement = new Element("Params");
		Element meterNumberElement = new Element("ACCOUNT_NO");
		meterNumberElement.addContent(accountNumber);
		paramsElement.addContent(meterNumberElement);
		methodNameElement.addContent(paramsElement);
		requestElement.addContent(methodNameElement);
		
		// post request, get response
		String resp = doRequestPost(requestElement);
			
		return resp;
	}
	
	private SimpleXPathTemplate getReplyTemplate(String reply) throws JDOMException, IOException, CayentaRequestException {
			
		Element currentReplyElement = CayentaXmlUtils.getElementForXmlString(reply);
		String currentReplyTypeName = CayentaXmlUtils.getMethodName(currentReplyElement);
		SimpleXPathTemplate currentReplyTemplate = XmlUtils.getXPathTemplateForElement(currentReplyElement);
		checkReplyStatus(currentReplyTypeName, currentReplyElement);
		
		return currentReplyTemplate;
	}
	
	// CHECK REPLY STATUSES
	private void checkReplyStatus(String replyTypeName, Element replyElement) throws CayentaRequestException {
		
		int replyStatus = CayentaXmlUtils.getReplyStatusValue(replyTypeName, replyElement);
		if (replyStatus != 0) {
			String statusDescriptionPrefix = "System failure. STATUS = " + replyStatus + ". ";
			String statusDescription = CayentaXmlUtils.getReplyStatusDescription(replyTypeName, replyElement);
			statusDescription = (statusDescription == null ? statusDescriptionPrefix : statusDescriptionPrefix + statusDescription + ".");
			
			log.debug("Reply contains system error status: " + statusDescription + ": " + xmlOutputter.outputString(replyElement));
			throw new CayentaRequestException("Reply contains system error status.");
		}
		
		int replyFunctionStatus = CayentaXmlUtils.getReplyFunctionStatusValue(replyTypeName, replyElement);
		if (replyFunctionStatus != 0) {
			
			// expected failure types
			if (replyTypeName.equals("GetInstalledMeters") && replyFunctionStatus == -3) {
				throw new CayentaMeterNotFoundException("");
			}
			
			String statusDescriptionPrefix = "Function failure. STATUS = " + replyFunctionStatus + ". ";
			String statusDescription = CayentaXmlUtils.getReplyFunctionStatusDescription(replyTypeName, replyElement);
			statusDescription = (statusDescription == null ? statusDescriptionPrefix : statusDescriptionPrefix + statusDescription + ".");

			log.debug("Reply contains function error status: " + statusDescription + ": " + xmlOutputter.outputString(replyElement));
			throw new CayentaRequestException("Reply contains function error status.");
		}
	}
	
	// DO POST
	private String doRequestPost(Element requestElement) throws CayentaRequestException {
		
		try {
			log.debug("Sending request: " + xmlOutputter.outputString(requestElement));
			String value = xmlOutputter.outputString(requestElement);
			
			// --- TEST FOR MOCK RESPONSE ---
			SimpleHttpPostService postService = (new MockSimpleHttpPostServiceFactory()).getCayentaPostService();
			
			//SimpleHttpPostService postService = simpleHttpPostServiceFactory.getCayentaPostService();
			String resp = postService.postValue(postElementName, value);
			log.debug("Recieved response: " + resp);
			return resp;
		} catch (HttpException e) {
			log.debug("Unable to communicate with Cayenta API server due to HttpException.", e);
			throw new CayentaRequestException("Unable to communicate with Cayenta API server.", e);
		} catch (IOException e) {
			log.debug("Unable to communicate with Cayenta API server due to IOException.", e);
			throw new CayentaRequestException("Unable to communicate with Cayenta API server.", e);
		}
	}
	
	@Autowired
	public void setSimpleHttpPostServiceFactory(
			SimpleHttpPostServiceFactory simpleHttpPostServiceFactory) {
		this.simpleHttpPostServiceFactory = simpleHttpPostServiceFactory;
	}
}
