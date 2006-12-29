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

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.PrefixedQName;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeader;
import org.apache.axis.message.SOAPHeaderElement;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.dao.RawPointHistoryDao;
import com.cannontech.multispeak.service.ArrayOfErrorObject;
import com.cannontech.multispeak.service.ArrayOfString;
import com.cannontech.multispeak.service.Customer;
import com.cannontech.multispeak.service.ErrorObject;
import com.cannontech.multispeak.service.Extensions;
import com.cannontech.multispeak.service.Meter;
import com.cannontech.multispeak.service.Nameplate;
import com.cannontech.multispeak.service.UtilityInfo;
import com.cannontech.roles.yukon.MultispeakRole;
import com.cannontech.spring.YukonSpringHook;

/**
 * @author stacey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MultispeakFuncs
{
    private MultispeakDao multispeakDao;
    private RawPointHistoryDao mspRawPointHistoryDao;
    private DBPersistentDao dbPersistentDao;

    public static MultispeakDao getMultispeakDao() {
        return (MultispeakDao) YukonSpringHook.getBean("multispeakDao");
//        return multispeakDao;
    }
    
    /**
     * @param multispeakDao The multispeakDao to set.
     */
    public void setMultispeakDao(MultispeakDao multispeakDao) {
        this.multispeakDao = multispeakDao;
    }

    /**
     * @param mspRawPointHistoryDao The mspRawPointHistoryDao to set.
     */
    public void setMspRawPointHistoryDao(RawPointHistoryDao mspRawPointHistoryDao) {
        this.mspRawPointHistoryDao = mspRawPointHistoryDao;
    }

    public static RawPointHistoryDao getMspRawPointHistoryDao() {
        return (RawPointHistoryDao) YukonSpringHook.getBean("mspRawPointHistoryDao");
//        return mspRawPointHistoryDao;
    }
     
    /**
     * @return Returns the dbPersistentDao.
     */
    public static DBPersistentDao getDbPersistentDao(){
        return (DBPersistentDao) YukonSpringHook.getBean("dbPersistentDao");
//        return dbPersistentDao;
    }

    /**
     * @param dbPersistentDao The dbPersistentDao to set.
     */
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao){
        this.dbPersistentDao = dbPersistentDao;
    }
    
	public static void logArrayOfString(String intfaceName, String methodName, String[] strings)
	{
		if (strings != null)
		{
			for (int i = 0; i < strings.length; i++)
			{
				CTILogger.info("Return from " + intfaceName + " (" + methodName + "): " + strings[i]);
			}
		}
	}
	
	public static void logArrayOfErrorObjects(String intfaceName, String methodName, ErrorObject[] objects )
	{
		if (objects != null)
		{
			for (int i = 0; i < objects.length; i++)
			{
				CTILogger.info("Error Return from " + intfaceName + "(" + methodName + "): " + (objects[i] == null? "Null" : objects[i].getObjectID() +" - " + objects[i].getErrorString()));
			}
		}
	}
    
    public static SOAPHeaderElement getHeader(MultispeakVendor mspVendor) {
        return new SOAPHeaderElement("http://www.multispeak.org", "MultiSpeakMsgHeader", new YukonMultispeakMsgHeader(mspVendor.getOutUserName(), mspVendor.getOutPassword()));
    }
    
	public static void loadResponseHeader() 
	{
		try {
			//Get current message context
			MessageContext ctx = MessageContext.getCurrentContext();

			// Get SOAP envelope of response
			SOAPEnvelope env = ctx.getResponseMessage().getSOAPEnvelope();
			
			//Create SOAP header object } } } 
			SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org", "MultiSpeakMsgHeader", new YukonMultispeakMsgHeader());

			// Set Header
			env.addHeader(header);
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			CTILogger.error(e);
		}
	}

    public static YukonMultispeakMsgHeader getResponseHeader() throws AxisFault {
        // Get current message context
        MessageContext ctx = MessageContext.getCurrentContext();

        // Get SOAP envelope of response
        SOAPEnvelope env = ctx.getResponseMessage().getSOAPEnvelope();
        
        return (YukonMultispeakMsgHeader)env.getHeaderByName("http://www.multispeak.org", "MultiSpeakMsgHeader").getObjectValue();
    }
    
    public static String getObjectID(String key, int deviceID)
    {
        if( key.toLowerCase().startsWith("device") || key.toLowerCase().startsWith("pao"))
        {
            LiteYukonPAObject lPao = DaoFactory.getPaoDao().getLiteYukonPAO(deviceID);
            return (lPao == null ? null : lPao.getPaoName());
        }
        else //if(key.toLowerCase().startsWith("meternum")) // default value
        {
            LiteDeviceMeterNumber ldmn = DaoFactory.getDeviceDao().getLiteDeviceMeterNumber(deviceID);
            return (ldmn == null ? null : ldmn.getMeterNumber());
        }
    }

    public static LiteYukonPAObject getLiteYukonPaobject(String key, String objectID)
    {
        if( key.toLowerCase().startsWith("device") || key.toLowerCase().startsWith("pao"))
            return DaoFactory.getDeviceDao().getLiteYukonPaobjectByDeviceName(objectID);
        else //if(key.toLowerCase().startsWith("meternum")) // default value
            return DaoFactory.getDeviceDao().getLiteYukonPaobjectByMeterNumber(objectID);
    }
    
	/**
	 * This method should be called by every multispeak function!!!
	 *
	 */
	public static void init()
	{
		try
		{
			CTILogger.info("MSP MESSAGE RECEIVED: " + MessageContext.getCurrentContext().getCurrentMessage().getSOAPPartAsString().toString());
		} catch (AxisFault e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MultispeakFuncs.loadResponseHeader();
	}
	/**
	 * A common declaration of the pingURL method for all services to use.
	 * @param interfaceName
	 * @return
	 */
	public static ArrayOfErrorObject pingURL(String interfaceName)
	{
		if (Multispeak.getInstance() != null)
			return new ArrayOfErrorObject(new ErrorObject[0]);
		ErrorObject err = new ErrorObject();
		err.setErrorString("Yukon Multispeak WebService '" + interfaceName + "' is not running.");
		err.setEventTime(new GregorianCalendar());
		ErrorObject[] errorObject = new ErrorObject[]{err};
		MultispeakFuncs.logArrayOfErrorObjects(interfaceName, "pingURL", errorObject);
		return new ArrayOfErrorObject(errorObject);
	}
	
	/**
	 * A common declaration of the getMethods method for all services to use.
	 * @param interfaceName
	 * @param methods
	 * @return
	 * @throws java.rmi.RemoteException
	 */
	public static ArrayOfString getMethods(String interfaceName, String[] methods) throws java.rmi.RemoteException {
		MultispeakFuncs.logArrayOfString(interfaceName, "getMethods", methods);
		return new ArrayOfString(methods);
	}
	
	public static String getCompanyNameFromSOAPHeader() throws java.rmi.RemoteException
	{
		String companyName = null;
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
                    if( pQName.getQualifiedName().equalsIgnoreCase("company")){
                        companyName = ele.getAttribute(pQName.getQualifiedName());
                        break;
                    }
                }
			}
		}
		catch (SOAPException e)
		{
			e.printStackTrace();
		}
		return companyName;
	}
    
    public static String getAppNameFromSOAPHeader() throws java.rmi.RemoteException
    {
        String appName = null;
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
                    if( pQName.getQualifiedName().equalsIgnoreCase("appname")){
                        appName = ele.getAttribute(pQName.getQualifiedName());
                        break;
                    }
                }
            }
        }
        catch (SOAPException e) {
            e.printStackTrace();
        }
        return appName;
    }    
    
    public static MultispeakVendor getMultispeakVendorFromHeader() throws RemoteException {
        String companyName = MultispeakFuncs.getCompanyNameFromSOAPHeader();
        String appName = MultispeakFuncs.getAppNameFromSOAPHeader();
        return MultispeakFuncs.getMultispeakVendor(companyName, appName);
    }

    /**
     * Returns the MultispeakVendor for the companyName (uses toLower() for the company name so we can ignore the case)
     * @param companyName
     * @return
     */
    public static MultispeakVendor getMultispeakVendor(String companyName, String appName) throws RemoteException
    {
        try{
            return getMultispeakDao().getMultispeakVendor(companyName, appName);
        }
        catch (NotFoundException nfe)
        {
            throw new AxisFault("Company '" +companyName + "' does not have a defined interface.");
        }
    }

    /**
     * Creates a new (MSP) Meter object.
     * @param objectID The Multispeak objectID.
     * @param address The meter's transponderID (Physical Address)
     * @return
     */
    public static Meter createMeter(String objectID, String paoType, String collectionGroup, String billingGroup, String address)
    {
        Meter meter = new Meter();
        meter.setObjectID(objectID);
//      MessageElement element = new MessageElement(QName.valueOf("AMRMeterType"), paoType);
        MessageElement element2 = new MessageElement(QName.valueOf("AMRRdgGrp"), collectionGroup);
        Extensions ext = new Extensions();
        ext.set_any(new MessageElement[]{element2});
        meter.setExtensions(ext);
        meter.setMeterNo(objectID);
//        meter.setSerialNumber( );    //Meter serial number. This is the original number assigned to the meter by the manufacturer.
        meter.setMeterType(paoType);       //Meter type/model.
//        meter.setManufacturer();    //Meter manufacturer.
        meter.setAMRType(MultispeakDefines.AMR_TYPE);         //Type of AMR used on this meter, if any. This is a utility defined field.  A string containing the vendor name and type of AMR used, or "none"
        meter.setNameplate(getNameplate(objectID, address));
//        meter.setSealNumberList(null);  //List of seals applied to this meter.
//        meter.setUtilityInfo(null);     //This information relates the meter to the account with which it is associated
        
        //MSPDevice
//        meter.setDeviceClass(null); //A high-level description of this type of object (e.g., "kWh meter", "demand meter", etc.).
//        meter.setFacilityID(null);  //A utility-defined string designation for this device.
//        meter.setInServiceDate(null);   //The date and time that a device was placed into active service.
//        meter.setOutServiceDate(null);  //The date and time that a device was removed from active service.
        
        //MSPObject
//        meter.setUtility(null);
//        meter.setComments(null);
//        meter.setErrorString(null);
//        meter.setReplaceID(null);
        
        return meter;
    }

    /**
     * Creates a new (MSP) Nameplate object
     * @param objectID The multispeak objectID
     * @param address The meter's transponderID (Physical Address)
     * @return
     */
    public static Nameplate getNameplate(String objectID, String address)
    {
        Nameplate nameplate = new Nameplate();
        /*nameplate.setKh();
        nameplate.setKr();
        nameplate.setFrequency();
        nameplate.setNumberOfElements();
        nameplate.setBaseType();
        nameplate.setAccuracyClass();
        nameplate.setElementsVoltage();
        nameplate.setSupplyVoltage();
        nameplate.setMaxAmperage();
        nameplate.setTestAmperage();
        nameplate.setRegRatio();
        nameplate.setPhases();
        nameplate.setWires();
        nameplate.setDials();
        nameplate.setForm();
        nameplate.setMultiplier();
        nameplate.setDemandMult();
        nameplate.setTransformerRatio();*/
        nameplate.setTransponderID(address);
        return nameplate;
    }
    
    /**
     * Creates a new (MSP) UtilityInfo object
     * @param objectID The Multispeak objectID
     * @return
     */
    public static UtilityInfo getUtilityInfo(String objectID)
    {
        UtilityInfo utilityInfo = new UtilityInfo();
        /*utilityInfo.setAccountNumber();
        utilityInfo.setBus();
        utilityInfo.setCustID();
        utilityInfo.setDistrict();
        utilityInfo.setEaLoc();
        utilityInfo.setFeeder();
        utilityInfo.setOwner();
        utilityInfo.setPhaseCd();
        utilityInfo.setServLoc();
        utilityInfo.setSubstationCode();
        utilityInfo.setSubstationName();
        utilityInfo.setTransformerBankID();*/
        return utilityInfo;
    }
 
    /**
     * Creates a new (MSP) ErrorObject 
     * @param objectID The Multispeak objectID
     * @param errorMessage The error message.
     * @return
     */
    public static ErrorObject getErrorObject(String objectID, String errorMessage, String nounType){
        ErrorObject err = new ErrorObject();
        err.setEventTime(new GregorianCalendar());
        err.setObjectID(objectID);
        err.setErrorString(errorMessage);
        err.setNounType(nounType);
        return err;
    }
    
    public static String customerToString(Customer customer) {
        String returnStr = "";
        
        returnStr += (customer.getObjectID() != null ? "Customer: " + customer.getObjectID() + "/r/n" : "");
        
        String tempString = (customer.getLastName() != null ? customer.getLastName() + ", " : "") +
                     (customer.getFirstName() != null ? customer.getFirstName() + " " : "" ) + 
                     (customer.getMName() != null ? customer.getMName() : "" );
        if( tempString.length() > 0)
            returnStr += "Name: " + tempString  + "/r/n";
        
        returnStr += (customer.getDBAName() != null ? "DBA Name: " +customer.getDBAName() + "/r/n": "");
        
        tempString = (customer.getHomeAc() != null ? "(" + customer.getHomeAc() +") " : "");
        tempString += (customer.getHomePhone() != null ? customer.getHomePhone(): "");
        if( tempString.length() > 0)
            returnStr += "Home Phone: " + tempString + "/r/n";
        
        tempString = (customer.getDayAc() != null ? "(" + customer.getDayAc() +") " : "");
        tempString += (customer.getDayPhone() != null ? customer.getDayPhone(): "");
        if( tempString.length() > 0)
            returnStr += "Home Phone: " + tempString + "/r/n";
        
        returnStr += (customer.getBillAddr1() != null ? "Address 1: " + customer.getBillAddr1() + "/r/n": "");
        returnStr += (customer.getBillAddr2() != null ? "Address 2: " + customer.getBillAddr2() + "/r/n": "");
        
        tempString = (customer.getBillCity() != null ? customer.getBillCity() + ", ": "");
        tempString += (customer.getBillState() != null ? customer.getBillState() + " " : "");
        tempString += (customer.getBillZip() != null ? customer.getBillZip() : "");
        if( tempString.length() > 0)
            returnStr += "City/State/Zip: " + tempString + "/r/n";

        return returnStr;
        
    }
    
    public static int getPaoNameAlias() {
        
        String value = DaoFactory.getRoleDao().getGlobalPropertyValue(MultispeakRole.MSP_PAONAME_ALIAS);
        return Integer.valueOf(value).intValue();
    }
}