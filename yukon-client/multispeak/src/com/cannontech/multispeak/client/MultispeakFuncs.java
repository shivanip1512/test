package com.cannontech.multispeak.client;

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import com.cannontech.common.model.Address;
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
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.exception.PasswordExpiredException;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.point.stategroup.Disconnect410State;
import com.cannontech.database.db.point.stategroup.PointStateHelper;
import com.cannontech.database.db.point.stategroup.RfnDisconnectStatusState;
import com.cannontech.msp.beans.v3.ArrayOfErrorObject;
import com.cannontech.msp.beans.v3.ArrayOfString;
import com.cannontech.msp.beans.v3.Customer;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.LoadActionCode;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.ServiceLocation;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.data.MspLoadActionCode;
import com.cannontech.multispeak.data.MspReturnList;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.user.YukonUserContext;

public class MultispeakFuncs extends MultispeakFuncsBase {
    private final static Logger log = YukonLogManager.getLogger(MultispeakFuncs.class);

    @Autowired public AuthenticationService authenticationService;
    @Autowired public DeviceGroupService deviceGroupService;
    @Autowired public MultispeakDao multispeakDao;
    @Autowired public PaoDefinitionDao paoDefinitionDao;
    @Autowired public PointFormattingService pointFormattingService;
    @Autowired private ObjectFactory objectFactory;

