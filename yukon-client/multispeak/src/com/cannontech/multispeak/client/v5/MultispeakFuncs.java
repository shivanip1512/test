package com.cannontech.multispeak.client.v5;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.AbstractSoapMessage;
import org.springframework.ws.soap.SoapEnvelope;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Node;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.exception.PasswordExpiredException;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.point.stategroup.Disconnect410State;
import com.cannontech.database.db.point.stategroup.PointStateHelper;
import com.cannontech.database.db.point.stategroup.RfnDisconnectStatusState;
import com.cannontech.msp.beans.v5.commontypes.CSUnitsKind;
import com.cannontech.msp.beans.v5.commontypes.ErrorObject;
import com.cannontech.msp.beans.v5.enumerations.RCDStateKind;
import com.cannontech.msp.beans.v5.ws.response.MultiSpeakResponseMsgHeader;
import com.cannontech.multispeak.client.MessageContextHolder;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncsBase;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.data.v5.MspRCDStateKind;
import com.cannontech.multispeak.data.v5.MspReturnList;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;

public class MultispeakFuncs extends MultispeakFuncsBase {
    private final static Logger log = YukonLogManager.getLogger(MultispeakFuncs.class);

    @Autowired public AuthenticationService authenticationService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired public MultispeakDao multispeakDao;
    @Autowired public DeviceGroupService deviceGroupService;
    @Autowired public PointFormattingService pointFormattingService;
    @Autowired public PaoDefinitionDao paoDefinitionDao;
    @Resource(name="domainMarshallerV5") Jaxb2Marshaller jaxb2Marshaller;

    private static final QName QNAME_CALLER = new QName("http://www.multispeak.org/V5.0/ws/request", "Caller");
    private static final QName QNAME_COMPANY = new QName("http://www.multispeak.org/V5.0/commonTypes", "Company");
    private static final QName QNAME_APPNAME = new QName("http://www.multispeak.org/V5.0/commonTypes", "AppName");
    private static final QName QNAME_USERNAME = new QName("http://www.multispeak.org/V5.0/commonTypes", "SystemID");
    private static final QName QNAME_PASSWORD = new QName("http://www.multispeak.org/V5.0/commonTypes", "Password");
    private static final QName QNAME_LAST_SENT = new QName("http://www.multispeak.org/V5.0/commonTypes", "lastSent");
    private static final QName QNAME_OBJECT_REMAINING = new QName("http://www.multispeak.org/V5.0/commonTypes",
        "objectsRemaining");
    private static final QName QNAME_RESULT = new QName("http://www.multispeak.org/V5.0/response", "Result");


    public void logErrorObjects(String intfaceName, String methodName, List<ErrorObject> objects) {
        if (CollectionUtils.isNotEmpty(objects)) {
            for (ErrorObject errorObject : objects) {
                log.info("Error Return from " + intfaceName + "(" + methodName + "): " + (errorObject == null ? "Null"
                        : errorObject.getReferenceID() + " - " + errorObject.getDisplayString()));
            }
        }
    }
    
