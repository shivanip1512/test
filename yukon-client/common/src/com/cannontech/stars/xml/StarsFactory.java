package com.cannontech.stars.xml;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.data.customer.Contact;
import com.cannontech.database.db.contact.ContactNotification;
import com.cannontech.database.db.customer.Address;
import com.cannontech.database.db.web.YukonWebConfiguration;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.database.data.lite.StarsLiteFactory;
import com.cannontech.stars.database.db.customer.AccountSite;
import com.cannontech.stars.database.db.customer.CustomerAccount;
import com.cannontech.stars.database.db.customer.SiteInformation;
import com.cannontech.stars.database.db.report.CallReportBase;
import com.cannontech.stars.dr.account.dao.CallReportDao;
import com.cannontech.stars.dr.account.model.CallReport;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.xml.serialize.BillingAddress;
import com.cannontech.stars.xml.serialize.CallType;
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
import com.cannontech.stars.xml.serialize.StarsWebConfig;
import com.cannontech.stars.xml.serialize.StreetAddress;
import com.cannontech.stars.xml.serialize.Substation;
import com.cannontech.user.UserUtils;

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
			starsApp.setAddressingGroupID( app.getAddressingGroupID() );
			starsApp.setLoadNumber( app.getLoadNumber() );
			
			starsApp.setManufacturer( app.getManufacturer() );
			starsApp.setLocation( app.getLocation() );
			starsApp.setServiceCompany( app.getServiceCompany() );
			starsApp.setNotes( app.getNotes() );
			starsApp.setModelNumber( app.getModelNumber() );
			
			if (app.hasYearManufactured())
				starsApp.setYearManufactured( app.getYearManufactured() );
			if (app.hasKwCapacity())
				starsApp.setKwCapacity( app.getKwCapacity() );
			if (app.hasEfficiencyRating())
				starsApp.setEfficiencyRating( app.getEfficiencyRating() );
			
			starsApp.setAirConditioner( app.getAirConditioner() );
            starsApp.setDualStageAC( app.getDualStageAC() );
            starsApp.setChiller( app.getChiller() );
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
			
			YukonListEntry callType = YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( callDB.getCallTypeID().intValue() );
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

	public static List<StarsCallReport> getStarsCallReports(Integer accountID) {
	    
	    CallReportDao callReportDao = YukonSpringHook.getBean(CallReportDao.class);
	    List<CallReport> calls = callReportDao.getAllCallReportByAccountId(accountID);
	    
		if (calls == null) return null;
        
		List<StarsCallReport> callRprts = new ArrayList<StarsCallReport>(calls.size());
		for (CallReport call : calls) {
            StarsCallReport starsCallReport = new StarsCallReport();
            starsCallReport.setCallID(call.getCallId().intValue());
            starsCallReport.setCallNumber(StarsUtils.forceNotNull(call.getCallNumber()));
            starsCallReport.setCallDate(call.getDateTaken());
            starsCallReport.setTakenBy(StarsUtils.forceNotNull(call.getTakenBy()));
            starsCallReport.setDescription(call.getDescription());
            CallType callType = new CallType();
            StarsLiteFactory.setStarsCustListEntry(callType, YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(call.getCallTypeId()));
            starsCallReport.setCallType(callType);
            callRprts.add(starsCallReport);
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
            newAccount.setCICustomerType(account.getCICustomerType());
			newAccount.setAccountNotes( account.getAccountNotes() );
			newAccount.setPropertyNumber( account.getPropertyNumber() );
			newAccount.setPropertyNotes( account.getPropertyNotes() );
			newAccount.setStreetAddress( account.getStreetAddress() );
			newAccount.setStarsSiteInformation( account.getStarsSiteInformation() );
			newAccount.setBillingAddress( account.getBillingAddress() );
			newAccount.setPrimaryContact( account.getPrimaryContact() );
			newAccount.setAdditionalContact( account.getAdditionalContact() );
			newAccount.setTimeZone( account.getTimeZone() );
			newAccount.setCustomerNumber( account.getCustomerNumber() );
			newAccount.setRateScheduleID( account.getRateScheduleID() );
			newAccount.setAltTrackingNumber( account.getAltTrackingNumber() );
            newAccount.setCustAtHome( account.getCustAtHome() );
            newAccount.setCustStatus( account.getCustStatus() );
            
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
        acctSite.setCustAtHome( starsAccount.getCustAtHome() );
        acctSite.setCustomerStatus( starsAccount.getCustStatus() );
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
		newAccount.setCustomerNumber(CtiUtilities.STRING_NONE);
		newAccount.setRateScheduleID(CtiUtilities.NONE_ZERO_ID);
		newAccount.setAltTrackingNumber(CtiUtilities.STRING_NONE);
        newAccount.setCustStatus( "A" );
        newAccount.setCustAtHome( "N" );
        
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
	
	public static StarsFailure newStarsFailure(int statusCode, Exception exception) {
	    StarsFailure failure = new StarsFailure();
	    failure.setStatusCode(statusCode);
	    failure.setDescription(exception.getMessage());
	    failure.setException(exception);
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
			newContact.setLoginID(UserUtils.USER_NONE_ID);
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
			newContact.setLoginID( contact.getLoginID() );
			
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

	public static void setCustomerContact(Contact contact, StarsCustomerContact starsContact) 
	{
		//contact.setContactID( new Integer(starsContact.getContactID()) );
		contact.getContact().setContLastName( starsContact.getLastName() );
		contact.getContact().setContFirstName( starsContact.getFirstName() );
		contact.getContact().setLogInID( new Integer(starsContact.getLoginID()) );
        
		//TODO: Not a good way to do this
		contact.getContactNotifVect().removeAllElements();
        
        for (int i = 0; i < starsContact.getContactNotificationCount(); i++) 
		{
			com.cannontech.stars.xml.serialize.ContactNotification starsNotif = starsContact.getContactNotification(i);
			
			ContactNotification notif = new ContactNotification();
			notif.setNotificationCatID(new Integer(starsNotif.getNotifCatID()));
			notif.setNotification(starsNotif.getNotification());
			notif.setDisableFlag(starsNotif.getDisabled() ? "Y" : "N");
						
			contact.getContactNotifVect().addElement(notif);
		}
        /*
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
		*/
		//another quick fix...too many of these in STARS already!  :(
		/*this is a quick and dirty way to make sure the NestedDbPersistent comparator in Contact.update()
		will correctly perceive this as an entry to be removed from the table.*/
		/*for (int x = 0; x < contact.getContactNotifVect().size(); x++) 
		{
			if (((ContactNotification)contact.getContactNotifVect().get(x)).getOpCode() == Transaction.DELETE) 
			{
				contact.getContactNotifVect().remove(x);
				x--;
			}
		}*/
	}
    
    
	/* StarsInventory factory methods */

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
	
	public static void setInventoryBase(com.cannontech.stars.database.db.hardware.InventoryBase invDB, StarsInv starsInv) {
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
        if (starsInv.getDeviceStatus() != null)
            invDB.setCurrentStateID( starsInv.getDeviceStatus().getEntryID() );
	}
	
	
	/* StarsSrvReq factory methods */
	
	public static void setWorkOrderBase(com.cannontech.stars.database.data.report.WorkOrderBase workOrderBase, StarsSrvReq order) {
		//orderDB.setOrderID( new Integer(order.getOrderID()) );
		if (order.hasAccountID())
			workOrderBase.getWorkOrderBase().setAccountID( new Integer(order.getAccountID()) );
		if (order.getOrderNumber() != null)
			workOrderBase.getWorkOrderBase().setOrderNumber( order.getOrderNumber() );
		if (order.getAddtlOrderNumber() != null)
			workOrderBase.getWorkOrderBase().setAdditionalOrderNumber( order.getAddtlOrderNumber() );
		workOrderBase.getWorkOrderBase().setWorkTypeID( new Integer(order.getServiceType().getEntryID()) );
		if (order.getCurrentState() != null)
			workOrderBase.getWorkOrderBase().setCurrentStateID( new Integer(order.getCurrentState().getEntryID()) );
		workOrderBase.getWorkOrderBase().setServiceCompanyID( new Integer(order.getServiceCompany().getEntryID()) );
		if (order.getDateReported() != null)
			workOrderBase.getWorkOrderBase().setDateReported( order.getDateReported() );
		if (order.getOrderedBy() != null)
			workOrderBase.getWorkOrderBase().setOrderedBy( order.getOrderedBy() );
		if (order.getDescription() != null)
			workOrderBase.getWorkOrderBase().setDescription( order.getDescription() );
		if (order.getDateScheduled() != null)
			workOrderBase.getWorkOrderBase().setDateScheduled( order.getDateScheduled() );
		if (order.getDateCompleted() != null)
			workOrderBase.getWorkOrderBase().setDateCompleted( order.getDateCompleted() );
		if (order.getActionTaken() != null)
			workOrderBase.getWorkOrderBase().setActionTaken( order.getActionTaken() );
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
	
}
