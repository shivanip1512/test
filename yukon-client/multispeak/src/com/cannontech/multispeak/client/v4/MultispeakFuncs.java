package com.cannontech.multispeak.client.v4;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.AbstractSoapMessage;
import org.springframework.ws.soap.SoapEnvelope;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Node;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.multispeak.client.MessageContextHolder;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncsBase;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.YukonMultispeakMsgHeader;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.data.MspReturnList;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;

public class MultispeakFuncs extends MultispeakFuncsBase {
    private final static Logger log = YukonLogManager.getLogger(MultispeakFuncs.class);
    @Autowired public MultispeakDao multispeakDao;

    @Override
    public MultiSpeakVersion version() {
        return MultiSpeakVersion.V4;
    }

    @Override
    public void loadResponseHeader() throws MultispeakWebServiceException {
        SoapEnvelope env;
        try {
            env = getResponseMessageSOAPEnvelope();
            SoapHeader header = env.getHeader();
            // the YukonMultispeakMsgHeader will be built with "dummy" values for userId and pwd fields. The
            // expectation is that getMultispeakVendorFromHeader will replace these values with the correct
            // values from the other vendor once it is loaded.
            getHeader(header, "unauthorized", "unauthorized");
        } catch (NotFoundException | SOAPException e) {
            throw new MultispeakWebServiceException(e.getMessage());
        }

    }

    public SoapHeaderElement getHeader(SoapHeader header, String outUserName, String outPassword) throws SOAPException {

        YukonMultispeakMsgHeader yukonMspMsgHeader = new YukonMultispeakMsgHeader(outUserName, outPassword,
                version().getVersion());
        QName qname = new QName(version().getNamespace(), "MultiSpeakMsgHeader");
        SoapHeaderElement headerElement = header.addHeaderElement(qname);
        headerElement.addAttribute(new QName("MajorVersion"), "4");
        headerElement.addAttribute(new QName("MinorVersion"), "1");
        headerElement.addAttribute(new QName("Build"), "6");
        headerElement.addAttribute(new QName("UserID"), yukonMspMsgHeader.getUserID());
        headerElement.addAttribute(new QName("Pwd"), yukonMspMsgHeader.getPwd());
        headerElement.addAttribute(new QName("AppName"), yukonMspMsgHeader.getAppName());
        headerElement.addAttribute(new QName("AppVersion"), yukonMspMsgHeader.getAppVersion());
        headerElement.addAttribute(new QName("Company"), yukonMspMsgHeader.getCompany());
        headerElement.addAttribute(new QName("CSUnits"), yukonMspMsgHeader.getCSUnits().value());
        return headerElement;
    }

    @Override
    public LiteYukonUser authenticateMsgHeader() throws MultispeakWebServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Returns the Soap Envelope updated with the SOAPAction.
     * 
     * @return SoapEnvelope
     * @throws javax.xml.soap.SOAPExceptionn
     */
    private SoapEnvelope getResponseMessageSOAPEnvelope() throws SOAPException {

        MessageContext ctx = MessageContextHolder.getMessageContext();
        WebServiceMessage responseMessage = ctx.getResponse();
        AbstractSoapMessage abstractSoapMessage = (AbstractSoapMessage) responseMessage;
        SaajSoapMessage saajSoapMessage = (SaajSoapMessage) abstractSoapMessage;
        SOAPMessage soapMessage = saajSoapMessage.getSaajMessage();
        SoapEnvelope soapEnvelop = saajSoapMessage.getEnvelope();
        MimeHeaders mimeHeaders = soapMessage.getMimeHeaders();

        WebServiceMessage webServiceRequestMessage = ctx.getRequest();
        SaajSoapMessage saajSoapRequestMessage = (SaajSoapMessage) webServiceRequestMessage;
        Node nxtNode = saajSoapRequestMessage.getSaajMessage().getSOAPPart().getEnvelope().getBody().getFirstChild();
        if (nxtNode != null && nxtNode.getNamespaceURI() == null) {
            nxtNode = nxtNode.getNextSibling();
        }

        if (nxtNode != null) {
            String soapAction = nxtNode.getNamespaceURI() + "/" + nxtNode.getLocalName();
            mimeHeaders.setHeader("SOAPAction", soapAction);
        } else {
            log.warn("Namespace and method not identified. SOAPAction not set.");
        }

        return soapEnvelop;
    }

    /**
     * Helper method to update responseHeader.objectsRemaining and
     * responseHeader.lastSent
     * 
     * @param returnResultsSize
     * @param vendor
     * @return
     * @throws MultispeakWebServiceException
     */
    public void updateResponseHeader(MspReturnList returnList) throws MultispeakWebServiceException {
        getResponseHeaderElement().addAttribute(new QName("ObjectsRemaining"), String.valueOf(returnList.getObjectsRemaining()));
        log.debug("Updated MspMessageHeader.ObjectsRemaining " + returnList.getObjectsRemaining());

        getResponseHeaderElement().addAttribute(new QName("LastSent"), returnList.getLastSent());
        log.debug("Updated MspMessageHeader.LastSent " + returnList.getLastSent());
    }

    public SoapHeaderElement getResponseHeaderElement() throws MultispeakWebServiceException {
        SoapEnvelope env;
        try {
            env = getResponseMessageSOAPEnvelope();
        } catch (SOAPException e) {
            throw new MultispeakWebServiceException(e.getMessage());
        }
        SoapHeader header = env.getHeader();
        Iterator<SoapHeaderElement> it = header
                .examineHeaderElements(new QName("http://www.multispeak.org/Version_4.1_Release", "MultiSpeakMsgHeader"));
        return it.next();
    }

