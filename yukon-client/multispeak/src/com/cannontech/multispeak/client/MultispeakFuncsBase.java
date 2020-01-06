package com.cannontech.multispeak.client;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.soap.SOAPException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;
import org.w3c.dom.Node;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.MspPaoNameAliasEnum;
import com.cannontech.core.roleproperties.MultispeakMeterLookupFieldEnum;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.db.MultispeakInterface;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public abstract class MultispeakFuncsBase implements MultiSpeakVersionable {
    private final static Logger log = YukonLogManager.getLogger(MultispeakFuncsBase.class);

    @Autowired public AuthenticationService authenticationService;
    @Autowired public DeviceGroupService deviceGroupService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired public MultispeakDao multispeakDao;
    @Autowired public PaoDefinitionDao paoDefinitionDao;
    @Autowired public PointFormattingService pointFormattingService;
    @Autowired public RolePropertyDao rolePropertyDao;
    @Autowired public HttpComponentsMessageSender messageSender;

    /** A method that loads the response header. */
    public abstract void loadResponseHeader() throws MultispeakWebServiceException;

    /**
     * This method authenticate message header based on the userid/password.
     **/
    public abstract LiteYukonUser authenticateMsgHeader() throws MultispeakWebServiceException;

    public void logStrings(String intfaceName, String methodName, List<String> strings) {
        if (CollectionUtils.isNotEmpty(strings)) {
            for (String method : strings) {
                log.info("Return from " + intfaceName + " (" + methodName + "): " + method);
            }
        }
    }

    /**
     * gets Version from the request header
     * 
     * @return
     * @throws SOAPException
     */
    public MultiSpeakVersion getMSPVersion() throws SOAPException {

        MessageContext ctx = MessageContextHolder.getMessageContext();

        WebServiceMessage webServiceRequestMessage = ctx.getRequest();
        SaajSoapMessage saajSoapRequestMessage = (SaajSoapMessage) webServiceRequestMessage;
        Node nxtNode = saajSoapRequestMessage.getSaajMessage().getSOAPPart().getEnvelope().getBody().getFirstChild();
        return getMSPVersion(nxtNode);
    }

    public MultiSpeakVersion getMSPVersion(Node nxtNode) {
        if (nxtNode != null && nxtNode.getNamespaceURI() == null) {
            nxtNode = nxtNode.getNextSibling();
        }
        String soapAction = "";
        if (nxtNode != null) {
            soapAction = nxtNode.getNamespaceURI() + "/" + nxtNode.getLocalName();
        } else {
            log.warn("Namespace and method not identified. SOAPAction not set.");
        }
        if (soapAction.contains(MultiSpeakVersion.V3.getVersion())) {
            return MultiSpeakVersion.V3;
        } else if (soapAction.contains(MultiSpeakVersion.V5.getVersion())) {
            return MultiSpeakVersion.V5;
        }
        return MultiSpeakVersion.V3;
    }

    /**
     * This method should be called by every multispeak function!!!
     */
    public void init() throws MultispeakWebServiceException {
        loadResponseHeader();
    }

    /**
     * A common declaration of the getMethods method for all services to use.
     * 
     * @param interfaceName
     * @param methods
     * @return
     * @throws java.rmi.RemoteException
     */
    public List<String> getMethods(String interfaceName, List<String> methods) {
        logStrings(interfaceName, "GetMethods", methods);
        return methods;
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
        MspPaoNameAliasEnum paoNameAlias =
            globalSettingDao.getEnum(GlobalSettingType.MSP_PAONAME_ALIAS, MspPaoNameAliasEnum.class);
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
     * 
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
     * Helper method to construct a deviceName alias value value containing an
     * additional quantifier. Format is "value [quantifer]" NOTE: For
     * mspVendor.CompanyName = NISC -> Only add the quantifier for quantifier !=
     * 1.
     * 
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
     * Return the responseUrl. Used for asynchronous calls, where Yukon is the
     * Server. (Initiate_toYukon > Return_fromYukon; Notification_toVendor >
     * Return_fromVendor) Yukon receives the InitiateXxx. Then asynchoronously
     * pushes notificationXxx _to_ responseUrl. If responseURL is not blank,
     * return responseURL. Otherwise, loop through services and try to build the
     * responseURL from the mspVendor's URL and service endpoint
     * 
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
                Pair<String, MultiSpeakVersion> key = MultispeakVendor.buildMapKey(service, version());
                MultispeakInterface mspInterface = mspVendor.getMspInterfaceMap().get(key);
                if (mspInterface != null) {
                    return mspInterface.getMspEndpoint();
                }
            }
        }

        return "";
    }

    /**
     * Return the endpointUrl to send method/request _to_. Used for synchronous
     * calls, where Yukon is the Client. (Get_fromVendor > Return_toYukon)
     * 
     * @param mspVendor
     * @param services
     */
    public String getEndpointUrl(MultispeakVendor mspVendor, String services) {
        return encodeURL(getResponseUrl(mspVendor, null, services));
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
            } else if (inputDate instanceof Instant) {
                gc.setTimeInMillis(((Instant) inputDate).getMillis());
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

    /**
     * Encodes the given url by replacing unsafe ASCII characters
     * and converting them to a valid ASCII format
     * 
     * @param String - URL to be encoded
     * @return encodedURL
     */
    public String encodeURL(String endpointUrl) {
        try {
            URL url = new URL(endpointUrl);
            URI uri =
                new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
                    url.getQuery(), null);
            endpointUrl = uri.toString();
        } catch (MalformedURLException e) {
            log.error("URL " + endpointUrl + " is a malformed URL");
        } catch (URISyntaxException e) {
            log.error("URI " + endpointUrl + " is a malformed URL");
        }
        return endpointUrl;
    }

    /**
     * Helper method to return the CB_Server MultispeakVersion for primaryCIS
     * Returns null if mspVendor doesn't support any version of CB_Server.
     * Performs lookup in version order, returning first found.
     */
    public MultiSpeakVersion getPrimaryCISVersion(MultispeakVendor mspVendor) {
        MultispeakInterface cb_server_v3 = mspVendor.getMspInterfaceMap()
                .get(MultispeakVendor.buildMapKey(MultispeakDefines.CB_Server_STR,  MultiSpeakVersion.V3));
        
        if (cb_server_v3 != null) {
            return cb_server_v3.getVersion();
        } else {
            MultispeakInterface cb_server_v5 = mspVendor.getMspInterfaceMap()
                    .get(MultispeakVendor.buildMapKey(MultispeakDefines.CB_Server_STR,  MultiSpeakVersion.V5));
            
            if (cb_server_v5 != null) {
                return cb_server_v5.getVersion();
            }
        }
        return null;
    }

    /**
     * This method sets value of ConnectionTimeOut and ReadTimeout for HttpComponentsMessageSender.
     */
    private void setMsgSenderTimeOutValues(HttpComponentsMessageSender messageSender, MultispeakVendor mspVendor) {
        int timeOut = (int) mspVendor.getRequestMessageTimeout();
        messageSender.setReadTimeout(timeOut);
        messageSender.setConnectionTimeout(timeOut);
    }
    
    /**
     * Sets message sender/SSL message sender based on vendor settings.
     */
    public void setMsgSender(WebServiceTemplate webServiceTemplate, MultispeakVendor mspVendor) {
        if (mspVendor.getValidateCertificate()) {
            setMsgSenderTimeOutValues(messageSender, mspVendor);
            webServiceTemplate.setMessageSender(messageSender);
        } else {
            int timeOut = (int) mspVendor.getRequestMessageTimeout();
            webServiceTemplate.setMessageSender(HttpComponentsMessageSenderWithSSL.getInstance(timeOut));
        }

    }
    
    /** 
     * Builds the Primary CIS Vendor list for Global setting and Yukon Setup page.
     */
    public List<MultispeakVendor> getPrimaryCisVendorList() {
        List<MultispeakVendor> mspCisVendorList = multispeakDao.getMultispeakCISVendors();
        mspCisVendorList.add(0, MultispeakVendor.noneVendor);
        return mspCisVendorList;
    }
}