package com.cannontech.database.data.lite.stars;

import java.util.*;

import com.cannontech.common.constants.*;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.*;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.util.*;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.serialize.types.*;
import com.cannontech.stars.xml.StarsCustListEntryFactory;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class StarsLiteFactory {

	public static LiteBase createLite( com.cannontech.database.db.DBPersistent db ) {
		LiteBase lite = null;
		
		if (db instanceof com.cannontech.database.data.customer.Contact) {
			lite = new LiteCustomerContact();
			setLiteCustomerContact( (LiteCustomerContact) lite, (com.cannontech.database.data.customer.Contact) db );
		}
		else if (db instanceof com.cannontech.database.db.customer.Address) {
			lite = new LiteAddress();
			setLiteAddress( (LiteAddress) lite, (com.cannontech.database.db.customer.Address) db );
		}
		else if (db instanceof com.cannontech.database.data.stars.hardware.LMHardwareBase) {
			lite = new LiteLMHardwareBase();
			setLiteLMHardware( (LiteLMHardwareBase) lite, (com.cannontech.database.data.stars.hardware.LMHardwareBase) db );
		}
		else if (db instanceof com.cannontech.database.data.stars.event.LMHardwareEvent) {
			lite = new LiteLMHardwareEvent();
			setLiteLMCustomerEvent( (LiteLMCustomerEvent) lite, ((com.cannontech.database.data.stars.event.LMHardwareEvent) db).getLMCustomerEventBase() );
		}
		else if (db instanceof com.cannontech.database.data.stars.event.LMProgramEvent) {
			lite = new LiteLMProgramEvent();
			setLiteLMCustomerEvent( (LiteLMCustomerEvent) lite, ((com.cannontech.database.data.stars.event.LMProgramEvent) db).getLMCustomerEventBase() );
		}
		else if (db instanceof com.cannontech.database.db.stars.report.WorkOrderBase) {
			lite = new LiteWorkOrderBase();
			setLiteWorkOrderBase( (LiteWorkOrderBase) lite, (com.cannontech.database.db.stars.report.WorkOrderBase) db );
		}
		else if (db instanceof com.cannontech.database.db.stars.customer.CustomerAccount) {
			lite = new LiteCustomerAccount();
			setLiteCustomerAccount( (LiteCustomerAccount) lite, (com.cannontech.database.db.stars.customer.CustomerAccount) db );
		}
		else if (db instanceof com.cannontech.database.data.customer.Customer) {
			lite = new LiteCustomer();
			setLiteCustomer( (LiteCustomer) lite, (com.cannontech.database.data.customer.Customer) db );
		}
		else if (db instanceof com.cannontech.database.db.stars.customer.AccountSite) {
			lite = new LiteAccountSite();
			setLiteAccountSite( (LiteAccountSite) lite, (com.cannontech.database.db.stars.customer.AccountSite) db );
		}
		else if (db instanceof com.cannontech.database.db.stars.customer.SiteInformation) {
			lite = new LiteSiteInformation();
			setLiteSiteInformation( (LiteSiteInformation) lite, (com.cannontech.database.db.stars.customer.SiteInformation) db );
		}
		else if (db instanceof com.cannontech.database.db.pao.LMControlHistory) {
			lite = new LiteLMControlHistory();
			setLiteLMControlHistory( (LiteLMControlHistory) lite, (com.cannontech.database.db.pao.LMControlHistory) db );
		}
		else if (db instanceof com.cannontech.database.db.stars.hardware.LMThermostatSeason) {
			lite = new LiteLMThermostatSeason();
			setLiteLMThermostatSeason( (LiteLMThermostatSeason) lite, (com.cannontech.database.db.stars.hardware.LMThermostatSeason) db );
		}
		else if (db instanceof com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry) {
			lite = new LiteLMThermostatSeasonEntry();
			setLiteLMThermostatSeasonEntry( (LiteLMThermostatSeasonEntry) lite, (com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry) db );
		}
		else if (db instanceof com.cannontech.database.data.stars.hardware.LMThermostatSeason) {
			lite = new LiteLMThermostatSeason();
			setLiteLMThermostatSeason( (LiteLMThermostatSeason) lite, (com.cannontech.database.data.stars.hardware.LMThermostatSeason) db );
		}
		else if (db instanceof com.cannontech.database.db.stars.hardware.LMThermostatManualOption) {
			lite = new LiteLMThermostatManualOption();
			setLiteLMThermostatManualOption( (LiteLMThermostatManualOption) lite, (com.cannontech.database.db.stars.hardware.LMThermostatManualOption) db );
		}
		else if (db instanceof com.cannontech.database.data.stars.appliance.ApplianceBase) {
			lite = new LiteStarsAppliance();
			setLiteApplianceBase( (LiteStarsAppliance) lite, (com.cannontech.database.data.stars.appliance.ApplianceBase) db );
		}
		
		return lite;
	}
	
	public static void setLiteCustomerContact(LiteCustomerContact liteContact, com.cannontech.database.data.customer.Contact contact) {
		liteContact.setContactID( contact.getContact().getContactID().intValue() );
		liteContact.setLastName( contact.getContact().getContLastName() );
		liteContact.setFirstName( contact.getContact().getContFirstName() );
		
		for (int i = 0; i < contact.getContactNotifVect().size(); i++) {
			com.cannontech.database.db.contact.ContactNotification notif =
					(com.cannontech.database.db.contact.ContactNotification) contact.getContactNotifVect().get(i);
			if (notif.getNotificationCatID().intValue() == YukonListEntryTypes.YUK_DEF_ID_HOME_PHONE)
				liteContact.setHomePhone( notif.getNotification() );
			else if (notif.getNotificationCatID().intValue() == YukonListEntryTypes.YUK_DEF_ID_WORK_PHONE)
				liteContact.setWorkPhone( notif.getNotification() );
			else if (notif.getNotificationCatID().intValue() == YukonListEntryTypes.YUK_DEF_ID_EMAIL)
				liteContact.setEmail( notif.getNotification() );
		}
	}
	
	public static void setLiteAddress(LiteAddress liteAddr, com.cannontech.database.db.customer.Address addr) {
		liteAddr.setAddressID( addr.getAddressID().intValue() );
		liteAddr.setLocationAddress1( addr.getLocationAddress1() );
		liteAddr.setLocationAddress2( addr.getLocationAddress2() );
		liteAddr.setCityName( addr.getCityName() );
		liteAddr.setStateCode( addr.getStateCode() );
		liteAddr.setZipCode( addr.getZipCode() );
		liteAddr.setCounty( addr.getCounty() );
	}
	
	public static void setLiteLMHardware(LiteLMHardwareBase liteHw, com.cannontech.database.data.stars.hardware.LMHardwareBase hw) {
		liteHw.setInventoryID( hw.getInventoryBase().getInventoryID().intValue() );
		liteHw.setAccountID( hw.getInventoryBase().getAccountID().intValue() );
		liteHw.setCategoryID( hw.getInventoryBase().getCategoryID().intValue() );
		liteHw.setInstallationCompanyID( hw.getInventoryBase().getInstallationCompanyID().intValue() );
		liteHw.setReceiveDate( hw.getInventoryBase().getReceiveDate().getTime() );
		liteHw.setInstallDate( hw.getInventoryBase().getInstallDate().getTime() );
		liteHw.setRemoveDate( hw.getInventoryBase().getRemoveDate().getTime() );
		liteHw.setAlternateTrackingNumber( hw.getInventoryBase().getAlternateTrackingNumber() );
		liteHw.setVoltageID( hw.getInventoryBase().getVoltageID().intValue() );
		liteHw.setNotes( hw.getInventoryBase().getNotes() );
		liteHw.setManufactureSerialNumber( hw.getLMHardwareBase().getManufacturerSerialNumber() );
		liteHw.setLmHardwareTypeID( hw.getLMHardwareBase().getLMHardwareTypeID().intValue() );
		
		liteHw.setLmHardwareHistory( new ArrayList() );
		com.cannontech.database.data.stars.event.LMHardwareEvent[] events =
				com.cannontech.database.data.stars.event.LMHardwareEvent.getAllLMHardwareEvents( hw.getInventoryBase().getInventoryID() );
		for (int i = 0; i < events.length; i++) {
			LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) createLite( events[i] );
			liteHw.getLmHardwareHistory().add( liteEvent );
		}
	}
	
	public static void setLiteLMCustomerEvent(LiteLMCustomerEvent liteEvent, com.cannontech.database.db.stars.event.LMCustomerEventBase event) {
        liteEvent.setEventID( event.getEventID().intValue() );
        liteEvent.setActionID( event.getActionID().intValue() );
        liteEvent.setEventDateTime( event.getEventDateTime().getTime() );
        liteEvent.setEventTypeID( event.getEventTypeID().intValue() );
        liteEvent.setNotes( event.getNotes() );
	}
	
	public static void setLiteWorkOrderBase(LiteWorkOrderBase liteOrder, com.cannontech.database.db.stars.report.WorkOrderBase order) {
		liteOrder.setOrderID( order.getOrderID().intValue() );
		liteOrder.setOrderNumber( order.getOrderNumber() );
		liteOrder.setWorkTypeID( order.getWorkTypeID().intValue() );
		liteOrder.setCurrentStateID( order.getCurrentStateID().intValue() );
		liteOrder.setServiceCompanyID( order.getServiceCompanyID().intValue() );
		liteOrder.setDateReported( order.getDateReported().getTime() );
		liteOrder.setDateScheduled( order.getDateScheduled().getTime() );
		liteOrder.setDateCompleted( order.getDateCompleted().getTime() );
		liteOrder.setDescription( order.getDescription() );
		liteOrder.setActionTaken( order.getActionTaken() );
		liteOrder.setOrderedBy( order.getOrderedBy() );
		liteOrder.setAccountID( order.getAccountID().intValue() );
	}
	
	public static void setLiteSiteInformation(LiteSiteInformation liteSiteInfo, com.cannontech.database.db.stars.customer.SiteInformation siteInfo) {
		liteSiteInfo.setSiteID( siteInfo.getSiteID().intValue() );
		liteSiteInfo.setFeeder( siteInfo.getFeeder() );
		liteSiteInfo.setPole( siteInfo.getPole() );
		liteSiteInfo.setTransformerSize( siteInfo.getTransformerSize() );
		liteSiteInfo.setServiceVoltage( siteInfo.getServiceVoltage() );
		liteSiteInfo.setSubstationID( siteInfo.getSubstationID().intValue() );
	}
	
	public static void setLiteCustomerAccount(LiteCustomerAccount liteAccount, com.cannontech.database.db.stars.customer.CustomerAccount account) {
		liteAccount.setAccountID( account.getAccountID().intValue() );
		liteAccount.setAccountSiteID( account.getAccountSiteID().intValue() );
		liteAccount.setAccountNumber( account.getAccountNumber() );
		liteAccount.setCustomerID( account.getCustomerID().intValue() );
		liteAccount.setBillingAddressID( account.getBillingAddressID().intValue() );
		liteAccount.setAccountNotes( account.getAccountNotes() );
		liteAccount.setLoginID( account.getLoginID().intValue() );
	}
	
	public static void setLiteCustomer(LiteCustomer liteCustomer, com.cannontech.database.data.customer.Customer customer) {
		liteCustomer.setCustomerID( customer.getCustomer().getCustomerID().intValue() );
		liteCustomer.setPrimaryContactID( customer.getCustomer().getPrimaryContactID().intValue() );
		liteCustomer.setCustomerTypeID( customer.getCustomer().getCustomerTypeID().intValue() );
		liteCustomer.setTimeZone( customer.getCustomer().getTimeZone() );
		
		int[] contactIDs = customer.getCustomerContactIDs();
		for (int i = 0; i < contactIDs.length; i++)
			liteCustomer.getAdditionalContacts().add( new Integer(contactIDs[i]) );
	}
	
	public static void setLiteAccountSite(LiteAccountSite liteAcctSite, com.cannontech.database.db.stars.customer.AccountSite acctSite) {
		liteAcctSite.setAccountSiteID( acctSite.getAccountSiteID().intValue() );
		liteAcctSite.setSiteInformationID( acctSite.getSiteInformationID().intValue() );
		liteAcctSite.setSiteNumber( acctSite.getSiteNumber() );
		liteAcctSite.setStreetAddressID( acctSite.getStreetAddressID().intValue() );
		liteAcctSite.setPropertyNotes( acctSite.getPropertyNotes() );
	}
	
	public static void setLiteLMControlHistory(LiteLMControlHistory liteCtrlHist, com.cannontech.database.db.pao.LMControlHistory ctrlHist) {
		liteCtrlHist.setLmCtrlHistID( ctrlHist.getLmCtrlHistID().intValue() );
		liteCtrlHist.setStartDateTime( ctrlHist.getStartDateTime().getTime() );
		liteCtrlHist.setControlDuration( ctrlHist.getControlDuration().longValue() );
		liteCtrlHist.setControlType( ctrlHist.getControlType() );
		liteCtrlHist.setCurrentDailyTime( ctrlHist.getCurrentDailyTime().longValue() );
		liteCtrlHist.setCurrentMonthlyTime( ctrlHist.getCurrentMonthlyTime().longValue() );
		liteCtrlHist.setCurrentSeasonalTime( ctrlHist.getCurrentSeasonalTime().longValue() );
		liteCtrlHist.setCurrentAnnualTime( ctrlHist.getCurrentAnnualTime().longValue() );
		liteCtrlHist.setActiveRestore( ctrlHist.getActiveRestore() );
	}

	public static void setLiteLMThermostatSeason(LiteLMThermostatSeason liteSeason, com.cannontech.database.db.stars.hardware.LMThermostatSeason season) {
		liteSeason.setSeasonID( season.getSeasonID().intValue() );
		liteSeason.setInventoryID( season.getInventoryID().intValue() );
		liteSeason.setWebConfigurationID( season.getWebConfigurationID().intValue() );
		liteSeason.setStartDate( season.getStartDate().getTime() );
		liteSeason.setDisplayOrder( season.getDisplayOrder().intValue() );
	}

	public static void setLiteLMThermostatSeasonEntry(LiteLMThermostatSeasonEntry liteSeasonEntry, com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry seasonEntry) {
		liteSeasonEntry.setEntryID( seasonEntry.getEntryID().intValue() );
		liteSeasonEntry.setSeasonID( seasonEntry.getSeasonID().intValue() );
		liteSeasonEntry.setTimeOfWeekID( seasonEntry.getTimeOfWeekID().intValue() );
		liteSeasonEntry.setStartTime( seasonEntry.getStartTime().intValue() );
		liteSeasonEntry.setTemperature( seasonEntry.getTemperature().intValue() );
	}
	
	public static void setLiteLMThermostatSeason(LiteLMThermostatSeason liteSeason, com.cannontech.database.data.stars.hardware.LMThermostatSeason season) {
		setLiteLMThermostatSeason( liteSeason, season.getLMThermostatSeason() );
		
		ArrayList seasonEntries = season.getLMThermostatSeasonEntries();
		liteSeason.setSeasonEntries( new ArrayList() );
		for (int i = 0; i < seasonEntries.size(); i++) {
			com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry seasonEntry =
					(com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry) seasonEntries.get(i);
			liteSeason.getSeasonEntries().add( createLite(seasonEntry) );
		}
	}
	
	public static void setLiteLMThermostatManualOption(LiteLMThermostatManualOption liteOption, com.cannontech.database.db.stars.hardware.LMThermostatManualOption option) {
		liteOption.setInventoryID( option.getInventoryID().intValue() );
		liteOption.setPreviousTemperature( option.getPreviousTemperature().intValue() );
		liteOption.setHoldTemperature( option.getHoldTemperature().equalsIgnoreCase("Y") ? true : false );
		liteOption.setOperationStateID( option.getOperationStateID().intValue() );
		liteOption.setFanOperationID( option.getFanOperationID().intValue() );
	}
	
	public static void setLiteApplianceBase(LiteStarsAppliance liteApp, com.cannontech.database.data.stars.appliance.ApplianceBase app) {
		liteApp.setApplianceID( app.getApplianceBase().getApplianceID().intValue() );
		liteApp.setAccountID( app.getApplianceBase().getAccountID().intValue() );
		liteApp.setApplianceCategoryID( app.getApplianceBase().getApplianceCategoryID().intValue() );
		liteApp.setLmProgramID( app.getApplianceBase().getLMProgramID().intValue() );
		liteApp.setYearManufactured( app.getApplianceBase().getYearManufactured().intValue() );
		liteApp.setManufacturerID( app.getApplianceBase().getManufacturerID().intValue() );
		liteApp.setLocationID( app.getApplianceBase().getLocationID().intValue() );
		liteApp.setNotes( app.getApplianceBase().getNotes() );
		
		if (app.getLMHardwareConfig().getInventoryID() != null) {
			liteApp.setInventoryID( app.getLMHardwareConfig().getInventoryID().intValue() );
			liteApp.setAddressingGroupID( app.getLMHardwareConfig().getAddressingGroupID().intValue() );
		}
	}
	
	
	public static DBPersistent createDBPersistent(LiteBase lite) {
		DBPersistent db = null;
		
		switch (lite.getLiteType()) {
			case LiteTypes.STARS_CUSTOMER_CONTACT:
				db = new com.cannontech.database.data.customer.Contact();
				setContact( (com.cannontech.database.data.customer.Contact) db, (LiteCustomerContact) lite );
				break;
			case LiteTypes.STARS_ADDRESS:
				db = new com.cannontech.database.db.customer.Address();
				setAddress( (com.cannontech.database.db.customer.Address) db, (LiteAddress) lite );
				break;
			case LiteTypes.STARS_CUSTOMER_ACCOUNT:
				db = new com.cannontech.database.db.stars.customer.CustomerAccount();
				setCustomerAccount( (com.cannontech.database.db.stars.customer.CustomerAccount) db, (LiteCustomerAccount) lite );
				break;
			case LiteTypes.STARS_CUSTOMER:
				db = new com.cannontech.database.db.customer.Customer();
				setCustomer( (com.cannontech.database.db.customer.Customer) db, (LiteCustomer) lite );
				break;
			case LiteTypes.STARS_ACCOUNT_SITE:
				db = new com.cannontech.database.db.stars.customer.AccountSite();
				setAccountSite( (com.cannontech.database.db.stars.customer.AccountSite) db, (LiteAccountSite) lite );
				break;
			case LiteTypes.STARS_SITE_INFORMATION:
				db = new com.cannontech.database.db.stars.customer.SiteInformation();
				setSiteInformation( (com.cannontech.database.db.stars.customer.SiteInformation) db, (LiteSiteInformation) lite );
				break;
			case LiteTypes.STARS_LMHARDWARE_EVENT:
				db = new com.cannontech.database.data.stars.event.LMHardwareEvent();
				setLMCustomerEventBase( (com.cannontech.database.data.stars.event.LMCustomerEventBase) db, (LiteLMCustomerEvent) lite );
				break;
			case LiteTypes.STARS_LMPROGRAM_EVENT:
				db = new com.cannontech.database.data.stars.event.LMProgramEvent();
				setLMCustomerEventBase( (com.cannontech.database.data.stars.event.LMCustomerEventBase) db, (LiteLMCustomerEvent) lite );
				break;
			case LiteTypes.STARS_LMHARDWARE:
				db = new com.cannontech.database.data.stars.hardware.LMHardwareBase();
				setLMHardwareBase( (com.cannontech.database.data.stars.hardware.LMHardwareBase) db, (LiteLMHardwareBase) lite );
				break;
			case LiteTypes.YUKON_USER:
				db = new com.cannontech.database.db.user.YukonUser();
				setYukonUser( (com.cannontech.database.db.user.YukonUser) db, (com.cannontech.database.data.lite.LiteYukonUser) lite );
				break;
			case LiteTypes.STARS_THERMOSTAT_SEASON:
				db = new com.cannontech.database.db.stars.hardware.LMThermostatSeason();
				setLMThermostatSeason( (com.cannontech.database.db.stars.hardware.LMThermostatSeason) db, (LiteLMThermostatSeason) lite );
				break;
			case LiteTypes.STARS_THERMOSTAT_SEASON_ENTRY:
				db = new com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry();
				setLMThermostatSeasonEntry( (com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry) db, (LiteLMThermostatSeasonEntry) lite );
				break;
			case LiteTypes.STARS_THERMOSTAT_MANUAL_OPTION:
				db = new com.cannontech.database.db.stars.hardware.LMThermostatManualOption();
				setLMThermostatManualOption( (com.cannontech.database.db.stars.hardware.LMThermostatManualOption) db, (LiteLMThermostatManualOption) lite );
				break;
			case LiteTypes.STARS_APPLIANCE:
				db = new com.cannontech.database.data.stars.appliance.ApplianceBase();
				setApplianceBase( (com.cannontech.database.data.stars.appliance.ApplianceBase) db, (LiteStarsAppliance) lite );
				break;
			case LiteTypes.STARS_WORK_ORDER_BASE:
				db = new com.cannontech.database.db.stars.report.WorkOrderBase();
				setWorkOrderBase( (com.cannontech.database.db.stars.report.WorkOrderBase) db, (LiteWorkOrderBase) lite );
				break;
		}
		
		return db;
	}
	
	public static void setContact(com.cannontech.database.data.customer.Contact contact, LiteCustomerContact liteContact) {
		contact.getContact().setContactID( new Integer(liteContact.getContactID()) );
		contact.getContact().setContLastName( liteContact.getLastName() );
		contact.getContact().setContFirstName( liteContact.getFirstName() );
		
		if (liteContact.getHomePhone() != null && liteContact.getHomePhone().length() > 0) {
			com.cannontech.database.db.contact.ContactNotification notif = new com.cannontech.database.db.contact.ContactNotification();
			notif.setContactID( new Integer(liteContact.getContactID()) );
			notif.setNotificationCatID( new Integer(YukonListEntryTypes.YUK_DEF_ID_HOME_PHONE) );
			notif.setNotification( liteContact.getHomePhone() );
			contact.getContactNotifVect().add( notif );
		}
		
		if (liteContact.getWorkPhone() != null && liteContact.getWorkPhone().length() > 0) {
			com.cannontech.database.db.contact.ContactNotification notif = new com.cannontech.database.db.contact.ContactNotification();
			notif.setContactID( new Integer(liteContact.getContactID()) );
			notif.setNotificationCatID( new Integer(YukonListEntryTypes.YUK_DEF_ID_WORK_PHONE) );
			notif.setNotification( liteContact.getWorkPhone() );
			contact.getContactNotifVect().add( notif );
		}
		
		if (liteContact.getEmail() != null && liteContact.getEmail().length() > 0) {
			com.cannontech.database.db.contact.ContactNotification notif = new com.cannontech.database.db.contact.ContactNotification();
			notif.setContactID( new Integer(liteContact.getContactID()) );
			notif.setNotificationCatID( new Integer(YukonListEntryTypes.YUK_DEF_ID_EMAIL) );
			notif.setNotification( liteContact.getEmail() );
			contact.getContactNotifVect().add( notif );
		}
	}
	
	public static void setAddress(com.cannontech.database.db.customer.Address addr, LiteAddress liteAddr) {
		addr.setAddressID( new Integer(liteAddr.getAddressID()) );
		addr.setLocationAddress1( liteAddr.getLocationAddress1() );
		addr.setLocationAddress2( liteAddr.getLocationAddress2() );
		addr.setCityName( liteAddr.getCityName() );
		addr.setStateCode( liteAddr.getStateCode() );
		addr.setZipCode( liteAddr.getZipCode() );
		addr.setCounty( liteAddr.getCounty() );
	}
	
	public static void setCustomerAccount(com.cannontech.database.db.stars.customer.CustomerAccount account, LiteCustomerAccount liteAccount) {
		account.setAccountID( new Integer(liteAccount.getAccountID()) );
		account.setAccountSiteID( new Integer(liteAccount.getAccountSiteID()) );
		account.setAccountNumber( liteAccount.getAccountNumber() );
		account.setCustomerID( new Integer(liteAccount.getCustomerID()) );
		account.setBillingAddressID( new Integer(liteAccount.getBillingAddressID()) );
		account.setAccountNotes( liteAccount.getAccountNotes() );
		account.setLoginID( new Integer(liteAccount.getLoginID()) );
	}
	
	public static void setCustomer(com.cannontech.database.db.customer.Customer customer, LiteCustomer liteCustomer) {
		customer.setCustomerID( new Integer(liteCustomer.getCustomerID()) );
		customer.setPrimaryContactID( new Integer(liteCustomer.getPrimaryContactID()) );
		customer.setCustomerTypeID( new Integer(liteCustomer.getCustomerTypeID()) );
		customer.setTimeZone( liteCustomer.getTimeZone() );
	}
	
	public static void setAccountSite(com.cannontech.database.db.stars.customer.AccountSite acctSite, LiteAccountSite liteAcctSite) {
		acctSite.setAccountSiteID( new Integer(liteAcctSite.getAccountSiteID()) );
		acctSite.setSiteInformationID( new Integer(liteAcctSite.getSiteInformationID()) );
		acctSite.setSiteNumber( liteAcctSite.getSiteNumber() );
		acctSite.setStreetAddressID( new Integer(liteAcctSite.getStreetAddressID()) );
		acctSite.setPropertyNotes( liteAcctSite.getPropertyNotes() );
	}
	
	public static void setSiteInformation(com.cannontech.database.db.stars.customer.SiteInformation siteInfo, LiteSiteInformation liteSiteInfo) {
		siteInfo.setSiteID( new Integer(liteSiteInfo.getSiteID()) );
		siteInfo.setSubstationID( new Integer(liteSiteInfo.getSubstationID()) );
		siteInfo.setFeeder( liteSiteInfo.getFeeder() );
		siteInfo.setPole( liteSiteInfo.getPole() );
		siteInfo.setTransformerSize( liteSiteInfo.getTransformerSize() );
		siteInfo.setServiceVoltage( liteSiteInfo.getServiceVoltage() );
	}
	
	public static void setLMCustomerEventBase(com.cannontech.database.data.stars.event.LMCustomerEventBase event, LiteLMCustomerEvent liteEvent) {
		event.setEventID( new Integer(liteEvent.getEventID()) );
		event.getLMCustomerEventBase().setEventTypeID( new Integer(liteEvent.getEventTypeID()) );
		event.getLMCustomerEventBase().setActionID( new Integer(liteEvent.getActionID()) );
		event.getLMCustomerEventBase().setEventDateTime( new Date(liteEvent.getEventDateTime()) );
		event.getLMCustomerEventBase().setNotes( liteEvent.getNotes() );
	}
	
	public static void setLMHardwareBase(com.cannontech.database.data.stars.hardware.LMHardwareBase hw, LiteLMHardwareBase liteHw) {
		hw.setInventoryID( new Integer(liteHw.getInventoryID()) );
		hw.getInventoryBase().setAccountID( new Integer(liteHw.getAccountID()) );
		hw.getInventoryBase().setCategoryID( new Integer(liteHw.getCategoryID()) );
		hw.getInventoryBase().setInstallationCompanyID( new Integer(liteHw.getInstallationCompanyID()) );
		hw.getInventoryBase().setReceiveDate( new Date(liteHw.getReceiveDate()) );
		hw.getInventoryBase().setInstallDate( new Date(liteHw.getInstallDate()) );
		hw.getInventoryBase().setRemoveDate( new Date(liteHw.getRemoveDate()) );
		hw.getInventoryBase().setAlternateTrackingNumber( liteHw.getAlternateTrackingNumber() );
		hw.getInventoryBase().setVoltageID( new Integer(liteHw.getVoltageID()) );
		hw.getInventoryBase().setNotes( liteHw.getNotes() );
		hw.getLMHardwareBase().setManufacturerSerialNumber( liteHw.getManufactureSerialNumber() );
		hw.getLMHardwareBase().setLMHardwareTypeID( new Integer(liteHw.getLmHardwareTypeID()) );
	}
	
	public static void setYukonUser(com.cannontech.database.db.user.YukonUser user, com.cannontech.database.data.lite.LiteYukonUser liteUser) {
		user.setUserID( new Integer(liteUser.getUserID()) );
		user.setUsername( liteUser.getUsername() );
		user.setPassword( liteUser.getPassword() );
	}
	
	public static void setLMThermostatSeason(com.cannontech.database.db.stars.hardware.LMThermostatSeason season, LiteLMThermostatSeason liteSeason) {
		season.setSeasonID( new Integer(liteSeason.getSeasonID()) );
		season.setInventoryID( new Integer(liteSeason.getInventoryID()) );
		season.setWebConfigurationID( new Integer(liteSeason.getWebConfigurationID()) );
		season.setStartDate( new Date(liteSeason.getStartDate()) );
		season.setDisplayOrder( new Integer(liteSeason.getDisplayOrder()) );
	}
	
	public static void setLMThermostatSeasonEntry(com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry entry, LiteLMThermostatSeasonEntry liteEntry) {
		entry.setEntryID( new Integer(liteEntry.getEntryID()) );
		entry.setSeasonID( new Integer(liteEntry.getSeasonID()) );
		entry.setTimeOfWeekID( new Integer(liteEntry.getTimeOfWeekID()) );
		entry.setStartTime( new Integer(liteEntry.getStartTime()) );
		entry.setTemperature( new Integer(liteEntry.getTemperature()) );
	}
	
	public static void setLMThermostatManualOption(com.cannontech.database.db.stars.hardware.LMThermostatManualOption option, LiteLMThermostatManualOption liteOption) {
		option.setInventoryID( new Integer(liteOption.getInventoryID()) );
		option.setPreviousTemperature( new Integer(liteOption.getPreviousTemperature()) );
		option.setHoldTemperature( liteOption.isHoldTemperature() ? "Y":"N" );
		option.setOperationStateID( new Integer(liteOption.getOperationStateID()) );
		option.setFanOperationID( new Integer(liteOption.getFanOperationID()) );
	}
	
	public static void setApplianceBase(com.cannontech.database.data.stars.appliance.ApplianceBase app, LiteStarsAppliance liteApp) {
		app.setApplianceID( new Integer(liteApp.getApplianceID()) );
		app.getApplianceBase().setAccountID( new Integer(liteApp.getAccountID()) );
		app.getApplianceBase().setApplianceCategoryID( new Integer(liteApp.getApplianceCategoryID()) );
		app.getApplianceBase().setLMProgramID( new Integer(liteApp.getLmProgramID()) );
		app.getApplianceBase().setYearManufactured( new Integer(liteApp.getYearManufactured()) );
		app.getApplianceBase().setManufacturerID( new Integer(liteApp.getManufacturerID()) );
		app.getApplianceBase().setLocationID( new Integer(liteApp.getLocationID()) );
		app.getApplianceBase().setNotes( liteApp.getNotes() );
		
		if (liteApp.getInventoryID() != com.cannontech.database.db.stars.hardware.InventoryBase.NONE_INT) {
			app.getLMHardwareConfig().setApplianceID( app.getApplianceBase().getApplianceID() );
			app.getLMHardwareConfig().setInventoryID( new Integer(liteApp.getInventoryID()) );
			app.getLMHardwareConfig().setAddressingGroupID( new Integer(liteApp.getAddressingGroupID()) );
		}
	}
	
	public static void setWorkOrderBase(com.cannontech.database.db.stars.report.WorkOrderBase order, LiteWorkOrderBase liteOrder) {
		order.setOrderID( new Integer(liteOrder.getOrderID()) );
		order.setOrderNumber( liteOrder.getOrderNumber() );
		order.setWorkTypeID( new Integer(liteOrder.getWorkTypeID()) );
		order.setCurrentStateID( new Integer(liteOrder.getCurrentStateID()) );
		order.setServiceCompanyID( new Integer(liteOrder.getServiceCompanyID()) );
		order.setDateReported( new Date(liteOrder.getDateReported()) );
		order.setOrderedBy( liteOrder.getOrderedBy() );
		order.setDescription( liteOrder.getDescription() );
		order.setDateScheduled( new Date(liteOrder.getDateScheduled()) );
		order.setDateCompleted( new Date(liteOrder.getDateCompleted()) );
		order.setActionTaken( liteOrder.getActionTaken() );
		order.setAccountID( new Integer(liteOrder.getAccountID()) );
	}
	
	
	public static com.cannontech.database.data.stars.customer.CustomerAccount createCustomerAccount(LiteStarsCustAccountInformation liteAccount, int energyCompanyID) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
		com.cannontech.database.data.stars.customer.CustomerAccount account =
				new com.cannontech.database.data.stars.customer.CustomerAccount();
				
		account.setCustomerAccount( (com.cannontech.database.db.stars.customer.CustomerAccount) createDBPersistent(liteAccount.getCustomerAccount()) );
		
		LiteAddress liteAddr = energyCompany.getAddress( liteAccount.getCustomerAccount().getBillingAddressID() );
		account.setBillingAddress( (com.cannontech.database.db.customer.Address) createDBPersistent(liteAddr) );
				
		com.cannontech.database.data.stars.customer.AccountSite acctSite =
				new com.cannontech.database.data.stars.customer.AccountSite();
		acctSite.setAccountSite( (com.cannontech.database.db.stars.customer.AccountSite) createDBPersistent(liteAccount.getAccountSite()) );
		com.cannontech.database.data.stars.customer.SiteInformation siteInfo =
				new com.cannontech.database.data.stars.customer.SiteInformation();
		siteInfo.setSiteInformation( (com.cannontech.database.db.stars.customer.SiteInformation) createDBPersistent(liteAccount.getSiteInformation()) );
		acctSite.setSiteInformation( siteInfo );
		liteAddr = energyCompany.getAddress( liteAccount.getAccountSite().getStreetAddressID() );
		acctSite.setStreetAddress( (com.cannontech.database.db.customer.Address) createDBPersistent(liteAddr) );
		account.setAccountSite( acctSite );
		
		com.cannontech.database.data.customer.Customer customer =
				new com.cannontech.database.data.customer.Customer();
		customer.setCustomer( (com.cannontech.database.db.customer.Customer) createDBPersistent(liteAccount.getCustomer()) );
		account.setCustomer( customer );
		
		for (int i = 0; i < liteAccount.getInventories().size(); i++) {
			LiteLMHardwareBase liteHw = energyCompany.getLMHardware( ((Integer) liteAccount.getInventories().get(i)).intValue(), true );
			com.cannontech.database.data.stars.hardware.LMHardwareBase hw =
					(com.cannontech.database.data.stars.hardware.LMHardwareBase) createDBPersistent(liteHw);
			account.getInventoryVector().addElement( hw.getInventoryBase() );
		}
		
		for (int i = 0; i < liteAccount.getAppliances().size(); i++) {
			LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAccount.getAppliances().get(i);
			com.cannontech.database.data.stars.appliance.ApplianceBase app =
					(com.cannontech.database.data.stars.appliance.ApplianceBase) createDBPersistent(liteApp);
			account.getApplianceVector().addElement( app );
		}
		
		return account;
	}

	
	public static void setStarsCustomerContact(StarsCustomerContact starsContact, LiteCustomerContact liteContact) {
		starsContact.setContactID( liteContact.getContactID() );
		starsContact.setLastName( forceNotNull(liteContact.getLastName()) );
		starsContact.setFirstName( forceNotNull(liteContact.getFirstName()) );
		starsContact.setHomePhone( forceNotNull(liteContact.getHomePhone()) );
		starsContact.setWorkPhone( forceNotNull(liteContact.getWorkPhone()) );
		starsContact.setEmail( forceNotNull(liteContact.getEmail()) );
	}
	
	public static void setStarsCustomerAddress(StarsCustomerAddress starsAddr, LiteAddress liteAddr) {
		starsAddr.setAddressID( liteAddr.getAddressID() );
		starsAddr.setStreetAddr1( forceNotNull(liteAddr.getLocationAddress1()) );
		starsAddr.setStreetAddr2( forceNotNull(liteAddr.getLocationAddress2()) );
		starsAddr.setCity( forceNotNull(liteAddr.getCityName()) );
		starsAddr.setState( forceNotNull(liteAddr.getStateCode()) );
		starsAddr.setZip( forceNotNull(liteAddr.getZipCode()) );
		starsAddr.setCounty( forceNotNull(liteAddr.getCounty()) );
	}
	
	public static void setStarsLMCustomerEvent(StarsLMCustomerEvent starsEvent, LiteLMCustomerEvent liteEvent) {
		YukonListEntry entry = YukonListFuncs.getYukonListEntry( liteEvent.getActionID() );
		starsEvent.setEventAction( entry.getEntryText() );
		starsEvent.setEventDateTime( new Date(liteEvent.getEventDateTime()) );
		starsEvent.setNotes( forceNotNull(liteEvent.getNotes()) );
		starsEvent.setYukonDefID( entry.getYukonDefID() );
	}
	
	public static void setStarsThermostatSettings(StarsThermoSettings starsSettings, LiteStarsThermostatSettings liteSettings) {
		starsSettings.setInventoryID( liteSettings.getInventoryID() );
		
		for (int i = 0; i < liteSettings.getThermostatSeasons().size(); i++) {
			LiteLMThermostatSeason liteSeason = (LiteLMThermostatSeason) liteSettings.getThermostatSeasons().get(i);
			StarsThermostatSeason starsSeason = new StarsThermostatSeason();
			
			LiteWebConfiguration liteConfig = SOAPServer.getWebConfiguration( liteSeason.getWebConfigurationID() );
			if (liteConfig.getAlternateDisplayName().equalsIgnoreCase("Summer"))
				starsSeason.setMode( StarsThermoModeSettings.COOL );
			else
				starsSeason.setMode( StarsThermoModeSettings.HEAT );
			
			Calendar startCal = Calendar.getInstance();
			startCal.setTime( new Date(liteSeason.getStartDate()) );
			startCal.set( Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) );
			starsSeason.setStartDate( new org.exolab.castor.types.Date(startCal.getTime()) );
			
			if (liteSettings.getInventoryID() < 0)	// Add thermostat season web configuration only to default settings
				starsSeason.setStarsWebConfig( createStarsWebConfig(liteConfig) );
			
			Hashtable towTable = new Hashtable();
			for (int j = 0; j < liteSeason.getSeasonEntries().size(); j++) {
				LiteLMThermostatSeasonEntry liteEntry = (LiteLMThermostatSeasonEntry) liteSeason.getSeasonEntries().get(j);
				Integer towID = new Integer( liteEntry.getTimeOfWeekID() );
				
				ArrayList liteEntryList = (ArrayList) towTable.get( towID );
				if (liteEntryList == null) {
					liteEntryList = new ArrayList();
					towTable.put( towID, liteEntryList );
				}
				liteEntryList.add( liteEntry );
			}
			
			Iterator it = towTable.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				Integer towID = (Integer) entry.getKey();
				ArrayList liteEntryList = (ArrayList) entry.getValue();
				
				/* Each thermostat schedule must have 4 "time/temperature"s,
				 * othewise, use the energy company default.
				 */
				if (liteEntryList.size() != 4) continue;
				
				StarsThermostatSchedule starsSchd = new StarsThermostatSchedule();
				starsSchd.setDay( ServerUtils.getThermDaySetting(towID.intValue()) );
				
				LiteLMThermostatSeasonEntry liteEntry = (LiteLMThermostatSeasonEntry) liteEntryList.get(0);
				starsSchd.setTime1( new org.exolab.castor.types.Time(liteEntry.getStartTime() * 1000) );
				starsSchd.setTemperature1( liteEntry.getTemperature() );
				
				liteEntry = (LiteLMThermostatSeasonEntry) liteEntryList.get(1);
				starsSchd.setTime2( new org.exolab.castor.types.Time(liteEntry.getStartTime() * 1000) );
				starsSchd.setTemperature2( liteEntry.getTemperature() );
				
				liteEntry = (LiteLMThermostatSeasonEntry) liteEntryList.get(2);
				starsSchd.setTime3( new org.exolab.castor.types.Time(liteEntry.getStartTime() * 1000) );
				starsSchd.setTemperature3( liteEntry.getTemperature() );
				
				liteEntry = (LiteLMThermostatSeasonEntry) liteEntryList.get(3);
				starsSchd.setTime4( new org.exolab.castor.types.Time(liteEntry.getStartTime() * 1000) );
				starsSchd.setTemperature4( liteEntry.getTemperature() );
				
				starsSeason.addStarsThermostatSchedule( starsSchd );
			}
			
			starsSettings.addStarsThermostatSeason( starsSeason );
		}
		
		if (liteSettings.getThermostatOption() != null)
			starsSettings.setStarsThermostatManualOption( createStarsThermostatManualOption(liteSettings.getThermostatOption()) );
	}
	
	public static void setStarsQuestionAnswer(StarsQuestionAnswer starsQuestion, LiteInterviewQuestion liteQuestion) {
		starsQuestion.setQuestionID( liteQuestion.getQuestionID() );
		starsQuestion.setQuestion( liteQuestion.getQuestion() );
		
		QuestionType qType = new QuestionType();
		qType.setEntryID( liteQuestion.getQuestionType() );
		qType.setContent( YukonListFuncs.getYukonListEntry(liteQuestion.getQuestionType()).getEntryText() );
		starsQuestion.setQuestionType( qType );
		
		AnswerType aType = new AnswerType();
		aType.setEntryID( liteQuestion.getAnswerType() );
		aType.setContent( YukonListFuncs.getYukonListEntry(liteQuestion.getAnswerType()).getEntryText() );
		starsQuestion.setAnswerType( aType );
	}
	
	public static void setStarsCustListEntry(StarsCustListEntry starsEntry, YukonListEntry yukonEntry) {
		starsEntry.setEntryID( yukonEntry.getEntryID() );
		starsEntry.setContent( yukonEntry.getEntryText() );
		//starsEntry.setYukonDefID( yukonEntry.getYukonDefID() );
	}
	
	public static void setStarsCustAccountInformation(StarsCustAccountInformation starsAcctInfo, LiteStarsCustAccountInformation liteAcctInfo, int energyCompanyID, boolean isOperator) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
		
		LiteCustomerAccount liteAccount = liteAcctInfo.getCustomerAccount();
		LiteCustomer liteCustomer = liteAcctInfo.getCustomer();
		LiteAccountSite liteAcctSite = liteAcctInfo.getAccountSite();
		LiteSiteInformation liteSiteInfo = liteAcctInfo.getSiteInformation();
		
		StarsCustomerAccount starsAccount = new StarsCustomerAccount();
		starsAccount.setAccountID( liteAccount.getAccountID() );
		starsAccount.setCustomerID( liteAccount.getCustomerID() );
		starsAccount.setAccountNumber( forceNotNull(liteAccount.getAccountNumber()) );
		starsAccount.setIsCommercial( liteCustomer.getCustomerTypeID() == CustomerTypes.CUSTOMER_CI );
		starsAccount.setCompany( "" );
		starsAccount.setAccountNotes( forceNotNull(liteAccount.getAccountNotes()) );
		starsAccount.setPropertyNumber( forceNotNull(liteAcctSite.getSiteNumber()) );
		starsAccount.setPropertyNotes( forceNotNull(liteAcctSite.getPropertyNotes()) );
		starsAccount.setTimeZone( liteCustomer.getTimeZone() );
		starsAcctInfo.setStarsCustomerAccount( starsAccount );
		
		StreetAddress streetAddr = new StreetAddress();
		setStarsCustomerAddress( streetAddr, energyCompany.getAddress(liteAcctSite.getStreetAddressID()) );
		starsAccount.setStreetAddress( streetAddr );
		
		starsAccount.setStarsSiteInformation( createStarsSiteInformation(liteSiteInfo, energyCompanyID) );
				
		BillingAddress billAddr = new BillingAddress();
		setStarsCustomerAddress( billAddr, energyCompany.getAddress(liteAccount.getBillingAddressID()) );
		starsAccount.setBillingAddress( billAddr );
		
		PrimaryContact primContact = new PrimaryContact();
		setStarsCustomerContact( primContact, energyCompany.getCustomerContact(liteCustomer.getPrimaryContactID()) );
		starsAccount.setPrimaryContact( primContact );
		
		for (int i = 0; i < liteCustomer.getAdditionalContacts().size(); i++) {
			Integer contactID = (Integer) liteCustomer.getAdditionalContacts().get(i);
			AdditionalContact contact = new AdditionalContact();
			setStarsCustomerContact( contact, energyCompany.getCustomerContact(contactID.intValue()) );
			starsAccount.addAdditionalContact( contact );
		}
		
		ArrayList liteProgs = liteAcctInfo.getLmPrograms();
		StarsLMPrograms starsProgs = new StarsLMPrograms();
		starsAcctInfo.setStarsLMPrograms( starsProgs );
		
		for (int i = 0; i < liteProgs.size(); i++) {
			LiteStarsLMProgram liteProg = (LiteStarsLMProgram) liteProgs.get(i);
			LiteStarsAppliance liteApp = null;
			
			ArrayList liteApps = liteAcctInfo.getAppliances();
			for (int k = 0; k < liteApps.size(); k++) {
				LiteStarsAppliance lApp = (LiteStarsAppliance) liteApps.get(k);
				if (lApp.getLmProgramID() == liteProg.getLmProgram().getProgramID()) {
					liteApp = lApp;
					break;
				}
			}
			
			starsProgs.addStarsLMProgram( createStarsLMProgram(liteProg, liteApp, energyCompanyID) );
		}
		
		if (liteAcctInfo.getThermostatSettings() != null) {
			StarsThermostatSettings starsThermSettings = new StarsThermostatSettings();
			setStarsThermostatSettings( starsThermSettings, liteAcctInfo.getThermostatSettings() );
			starsAcctInfo.setStarsThermostatSettings( starsThermSettings );
			
			StarsDefaultThermostatSettings starsDftThermSettings = new StarsDefaultThermostatSettings();
			setStarsThermostatSettings( starsDftThermSettings, energyCompany.getDefaultThermostatSettings() );
			starsAcctInfo.setStarsDefaultThermostatSettings( starsDftThermSettings );
		}
		
		if (isOperator) {
			ArrayList liteApps = liteAcctInfo.getAppliances();
			StarsAppliances starsApps = new StarsAppliances();
			starsAcctInfo.setStarsAppliances( starsApps );
			
			TreeMap tmap = new TreeMap();
			for (int i = 0; i < liteApps.size(); i++) {
				LiteStarsAppliance liteApp = (LiteStarsAppliance) liteApps.get(i);
				StarsAppliance starsApp = (StarsAppliance) createStarsAppliance(liteApp, energyCompanyID);
				
				ArrayList list = (ArrayList) tmap.get( starsApp.getCategoryName() );
				if (list == null) {
					list = new ArrayList();
					tmap.put( starsApp.getCategoryName(), list );
				}
				list.add( starsApp );
			}
			
			Iterator it = tmap.values().iterator();
			while (it.hasNext()) {
				ArrayList list = (ArrayList) it.next();
				for (int i = 0; i < list.size(); i++)
					starsApps.addStarsAppliance( (StarsAppliance) list.get(i) );
			}
			
			ArrayList liteInvs = liteAcctInfo.getInventories();
			StarsInventories starsInvs = new StarsInventories();
			starsAcctInfo.setStarsInventories( starsInvs );
			
			tmap.clear();
			for (int i = 0; i < liteInvs.size(); i++) {
				LiteLMHardwareBase liteHw = energyCompany.getLMHardware( ((Integer) liteInvs.get(i)).intValue(), true );
				StarsLMHardware starsHw = createStarsLMHardware(liteHw, energyCompanyID);
				
				ArrayList list = (ArrayList) tmap.get( starsHw.getLMDeviceType().getContent() );
				if (list == null) {
					list = new ArrayList();
					tmap.put( starsHw.getLMDeviceType().getContent(), list );
				}
				list.add( starsHw );
			}
			
			it = tmap.values().iterator();
			while (it.hasNext()) {
				ArrayList list = (ArrayList) it.next();
				for (int i = 0; i < list.size(); i++)
					starsInvs.addStarsLMHardware( (StarsLMHardware) list.get(i) );
			}
			
			ArrayList liteCompanies = liteAcctInfo.getServiceCompanies();
			StarsServiceCompanies starsCompanies = new StarsServiceCompanies();
			starsAcctInfo.setStarsServiceCompanies( starsCompanies );
			
			for (int i = 0; i < liteCompanies.size(); i++) {
				LiteServiceCompany liteCompany = energyCompany.getServiceCompany( ((Integer) liteCompanies.get(i)).intValue() );
				StarsServiceCompany starsCompany = StarsLiteFactory.createStarsServiceCompany( liteCompany, energyCompanyID );
				starsCompanies.addStarsServiceCompany( starsCompany );
			}
			
			ArrayList liteCalls = liteAcctInfo.getCallReportHistory();
			StarsCallReportHistory starsCalls = new StarsCallReportHistory();
			starsAcctInfo.setStarsCallReportHistory( starsCalls );
			
			for (int i = 0; i < liteCalls.size(); i++) {
				StarsCallReport starsCall = (StarsCallReport) liteCalls.get(i);
				starsCalls.addStarsCallReport( starsCall );
			}
			
			ArrayList liteOrders = liteAcctInfo.getServiceRequestHistory();
			StarsServiceRequestHistory starsOrders = new StarsServiceRequestHistory();
			starsAcctInfo.setStarsServiceRequestHistory( starsOrders );
			
			for (int i = 0; i < liteOrders.size(); i++) {
				LiteWorkOrderBase liteOrder = energyCompany.getWorkOrderBase( ((Integer) liteOrders.get(i)).intValue() );
				starsOrders.addStarsServiceRequest( createStarsServiceRequest(liteOrder, energyCompanyID) );
			}
			
	        if (liteAccount.getLoginID() > com.cannontech.user.UserUtils.USER_YUKON_ID) {
		        LiteYukonUser liteUser = com.cannontech.database.cache.functions.YukonUserFuncs.getLiteYukonUser( liteAccount.getLoginID() );
				starsAcctInfo.setStarsUser( createStarsUser(liteUser) );
	        }
		}
	}
		
	
	public static StarsCustAccountInformation createStarsCustAccountInformation(LiteStarsCustAccountInformation liteAcctInfo, int energyCompanyID, boolean isOperator) {
		StarsCustAccountInformation starsAcctInfo = new StarsCustAccountInformation();
		setStarsCustAccountInformation( starsAcctInfo, liteAcctInfo, energyCompanyID, isOperator );
		return starsAcctInfo;
	}
	
	public static StarsSiteInformation createStarsSiteInformation(LiteSiteInformation liteSite, int energyCompanyID) {
		StarsSiteInformation starsSite = new StarsSiteInformation();
		
		starsSite.setSiteID( liteSite.getSiteID() );
		starsSite.setFeeder( forceNotNull(liteSite.getFeeder()) );
		starsSite.setPole( forceNotNull(liteSite.getPole()) );
		starsSite.setTransformerSize( forceNotNull(liteSite.getTransformerSize()) );
		starsSite.setServiceVoltage( forceNotNull(liteSite.getServiceVoltage()) );
		
		Substation sub = new Substation();
		sub.setEntryID( liteSite.getSubstationID() );
		sub.setContent( SOAPServer.getEnergyCompany(energyCompanyID).getStarsSelectionListEntry(
				com.cannontech.database.db.stars.Substation.LISTNAME_SUBSTATION, liteSite.getSubstationID()).getContent() );
		starsSite.setSubstation( sub );
		
		return starsSite;
	}
	
	public static StarsLMHardware createStarsLMHardware(LiteLMHardwareBase liteHw, int energyCompanyID) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
		StarsLMHardware starsHw = new StarsLMHardware();
		
		starsHw.setInventoryID( liteHw.getInventoryID() );
		starsHw.setCategory( YukonListFuncs.getYukonListEntry(liteHw.getCategoryID()).getEntryText() );
				
		InstallationCompany company = new InstallationCompany();
		company.setEntryID( liteHw.getInstallationCompanyID() );
		company.setContent( YukonListFuncs.getYukonListEntry(liteHw.getInstallationCompanyID()).getEntryText() );
		starsHw.setInstallationCompany( company );
		
		starsHw.setReceiveDate( new Date(liteHw.getReceiveDate()) );
		starsHw.setInstallDate( new Date(liteHw.getInstallDate()) );
		starsHw.setRemoveDate( new Date(liteHw.getRemoveDate()) );
		starsHw.setAltTrackingNumber( forceNotNull(liteHw.getAlternateTrackingNumber()) );
		
		Voltage volt = new Voltage();
		volt.setEntryID( liteHw.getVoltageID() );
		volt.setContent( YukonListFuncs.getYukonListEntry(liteHw.getVoltageID()).getEntryText() );
		starsHw.setVoltage( volt );
		
		starsHw.setNotes( forceNotNull(liteHw.getNotes()) );
		starsHw.setManufactureSerialNumber( forceNotNull(liteHw.getManufactureSerialNumber()) );
		
		LMDeviceType hwType = new LMDeviceType();
		hwType.setEntryID( liteHw.getLmHardwareTypeID() );
		hwType.setContent( YukonListFuncs.getYukonListEntry(liteHw.getLmHardwareTypeID()).getEntryText() );
		starsHw.setLMDeviceType( hwType );
		starsHw.setInstallationNotes( "" );
		
		DeviceStatus hwStatus = new DeviceStatus();
		setStarsCustListEntry( hwStatus, energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL) );
		starsHw.setDeviceStatus( hwStatus );
		
		if (liteHw.getLmHardwareHistory() != null) {
			StarsLMHardwareHistory hwHist = new StarsLMHardwareHistory();
			for (int i = 0; i < liteHw.getLmHardwareHistory().size(); i++) {
				LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) liteHw.getLmHardwareHistory().get(i);
				StarsLMHardwareEvent starsEvent = new StarsLMHardwareEvent();
				setStarsLMCustomerEvent( starsEvent, liteEvent );
				hwHist.addStarsLMHardwareEvent( starsEvent );
			}
			starsHw.setStarsLMHardwareHistory( hwHist );
				
			// set hardware status and installation notes
			for (int i = hwHist.getStarsLMHardwareEventCount() - 1; i >= 0; i--) {
				if (hwHist.getStarsLMHardwareEvent(i).getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_CONFIG) {
					hwStatus = new DeviceStatus();
					setStarsCustListEntry( hwStatus, energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL) );
					starsHw.setDeviceStatus( hwStatus );
				}
				else if (hwHist.getStarsLMHardwareEvent(i).getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED) {
					hwStatus = new DeviceStatus();
					setStarsCustListEntry( hwStatus, energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL) );
					starsHw.setDeviceStatus( hwStatus );
					break;
				}
				else if (hwHist.getStarsLMHardwareEvent(i).getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION) {
					hwStatus = new DeviceStatus();
					setStarsCustListEntry( hwStatus, energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_TEMP_UNAVAIL) );
					starsHw.setDeviceStatus( hwStatus );
					break;
				}
			}
			
			for (int i = 0; i < hwHist.getStarsLMHardwareEventCount(); i++) {
				if (hwHist.getStarsLMHardwareEvent(i).getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL) {
					starsHw.setInstallationNotes( hwHist.getStarsLMHardwareEvent(i).getNotes() );
					break;
				}
			}
		}
		
		return starsHw;
	}
	
	public static StarsServiceRequest createStarsServiceRequest(LiteWorkOrderBase liteOrder, int energyCompanyID) {
		StarsServiceRequest starsOrder = new StarsServiceRequest();
		
		starsOrder.setOrderID( liteOrder.getOrderID() );
		starsOrder.setOrderNumber( forceNotNull(liteOrder.getOrderNumber()) );
		
		ServiceType servType = new ServiceType();
		servType.setEntryID( liteOrder.getWorkTypeID() );
		servType.setContent( YukonListFuncs.getYukonListEntry(liteOrder.getWorkTypeID()).getEntryText() );
		starsOrder.setServiceType( servType );
		
		ServiceCompany company = new ServiceCompany(); 
		company.setEntryID( liteOrder.getServiceCompanyID() );
		company.setContent( SOAPServer.getEnergyCompany(energyCompanyID).getStarsSelectionListEntry(
				com.cannontech.database.db.stars.report.ServiceCompany.LISTNAME_SERVICECOMPANY, liteOrder.getServiceCompanyID()).getContent() );
		starsOrder.setServiceCompany( company );
		
		starsOrder.setDateReported( new Date(liteOrder.getDateReported()) );
		starsOrder.setDateScheduled( new Date(liteOrder.getDateScheduled()) );
		starsOrder.setDateCompleted( new Date(liteOrder.getDateCompleted()) );
		starsOrder.setOrderedBy( forceNotNull(liteOrder.getOrderedBy()) );
		starsOrder.setDescription( forceNotNull(liteOrder.getDescription()) );
		starsOrder.setActionTaken( forceNotNull(liteOrder.getActionTaken()) );
		
		CurrentState status = new CurrentState();
		status.setEntryID( liteOrder.getCurrentStateID() );
		status.setContent( YukonListFuncs.getYukonListEntry(liteOrder.getCurrentStateID()).getEntryText() );
		starsOrder.setCurrentState( status );
		
		return starsOrder;
	}
	
	public static StarsLMControlHistory createStarsLMControlHistory(LiteStarsLMControlHistory liteCtrlHist, StarsCtrlHistPeriod period, boolean getSummary) {
        StarsLMControlHistory starsCtrlHist = new StarsLMControlHistory();
        starsCtrlHist.setBeingControlled( false );
        
        if (period.getType() != StarsCtrlHistPeriod.NONE_TYPE) {
	        int startIndex = 0;
	        if (period.getType() == StarsCtrlHistPeriod.PASTDAY_TYPE)
	        	startIndex = liteCtrlHist.getCurrentDayStartIndex();
	        else if (period.getType() == StarsCtrlHistPeriod.PASTWEEK_TYPE)
	        	startIndex = liteCtrlHist.getCurrentWeekStartIndex();
	        else if (period.getType() == StarsCtrlHistPeriod.PASTMONTH_TYPE)
	        	startIndex = liteCtrlHist.getCurrentMonthStartIndex();
	        else if (period.getType() == StarsCtrlHistPeriod.PASTYEAR_TYPE)
	        	startIndex = liteCtrlHist.getCurrentYearStartIndex();
	        
        	ControlHistory hist = null;
        	long lastStartTime = 0;
	        for (int i = startIndex; i < liteCtrlHist.getLmControlHistory().size(); i++) {
	        	LiteLMControlHistory lmCtrlHist = (LiteLMControlHistory) liteCtrlHist.getLmControlHistory().get(i);

                /*
                 * ActiveRestore is defined as below:
                 * N - This is the first entry for any new control.
                 * C - Previous command was repeated extending the current control interval.
                 * T - Control terminated based on time set in load group.
                 * M - Control terminated because of an active restore or terminate command being sent.
                 * O - Control terminated because a new command of a different nature was sent to this group.
                 */
                if (lmCtrlHist.getActiveRestore().equals("N")) {
                	lastStartTime = lmCtrlHist.getStartDateTime();
                	
                	hist = new ControlHistory();
                	hist.setStartDateTime( new Date(lmCtrlHist.getStartDateTime()) );
		            hist.setControlDuration( (int) lmCtrlHist.getControlDuration() );
                	starsCtrlHist.addControlHistory( hist );
                }
                else if (lmCtrlHist.getActiveRestore().equals("C")) {
                	// If this is the last record, and the time stamp is close to now, then we're being controlled right now
                	if (i == liteCtrlHist.getLmControlHistory().size() - 1) {
                		long ctrlPeriod = (hist == null) ? lmCtrlHist.getControlDuration() : lmCtrlHist.getControlDuration() - hist.getControlDuration();
                		if ((new Date().getTime() - lmCtrlHist.getStartDateTime()) * 0.001 - lmCtrlHist.getControlDuration() < 2 * ctrlPeriod)
                			starsCtrlHist.setBeingControlled( true );
                	}
                		
                	if (Math.abs(lmCtrlHist.getStartDateTime() - lastStartTime) < 1000) {
                		if (hist != null)
                			hist.setControlDuration( (int) lmCtrlHist.getControlDuration() );
                	}
                	else {
                		// This is a new control period
                		lastStartTime = lmCtrlHist.getStartDateTime();
                		
	                	hist = new ControlHistory();
	                	hist.setStartDateTime( new Date(lmCtrlHist.getStartDateTime()) );
			            hist.setControlDuration( (int) lmCtrlHist.getControlDuration() );
	                	starsCtrlHist.addControlHistory( hist );
                	}
                }
	        	else if (lmCtrlHist.getActiveRestore().equals("M") || lmCtrlHist.getActiveRestore().equals("T")) {
	        		if (Math.abs(lmCtrlHist.getStartDateTime() - lastStartTime) < 1000) {
	        			if (hist != null)
				            hist.setControlDuration( (int) lmCtrlHist.getControlDuration() );
	        		}
/*	        		else {
	        			// 'M' and 'T' can be a control period by itself
			            lastStartTime = lmCtrlHist.getStartDateTime();
		        		
			            hist = new ControlHistory();
			            hist.setStartDateTime( new Date(lmCtrlHist.getStartDateTime()) );
			            hist.setControlDuration( (int) lmCtrlHist.getControlDuration() );
			            starsCtrlHist.addControlHistory( hist );
	        		}
*/	        		
		            hist = null;
	        	}
	        }
        }
        
        if (getSummary) {
            ControlSummary summary = new ControlSummary();
            int dailyTime = 0;
            int monthlyTime = 0;
            int seasonalTime = 0;
            int annualTime = 0;
            
            int size = liteCtrlHist.getLmControlHistory().size();
            if (size > 0) {
            	LiteLMControlHistory lastCtrlHist = (LiteLMControlHistory) liteCtrlHist.getLmControlHistory().get(size - 1);
            	seasonalTime = (int) lastCtrlHist.getCurrentSeasonalTime();
            	if (liteCtrlHist.getCurrentYearStartIndex() < size) {
            		annualTime = (int) lastCtrlHist.getCurrentAnnualTime();
            		if (liteCtrlHist.getCurrentMonthStartIndex() < size) {
            			monthlyTime = (int) lastCtrlHist.getCurrentMonthlyTime();
		            	if (liteCtrlHist.getCurrentDayStartIndex() < size)
		            		dailyTime = (int) lastCtrlHist.getCurrentDailyTime();
            		}
            	}
            }
            
            summary.setDailyTime( dailyTime );
            summary.setMonthlyTime( monthlyTime );
            summary.setSeasonalTime( seasonalTime );
            summary.setAnnualTime( annualTime );
            starsCtrlHist.setControlSummary( summary );
        }

        return starsCtrlHist;
	}
	
	public static StarsLMProgram createStarsLMProgram(LiteStarsLMProgram liteProg, LiteStarsAppliance liteApp, int energyCompanyID) {
		StarsLMProgram starsProg = new StarsLMProgram();
		starsProg.setProgramID( liteProg.getLmProgram().getProgramID() );
		starsProg.setGroupID( liteProg.getGroupID() );
		starsProg.setProgramName( forceNotNull(liteProg.getLmProgram().getProgramName()) );
		starsProg.setApplianceCategoryID( liteApp.getApplianceCategoryID() );

		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
		
		LiteStarsLMControlHistory liteCtrlHist = energyCompany.getLMControlHistory( liteProg.getGroupID() );
		if (liteCtrlHist != null)
			starsProg.setStarsLMControlHistory( createStarsLMControlHistory(liteCtrlHist, StarsCtrlHistPeriod.PASTDAY, true) );
		
		if (liteProg.getProgramHistory() != null) {
			StarsLMProgramHistory progHist = new StarsLMProgramHistory();
			for (int k = 0; k < liteProg.getProgramHistory().size(); k++) {
				LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) liteProg.getProgramHistory().get(k);
				StarsLMProgramEvent starsEvent = new StarsLMProgramEvent();
				setStarsLMCustomerEvent( starsEvent, liteEvent );
				progHist.addStarsLMProgramEvent( starsEvent );
			}
			starsProg.setStarsLMProgramHistory( progHist );
			
			if (ServerUtils.isInService(
					liteProg.getProgramHistory(),
					energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION).getEntryID(),
					energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED).getEntryID()
				))
				starsProg.setStatus( ServletUtils.IN_SERVICE );
			else
				starsProg.setStatus( ServletUtils.OUT_OF_SERVICE );
		}
		
		return starsProg;
	}
	
	public static StarsWebConfig createStarsWebConfig(LiteWebConfiguration liteWebConfig) {
		StarsWebConfig starsWebConfig = new StarsWebConfig();
		starsWebConfig.setLogoLocation( forceNotNull(liteWebConfig.getLogoLocation()) );
		starsWebConfig.setDescription( forceNotNull(liteWebConfig.getDescription()) );
		starsWebConfig.setAlternateDisplayName( forceNotNull(liteWebConfig.getAlternateDisplayName()) );
		starsWebConfig.setURL( forceNotNull(liteWebConfig.getUrl()) );
		
		return starsWebConfig;
	}
	
	public static StarsApplianceCategory createStarsApplianceCategory(LiteApplianceCategory liteAppCat, ArrayList liteProgs) {
		StarsApplianceCategory starsAppCat = new StarsApplianceCategory();
		starsAppCat.setApplianceCategoryID( liteAppCat.getApplianceCategoryID() );
		starsAppCat.setCategoryID( liteAppCat.getCategoryID() );
		starsAppCat.setDescription( forceNotNull(liteAppCat.getDescription()) );
		
		ArrayList liteWebConfigs = SOAPServer.getAllWebConfigurations();
		for (int i = 0; i < liteWebConfigs.size(); i++) {
			LiteWebConfiguration liteWebConfig = (LiteWebConfiguration) liteWebConfigs.get(i);
			if (liteWebConfig.getConfigID() == liteAppCat.getWebConfigurationID())
				starsAppCat.setStarsWebConfig( createStarsWebConfig(liteWebConfig) );
		}
		
		if (liteProgs != null) {
			for (int i = 0; i < liteProgs.size(); i++) {
				LiteLMProgram liteProg = (LiteLMProgram) liteProgs.get(i);
				StarsEnrLMProgram starsProg = new StarsEnrLMProgram();
				starsProg.setProgramID( liteProg.getProgramID() );
				starsProg.setProgramName( liteProg.getProgramName() );
				
				for (int j = 0; j < liteWebConfigs.size(); j++) {
					LiteWebConfiguration liteWebConfig = (LiteWebConfiguration) liteWebConfigs.get(j);
					if (liteWebConfig.getConfigID() == liteProg.getWebSettingsID())
						starsProg.setStarsWebConfig( createStarsWebConfig(liteWebConfig) );
				}
				
				starsAppCat.addStarsEnrLMProgram( starsProg );
			}
		}
		
		return starsAppCat;
	}
	
	public static StarsServiceCompany createStarsServiceCompany(LiteServiceCompany liteCompany, int energyCompanyID) {
		StarsServiceCompany starsCompany = new StarsServiceCompany();
		starsCompany.setCompanyID( liteCompany.getCompanyID() );
		starsCompany.setCompanyName( forceNotNull(liteCompany.getCompanyName()) );
		starsCompany.setMainPhoneNumber( forceNotNull(liteCompany.getMainPhoneNumber()) );
		starsCompany.setMainFaxNumber( forceNotNull(liteCompany.getMainFaxNumber()) );
		
		CompanyAddress companyAddr = new CompanyAddress();
		setStarsCustomerAddress( companyAddr, SOAPServer.getEnergyCompany(energyCompanyID).getAddress( liteCompany.getAddressID()) );
		starsCompany.setCompanyAddress( companyAddr );
		
		return starsCompany;
	}
	
	public static StarsUser createStarsUser(com.cannontech.database.data.lite.LiteYukonUser liteUser) {
		StarsUser starsUser = new StarsUser();
		starsUser.setUsername( forceNotNull(liteUser.getUsername()) );
		starsUser.setPassword( forceNotNull(liteUser.getPassword()) );
		
		return starsUser;
	}
	
	public static StarsThermostatManualOption createStarsThermostatManualOption(LiteLMThermostatManualOption liteOpt) {
		StarsThermostatManualOption starsOpt = new StarsThermostatManualOption();
		starsOpt.setTemperature( liteOpt.getPreviousTemperature() );
		starsOpt.setHold( liteOpt.isHoldTemperature() );
		starsOpt.setMode( ServerUtils.getThermModeSetting(liteOpt.getOperationStateID()) );
		starsOpt.setFan( ServerUtils.getThermFanSetting(liteOpt.getFanOperationID()) );
		
		return starsOpt;
	}
	
	public static StarsAppliance createStarsAppliance(LiteStarsAppliance liteApp, int energyCompanyID) {
        StarsAppliance starsApp = new StarsAppliance();
        
        starsApp.setApplianceID( liteApp.getApplianceID() );
        starsApp.setApplianceCategoryID( liteApp.getApplianceCategoryID() );
    	starsApp.setInventoryID( liteApp.getInventoryID() );
    	starsApp.setLmProgramID( liteApp.getLmProgramID() );
        starsApp.setNotes( forceNotNull(liteApp.getNotes()) );
        
        if (liteApp.getYearManufactured() > 0)
        	starsApp.setYearManufactured( String.valueOf(liteApp.getYearManufactured()) );
        else
        	starsApp.setYearManufactured( "" );
       	
       	Manufacturer manu = new Manufacturer();
       	setStarsCustListEntry( manu, YukonListFuncs.getYukonListEntry(liteApp.getManufacturerID()) );
       	starsApp.setManufacturer( manu );
        
        Location loc = new Location();
        setStarsCustListEntry( loc, YukonListFuncs.getYukonListEntry(liteApp.getLocationID()) );
        starsApp.setLocation( loc );
        
        starsApp.setServiceCompany( new ServiceCompany() );
        
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
        LiteApplianceCategory liteAppCat = energyCompany.getApplianceCategory( liteApp.getApplianceCategoryID() );
        if (liteAppCat != null)
	        starsApp.setCategoryName( forceNotNull(liteAppCat.getDescription()) );
	    else
	    	starsApp.setCategoryName( "(Unknown)" );
        
        return starsApp;
	}
	
	public static StarsCustSelectionList createStarsCustSelectionList(YukonSelectionList yukonList) {
		StarsCustSelectionList starsList = new StarsCustSelectionList();
		
		starsList.setListID( yukonList.getListID() );
		starsList.setListName( yukonList.getListName() );
		
		ArrayList entries = yukonList.getYukonListEntries();
		for (int i = 0; i < entries.size(); i++) {
			if (yukonList.getListID() == LiteStarsEnergyCompany.FAKE_LIST_ID)	// substation list or service company list
				starsList.addStarsSelectionListEntry( (StarsSelectionListEntry) entries.get(i) );
			else {
				StarsSelectionListEntry starsEntry = new StarsSelectionListEntry();
				YukonListEntry yukonEntry = (YukonListEntry) entries.get(i);
				setStarsCustListEntry( starsEntry, yukonEntry );
				starsEntry.setYukonDefID( yukonEntry.getYukonDefID() );
				starsList.addStarsSelectionListEntry( starsEntry );
			}
		}
		
		return starsList;
	}
	
	public static StarsCustomerFAQ createStarsCustomerFAQ(LiteCustomerFAQ liteFAQ) {
		StarsCustomerFAQ starsFAQ = new StarsCustomerFAQ();
		starsFAQ.setQuestionID( liteFAQ.getQuestionID() );
		starsFAQ.setQuestion( liteFAQ.getQuestion() );
		starsFAQ.setAnswer( liteFAQ.getAnswer() );
		
		return starsFAQ;
	}
	
	
	public static String forceNotNull(String str) {
		return (str == null) ? "" : str;
	}
	
	
	public static boolean isIdenticalCustomerContact(LiteCustomerContact liteContact, StarsCustomerContact starsContact) {
		return (forceNotNull(liteContact.getLastName()).equals( starsContact.getLastName() )
				&& forceNotNull(liteContact.getFirstName()).equals( starsContact.getFirstName() )
				&& forceNotNull(liteContact.getHomePhone()).equals( starsContact.getHomePhone() )
				&& forceNotNull(liteContact.getWorkPhone()).equals( starsContact.getWorkPhone() )
				&& forceNotNull(liteContact.getEmail()).equals( forceNotNull(starsContact.getEmail()) ));
	}
	
	public static boolean isIdenticalCustomerAddress(LiteAddress liteAddr, StarsCustomerAddress starsAddr) {
		return (forceNotNull(liteAddr.getLocationAddress1()).equals( starsAddr.getStreetAddr1() )
				&& forceNotNull(liteAddr.getLocationAddress2()).equals( starsAddr.getStreetAddr2() )
				&& forceNotNull(liteAddr.getCityName()).equals( starsAddr.getCity() )
				&& forceNotNull(liteAddr.getStateCode()).equals( starsAddr.getState() )
				&& forceNotNull(liteAddr.getZipCode()).equals( starsAddr.getZip() )
				&& forceNotNull(liteAddr.getCounty()).equals( forceNotNull(starsAddr.getCounty()) ));
	}
	
	public static boolean isIdenticalSiteInformation(LiteSiteInformation liteSite, StarsSiteInformation starsSite) {
		return (forceNotNull(liteSite.getFeeder()).equals( starsSite.getFeeder() )
				&& forceNotNull(liteSite.getPole()).equals( starsSite.getPole() )
				&& forceNotNull(liteSite.getTransformerSize()).equals( starsSite.getTransformerSize() )
				&& forceNotNull(liteSite.getServiceVoltage()).equals( starsSite.getServiceVoltage() )
				&& liteSite.getSubstationID() == starsSite.getSubstation().getEntryID());
	}
	
	public static boolean isIdenticalCustomerAccount(LiteCustomerAccount liteAccount, StarsCustAccount starsAccount) {
		return (forceNotNull(liteAccount.getAccountNumber()).equals( starsAccount.getAccountNumber() )
				&& forceNotNull(liteAccount.getAccountNotes()).equals( starsAccount.getAccountNotes() ));
	}
	
	public static boolean isIdenticalCustomer(LiteCustomer liteCustomer, StarsCustAccount starsCustomer) {
		return ((starsCustomer.getIsCommercial() && liteCustomer.getCustomerTypeID() == CustomerTypes.CUSTOMER_CI
					|| !starsCustomer.getIsCommercial() && liteCustomer.getCustomerTypeID() == CustomerTypes.CUSTOMER_RESIDENTIAL)
				&& (starsCustomer.getTimeZone() == null || liteCustomer.getTimeZone().equalsIgnoreCase( starsCustomer.getTimeZone() )));
	}
	
	public static boolean isIdenticalAccountSite(LiteAccountSite liteAcctSite, StarsCustAccount starsAcctSite) {
		return (forceNotNull(liteAcctSite.getSiteNumber()).equals( starsAcctSite.getPropertyNumber() )
				&& forceNotNull(liteAcctSite.getPropertyNotes()).equals( starsAcctSite.getPropertyNotes() ));
	}
	
	public static boolean isIdenticalThermModeSetting(LiteLMThermostatSeason liteSeason, StarsThermostatSeason starsSeason) {
		LiteWebConfiguration liteConfig = SOAPServer.getWebConfiguration( liteSeason.getWebConfigurationID() );
		return ((starsSeason.getMode().getType() == StarsThermoModeSettings.COOL_TYPE) && liteConfig.getAlternateDisplayName().equalsIgnoreCase("Summer")
				|| (starsSeason.getMode().getType() == StarsThermoModeSettings.HEAT_TYPE) && liteConfig.getAlternateDisplayName().equalsIgnoreCase("Winter"));
	}
	
	public static boolean isIdenticalThermDaySetting(LiteLMThermostatSeasonEntry liteEntry, StarsThermostatSchedule starsSched) {
		YukonListEntry entry = YukonListFuncs.getYukonListEntry( liteEntry.getTimeOfWeekID() );
		return ((starsSched.getDay().getType() == StarsThermoDaySettings.WEEKDAY_TYPE) && entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_TOW_WEEKDAY
				|| (starsSched.getDay().getType() == StarsThermoDaySettings.SATURDAY_TYPE) && entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_TOW_SATURDAY
				|| (starsSched.getDay().getType() == StarsThermoDaySettings.SUNDAY_TYPE) && entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_TOW_SUNDAY );
	}
	
	public static boolean isIdenticalThermostatSchedule(LiteLMThermostatSeasonEntry[] liteSched, StarsThermostatSchedule starsSched) {
		if (liteSched.length != 4) return false;
		int time1 = starsSched.getTime1().getHour() * 3600 + starsSched.getTime1().getMinute() * 60 + starsSched.getTime1().getSeconds();
		int time2 = starsSched.getTime2().getHour() * 3600 + starsSched.getTime2().getMinute() * 60 + starsSched.getTime2().getSeconds();
		int time3 = starsSched.getTime3().getHour() * 3600 + starsSched.getTime3().getMinute() * 60 + starsSched.getTime3().getSeconds();
		int time4 = starsSched.getTime4().getHour() * 3600 + starsSched.getTime4().getMinute() * 60 + starsSched.getTime4().getSeconds();
		return ((liteSched[0].getStartTime() == time1) && (liteSched[0].getTemperature() == starsSched.getTemperature1())
				&& (liteSched[1].getStartTime() == time2) && (liteSched[1].getTemperature() == starsSched.getTemperature2())
				&& (liteSched[2].getStartTime() == time3) && (liteSched[2].getTemperature() == starsSched.getTemperature3())
				&& (liteSched[3].getStartTime() == time4) && (liteSched[3].getTemperature() == starsSched.getTemperature4()));
	}
}