    /**
     * This method returns an multispeak vendor.
     * 
     * @param version - multispeak version.
     * @return MultispeakVendor - Multispeak vendor information.
     **/
    public MultispeakVendor getMultispeakVendorFromHeader()
            throws MultispeakWebServiceException {
        try {
            SoapEnvelope env = getRequestMessageSOAPEnvelope();
            SoapHeader soapHeader = env.getHeader();
            String companyName = getCompanyNameFromSOAPHeader(soapHeader);
            String appName = getAppNameFromSOAPHeader(soapHeader);
            if (StringUtils.isEmpty(companyName)) {
                throw new MultispeakWebServiceException("Company name is required");
            }
            MultispeakVendor mspVendor = multispeakDao.getMultispeakVendorFromCache(companyName, appName);
            // Cannon is the name used by Yukon. We will not process the request if any other vendor is trying to
            // call the MSP web service with this name.
            if (mspVendor.getVendorID() == MultispeakVendor.CANNON_MSP_VENDORID) {
                throw new MultispeakWebServiceException("Invalid Company and/or AppName received: Company="
                        + companyName + " AppName=" + appName);
            }
            // update the responseHeader, replace with the correct userId and pwd from the "other" vendor now that we have it
            // loaded.
            getResponseHeaderElement().addAttribute(new QName("UserID"), mspVendor.getOutUserName());
            getResponseHeaderElement().addAttribute(new QName("Pwd"), mspVendor.getOutPassword());
            return mspVendor;
        } catch (NotFoundException e) {
            throw new MultispeakWebServiceException(e.getMessage());
        }
    }

    private SoapEnvelope getRequestMessageSOAPEnvelope() {
        MessageContext ctx = MessageContextHolder.getMessageContext();
        WebServiceMessage requestMessage = ctx.getRequest();
        AbstractSoapMessage abstractSoapMessage = (AbstractSoapMessage) requestMessage;
        SaajSoapMessage saajSoapMessage = (SaajSoapMessage) abstractSoapMessage;
        SoapEnvelope soapEnvelop = saajSoapMessage.getEnvelope();
        return soapEnvelop;
    }

    /**
     * This method returns an Company name from the request header.
     * 
     * @param soapHeader        - request header.
     * @param multispeakVersion - Multispeak version.
     * @return String - attribute value.
     **/
    public String getCompanyNameFromSOAPHeader(SoapHeader header) {
        return getAtributeFromSOAPHeader(header, MultispeakDefines.COMPANY);
    }

    /**
     * This method returns an App name from the request header.
     * 
     * @param soapHeader        - request header.
     * @param multispeakVersion - Multispeak version.
     * @return String - attribute value.
     **/
    public String getAppNameFromSOAPHeader(SoapHeader header) {
        return getAtributeFromSOAPHeader(header, MultispeakDefines.APPNAME);
    }

    /**
     * This method returns an attribute value from the request header.
     * 
     * @param soapHeader    - request header.
     * @param attributeName - Name of attribute whose value we need.
     * @return String - attribute value.
     **/
    private String getAtributeFromSOAPHeader(SoapHeader soapHeader, String attributeName) {
        String attributeValue = null;

        Iterator<SoapHeaderElement> iterator = soapHeader.examineAllHeaderElements();
        while (iterator.hasNext()) {
            SoapHeaderElement element = iterator.next();
            attributeValue = element.getAttributeValue(new QName(attributeName));
        }

        return attributeValue;
    }
    
    /**
     * Helper method to update responseHeader.resultIdentifier.replyCodeCategory
     * 
     * @throws MultispeakWebServiceException
     */
    public void updateResultIdentifierInResponseHeader(String value) throws MultispeakWebServiceException {
        try {
            SOAPHeader header = getResponseSOAPMessage().getSOAPPart().getEnvelope().getHeader();

            Iterator<?> headerElements = header.examineAllHeaderElements();
            while (headerElements.hasNext()) {
                SOAPHeaderElement headerElement = (SOAPHeaderElement) headerElements.next();

                SOAPElement resultElement = headerElement.addChildElement("Result", "res");
                SOAPElement resultIdentifierElement = resultElement.addChildElement("resultIdentifier", "com");
                SOAPElement replyCodeCategoryElement =
                    resultIdentifierElement.addChildElement("replyCodeCategory", "com");
                replyCodeCategoryElement.addTextNode(value);

            }
        } catch (SOAPException e) {
            throw new MultispeakWebServiceException("Unable to add result object in response", e);
        }
    }
    
    /**
     * Returns response soap message
     * 
     * @return SOAPMessage
     * @throws javax.xml.soap.SOAPException
     */
    private SOAPMessage getResponseSOAPMessage() throws SOAPException {

        MessageContext ctx = MessageContextHolder.getMessageContext();
        WebServiceMessage responseMessage = ctx.getResponse();
        AbstractSoapMessage abstractSoapMessage = (AbstractSoapMessage) responseMessage;
        SaajSoapMessage saajSoapMessage = (SaajSoapMessage) abstractSoapMessage;
        SOAPMessage soapMessage = saajSoapMessage.getSaajMessage();
        return soapMessage;

    }

}
