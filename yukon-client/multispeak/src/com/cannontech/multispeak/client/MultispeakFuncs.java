/*
 * Created on Aug 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.cannontech.multispeak.client;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Iterator;

import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.message.PrefixedQName;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeader;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.LogHelper;
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
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.data.MspLoadActionCode;
import com.cannontech.multispeak.data.MspReturnList;
import com.cannontech.multispeak.deploy.service.Customer;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.LoadActionCode;
import com.cannontech.multispeak.deploy.service.ServiceLocation;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.SystemUserContext;

public class MultispeakFuncs
{
    private final Logger log = YukonLogManager.getLogger(MultispeakFuncs.class);
    
    @Autowired public MultispeakDao multispeakDao;
    @Autowired public RolePropertyDao rolePropertyDao;
    @Autowired public DeviceGroupService deviceGroupService;
    @Autowired public AuthenticationService authenticationService;
    @Autowired public PaoDefinitionDao paoDefinitionDao;
    @Autowired public PointFormattingService pointFormattingService;
    @Autowired private GlobalSettingDao globalSettingDao;

    public void logStrings(String intfaceName, String methodName, String[] strings)
	{
		if (strings != null)
		{
			for (int i = 0; i < strings.length; i++)
			{
				log.info("Return from " + intfaceName + " (" + methodName + "): " + strings[i]);
			}
		}
	}
	
	public void logErrorObjects(String intfaceName, String methodName, ErrorObject[] objects )
	{
		if (objects != null)
		{
			for (int i = 0; i < objects.length; i++)
			{
			    log.info("Error Return from " + intfaceName + "(" + methodName + "): " + (objects[i] == null? "Null" : objects[i].getObjectID() +" - " + objects[i].getErrorString()));
			}
		}
	}
    
	public void loadResponseHeader() throws RemoteException 
	{
		SOAPEnvelope env = getResponseMessageSOAPEnvelope();
		try{
		    MultispeakVendor mspVendor = multispeakDao.getMultispeakVendorFromCache(MultispeakVendor.CANNON_MSP_COMPANYNAME, "");
	         // Set Header
            env.addHeader(mspVendor.getHeader());
        }
        catch (NotFoundException e) {
            throw new RemoteException(e.getMessage());
        }
	}

    public YukonMultispeakMsgHeader getResponseHeader() throws RemoteException {
        SOAPEnvelope env = getResponseMessageSOAPEnvelope();
        return (YukonMultispeakMsgHeader)env.getHeaderByName("http://www.multispeak.org/Version_3.0", "MultiSpeakMsgHeader").getObjectValue();
    }
    
    private SOAPEnvelope getResponseMessageSOAPEnvelope() throws RemoteException {
        // Get current message context
        MessageContext ctx = MessageContext.getCurrentContext();
        // Get SOAP envelope of response
        SOAPEnvelope env = ctx.getResponseMessage().getSOAPEnvelope();
        return env;
    }

	/**
	 * This method should be called by every multispeak function!!!
	 *
	 */
	public void init() throws RemoteException
	{
		try {
		    log.info("MSP MESSAGE RECEIVED: " + MessageContext.getCurrentContext().getCurrentMessage().getSOAPPartAsString().toString());
		} catch (AxisFault e) {
		    log.error(e);
		}
		loadResponseHeader();
	}
	
	/**
	 * A common declaration of the getMethods method for all services to use.
	 * @param interfaceName
	 * @param methods
	 * @return
	 * @throws java.rmi.RemoteException
	 */
	public String[] getMethods(String interfaceName, String[] methods){
		logStrings(interfaceName, "getMethods", methods);
		return methods;
	}
	
	private String getAtributeFromSOAPHeader(String attributeName) throws java.rmi.RemoteException
	{
		String attributeValue = null;
		try
		{
			// Gets the SOAPHeader for the Request Message
			SOAPHeader soapHead = (SOAPHeader)MessageContext.getCurrentContext().getRequestMessage().getSOAPEnvelope().getHeader();
			// Gets all the SOAPHeaderElements into an Iterator
			Iterator itrElements= soapHead.getChildElements();
			while(itrElements.hasNext())
			{
				org.apache.axis.message.SOAPHeaderElement ele = (org.apache.axis.message.SOAPHeaderElement)itrElements.next();
                Iterator iterAllAttr = ele.getAllAttributes();
                while (iterAllAttr.hasNext()){
                    PrefixedQName pQName = (PrefixedQName)iterAllAttr.next();
                    if( pQName.getQualifiedName().equalsIgnoreCase(attributeName)){
                        attributeValue = ele.getAttribute(pQName.getQualifiedName());
                        break;
                    }
                }
			}
		}
		catch (SOAPException e) {
		    log.error(e);
		}
		return attributeValue;
	}
	public String getCompanyNameFromSOAPHeader() throws java.rmi.RemoteException {
		return getAtributeFromSOAPHeader("company");
	}
    public String getAppNameFromSOAPHeader() throws java.rmi.RemoteException
    {
        return getAtributeFromSOAPHeader("appname");
    }    

    public LiteYukonUser authenticateMsgHeader() throws java.rmi.RemoteException {
        try {
            String username = getAtributeFromSOAPHeader("userID");
            String password = getAtributeFromSOAPHeader("pwd");
            //TEMPORARY FOR TESTING
//            username = "yukon";
//            password = "yukon";
        	LiteYukonUser user = authenticationService.login(username, password);

            return user;
        } catch (PasswordExpiredException e) {
            throw new RemoteException(e.getMessage());
        } catch(BadAuthenticationException e) {
        	throw new RemoteException(e.getMessage());
        }
    }
    
    public MultispeakVendor getMultispeakVendorFromHeader() throws RemoteException {
        String companyName = getCompanyNameFromSOAPHeader();
        String appName = getAppNameFromSOAPHeader();
        
        try{
            return multispeakDao.getMultispeakVendorFromCache(companyName, appName);
        }
        catch (NotFoundException e) {
            throw new RemoteException(e.getMessage());
        }
    }
 
    public String customerToString(Customer customer) {
        String returnStr = "";
        
        returnStr += (StringUtils.isNotBlank(customer.getObjectID()) ? "Customer: " + customer.getObjectID() + "/r/n" : "");
        
        String tempString = (StringUtils.isNotBlank(customer.getLastName()) ? customer.getLastName() + ", " : "") +
                     (StringUtils.isNotBlank(customer.getFirstName()) ? customer.getFirstName() + " " : "" ) + 
                     (StringUtils.isNotBlank(customer.getMName()) ? customer.getMName() : "" );
        if( StringUtils.isNotBlank(tempString))
            returnStr += "Name: " + tempString  + "/r/n";
        
        returnStr += (StringUtils.isNotBlank(customer.getDBAName()) ? "DBA Name: " +customer.getDBAName() + "/r/n": "");
        
        tempString = (StringUtils.isNotBlank(customer.getHomeAc()) ? "(" + customer.getHomeAc() +") " : "");
        tempString += (StringUtils.isNotBlank(customer.getHomePhone()) ? customer.getHomePhone(): "");
        if( StringUtils.isNotBlank(tempString))
            returnStr += "Home Phone: " + tempString + "/r/n";
        
        tempString = (StringUtils.isNotBlank(customer.getDayAc()) ? "(" + customer.getDayAc() +") " : "");
        tempString += (StringUtils.isNotBlank(customer.getDayPhone()) ? customer.getDayPhone(): "");
        if( StringUtils.isNotBlank(tempString))
            returnStr += "Home Phone: " + tempString + "/r/n";
        
        returnStr += (StringUtils.isNotBlank(customer.getBillAddr1()) ? "Bill Addr1: " + customer.getBillAddr1() + "/r/n": "");
        returnStr += (StringUtils.isNotBlank(customer.getBillAddr2()) ? "Bill Addr2: " + customer.getBillAddr2() + "/r/n": "");
        
        tempString = (StringUtils.isNotBlank(customer.getBillCity()) ? customer.getBillCity() + ", ": "");
        tempString += (StringUtils.isNotBlank(customer.getBillState()) ? customer.getBillState() + " " : "");
        tempString += (StringUtils.isNotBlank(customer.getBillZip()) ? customer.getBillZip() : "");
        if( StringUtils.isNotBlank(tempString))
            returnStr += "City/State/Zip: " + tempString + "/r/n";

        return returnStr;
    }

    public String serviceLocationToString(ServiceLocation serviceLocation) {
        String returnStr = "";
        
        /* NISC fields availalbe toDate 20070101
        private com.cannontech.multispeak.deploy.service.Network network;
        network.getBoardDist();
        network.getDistrict();
        network.getEaLoc().getName();
        network.getFeeder();
        network.getLinkedTransformer().getBankID();
        network.getPhaseCd().getValue();
        network.getSubstationCode();
        revenueClass;
        billingCycle;
        route;
        specialNeeds;
        connectDate;*/        
        
        returnStr += (StringUtils.isNotBlank(serviceLocation.getObjectID()) ? "Service Location: " + serviceLocation.getObjectID() + "/r/n" : "");
        returnStr += (StringUtils.isNotBlank(serviceLocation.getCustID()) ? "Customer ID: " +serviceLocation.getCustID() + "/r/n": "");
        returnStr += (StringUtils.isNotBlank(serviceLocation.getAccountNumber()) ? "Account #: " +serviceLocation.getAccountNumber() + "/r/n": "");
        returnStr += (StringUtils.isNotBlank(serviceLocation.getGridLocation()) ? "Grid Location: " +serviceLocation.getGridLocation() + "/r/n": "");
        
        returnStr += (StringUtils.isNotBlank(serviceLocation.getBillingCycle()) ? "Billing Cycle: " +serviceLocation.getBillingCycle() + "/r/n": "");
        returnStr += (StringUtils.isNotBlank(serviceLocation.getServType()) ? "Service Type: " +serviceLocation.getServType() + "/r/n": "");
        returnStr += (StringUtils.isNotBlank(serviceLocation.getServStatus()) ? "Service Status: " +serviceLocation.getServStatus() + "/r/n": "");
        
        returnStr += (StringUtils.isNotBlank(serviceLocation.getServAddr1()) ? "Service Addr1: " + serviceLocation.getServAddr1() + "/r/n": "");
        returnStr += (StringUtils.isNotBlank(serviceLocation.getServAddr2()) ? "Service Addr2: " + serviceLocation.getServAddr2() + "/r/n": "");
        
        String tempString = (StringUtils.isNotBlank(serviceLocation.getServCity()) ? serviceLocation.getServCity() + ", ": "");
        tempString += (StringUtils.isNotBlank(serviceLocation.getServState()) ? serviceLocation.getServState() + " " : "");
        tempString += (StringUtils.isNotBlank(serviceLocation.getServZip()) ? serviceLocation.getServZip() : "");
        if( StringUtils.isNotBlank(tempString))
            returnStr += "City/State/Zip: " + tempString + "/r/n";

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
        MspPaoNameAliasEnum paoNameAlias = globalSettingDao.getEnum(GlobalSettingType.MSP_PAONAME_ALIAS, MspPaoNameAliasEnum.class);
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
        if( mspVendor != null && mspVendor.getVendorID() != null) {
            int primaryCIS = getPrimaryCIS();
            return mspVendor.getVendorID().intValue() == primaryCIS;
        }
        return false;        
    }
    /**
     * @return Returns the billingCycle parent Device Group
     */
    public DeviceGroup getBillingCycleDeviceGroup() throws NotFoundException{
        //WE MAY HAVE SOME PROBLEMS HERE WITH THE EXPLICIT CAST TO STOREDDEVICEGROUP....
        String value = globalSettingDao.getString(GlobalSettingType.MSP_BILLING_CYCLE_PARENT_DEVICEGROUP);
        DeviceGroup deviceGroup = deviceGroupService.resolveGroupName(value);
        return deviceGroup;
    }
    
    /**
     * @return Returns the deviceGroup for a SystemGroupEnum
     */
    public StoredDeviceGroup getSystemGroup(SystemGroupEnum systemGroupEnum) throws NotFoundException{
        //WE MAY HAVE SOME PROBLEMS HERE WITH THE EXPLICIT CAST TO STOREDDEVICEGROUP....
        StoredDeviceGroup deviceGroup = (StoredDeviceGroup)deviceGroupService.resolveGroupName(systemGroupEnum.getFullPath());
        return deviceGroup;
    }
    
    /**
     * Helper method to construct a deviceName alias value value containing an additional quantifier.
     * Format is "value [quantifer]"
     * NOTE:  For mspVendor.CompanyName = NISC -> Only add the quantifier for quantifier != 1.
     * @param value
     * @param quantifier 
     * @return
     */
    public String buildAliasWithQuantifier(String value, String quantifier, MultispeakVendor mspVendor) {
        boolean isNISC = mspVendor.getCompanyName().equalsIgnoreCase("NISC");
        String valueWithQuantifier = value;
        
        if (StringUtils.isNotBlank(quantifier)) {
            if (isNISC && StringUtils.equals(quantifier, "1")) {   // NISC vendor specific handling
                return valueWithQuantifier;
            }
            valueWithQuantifier += " [" + quantifier + "]";
        }       
        return valueWithQuantifier;
    }
    
    /**
     * Helper method to parse the main alias value from a string containing a quantifier too.
     * Format of value is expected to be "value [quantifier]".
     * After parse, returned value will be "value" (the [quantifier] part will be removed).
     * @param value
     * @return
     */
    public String parseAliasWithQuantifier(String value) {
        String parsedValue = value;
        
        int bracketIndex = parsedValue.lastIndexOf("[");
        if (bracketIndex > 0){ //found an instance of [
            //truncate to the underscore index, not inclusive of.   This should remove things like 12345 [3] and make 12345
            //must trim to remove end of string whitespace
            parsedValue = parsedValue.substring(0, bracketIndex).trim();
        }
        return parsedValue;
    }
    
    /**
     * This method returns the cisInfoWidgetName for the user.  If it is NONE, it will use the venderId
     * and proceed as if they actually had the MULTISPEAK value set.
     */
    public String getCisDetailWidget(LiteYukonUser liteYukonUser) {
        boolean cisDetailWidgetEnabled = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.CIS_DETAIL_WIDGET_ENABLED, liteYukonUser);
        if (cisDetailWidgetEnabled) {
            CisDetailRolePropertyEnum cisDetailRoleProperty = 
                rolePropertyDao.getPropertyEnumValue(YukonRoleProperty.CIS_DETAIL_TYPE, CisDetailRolePropertyEnum.class, liteYukonUser);
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
     * Helper method to update responseHeader.objectsRemaining and responseHeader.lastSent
     * @param returnResultsSize
     * @param vendor
     * @return
     */
    public void updateResponseHeader(MspReturnList returnList) throws RemoteException {
        getResponseHeader().setObjectsRemaining(new BigInteger(String.valueOf(returnList.getObjectsRemaining())));
        LogHelper.debug(log, "Updated MspMessageHeader.ObjectsRemaining %s", returnList.getObjectsRemaining());
        
        getResponseHeader().setLastSent(returnList.getLastSent());
        LogHelper.debug(log, "Updated MspMessageHeader.LastSent %s", returnList.getLastSent());
    }
    

    /**
     * Translates the rawState into a loadActionCode based on the type of meter and expected state group for that type.
     * Returns loadActionCode.Unknown when cannot be determined.
     * @param meter
     * @return
     */
    public LoadActionCode getLoadActionCode(YukonDevice yukonDevice, PointValueHolder pointValueHolder) {

        MspLoadActionCode mspLoadActionCode;
        try {

            LogHelper.debug(log, "Returning disconnect status from cache: %s", pointFormattingService.getCachedInstance().getValueString(pointValueHolder, Format.FULL, new SystemUserContext()));

            boolean isRfnDisconnect = paoDefinitionDao.isTagSupported(yukonDevice.getPaoIdentifier().getPaoType(), PaoTag.DISCONNECT_RFN);
            if (isRfnDisconnect) {
                RfnDisconnectStatusState pointState = PointStateHelper.decodeRawState(RfnDisconnectStatusState.class, pointValueHolder.getValue());
                mspLoadActionCode = MspLoadActionCode.getForRfnState(pointState);
            } else {    //assume everything else is PLC
                Disconnect410State pointState = PointStateHelper.decodeRawState(Disconnect410State.class, pointValueHolder.getValue());
                mspLoadActionCode = MspLoadActionCode.getForPlcState(pointState);
            }
        } catch (IllegalArgumentException e) {
            // we were unable to decode the rawState
            log.warn("Unable to decode rawState. value:" + pointValueHolder.getValue());
            return LoadActionCode.Unknown;
        } 
        return mspLoadActionCode.getLoadActionCode();
    }
}