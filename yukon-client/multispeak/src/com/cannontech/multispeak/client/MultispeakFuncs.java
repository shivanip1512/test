/*
 * Created on Aug 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.cannontech.multispeak.client;

import java.rmi.RemoteException;
import java.util.Iterator;

import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.message.PrefixedQName;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeader;
import org.apache.commons.lang.StringUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.deploy.service.Customer;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.ServiceLocation;
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
    public RoleDao roleDao;

    /**
     * @param multispeakDao The multispeakDao to set.
     */
    public void setMultispeakDao(MultispeakDao multispeakDao) {
        this.multispeakDao = multispeakDao;
    }

    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public void logStrings(String intfaceName, String methodName, String[] strings)
	{
		if (strings != null)
		{
			for (int i = 0; i < strings.length; i++)
			{
				CTILogger.info("Return from " + intfaceName + " (" + methodName + "): " + strings[i]);
			}
		}
	}
	
	public void logErrorObjects(String intfaceName, String methodName, ErrorObject[] objects )
	{
		if (objects != null)
		{
			for (int i = 0; i < objects.length; i++)
			{
				CTILogger.info("Error Return from " + intfaceName + "(" + methodName + "): " + (objects[i] == null? "Null" : objects[i].getObjectID() +" - " + objects[i].getErrorString()));
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
			CTILogger.info("MSP MESSAGE RECEIVED: " + MessageContext.getCurrentContext().getCurrentMessage().getSOAPPartAsString().toString());
		} catch (AxisFault e) {
			CTILogger.error(e);
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

    public int getPaoNameAlias() {
        
        String value = roleDao.getGlobalPropertyValue(MultispeakRole.MSP_PAONAME_ALIAS);
        return Integer.valueOf(value).intValue();
    }
    
    /**
     * @return Returns the primaryCIS vendorID.
     */
    public int getPrimaryCIS()
    {
        String value = roleDao.getGlobalPropertyValue(MultispeakRole.MSP_PRIMARY_CB_VENDORID);
        return Integer.valueOf(value).intValue();
    }

}