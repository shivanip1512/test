package com.cannontech.stars.xml;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
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
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.xml.serialize.BillingAddress;
import com.cannontech.stars.xml.serialize.CallType;
import com.cannontech.stars.xml.serialize.DeviceStatus;
import com.cannontech.stars.xml.serialize.DeviceType;
import com.cannontech.stars.xml.serialize.InstallationCompany;
import com.cannontech.stars.xml.serialize.PrimaryContact;
import com.cannontech.stars.xml.serialize.StarsApp;
import com.cannontech.stars.xml.serialize.StarsCallReport;
import com.cannontech.stars.xml.serialize.StarsCallRprt;
import com.cannontech.stars.xml.serialize.StarsCustAccount;
import com.cannontech.stars.xml.serialize.StarsCustListEntry;
import com.cannontech.stars.xml.serialize.StarsCustResidence;
import com.cannontech.stars.xml.serialize.StarsCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsCustomerAddress;
import com.cannontech.stars.xml.serialize.StarsCustomerContact;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsInv;
import com.cannontech.stars.xml.serialize.StarsSiteInformation;
import com.cannontech.stars.xml.serialize.StarsSrvReq;
import com.cannontech.stars.xml.serialize.StarsThermostatSchedule;
import com.cannontech.stars.xml.serialize.StarsUsr;
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
			CTILogger.error( e.getMessage(), e );
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
			CTILogger.error( e.getMessage(), e );
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
			CTILogger.error( e.getMessage(), e );
		}
		
		return null;
	}
	
	
	/* StarsApp factory methods */

	public static StarsApp newStarsApp(StarsApp app, Class type) {
		try {
			StarsApp starsApp = (StarsApp) type.newInstance();
			
			starsApp.setApplianceID( app.getApplianceID() );
			starsApp.setApplianceCategoryID( app.getApplianceCategoryID() );
			starsApp.setProgramID( app.getProgramID() );
			starsApp.setInventoryID( app.getInventoryID() );
			
			starsApp.setManufacturer( app.getManufacturer() );
			starsApp.setLocation( app.getLocation() );
			starsApp.setServiceCompany( app.getServiceCompany() );
			starsApp.setNotes( app.getNotes() );
			starsApp.setModelNumber( app.getModelNumber() );
			
			if (app.hasYearManufactured())
				starsApp.setYearManufactured( app.getYearManufactured() );
			if (app.hasKWCapacity())
				starsApp.setKWCapacity( app.getKWCapacity() );
			if (app.hasEfficiencyRating())
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
			CTILogger.error( e.getMessage(), e );
		}
		
		return null;
	}
	
	
	/* StarsCallRprt factory methods */

	public static StarsCallReport newStarsCallReport(CallReportBase callDB) {
		try {
			StarsCallReport starsCall = new StarsCallReport();
			
			starsCall.setCallID( callDB.getCallID().intValue() );
			starsCall.setCallNumber( callDB.getCallNumber() );
			starsCall.setCallDate( callDB.getDateTaken() );
			starsCall.setDescription( callDB.getDescription() );
			starsCall.setTakenBy( callDB.getTakenBy() );
			
			YukonListEntry callType = YukonListFuncs.getYukonListEntry( callDB.getCallTypeID().intValue() );
			starsCall.setCallType( (CallType)newStarsCustListEntry(callType, CallType.class) );
			
			return starsCall;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
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
			callRprts[i].setCallNumber( StarsUtils.forceNotNull(calls[i].getCallNumber()) );
			callRprts[i].setCallDate( calls[i].getDateTaken() );
			callRprts[i].setTakenBy( StarsUtils.forceNotNull(calls[i].getTakenBy()) );
			callRprts[i].setDescription( StarsUtils.forceNotNull(calls[i].getDescription()) );
        	
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
            
			newAccount.setAccountID( account.getAccountID() );
			newAccount.setCustomerID( account.getCustomerID() );
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
			newAccount.setTimeZone( account.getTimeZone() );
            
			return newAccount;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
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
		sub.setEntryID( CtiUtilities.NONE_ZERO_ID );
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
			CTILogger.error( e.getMessage(), e );
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
			CTILogger.error( e.getMessage(), e );
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
	public static StarsCustomerContact newStarsCustomerContact(Class type) {
		try {
			StarsCustomerContact newContact = (StarsCustomerContact) type.newInstance();
			newContact.setContactID( CtiUtilities.NONE_ZERO_ID );
			newContact.setLastName( "" );
			newContact.setFirstName( "" );
			return newContact;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}

		return null;
	}
    
	public static StarsCustomerContact newStarsCustomerContact(StarsCustomerContact contact, Class type) {
		try {
			StarsCustomerContact newContact = (StarsCustomerContact) type.newInstance();
			newContact.setContactID( contact.getContactID() );
			newContact.setLastName( contact.getLastName() );
			newContact.setFirstName( contact.getFirstName() );
			
			for (int i = 0; i < contact.getContactNotificationCount(); i++) {
				com.cannontech.stars.xml.serialize.ContactNotification notif = new com.cannontech.stars.xml.serialize.ContactNotification();
				notif.setNotifCatID( contact.getContactNotification(i).getNotifCatID() );
				notif.setDisabled( contact.getContactNotification(i).getDisabled() );
				notif.setNotification( contact.getContactNotification(i).getNotification() );
				newContact.addContactNotification( notif );
			}

			return newContact;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}

		return null;
	}

	public static void setCustomerContact(Contact contact, StarsCustomerContact starsContact) {
		//contact.setContactID( new Integer(starsContact.getContactID()) );
		contact.getContact().setContLastName( starsContact.getLastName() );
		contact.getContact().setContFirstName( starsContact.getFirstName() );
        
		for (int i = 0; i < contact.getContactNotifVect().size(); i++) {
			ContactNotification notif = (ContactNotification) contact.getContactNotifVect().get(i);
			// Set all the opcode to DELETE first, then change them to UPDATE or add INSERT accordingly
			notif.setOpCode( Transaction.DELETE );
		}
		
		for (int i = 0; i < starsContact.getContactNotificationCount(); i++) {
			com.cannontech.stars.xml.serialize.ContactNotification starsNotif = starsContact.getContactNotification(i);
			
			ContactNotification notif = null;
			for (int j = 0; j < contact.getContactNotifVect().size(); j++) {
				if (((ContactNotification)contact.getContactNotifVect().get(j)).getNotificationCatID().intValue() == starsNotif.getNotifCatID()) {
					notif = (ContactNotification) contact.getContactNotifVect().get(j);
					break;
				}
			}
			
			if (notif != null) {
				notif.setDisableFlag( starsNotif.getDisabled()? "Y" : "N" );
				notif.setNotification( starsNotif.getNotification() );
				if (notif.getOpCode() == Transaction.DELETE)
					notif.setOpCode( Transaction.UPDATE );
			}
			else {
				notif = new ContactNotification();
				notif.setNotificationCatID( new Integer(starsNotif.getNotifCatID()) );
				notif.setDisableFlag( starsNotif.getDisabled()? "Y" : "N" );
				notif.setNotification( starsNotif.getNotification() );
				notif.setOrdering( new Integer(i) );
				notif.setOpCode( Transaction.INSERT );
				contact.getContactNotifVect().add( notif );
			}
		}
	}
    
    
	/* StarsInventory factory methods */

	public static StarsInv newStarsInv(Class type) {
		try {
			StarsInv starsInv = (StarsInv) type.newInstance();
			
			starsInv.setInventoryID( -1 );
			starsInv.setDeviceID( CtiUtilities.NONE_ZERO_ID );
			starsInv.setDeviceType( (DeviceType) newEmptyStarsCustListEntry(DeviceType.class) );
			starsInv.setDeviceLabel( "" );
			starsInv.setInstallationCompany( (InstallationCompany) newEmptyStarsCustListEntry(InstallationCompany.class) );
			starsInv.setInstallDate( new java.util.Date() );
			starsInv.setAltTrackingNumber( "" );
			starsInv.setVoltage( (Voltage) newEmptyStarsCustListEntry(Voltage.class) );
			starsInv.setNotes( "" );
			starsInv.setInstallationNotes( "" );
			starsInv.setDeviceStatus( (DeviceStatus) newEmptyStarsCustListEntry(DeviceStatus.class) );
			
			return starsInv;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		
		return null;
	}

	public static StarsInv newStarsInv(StarsInv inv, Class type) {
		try {
			StarsInv starsInv = (StarsInv) type.newInstance();
			
			starsInv.setInventoryID( inv.getInventoryID() );
			starsInv.setDeviceID( inv.getDeviceID() );
			starsInv.setDeviceType( inv.getDeviceType() );
			starsInv.setCategory( inv.getCategory() );
			starsInv.setDeviceLabel( inv.getDeviceLabel() );
			starsInv.setInstallationCompany( inv.getInstallationCompany() );
			starsInv.setReceiveDate( inv.getReceiveDate() );
			starsInv.setInstallDate( inv.getInstallDate() );
			starsInv.setRemoveDate( inv.getRemoveDate() );
			starsInv.setAltTrackingNumber( inv.getAltTrackingNumber() );
			starsInv.setVoltage( inv.getVoltage() );
			starsInv.setNotes( inv.getNotes() );
			starsInv.setInstallationNotes( inv.getInstallationNotes() );
			starsInv.setDeviceStatus( inv.getDeviceStatus() );
			starsInv.setStarsLMHardwareHistory( inv.getStarsLMHardwareHistory() );
			
			starsInv.setLMHardware( inv.getLMHardware() );
			starsInv.setMCT( inv.getMCT() );
			
			return starsInv;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		
		return null;
	}
	
	public static void setInventoryBase(com.cannontech.database.db.stars.hardware.InventoryBase invDB, StarsInv starsInv) {
		if (starsInv.getInstallationCompany() != null)
			invDB.setInstallationCompanyID( new Integer(starsInv.getInstallationCompany().getEntryID()) );
		if (starsInv.getReceiveDate() != null)
			invDB.setReceiveDate( starsInv.getReceiveDate() );
		if (starsInv.getInstallDate() != null)
			invDB.setInstallDate( starsInv.getInstallDate() );
		if (starsInv.getRemoveDate() != null)
			invDB.setRemoveDate( starsInv.getRemoveDate() );
		if (starsInv.getAltTrackingNumber() != null)
			invDB.setAlternateTrackingNumber( starsInv.getAltTrackingNumber() );
		if (starsInv.getVoltage() != null)
			invDB.setVoltageID( new Integer(starsInv.getVoltage().getEntryID()) );
		if (starsInv.getNotes() != null)
			invDB.setNotes( starsInv.getNotes() );
		invDB.setDeviceID( new Integer(starsInv.getDeviceID()) );
		if (starsInv.getDeviceLabel() != null)
			invDB.setDeviceLabel( starsInv.getDeviceLabel() );
	}
	
	
	/* StarsSrvReq factory methods */
	
	public static void setWorkOrderBase(WorkOrderBase orderDB, StarsSrvReq order) {
		//orderDB.setOrderID( new Integer(order.getOrderID()) );
		if (order.hasAccountID())
			orderDB.setAccountID( new Integer(order.getAccountID()) );
		if (order.getOrderNumber() != null)
			orderDB.setOrderNumber( order.getOrderNumber() );
		orderDB.setWorkTypeID( new Integer(order.getServiceType().getEntryID()) );
		if (order.getCurrentState() != null)
			orderDB.setCurrentStateID( new Integer(order.getCurrentState().getEntryID()) );
		orderDB.setServiceCompanyID( new Integer(order.getServiceCompany().getEntryID()) );
		if (order.getDateReported() != null)
			orderDB.setDateReported( order.getDateReported() );
		if (order.getOrderedBy() != null)
			orderDB.setOrderedBy( order.getOrderedBy() );
		if (order.getDescription() != null)
			orderDB.setDescription( order.getDescription() );
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
			CTILogger.error( e.getMessage(), e );
		}
		
		return null;
	}
	
	
	/* StarsUsr factory methods */
	
	public static StarsUsr newStarsUser(StarsUsr user, Class type) {
		try {
			StarsUsr newUser = (StarsUsr) type.newInstance();
			newUser.setUserID( user.getUserID() );
			newUser.setUsername( user.getUsername() );
			newUser.setPassword( user.getPassword() );
			newUser.setStatus( user.getStatus() );
			newUser.setGroupID( user.getGroupID() );
			
			return newUser;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		
		return null;
	}

}