    /**
     * Returns list of ErrorObject
     * 
     * @throws MultispeakWebServiceClientException
     */
    public List<ErrorObject> getErrorObjectsFromResponse() throws MultispeakWebServiceClientException {

        List<ErrorObject> errorObjects;
        MultiSpeakResponseMsgHeader responseHeader = null;
        MessageContext message = MessageContextHolder.getMessageContext();
        WebServiceMessage requestMessage = message.getResponse();
        SaajSoapMessage saajSoapMessage = (SaajSoapMessage) requestMessage;
        SoapEnvelope soapEnvelop = saajSoapMessage.getEnvelope();
        SoapHeader soapHeader = soapEnvelop.getHeader();

        try {
            Iterator<SoapHeaderElement> iterator = soapHeader.examineAllHeaderElements();
            while (iterator.hasNext()) {
                SoapHeaderElement element = iterator.next();
                DOMSource soapElement = (DOMSource) element.getSource();
                JAXBElement<?> rootElement = (JAXBElement<?>) jaxb2Marshaller.unmarshal(soapElement);
                responseHeader = (MultiSpeakResponseMsgHeader) rootElement.getValue();
            }
            errorObjects =
                responseHeader.getResult() == null ? null : responseHeader.getResult().getErrorObjects() != null
                    ? responseHeader.getResult().getErrorObjects().getErrorObject() : null;
        } catch (Exception e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
        return errorObjects;
    }

    
    
    public void getHeader(SOAPMessage soapMessage) throws SOAPException {
        SOAPEnvelope env = soapMessage.getSOAPPart().getEnvelope();

        Node nxtNode = getRequestSOAPMessage().getSOAPPart().getEnvelope().getBody().getFirstChild();
        if (nxtNode != null && nxtNode.getNamespaceURI() == null) {
            nxtNode = nxtNode.getNextSibling();
        }

        if (nxtNode != null) {
            String soapAction = nxtNode.getNamespaceURI() + "/" + nxtNode.getLocalName();
            soapMessage.getMimeHeaders().setHeader("SOAPAction", soapAction);
        } else {
            log.warn("Namespace and method not identified. SOAPAction not set.");
        }
        env.addNamespaceDeclaration(nxtNode.getPrefix(), nxtNode.getNamespaceURI());
        env.addNamespaceDeclaration("com", "http://www.multispeak.org/V5.0/commonTypes");
        env.addNamespaceDeclaration("res", "http://www.multispeak.org/V5.0/ws/response");

        SOAPHeader header = env.getHeader();
        SOAPElement headElement = header.addChildElement("MultiSpeakResponseMsgHeader", "res");
        
        getHeader(headElement, "res");

    }

    public void getHeader(SOAPElement headElement, String prefix) throws SOAPException {
        
        MultispeakVendor mspVendor =
                multispeakDao.getMultispeakVendorFromCache(MultispeakDefines.MSP_COMPANY_YUKON,
                    MultispeakDefines.MSP_APPNAME_YUKON);
        SOAPElement callerElement = headElement.addChildElement("Caller", prefix);
        SOAPElement appNameElement = callerElement.addChildElement("AppName", "com");
        appNameElement.addTextNode("Yukon");
        SOAPElement appVersionElement = callerElement.addChildElement("AppVersion", "com");
        appVersionElement.addTextNode(VersionTools.getYUKON_VERSION());
        SOAPElement companyElement = callerElement.addChildElement("Company", "com");
        companyElement.addTextNode("Cannon");
        SOAPElement passwordElement = callerElement.addChildElement("Password", "com");
        passwordElement.addTextNode(mspVendor.getOutPassword());
        SOAPElement systemIDElement = callerElement.addChildElement("SystemID", "com");
        systemIDElement.addTextNode(mspVendor.getOutUserName());
        SOAPElement coordSysInforElement = headElement.addChildElement("CoordinateSystemInformation", prefix);
        SOAPElement csUnit = coordSysInforElement.addChildElement("CSUnits", "com");
        csUnit.addTextNode(CSUnitsKind.FEET.value());
        SOAPElement multiSpeakVersionElement = headElement.addChildElement("MultiSpeakVersion", prefix);
        SOAPElement majorVersionElement = multiSpeakVersionElement.addChildElement("MajorVersion", "com");
        majorVersionElement.addTextNode("5");
        SOAPElement minorElement = multiSpeakVersionElement.addChildElement("MinorVersion", "com");
        minorElement.addTextNode("0");
        SOAPElement buildElement = multiSpeakVersionElement.addChildElement("Build", "com");
        buildElement.addTextNode("8");
    }

    @Override
    public void loadResponseHeader() throws MultispeakWebServiceException {
        SOAPMessage soapMessage;
        try {
            soapMessage = getResponseSOAPMessage();
            getHeader(soapMessage);

        } catch (NotFoundException | SOAPException e) {
            throw new MultispeakWebServiceException(e.getMessage());
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

    /**
     * Returns request soap message
     * 
     * @return SOAPMessage
     * @throws javax.xml.soap.SOAPException
     */

    private SOAPMessage getRequestSOAPMessage() {
        MessageContext ctx = MessageContextHolder.getMessageContext();
        WebServiceMessage requestMessage = ctx.getRequest();
        AbstractSoapMessage abstractSoapMessage = (AbstractSoapMessage) requestMessage;
        SaajSoapMessage saajSoapMessage = (SaajSoapMessage) abstractSoapMessage;
        SOAPMessage soapMessage = saajSoapMessage.getSaajMessage();
        return soapMessage;
    }

    /**
     * This method returns an Company name from the request header.
     * @throws SOAPException
     **/
    public String getCompanyNameFromSOAPHeader() throws SOAPException {
        return getNodeValueFromSOAPMessage(QNAME_COMPANY, QNAME_CALLER);
    }

    /**
     * This method returns an App name from the request header.
     * @throws SOAPException
     **/
    public String getAppNameFromSOAPHeader() throws SOAPException {
        return getNodeValueFromSOAPMessage(QNAME_APPNAME, QNAME_CALLER);
    }

    @Override
    public LiteYukonUser authenticateMsgHeader() throws MultispeakWebServiceException {
        LiteYukonUser user = null;
        try {
            String username = getNodeValueFromSOAPMessage(QNAME_USERNAME, QNAME_CALLER);
            String password = getNodeValueFromSOAPMessage(QNAME_PASSWORD, QNAME_CALLER);

            if (username != null && password != null) {
                user = authenticationService.login(username, password);
            } else {
                throw new MultispeakWebServiceException("User credentials(SystemID/Password) are not valid in request.");
            }

        } catch (PasswordExpiredException e) {
            throw new MultispeakWebServiceException("Password expired.", e);
        } catch (BadAuthenticationException e) {
            throw new MultispeakWebServiceException("User authentication failed.", e);
        } catch (SOAPException e) {
            throw new MultispeakWebServiceException(e.getMessage());
        }
        return user;

    }

    /**
     * This method returns an multispeak vendor.
     * 
     * @return MultispeakVendor - Multispeak vendor information.
     * @throws MultispeakWebServiceException
     **/
    public MultispeakVendor getMultispeakVendorFromHeader() throws MultispeakWebServiceException {
        try {
            String companyName = getCompanyNameFromSOAPHeader();
            String appName = getAppNameFromSOAPHeader();
            return multispeakDao.getMultispeakVendorFromCache(companyName, appName);
        } catch (NotFoundException | SOAPException e) {
            throw new MultispeakWebServiceException(e.getMessage());
        }
    }

    /**
     * This method returns an child node value from SOAPHeader
     **/

    private String getNodeValueFromSOAPMessage(QName qNameToFind, QName callerQname) throws SOAPException {
        String nodeValue = null;

        SOAPHeader header = getRequestSOAPMessage().getSOAPPart().getEnvelope().getHeader();
        Iterator<?> headerElements = header.examineAllHeaderElements();
        while (headerElements.hasNext()) {
            SOAPHeaderElement headerElement = (SOAPHeaderElement) headerElements.next();
            Iterator<?> childElements = headerElement.getChildElements(callerQname);
            while (childElements.hasNext()) {
                Node soapNode = (Node) childElements.next();
                if (soapNode instanceof SOAPElement) {
                    SOAPElement element = (SOAPElement) soapNode;
                    nodeValue = getFirstChildElementValue(element, qNameToFind);

                }

            }

        }
        return nodeValue;
    }

    /**
     * This method returns child node value from SOAPElement
     **/

    private String getFirstChildElementValue(SOAPElement soapElement, QName qNameToFind) {
        String nodeValue = null;
        Iterator<?> childElements = soapElement.getChildElements(qNameToFind);
        while (childElements.hasNext()) {
            SOAPElement element = (SOAPElement) childElements.next(); // use first

            nodeValue = element.getValue();
        }

        return nodeValue;
    }

    /**
     * Adding ErrorObject and Version in response
     */

    public void addErrorObjectsInResponseHeader(List<ErrorObject> errorObjects) throws MultispeakWebServiceException {
        try {
            SOAPHeader header = getResponseSOAPMessage().getSOAPPart().getEnvelope().getHeader();

            Iterator<?> headerElements = header.examineAllHeaderElements();
            while (headerElements.hasNext()) {
                SOAPHeaderElement headerElement = (SOAPHeaderElement) headerElements.next();

                SOAPElement resultElement = headerElement.addChildElement("Result", "res");
                SOAPElement errorElements = resultElement.addChildElement("errorObjects", "com");

                for (ErrorObject errorObject : errorObjects) {
                    SOAPElement errorElement = errorElements.addChildElement("errorObject", "com");
                    errorElement.addAttribute(new QName("referenceID"), errorObject.getReferenceID());
                    errorElement.addAttribute(new QName("nounType"), errorObject.getNounType());
                    SOAPElement errorMsgElement = errorElement.addChildElement("displayString", "com");
                    errorMsgElement.addTextNode(errorObject.getDisplayString());
                    SOAPElement errorEventTimeElement = errorElement.addChildElement("eventTime", "com");
                    errorEventTimeElement.addTextNode(errorObject.getEventTime().toString());

                }
            }

        } catch (SOAPException e) {
            throw new MultispeakWebServiceException("Unable to add error object in response", e);
        }

    }

    
    /**
     * Helper method to update responseHeader.objectsRemaining and
     * responseHeader.lastSent
     * 
     * @param returnResultsSize
     * @return
     * @throws MultispeakWebServiceException
     */
    public void updateResponseHeader(MspReturnList returnList) throws MultispeakWebServiceException {
        try {
            SOAPHeader header = getResponseSOAPMessage().getSOAPPart().getEnvelope().getHeader();
            SOAPElement resultElement = null;
            Iterator<?> headerElements = header.examineAllHeaderElements();
            while (headerElements.hasNext()) {
                SOAPHeaderElement headerElement = (SOAPHeaderElement) headerElements.next();
                QName name = headerElement.createQName("result", "res");
                Iterator childElements = headerElement.getChildElements(name);
                if (!childElements.hasNext()) {
                    resultElement = headerElement.addChildElement("result", "res");
                } else {
                    resultElement = (SOAPElement) childElements.next();
                }
                if (returnList.getLastSent() != null) {
                    SOAPElement lastSentElement = resultElement.addChildElement("lastSent", "com");
                    lastSentElement.addTextNode(returnList.getLastSent());
                    log.debug("Updated MspMessageHeader.LastSent " + returnList.getLastSent());
                }
                SOAPElement objectsRemainingElement = resultElement.addChildElement("objectsRemaining", "com");
                objectsRemainingElement.addTextNode(String.valueOf(returnList.getObjectsRemaining()));
                log.debug("Updated MspMessageHeader.ObjectsRemaining " + returnList.getObjectsRemaining());
            }
        } catch (SOAPException e) {
            throw new MultispeakWebServiceException("Unable to add result object in response", e);
        }
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
    
    public String getLastSentFromHeader() {
        return getChildValue(QNAME_LAST_SENT, QNAME_RESULT);
    }

    public String getObjectRemainingValueFromHeader() {
        return getChildValue(QNAME_OBJECT_REMAINING, QNAME_RESULT);
    }

    /**
     * This method returns an child value of given qNameToFind from response header.
     * 
     * @param qNameToFind - child to find
     * @param qNameResult - child to find from
     * @return String - child value.
     **/
    private String getChildValue(QName qNameToFind, QName qNameResult) {
        String nodeValue = null;
        SOAPHeader header = null;
        try {
            header = getResponseSOAPMessage().getSOAPPart().getEnvelope().getHeader();
            Iterator<?> headerElements = header.examineAllHeaderElements();
            while (headerElements.hasNext()) {
                SOAPHeaderElement headerElement = (SOAPHeaderElement) headerElements.next();
                Iterator<?> childElements = headerElement.getChildElements(qNameResult);
                while (childElements.hasNext()) {
                    Node soapNode = (Node) childElements.next();
                    if (soapNode instanceof SOAPElement) {
                        SOAPElement element = (SOAPElement) soapNode;
                        nodeValue = getFirstChildElementValue(element, qNameToFind);
                    }
                }
            }
        } catch (SOAPException e) {
            log.error("Error to fetch child node from header", e);
        }
        return nodeValue;
    }



    
    /**
     * Translates the rawState into a RCDStateKind based on the type of meter
     * and expected state group for that type. Returns RCDStateKind.Unknown
     * when cannot be determined.
     * 
     * @param meter
     * @return
     */
    public RCDStateKind getRCDStateKind(YukonDevice yukonDevice, PointValueHolder pointValueHolder) {

        MspRCDStateKind mspRCDStateKind;
        try {

            log.debug("Returning disconnect status from cache: "
                + pointFormattingService.getCachedInstance().getValueString(pointValueHolder, Format.FULL,
                    YukonUserContext.system));

            boolean isRfnDisconnect =
                paoDefinitionDao.isTagSupported(yukonDevice.getPaoIdentifier().getPaoType(), PaoTag.DISCONNECT_RFN);
            if (isRfnDisconnect) {
                RfnDisconnectStatusState pointState = PointStateHelper.decodeRawState(RfnDisconnectStatusState.class, pointValueHolder.getValue());
                mspRCDStateKind = MspRCDStateKind.getForRfnState(pointState);
                log.debug("returning RCDStateKind for RFN: " + mspRCDStateKind);
            } else { // assume everything else is PLC
                Disconnect410State pointState =
                    PointStateHelper.decodeRawState(Disconnect410State.class, pointValueHolder.getValue());
                mspRCDStateKind = MspRCDStateKind.getForPlcState(pointState);
                log.debug("returning RCDStateKind for PLC: " + mspRCDStateKind);
            }
        } catch (IllegalArgumentException e) {
            // we were unable to decode the rawState
            log.warn("Unable to decode rawState. value:" + pointValueHolder.getValue());
            return RCDStateKind.UNKNOWN;
        }
        return mspRCDStateKind.getRCDStateKind();
    }
    
}