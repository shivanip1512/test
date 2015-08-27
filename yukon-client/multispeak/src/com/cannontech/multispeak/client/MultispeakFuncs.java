package com.cannontech.multispeak.client;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.exception.PasswordExpiredException;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.roleproperties.CisDetailRolePropertyEnum;
import com.cannontech.core.roleproperties.MspPaoNameAliasEnum;
import com.cannontech.core.roleproperties.MultispeakMeterLookupFieldEnum;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
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
import com.cannontech.multispeak.db.MultispeakInterface;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;

public class MultispeakFuncs {
    private final static Logger log = YukonLogManager.getLogger(MultispeakFuncs.class);

    @Autowired public AuthenticationService authenticationService;
    @Autowired public DeviceGroupService deviceGroupService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired public MultispeakDao multispeakDao;
    @Autowired public PaoDefinitionDao paoDefinitionDao;
    @Autowired public PointFormattingService pointFormattingService;
    @Autowired public RolePropertyDao rolePropertyDao;
    @Autowired private ObjectFactory objectFactory;

    public void logStrings(String intfaceName, String methodName, List<String> strings) {
        if (CollectionUtils.isNotEmpty(strings)) {
            for (String method : strings) {
                log.info("Return from " + intfaceName + " (" + methodName + "): " + method);
            }
        }
    }

    public void logErrorObjects(String intfaceName, String methodName, List<ErrorObject> objects) {
        if (CollectionUtils.isNotEmpty(objects)) {
            for (ErrorObject errorObject : objects) {
                log.info("Error Return from " + intfaceName + "(" + methodName + "): " + (errorObject == null ? "Null"
                        : errorObject.getObjectID() + " - " + errorObject.getErrorString()));
            }
        }
    }

