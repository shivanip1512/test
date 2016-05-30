package com.cannontech.web.cayenta.service.impl;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.xml.xpath.XPathException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.web.cayenta.model.CayentaLocationInfo;
import com.cannontech.web.cayenta.model.CayentaMeterInfo;
import com.cannontech.web.cayenta.model.CayentaPhoneInfo;
import com.cannontech.web.cayenta.service.CayentaApiService;
import com.cannontech.web.cayenta.util.CayentaMeterNotFoundException;
import com.cannontech.web.cayenta.util.CayentaRequestException;
import com.cannontech.web.cayenta.util.CayentaXmlUtils;
import com.cannontech.web.simplePost.SimpleHttpPostService;
import com.cannontech.web.simplePost.SimpleHttpPostServiceFactory;

public class CayentaApiServiceImpl implements CayentaApiService {

	private static Logger log = YukonLogManager.getLogger(CayentaApiServiceImpl.class);
	private static XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
	private SimpleHttpPostServiceFactory simpleHttpPostServiceFactory;
	private ConfigurationSource configurationSource;
	
	private static String postElementName = "XMLREQUEST";
	
	// GET LOCATION
	@Override
    public CayentaLocationInfo getLocationInfoForMeterName(String meterName) throws CayentaRequestException {
		
		CayentaLocationInfo info = new CayentaLocationInfo();
		String getLocationReply = "";
		
		try {
			getLocationReply = doRequestWithMeterNameAsLocationNumber("GetLocation", meterName);
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
	@Override
    public CayentaMeterInfo getMeterInfoForMeterName(String meterName) throws CayentaRequestException {
		
		CayentaMeterInfo info = new CayentaMeterInfo();
		String getMeterReply = "";
		
		try {
			getMeterReply = doRequestWithMeterNameAsLocationNumber("GetInstalledMeters", meterName);
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
	@Override
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
	private String doRequestWithMeterNameAsLocationNumber(String methodName, String meterName) 
	        throws CayentaRequestException {
		
		// build account request
		Element requestElement = new Element("Request");
		Element methodNameElement = new Element(methodName);
		Element paramsElement = new Element("Params");
		Element meterNameElement = new Element("LOCATION_NO");
		meterNameElement.addContent(meterName);
		paramsElement.addContent(meterNameElement);
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
	
	private SimpleXPathTemplate getReplyTemplate(String reply) throws JDOMException, 
	IOException, CayentaRequestException {
			
		Element currentReplyElement = CayentaXmlUtils.getElementForXmlString(reply);
		String currentReplyTypeName = CayentaXmlUtils.getMethodName(currentReplyElement);
		SimpleXPathTemplate currentReplyTemplate = YukonXml.getXPathTemplateForElement(currentReplyElement);
		checkReplyStatus(currentReplyTypeName, currentReplyElement);
		
		return currentReplyTemplate;
	}
	
	// CHECK REPLY STATUSES
	private void checkReplyStatus(String replyTypeName, Element replyElement) throws CayentaRequestException {
		
		int replyStatus = CayentaXmlUtils.getReplyStatusValue(replyTypeName, replyElement);
		if (replyStatus != 0) {
			String statusDescriptionPrefix = "System failure. STATUS = " + replyStatus + ". ";
			String statusDescription = CayentaXmlUtils.getReplyStatusDescription(replyTypeName, replyElement);
			statusDescription = (statusDescription == null ? statusDescriptionPrefix : statusDescriptionPrefix 
			    + statusDescription + ".");
			
			log.debug("Reply contains system error status: " + statusDescription + ": " 
			+ xmlOutputter.outputString(replyElement));
			throw new CayentaRequestException("Reply contains system error status.");
		}
		
		int replyFunctionStatus;
		try {
			replyFunctionStatus= CayentaXmlUtils.getReplyFunctionStatusValue(replyTypeName, replyElement);
		} catch (CayentaRequestException e) {
			// no function failure if function has no status param
			log.debug("Reply has no function status param, assume no function error");
			return;
		}
		
		if (replyFunctionStatus != 0) {
			
			// expected failure types
			if (replyTypeName.equals("GetInstalledMeters") && replyFunctionStatus == -3) {
				throw new CayentaMeterNotFoundException("");
			}
			
			if (replyTypeName.equals("GetLocation") && replyFunctionStatus == -4) {
				throw new CayentaMeterNotFoundException("");
			}
			
			String statusDescriptionPrefix = "Function failure. STATUS = " + replyFunctionStatus + ". ";
			String statusDescription = CayentaXmlUtils.getReplyFunctionStatusDescription(replyTypeName, replyElement);
			statusDescription = (statusDescription == null ? statusDescriptionPrefix : statusDescriptionPrefix 
			    + statusDescription + ".");

			log.debug("Reply contains function error status: " + statusDescription + ": " 
			+ xmlOutputter.outputString(replyElement));
			throw new CayentaRequestException("Reply contains function error status.");
		}
	}
	
	// DO POST
	private String doRequestPost(Element requestElement) throws CayentaRequestException {
		try {
			log.debug("Sending request: " + xmlOutputter.outputString(requestElement));
			String value = xmlOutputter.outputString(requestElement);
			
			String url = configurationSource.getRequiredString("CAYENTA_API_SERVER_URL");
			int port = configurationSource.getRequiredInteger("CAYENTA_API_SERVER_PORT");
			String userName = configurationSource.getRequiredString("CAYENTA_API_SERVER_USERNAME");
			String password = configurationSource.getRequiredString("CAYENTA_API_SERVER_PASSWORD");
			SimpleHttpPostService postService = 
			        simpleHttpPostServiceFactory.getSimpleHttpPostService(url, port, userName, password);
			
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
	
	@Autowired
	public void setConfigurationSource(ConfigurationSource configurationSource) {
		this.configurationSource = configurationSource;
	}
}
