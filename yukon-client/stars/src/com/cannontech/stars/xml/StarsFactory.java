package com.cannontech.stars.xml;

import java.util.Vector;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.data.customer.Contact;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.contact.ContactNotification;
import com.cannontech.database.db.customer.Address;
import com.cannontech.database.db.stars.customer.AccountSite;
import com.cannontech.database.db.stars.customer.CustomerAccount;
import com.cannontech.database.db.stars.customer.SiteInformation;
import com.cannontech.database.db.stars.report.CallReportBase;
import com.cannontech.database.db.stars.report.WorkOrderBase;
import com.cannontech.database.db.web.YukonWebConfiguration;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.xml.serialize.BillingAddress;
import com.cannontech.stars.xml.serialize.CallType;
import com.cannontech.stars.xml.serialize.DeviceStatus;
import com.cannontech.stars.xml.serialize.Email;
import com.cannontech.stars.xml.serialize.InstallationCompany;
import com.cannontech.stars.xml.serialize.LMDeviceType;
import com.cannontech.stars.xml.serialize.PrimaryContact;
import com.cannontech.stars.xml.serialize.StarsApp;
import com.cannontech.stars.xml.serialize.StarsCallReport;
import com.cannontech.stars.xml.serialize.StarsCallRprt;
import com.cannontech.stars.xml.serialize.StarsContactNotification;
import com.cannontech.stars.xml.serialize.StarsCustAccount;
import com.cannontech.stars.xml.serialize.StarsCustListEntry;
import com.cannontech.stars.xml.serialize.StarsCustResidence;
import com.cannontech.stars.xml.serialize.StarsCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsCustomerAddress;
import com.cannontech.stars.xml.serialize.StarsCustomerContact;
import com.cannontech.stars.xml.serialize.StarsDevice;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMHw;
import com.cannontech.stars.xml.serialize.StarsSiteInformation;
import com.cannontech.stars.xml.serialize.StarsSrvReq;
import com.cannontech.stars.xml.serialize.StarsThermostatSchedule;
import com.cannontech.stars.xml.serialize.StarsWebConfig;
import com.cannontech.stars.xml.serialize.StreetAddress;
import com.cannontech.stars.xml.serialize.Substation;
import com.cannontech.stars.xml.serialize.Voltage;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class StarsFactory {

	/* StarsCustListEntry factory methods */
	
	public static StarsCustListEntry newStarsCustListEntry(StarsCustListEntry entry, Class type) {
		try {
			StarsCustListEntry newEntry = (StarsCustListEntry) type.newInstance();
			newEntry.setEntryID( entry.getEntryID() );
			newEntry.setContent( entry.getContent() );
			//newEntry.setYukonDefID( entry.getYukonDefID() );
			
			return newEntry;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static StarsCustListEntry newStarsCustListEntry(YukonListEntry entry, Class type) {
		if (entry == null) return newEmptyStarsCustListEntry( type );
		
		try {
			StarsCustListEntry newEntry = (StarsCustListEntry) type.newInstance();
			newEntry.setEntryID( entry.getEntryID() );
			newEntry.setContent( entry.getEntryText() );
			//newEntry.setYukonDefID( entry.getYukonDefID() );
			
			return newEntry;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static StarsCustListEntry newEmptyStarsCustListEntry(Class type) {
		try {
			StarsCustListEntry newEntry = (StarsCustListEntry) type.newInstance();
			newEntry.setEntryID( 0 );
			newEntry.setContent( "(none)" );
			return newEntry;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/* StarsApp factory methods */

	public static StarsApp newStarsApp(StarsApp app, Class type) {
		try {
			StarsApp starsApp = (StarsApp) type.newInstance();
			
			starsApp.setApplianceID( app.getApplianceID() );
			starsApp.setApplianceCategoryID( app.getApplianceCategoryID() );
			starsApp.setDescription( app.getDescription() );
			starsApp.setYearManufactured( app.getYearManufactured() );
			starsApp.setManufacturer( app.getManufacturer() );
			starsApp.setLocation( app.getLocation() );
			starsApp.setServiceCompany( app.getServiceCompany() );
			starsApp.setNotes( app.getNotes() );
			starsApp.setModelNumber( app.getModelNumber() );
			starsApp.setKWCapacity( app.getKWCapacity() );
			starsApp.setEfficiencyRating( app.getEfficiencyRating() );
			
			starsApp.setAirConditioner( app.getAirConditioner() );
			starsApp.setWaterHeater( app.getWaterHeater() );
			starsApp.setDualFuel( app.getDualFuel() );
			starsApp.setGenerator( app.getGenerator() );
			starsApp.setGrainDryer( app.getGrainDryer() );
			starsApp.setStorageHeat( app.getStorageHeat() );
			starsApp.setHeatPump( app.getHeatPump() );
			starsApp.setIrrigation( app.getIrrigation() );
			
			return starsApp;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/* StarsCallRprt factory methods */

	public static StarsCallReport newStarsCallReport(CallReportBase callDB) {
		try {
			StarsCallReport starsCall = new StarsCallReport();
			
			String callNo = callDB.getCallNumber();
			if (callNo.startsWith( ServerUtils.AUTO_GEN_NUM_PREC ))
				callNo = callNo.substring( ServerUtils.AUTO_GEN_NUM_PREC.length() );
			
			YukonListEntry callType = YukonListFuncs.getYukonListEntry( callDB.getCallTypeID().intValue() );
			
			starsCall.setCallID( callDB.getCallID().intValue() );
			starsCall.setCallNumber( callNo );
			starsCall.setCallDate( callDB.getDateTaken() );
			starsCall.setCallType( (CallType)newStarsCustListEntry(callType, CallType.class) );
			starsCall.setDescription( callDB.getDescription() );
			starsCall.setTakenBy( callDB.getTakenBy() );
			
			return starsCall;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static void setCallReportBase(CallReportBase callDB, StarsCallRprt call) {
		if (call.getCallNumber() != null)
			callDB.setCallNumber( call.getCallNumber() );
		if (call.getCallType() != null)
			callDB.setCallTypeID( new Integer(call.getCallType().getEntryID()) );
		if (call.getCallDate() != null)
			callDB.setDateTaken( call.getCallDate() );
		if (call.getDescription() != null)
			callDB.setDescription( call.getDescription() );
		if (call.getTakenBy() != null)
			callDB.setTakenBy( call.getTakenBy() );
	}

	public static StarsCallReport[] getStarsCallReports(Integer accountID) {
        com.cannontech.database.db.stars.report.CallReportBase[] calls =
        		CallReportBase.getAllCallReports( accountID );
        if (calls == null) return null;
        
        StarsCallReport[] callRprts = new StarsCallReport[ calls.length ];
        for (int i = 0; i < calls.length; i++) {
        	callRprts[i] = new StarsCallReport();
        	
        	callRprts[i].setCallID( calls[i].getCallID().intValue() );
        	callRprts[i].setCallDate( calls[i].getDateTaken() );
        	callRprts[i].setTakenBy( ServerUtils.forceNotNull(calls[i].getTakenBy()) );
        	callRprts[i].setDescription( ServerUtils.forceNotNull(calls[i].getDescription()) );
        	
        	String callNo = ServerUtils.forceNotNull(calls[i].getCallNumber());
        	if (callNo.startsWith( ServerUtils.AUTO_GEN_NUM_PREC ))
        		callNo = callNo.substring( ServerUtils.AUTO_GEN_NUM_PREC.length() );
			callRprts[i].setCallNumber( callNo );
        	
        	CallType callType = new CallType();
        	StarsLiteFactory.setStarsCustListEntry( callType, YukonListFuncs.getYukonListEntry(calls[i].getCallTypeID().intValue()) );
        	callRprts[i].setCallType( callType );
        }
        
        return callRprts;
	}
	
	
	/* StarsCustAccount factory methods */

	public static StarsCustAccount newStarsCustAccount(StarsCustAccount account, Class type) {
        try {
            StarsCustAccount newAccount = (StarsCustAccount) type.newInstance();

            newAccount.setAccountNumber( account.getAccountNumber() );
            newAccount.setIsCommercial( account.getIsCommercial() );
            newAccount.setCompany( account.getCompany() );
            newAccount.setAccountNotes( account.getAccountNotes() );
            newAccount.setPropertyNumber( account.getPropertyNumber() );
            newAccount.setPropertyNotes( account.getPropertyNotes() );
            newAccount.setStreetAddress( account.getStreetAddress() );
            newAccount.setStarsSiteInformation( account.getStarsSiteInformation() );
            newAccount.setBillingAddress( account.getBillingAddress() );
            newAccount.setPrimaryContact( account.getPrimaryContact() );
            newAccount.setAdditionalContact( account.getAdditionalContact() );

            return newAccount;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

	public static void setCustomerAccount(CustomerAccount account, StarsCustAccount starsAccount) {
    	account.setAccountNumber( starsAccount.getAccountNumber() );
    	account.setAccountNotes( starsAccount.getAccountNotes() );
    }

	public static void setAccountSite(AccountSite acctSite, StarsCustAccount starsAccount) {
    	acctSite.setSiteNumber( starsAccount.getPropertyNumber() );
    	acctSite.setPropertyNotes( starsAccount.getPropertyNotes() );
    }

	public static void setSiteInformation(SiteInformation siteInfo, StarsCustAccount starsAccount) {
    	StarsSiteInformation starsSiteInfo = starsAccount.getStarsSiteInformation();
    	siteInfo.setSubstationID( new Integer(starsSiteInfo.getSubstation().getEntryID()) );
    	siteInfo.setFeeder( starsSiteInfo.getFeeder() );
    	siteInfo.setPole( starsSiteInfo.getPole() );
    	siteInfo.setTransformerSize( starsSiteInfo.getTransformerSize() );
    	siteInfo.setServiceVoltage( starsSiteInfo.getServiceVoltage() );
    }
    
    public static StarsCustomerAccount newStarsCustomerAccount() {
    	StarsCustomerAccount newAccount = new StarsCustomerAccount();
        newAccount.setAccountNumber( "" );
        newAccount.setIsCommercial( false );
        newAccount.setCompany( "" );
        newAccount.setAccountNotes( "" );
        newAccount.setPropertyNumber( "" );
        newAccount.setPropertyNotes( "" );
        newAccount.setStreetAddress( (StreetAddress)newStarsCustomerAddress(StreetAddress.class) );
        newAccount.setBillingAddress( (BillingAddress)newStarsCustomerAddress(BillingAddress.class) );
        newAccount.setPrimaryContact( (PrimaryContact)newStarsCustomerContact(PrimaryContact.class) );
        
        Substation sub = new Substation();
        sub.setEntryID( CtiUtilities.NONE_ID );
        StarsSiteInformation siteInfo = new StarsSiteInformation();
        siteInfo.setSubstation( sub );
        siteInfo.setFeeder( "" );
        siteInfo.setPole( "" );
        siteInfo.setTransformerSize( "" );
        siteInfo.setServiceVoltage( "" );
        newAccount.setStarsSiteInformation( siteInfo );
    	
    	return newAccount;
    }
    
    
    /* StarsFailure factory methods */

	public static StarsFailure newStarsFailure(int statusCode, String description) {
		StarsFailure failure = new StarsFailure();
		failure.setStatusCode( statusCode );
		failure.setDescription( description );
		
		return failure;
	}
	
	
	/* StarsCustomerAddress factory methods */
	
	public static StarsCustomerAddress newStarsCustomerAddress(Class type) {
        try {
            StarsCustomerAddress newAddr = (StarsCustomerAddress) type.newInstance();
	        newAddr.setStreetAddr1( "" );
	        newAddr.setStreetAddr2( "" );
	        newAddr.setCity( "" );
	        newAddr.setState( "" );
	        newAddr.setZip( "" );
	        newAddr.setCounty( "" );
	        
	        return newAddr;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
	}

	public static StarsCustomerAddress newStarsCustomerAddress(StarsCustomerAddress addr, Class type) {
        try {
            StarsCustomerAddress newAddr = (StarsCustomerAddress) type.newInstance();

            newAddr.setStreetAddr1( addr.getStreetAddr1() );
            newAddr.setStreetAddr2( addr.getStreetAddr2() );
            newAddr.setCity( addr.getCity() );
            newAddr.setState( addr.getState() );
            newAddr.setZip( addr.getZip() );
            newAddr.setCounty( addr.getCounty() );

            return newAddr;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

	public static void setCustomerAddress(Address addr, StarsCustomerAddress starsAddr) {
    	addr.setLocationAddress1( starsAddr.getStreetAddr1() );
    	addr.setLocationAddress2( starsAddr.getStreetAddr2() );
    	addr.setCityName( starsAddr.getCity() );
    	addr.setStateCode( starsAddr.getState() );
    	addr.setZipCode( starsAddr.getZip() );
    	if (starsAddr.getCounty() != null) addr.setCounty( starsAddr.getCounty() );
    }
    
    
    /* StarsCustomerContact factory methods */

	public static StarsContactNotification newStarsContactNotification(boolean enabled, String notification, Class type) {
        try {
            StarsContactNotification newNotif = (StarsContactNotification) type.newInstance();
            // If notification is empty, enabled is automatically set to false
            newNotif.setEnabled( (notification.length() > 0)? enabled : false );
            newNotif.setNotification( notification );
            
            return newNotif;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
	public static StarsCustomerContact newStarsCustomerContact(Class type) {
        try {
            StarsCustomerContact newContact = (StarsCustomerContact) type.newInstance();
            newContact.setContactID( CtiUtilities.NONE_ID );
            newContact.setLastName( "" );
            newContact.setFirstName( "" );
            newContact.setHomePhone( "" );
            newContact.setWorkPhone( "" );
            newContact.setEmail( (Email)newStarsContactNotification(false, "", Email.class) );

            return newContact;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
	public static StarsCustomerContact newStarsCustomerContact(StarsCustomerContact contact, Class type) {
        try {
            StarsCustomerContact newContact = (StarsCustomerContact) type.newInstance();
            newContact.setContactID( contact.getContactID() );
            newContact.setLastName( contact.getLastName() );
            newContact.setFirstName( contact.getFirstName() );
            newContact.setHomePhone( contact.getHomePhone() );
            newContact.setWorkPhone( contact.getWorkPhone() );
            newContact.setEmail( (Email) newStarsContactNotification(
            		contact.getEmail().getEnabled(), contact.getEmail().getNotification(), Email.class) );

            return newContact;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

	public static void setCustomerContact(Contact contact, StarsCustomerContact starsContact) {
    	//contact.setContactID( new Integer(starsContact.getContactID()) );
        contact.getContact().setContLastName( starsContact.getLastName() );
        contact.getContact().setContFirstName( starsContact.getFirstName() );
        
        ContactNotification notifHPhone = null;
        ContactNotification notifWPhone = null;
        ContactNotification notifEmail = null;
        
        Vector contactNotifVect = contact.getContactNotifVect();
        for (int i = 0; i < contactNotifVect.size(); i++) {
			ContactNotification notif = (ContactNotification) contactNotifVect.get(i);
			// Set all the opcode to DELETE first, then change them to UPDATE or add INSERT accordingly
			notif.setOpCode( Transaction.DELETE );
			
			if (notif.getNotificationCatID().intValue() == YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE)
				notifHPhone = notif;
			else if (notif.getNotificationCatID().intValue() == YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE)
				notifWPhone = notif;
			else if (notif.getNotificationCatID().intValue() == YukonListEntryTypes.YUK_ENTRY_ID_EMAIL)
				notifEmail = notif;
        }
        
        if (starsContact.getHomePhone().length() > 0) {
        	if (notifHPhone != null) {
        		notifHPhone.setNotification( starsContact.getHomePhone() );
        		notifHPhone.setOpCode( Transaction.UPDATE );
        	}
        	else {
				notifHPhone = new ContactNotification();
				notifHPhone.setNotificationCatID( new Integer(YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE) );
				notifHPhone.setNotification( starsContact.getHomePhone() );
				notifHPhone.setDisableFlag( "Y" );
				notifHPhone.setOpCode( Transaction.INSERT );
				
				contactNotifVect.add( notifHPhone );
        	}
        }
        
        if (starsContact.getWorkPhone().length() > 0) {
        	if (notifWPhone != null) {
        		notifWPhone.setNotification( starsContact.getWorkPhone() );
        		notifWPhone.setOpCode( Transaction.UPDATE );
        	}
        	else {
				notifWPhone = new ContactNotification();
				notifWPhone.setNotificationCatID( new Integer(YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE) );
				notifWPhone.setNotification( starsContact.getWorkPhone() );
				notifWPhone.setDisableFlag( "Y" );
				notifWPhone.setOpCode( Transaction.INSERT );
				
				contactNotifVect.add( notifWPhone );
        	}
        }
        
        if (starsContact.getEmail() != null && starsContact.getEmail().getNotification().length() > 0) {
        	if (notifEmail != null) {
				notifEmail.setNotification( starsContact.getEmail().getNotification() );
				notifEmail.setDisableFlag( starsContact.getEmail().getEnabled()? "N" : "Y" );
				notifEmail.setOpCode( Transaction.UPDATE );
        	}
        	else {
				notifEmail = new ContactNotification();
				notifEmail.setNotificationCatID( new Integer(YukonListEntryTypes.YUK_ENTRY_ID_EMAIL) );
				notifEmail.setNotification( starsContact.getEmail().getNotification() );
				notifEmail.setDisableFlag( starsContact.getEmail().getEnabled() ? "N" : "Y" );
				notifEmail.setOpCode( Transaction.INSERT );
				
				contactNotifVect.add( notifEmail );
        	}
        }
    }
    
    
    /* StarsInventory factory methods */

	public static StarsInventory newStarsInventory(Class type) {
		try {
			StarsInventory starsInv = (StarsInventory) type.newInstance();
			
			starsInv.setInventoryID( -1 );
			starsInv.setDeviceID( CtiUtilities.NONE_ID );
			starsInv.setDeviceLabel( "" );
			starsInv.setInstallationCompany( (InstallationCompany) newEmptyStarsCustListEntry(InstallationCompany.class) );
			starsInv.setInstallDate( new java.util.Date() );
			starsInv.setAltTrackingNumber( "" );
			starsInv.setVoltage( (Voltage) newEmptyStarsCustListEntry(Voltage.class) );
			starsInv.setNotes( "" );
			starsInv.setInstallationNotes( "" );
			starsInv.setDeviceStatus( (DeviceStatus) newEmptyStarsCustListEntry(DeviceStatus.class) );
			
			if (starsInv instanceof StarsLMHw) {
				((StarsLMHw)starsInv).setManufactureSerialNumber( "" );
				((StarsLMHw)starsInv).setLMDeviceType( (LMDeviceType) newEmptyStarsCustListEntry(LMDeviceType.class) );
			}
			else if (starsInv instanceof StarsDevice) {
				((StarsDevice)starsInv).setDeviceName( "" );
			}
			
			return starsInv;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static StarsLMHw newStarsLMHw(StarsLMHw hw, Class type) {
		try {
			StarsLMHw starsHw = (StarsLMHw) type.newInstance();
			
			starsHw.setInventoryID( hw.getInventoryID() );
			starsHw.setCategory( hw.getCategory() );
			starsHw.setDeviceLabel( hw.getDeviceLabel() );
			starsHw.setInstallationCompany( hw.getInstallationCompany() );
			starsHw.setReceiveDate( hw.getReceiveDate() );
			starsHw.setInstallDate( hw.getInstallDate() );
			starsHw.setRemoveDate( hw.getRemoveDate() );
			starsHw.setAltTrackingNumber( hw.getAltTrackingNumber() );
			starsHw.setVoltage( hw.getVoltage() );
			starsHw.setNotes( hw.getNotes() );
			starsHw.setInstallationNotes( hw.getInstallationNotes() );
			starsHw.setManufactureSerialNumber( hw.getManufactureSerialNumber() );
			starsHw.setLMDeviceType( hw.getLMDeviceType() );
			starsHw.setDeviceStatus( hw.getDeviceStatus() );
			starsHw.setStarsLMHardwareHistory( hw.getStarsLMHardwareHistory() );
			
			return starsHw;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void setInventoryBase(com.cannontech.database.db.stars.hardware.InventoryBase invDB, StarsLMHw starsHw) {
		if (starsHw.getInstallationCompany() != null)
			invDB.setInstallationCompanyID( new Integer(starsHw.getInstallationCompany().getEntryID()) );
		if (starsHw.getReceiveDate() != null)
			invDB.setReceiveDate( starsHw.getReceiveDate() );
		if (starsHw.getInstallDate() != null)
			invDB.setInstallDate( starsHw.getInstallDate() );
		if (starsHw.getRemoveDate() != null)
			invDB.setRemoveDate( starsHw.getRemoveDate() );
		invDB.setAlternateTrackingNumber( starsHw.getAltTrackingNumber() );
		if (starsHw.getVoltage() != null)
			invDB.setVoltageID( new Integer(starsHw.getVoltage().getEntryID()) );
		invDB.setNotes( starsHw.getNotes() );
		invDB.setDeviceID( new Integer(starsHw.getDeviceID()) );
		if (starsHw.getDeviceLabel().trim().length() > 0)
			invDB.setDeviceLabel( starsHw.getDeviceLabel() );
		else if (starsHw.getDeviceID() > 0)
			invDB.setDeviceLabel( PAOFuncs.getYukonPAOName(starsHw.getDeviceID()) );
		else
			invDB.setDeviceLabel( starsHw.getManufactureSerialNumber() );
	}
	
	
	/* StarsSrvReq factory methods */
	
	public static void setWorkOrderBase(WorkOrderBase orderDB, StarsSrvReq order) {
		//orderDB.setOrderID( new Integer(order.getOrderID()) );
		if (order.hasAccountID())
			orderDB.setAccountID( new Integer(order.getAccountID()) );
		if (order.getOrderNumber() != null)
			orderDB.setOrderNumber( order.getOrderNumber() );
		orderDB.setWorkTypeID( new Integer(order.getServiceType().getEntryID()) );
		orderDB.setDateReported( order.getDateReported() );
		orderDB.setServiceCompanyID( new Integer(order.getServiceCompany().getEntryID()) );
		orderDB.setOrderedBy( order.getOrderedBy() );
		orderDB.setDescription( order.getDescription() );
		
		/* Don't set the current state ID because we need to check if the state is valid
		if (order.getCurrentState() != null)
			orderDB.setCurrentStateID( new Integer(order.getCurrentState().getEntryID()) );
		*/
		if (order.getDateScheduled() != null)
			orderDB.setDateScheduled( order.getDateScheduled() );
		if (order.getDateCompleted() != null)
			orderDB.setDateCompleted( order.getDateCompleted() );
		if (order.getActionTaken() != null)
			orderDB.setActionTaken( order.getActionTaken() );
	}
	
	
	/* StarsThermostatSchedule factory methods */

	public static StarsThermostatSchedule newStarsThermostatSchedule(StarsThermostatSchedule sched) {
		StarsThermostatSchedule newSched = new StarsThermostatSchedule();
		newSched.setDay( sched.getDay() );
		newSched.setTime1( sched.getTime1() );
		newSched.setTemperature1( sched.getTemperature1() );
		newSched.setTime2( sched.getTime2() );
		newSched.setTemperature2( sched.getTemperature2() );
		newSched.setTime3( sched.getTime3() );
		newSched.setTemperature3( sched.getTemperature3() );
		newSched.setTime4( sched.getTime4() );
		newSched.setTemperature4( sched.getTemperature4() );
		
		return newSched;
	}
	
	
	/* StarsWebConfig factory methods */

	public static StarsWebConfig newStarsWebConfig(YukonWebConfiguration config) {
		StarsWebConfig starsConfig = new StarsWebConfig();
		
		starsConfig.setAlternateDisplayName( config.getAlternateDisplayName() );
		starsConfig.setDescription( config.getDescription() );
		starsConfig.setLogoLocation( config.getLogoLocation() );
		starsConfig.setURL( config.getURL() );
		
		return starsConfig;
	}

	public static void setWebConfig(YukonWebConfiguration config, StarsWebConfig starsConfig) {
		config.setAlternateDisplayName( starsConfig.getAlternateDisplayName() );
		config.setDescription( starsConfig.getDescription() );
		config.setLogoLocation( starsConfig.getLogoLocation() );
		config.setURL( starsConfig.getURL() );
	}
	
	
	/* StarsCustResidence factory methods */
	
	public static StarsCustResidence newStarsCustResidence(StarsCustResidence res, Class type) {
		try {
			StarsCustResidence newRes = (StarsCustResidence) type.newInstance();
			newRes.setResidenceType( res.getResidenceType() );
			newRes.setConstructionMaterial( res.getConstructionMaterial() );
			newRes.setDecadeBuilt( res.getDecadeBuilt() );
			newRes.setSquareFeet( res.getSquareFeet() );
			newRes.setInsulationDepth( res.getInsulationDepth() );
			newRes.setGeneralCondition( res.getGeneralCondition() );
			newRes.setMainCoolingSystem( res.getMainCoolingSystem() );
			newRes.setMainHeatingSystem( res.getMainHeatingSystem() );
			newRes.setNumberOfOccupants( res.getNumberOfOccupants() );
			newRes.setOwnershipType( res.getOwnershipType() );
			newRes.setMainFuelType( res.getMainFuelType() );
			newRes.setNotes( res.getNotes() );
			
			return newRes;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
