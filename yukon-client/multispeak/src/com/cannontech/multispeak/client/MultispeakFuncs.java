/*
 * Created on Aug 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.cannontech.multispeak.client;

import java.rmi.RemoteException;
import java.util.GregorianCalendar;
import java.util.Iterator;

import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.message.PrefixedQName;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeader;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.service.ArrayOfString;
import com.cannontech.multispeak.service.Customer;
import com.cannontech.multispeak.service.ErrorObject;
import com.cannontech.multispeak.service.ServiceLocation;
import com.cannontech.roles.yukon.MultispeakRole;

/**
 * @author stacey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MultispeakFuncs
{
    public MultispeakDao multispeakDao;
    public PaoDao paoDao;
    public DeviceDao deviceDao;
    public RoleDao roleDao;

    /**
     * @param multispeakDao The multispeakDao to set.
     */
    public void setMultispeakDao(MultispeakDao multispeakDao) {
        this.multispeakDao = multispeakDao;
    }

	public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public void logArrayOfString(String intfaceName, String methodName, String[] strings)
	{
		if (strings != null)
		{
			for (int i = 0; i < strings.length; i++)
			{
				CTILogger.info("Return from " + intfaceName + " (" + methodName + "): " + strings[i]);
			}
		}
	}
	
	public void logArrayOfErrorObjects(String intfaceName, String methodName, ErrorObject[] objects )
	{
		if (objects != null)
		{
			for (int i = 0; i < objects.length; i++)
			{
				CTILogger.info("Error Return from " + intfaceName + "(" + methodName + "): " + (objects[i] == null? "Null" : objects[i].getObjectID() +" - " + objects[i].getErrorString()));
			}
		}
	}
    
	public void loadResponseHeader() 
	{
		try {
			SOAPEnvelope env = getResponseMessageSOAPEnvelope();
			MultispeakVendor mspVendor = getMultispeakVendor(MultispeakVendor.CANNON_MSP_COMPANYNAME, "");

			// Set Header
			env.addHeader(mspVendor.getHeader());
		} catch (RemoteException e) {
			CTILogger.error(e);
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
    
    public String getObjectID(String key, int deviceID)
    {
        if( key.toLowerCase().startsWith("device") || key.toLowerCase().startsWith("pao"))
        {
            LiteYukonPAObject lPao = paoDao.getLiteYukonPAO(deviceID);
            return (lPao == null ? null : lPao.getPaoName());
        }
        else //if(key.toLowerCase().startsWith("meternum")) // default value
        {
            LiteDeviceMeterNumber ldmn = deviceDao.getLiteDeviceMeterNumber(deviceID);
            return (ldmn == null ? null : ldmn.getMeterNumber());
        }
    }

    public LiteYukonPAObject getLiteYukonPaobject(String key, String objectID)
    {
        if( key.toLowerCase().startsWith("device") || key.toLowerCase().startsWith("pao"))
            return deviceDao.getLiteYukonPaobjectByDeviceName(objectID);
        else //if(key.toLowerCase().startsWith("meternum")) // default value
            return deviceDao.getLiteYukonPaobjectByMeterNumber(objectID);
    }
    
	/**
	 * This method should be called by every multispeak function!!!
	 *
	 */
	public void init()
	{
		try {
			CTILogger.info("MSP MESSAGE RECEIVED: " + MessageContext.getCurrentContext().getCurrentMessage().getSOAPPartAsString().toString());
		} catch (AxisFault e) {
			CTILogger.error(e);
		}
		loadResponseHeader();
	}
//	/**
//	 * A common declaration of the pingURL method for all services to use.
//	 * @param interfaceName
//	 * @return
//	 */
//	public static ArrayOfErrorObject pingURL(String interfaceName)
//	{
//		if (Multispeak.getInstance() != null)
//			return new ArrayOfErrorObject(new ErrorObject[0]);
//		ErrorObject err = new ErrorObject();
//		err.setErrorString("Yukon Multispeak WebService '" + interfaceName + "' is not running.");
//		err.setEventTime(new GregorianCalendar());
//		ErrorObject[] errorObject = new ErrorObject[]{err};
//		MultispeakFuncs.logArrayOfErrorObjects(interfaceName, "pingURL", errorObject);
//		return new ArrayOfErrorObject(errorObject);
//	}
	
	/**
	 * A common declaration of the getMethods method for all services to use.
	 * @param interfaceName
	 * @param methods
	 * @return
	 * @throws java.rmi.RemoteException
	 */
	public ArrayOfString getMethods(String interfaceName, String[] methods){
		logArrayOfString(interfaceName, "getMethods", methods);
		return new ArrayOfString(methods);
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
			CTILogger.error(e);
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
    
    public MultispeakVendor getMultispeakVendorFromHeader() throws RemoteException {
        String companyName = getCompanyNameFromSOAPHeader();
        String appName = getAppNameFromSOAPHeader();
        return getMultispeakVendor(companyName, appName);
    }

    /**
     * Returns the MultispeakVendor for the companyName (uses toLower() for the company name so we can ignore the case)
     * @param companyName
     * @return
     */
    public MultispeakVendor getMultispeakVendor(String companyName, String appName) throws RemoteException
    {
        try{
            return multispeakDao.getMultispeakVendor(companyName, appName);
        }
        catch (IncorrectResultSizeDataAccessException e) {
            throw new AxisFault("Company '" +companyName + "' does not have a defined interface.");
        }
        catch (NotFoundException nfe) {
            throw new AxisFault("Company '" +companyName + "' does not have a defined interface.");
        }
    }
 
    /**
     * Creates a new (MSP) ErrorObject 
     * @param objectID The Multispeak objectID
     * @param errorMessage The error message.
     * @return
     */
    public ErrorObject getErrorObject(String objectID, String errorMessage, String nounType){
        ErrorObject err = new ErrorObject();
        err.setEventTime(new GregorianCalendar());
        err.setObjectID(objectID);
        err.setErrorString(errorMessage);
        err.setNounType(nounType);
        return err;
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
        private com.cannontech.multispeak.service.Network network;
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

    public int getPaoNameAlias() {
        
        String value = roleDao.getGlobalPropertyValue(MultispeakRole.MSP_PAONAME_ALIAS);
        return Integer.valueOf(value).intValue();
    }
}