    /** A method that loads the response header. */
    public void loadResponseHeader() throws MultispeakWebServiceException {
        SoapEnvelope env;
        try {
            MultispeakVendor mspVendor = multispeakDao.getMultispeakVendorFromCache(MultispeakDefines.MSP_COMPANY_YUKON,
                MultispeakDefines.MSP_APPNAME_YUKON);
            env = getResponseMessageSOAPEnvelope();
            SoapHeader header = env.getHeader();
            mspVendor.getHeader(header);
        } catch (NotFoundException | SOAPException e) {
            throw new MultispeakWebServiceException(e.getMessage());
        }
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
        if (nxtNode.getNamespaceURI() == null) {
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
     * This method should be called by every multispeak function!!!
     */
    public void init() throws MultispeakWebServiceException {
        loadResponseHeader();
    }

    /**
     * A common declaration of the getMethods method for all services to use.
     * @param interfaceName
     * @param methods
     * @return
     * @throws java.rmi.RemoteException
     */
    public List<String> getMethods(String interfaceName, List<String> methods) {
        logStrings(interfaceName, "getMethods", methods);
        return methods;
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
    public String getCompanyNameFromSOAPHeader(SoapHeader header, MultiSpeakVersion multispeakVersion) {
        return getAtributeFromSOAPHeader(header, MultispeakDefines.COMPANY);
    }

    /**
     * This method returns an App name from the request header.
     * @param soapHeader - request header.
     * @param multispeakVersion - Multispeak version.
     * @return String - attribute value.
     **/
    public String getAppNameFromSOAPHeader(SoapHeader header, MultiSpeakVersion multispeakVersion) {
        return getAtributeFromSOAPHeader(header, MultispeakDefines.APPNAME);
    }

    /**
     * This method authenticate message header based on the userid/password.
     **/
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
    public MultispeakVendor getMultispeakVendorFromHeader(MultiSpeakVersion version)
            throws MultispeakWebServiceException {
        try {
            SoapEnvelope env = getRequestMessageSOAPEnvelope();
            SoapHeader soapHeader = env.getHeader();
            String companyName = getCompanyNameFromSOAPHeader(soapHeader, version);
            String appName = getAppNameFromSOAPHeader(soapHeader, version);
            return multispeakDao.getMultispeakVendorFromCache(companyName, appName);
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

    public boolean usesPaoNameAliasExtension() {
        String paoNameAliasExtension = getPaoNameAliasExtension();
        return StringUtils.isNotBlank(paoNameAliasExtension);
    }

    public String getPaoNameAliasExtension() {
        return globalSettingDao.getString(GlobalSettingType.MSP_PAONAME_EXTENSION);
    }

    public MultispeakMeterLookupFieldEnum getMeterLookupField() {
        return globalSettingDao.getEnum(GlobalSettingType.MSP_METER_LOOKUP_FIELD, MultispeakMeterLookupFieldEnum.class);
    }

    public MspPaoNameAliasEnum getPaoNameAlias() {
        MspPaoNameAliasEnum paoNameAlias = globalSettingDao.getEnum(GlobalSettingType.MSP_PAONAME_ALIAS,
                                                                    MspPaoNameAliasEnum.class);
        return paoNameAlias;
    }

    /**
     * @return Returns the primaryCIS vendorID.
     */
    public int getPrimaryCIS() {
        return globalSettingDao.getInteger(GlobalSettingType.MSP_PRIMARY_CB_VENDORID);
    }

    /**
     * Return true if mspVendor is the primaryCIS Vendor, else return false;
     * Also returns false if mspVendor or it's vendorId are null.
     * @param mspVendor
     * @return boolean
     */
    public boolean isPrimaryCIS(MultispeakVendor mspVendor) {
        if (mspVendor != null && mspVendor.getVendorID() != null) {
            int primaryCIS = getPrimaryCIS();
            return mspVendor.getVendorID().intValue() == primaryCIS;
        }
        return false;
    }

    /**
     * @return Returns the billingCycle parent Device Group
     */
    public DeviceGroup getBillingCycleDeviceGroup() throws NotFoundException {
        // WE MAY HAVE SOME PROBLEMS HERE WITH THE EXPLICIT CAST TO
        // STOREDDEVICEGROUP....
        String value = globalSettingDao.getString(GlobalSettingType.MSP_BILLING_CYCLE_PARENT_DEVICEGROUP);
        DeviceGroup deviceGroup = deviceGroupService.resolveGroupName(value);
        return deviceGroup;
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
     * Helper method to construct a deviceName alias value value containing an
     * additional quantifier. Format is "value [quantifer]" NOTE: For
     * mspVendor.CompanyName = NISC -> Only add the quantifier for quantifier !=
     * 1.
     * @param value
     * @param quantifier
     * @return
     */
    public String buildAliasWithQuantifier(String value, String quantifier, MultispeakVendor mspVendor) {
        boolean isNISC = mspVendor.getCompanyName().equalsIgnoreCase("NISC");
        String valueWithQuantifier = value;

        if (StringUtils.isNotBlank(quantifier)) {
            if (isNISC && StringUtils.equals(quantifier, "1")) { // NISC vendor
                                                                 // specific
                                                                 // handling
                return valueWithQuantifier;
            }
            valueWithQuantifier += " [" + quantifier + "]";
        }
        return valueWithQuantifier;
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
     * This method returns the cisInfoWidgetName for the user. If it is NONE, it
     * will use the venderId and proceed as if they actually had the MULTISPEAK
     * value set.
     */
    public String getCisDetailWidget(LiteYukonUser liteYukonUser) {
        boolean cisDetailWidgetEnabled = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.CIS_DETAIL_WIDGET_ENABLED,
                                                                                 liteYukonUser);
        if (cisDetailWidgetEnabled) {
            CisDetailRolePropertyEnum cisDetailRoleProperty = rolePropertyDao.getPropertyEnumValue(YukonRoleProperty.CIS_DETAIL_TYPE,
                                                                                                   CisDetailRolePropertyEnum.class,
                                                                                                   liteYukonUser);
            String cisInfoWidgetName = cisDetailRoleProperty.getWidgetName();
            if (cisInfoWidgetName == null) {
                int vendorId = getPrimaryCIS();
                if (vendorId > 0) {
                    cisInfoWidgetName = CisDetailRolePropertyEnum.MULTISPEAK.getWidgetName();
                }
            }
            return cisInfoWidgetName;
        }
        return null;
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

    /**
     * Return the responseUrl. Used for asynchronous calls, where Yukon is the
     * Server. (Initiate_toYukon > Return_fromYukon; Notification_toVendor >
     * Return_fromVendor) Yukon receives the InitiateXxx. Then asynchoronously
     * pushes notificationXxx _to_ responseUrl. If responseURL is not blank,
     * return responseURL. Otherwise, loop through services and try to build the
     * responseURL from the mspVendor's URL and service endpoint
     * @param mspVendor
     * @param responseURL
     * @param services
     * @return responseURL for notification messages
     */
    public String getResponseUrl(MultispeakVendor mspVendor, String responseURL, String... services) {
        if (StringUtils.isNotBlank(responseURL)) {
            return responseURL;
        } else {
            for (String service : services) {
                MultispeakInterface mspInterface = mspVendor.getMspInterfaceMap().get(service);
                if (mspInterface != null) {
                    return mspVendor.getUrl() + mspInterface.getMspEndpoint();
                }
            }
        }

        // return empty response URL...may need to do some more here? We don't
        // expect to ever be in this situation!!
        return "";
    }

    /**
     * Return the endpointUrl to send method/request _to_. Used for synchronous
     * calls, where Yukon is the Client. (Get_fromVendor > Return_toYukon)
     * @param mspVendor
     * @param services
     */
    public String getEndpointUrl(MultispeakVendor mspVendor, String services) {
        return getResponseUrl(mspVendor, null, services);
    }
    
     /**
     * This method will return XMLGregorianCalendar type for given date/Calendar/null in case of current date
     * 
     * @param Object - Input date/Calendar/null
     * @return eventime
     */
    public static XMLGregorianCalendar toXMLGregorianCalendar(Object inputDate) {
        XMLGregorianCalendar eventTime = null;

        try {
            GregorianCalendar gc = (GregorianCalendar) GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
            if (inputDate instanceof Date) {
                Date date = (Date) inputDate;
                gc.setTime(date);
            } else if (inputDate instanceof Calendar) {
                Calendar cal = (Calendar) inputDate;
                gc.setTimeInMillis(cal.getTimeInMillis());
            } else {
                Date date = new Date();
                gc.setTime(date);
            }
            eventTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        } catch (DatatypeConfigurationException e) {
            log.warn("caught exception in parsing event time", e);
        }
        return eventTime;
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
}