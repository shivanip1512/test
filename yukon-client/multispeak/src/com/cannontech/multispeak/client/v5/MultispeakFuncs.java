package com.cannontech.multispeak.client.v5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
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
import com.cannontech.common.model.Address;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.point.stategroup.Disconnect410State;
import com.cannontech.database.db.point.stategroup.PointStateHelper;
import com.cannontech.database.db.point.stategroup.RfnDisconnectStatusState;
import com.cannontech.msp.beans.v5.commontypes.AddressItems;
import com.cannontech.msp.beans.v5.commontypes.CSUnitsKind;
import com.cannontech.msp.beans.v5.commontypes.ErrorObject;
import com.cannontech.msp.beans.v5.commontypes.PhoneNumber;
import com.cannontech.msp.beans.v5.enumerations.PhoneTypeKind;
import com.cannontech.msp.beans.v5.enumerations.RCDStateKind;
import com.cannontech.msp.beans.v5.multispeak.Customer;
import com.cannontech.msp.beans.v5.multispeak.ElectricMeter;
import com.cannontech.msp.beans.v5.multispeak.Meters;
import com.cannontech.msp.beans.v5.multispeak.MspMeter;
import com.cannontech.msp.beans.v5.multispeak.WaterMeter;
import com.cannontech.msp.beans.v5.ws.response.MultiSpeakResponseMsgHeader;
import com.cannontech.multispeak.client.MessageContextHolder;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncsBase;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.data.MspReturnList;
import com.cannontech.multispeak.data.v5.MspRCDStateKind;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.user.YukonUserContext;

public class MultispeakFuncs extends MultispeakFuncsBase {
    private final static Logger log = YukonLogManager.getLogger(MultispeakFuncs.class);

    @Autowired public AuthenticationService authenticationService;
    @Autowired public MultispeakDao multispeakDao;
    @Autowired public DeviceGroupService deviceGroupService;
    @Autowired public PointFormattingService pointFormattingService;
    @Autowired public PaoDefinitionDao paoDefinitionDao;
    @Autowired private PhoneNumberFormattingService phoneNumberFormattingService;
    
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
    private static final QName QNAME_CALLER_RES = new QName("http://www.multispeak.org/V5.0/ws/response", "Caller");


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

    
    
    public void getHeader(SOAPMessage soapMessage, MultispeakVendor mspVendor) throws SOAPException {
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
        getHeader(headElement, "res", mspVendor);

    }

    public void getHeader(SOAPElement headElement, String prefix, MultispeakVendor mspVendor) throws SOAPException {

        SOAPElement callerElement = headElement.addChildElement("Caller", prefix);
        SOAPElement appNameElement = callerElement.addChildElement("AppName", "com");
        appNameElement.addTextNode("Yukon");
        SOAPElement appVersionElement = callerElement.addChildElement("AppVersion", "com");
        appVersionElement.addTextNode(VersionTools.getYUKON_VERSION());
        SOAPElement companyElement = callerElement.addChildElement("Company", "com");
        companyElement.addTextNode("Cannon");
        SOAPElement systemIDElement = callerElement.addChildElement("SystemID", "com");
        systemIDElement.addTextNode(mspVendor.getOutUserName());
        SOAPElement passwordElement = callerElement.addChildElement("Password", "com");
        passwordElement.addTextNode(mspVendor.getOutPassword());
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
            // the MultiSpeakResponseMsgHeader.Caller will be built with "dummy" values for userId and pwd
            // fields. The expectation is that getMultispeakVendorFromHeader will replace these values with
            // the correct values from the other vendor once it is loaded.
            MultispeakVendor mspVendor = multispeakDao.getMultispeakVendorFromCache(MultispeakDefines.MSP_COMPANY_YUKON,
                MultispeakDefines.MSP_APPNAME_YUKON);
            getHeader(soapMessage, mspVendor);

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
        return getNodeValueFromRequestSOAPMessage(QNAME_COMPANY, QNAME_CALLER);
    }

    /**
     * This method returns an App name from the request header.
     * @throws SOAPException
     **/
    public String getAppNameFromSOAPHeader() throws SOAPException {
        return getNodeValueFromRequestSOAPMessage(QNAME_APPNAME, QNAME_CALLER);
    }

    @Override
    public LiteYukonUser authenticateMsgHeader() throws MultispeakWebServiceException {
        LiteYukonUser user = null;
        try {
            String username = getNodeValueFromRequestSOAPMessage(QNAME_USERNAME, QNAME_CALLER);
            String password = getNodeValueFromRequestSOAPMessage(QNAME_PASSWORD, QNAME_CALLER);

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
            // update the responseHeader, replace with the correct userId and pwd from the "other" vendor now
            // that we have it loaded.
            updateNodeValueInResponseSOAPMessage(mspVendor.getOutUserName(), QNAME_USERNAME, QNAME_CALLER_RES);
            updateNodeValueInResponseSOAPMessage(mspVendor.getOutPassword(), QNAME_PASSWORD, QNAME_CALLER_RES);
            return mspVendor;
        } catch (NotFoundException | SOAPException e) {
            throw new MultispeakWebServiceException(e.getMessage());
        }
    }