    public void logErrorObjects(String intfaceName, String methodName, List<ErrorObject> objects) {
        if (CollectionUtils.isNotEmpty(objects)) {
            for (ErrorObject errorObject : objects) {
                log.info("Error Return from " + intfaceName + "(" + methodName + "): " + (errorObject == null ? "Null"
                        : errorObject.getObjectID() + " - " + errorObject.getErrorString()));
            }
        }
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
            MultispeakVendor mspVendor = multispeakDao.getMultispeakVendorFromCache(MultispeakDefines.MSP_COMPANY_YUKON,
                MultispeakDefines.MSP_APPNAME_YUKON);
            getHeader(header, mspVendor);

        } catch (NotFoundException | SOAPException e) {
            throw new MultispeakWebServiceException(e.getMessage());
        }
    }

    public SoapHeaderElement getHeader(SoapHeader header, MultispeakVendor mspVendor) throws SOAPException {

        YukonMultispeakMsgHeader yukonMspMsgHeader =
            new YukonMultispeakMsgHeader(mspVendor.getOutUserName(), mspVendor.getOutPassword(), version().getVersion());
        QName qname = new QName(version().namespace, "MultiSpeakMsgHeader");
        SoapHeaderElement headerElement = header.addHeaderElement(qname);
        headerElement.addAttribute(new QName("Version"), yukonMspMsgHeader.getVersion());
        headerElement.addAttribute(new QName("UserID"), yukonMspMsgHeader.getUserID());
        headerElement.addAttribute(new QName("Pwd"), yukonMspMsgHeader.getPwd());
        headerElement.addAttribute(new QName("AppName"), yukonMspMsgHeader.getAppName());
        headerElement.addAttribute(new QName("AppVersion"), yukonMspMsgHeader.getAppVersion());
        headerElement.addAttribute(new QName("Company"), yukonMspMsgHeader.getCompany());
        headerElement.addAttribute(new QName("CSUnits"), yukonMspMsgHeader.getCSUnits().value());
        return headerElement;
    }
    
    public SoapHeaderElement getResponseHeaderElement() throws MultispeakWebServiceException {
        SoapEnvelope env;
        try {
             env = getResponseMessageSOAPEnvelope();
        } catch (SOAPException e) {
            throw new MultispeakWebServiceException(e.getMessage());
        }
        SoapHeader header = env.getHeader();
        Iterator<SoapHeaderElement> it = header.examineHeaderElements(new QName("http://www.multispeak.org/Version_3.0", "MultiSpeakMsgHeader"));
        return it.next();
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
     * This method returns an attribute value from the request header.
     * @param soapHeader - request header.
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
     * This method returns an Company name from the request header.
     * @param soapHeader - request header.
     * @param multispeakVersion - Multispeak version.
     * @return String - attribute value.
     **/
    public String getCompanyNameFromSOAPHeader(SoapHeader header) {
        return getAtributeFromSOAPHeader(header, MultispeakDefines.COMPANY);
    }

    /**
     * This method returns an App name from the request header.
     * @param soapHeader - request header.
     * @param multispeakVersion - Multispeak version.
     * @return String - attribute value.
     **/
    public String getAppNameFromSOAPHeader(SoapHeader header) {
        return getAtributeFromSOAPHeader(header, MultispeakDefines.APPNAME);
    }

    @Override
    public LiteYukonUser authenticateMsgHeader() throws MultispeakWebServiceException {
        try {
            SoapEnvelope env = getRequestMessageSOAPEnvelope();
            SoapHeader soapHeader = env.getHeader();
            String username = getAtributeFromSOAPHeader(soapHeader, "UserID");
            String password = getAtributeFromSOAPHeader(soapHeader, "Pwd");
            LiteYukonUser user = authenticationService.login(username, password);
            return user;

        } catch (PasswordExpiredException e) {
            throw new MultispeakWebServiceException("Password expired.", e);
        } catch (BadAuthenticationException e) {
            throw new MultispeakWebServiceException("User authentication failed.", e);
        }
    }

    /**
     * This method returns an multispeak vendor.
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
            // update the responseHeader, replace with the correct userId and pwd from the "other" vendor now that we have it loaded.
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
    
    public String customerToString(Customer customer) {
        String returnStr = "";

        returnStr += (StringUtils.isNotBlank(customer.getObjectID()) ? "Customer: " + customer.getObjectID() + "/r/n"
                : "");

        String tempString = (StringUtils.isNotBlank(customer.getLastName()) ? customer.getLastName() + ", " : "") + (StringUtils.isNotBlank(customer.getFirstName()) ? customer.getFirstName() + " "
                : "") + (StringUtils.isNotBlank(customer.getMName()) ? customer.getMName() : "");

        if (StringUtils.isNotBlank(tempString)) {
            returnStr += "Name: " + tempString + "/r/n";
        }

        returnStr += (StringUtils.isNotBlank(customer.getDBAName()) ? "DBA Name: " + customer.getDBAName() + "/r/n"
                : "");

        tempString = (StringUtils.isNotBlank(customer.getHomeAc()) ? "(" + customer.getHomeAc() + ") " : "");
        tempString += (StringUtils.isNotBlank(customer.getHomePhone()) ? customer.getHomePhone() : "");
        if (StringUtils.isNotBlank(tempString)) {
            returnStr += "Home Phone: " + tempString + "/r/n";
        }

        tempString = (StringUtils.isNotBlank(customer.getDayAc()) ? "(" + customer.getDayAc() + ") " : "");
        tempString += (StringUtils.isNotBlank(customer.getDayPhone()) ? customer.getDayPhone() : "");
        if (StringUtils.isNotBlank(tempString)) {
            returnStr += "Home Phone: " + tempString + "/r/n";
        }

        returnStr += (StringUtils.isNotBlank(customer.getBillAddr1()) ? "Bill Addr1: " + customer.getBillAddr1() + "/r/n"
                : "");
        returnStr += (StringUtils.isNotBlank(customer.getBillAddr2()) ? "Bill Addr2: " + customer.getBillAddr2() + "/r/n"
                : "");

        tempString = (StringUtils.isNotBlank(customer.getBillCity()) ? customer.getBillCity() + ", " : "");
        tempString += (StringUtils.isNotBlank(customer.getBillState()) ? customer.getBillState() + " " : "");
        tempString += (StringUtils.isNotBlank(customer.getBillZip()) ? customer.getBillZip() : "");
        if (StringUtils.isNotBlank(tempString)) {
            returnStr += "City/State/Zip: " + tempString + "/r/n";
        }

        return returnStr;
    }

    public String serviceLocationToString(ServiceLocation serviceLocation) {
        String returnStr = "";

        /*
         * NISC fields availalbe toDate 20070101 private
         * com.cannontech.multispeak.deploy.service.Network network;
         * network.getBoardDist(); network.getDistrict();
         * network.getEaLoc().getName(); network.getFeeder();
         * network.getLinkedTransformer().getBankID();
         * network.getPhaseCd().getValue(); network.getSubstationCode();
         * revenueClass; billingCycle; route; specialNeeds; connectDate;
         */

        returnStr += (StringUtils.isNotBlank(serviceLocation.getObjectID()) ? "Service Location: " + serviceLocation.getObjectID() + "/r/n"
                : "");
        returnStr += (StringUtils.isNotBlank(serviceLocation.getCustID()) ? "Customer ID: " + serviceLocation.getCustID() + "/r/n"
                : "");
        returnStr += (StringUtils.isNotBlank(serviceLocation.getAccountNumber()) ? "Account #: " + serviceLocation.getAccountNumber() + "/r/n"
                : "");
        returnStr += (StringUtils.isNotBlank(serviceLocation.getGridLocation()) ? "Grid Location: " + serviceLocation.getGridLocation() + "/r/n"
                : "");

        returnStr += (StringUtils.isNotBlank(serviceLocation.getBillingCycle()) ? "Billing Cycle: " + serviceLocation.getBillingCycle() + "/r/n"
                : "");
        returnStr += (StringUtils.isNotBlank(serviceLocation.getServType()) ? "Service Type: " + serviceLocation.getServType() + "/r/n"
                : "");
        returnStr += (StringUtils.isNotBlank(serviceLocation.getServStatus()) ? "Service Status: " + serviceLocation.getServStatus() + "/r/n"
                : "");

        returnStr += (StringUtils.isNotBlank(serviceLocation.getServAddr1()) ? "Service Addr1: " + serviceLocation.getServAddr1() + "/r/n"
                : "");
        returnStr += (StringUtils.isNotBlank(serviceLocation.getServAddr2()) ? "Service Addr2: " + serviceLocation.getServAddr2() + "/r/n"
                : "");

        String tempString = (StringUtils.isNotBlank(serviceLocation.getServCity()) ? serviceLocation.getServCity() + ", "
                : "");
        tempString += (StringUtils.isNotBlank(serviceLocation.getServState()) ? serviceLocation.getServState() + " "
                : "");
        tempString += (StringUtils.isNotBlank(serviceLocation.getServZip()) ? serviceLocation.getServZip() : "");
        if (StringUtils.isNotBlank(tempString)) {
            returnStr += "City/State/Zip: " + tempString + "/r/n";
        }

        return returnStr;
    }


    /**
     * @return Returns the deviceGroup for a SystemGroupEnum
     */
    public StoredDeviceGroup getSystemGroup(SystemGroupEnum systemGroupEnum) throws NotFoundException {
        // WE MAY HAVE SOME PROBLEMS HERE WITH THE EXPLICIT CAST TO
        // STOREDDEVICEGROUP....
        StoredDeviceGroup deviceGroup = (StoredDeviceGroup) deviceGroupService.resolveGroupName(systemGroupEnum);
        return deviceGroup;
    }


    /**
     * Helper method to parse the main alias value from a string containing a
     * quantifier too. Format of value is expected to be "value [quantifier]".
     * After parse, returned value will be "value" (the [quantifier] part will
     * be removed).
     * @param value
     * @return
     */
    public String parseAliasWithQuantifier(String value) {
        String parsedValue = value;

        int bracketIndex = parsedValue.lastIndexOf("[");
        if (bracketIndex > 0) { // found an instance of [
            // truncate to the underscore index, not inclusive of. This should
            // remove things like 12345 [3] and make 12345 must trim to remove
            // end of string whitespace
            parsedValue = parsedValue.substring(0, bracketIndex).trim();
        }
        return parsedValue;
    }

    /**
     * Helper method to update responseHeader.objectsRemaining and
     * responseHeader.lastSent
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

    /**
     * Translates the rawState into a loadActionCode based on the type of meter
     * and expected state group for that type. Returns loadActionCode.Unknown
     * when cannot be determined.
     * @param meter
     * @return
     */
    public LoadActionCode getLoadActionCode(YukonDevice yukonDevice, PointValueHolder pointValueHolder) {

        MspLoadActionCode mspLoadActionCode;
        try {

            log.debug("Returning disconnect status from cache: "
                + pointFormattingService.getCachedInstance().getValueString(pointValueHolder, Format.FULL, YukonUserContext.system));

            boolean isRfnDisconnect = paoDefinitionDao.isTagSupported(yukonDevice.getPaoIdentifier().getPaoType(),
                                                                      PaoTag.DISCONNECT_RFN);
            if (isRfnDisconnect) {
                RfnDisconnectStatusState pointState = PointStateHelper.decodeRawState(RfnDisconnectStatusState.class,
                                                                                      pointValueHolder.getValue());
                mspLoadActionCode = MspLoadActionCode.getForRfnState(pointState);
                log.debug("returning loadActionCode for RFN: " + mspLoadActionCode);
            } else { // assume everything else is PLC
                Disconnect410State pointState = PointStateHelper.decodeRawState(Disconnect410State.class,
                                                                                pointValueHolder.getValue());
                mspLoadActionCode = MspLoadActionCode.getForPlcState(pointState);
                log.debug("returning loadActionCode for PLC: " + mspLoadActionCode);
            }
        } catch (IllegalArgumentException e) {
            // we were unable to decode the rawState
            log.warn("Unable to decode rawState. value:" + pointValueHolder.getValue());
            return LoadActionCode.UNKNOWN;
        }
        return mspLoadActionCode.getLoadActionCode();
    }

    

    public ArrayOfErrorObject toArrayOfErrorObject(List<ErrorObject> errorObjects) {
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        if (errorObjects != null) {
            arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        }
        return arrayOfErrorObject;
    }

    public ArrayOfString toArrayOfString(List<String> strings) {
        ArrayOfString arrayOfString = objectFactory.createArrayOfString();
        if (strings != null) {
            arrayOfString.getString().addAll(strings);
        }
        return arrayOfString;
    }
    
    public Address getCustomerAddressInfo(Customer mspCustomer) {
        Address address = new Address();
        address.setLocationAddress1(mspCustomer.getBillAddr1());
        address.setLocationAddress2(mspCustomer.getBillAddr2());
        address.setCityName(mspCustomer.getBillCity());
        address.setStateCode(mspCustomer.getBillState());
        address.setZipCode(mspCustomer.getBillZip());
        return address;
    }

    public Address getServLocAddressInfo(ServiceLocation mspServLoc) {
        Address address = new Address();
        address.setLocationAddress1(mspServLoc.getServAddr1());
        address.setLocationAddress2(mspServLoc.getServAddr2());
        address.setCityName(mspServLoc.getServCity());
        address.setStateCode(mspServLoc.getServState());
        address.setZipCode(mspServLoc.getServZip());
        return address;
    }

    @Override
    public MultiSpeakVersion version() {
        return MultiSpeakVersion.V3;
    }
}