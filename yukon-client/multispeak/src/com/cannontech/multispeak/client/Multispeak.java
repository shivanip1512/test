 /*
 * Created on Jul 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.client;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.commons.lang.StringUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.impl.DeviceDaoImpl;
import com.cannontech.core.dao.impl.PaoDaoImpl;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.database.db.importer.ImportData;
import com.cannontech.database.db.point.SystemLog;
import com.cannontech.message.dispatch.message.SystemLogHelper;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.data.MeterReadFactory;
import com.cannontech.multispeak.data.ReadableDevice;
import com.cannontech.multispeak.event.CDEvent;
import com.cannontech.multispeak.event.CDStatusEvent;
import com.cannontech.multispeak.event.MeterReadEvent;
import com.cannontech.multispeak.event.MultispeakEvent;
import com.cannontech.multispeak.event.ODEvent;
import com.cannontech.multispeak.service.ArrayOfMeter;
import com.cannontech.multispeak.service.CB_MRSoap_BindingStub;
import com.cannontech.multispeak.service.ConnectDisconnectEvent;
import com.cannontech.multispeak.service.ErrorObject;
import com.cannontech.multispeak.service.ExtensionsItem;
import com.cannontech.multispeak.service.LoadActionCode;
import com.cannontech.multispeak.service.Meter;
import com.cannontech.multispeak.service.MeterRead;
import com.cannontech.multispeak.service.ServiceLocation;
import com.cannontech.multispeak.service.impl.MultispeakPortFactory;
import com.cannontech.yimp.util.DBFuncs;
import com.cannontech.yukon.BasicServerConnection;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Multispeak implements MessageListener {
	
    private MultispeakFuncs multispeakFuncs;
    private BasicServerConnection porterConnection;
    private MultispeakDao multispeakDao;
    private DBPersistentDao dbPersistentDao;
    private PaoDaoImpl paoDao;
    private DeviceDaoImpl deviceDao;
    private SystemLogHelper _systemLogHelper = null;
    
	/** Singleton incrementor for messageIDs to send to porter connection */
	private static long messageID = 1;

	/** A map of Long(userMessageID) to MultispeakEvent values */
	private static Map<Long,MultispeakEvent> eventsMap = new HashMap<Long,MultispeakEvent>();
	 
	public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
        this.multispeakFuncs = multispeakFuncs;
    }

    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }

    public void setMultispeakDao(MultispeakDao multispeakDao) {
        this.multispeakDao = multispeakDao;
    }

    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }

    public void setPaoDao(PaoDaoImpl paoDao) {
        this.paoDao = paoDao;
    }

    public void setDeviceDao(DeviceDaoImpl deviceDao) {
        this.deviceDao = deviceDao;
    }

    /**
     * Get the static instance of Multispeak (this) object.
     * Adds a message listener to the pil connection instance. 
     * @return
     */
    public void initialize() throws Exception {
        CTILogger.info("New MSP instance created");
        porterConnection.addMessageListener(this);
    }
    
    /**
	 * generate a unique mesageid, don't let it be negative
	 * @return
	 */
	private synchronized long generateMessageID() {
		if(++messageID == Long.MAX_VALUE) {
			messageID = 1;
		}
		return messageID;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.message.util.MessageListener#messageReceived(com.cannontech.message.util.MessageEvent)
	 */
	public void messageReceived(MessageEvent e)
	{
		Message in = e.getMessage();		
		if(in instanceof Return)
		{
			Return returnMsg = (Return) in;
			synchronized(this)
			{
                CTILogger.info("Message Received [ID:"+ returnMsg.getUserMessageID() + 
                                " DevID:" + returnMsg.getDeviceID() + 
                                " Command:" + returnMsg.getCommandString() +
                                " Result:" + returnMsg.getResultString() + 
                                " Status:" + returnMsg.getStatus() +
                                " More:" + returnMsg.getExpectMore()+"]");

                if( returnMsg.getExpectMore() == 0) {
                    
					CTILogger.info("Received Message From ID:" + returnMsg.getDeviceID() + " - " + returnMsg.getResultString());

                    MultispeakEvent event = getEventsMap().get(new Long (returnMsg.getUserMessageID()) );

                    boolean doneProcessing = event.messageReceived(returnMsg);
                    if (doneProcessing)
                        getEventsMap().remove(new Long(event.getPilMessageID()));
				}
			}
		}
	}
    
    public LoadActionCode CDMeterState(MultispeakVendor mspVendor, String meterNumber) throws RemoteException
    {
        if ( ! porterConnection.isValid() ) {
            throw new AxisFault("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");
        }

        LiteYukonPAObject lPao = multispeakFuncs.getLiteYukonPaobject(mspVendor.getUniqueKey(), meterNumber);
        long id = generateMessageID();
        CDStatusEvent event = new CDStatusEvent(mspVendor, id);
        
        if (lPao != null)
        {
            event.setMeterNumber(meterNumber);
            getEventsMap().put(new Long(id), event);
            
            Request pilRequest = null;
            CTILogger.info("Received " + meterNumber + " for CDMeterState from " + mspVendor.getCompanyName());

            String commandStr = "getstatus disconnect";
            pilRequest = new Request(lPao.getYukonID(), commandStr, id);
            pilRequest.setPriority(15);
            porterConnection.write(pilRequest);
            logMSPActivity("getCDMeterState",
            				"(ID:" + lPao.getYukonID()+ ") MeterNumber (" + meterNumber + ") - " + commandStr,
            				mspVendor.getCompanyName());    

            synchronized (event)
            {
                long millisTimeOut = 0; //
                while (event.getLoadActionCode() == null && millisTimeOut < 120000)  //quit after 2 minutes
                {
                    try {
                        Thread.sleep(1000);
                        millisTimeOut += 1000;
                    } catch (InterruptedException e) {
                        CTILogger.error(e);
                    }
                }
                if( millisTimeOut >= 120000) {// this broke the loop, more than likely, have to kill it sometime
                    event.setResultMessage("Reading Timed out after 2 minutes.");
                    logMSPActivity("getCDMeterState", "Reading Timed out after 2 minutes.  No reading collected.", mspVendor.getCompanyName());
                    if(event.getLoadActionCode() == null)
                        event.setLoadActionCode(LoadActionCode.Unknown);
                }

            }
      }

        return event.getLoadActionCode();
    }
    
    /**
     * This is a workaround method for SEDC.  This method is used to perform an actual meter interrogation and then return
     * the collected reading if message recieved within 2 minutes.
     * @param mspVendor
     * @param meterNumber
     * @return
     * @throws RemoteException
     */
    public MeterRead getLatestReadingInterrogate(MultispeakVendor mspVendor, String meterNumber)
    {
        LiteYukonPAObject lPao = multispeakFuncs.getLiteYukonPaobject(mspVendor.getUniqueKey(), meterNumber);
    	long id = generateMessageID();      
        MeterReadEvent event = new MeterReadEvent(mspVendor, id);
                
        if (lPao != null)
        {
            ReadableDevice device = MeterReadFactory.createMeterReadObject(lPao.getCategory(), lPao.getType(), meterNumber);
            event.setDevice(device);
            getEventsMap().put(new Long(id), event);
            String commandStr = "getvalue kwh update";
            if( DeviceTypesFuncs.isMCT4XX(lPao.getType()) )
                commandStr = "getvalue peak update";
            
            Request pilRequest = null;
            CTILogger.info("Received " + meterNumber + " for LatestReadingInterrogate from " + mspVendor.getCompanyName());

//          getvalue peak returns the peak kW and the total kWh
            pilRequest = new Request(lPao.getYukonID(), commandStr, id);
            pilRequest.setPriority(15);
            porterConnection.write(pilRequest);
            logMSPActivity("getLatestReadingByMeterNo",
    						"(ID:" + lPao.getYukonID()+ ") MeterNumber (" + meterNumber + ") - " + commandStr,
    						mspVendor.getCompanyName());

            synchronized (event)
            {
                long millisTimeOut = 0; //
                while (!event.getDevice().isPopulated() && millisTimeOut < 120000)  //quit after 2 minutes
                {
                    try {
                        Thread.sleep(1000);
                        millisTimeOut += 1000;
                    } catch (InterruptedException e) {
                        CTILogger.error(e);
                    }
                }
                if( millisTimeOut >= 120000) {// this broke the loop, more than likely, have to kill it sometime
                    logMSPActivity("getLatestReadingByMeterNo", "MeterNumber (" + meterNumber + ") - Reading Timed out after 2 minutes.  No reading collected.", mspVendor.getCompanyName());
                }
            }
      }
        return event.getDevice().getMeterRead();
    }
    
	/**
	 * Send a ping command to pil connection for each meter in meterNumbers.
	 * @param meterNumbers
	 * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
	 */
	public synchronized ErrorObject[] ODEvent(MultispeakVendor vendor, String[] meterNumbers) throws RemoteException
	{
        if( vendor.getUrl() == null || vendor.getUrl().equalsIgnoreCase(CtiUtilities.STRING_NONE))
            throw new RemoteException("OMS vendor unknown.  Please contact Yukon administrator to set the Multispeak Vendor Role Property value in Yukon.");
        
        else if ( ! porterConnection.isValid() )
            throw new RemoteException("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");

        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
		Request pilRequest = null;
		CTILogger.info("Received " + meterNumbers.length + " Meter(s) for Outage Verification Testing from " + vendor.getCompanyName());
		
        for (String meterNumber : meterNumbers) {
			LiteYukonPAObject lPao = multispeakFuncs.getLiteYukonPaobject(vendor.getUniqueKey(), meterNumber);
			if (lPao == null)
			{
                ErrorObject err = multispeakFuncs.getErrorObject(meterNumber, 
                                                                 "MeterNumber: " + meterNumber + " - Was NOT found in Yukon.",
                                                                 "Meter");
				errorObjects.add(err);
			}
			else
			{
				long id = generateMessageID();		
				ODEvent event = new ODEvent(vendor, id);
				getEventsMap().put(new Long(id), event);
			    
				pilRequest = new Request(lPao.getYukonID(), "ping noqueue", id);
				pilRequest.setPriority(13);	//just below Client applications
				porterConnection.write(pilRequest);
			}
			
		}
    		
		if( !errorObjects.isEmpty())
		{
			ErrorObject[] errors = new ErrorObject[errorObjects.size()];
			errorObjects.toArray(errors);
			return errors;
		}
		return new ErrorObject[0];
	}
	
    /**
     * Send meter read commands to pil connection for each meter in meterNumbers.
     * @param meterNumbers
     * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
     */
    public synchronized ErrorObject[] MeterReadEvent(MultispeakVendor vendor, String[] meterNumbers)
    {
        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        
        Request pilRequest = null;
        CTILogger.info("Received " + meterNumbers.length + " Meter(s) for MeterReading from " + vendor.getCompanyName());
        
        for (String meterNumber : meterNumbers) {
            LiteYukonPAObject lPao = multispeakFuncs.getLiteYukonPaobject(vendor.getUniqueKey(), meterNumber);
            if (lPao == null)
            {
                ErrorObject err = multispeakFuncs.getErrorObject(meterNumber, 
                                                                 "MeterNumber: " + meterNumber + " - Was NOT found in Yukon.",
                                                                 "Meter");
                errorObjects.add(err);
            }
            else
            {
                long id = generateMessageID();      
                MeterReadEvent event = new MeterReadEvent(vendor, id);
//                MeterReadEvent event = new MeterReadEvent(vendor, id, 2);
                ReadableDevice device = MeterReadFactory.createMeterReadObject(lPao.getCategory(), lPao.getType(), meterNumber);
                event.setDevice(device);
                getEventsMap().put(new Long(id), event);
                
//                pilRequest = new Request(lPao.getYukonID(), "getvalue kwh update", id);
                String commandStr = "getvalue kwh update";
                if( DeviceTypesFuncs.isMCT4XX(lPao.getType()) )
                    commandStr = "getvalue peak update";
                //getvalue peak returns the peak kW and the total kWh
                pilRequest = new Request(lPao.getYukonID(), commandStr, id);
                pilRequest.setPriority(13); //just below Client applications
                porterConnection.write(pilRequest);
                
//                pilRequest.setCommandString("getvalue demand update");
//                porterConnection.write(pilRequest);
            }
            
        }
        
        if( !errorObjects.isEmpty())
        {
            ErrorObject[] errors = new ErrorObject[errorObjects.size()];
            errorObjects.toArray(errors);
            return errors;
        }
        return new ErrorObject[0];
    }
    
    /**
     * Send a ping command to pil connection for each meter in meterNumbers.
     * @param meterNumbers
     * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
     */
    public synchronized ErrorObject[] CDEvent(MultispeakVendor vendor, ConnectDisconnectEvent [] cdEvents) throws RemoteException
    {
        if ( ! porterConnection.isValid() )
            throw new RemoteException("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");

        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        
        Request pilRequest = null;
        CTILogger.info("Received " + cdEvents.length + " Meter(s) for Connect/Disconnect from " + vendor.getCompanyName());
        
        for (int i = 0; i < cdEvents.length; i++) {
            ConnectDisconnectEvent cdEvent = cdEvents[i];
            String meterNumber = cdEvent.getObjectID();
            LiteYukonPAObject lPao = multispeakFuncs.getLiteYukonPaobject(vendor.getUniqueKey(), meterNumber);
            if (lPao == null) {
                ErrorObject err = multispeakFuncs.getErrorObject(meterNumber,
                												 "MeterNumber (" + meterNumber + ") - Invalid Yukon MeterNumber.",
                                                                 "Meter");
                errorObjects.add(err);
            } else if (multispeakDao.isCDSupportedMeter(meterNumber, vendor.getUniqueKey())){
                long id = generateMessageID();      
                CDEvent event = new CDEvent(vendor, id);
                getEventsMap().put(new Long(id), event);
                
                String commandStr = "control "; //connect|disconnect
                String loadActionCode = cdEvent.getLoadActionCode().getValue();
                if( loadActionCode.equalsIgnoreCase(LoadActionCode._Connect))
                    commandStr += "connect";
                else if( loadActionCode.equalsIgnoreCase(LoadActionCode._Disconnect))
                    commandStr += "disconnect";
                
                pilRequest = new Request(lPao.getYukonID(), commandStr, id);
                pilRequest.setPriority(13); //just below Client applications
                porterConnection.write(pilRequest);
                logMSPActivity("initiateConnectDisconnect",
        						"(ID:" + lPao.getYukonID()+ ") MeterNumber (" + meterNumber + ") - " + commandStr + " sent for ReasonCode: " + cdEvent.getReasonCode().getValue(),
        						vendor.getCompanyName());
            } else {
                ErrorObject err = multispeakFuncs.getErrorObject(meterNumber, 
                												"MeterNumber (" + meterNumber + ") - Invalid Yukon Connect/Disconnect Meter.",
                												"Meter");
                errorObjects.add(err);
            }
        }
            
        if( !errorObjects.isEmpty())
        {
            ErrorObject[] errors = new ErrorObject[errorObjects.size()];
            errorObjects.toArray(errors);
            return errors;
        }
        return new ErrorObject[0];
    }
    
	/**
	 * @return
	 */
	public static Map<Long,MultispeakEvent> getEventsMap() {
		return eventsMap;
	}
    
    public ErrorObject[] initiateStatusChange(MultispeakVendor mspVendor, String[] meterNos, String statusPrefix) {
        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        
        for  (int i = 0; i < meterNos.length; i++){
            String meterNo = meterNos[i];
            LiteYukonPAObject liteYukonPaobject = multispeakFuncs.getLiteYukonPaobject(mspVendor.getUniqueKey(), meterNo);
            if( liteYukonPaobject != null){
                YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(liteYukonPaobject);
                if (yukonPaobject instanceof MCTBase){
                    String origCollGroup = ((MCTBase)yukonPaobject).getDeviceMeterGroup().getCollectionGroup();
                    if( origCollGroup.indexOf(statusPrefix) < 0) {
                    	String newCollGroup = statusPrefix + origCollGroup;
                        ((MCTBase)yukonPaobject).getDeviceMeterGroup().setCollectionGroup(newCollGroup);
                        dbPersistentDao.performDBChange(yukonPaobject, Transaction.UPDATE);
                        logMSPActivity("initiateStatusChange",
                                "MeterNumber(" + meterNo + ") - CollGroup(OLD:" + origCollGroup + " NEW:" + newCollGroup + ");", 
                                mspVendor.getCompanyName());                        
                    } else {
                        ErrorObject err = multispeakFuncs.getErrorObject(meterNo,
                                                                         "MeterNumber: " + meterNo + " - Is already in State (" + origCollGroup +").  Cannot initiate change.",
                                                                         "Meter");
                        errorObjects.add(err);
                    }
                }
            }else {
                ErrorObject err = multispeakFuncs.getErrorObject(meterNo, 
                                                                 "MeterNumber: " + meterNo + " - Was NOT found in Yukon.",
                                                                 "Meter");
                errorObjects.add(err);              
            }
        }
        if( !errorObjects.isEmpty())
        {
            ErrorObject[] errors = new ErrorObject[errorObjects.size()];
            errorObjects.toArray(errors);
            return errors;
        }
        return new ErrorObject[0];
    }

    public ErrorObject[] cancelStatusChange(MultispeakVendor mspVendor, String[] meterNos, String statusPrefix) {
        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        
        for  (int i = 0; i < meterNos.length; i++){
            String meterNo = meterNos[i];
            LiteYukonPAObject liteYukonPaobject = multispeakFuncs.getLiteYukonPaobject(mspVendor.getUniqueKey(), meterNo);
            if( liteYukonPaobject != null) {
                YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(liteYukonPaobject);
                if (yukonPaobject instanceof MCTBase){
                    String origCollGroup = ((MCTBase)yukonPaobject).getDeviceMeterGroup().getCollectionGroup();
                    if( origCollGroup.indexOf(statusPrefix) > -1) {
                    	String newCollGroup = origCollGroup.replaceAll(statusPrefix, "");
                        ((MCTBase)yukonPaobject).getDeviceMeterGroup().setCollectionGroup(newCollGroup);
                        dbPersistentDao.performDBChange(yukonPaobject, Transaction.UPDATE);
                        logMSPActivity("cancelStatusChange",
                                "MeterNumber(" + meterNo + ") - CollGroup(OLD:" + origCollGroup + " NEW:" + newCollGroup + ");", 
                                mspVendor.getCompanyName());                        
                    }
                    else {
                        ErrorObject err = multispeakFuncs.getErrorObject(meterNo, 
                                                                         "MeterNumber: " + meterNo + " - Is not in State (" + statusPrefix+").  Cannot cancel.",
                                                                         "Meter");
                        errorObjects.add(err);
                    }
                }
            } else {
                ErrorObject err = multispeakFuncs.getErrorObject(meterNo, 
                                                                 "MeterNumber: " + meterNo + " - Was NOT found in Yukon.",
                                                                 "Meter");
                errorObjects.add(err);
            }
        }
        if( !errorObjects.isEmpty())
        {
            ErrorObject[] errors = new ErrorObject[errorObjects.size()];
            errorObjects.toArray(errors);
            return errors;
        }
        return new ErrorObject[0];
    }
    
    public ErrorObject[] addMeterObject(MultispeakVendor mspVendor, Meter[] addMeters) {
        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        
        for  (int i = 0; i < addMeters.length; i++){
            Meter mspMeter = addMeters[i];
            String meterNo = mspMeter.getMeterNo().trim();
            String mspAddress = mspMeter.getNameplate().getTransponderID().trim();
            ServiceLocation mspServiceLocation = null;
            
            //Find Meter by MeterNumber in Yukon
            LiteYukonPAObject liteYukonPaobject = multispeakFuncs.getLiteYukonPaobject(mspVendor.getUniqueKey(), meterNo);
            
            if( liteYukonPaobject != null) {    //Meter exists in Yukon
                YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(liteYukonPaobject);
                if (yukonPaobject instanceof MCTBase){
                    DeviceMeterGroup deviceMeterGroup = ((MCTBase)yukonPaobject).getDeviceMeterGroup();
                    DeviceCarrierSettings deviceCarrierSettings = ((MCTBase)yukonPaobject).getDeviceCarrierSettings();
                    
                    boolean disabled = yukonPaobject.isDisabled();
                    String address = deviceCarrierSettings.getAddress().toString();
                    if( address.equalsIgnoreCase(mspMeter.getNameplate().getTransponderID())) { //Same MeterNumber and Address
                        if( disabled ) {        //Disabled Meter found
                        	//Load the CIS serviceLocation.
                        	mspServiceLocation = getServiceLocation(mspVendor, meterNo);
                        	String billingCycle = mspServiceLocation.getBillingCycle();
                            //Enable Meter and update applicable fields.
                            String oldCollGroup = deviceMeterGroup.getCollectionGroup();
                            String oldBillGroup = deviceMeterGroup.getBillingGroup();
                            deviceMeterGroup.setCollectionGroup(oldCollGroup.replaceAll(DeviceMeterGroup.INVENTORY_GROUP_PREFIX, ""));
                            yukonPaobject.setPAOName(yukonPaobject.getPAOName().replaceAll(DeviceMeterGroup.REMOVED_METER_NUMBER_SUFFIX, ""));
                            if (!StringUtils.isBlank(billingCycle))
                            	deviceMeterGroup.setBillingGroup(billingCycle);
                            
                            yukonPaobject.setDisabled(false);
                            String logTemp = "";
                            if( multispeakFuncs.getPaoNameAlias() == MultispeakVendor.ACCOUNT_NUMBER_PAONAME) {
                                if( StringUtils.isBlank(mspMeter.getUtilityInfo().getAccountNumber()))
                                    logTemp = "; PAOName(ActNum) NO CHANGE - MSP ACCOUNT NUMBER EMPTY.";
                                else
                                    logTemp = "; PAOName(ActNum)(OLD:" + yukonPaobject.getPAOName() + " NEW:" + mspMeter.getUtilityInfo().getAccountNumber() + ").";
                                yukonPaobject.setPAOName(mspMeter.getUtilityInfo().getAccountNumber());
                            } else if( multispeakFuncs.getPaoNameAlias() == MultispeakVendor.SERVICE_LOCATION_PAONAME) {
                                if( StringUtils.isBlank(mspMeter.getUtilityInfo().getServLoc()))
                                    logTemp = "; PAOName(ServLoc) NO CHANGE - MSP SERVICE LOCATION OBJECTID EMPTY.";
                                else
                                    logTemp = "; PAOName(ServLoc)(OLD:" + yukonPaobject.getPAOName() + " NEW:" + mspMeter.getUtilityInfo().getServLoc() + ").";
                                yukonPaobject.setPAOName(mspMeter.getUtilityInfo().getServLoc());
                            } else if( multispeakFuncs.getPaoNameAlias() == MultispeakVendor.CUSTOMER_PAONAME) {
                                if( StringUtils.isBlank(mspMeter.getUtilityInfo().getCustID()))
                                    logTemp = "; PAOName(CustID) NO CHANGE - MSP CUSTID EMPTY.";
                                else
                                    logTemp = "; PAOName(CustID)(OLD:" + yukonPaobject.getPAOName() + " NEW:" + mspMeter.getUtilityInfo().getCustID() + ").";
                                yukonPaobject.setPAOName(mspMeter.getUtilityInfo().getCustID());
                            }
                            dbPersistentDao.performDBChange(yukonPaobject, Transaction.UPDATE);
                            logMSPActivity("MeterAddNotification",
                                           "MeterNumber(" + meterNo + ") - CollGroup(OLD:" + oldCollGroup + " NEW:" + deviceMeterGroup.getCollectionGroup() + ");" +
                                           "BillingGroup(OLD:" + oldBillGroup + " NEW:" + billingCycle +").", 
                                           mspVendor.getCompanyName());
                            logMSPActivity("MeterAddNotification",
                                           "MeterNumber(" + meterNo + ") - Meter Enabled" + logTemp, 
                                           mspVendor.getCompanyName());
                            //TODO Read the Meter.
                        }
                        else {  //Meter is currently enabled in Yukon
                            ErrorObject err = multispeakFuncs.getErrorObject(meterNo, 
                                                                             "Warning: MeterNumber(" + meterNo + ") - Meter is already active.  No updates were made.",
                                                                             "Meter");
                            errorObjects.add(err);
                            logMSPActivity("MeterAddNotification",
                                           "MeterNumber(" + meterNo + ") - Meter is already active.  No updates were made.",
                                           mspVendor.getCompanyName());
                        }
                        
                    } else {    //Address is different!
                        if( disabled ) {        //Disabled Meter found

                            //Lookup meter by mspAddress
                            List liteYukonPaoByAddressList = paoDao.getLiteYukonPaobjectsByAddress(new Integer(mspAddress).intValue());
                            
                            if (liteYukonPaoByAddressList.isEmpty()) {  //New Hardware
                                //Need to "remove" the existing (disabled) Meter Number
                                deviceMeterGroup.setMeterNumber(meterNo + DeviceMeterGroup.REMOVED_METER_NUMBER_SUFFIX);
                                yukonPaobject.setPAOName(yukonPaobject.getPAOName() + DeviceMeterGroup.REMOVED_METER_NUMBER_SUFFIX);
                                String oldCollGroup = deviceMeterGroup.getCollectionGroup();
                                if( deviceMeterGroup.getCollectionGroup().indexOf(DeviceMeterGroup.INVENTORY_GROUP_PREFIX ) < 0) {
                                    deviceMeterGroup.setCollectionGroup(DeviceMeterGroup.INVENTORY_GROUP_PREFIX + oldCollGroup);
                                }
                                dbPersistentDao.performDBChange(yukonPaobject, Transaction.UPDATE);
                                logMSPActivity("MeterAddNotification",
                                               "MeterNumber(" + meterNo + ") - _REM disabled MeterNo with conflicting Address; " + 
                                               "CollGroup(OLD:" + oldCollGroup + " NEW:" + deviceMeterGroup.getCollectionGroup() + ").", 
                                               mspVendor.getCompanyName());
                                
                                
//                                  Find a valid Template!
                                String templateName = "*Default Template";
                                if( mspMeter.getExtensionsList() != null) {
	                                ExtensionsItem [] eItems = mspMeter.getExtensionsList().getExtensionsItem();
	                                for (int j = 0; j < eItems.length; j++) {
	                                    ExtensionsItem eItem = eItems[j];
	                                    String extName = eItem.getExtName();
	                                    if ( extName.equalsIgnoreCase("AMRMeterType"))
	                                        templateName = eItem.getExtValue();
	                                }
	/*                                    if (templateName == null) { //Template NOT provided!
	                                        ErrorObject err = multispeakFuncs.getErrorObject(meterNo, "Error: MeterNumber(" + meterNo + ")- AMRMeterType extension not found in Yukon or is not a valid template in yukon. Meter was NOT added.");
	                                        errorObjects.add(err);
	                                        logMSPActivity("MeterAddNotification",
	                                                       "MeterNumber(" + meterNo + ") - AMRMeterType extension not found or is not a valid template in Yukon. Meter was NOT added.", 
	                                                       mspVendor.getCompanyName());
	                                                                        
	                                        //TODO Need to use "default" template if possible
	                                    } 
	*/
                                }
                                //Find template object in Yukon
                                LiteYukonPAObject liteYukonPaobjectTemplate = deviceDao.getLiteYukonPaobjectByDeviceName(templateName);
                                if( liteYukonPaobjectTemplate == null){ 
                                    ErrorObject err = multispeakFuncs.getErrorObject(meterNo, 
                                                                                     "Error: MeterNumber(" + meterNo + ")- AMRMeterType(" + templateName + ") not found in Yukon. Meter was NOT added.",
                                                                                     "Meter");
                                    errorObjects.add(err);
                                    logMSPActivity("MeterAddNotification",
                                                   "MeterNumber(" + meterNo + ") - AMRMeterType(" + templateName + ") not found in Yukon. Meter was NOT added.",
                                                   mspVendor.getCompanyName());
                                }
                                else {    //Valid template found
                                	//Load the CIS serviceLocation.
                                	mspServiceLocation = getServiceLocation(mspVendor, meterNo);
                                    //Find a valid substation
                                    if( mspMeter.getUtilityInfo() == null || mspMeter.getUtilityInfo().getSubstationName()== null){
                                        addImportData(mspMeter, mspServiceLocation, templateName, "");
                                        logMSPActivity("MeterAddNotification",
                                                       "MeterNumber(" + meterNo + ") - Meter inserted into ImportData for processing.", 
                                                       mspVendor.getCompanyName());
                                    } else {
                                        String substationName = mspMeter.getUtilityInfo().getSubstationName();
                                        List routeNames = DBFuncs.getRouteNamesFromSubstationName(substationName);
                                        if( routeNames.isEmpty()){
                                            ErrorObject err = multispeakFuncs.getErrorObject(meterNo, 
                                                                                             "Error: MeterNumber(" + meterNo + ") - SubstationName(" + substationName + ") - no RouteMappings found in Yukon.  Meter was NOT added",
                                                                                             "Meter");
                                            errorObjects.add(err);
                                            logMSPActivity("MeterAddNotification",
                                                           "MeterNumber(" + meterNo + ") - SubstationName(" + substationName + ") not found in Yukon. Meter was NOT added.", 
                                                           mspVendor.getCompanyName());
                                        }
                                        else {
                                            addImportData(mspMeter, mspServiceLocation, templateName, substationName);
                                            logMSPActivity("MeterAddNotification",
                                                           "MeterNumber(" + meterNo + ") - Meter inserted into ImportData for processing.", 
                                                           mspVendor.getCompanyName());
                                        }
                                    }
                                }                                    
                                
                            } else {    //Meter Number and Address both exist...on different objects! 
                                LiteYukonPAObject liteYukonPaoByAddress = (LiteYukonPAObject)liteYukonPaoByAddressList.get(0);
                                YukonPAObject yukonPaobjectByAddress = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(liteYukonPaoByAddress);
                                
                                if (yukonPaobjectByAddress.isDisabled()) {  //Address object is disabled, so we can update and activate the Meter Number object
                                	//Load the CIS serviceLocation.
                                	mspServiceLocation = getServiceLocation(mspVendor, meterNo);
                                	String billingCycle = mspServiceLocation.getBillingCycle();
                                    //TODO deleteRawPointHistory(yukonPaobject);
                                    String oldCollGroup = deviceMeterGroup.getCollectionGroup();
                                    String oldBillGroup = deviceMeterGroup.getBillingGroup();
                                    deviceMeterGroup.setCollectionGroup(oldCollGroup.replaceAll(DeviceMeterGroup.INVENTORY_GROUP_PREFIX, ""));
                                    yukonPaobject.setPAOName(yukonPaobject.getPAOName().replaceAll(DeviceMeterGroup.REMOVED_METER_NUMBER_SUFFIX, ""));
                                    if (!StringUtils.isBlank(billingCycle))
                                    	deviceMeterGroup.setBillingGroup(billingCycle);
                                    
                                    String oldAddress = String.valueOf(deviceCarrierSettings.getAddress());
                                    deviceCarrierSettings.setAddress(Integer.valueOf(mspAddress));
                                    
                                    yukonPaobject.setDisabled(false);
                                    String logTemp = "";
                                    if( multispeakFuncs.getPaoNameAlias() == MultispeakVendor.ACCOUNT_NUMBER_PAONAME) {
                                        if( StringUtils.isBlank(mspMeter.getUtilityInfo().getAccountNumber()))
                                            logTemp = "; PAOName(ActNum) NO CHANGE - MSP ACCOUNT NUMBER EMPTY.";
                                        else
                                            logTemp = "; PAOName(ActNum)(OLD:" + yukonPaobject.getPAOName() + " NEW:" + mspMeter.getUtilityInfo().getAccountNumber() + ").";
                                        yukonPaobject.setPAOName(mspMeter.getUtilityInfo().getAccountNumber());
                                    } else if( multispeakFuncs.getPaoNameAlias() == MultispeakVendor.SERVICE_LOCATION_PAONAME) {
                                        if( StringUtils.isBlank(mspMeter.getUtilityInfo().getServLoc()))
                                            logTemp = "; PAOName(ServLoc) NO CHANGE - MSP SERVICE LOCATION OBJECTID EMPTY.";
                                        else
                                            logTemp = "; PAOName(ServLoc)(OLD:" + yukonPaobject.getPAOName() + " NEW:" + mspMeter.getUtilityInfo().getServLoc() + ").";
                                        yukonPaobject.setPAOName(mspMeter.getUtilityInfo().getServLoc());
                                    } else if( multispeakFuncs.getPaoNameAlias() == MultispeakVendor.CUSTOMER_PAONAME) {
                                        if( StringUtils.isBlank(mspMeter.getUtilityInfo().getCustID()))
                                            logTemp = "; PAOName(CustID) NO CHANGE - MSP CUSTID EMPTY.";
                                        else
                                            logTemp = "; PAOName(CustID)(OLD:" + yukonPaobject.getPAOName() + " NEW:" + mspMeter.getUtilityInfo().getCustID() + ").";
                                        yukonPaobject.setPAOName(mspMeter.getUtilityInfo().getCustID());
                                    }
                                    
                                    dbPersistentDao.performDBChange(yukonPaobject, Transaction.UPDATE);
                                    logMSPActivity("MeterAddNotification",
                                                   "MeterNumber(" + meterNo + ") - CollGroup(OLD:" + oldCollGroup + " NEW:" + deviceMeterGroup.getCollectionGroup() + "); " +
                                                   "BillingGroup(OLD:" + oldBillGroup + " NEW:" + billingCycle +").",
                                                   mspVendor.getCompanyName());
                                    logMSPActivity("MeterAddNotification",
                                                   "MeterNumber(" + meterNo + ") - Address(OLD:" + oldAddress + " NEW:" + deviceCarrierSettings.getAddress().toString() + ").", 
                                                   mspVendor.getCompanyName());
                                    logMSPActivity("MeterAddNotification",
                                                   "MeterNumber(" + meterNo + ") - Meter Enabled." + logTemp, 
                                                   mspVendor.getCompanyName());

                                    //TODO Read the Meter.
                                                                        
                                } else {    //Address is already active
                                    ErrorObject err = multispeakFuncs.getErrorObject(meterNo, 
                                                                                     "Error:  MeterNumber(" + meterNo + ") with Address(" + mspAddress + 
                                                                                     ") is already active on another enabled meter with MeterNumber(" + 
                                                                                     ((MCTBase)yukonPaobjectByAddress).getDeviceMeterGroup().getMeterNumber() + "). No updates were made.",
                                                                                     "Meter");
                                    errorObjects.add(err);
                                    logMSPActivity("MeterAddNotification",
                                                   "Error:  MeterNumber(" + meterNo + ") with Address(" + mspAddress +
                                                   ") is already active on another enabled meter with MeterNumber (" +
                                                   ((MCTBase)yukonPaobjectByAddress).getDeviceMeterGroup().getMeterNumber() + "). No updates were made.",
                                                   mspVendor.getCompanyName());
                                }
                            }
                        }
                        else {  //Meter is currently enabled in Yukon
                            ErrorObject err = multispeakFuncs.getErrorObject(meterNo, 
                                                                             "Error: MeterNumber(" + meterNo + ") - Meter is already active with a different " +
                                                                             "Address(" + deviceCarrierSettings.getAddress().toString() +"). No updates were made.",
                                                                             "Meter");
                            errorObjects.add(err);
                            logMSPActivity("MeterAddNotification",
                                           "Error: MeterNumber(" + meterNo + ") - Meter is already active with a different " +
                                           "Address(" + deviceCarrierSettings.getAddress().toString() +"). No updates were made.", 
                                           mspVendor.getCompanyName());                                
                        }
                    }
                }
            }else { //Meter Number not currently found in Yukon
                //Lookup meter by mspAddress
                List liteYukonPaoByAddressList = paoDao.getLiteYukonPaobjectsByAddress(new Integer(mspAddress).intValue());
                if (liteYukonPaoByAddressList.isEmpty()) {  //New Hardware
                    //Find a valid Template!
                	String templateName = "*Default Template";
                	if( mspMeter.getExtensionsList() != null) {
	                    ExtensionsItem [] eItems = mspMeter.getExtensionsList().getExtensionsItem();
	                    for (int j = 0; j < eItems.length; j++) {
	                        ExtensionsItem eItem = eItems[j];
	                        String extName = eItem.getExtName();
	                        if ( extName.equalsIgnoreCase("AMRMeterType"))
	                            templateName = eItem.getExtValue();
	                    }
	                    /*if (templateName == null) { //Template NOT provided!
	                        ErrorObject err = multispeakFuncs.getErrorObject(meterNo, 
	                                                                         "Error: MeterNumber(" + meterNo + ") - AMRMeterType extension not found in Yukon or is not a valid template in yukon. Meter was NOT added.",
	                                                                         "Meter");
	                        errorObjects.add(err);
	                        logMSPActivity("MeterAddNotification",
	                                       "MeterNumber(" + meterNo + ") - AMRMeterType extension not found or is not a valid template in Yukon. Meter was NOT added.",
	                                       mspVendor.getCompanyName());
	                                                        
	                        //TODO Need to use "default" template if possible
	                    } */
                	}
                    LiteYukonPAObject liteYukonPaobjectTemplate = deviceDao.getLiteYukonPaobjectByDeviceName(templateName);
                    if( liteYukonPaobjectTemplate == null){ 
                        ErrorObject err = multispeakFuncs.getErrorObject(meterNo, 
                                                                         "Error: MeterNumber(" + meterNo + ")- AMRMeterType(" + templateName + ") not found in Yukon. Meter was NOT added.",
                                                                         "Meter");
                        errorObjects.add(err);
                        logMSPActivity("MeterAddNotification",
                                       "MeterNumber(" + meterNo + ") - AMRMeterType(" + templateName + ") not found in Yukon. Meter was NOT added.", 
                                       mspVendor.getCompanyName());
                    }
                    else {    //Valid template found
                    	//Load the CIS serviceLocation.
                    	mspServiceLocation = getServiceLocation(mspVendor, meterNo);
                        //Find a valid substation
                        if( mspMeter.getUtilityInfo() == null || mspMeter.getUtilityInfo().getSubstationName()== null){
                            addImportData(mspMeter, mspServiceLocation, templateName, "");
                            logMSPActivity("MeterAddNotification",
                                           "MeterNumber(" + meterNo + ") - Meter inserted into ImportData for processing.",
                                           mspVendor.getCompanyName());
                        } else {
                            String substationName = mspMeter.getUtilityInfo().getSubstationName();
                            List routeNames = DBFuncs.getRouteNamesFromSubstationName(substationName);
                            if( routeNames.isEmpty()){
                                ErrorObject err = multispeakFuncs.getErrorObject(meterNo, 
                                                                                 "Error: MeterNumber(" + meterNo + ") - SubstationName(" + substationName + ") - no RouteMappings found in Yukon.  Meter was NOT added",
                                                                                 "Meter");
                                errorObjects.add(err);
                                logMSPActivity("MeterAddNotification",
                                               "MeterNumber(" + meterNo + ") - SubstationName(" + substationName + ") not found in Yukon. Meter was NOT added.", 
                                               mspVendor.getCompanyName());
                            }
                            else {
                                addImportData(mspMeter, mspServiceLocation, templateName, substationName);
                                logMSPActivity("MeterAddNotification",
                                               "MeterNumber(" + meterNo + ") - Meter inserted into ImportData for processing.", 
                                               mspVendor.getCompanyName());
                            }
                        }
                    }
                } else { // mspAddress already exists in Yukon 
                    LiteYukonPAObject liteYukonPaoByAddress = (LiteYukonPAObject)liteYukonPaoByAddressList.get(0);
                    YukonPAObject yukonPaobjectByAddress = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(liteYukonPaoByAddress);
                    if (yukonPaobjectByAddress.isDisabled()) {  //Address object is disabled, so we can update and activate the Address object
                        //TODO deleteRawPointHistory(yukonPaobject);
                    	//Load the CIS serviceLocation.
                    	mspServiceLocation = getServiceLocation(mspVendor, meterNo);
                    	String billingCycle = mspServiceLocation.getBillingCycle();
                        yukonPaobjectByAddress.setDisabled(false);
                        String logTemp = "";
                        if( multispeakFuncs.getPaoNameAlias() == MultispeakVendor.ACCOUNT_NUMBER_PAONAME) {
                            if( StringUtils.isBlank(mspMeter.getUtilityInfo().getAccountNumber()))
                                logTemp = "; PAOName(ActNum) NO CHANGE - MSP ACCOUNT NUMBER EMPTY.";
                            else
                                logTemp = "; PAOName(ActNum)(OLD:" + yukonPaobjectByAddress.getPAOName() + " NEW:" + mspMeter.getUtilityInfo().getAccountNumber() + ").";
                            yukonPaobjectByAddress.setPAOName(mspMeter.getUtilityInfo().getAccountNumber());
                        } else if( multispeakFuncs.getPaoNameAlias() == MultispeakVendor.SERVICE_LOCATION_PAONAME) {
                            if( StringUtils.isBlank(mspMeter.getUtilityInfo().getServLoc()))
                                logTemp = "; PAOName(ServLoc) NO CHANGE - MSP SERVICE LOCATION OBJECTID EMPTY.";
                            else
                                logTemp = "; PAOName(ServLoc)(OLD:" + yukonPaobjectByAddress.getPAOName() + " NEW:" + mspMeter.getUtilityInfo().getServLoc() + ").";
                            yukonPaobjectByAddress.setPAOName(mspMeter.getUtilityInfo().getServLoc());
                        } else if( multispeakFuncs.getPaoNameAlias() == MultispeakVendor.CUSTOMER_PAONAME) {
                            if( StringUtils.isBlank(mspMeter.getUtilityInfo().getCustID()))
                                logTemp = "; PAOName(CustID) NO CHANGE - MSP CUSTID EMPTY.";
                            else
                                logTemp = "; PAOName(CustID)(OLD:" + yukonPaobjectByAddress.getPAOName() + " NEW:" + mspMeter.getUtilityInfo().getCustID() + ").";
                            yukonPaobjectByAddress.setPAOName(mspMeter.getUtilityInfo().getCustID());
                        }
                        
                        DeviceMeterGroup deviceMeterGroup = ((MCTBase)yukonPaobjectByAddress).getDeviceMeterGroup();
                        String oldCollGroup = deviceMeterGroup.getCollectionGroup();
                        String oldBillGroup = deviceMeterGroup.getBillingGroup();
                        String oldMeterNo = deviceMeterGroup.getMeterNumber();
                        deviceMeterGroup.setMeterNumber(meterNo);
                        deviceMeterGroup.setCollectionGroup(oldCollGroup.replaceAll(DeviceMeterGroup.INVENTORY_GROUP_PREFIX, ""));
                        yukonPaobjectByAddress.setPAOName(yukonPaobjectByAddress.getPAOName().replaceAll(DeviceMeterGroup.REMOVED_METER_NUMBER_SUFFIX, ""));
                        if (!StringUtils.isBlank(billingCycle))
                        	deviceMeterGroup.setBillingGroup(billingCycle);
                        
                        dbPersistentDao.performDBChange(yukonPaobjectByAddress, Transaction.UPDATE);
                        logMSPActivity("MeterAddNotification",
                                       "MeterNumber(" + meterNo + ") - MeterNumber(OLD:" +oldMeterNo + "); CollGroup(OLD:" + oldCollGroup + " NEW:" + deviceMeterGroup.getCollectionGroup() + ");" +
                                       "BillingGroup(OLD:" + oldBillGroup + " NEW:" + billingCycle +").",
                                       mspVendor.getCompanyName());
                        logMSPActivity("MeterAddNotification",
                                       "MeterNumber(" + meterNo + ") - Meter Enabled" + logTemp,
                                       mspVendor.getCompanyName());
                        //TODO Read the Meter.
                                                            
                    } else {    //Address is already active
                        ErrorObject err = multispeakFuncs.getErrorObject(meterNo, 
                                                                         "Error: Address(" + mspAddress + ") "+ 
                                                                         " - is already active on another enabled meter with MeterNumber(" +
                                                                         ((MCTBase)yukonPaobjectByAddress).getDeviceMeterGroup().getMeterNumber() +"). Meter was NOT added.",
                                                                         "Meter");
                        errorObjects.add(err);
                        
                        logMSPActivity("MeterAddNotification",
                                       "Error:  Address(" + mspAddress + ") - is already active on another enabled meter with MeterNumber(" +
                                       ((MCTBase)yukonPaobjectByAddress).getDeviceMeterGroup().getMeterNumber() + "). Meter was NOT added.",
                                       mspVendor.getCompanyName());                            
                    }
                }
            }
        }//end for

        if( !errorObjects.isEmpty()) {
            ErrorObject[] errors = new ErrorObject[errorObjects.size()];
            errorObjects.toArray(errors);
            return errors;
        }
        return new ErrorObject[0];
    }
    
    /**
     * Removes (disables) a list of meters in Yukon.
     * @param mspVendor
     * @param removeMeters
     * @return
     */
    public ErrorObject[] removeMeterObject(MultispeakVendor mspVendor, Meter[] removeMeters) {
        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        
        for  (int i = 0; i < removeMeters.length; i++){
            Meter mspMeter = removeMeters[i];
            String meterNo = mspMeter.getMeterNo().trim();
            //Lookup meter in Yukon by msp meter number
            LiteYukonPAObject liteYukonPaobject = multispeakFuncs.getLiteYukonPaobject(mspVendor.getUniqueKey(), meterNo);
            if( liteYukonPaobject != null) {    //Meter exists
                YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(liteYukonPaobject);
                if (yukonPaobject instanceof MCTBase){
                    boolean disabled = yukonPaobject.isDisabled();
                    if( !disabled ) {//enabled
                        yukonPaobject.setDisabled(true);
                        DeviceMeterGroup deviceMeterGroup = ((MCTBase)yukonPaobject).getDeviceMeterGroup();
                        String oldCollGroup = deviceMeterGroup.getCollectionGroup();
                        String oldMeterNo = deviceMeterGroup.getMeterNumber();
                        String oldPaoName = yukonPaobject.getPAOName();
                        if( oldMeterNo.indexOf(DeviceMeterGroup.REMOVED_METER_NUMBER_SUFFIX) < 0)
                            deviceMeterGroup.setMeterNumber(oldMeterNo + DeviceMeterGroup.REMOVED_METER_NUMBER_SUFFIX);
                        if( oldCollGroup.indexOf(DeviceMeterGroup.INVENTORY_GROUP_PREFIX ) < 0)
                            ((MCTBase)yukonPaobject).getDeviceMeterGroup().setCollectionGroup(DeviceMeterGroup.INVENTORY_GROUP_PREFIX + oldCollGroup);
                        if( oldPaoName.indexOf(DeviceMeterGroup.REMOVED_METER_NUMBER_SUFFIX) < 0)
                            yukonPaobject.setPAOName(oldPaoName + DeviceMeterGroup.REMOVED_METER_NUMBER_SUFFIX);
                        
                        dbPersistentDao.performDBChange(yukonPaobject, Transaction.UPDATE);
                        logMSPActivity("MeterRemoveNotification",
                                       "MeterNumber(" + oldMeterNo + ") - MeterNumber(NEW:" +deviceMeterGroup.getMeterNumber() +");" + 
                                       "CollGroup(OLD:" + oldCollGroup + " NEW:" + deviceMeterGroup.getCollectionGroup() + ").",
                                       mspVendor.getCompanyName());
                        logMSPActivity("MeterRemoveNotification",
                                       "MeterNumber(" + meterNo + ") - Meter Disabled;" +
                                       "PaoName(OLD:" + oldPaoName + " NEW:" + yukonPaobject.getPAOName() + ").",
                                       mspVendor.getCompanyName());
                    }
                    else {
                        ErrorObject err = multispeakFuncs.getErrorObject(meterNo,
                                                                         "Warning: MeterNumber(" + meterNo + ") - Meter is already disabled. No updates were made.",
                                                                         "Meter");
                        errorObjects.add(err);
                        logMSPActivity("MeterRemoveNotification",
                                       "Warning: MeterNumber(" + meterNo + ") - Meter is already disabled. No updates were made.",
                                       mspVendor.getCompanyName());
                    }
                }
            }else {
                ErrorObject err = multispeakFuncs.getErrorObject(meterNo, 
                                                                 "Error: MeterNumber(" + meterNo + ") - Meter was NOT found in Yukon. No updates were made.",
                                                                 "Meter");
                errorObjects.add(err);
                logMSPActivity("MeterRemoveNotification",
                               "Error: MeterNumber(" + meterNo + ") - Meter was NOT found in Yukon. No updates were made.",
                               mspVendor.getCompanyName());
            }
        }
        if( !errorObjects.isEmpty())
        {
            ErrorObject[] errors = new ErrorObject[errorObjects.size()];
            errorObjects.toArray(errors);
            return errors;
        }
        return new ErrorObject[0];
    }
    
    /**
     * 
     * @param mspVendor
     * @param serviceLocations
     * @return
     */
    public ErrorObject[] updateServiceLocation(MultispeakVendor mspVendor, ServiceLocation[] serviceLocations) {
        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        
        for  (int i = 0; i < serviceLocations.length; i++){
            ServiceLocation mspServiceLocation = serviceLocations[i];
            String serviceLocationStr = mspServiceLocation.getObjectID();
            String paoName = null;
            LiteYukonPAObject liteYukonPaobject = null;
            String logString = "";
            
            //Find a liteYukonPaobject 
            if( multispeakFuncs.getPaoNameAlias() == MultispeakVendor.SERVICE_LOCATION_PAONAME) {
                paoName = mspServiceLocation.getObjectID();
                logString = "ServLoc";
                liteYukonPaobject = deviceDao.getLiteYukonPaobjectByDeviceName(paoName);
            }
            else if( multispeakFuncs.getPaoNameAlias() == MultispeakVendor.ACCOUNT_NUMBER_PAONAME) {
                paoName = mspServiceLocation.getAccountNumber();
                logString = "AcctNo";
                liteYukonPaobject = deviceDao.getLiteYukonPaobjectByDeviceName(paoName);
            }
            else if( multispeakFuncs.getPaoNameAlias() == MultispeakVendor.CUSTOMER_PAONAME) {
                paoName = mspServiceLocation.getCustID();
                logString = "CustNo";
                liteYukonPaobject = deviceDao.getLiteYukonPaobjectByDeviceName(paoName);
            }
            else { // lookup by meter number
                //lookup meter by servicelocation
                String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_MR_STR);
                logString = "MeterNo";

                try {
                	CB_MRSoap_BindingStub port = MultispeakPortFactory.getCB_MRPort(mspVendor);
                	ArrayOfMeter mspMeters = port.getMeterByServLoc(serviceLocationStr);
                    if( mspMeters != null && mspMeters.getMeter() != null) {
                        for ( int j = 0; j < mspMeters.getMeter().length; j++){
                        	paoName = mspMeters.getMeter(j).getMeterNo();
                            LiteYukonPAObject tempPao = deviceDao.getLiteYukonPaobjectByMeterNumber(paoName);
                            if( tempPao != null) {
                                liteYukonPaobject = tempPao;
                                break;
                            }
                        }
                    }
                } catch (ServiceException e) {
                	CTILogger.error("CB_MR service is not defined for company(" + mspVendor.getCompanyName()+ ") - getMeterByServLoc failed.");
        			CTILogger.error("ServiceExceptionDetail: " + e.getMessage());
                } catch (RemoteException e) {
                	CTILogger.error("TargetService: " + endpointURL + " - updateServiceLocation (" + mspVendor.getCompanyName() + ")");
        			CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
                } 
            }
            String logTemp = "";
            if( liteYukonPaobject != null) {
                YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(liteYukonPaobject);
                if (yukonPaobject instanceof MCTBase) {
                    DeviceMeterGroup deviceMeterGroup = ((MCTBase)yukonPaobject).getDeviceMeterGroup();
                    boolean update = false;
                    if( multispeakFuncs.getPaoNameAlias() == MultispeakVendor.ACCOUNT_NUMBER_PAONAME) {
                        if( !yukonPaobject.getPAOName().equals(mspServiceLocation.getAccountNumber())) {  //update accountnumber
                            if( mspServiceLocation.getAccountNumber() == null || mspServiceLocation.getAccountNumber().length() < 1)
                                logTemp = "; PAOName(ActNum) NO CHANGE - MSP ACCOUNT NUMBER EMPTY.";
                            else
                                logTemp = "; PAOName(ActNum)(OLD:" + yukonPaobject.getPAOName() + " NEW:" + mspServiceLocation.getAccountNumber() + ").";
                            yukonPaobject.setPAOName(mspServiceLocation.getAccountNumber());
                            update = true;
                        }
                    }
                    else if( multispeakFuncs.getPaoNameAlias() == MultispeakVendor.SERVICE_LOCATION_PAONAME) {
                        if( !yukonPaobject.getPAOName().equals(mspServiceLocation.getObjectID())) { //update serviceLocation
                            if( mspServiceLocation.getObjectID() == null || mspServiceLocation.getObjectID().length() < 1)
                                logTemp = "; PAOName(ServLoc) NO CHANGE - MSP SERVICE LOCATION OBJECTID EMPTY.";
                            else
                                logTemp = "; PAOName(ServLoc)(OLD:" + yukonPaobject.getPAOName() + " NEW:" + mspServiceLocation.getObjectID() + ").";
                            yukonPaobject.setPAOName(mspServiceLocation.getCustID());
                            update = true;
                        }
                    }
                    else if( multispeakFuncs.getPaoNameAlias() == MultispeakVendor.CUSTOMER_PAONAME) {
                        if( !yukonPaobject.getPAOName().equals(mspServiceLocation.getCustID())) { //update customer
                            if( mspServiceLocation.getCustID() == null || mspServiceLocation.getCustID().length() < 1)
                                logTemp = "; PAOName(CustID) NO CHANGE - MSP CUSTID EMPTY.";
                            else
                                logTemp = "; PAOName(CustID)(OLD:" + yukonPaobject.getPAOName() + " NEW:" + mspServiceLocation.getCustID() + ").";
                            yukonPaobject.setPAOName(mspServiceLocation.getCustID());
                            update = true;
                        }
                    }
                    if( !deviceMeterGroup.getBillingGroup().equals(mspServiceLocation.getBillingCycle())) {
                        String oldBillGroup = deviceMeterGroup.getBillingGroup();
                        deviceMeterGroup.setBillingGroup(mspServiceLocation.getBillingCycle());
                        logMSPActivity("ServiceLocationChangedNotification",
                                       "MeterNumber(" + deviceMeterGroup.getMeterNumber()+ ") - BillingGroup(OLD:" + oldBillGroup + " NEW:" +mspServiceLocation.getBillingCycle() +").",
                                       mspVendor.getCompanyName());
                        update = true;
                    }
                    
                    if (update) {
                        dbPersistentDao.performDBChange(yukonPaobject, Transaction.UPDATE);
                        logMSPActivity("ServiceLocationChangedNotification",
                                       "MeterNumber(" + deviceMeterGroup.getMeterNumber()+ ") - " + logTemp,
                                       mspVendor.getCompanyName());
                    }


                }
            } else {
                ErrorObject err = multispeakFuncs.getErrorObject(serviceLocationStr, 
                                                                 "ServiceLocation(" + serviceLocationStr + ") - ServiceLocation was NOT found in Yukon.", 
                                                                 "ServiceLocation");
                errorObjects.add(err);
                logMSPActivity("ServiceLocationChangedNotification",
                               logString + "(" + paoName + ") - was NOT found in Yukon",
                               mspVendor.getCompanyName());
            }
        }
        if( !errorObjects.isEmpty())
        {
            ErrorObject[] errors = new ErrorObject[errorObjects.size()];
            errorObjects.toArray(errors);
            return errors;
        }
        return new ErrorObject[0];
    }

    public SystemLogHelper getSystemLogHelper() {
        if (_systemLogHelper == null)
            _systemLogHelper = new SystemLogHelper(PointTypes.SYS_PID_MULTISPEAK);
        return _systemLogHelper;
    }
    
    private void addImportData(Meter mspMeter, ServiceLocation mspServiceLocation, String templateName, String substationName){
        String address = mspMeter.getNameplate().getTransponderID();
        String meterNumber = mspMeter.getMeterNo();
        String paoName = meterNumber;
        String billingGroup = StringUtils.isBlank(mspServiceLocation.getBillingCycle())? "":mspServiceLocation.getBillingCycle();
        
        if( multispeakFuncs.getPaoNameAlias() == MultispeakVendor.ACCOUNT_NUMBER_PAONAME) {
            if( !StringUtils.isBlank(mspMeter.getUtilityInfo().getAccountNumber()))
                paoName = mspMeter.getUtilityInfo().getAccountNumber();
        } else if( multispeakFuncs.getPaoNameAlias() == MultispeakVendor.SERVICE_LOCATION_PAONAME) {
            if( !StringUtils.isBlank(mspMeter.getUtilityInfo().getServLoc()))
                paoName = mspMeter.getUtilityInfo().getServLoc();
        } else if( multispeakFuncs.getPaoNameAlias() == MultispeakVendor.CUSTOMER_PAONAME) {
            if( !StringUtils.isBlank(mspMeter.getUtilityInfo().getCustID()))
                paoName = mspMeter.getUtilityInfo().getCustID();
        }

        ImportData importData = new ImportData(address, paoName, "", meterNumber, 
                                               "Default", "Default", templateName, billingGroup, substationName);
        try {
            Transaction.createTransaction(Transaction.INSERT, importData).execute();
        } catch (TransactionException e) {
            CTILogger.error(e);
        }
    }
    
    private void logMSPActivity(String method, String description, String userName) {
        getSystemLogHelper().log(PointTypes.SYS_PID_MULTISPEAK, method, description, userName, SystemLog.TYPE_MULTISPEAK); 
    }
    
    /**
    * Returns a serviceLocation for the meterNo.
    * If the interface/method is not supported by mspVendor, or if no object is found, an empty ServiceLocation object is returned.
    * @param mspVendor
    * @param meterNo
    * @return
    */
   private ServiceLocation getServiceLocation(MultispeakVendor mspVendor, String meterNo) {
	   // Load the CIS serviceLocation.
   		ServiceLocation mspServiceLocation = new ServiceLocation();
   		String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_MR_STR);
    	try {
    		CB_MRSoap_BindingStub port = MultispeakPortFactory.getCB_MRPort(mspVendor);
   			mspServiceLocation =  port.getServiceLocationByMeterNo(meterNo);
    	} catch (ServiceException e) {
    		CTILogger.error("CB_MR service is not defined for company(" + mspVendor.getCompanyName()+ ") - getServiceLocationByMeterNo failed.");
			CTILogger.error("ServiceExceptionDetail: " + e.getMessage());
    	} catch (RemoteException e) {
    		CTILogger.error("TargetService: " + endpointURL + " - getServiceLocationByMeterNo (" + mspVendor.getCompanyName() + ")");
			CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
			CTILogger.info("A default(empty) is being used for ServiceLocation");
       }
       return mspServiceLocation;
   }
}