    /**
     * Update Node value in response
     * 
     * @throws SOAPException
     */
    private void updateNodeValueInResponseSOAPMessage(String nodeValue, QName qNameToFind, QName callerQname)
            throws SOAPException {
        SOAPHeader header = getResponseSOAPMessage().getSOAPPart().getEnvelope().getHeader();
        SOAPElement childSoapElement = getElementFromSOAPMessage(header, qNameToFind, callerQname);
        childSoapElement.setValue(nodeValue);
    }

    /**
     * This method returns node value from SOAPHeader
     **/

    private String getNodeValueFromRequestSOAPMessage(QName qNameToFind, QName callerQname) throws SOAPException {
        SOAPHeader header = getRequestSOAPMessage().getSOAPPart().getEnvelope().getHeader();
        SOAPElement childSoapElement = getElementFromSOAPMessage(header, qNameToFind, callerQname);
        if (childSoapElement != null) {
            return childSoapElement.getValue();
        } else {
            return StringUtils.EMPTY;
        }
    }

    /**
     * This method returns child SOAPElement from SOAPHeader
     **/

    private SOAPElement getElementFromSOAPMessage(SOAPHeader header, QName qNameToFind, QName callerQname) throws SOAPException {
        SOAPElement childSoapElement = null;

        Iterator<?> headerElements = header.examineAllHeaderElements();
        while (headerElements.hasNext()) {
            SOAPHeaderElement headerElement = (SOAPHeaderElement) headerElements.next();
            Iterator<?> childElements = headerElement.getChildElements(callerQname);
            while (childElements.hasNext()) {
                Node soapNode = (Node) childElements.next();
                if (soapNode instanceof SOAPElement) {
                    SOAPElement element = (SOAPElement) soapNode;
                    childSoapElement = getFirstChildElementValue(element, qNameToFind);
                }
            }
        }
        return childSoapElement;
    }

    /**
     * This method returns child SOAPElement
     **/

    private SOAPElement getFirstChildElementValue(SOAPElement soapElement, QName qNameToFind) {
        SOAPElement childSoapElement = null;
        Iterator<?> childElements = soapElement.getChildElements(qNameToFind);
        while (childElements.hasNext()) {
            childSoapElement = (SOAPElement) childElements.next(); // use first
        }

        return childSoapElement;
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
                        nodeValue = getFirstChildElementValue(element, qNameToFind).getValue();
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

    @Override
    public MultiSpeakVersion version() {
        return MultiSpeakVersion.V5;
    }

    public List<MspMeter> getMspMeters(Meters meters) {
        List<MspMeter> mspMeters = new ArrayList<>();
   
        List<ElectricMeter> electricMeters = (null != meters.getElectricMeters()) ? meters.getElectricMeters().getElectricMeter() : null;
        if (CollectionUtils.isNotEmpty(electricMeters)) {
            mspMeters.addAll(electricMeters);
        }

        List<WaterMeter> waterMeters = (null != meters.getWaterMeters()) ? meters.getWaterMeters().getWaterMeter() : null;
        if (CollectionUtils.isNotEmpty(waterMeters)) {
            mspMeters.addAll(waterMeters);
        }
        return mspMeters;

    }

    public List<Address> getAddressList(AddressItems addressItems) {
        List<Address> addressList = new ArrayList<>();
        addressItems.getAddressItem().forEach(addressItem -> {
            if (addressItem.getAddress() != null) {
                Address address = new Address();
                address.setLocationAddress1(addressItem.getAddress().getAddress1());
                address.setLocationAddress2(addressItem.getAddress().getAddress2());
                address.setCityName(addressItem.getAddress().getCity());
                address.setStateCode(addressItem.getAddress().getState());
                address.setCounty(addressItem.getAddress().getCountry());
                address.setZipCode(addressItem.getAddress().getPostalCode());
                addressList.add(address);
            }
        });
        return addressList;
    }
    
    // Returns phone number (Home and Business) of the primary contact
    public Map<PhoneTypeKind, String> getPrimaryContacts(Customer mspCustomer) {
        Map<PhoneTypeKind, String> allPhoneNumbers = new HashMap<>();

        List<PhoneNumber> phoneNumber = new ArrayList<>();
        if (mspCustomer.getContactInfo() != null && mspCustomer.getContactInfo().getPhoneNumbers() != null) {
            phoneNumber = mspCustomer.getContactInfo().getPhoneNumbers().getPhoneNumber();

            phoneNumber.forEach(phNo -> {
                if (phNo.getPhoneType() != null) {
                    if (phNo.getPhoneType().getValue() == PhoneTypeKind.HOME
                        || phNo.getPhoneType().getValue() == PhoneTypeKind.BUSINESS) {

                        allPhoneNumbers.put(phNo.getPhoneType().getValue(), phoneNumberFormattingService.formatPhone(
                            phNo.getPhone().getAreaCode(), phNo.getPhone().getLocalNumber()));
                    }
                }
            });
        }
        return allPhoneNumbers;
    }
}