package com.cannontech.database.data.lite.stars;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.macro.GenericMacro;
import com.cannontech.database.db.macro.MacroTypes;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.OptOutEventQueue;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsMsgUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings;

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
			lite = new LiteContact(0);
			setLiteContact( (LiteContact) lite, (com.cannontech.database.data.customer.Contact) db );
		}
		else if (db instanceof com.cannontech.database.db.customer.Address) {
			lite = new LiteAddress();
			setLiteAddress( (LiteAddress) lite, (com.cannontech.database.db.customer.Address) db );
		}
		else if (db instanceof com.cannontech.database.data.stars.hardware.LMHardwareBase) {
			lite = new LiteStarsLMHardware();
			setLiteStarsLMHardware( (LiteStarsLMHardware) lite, (com.cannontech.database.data.stars.hardware.LMHardwareBase) db );
		}
		else if (db instanceof com.cannontech.database.data.stars.event.LMHardwareEvent) {
			lite = new LiteLMHardwareEvent();
			setLiteLMHardwareEvent( (LiteLMHardwareEvent) lite, (com.cannontech.database.data.stars.event.LMHardwareEvent) db );
		}
		else if (db instanceof com.cannontech.database.data.stars.event.LMProgramEvent) {
			lite = new LiteLMProgramEvent();
			setLiteLMProgramEvent( (LiteLMProgramEvent) lite, (com.cannontech.database.data.stars.event.LMProgramEvent) db );
		}
		else if (db instanceof com.cannontech.database.db.stars.report.WorkOrderBase) {
			lite = new LiteWorkOrderBase();
			setLiteWorkOrderBase( (LiteWorkOrderBase) lite, (com.cannontech.database.db.stars.report.WorkOrderBase) db );
		}
		else if (db instanceof com.cannontech.database.db.stars.customer.CustomerAccount) {
			lite = new LiteCustomerAccount();
			setLiteCustomerAccount( (LiteCustomerAccount) lite, (com.cannontech.database.db.stars.customer.CustomerAccount) db );
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
		else if (db instanceof com.cannontech.database.data.stars.event.LMThermostatManualEvent) {
			lite = new LiteLMThermostatManualEvent();
			setLiteLMThermostatManualEvent( (LiteLMThermostatManualEvent) lite, (com.cannontech.database.data.stars.event.LMThermostatManualEvent) db );
		}
		else if (db instanceof com.cannontech.database.data.stars.appliance.ApplianceBase) {
			lite = new LiteStarsAppliance();
			setLiteStarsAppliance( (LiteStarsAppliance) lite, (com.cannontech.database.data.stars.appliance.ApplianceBase) db );
		}
		else if (db instanceof com.cannontech.database.db.stars.customer.CustomerResidence) {
			lite = new LiteCustomerResidence();
			setLiteCustomerResidence( (LiteCustomerResidence) lite, (com.cannontech.database.db.stars.customer.CustomerResidence) db );
		}
		else if (db instanceof com.cannontech.database.db.stars.appliance.ApplianceCategory) {
			lite = new LiteApplianceCategory();
			setLiteApplianceCategory( (LiteApplianceCategory) lite, (com.cannontech.database.db.stars.appliance.ApplianceCategory) db );
		}
		else if (db instanceof com.cannontech.database.db.stars.report.ServiceCompany) {
			lite = new LiteServiceCompany();
			setLiteServiceCompany( (LiteServiceCompany) lite, (com.cannontech.database.db.stars.report.ServiceCompany) db );
		}
		else if (db instanceof com.cannontech.database.db.stars.Substation) {
			lite = new LiteSubstation();
			setLiteSubstation( (LiteSubstation) lite, (com.cannontech.database.db.stars.Substation) db );
		}
		else if (db instanceof com.cannontech.database.db.stars.CustomerFAQ) {
			lite = new LiteCustomerFAQ();
			setLiteCustomerFAQ( (LiteCustomerFAQ) lite, (com.cannontech.database.db.stars.CustomerFAQ) db );
		}
		else if (db instanceof com.cannontech.database.db.stars.InterviewQuestion) {
			lite = new LiteInterviewQuestion();
			setLiteInterviewQuestion( (LiteInterviewQuestion) lite, (com.cannontech.database.db.stars.InterviewQuestion) db );
		}
		else if (db instanceof com.cannontech.database.db.web.YukonWebConfiguration) {
			lite = new LiteWebConfiguration();
			setLiteWebConfiguration( (LiteWebConfiguration) lite, (com.cannontech.database.db.web.YukonWebConfiguration) db );
		}
		else if (db instanceof com.cannontech.database.db.stars.LMProgramWebPublishing) {
			lite = new LiteLMProgramWebPublishing();
			setLiteLMProgramWebPublishing( (LiteLMProgramWebPublishing) lite, (com.cannontech.database.db.stars.LMProgramWebPublishing) db );
		}
		
		return lite;
	}
	
	public static void setLiteContact(LiteContact liteContact, com.cannontech.database.data.customer.Contact contact) {
		liteContact.setContactID( contact.getContact().getContactID().intValue() );
		liteContact.setContLastName( contact.getContact().getContLastName() );
		liteContact.setContFirstName( contact.getContact().getContFirstName() );
		liteContact.setLoginID( contact.getContact().getLogInID().intValue() );
		liteContact.setAddressID( contact.getContact().getAddressID().intValue() );
		
		liteContact.getLiteContactNotifications().removeAllElements();
		
		for (int i = 0; i < contact.getContactNotifVect().size(); i++) {
			com.cannontech.database.db.contact.ContactNotification notif =
					(com.cannontech.database.db.contact.ContactNotification) contact.getContactNotifVect().get(i);
			
			LiteContactNotification liteNotif = new LiteContactNotification(
					notif.getContactNotifID().intValue(), liteContact.getContactID(),
					notif.getNotificationCatID().intValue(), notif.getDisableFlag(), notif.getNotification() );
			liteContact.getLiteContactNotifications().add( liteNotif );
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
	
	public static void setLiteInventoryBase(LiteInventoryBase liteInv, com.cannontech.database.db.stars.hardware.InventoryBase invDB) {
		liteInv.setInventoryID( invDB.getInventoryID().intValue() );
		liteInv.setAccountID( invDB.getAccountID().intValue() );
		liteInv.setCategoryID( invDB.getCategoryID().intValue() );
		liteInv.setInstallationCompanyID( invDB.getInstallationCompanyID().intValue() );
		liteInv.setReceiveDate( invDB.getReceiveDate().getTime() );
		liteInv.setInstallDate( invDB.getInstallDate().getTime() );
		liteInv.setRemoveDate( invDB.getRemoveDate().getTime() );
		liteInv.setAlternateTrackingNumber( invDB.getAlternateTrackingNumber() );
		liteInv.setVoltageID( invDB.getVoltageID().intValue() );
		liteInv.setNotes( invDB.getNotes() );
		liteInv.setDeviceID( invDB.getDeviceID().intValue() );
		liteInv.setDeviceLabel( invDB.getDeviceLabel() );
		
		ArrayList invHist = liteInv.getInventoryHistory();
		invHist.clear();
		
		com.cannontech.database.data.stars.event.LMHardwareEvent[] events =
				com.cannontech.database.data.stars.event.LMHardwareEvent.getAllLMHardwareEvents( new Integer(liteInv.getInventoryID()) );
		for (int i = 0; i < events.length; i++) {
			LiteLMHardwareEvent liteEvent = (LiteLMHardwareEvent) createLite( events[i] );
			invHist.add( liteEvent );
		}
		
		liteInv.updateDeviceStatus();
	}
	
	public static void setLiteStarsLMHardware(LiteStarsLMHardware liteHw, com.cannontech.database.data.stars.hardware.LMHardwareBase hw) {
		setLiteInventoryBase( liteHw, hw.getInventoryBase() );
		
		liteHw.setManufacturerSerialNumber( hw.getLMHardwareBase().getManufacturerSerialNumber() );
		liteHw.setLmHardwareTypeID( hw.getLMHardwareBase().getLMHardwareTypeID().intValue() );
		liteHw.setRouteID( hw.getLMHardwareBase().getRouteID().intValue() );
		liteHw.setConfigurationID( hw.getLMHardwareBase().getConfigurationID().intValue() );
	}
	
	public static void setLiteLMConfiguration(LiteLMConfiguration liteCfg, com.cannontech.database.data.stars.hardware.LMConfigurationBase cfg) {
		liteCfg.setConfigurationID( cfg.getLMConfigurationBase().getConfigurationID().intValue() );
		liteCfg.setColdLoadPickup( cfg.getLMConfigurationBase().getColdLoadPickup() );
		liteCfg.setTamperDetect( cfg.getLMConfigurationBase().getTamperDetect() );
		
		if (cfg.getSA205() != null) {
			LiteLMConfiguration.SA205 sa205 = new LiteLMConfiguration.SA205();
			sa205.setSlot1( cfg.getSA205().getSlot1().intValue() );
			sa205.setSlot2( cfg.getSA205().getSlot2().intValue() );
			sa205.setSlot3( cfg.getSA205().getSlot3().intValue() );
			sa205.setSlot4( cfg.getSA205().getSlot4().intValue() );
			sa205.setSlot5( cfg.getSA205().getSlot5().intValue() );
			sa205.setSlot6( cfg.getSA205().getSlot6().intValue() );
			liteCfg.setSA205( sa205 );
		}
		else if (cfg.getSA305() != null) {
			LiteLMConfiguration.SA305 sa305 = new LiteLMConfiguration.SA305();
			sa305.setUtility( cfg.getSA305().getUtility().intValue() );
			sa305.setGroup( cfg.getSA305().getGroupAddress().intValue() );
			sa305.setDivision( cfg.getSA305().getDivision().intValue() );
			sa305.setSubstation( cfg.getSA305().getSubstation().intValue() );
			sa305.setRateFamily( cfg.getSA305().getRateFamily().intValue() );
			sa305.setRateMember( cfg.getSA305().getRateMember().intValue() );
			sa305.setRateHierarchy( cfg.getSA305().getRateHierarchy().intValue() );
			liteCfg.setSA305( sa305 );
		}
		else if (cfg.getExpressCom() != null) {
			LiteLMConfiguration.ExpressCom xcom = new LiteLMConfiguration.ExpressCom();
			xcom.setServiceProvider( cfg.getExpressCom().getServiceProvider().intValue() );
			xcom.setGEO( cfg.getExpressCom().getGEO().intValue() );
			xcom.setSubstation( cfg.getExpressCom().getSubstation().intValue() );
			xcom.setFeeder( cfg.getExpressCom().getFeeder().intValue() );
			xcom.setZip( cfg.getExpressCom().getZip().intValue() );
			xcom.setUserAddress( cfg.getExpressCom().getUserAddress().intValue() );
			xcom.setProgram( cfg.getExpressCom().getProgram() );
			xcom.setSplinter( cfg.getExpressCom().getSplinter() );
			liteCfg.setExpressCom( xcom );
		}
		else if (cfg.getVersaCom() != null) {
			LiteLMConfiguration.VersaCom vcom = new LiteLMConfiguration.VersaCom();
			vcom.setUtilityID( cfg.getVersaCom().getUtilityID().intValue() );
			vcom.setSection( cfg.getVersaCom().getSection().intValue() );
			vcom.setClassAddress( cfg.getVersaCom().getClassAddress().intValue() );
			vcom.setDivisionAddress( cfg.getVersaCom().getDivisionAddress().intValue() );
			liteCfg.setVersaCom( vcom );
		}
	}
	
	public static void extendLiteInventoryBase(LiteInventoryBase liteInv, LiteStarsEnergyCompany energyCompany) {
		if (liteInv instanceof LiteStarsLMHardware) {
			LiteStarsLMHardware liteHw = (LiteStarsLMHardware) liteInv;
			if (liteHw.isThermostat())
				liteHw.setThermostatSettings( energyCompany.getThermostatSettings(liteHw) );
		}
		
		liteInv.setExtended( true );
	}
	
	public static void setLiteLMCustomerEvent(LiteLMCustomerEvent liteEvent, com.cannontech.database.db.stars.event.LMCustomerEventBase event) {
		liteEvent.setEventID( event.getEventID().intValue() );
		liteEvent.setActionID( event.getActionID().intValue() );
		liteEvent.setEventDateTime( event.getEventDateTime().getTime() );
		liteEvent.setEventTypeID( event.getEventTypeID().intValue() );
		liteEvent.setNotes( event.getNotes() );
	}
	
	public static void setLiteLMHardwareEvent(LiteLMHardwareEvent liteEvent, com.cannontech.database.data.stars.event.LMHardwareEvent event) {
		setLiteLMCustomerEvent( liteEvent, event.getLMCustomerEventBase() );
		liteEvent.setInventoryID( event.getLMHardwareEvent().getInventoryID().intValue() );
	}
	
	public static void setLiteLMProgramEvent(LiteLMProgramEvent liteEvent, com.cannontech.database.data.stars.event.LMProgramEvent event) {
		setLiteLMCustomerEvent( liteEvent, event.getLMCustomerEventBase() );
		liteEvent.setProgramID( event.getLMProgramEvent().getProgramID().intValue() );
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
		//liteAccount.setLoginID( account.getLoginID().intValue() );
	}
	
	public static void setLiteCustomer(LiteCustomer liteCustomer, com.cannontech.database.data.customer.Customer customer) {
		liteCustomer.setCustomerID( customer.getCustomer().getCustomerID().intValue() );
		liteCustomer.setPrimaryContactID( customer.getCustomer().getPrimaryContactID().intValue() );
		liteCustomer.setCustomerTypeID( customer.getCustomer().getCustomerTypeID().intValue() );
		liteCustomer.setTimeZone( customer.getCustomer().getTimeZone() );
		
		int[] contactIDs = customer.getCustomerContactIDs();
		for (int i = 0; i < contactIDs.length; i++)
			liteCustomer.getAdditionalContacts().add( ContactFuncs.getContact(contactIDs[i]) );
	}
	
	public static void setLiteCICustomer(LiteCICustomer liteCI, com.cannontech.database.data.customer.CICustomerBase ci) {
		setLiteCustomer(liteCI, ci);
		liteCI.setMainAddressID( ci.getCiCustomerBase().getMainAddressID().intValue() );
		liteCI.setDemandLevel( ci.getCiCustomerBase().getCustDmdLevel().doubleValue() );
		liteCI.setCurtailAmount( ci.getCiCustomerBase().getCurtailAmount().doubleValue() );
		liteCI.setCompanyName( ci.getCiCustomerBase().getCompanyName() );
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
		liteCtrlHist.setStopDateTime( ctrlHist.getStopDateTime().getTime() );
	}

	public static void setLiteLMThermostatSeason(LiteLMThermostatSeason liteSeason, com.cannontech.database.db.stars.hardware.LMThermostatSeason season) {
		liteSeason.setSeasonID( season.getSeasonID().intValue() );
		liteSeason.setScheduleID( season.getScheduleID().intValue() );
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
	
	public static LiteLMThermostatSeason createLiteLMThermostatSeason(com.cannontech.database.data.stars.hardware.LMThermostatSeason season) {
		LiteLMThermostatSeason liteSeason = new LiteLMThermostatSeason();
		setLiteLMThermostatSeason( liteSeason, season.getLMThermostatSeason() );
		
		ArrayList seasonEntries = season.getLMThermostatSeasonEntries();
		liteSeason.getSeasonEntries().clear();
		for (int i = 0; i < seasonEntries.size(); i++) {
			com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry seasonEntry =
					(com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry) seasonEntries.get(i);
			liteSeason.getSeasonEntries().add( createLite(seasonEntry) );
		}
		
		return liteSeason;
	}
	
	public static void setLiteLMThermostatSchedule(LiteLMThermostatSchedule liteSchedule, com.cannontech.database.db.stars.hardware.LMThermostatSchedule schedule) {
		liteSchedule.setScheduleID( schedule.getScheduleID().intValue() );
		liteSchedule.setScheduleName( schedule.getScheduleName() );
		liteSchedule.setThermostatTypeID( schedule.getThermostatTypeID().intValue() );
		liteSchedule.setAccountID( schedule.getAccountID().intValue() );
		liteSchedule.setInventoryID( schedule.getInventoryID().intValue() );
	}
	
	public static LiteLMThermostatSchedule createLiteLMThermostatSchedule(com.cannontech.database.data.stars.hardware.LMThermostatSchedule schedule) {
		LiteLMThermostatSchedule liteSchedule = new LiteLMThermostatSchedule();
		setLiteLMThermostatSchedule( liteSchedule, schedule.getLmThermostatSchedule() );
		
		for (int i = 0; i < schedule.getThermostatSeasons().size(); i++) {
			com.cannontech.database.data.stars.hardware.LMThermostatSeason season =
					(com.cannontech.database.data.stars.hardware.LMThermostatSeason) schedule.getThermostatSeasons().get(i);
			liteSchedule.getThermostatSeasons().add( createLiteLMThermostatSeason(season) );
		}
		
		return liteSchedule;
	}
	
	public static void setLiteLMThermostatManualEvent(LiteLMThermostatManualEvent liteEvent, com.cannontech.database.data.stars.event.LMThermostatManualEvent event) {
		setLiteLMCustomerEvent(liteEvent, event.getLMCustomerEventBase());
		liteEvent.setInventoryID( event.getLmThermostatManualEvent().getInventoryID().intValue() );
		liteEvent.setPreviousTemperature( event.getLmThermostatManualEvent().getPreviousTemperature().intValue() );
		liteEvent.setHoldTemperature( event.getLmThermostatManualEvent().getHoldTemperature().equalsIgnoreCase("Y") );
		liteEvent.setOperationStateID( event.getLmThermostatManualEvent().getOperationStateID().intValue() );
		liteEvent.setFanOperationID( event.getLmThermostatManualEvent().getFanOperationID().intValue() );
	}
	
	public static void setLiteStarsAppliance(LiteStarsAppliance liteApp, com.cannontech.database.data.stars.appliance.ApplianceBase app) {
		liteApp.setApplianceID( app.getApplianceBase().getApplianceID().intValue() );
		liteApp.setAccountID( app.getApplianceBase().getAccountID().intValue() );
		liteApp.setApplianceCategoryID( app.getApplianceBase().getApplianceCategoryID().intValue() );
		liteApp.setProgramID( app.getApplianceBase().getProgramID().intValue() );
		liteApp.setYearManufactured( app.getApplianceBase().getYearManufactured().intValue() );
		liteApp.setManufacturerID( app.getApplianceBase().getManufacturerID().intValue() );
		liteApp.setLocationID( app.getApplianceBase().getLocationID().intValue() );
		liteApp.setNotes( app.getApplianceBase().getNotes() );
		liteApp.setModelNumber( app.getApplianceBase().getModelNumber() );
		liteApp.setKWCapacity( app.getApplianceBase().getKWCapacity().intValue() );
		liteApp.setEfficiencyRating( app.getApplianceBase().getEfficiencyRating().intValue() );
		
		if (app.getLMHardwareConfig().getInventoryID() != null) {
			liteApp.setInventoryID( app.getLMHardwareConfig().getInventoryID().intValue() );
			liteApp.setAddressingGroupID( app.getLMHardwareConfig().getAddressingGroupID().intValue() );
			liteApp.setLoadNumber( app.getLMHardwareConfig().getLoadNumber().intValue() );
		}
	}
	
	public static void setLiteAppAirConditioner(LiteStarsAppliance.AirConditioner liteAppAC, com.cannontech.database.db.stars.appliance.ApplianceAirConditioner appAC) {
		liteAppAC.setTonnageID( appAC.getTonnageID().intValue() );
		liteAppAC.setTypeID( appAC.getTypeID().intValue() );
	}
	
	public static void setLiteAppWaterHeater(LiteStarsAppliance.WaterHeater liteAppWH, com.cannontech.database.db.stars.appliance.ApplianceWaterHeater appWH) {
		liteAppWH.setNumberOfGallonsID( appWH.getNumberOfGallonsID().intValue() );
		liteAppWH.setEnergySourceID( appWH.getEnergySourceID().intValue() );
		liteAppWH.setNumberOfElements( appWH.getNumberOfElements().intValue() );
	}
	
	public static void setLiteAppDualFuel(LiteStarsAppliance.DualFuel liteAppDF, com.cannontech.database.db.stars.appliance.ApplianceDualFuel appDF) {
		liteAppDF.setSwitchOverTypeID( appDF.getSwitchOverTypeID().intValue() );
		liteAppDF.setSecondaryKWCapacity( appDF.getSecondaryKWCapacity().intValue() );
		liteAppDF.setSecondaryEnergySourceID( appDF.getSecondaryEnergySourceID().intValue() );
	}
	
	public static void setLiteAppGenerator(LiteStarsAppliance.Generator liteAppGen, com.cannontech.database.db.stars.appliance.ApplianceGenerator appGen) {
		liteAppGen.setTransferSwitchTypeID( appGen.getTransferSwitchTypeID().intValue() );
		liteAppGen.setTransferSwitchMfgID( appGen.getTransferSwitchMfgID().intValue() );
		liteAppGen.setPeakKWCapacity( appGen.getPeakKWCapacity().intValue() );
		liteAppGen.setFuelCapGallons( appGen.getFuelCapGallons().intValue() );
		liteAppGen.setStartDelaySeconds( appGen.getStartDelaySeconds().intValue() );
	}
	
	public static void setLiteAppGrainDryer(LiteStarsAppliance.GrainDryer liteAppGD, com.cannontech.database.db.stars.appliance.ApplianceGrainDryer appGD) {
		liteAppGD.setDryerTypeID( appGD.getDryerTypeID().intValue() );
		liteAppGD.setBinSizeID( appGD.getBinSizeID().intValue() );
		liteAppGD.setBlowerEnergySourceID( appGD.getBlowerEnergySourceID().intValue() );
		liteAppGD.setBlowerHorsePowerID( appGD.getBlowerHorsePowerID().intValue() );
		liteAppGD.setBlowerHeatSourceID( appGD.getBlowerHeatSourceID().intValue() );
	}
	
	public static void setLiteAppStorageHeat(LiteStarsAppliance.StorageHeat liteAppSH, com.cannontech.database.db.stars.appliance.ApplianceStorageHeat appSH) {
		liteAppSH.setStorageTypeID( appSH.getStorageTypeID().intValue() );
		liteAppSH.setPeakKWCapacity( appSH.getPeakKWCapacity().intValue() );
		liteAppSH.setHoursToRecharge( appSH.getHoursToRecharge().intValue() );
	}
	
	public static void setLiteAppHeatPump(LiteStarsAppliance.HeatPump liteAppHP, com.cannontech.database.db.stars.appliance.ApplianceHeatPump appHP) {
		liteAppHP.setPumpTypeID( appHP.getPumpTypeID().intValue() );
		liteAppHP.setPumpSizeID( appHP.getPumpSizeID().intValue() );
		liteAppHP.setStandbySourceID( appHP.getStandbySourceID().intValue() );
		liteAppHP.setSecondsDelayToRestart( appHP.getSecondsDelayToRestart().intValue() );
	}
	
	public static void setLiteAppIrrigation(LiteStarsAppliance.Irrigation liteAppIrr, com.cannontech.database.db.stars.appliance.ApplianceIrrigation appIrr) {
		liteAppIrr.setIrrigationTypeID( appIrr.getIrrigationTypeID().intValue() );
		liteAppIrr.setHorsePowerID( appIrr.getHorsePowerID().intValue() );
		liteAppIrr.setEnergySourceID( appIrr.getEnergySourceID().intValue() );
		liteAppIrr.setSoilTypeID( appIrr.getSoilTypeID().intValue() );
		liteAppIrr.setMeterLocationID( appIrr.getMeterLocationID().intValue() );
		liteAppIrr.setMeterVoltageID( appIrr.getMeterVoltageID().intValue() );
	}
	
	public static void setLiteCustomerResidence(LiteCustomerResidence liteRes, com.cannontech.database.db.stars.customer.CustomerResidence res) {
		liteRes.setAccountSiteID( res.getAccountSiteID().intValue() );
		liteRes.setResidenceTypeID( res.getResidenceTypeID().intValue() );
		liteRes.setConstructionMaterialID( res.getConstructionMaterialID().intValue() );
		liteRes.setDecadeBuiltID( res.getDecadeBuiltID().intValue() );
		liteRes.setSquareFeetID( res.getSquareFeetID().intValue() );
		liteRes.setInsulationDepthID( res.getInsulationDepthID().intValue() );
		liteRes.setGeneralConditionID( res.getGeneralConditionID().intValue() );
		liteRes.setMainCoolingSystemID( res.getMainCoolingSystemID().intValue() );
		liteRes.setMainHeatingSystemID( res.getMainHeatingSystemID().intValue() );
		liteRes.setNumberOfOccupantsID( res.getNumberOfOccupantsID().intValue() );
		liteRes.setOwnershipTypeID( res.getOwnershipTypeID().intValue() );
		liteRes.setMainFuelTypeID( res.getMainFuelTypeID().intValue() );
		liteRes.setNotes( res.getNotes() );
	}
	
	public static LiteStarsAppliance createLiteStarsAppliance(com.cannontech.database.data.stars.appliance.ApplianceBase appliance, LiteStarsEnergyCompany energyCompany) {
		LiteStarsAppliance liteApp = new LiteStarsAppliance();
		setLiteStarsAppliance( liteApp, appliance );
		
		LiteApplianceCategory liteAppCat = energyCompany.getApplianceCategory( appliance.getApplianceBase().getApplianceCategoryID().intValue() );
		if (liteAppCat.getCategoryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_AIR_CONDITIONER).getEntryID()) {
			com.cannontech.database.db.stars.appliance.ApplianceAirConditioner app =
					com.cannontech.database.db.stars.appliance.ApplianceAirConditioner.getApplianceAirConditioner( appliance.getApplianceBase().getApplianceID() );
			if (app != null) {
				liteApp.setAirConditioner( new LiteStarsAppliance.AirConditioner() );
				StarsLiteFactory.setLiteAppAirConditioner( liteApp.getAirConditioner(), app );
			}
		}
		else if (liteAppCat.getCategoryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER).getEntryID()) {
			com.cannontech.database.db.stars.appliance.ApplianceWaterHeater app =
					com.cannontech.database.db.stars.appliance.ApplianceWaterHeater.getApplianceWaterHeater( appliance.getApplianceBase().getApplianceID() );
			if (app != null) {
				liteApp.setWaterHeater( new LiteStarsAppliance.WaterHeater() );
				StarsLiteFactory.setLiteAppWaterHeater( liteApp.getWaterHeater(), app );
			}
		}
		else if (liteAppCat.getCategoryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL).getEntryID()) {
			com.cannontech.database.db.stars.appliance.ApplianceDualFuel app =
					com.cannontech.database.db.stars.appliance.ApplianceDualFuel.getApplianceDualFuel( appliance.getApplianceBase().getApplianceID() );
			if (app != null) {
				liteApp.setDualFuel( new LiteStarsAppliance.DualFuel() );
				StarsLiteFactory.setLiteAppDualFuel( liteApp.getDualFuel(), app );
			}
		}
		else if (liteAppCat.getCategoryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER).getEntryID()) {
			com.cannontech.database.db.stars.appliance.ApplianceGenerator app =
					com.cannontech.database.db.stars.appliance.ApplianceGenerator.getApplianceGenerator( appliance.getApplianceBase().getApplianceID() );
			if (app != null) {
				liteApp.setGenerator( new LiteStarsAppliance.Generator() );
				StarsLiteFactory.setLiteAppGenerator( liteApp.getGenerator(), app );
			}
		}
		else if (liteAppCat.getCategoryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GRAIN_DRYER).getEntryID()) {
			com.cannontech.database.db.stars.appliance.ApplianceGrainDryer app =
					com.cannontech.database.db.stars.appliance.ApplianceGrainDryer.getApplianceGrainDryer( appliance.getApplianceBase().getApplianceID() );
			if (app != null) {
				liteApp.setGrainDryer( new LiteStarsAppliance.GrainDryer() );
				StarsLiteFactory.setLiteAppGrainDryer( liteApp.getGrainDryer(), app );
			}
		}
		else if (liteAppCat.getCategoryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT).getEntryID()) {
			com.cannontech.database.db.stars.appliance.ApplianceStorageHeat app =
					com.cannontech.database.db.stars.appliance.ApplianceStorageHeat.getApplianceStorageHeat( appliance.getApplianceBase().getApplianceID() );
			if (app != null) {
				liteApp.setStorageHeat( new LiteStarsAppliance.StorageHeat() );
				StarsLiteFactory.setLiteAppStorageHeat( liteApp.getStorageHeat(), app );
			}
		}
		else if (liteAppCat.getCategoryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_HEAT_PUMP).getEntryID()) {
			com.cannontech.database.db.stars.appliance.ApplianceHeatPump app =
					com.cannontech.database.db.stars.appliance.ApplianceHeatPump.getApplianceHeatPump( appliance.getApplianceBase().getApplianceID() );
			if (app != null) {
				liteApp.setHeatPump( new LiteStarsAppliance.HeatPump() );
				StarsLiteFactory.setLiteAppHeatPump( liteApp.getHeatPump(), app );
			}
		}
		else if (liteAppCat.getCategoryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_IRRIGATION).getEntryID()) {
			com.cannontech.database.db.stars.appliance.ApplianceIrrigation app =
					com.cannontech.database.db.stars.appliance.ApplianceIrrigation.getApplianceIrrigation( appliance.getApplianceBase().getApplianceID() );
			if (app != null) {
				liteApp.setIrrigation( new LiteStarsAppliance.Irrigation() );
				StarsLiteFactory.setLiteAppIrrigation( liteApp.getIrrigation(), app );
			}
		}
        
		return liteApp;
	}
	
	public static void setLiteApplianceCategory(LiteApplianceCategory liteAppCat, com.cannontech.database.db.stars.appliance.ApplianceCategory appCat) {
		liteAppCat.setApplianceCategoryID( appCat.getApplianceCategoryID().intValue() );
		liteAppCat.setCategoryID( appCat.getCategoryID().intValue() );
		liteAppCat.setDescription( appCat.getDescription() );
		liteAppCat.setWebConfigurationID( appCat.getWebConfigurationID().intValue() );
	}
	
	public static void setLiteServiceCompany(LiteServiceCompany liteCompany, com.cannontech.database.db.stars.report.ServiceCompany company) {
		liteCompany.setCompanyID( company.getCompanyID().intValue() );
		liteCompany.setCompanyName( company.getCompanyName() );
		liteCompany.setAddressID( company.getAddressID().intValue() );
		liteCompany.setMainPhoneNumber( company.getMainPhoneNumber() );
		liteCompany.setMainFaxNumber( company.getMainFaxNumber() );
		liteCompany.setPrimaryContactID( company.getPrimaryContactID().intValue() );
		liteCompany.setHiType( company.getHIType() );
	}
	
	public static void setLiteSubstation(LiteSubstation liteSub, com.cannontech.database.db.stars.Substation sub) {
		liteSub.setSubstationID( sub.getSubstationID().intValue() );
		liteSub.setSubstationName( sub.getSubstationName() );
		liteSub.setRouteID( sub.getRouteID().intValue() );
	}
	
	public static void setLiteCustomerFAQ(LiteCustomerFAQ liteFAQ, com.cannontech.database.db.stars.CustomerFAQ faq) {
		liteFAQ.setQuestionID( faq.getQuestionID().intValue() );
		liteFAQ.setSubjectID( faq.getSubjectID().intValue() );
		liteFAQ.setQuestion( faq.getQuestion() );
		liteFAQ.setAnswer( faq.getAnswer() );
	}
	
	public static void setLiteInterviewQuestion(LiteInterviewQuestion liteQuestion, com.cannontech.database.db.stars.InterviewQuestion question) {
		liteQuestion.setQuestionID( question.getQuestionID().intValue() );
		liteQuestion.setQuestionType( question.getQuestionType().intValue() );
		liteQuestion.setQuestion( question.getQuestion() );
		liteQuestion.setMandatory( question.getMandatory() );
		liteQuestion.setDisplayOrder( question.getDisplayOrder().intValue() );
		liteQuestion.setAnswerType( question.getAnswerType().intValue() );
		liteQuestion.setExpectedAnswer( question.getExpectedAnswer().intValue() );
	}
	
	public static void setLiteWebConfiguration(LiteWebConfiguration liteConfig, com.cannontech.database.db.web.YukonWebConfiguration config) {
		liteConfig.setConfigID(config.getConfigurationID().intValue() );
		liteConfig.setLogoLocation( config.getLogoLocation() );
		liteConfig.setDescription( config.getDescription() );
		liteConfig.setAlternateDisplayName( config.getAlternateDisplayName() );
		liteConfig.setUrl( config.getURL() );
	}
	
	public static void setLiteLMProgramWebPublishing(LiteLMProgramWebPublishing liteProg, com.cannontech.database.db.stars.LMProgramWebPublishing pubProg) {
		liteProg.setProgramID( pubProg.getProgramID().intValue() );
		liteProg.setApplianceCategoryID( pubProg.getApplianceCategoryID().intValue() );
		liteProg.setDeviceID( pubProg.getDeviceID().intValue() );
		liteProg.setWebSettingsID( pubProg.getWebSettingsID().intValue() );
		liteProg.setChanceOfControlID( pubProg.getChanceOfControlID().intValue() );
		liteProg.setProgramOrder( pubProg.getProgramOrder().intValue() );
		
		if (pubProg.getDeviceID().intValue() > 0) {
			try {
				com.cannontech.database.db.device.lm.LMProgramDirectGroup[] groups =
						com.cannontech.database.db.device.lm.LMProgramDirectGroup.getAllDirectGroups( pubProg.getDeviceID() );
				int[] groupIDs = new int[ groups.length ];
				for (int k = 0; k < groups.length; k++)
					groupIDs[k] = groups[k].getLmGroupDeviceID().intValue();
				liteProg.setGroupIDs( groupIDs );
			}
			catch (java.sql.SQLException e) {
				CTILogger.error( e.getMessage(), e );
			}
		}
		else
			liteProg.setGroupIDs( new int[0] );
	}
	
	
	public static DBPersistent createDBPersistent(LiteBase lite) {
		DBPersistent db = null;
		
		switch (lite.getLiteType()) {
			case LiteTypes.CONTACT:
				db = new com.cannontech.database.data.customer.Contact();
				setContact( (com.cannontech.database.data.customer.Contact) db, (LiteContact) lite );
				break;
			case LiteTypes.STARS_ADDRESS:
				db = new com.cannontech.database.db.customer.Address();
				setAddress( (com.cannontech.database.db.customer.Address) db, (LiteAddress) lite );
				break;
			case LiteTypes.STARS_CUSTOMER_ACCOUNT:
				db = new com.cannontech.database.db.stars.customer.CustomerAccount();
				setCustomerAccount( (com.cannontech.database.db.stars.customer.CustomerAccount) db, (LiteCustomerAccount) lite );
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
			case LiteTypes.STARS_LMTHERMOSTAT_MANUAL_EVENT:
				db = new com.cannontech.database.data.stars.event.LMThermostatManualEvent();
				setLMThermostatManualEvent( (com.cannontech.database.data.stars.event.LMThermostatManualEvent) db, (LiteLMThermostatManualEvent) lite );
				break;
			case LiteTypes.STARS_LMHARDWARE:
				db = new com.cannontech.database.data.stars.hardware.LMHardwareBase();
				setLMHardwareBase( (com.cannontech.database.data.stars.hardware.LMHardwareBase) db, (LiteStarsLMHardware) lite );
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
			case LiteTypes.STARS_APPLIANCE:
				db = new com.cannontech.database.data.stars.appliance.ApplianceBase();
				setApplianceBase( (com.cannontech.database.data.stars.appliance.ApplianceBase) db, (LiteStarsAppliance) lite );
				break;
			case LiteTypes.STARS_WORK_ORDER_BASE:
				db = new com.cannontech.database.db.stars.report.WorkOrderBase();
				setWorkOrderBase( (com.cannontech.database.db.stars.report.WorkOrderBase) db, (LiteWorkOrderBase) lite );
				break;
			case LiteTypes.ENERGY_COMPANY:
				db = new com.cannontech.database.db.company.EnergyCompany();
				setEnergyCompany( (com.cannontech.database.db.company.EnergyCompany) db, (LiteStarsEnergyCompany) lite );
				break;
			case LiteTypes.STARS_APPLIANCE_CATEGORY:
				db = new com.cannontech.database.db.stars.appliance.ApplianceCategory();
				setApplianceCategory( (com.cannontech.database.db.stars.appliance.ApplianceCategory) db, (LiteApplianceCategory) lite );
				break;
			case LiteTypes.STARS_SERVICE_COMPANY:
				db = new com.cannontech.database.db.stars.report.ServiceCompany();
				setServiceCompany( (com.cannontech.database.db.stars.report.ServiceCompany) db, (LiteServiceCompany) lite );
				break;
			case LiteTypes.STARS_SUBSTATION:
				db = new com.cannontech.database.db.stars.Substation();
				setSubstation( (com.cannontech.database.db.stars.Substation) db, (LiteSubstation) lite );
		}
		
		return db;
	}
	
	public static void setContact(com.cannontech.database.data.customer.Contact contact, LiteContact liteContact) {
		contact.getContact().setContactID( new Integer(liteContact.getContactID()) );
		contact.getContact().setContLastName( liteContact.getContLastName() );
		contact.getContact().setContFirstName( liteContact.getContFirstName() );
		contact.getContact().setLogInID( new Integer(liteContact.getLoginID()) );
		contact.getContact().setAddressID( new Integer(liteContact.getAddressID()) );
		
		contact.getContactNotifVect().removeAllElements();
		for (int i = 0; i < liteContact.getLiteContactNotifications().size(); i++) {
			LiteContactNotification liteNotif = (LiteContactNotification)
					liteContact.getLiteContactNotifications().get(i);
			
			com.cannontech.database.db.contact.ContactNotification notif =
					new com.cannontech.database.db.contact.ContactNotification();
			notif.setContactNotifID( new Integer(liteNotif.getContactNotifID()) );
			notif.setContactID( new Integer(liteNotif.getContactID()) );
			notif.setNotificationCatID( new Integer(liteNotif.getNotificationCategoryID()) );
			notif.setDisableFlag( liteNotif.getDisableFlag() );
			notif.setNotification( liteNotif.getNotification() );
			
			contact.getContactNotifVect().add( notif );
		}
	}
	
	public static void setContact(com.cannontech.database.data.customer.Contact contact, LiteContact liteContact, LiteStarsEnergyCompany energyCompany) {
		setContact( contact, liteContact );
		if (liteContact.getAddressID() > 0)
			setAddress( contact.getAddress(), energyCompany.getAddress(liteContact.getAddressID()) );
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
		//account.setLoginID( new Integer(liteAccount.getLoginID()) );
	}
	
	public static void setCustomer(com.cannontech.database.db.customer.Customer customer, LiteCustomer liteCustomer) {
		customer.setCustomerID( new Integer(liteCustomer.getCustomerID()) );
		customer.setPrimaryContactID( new Integer(liteCustomer.getPrimaryContactID()) );
		customer.setCustomerTypeID( new Integer(liteCustomer.getCustomerTypeID()) );
		customer.setTimeZone( liteCustomer.getTimeZone() );
	}
	
	public static void setCICustomerBase(com.cannontech.database.data.customer.CICustomerBase ci, LiteCICustomer liteCI) {
		setCustomer( ci.getCustomer(), liteCI );
		ci.setCustomerID( ci.getCustomer().getCustomerID() );
		ci.getCiCustomerBase().setMainAddressID( new Integer(liteCI.getMainAddressID()) );
		ci.getCiCustomerBase().setCustDmdLevel( new Double(liteCI.getDemandLevel()) );
		ci.getCiCustomerBase().setCurtailAmount( new Double(liteCI.getCurtailAmount()) );
		ci.getCiCustomerBase().setCompanyName( liteCI.getCompanyName() );
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
	
	public static void setInventoryBase(com.cannontech.database.db.stars.hardware.InventoryBase invDB, LiteInventoryBase liteInv) {
		invDB.setInventoryID( new Integer(liteInv.getInventoryID()) );
		invDB.setAccountID( new Integer(liteInv.getAccountID()) );
		invDB.setCategoryID( new Integer(liteInv.getCategoryID()) );
		invDB.setInstallationCompanyID( new Integer(liteInv.getInstallationCompanyID()) );
		invDB.setReceiveDate( new Date(liteInv.getReceiveDate()) );
		invDB.setInstallDate( new Date(liteInv.getInstallDate()) );
		invDB.setRemoveDate( new Date(liteInv.getRemoveDate()) );
		invDB.setAlternateTrackingNumber( liteInv.getAlternateTrackingNumber() );
		invDB.setVoltageID( new Integer(liteInv.getVoltageID()) );
		invDB.setNotes( liteInv.getNotes() );
		invDB.setDeviceID( new Integer(liteInv.getDeviceID()) );
		invDB.setDeviceLabel( liteInv.getDeviceLabel() );
	}
	
	public static void setLMHardwareBase(com.cannontech.database.data.stars.hardware.LMHardwareBase hw, LiteStarsLMHardware liteHw) {
		setInventoryBase( hw.getInventoryBase(), liteHw );
		
		hw.setInventoryID( hw.getInventoryBase().getInventoryID() );
		hw.getLMHardwareBase().setManufacturerSerialNumber( liteHw.getManufacturerSerialNumber() );
		hw.getLMHardwareBase().setLMHardwareTypeID( new Integer(liteHw.getLmHardwareTypeID()) );
		hw.getLMHardwareBase().setRouteID( new Integer(liteHw.getRouteID()) );
		hw.getLMHardwareBase().setConfigurationID( new Integer(liteHw.getConfigurationID()) );
	}
	
	public static void setYukonUser(com.cannontech.database.db.user.YukonUser user, com.cannontech.database.data.lite.LiteYukonUser liteUser) {
		user.setUserID( new Integer(liteUser.getUserID()) );
		user.setUsername( liteUser.getUsername() );
		user.setPassword( liteUser.getPassword() );
		user.setStatus( liteUser.getStatus() );
	}
	
	public static void setLMThermostatSeason(com.cannontech.database.db.stars.hardware.LMThermostatSeason season, LiteLMThermostatSeason liteSeason) {
		season.setSeasonID( new Integer(liteSeason.getSeasonID()) );
		season.setScheduleID( new Integer(liteSeason.getScheduleID()) );
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
	
	public static void setLMThermostatSchedule(com.cannontech.database.db.stars.hardware.LMThermostatSchedule schedule, LiteLMThermostatSchedule liteSched) {
		schedule.setScheduleID( new Integer(liteSched.getScheduleID()) );
		schedule.setScheduleName( liteSched.getScheduleName() );
		schedule.setThermostatTypeID( new Integer(liteSched.getThermostatTypeID()) );
		schedule.setAccountID( new Integer(liteSched.getAccountID()) );
		schedule.setInventoryID( new Integer(liteSched.getInventoryID()) );
	}
	
	public static com.cannontech.database.data.stars.hardware.LMThermostatSeason createLMThermostatSeason(LiteLMThermostatSeason liteSeason) {
		com.cannontech.database.data.stars.hardware.LMThermostatSeason season =
				new com.cannontech.database.data.stars.hardware.LMThermostatSeason();
		setLMThermostatSeason( season.getLMThermostatSeason(), liteSeason );
		
		for (int j = 0; j < liteSeason.getSeasonEntries().size(); j++) {
			LiteLMThermostatSeasonEntry liteEntry = (LiteLMThermostatSeasonEntry) liteSeason.getSeasonEntries().get(j);
			
			com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry entry =
					new com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry();
			setLMThermostatSeasonEntry( entry, liteEntry );
			
			season.getLMThermostatSeasonEntries().add( entry );
		}
		
		return season;
	}
	
	public static com.cannontech.database.data.stars.hardware.LMThermostatSchedule createLMThermostatSchedule(LiteLMThermostatSchedule liteSched) {
		com.cannontech.database.data.stars.hardware.LMThermostatSchedule schedule =
				new com.cannontech.database.data.stars.hardware.LMThermostatSchedule();
		setLMThermostatSchedule( schedule.getLmThermostatSchedule(), liteSched );
		
		for (int i = 0; i < liteSched.getThermostatSeasons().size(); i++) {
			LiteLMThermostatSeason liteSeason = (LiteLMThermostatSeason) liteSched.getThermostatSeasons().get(i);
			schedule.getThermostatSeasons().add( createLMThermostatSeason(liteSeason) );
		}
		
		return schedule;
	}
	
	public static void setLMThermostatManualEvent(com.cannontech.database.data.stars.event.LMThermostatManualEvent event, LiteLMThermostatManualEvent liteEvent) {
		setLMCustomerEventBase( event, liteEvent );
		event.getLmThermostatManualEvent().setInventoryID( new Integer(liteEvent.getInventoryID()) );
		event.getLmThermostatManualEvent().setPreviousTemperature( new Integer(liteEvent.getPreviousTemperature()) );
		event.getLmThermostatManualEvent().setHoldTemperature( liteEvent.isHoldTemperature() ? "Y" : "N" );
		event.getLmThermostatManualEvent().setOperationStateID( new Integer(liteEvent.getOperationStateID()) );
		event.getLmThermostatManualEvent().setFanOperationID( new Integer(liteEvent.getFanOperationID()) );
	}
	
	public static void setApplianceBase(com.cannontech.database.data.stars.appliance.ApplianceBase app, LiteStarsAppliance liteApp) {
		app.setApplianceID( new Integer(liteApp.getApplianceID()) );
		app.getApplianceBase().setAccountID( new Integer(liteApp.getAccountID()) );
		app.getApplianceBase().setApplianceCategoryID( new Integer(liteApp.getApplianceCategoryID()) );
		app.getApplianceBase().setProgramID( new Integer(liteApp.getProgramID()) );
		app.getApplianceBase().setYearManufactured( new Integer(liteApp.getYearManufactured()) );
		app.getApplianceBase().setManufacturerID( new Integer(liteApp.getManufacturerID()) );
		app.getApplianceBase().setLocationID( new Integer(liteApp.getLocationID()) );
		app.getApplianceBase().setNotes( liteApp.getNotes() );
		app.getApplianceBase().setModelNumber( liteApp.getModelNumber() );
		app.getApplianceBase().setKWCapacity( new Integer(liteApp.getKWCapacity()) );
		app.getApplianceBase().setEfficiencyRating( new Integer(liteApp.getEfficiencyRating()) );
		
		if (liteApp.getInventoryID() != CtiUtilities.NONE_ID) {
			app.getLMHardwareConfig().setApplianceID( app.getApplianceBase().getApplianceID() );
			app.getLMHardwareConfig().setInventoryID( new Integer(liteApp.getInventoryID()) );
			app.getLMHardwareConfig().setAddressingGroupID( new Integer(liteApp.getAddressingGroupID()) );
			app.getLMHardwareConfig().setLoadNumber( new Integer(liteApp.getLoadNumber()) );
		}
	}
	
	public static void setApplianceAirConditioner(com.cannontech.database.db.stars.appliance.ApplianceAirConditioner app, LiteStarsAppliance liteApp) {
		app.setApplianceID( new Integer(liteApp.getApplianceID()) );
		app.setTonnageID( new Integer(liteApp.getAirConditioner().getTonnageID()) );
		app.setTypeID( new Integer(liteApp.getAirConditioner().getTypeID()) );
	}
	
	public static void setApplianceWaterHeater(com.cannontech.database.db.stars.appliance.ApplianceWaterHeater app, LiteStarsAppliance liteApp) {
		app.setApplianceID( new Integer(liteApp.getApplianceID()) );
		app.setNumberOfGallonsID( new Integer(liteApp.getWaterHeater().getNumberOfGallonsID()) );
		app.setEnergySourceID( new Integer(liteApp.getWaterHeater().getNumberOfGallonsID()) );
		app.setNumberOfElements( new Integer(liteApp.getWaterHeater().getNumberOfElements()) );
	}
	
	public static void setApplianceDualFuel(com.cannontech.database.db.stars.appliance.ApplianceDualFuel app, LiteStarsAppliance liteApp) {
		app.setApplianceID( new Integer(liteApp.getApplianceID()) );
		app.setSwitchOverTypeID( new Integer(liteApp.getDualFuel().getSwitchOverTypeID()) );
		app.setSecondaryEnergySourceID( new Integer(liteApp.getDualFuel().getSecondaryEnergySourceID()) );
		app.setSecondaryKWCapacity( new Integer(liteApp.getDualFuel().getSecondaryKWCapacity()) );
	}
	
	public static void setApplianceGenerator(com.cannontech.database.db.stars.appliance.ApplianceGenerator app, LiteStarsAppliance liteApp) {
		app.setApplianceID( new Integer(liteApp.getApplianceID()) );
		app.setTransferSwitchTypeID( new Integer(liteApp.getGenerator().getTransferSwitchTypeID()) );
		app.setTransferSwitchMfgID( new Integer(liteApp.getGenerator().getTransferSwitchMfgID()) );
		app.setPeakKWCapacity( new Integer(liteApp.getGenerator().getPeakKWCapacity()) );
		app.setFuelCapGallons( new Integer(liteApp.getGenerator().getFuelCapGallons()) );
		app.setStartDelaySeconds( new Integer(liteApp.getGenerator().getStartDelaySeconds()) );
	}
	
	public static void setApplianceGrainDryer(com.cannontech.database.db.stars.appliance.ApplianceGrainDryer app, LiteStarsAppliance liteApp) {
		app.setApplianceID( new Integer(liteApp.getApplianceID()) );
		app.setDryerTypeID( new Integer(liteApp.getGrainDryer().getDryerTypeID()) );
		app.setBinSizeID( new Integer(liteApp.getGrainDryer().getBinSizeID()) );
		app.setBlowerEnergySourceID( new Integer(liteApp.getGrainDryer().getBlowerEnergySourceID()) );
		app.setBlowerHorsePowerID( new Integer(liteApp.getGrainDryer().getBlowerHorsePowerID()) );
		app.setBlowerHeatSourceID( new Integer(liteApp.getGrainDryer().getBlowerHeatSourceID()) );
	}
	
	public static void setApplianceStorageHeat(com.cannontech.database.db.stars.appliance.ApplianceStorageHeat app, LiteStarsAppliance liteApp) {
		app.setApplianceID( new Integer(liteApp.getApplianceID()) );
		app.setStorageTypeID( new Integer(liteApp.getStorageHeat().getStorageTypeID()) );
		app.setPeakKWCapacity( new Integer(liteApp.getStorageHeat().getPeakKWCapacity()) );
		app.setHoursToRecharge( new Integer(liteApp.getStorageHeat().getHoursToRecharge()) );
	}
	
	public static void setApplianceHeatPump(com.cannontech.database.db.stars.appliance.ApplianceHeatPump app, LiteStarsAppliance liteApp) {
		app.setApplianceID( new Integer(liteApp.getApplianceID()) );
		app.setPumpTypeID( new Integer(liteApp.getHeatPump().getPumpTypeID()) );
		app.setPumpSizeID( new Integer(liteApp.getHeatPump().getPumpSizeID()) );
		app.setStandbySourceID( new Integer(liteApp.getHeatPump().getStandbySourceID()) );
		app.setSecondsDelayToRestart( new Integer(liteApp.getHeatPump().getSecondsDelayToRestart()) );
	}
	
	public static void setApplianceIrrigation(com.cannontech.database.db.stars.appliance.ApplianceIrrigation app, LiteStarsAppliance liteApp) {
		app.setApplianceID( new Integer(liteApp.getApplianceID()) );
		app.setIrrigationTypeID( new Integer(liteApp.getIrrigation().getIrrigationTypeID()) );
		app.setHorsePowerID( new Integer(liteApp.getIrrigation().getHorsePowerID()) );
		app.setEnergySourceID( new Integer(liteApp.getIrrigation().getEnergySourceID()) );
		app.setSoilTypeID( new Integer(liteApp.getIrrigation().getSoilTypeID()) );
		app.setMeterLocationID( new Integer(liteApp.getIrrigation().getMeterLocationID()) );
		app.setMeterVoltageID( new Integer(liteApp.getIrrigation().getMeterVoltageID()) );
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
	
	public static void setEnergyCompany(com.cannontech.database.db.company.EnergyCompany company, LiteStarsEnergyCompany liteCompany) {
		company.setEnergyCompanyID( liteCompany.getEnergyCompanyID() );
		company.setName( liteCompany.getName() );
		company.setPrimaryContactID( new Integer(liteCompany.getPrimaryContactID()) );
		company.setUserID( new Integer(liteCompany.getUserID()) );
	}
	
	public static void setApplianceCategory(com.cannontech.database.db.stars.appliance.ApplianceCategory appCat, LiteApplianceCategory liteAppCat) {
		appCat.setApplianceCategoryID( new Integer(liteAppCat.getApplianceCategoryID()) );
		appCat.setCategoryID( new Integer(liteAppCat.getCategoryID()) );
		appCat.setDescription( liteAppCat.getDescription() );
		appCat.setWebConfigurationID( new Integer(liteAppCat.getWebConfigurationID()) );
	}
	
	public static void setServiceCompany(com.cannontech.database.db.stars.report.ServiceCompany company, LiteServiceCompany liteCompany) {
		company.setCompanyID( new Integer(liteCompany.getCompanyID()) );
		company.setCompanyName( liteCompany.getCompanyName() );
		company.setAddressID( new Integer(liteCompany.getAddressID()) );
		company.setMainPhoneNumber( liteCompany.getMainPhoneNumber() );
		company.setMainFaxNumber( liteCompany.getMainFaxNumber() );
		company.setPrimaryContactID( new Integer(liteCompany.getPrimaryContactID()) );
		company.setHIType( liteCompany.getHiType() );
	}
	
	public static void setSubstation(com.cannontech.database.db.stars.Substation sub, LiteSubstation liteSub) {
		sub.setSubstationID( new Integer(liteSub.getSubstationID()) );
		sub.setSubstationName( liteSub.getSubstationName() );
		sub.setRouteID( new Integer(liteSub.getRouteID()) );
	}
	
	
	public static com.cannontech.database.data.stars.customer.CustomerAccount createCustomerAccount(LiteStarsCustAccountInformation liteAccount, LiteStarsEnergyCompany energyCompany) {
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
		
		if (liteAccount.getCustomer() instanceof LiteCICustomer) {
			com.cannontech.database.data.customer.CICustomerBase ciCust =
					new com.cannontech.database.data.customer.CICustomerBase();
			StarsLiteFactory.setCICustomerBase( ciCust, (LiteCICustomer)liteAccount.getCustomer() );
			account.setCustomer( ciCust );
		}
		else {
			com.cannontech.database.data.customer.Customer customer =
					new com.cannontech.database.data.customer.Customer();
			StarsLiteFactory.setCustomer( customer.getCustomer(), liteAccount.getCustomer() );
			account.setCustomer( customer );
		}
		
		for (int i = 0; i < liteAccount.getInventories().size(); i++)
			account.getInventoryVector().add( liteAccount.getInventories().get(i) );
		
		for (int i = 0; i < liteAccount.getAppliances().size(); i++) {
			LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAccount.getAppliances().get(i);
			account.getApplianceVector().add( new Integer(liteApp.getApplianceID()) );
		}
		
		return account;
	}
	
	
	public static com.cannontech.database.db.constants.YukonListEntry createYukonListEntry(com.cannontech.common.constants.YukonListEntry cEntry) {
		com.cannontech.database.db.constants.YukonListEntry entry = new com.cannontech.database.db.constants.YukonListEntry();
		entry.setEntryID( new Integer(cEntry.getEntryID()) );
		entry.setListID( new Integer(cEntry.getListID()) );
		entry.setEntryOrder( new Integer(cEntry.getEntryOrder()) );
		entry.setEntryText( cEntry.getEntryText() );
		entry.setYukonDefID( new Integer(cEntry.getYukonDefID()) );
		
		return entry;
	}
	
	public static void setConstantYukonListEntry(
			com.cannontech.common.constants.YukonListEntry cEntry, com.cannontech.database.db.constants.YukonListEntry entry) {
		cEntry.setEntryID( entry.getEntryID().intValue() );
		cEntry.setListID( entry.getListID().intValue() );
		cEntry.setEntryOrder( entry.getEntryOrder().intValue() );
		cEntry.setEntryText( entry.getEntryText() );
		cEntry.setYukonDefID( entry.getYukonDefID().intValue() );
	}
	
	public static com.cannontech.database.db.constants.YukonSelectionList createYukonSelectionList(com.cannontech.common.constants.YukonSelectionList cList) {
		com.cannontech.database.db.constants.YukonSelectionList list = new com.cannontech.database.db.constants.YukonSelectionList();
		list.setListID( new Integer(cList.getListID()) );
		list.setOrdering( cList.getOrdering() );
		list.setSelectionLabel( cList.getSelectionLabel() );
		list.setWhereIsList( cList.getWhereIsList() );
		list.setListName( cList.getListName() );
		list.setUserUpdateAvailable( cList.getUserUpdateAvailable() );
		
		return list;
	}
	
	public static void setConstantYukonSelectionList(
			com.cannontech.common.constants.YukonSelectionList cList, com.cannontech.database.db.constants.YukonSelectionList list) {
		cList.setListID( list.getListID().intValue() );
		cList.setOrdering( list.getOrdering() );
		cList.setSelectionLabel( list.getSelectionLabel() );
		cList.setWhereIsList( list.getWhereIsList() );
		cList.setListName( list.getListName() );
		cList.setUserUpdateAvailable( list.getUserUpdateAvailable() );
	}

	
	public static void setStarsContactNotification(StarsContactNotification starsContNotif, LiteContactNotification liteContNotif) {
		starsContNotif.setNotifCatID( liteContNotif.getNotificationCategoryID() );
		starsContNotif.setDisabled( liteContNotif.isDisabled() );
		starsContNotif.setNotification( StarsUtils.forceNotNull(liteContNotif.getNotification()) );
	}
	
	public static void setStarsCustomerContact(StarsCustomerContact starsContact, LiteContact liteContact) {
		starsContact.setContactID( liteContact.getContactID() );
		starsContact.setLastName( StarsUtils.forceNotNull(liteContact.getContLastName()) );
		starsContact.setFirstName( StarsUtils.forceNotNull(liteContact.getContFirstName()) );
		
		starsContact.removeAllContactNotification();
		for (int i = 0; i < liteContact.getLiteContactNotifications().size(); i++) {
			LiteContactNotification liteContNotif = (LiteContactNotification) liteContact.getLiteContactNotifications().get(i);
			ContactNotification starsContNotif = new ContactNotification();
			setStarsContactNotification( starsContNotif, liteContNotif );
			starsContact.addContactNotification( starsContNotif );
		}
	}
	
	public static void setStarsCustomerAddress(StarsCustomerAddress starsAddr, LiteAddress liteAddr) {
		starsAddr.setAddressID( liteAddr.getAddressID() );
		starsAddr.setStreetAddr1( StarsUtils.forceNotNone(liteAddr.getLocationAddress1()) );
		starsAddr.setStreetAddr2( StarsUtils.forceNotNone(liteAddr.getLocationAddress2()) );
		starsAddr.setCity( StarsUtils.forceNotNone(liteAddr.getCityName()) );
		starsAddr.setState( StarsUtils.forceNotNone(liteAddr.getStateCode()) );
		starsAddr.setZip( StarsUtils.forceNotNone(liteAddr.getZipCode()) );
		starsAddr.setCounty( StarsUtils.forceNotNone(liteAddr.getCounty()) );
	}
	
	public static void setStarsLMCustomerEvent(StarsLMCustomerEvent starsEvent, LiteLMCustomerEvent liteEvent) {
		YukonListEntry entry = YukonListFuncs.getYukonListEntry( liteEvent.getActionID() );
		starsEvent.setEventAction( entry.getEntryText() );
		starsEvent.setEventDateTime( new Date(liteEvent.getEventDateTime()) );
		starsEvent.setNotes( StarsUtils.forceNotNull(liteEvent.getNotes()) );
		starsEvent.setYukonDefID( entry.getYukonDefID() );
	}
	
	public static void setStarsThermostatDynamicData(StarsThermostatDynamicData starsDynData, LiteStarsGatewayEndDevice liteDynData, LiteStarsEnergyCompany energyCompany) {
		if (liteDynData.getTimestamp() != 0)
			starsDynData.setLastUpdatedTime( new Date(liteDynData.getTimestamp()) );
		if (liteDynData.getDisplayedTemperature() != Integer.MIN_VALUE)
			starsDynData.setDisplayedTemperature( liteDynData.getDisplayedTemperature() );
		if (liteDynData.getDisplayedTempUnit() != null)
			starsDynData.setDisplayedTempUnit( liteDynData.getDisplayedTempUnit().equalsIgnoreCase("C") ? "Celsius" : "Fahrenheit" );
		starsDynData.setFan( StarsMsgUtils.getThermFanSetting(liteDynData.getFanSwitch()) );
		starsDynData.setMode( StarsMsgUtils.getThermModeSetting(liteDynData.getSystemSwitch()) );
		if (liteDynData.getCoolSetpoint() != Integer.MIN_VALUE)
			starsDynData.setCoolSetpoint( liteDynData.getCoolSetpoint() );
		if (liteDynData.getHeatSetpoint() != Integer.MIN_VALUE)
			starsDynData.setHeatSetpoint( liteDynData.getHeatSetpoint() );
		if (liteDynData.getSetpointStatus() != null &&
			(liteDynData.getSetpointStatus().equalsIgnoreCase("HOLD") ||
			liteDynData.getSetpointStatus().equalsIgnoreCase("VACATION")))
			starsDynData.setSetpointHold( true );
		else
			starsDynData.setSetpointHold( false );
		if (liteDynData.getLowerCoolSetpointLimit() != Integer.MIN_VALUE)
			starsDynData.setLowerCoolSetpointLimit( liteDynData.getLowerCoolSetpointLimit() );
		if (liteDynData.getUpperHeatSetpointLimit() != Integer.MIN_VALUE)
			starsDynData.setUpperHeatSetpointLimit( liteDynData.getUpperHeatSetpointLimit() );
		
		starsDynData.removeAllInfoString();

		// If the current mode is auto, then display that in the text area, and set mode to the last none-auto mode of the thermostat
		StarsThermoModeSettings mode = StarsMsgUtils.getThermModeSetting( liteDynData.getSystemSwitch() );
		if (mode != null && mode.getType() == StarsThermoModeSettings.AUTO_TYPE) {
			mode = StarsMsgUtils.getThermModeSetting( liteDynData.getLastSystemSwitch() );
			starsDynData.addInfoString( "Mode: AUTO" );
		}
		starsDynData.setMode( mode );
		
		if (liteDynData.getOutdoorTemperature() != Integer.MIN_VALUE) {
			String desc = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_GED_OUTDOOR_TEMP ).getEntryText();
			starsDynData.addInfoString( desc + ": " + liteDynData.getOutdoorTemperature() + "&deg;" + StarsUtils.forceNotNull(liteDynData.getDisplayedTempUnit()) );
		}
		if (liteDynData.getFilterRemaining() > 0) {
			StringTokenizer st = new StringTokenizer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_GED_FILTER).getEntryText(), "," );
			String desc = st.nextToken();
			starsDynData.addInfoString( desc + "(day): " + liteDynData.getFilterRemaining() );
		}
		if (liteDynData.getBattery() != null) {
			String desc = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_GED_BATTERY ).getEntryText();
			starsDynData.addInfoString( desc + ": " + liteDynData.getBattery() );
		}
		if (liteDynData.getCoolRuntime() > 0 || liteDynData.getHeatRuntime() > 0) {
			StringTokenizer st = new StringTokenizer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_GED_RUNTIMES).getEntryText(), "," );
			String coolDesc = st.nextToken();
			String heatDesc = st.nextToken();
			starsDynData.addInfoString( coolDesc + "(min): " + liteDynData.getCoolRuntime() );
			starsDynData.addInfoString( heatDesc + "(min): " + liteDynData.getHeatRuntime() );
		}
		for (int i = 0; i < liteDynData.getInfoStrings().size(); i++)
			starsDynData.addInfoString( (String) liteDynData.getInfoStrings().get(i) );
	}
	
	public static StarsThermostatProgram createStarsThermostatProgram(LiteLMThermostatSchedule liteSchedule, LiteStarsEnergyCompany energyCompany) {
		StarsThermostatProgram starsThermProg = new StarsThermostatProgram();
		
		starsThermProg.setScheduleID( liteSchedule.getScheduleID() );
		if (!liteSchedule.getScheduleName().equals( CtiUtilities.STRING_NONE ))
			starsThermProg.setScheduleName( liteSchedule.getScheduleName() );
		starsThermProg.setThermostatType( StarsMsgUtils.getThermostatType(liteSchedule.getThermostatTypeID()) );
		for (int i = 0; i < liteSchedule.getThermostatSeasons().size(); i++) {
			LiteLMThermostatSeason liteSeason = (LiteLMThermostatSeason) liteSchedule.getThermostatSeasons().get(i);
			StarsThermostatSeason starsSeason = createStarsThermostatSeason( liteSeason, energyCompany );
			starsThermProg.addStarsThermostatSeason( starsSeason );
		}
		
		return starsThermProg;
		
	}
	
	public static void setStarsThermostatSettings(StarsThermostatSettings starsSettings, LiteStarsLMHardware liteHw, LiteStarsEnergyCompany energyCompany) {
		LiteStarsThermostatSettings liteSettings = liteHw.getThermostatSettings();
		
		starsSettings.setStarsThermostatProgram( createStarsThermostatProgram(liteSettings.getThermostatSchedule(), energyCompany) );
		
		starsSettings.removeAllStarsThermostatManualEvent();
		for (int i = 0; i < liteSettings.getThermostatManualEvents().size(); i++) {
			LiteLMThermostatManualEvent liteEvent = (LiteLMThermostatManualEvent) liteSettings.getThermostatManualEvents().get(i);
			starsSettings.addStarsThermostatManualEvent( StarsLiteFactory.createStarsThermostatManualEvent(liteEvent) );
		}
		
		if (liteSettings.getDynamicData() != null) {
			StarsThermostatDynamicData starsDynData = new StarsThermostatDynamicData();
			setStarsThermostatDynamicData( starsDynData, liteSettings.getDynamicData(), energyCompany );
			starsSettings.setStarsThermostatDynamicData( starsDynData );
		}
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
	
	public static void setStarsCustResidence(StarsCustResidence starsRes, LiteCustomerResidence liteRes) {
		ResidenceType resType = new ResidenceType();
		setStarsCustListEntry( resType, YukonListFuncs.getYukonListEntry(liteRes.getResidenceTypeID()) );
		starsRes.setResidenceType( resType );
		
		ConstructionMaterial material = new ConstructionMaterial();
		setStarsCustListEntry( material, YukonListFuncs.getYukonListEntry(liteRes.getConstructionMaterialID()) );
		starsRes.setConstructionMaterial( material );
		
		DecadeBuilt decade = new DecadeBuilt();
		setStarsCustListEntry( decade, YukonListFuncs.getYukonListEntry(liteRes.getDecadeBuiltID()) );
		starsRes.setDecadeBuilt( decade );
		
		SquareFeet sqrFeet = new SquareFeet();
		setStarsCustListEntry( sqrFeet, YukonListFuncs.getYukonListEntry(liteRes.getSquareFeetID()) );
		starsRes.setSquareFeet( sqrFeet );
		
		InsulationDepth depth = new InsulationDepth();
		setStarsCustListEntry( depth, YukonListFuncs.getYukonListEntry(liteRes.getInsulationDepthID()) );
		starsRes.setInsulationDepth( depth );
		
		GeneralCondition condition = new GeneralCondition();
		setStarsCustListEntry( condition, YukonListFuncs.getYukonListEntry(liteRes.getGeneralConditionID()) );
		starsRes.setGeneralCondition( condition );
		
		MainCoolingSystem coolSys = new MainCoolingSystem();
		setStarsCustListEntry( coolSys, YukonListFuncs.getYukonListEntry(liteRes.getMainCoolingSystemID()) );
		starsRes.setMainCoolingSystem( coolSys );
		
		MainHeatingSystem heatSys = new MainHeatingSystem();
		setStarsCustListEntry( heatSys, YukonListFuncs.getYukonListEntry(liteRes.getMainHeatingSystemID()) );
		starsRes.setMainHeatingSystem( heatSys );
		
		NumberOfOccupants number = new NumberOfOccupants();
		setStarsCustListEntry( number, YukonListFuncs.getYukonListEntry(liteRes.getNumberOfOccupantsID()) );
		starsRes.setNumberOfOccupants( number );
		
		OwnershipType ownType = new OwnershipType();
		setStarsCustListEntry( ownType, YukonListFuncs.getYukonListEntry(liteRes.getOwnershipTypeID()) );
		starsRes.setOwnershipType( ownType );
		
		MainFuelType fuelType = new MainFuelType();
		setStarsCustListEntry( fuelType, YukonListFuncs.getYukonListEntry(liteRes.getMainFuelTypeID()) );
		starsRes.setMainFuelType( fuelType );
		
		starsRes.setNotes( liteRes.getNotes() );
	}
	
	public static StarsLMPrograms createStarsLMPrograms(LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany) {
		StarsLMPrograms starsProgs = new StarsLMPrograms();
		
		for (int i = 0; i < liteAcctInfo.getPrograms().size(); i++) {
			LiteStarsLMProgram liteProg = (LiteStarsLMProgram) liteAcctInfo.getPrograms().get(i);
			starsProgs.addStarsLMProgram( createStarsLMProgram(liteProg, liteAcctInfo) );
		}
		
		starsProgs.setStarsLMProgramHistory( createStarsLMProgramHistory(liteAcctInfo, energyCompany) );
		
		return starsProgs;
	}
	
	public static StarsAppliances createStarsAppliances(ArrayList liteApps, LiteStarsEnergyCompany energyCompany) {
		StarsAppliances starsApps = new StarsAppliances();
		
		TreeMap appMap = new TreeMap();
		for (int i = 0; i < liteApps.size(); i++) {
			LiteStarsAppliance liteApp = (LiteStarsAppliance) liteApps.get(i);
			StarsAppliance starsApp = (StarsAppliance) createStarsAppliance(liteApp, energyCompany);
			
			String description = energyCompany.getApplianceCategory( starsApp.getApplianceCategoryID() ).getDescription();
			ArrayList list = (ArrayList) appMap.get( description );
			if (list == null) {
				list = new ArrayList();
				appMap.put( description, list );
			}
			list.add( starsApp );
		}
		
		Iterator it = appMap.values().iterator();
		while (it.hasNext()) {
			ArrayList list = (ArrayList) it.next();
			for (int i = 0; i < list.size(); i++)
				starsApps.addStarsAppliance( (StarsAppliance) list.get(i) );
		}
		
		return starsApps;
	}
	
	public static void setStarsCustAccountInformation(StarsCustAccountInformation starsAcctInfo, LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany, boolean isOperator) {
		LiteCustomerAccount liteAccount = liteAcctInfo.getCustomerAccount();
		LiteCustomer liteCustomer = liteAcctInfo.getCustomer();
		LiteAccountSite liteAcctSite = liteAcctInfo.getAccountSite();
		LiteSiteInformation liteSiteInfo = liteAcctInfo.getSiteInformation();
		LiteContact liteContact = ContactFuncs.getContact( liteAcctInfo.getCustomer().getPrimaryContactID() );
		
		StarsCustomerAccount starsAccount = new StarsCustomerAccount();
		starsAccount.setAccountID( liteAccount.getAccountID() );
		starsAccount.setCustomerID( liteAccount.getCustomerID() );
		starsAccount.setAccountNumber( StarsUtils.forceNotNull(liteAccount.getAccountNumber()) );
		starsAccount.setIsCommercial( liteCustomer.getCustomerTypeID() == CustomerTypes.CUSTOMER_CI );
		if (liteCustomer instanceof LiteCICustomer)
			starsAccount.setCompany( ((LiteCICustomer)liteCustomer).getCompanyName() );
		else
			starsAccount.setCompany( "" );
		starsAccount.setAccountNotes( StarsUtils.forceNotNull(liteAccount.getAccountNotes()) );
		starsAccount.setPropertyNumber( StarsUtils.forceNotNull(liteAcctSite.getSiteNumber()) );
		starsAccount.setPropertyNotes( StarsUtils.forceNotNull(liteAcctSite.getPropertyNotes()) );
		starsAccount.setTimeZone( liteCustomer.getTimeZone() );
		starsAcctInfo.setStarsCustomerAccount( starsAccount );
		
		StreetAddress streetAddr = new StreetAddress();
		setStarsCustomerAddress( streetAddr, energyCompany.getAddress(liteAcctSite.getStreetAddressID()) );
		starsAccount.setStreetAddress( streetAddr );
		
		starsAccount.setStarsSiteInformation( createStarsSiteInformation(liteSiteInfo, energyCompany) );
				
		BillingAddress billAddr = new BillingAddress();
		setStarsCustomerAddress( billAddr, energyCompany.getAddress(liteAccount.getBillingAddressID()) );
		starsAccount.setBillingAddress( billAddr );
		
		PrimaryContact primContact = new PrimaryContact();
		setStarsCustomerContact( primContact, liteContact );
		starsAccount.setPrimaryContact( primContact );
		
		starsAccount.removeAllAdditionalContact();
		for (int i = 0; i < liteCustomer.getAdditionalContacts().size(); i++) {
			LiteContact lContact = (LiteContact) liteCustomer.getAdditionalContacts().get(i);
			AdditionalContact contact = new AdditionalContact();
			setStarsCustomerContact( contact, lContact );
			starsAccount.addAdditionalContact( contact );
		}
		
		if (liteAcctInfo.getCustomerResidence() != null) {
			StarsResidenceInformation residence = new StarsResidenceInformation();
			setStarsCustResidence( residence, liteAcctInfo.getCustomerResidence() );
			starsAcctInfo.setStarsResidenceInformation( residence );
		}
		
		StarsLMPrograms starsProgs = createStarsLMPrograms( liteAcctInfo, energyCompany );
		starsAcctInfo.setStarsLMPrograms( starsProgs );
		
		ArrayList liteInvs = liteAcctInfo.getInventories();
		StarsInventories starsInvs = new StarsInventories();
		starsAcctInfo.setStarsInventories( starsInvs );
		
		TreeMap invMap = new TreeMap();
		for (int i = 0; i < liteInvs.size(); i++) {
			LiteInventoryBase liteInv = energyCompany.getInventory( ((Integer)liteInvs.get(i)).intValue(), true );
			StarsInventory starsInv = createStarsInventory( liteInv, energyCompany );
			
			ArrayList list = (ArrayList) invMap.get( starsInv.getDeviceLabel() );
			if (list == null) {
				list = new ArrayList();
				invMap.put( ServletUtils.getInventoryLabel(starsInv), list );
			}
			list.add( starsInv );
		}
		
		Iterator it = invMap.values().iterator();
		while (it.hasNext()) {
			ArrayList list = (ArrayList) it.next();
			for (int i = 0; i < list.size(); i++)
				starsInvs.addStarsInventory( (StarsInventory)list.get(i) );
		}
		
		if (liteContact.getLoginID() != com.cannontech.user.UserUtils.USER_STARS_DEFAULT_ID &&
			liteContact.getLoginID() != com.cannontech.user.UserUtils.USER_ADMIN_ID)
		{
			LiteYukonUser liteUser = com.cannontech.database.cache.functions.YukonUserFuncs.getLiteYukonUser( liteContact.getLoginID() );
			starsAcctInfo.setStarsUser( createStarsUser(liteUser, energyCompany) );
		}
		
		ArrayList liteSchedules = liteAcctInfo.getThermostatSchedules();
		StarsSavedThermostatSchedules starsSchedules = new StarsSavedThermostatSchedules();
		starsAcctInfo.setStarsSavedThermostatSchedules( starsSchedules );
		
		for (int i = 0; i < liteSchedules.size(); i++) {
			LiteLMThermostatSchedule liteSchedule = (LiteLMThermostatSchedule) liteSchedules.get(i);
			starsSchedules.addStarsThermostatProgram( createStarsThermostatProgram(liteSchedule, energyCompany) );
		}
		
		if (isOperator) {
			starsAcctInfo.setStarsAppliances( createStarsAppliances(liteAcctInfo.getAppliances(), energyCompany) );
			
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
				LiteWorkOrderBase liteOrder = energyCompany.getWorkOrderBase( ((Integer) liteOrders.get(i)).intValue(), true );
				starsOrders.addStarsServiceRequest( createStarsServiceRequest(liteOrder, energyCompany) );
			}
		}
	}
	
	public static void setStarsEnergyCompany(StarsEnergyCompany starsCompany, LiteStarsEnergyCompany liteCompany) {
		starsCompany.setCompanyName( liteCompany.getName() );
		starsCompany.setMainPhoneNumber( "" );
		starsCompany.setMainFaxNumber( "" );
		starsCompany.setEmail( "" );
		starsCompany.setCompanyAddress( (CompanyAddress) StarsFactory.newStarsCustomerAddress(CompanyAddress.class) );
		starsCompany.setTimeZone( liteCompany.getDefaultTimeZone().getID() );
		
		if (liteCompany.getPrimaryContactID() != CtiUtilities.NONE_ID) {
			LiteContact liteContact = ContactFuncs.getContact( liteCompany.getPrimaryContactID() );
			
			if (liteContact != null) {
				LiteContactNotification liteNotifPhone = ContactFuncs.getContactNotification( liteContact, YukonListEntryTypes.YUK_ENTRY_ID_PHONE );
				starsCompany.setMainPhoneNumber( StarsUtils.getNotification(liteNotifPhone) );
				
				LiteContactNotification liteNotifFax = ContactFuncs.getContactNotification( liteContact, YukonListEntryTypes.YUK_ENTRY_ID_FAX );
				starsCompany.setMainFaxNumber( StarsUtils.getNotification(liteNotifFax) );
				
				LiteContactNotification liteNotifEmail = ContactFuncs.getContactNotification( liteContact, YukonListEntryTypes.YUK_ENTRY_ID_EMAIL );
				starsCompany.setEmail( StarsUtils.getNotification(liteNotifEmail) );
				
				if (liteContact.getAddressID() != CtiUtilities.NONE_ID) {
					LiteAddress liteAddr = liteCompany.getAddress( liteContact.getAddressID() );
					CompanyAddress starsAddr = new CompanyAddress();
					setStarsCustomerAddress( starsAddr, liteAddr );
					starsCompany.setCompanyAddress( starsAddr );
				}
			}
		}
	}
	
	public static void setStarsServiceCompany(StarsServiceCompany starsCompany, LiteServiceCompany liteCompany, LiteStarsEnergyCompany energyCompany) {
		starsCompany.setCompanyID( liteCompany.getCompanyID() );
		starsCompany.setInherited( !energyCompany.getServiceCompanies().contains(liteCompany) );
		starsCompany.setCompanyName( StarsUtils.forceNotNull(liteCompany.getCompanyName()) );
		starsCompany.setMainPhoneNumber( StarsUtils.forceNotNone(liteCompany.getMainPhoneNumber()) );
		starsCompany.setMainFaxNumber( StarsUtils.forceNotNone(liteCompany.getMainFaxNumber()) );
		starsCompany.setCompanyAddress( (CompanyAddress) StarsFactory.newStarsCustomerAddress(CompanyAddress.class) );
		starsCompany.setPrimaryContact( (PrimaryContact) StarsFactory.newStarsCustomerContact(PrimaryContact.class) );
		
		if (liteCompany.getAddressID() != CtiUtilities.NONE_ID) {
			LiteAddress liteAddr = energyCompany.getAddress( liteCompany.getAddressID());
			CompanyAddress companyAddr = new CompanyAddress();
			setStarsCustomerAddress( companyAddr, liteAddr );
			starsCompany.setCompanyAddress( companyAddr );
		}
		
		if (liteCompany.getPrimaryContactID() != CtiUtilities.NONE_ID) {
			LiteContact liteContact = ContactFuncs.getContact( liteCompany.getPrimaryContactID() );
			PrimaryContact primContact = new PrimaryContact();
			setStarsCustomerContact( primContact, liteContact );
			starsCompany.setPrimaryContact( primContact );
		}
	}
	
	public static void setStarsSubstation(StarsSubstation starsSub, LiteSubstation liteSub, LiteStarsEnergyCompany energyCompany) {
		starsSub.setSubstationID( liteSub.getSubstationID() );
		starsSub.setSubstationName( liteSub.getSubstationName() );
		starsSub.setRouteID( liteSub.getRouteID() );
		starsSub.setInherited( !energyCompany.getSubstations().contains(liteSub) );
	}
	
	public static StarsLMConfiguration createStarsLMConfiguration(LiteLMConfiguration liteCfg) {
		StarsLMConfiguration starsCfg = new StarsLMConfiguration();
		
		starsCfg.setColdLoadPickup( StarsUtils.forceNotNone(liteCfg.getColdLoadPickup()) );
		starsCfg.setTamperDetect( StarsUtils.forceNotNone(liteCfg.getTamperDetect()) );
		
		if (liteCfg.getSA205() != null) {
			SA205 sa205 = new SA205();
			sa205.setSlot1( liteCfg.getSA205().getSlot1() );
			sa205.setSlot2( liteCfg.getSA205().getSlot2() );
			sa205.setSlot3( liteCfg.getSA205().getSlot3() );
			sa205.setSlot4( liteCfg.getSA205().getSlot4() );
			sa205.setSlot5( liteCfg.getSA205().getSlot5() );
			sa205.setSlot6( liteCfg.getSA205().getSlot6() );
			starsCfg.setSA205( sa205 );
		}
		else if (liteCfg.getSA305() != null) {
			SA305 sa305 = new SA305();
			sa305.setUtility( liteCfg.getSA305().getUtility() );
			sa305.setGroup( liteCfg.getSA305().getGroup() );
			sa305.setDivision( liteCfg.getSA305().getDivision() );
			sa305.setSubstation( liteCfg.getSA305().getSubstation() );
			sa305.setRateFamily( liteCfg.getSA305().getRateFamily() );
			sa305.setRateMember( liteCfg.getSA305().getRateMember() );
			sa305.setRateHierarchy( liteCfg.getSA305().getRateHierarchy() );
			starsCfg.setSA305( sa305 );
		}
		else if (liteCfg.getExpressCom() != null) {
			ExpressCom xcom = new ExpressCom();
			xcom.setServiceProvider( liteCfg.getExpressCom().getServiceProvider() );
			xcom.setGEO( liteCfg.getExpressCom().getGEO() );
			xcom.setSubstation( liteCfg.getExpressCom().getSubstation() );
			xcom.setFeeder( liteCfg.getExpressCom().getFeeder() );
			xcom.setZip( liteCfg.getExpressCom().getZip() );
			xcom.setUserAddress( liteCfg.getExpressCom().getUserAddress() );
			xcom.setProgram( liteCfg.getExpressCom().getProgram() );
			xcom.setSplinter( liteCfg.getExpressCom().getSplinter() );
			starsCfg.setExpressCom( xcom );
		}
		else if (liteCfg.getVersaCom() != null) {
			VersaCom vcom = new VersaCom();
			vcom.setUtility( liteCfg.getVersaCom().getUtilityID() );
			vcom.setSection( liteCfg.getVersaCom().getSection() );
			vcom.setClassAddress( liteCfg.getVersaCom().getClassAddress() );
			vcom.setDivision( liteCfg.getVersaCom().getDivisionAddress() );
			starsCfg.setVersaCom( vcom );
		}
		
		return starsCfg;
	}
	
	public static void setStarsInv(StarsInv starsInv, LiteInventoryBase liteInv, LiteStarsEnergyCompany energyCompany) {
		starsInv.setInventoryID( liteInv.getInventoryID() );
		starsInv.setDeviceID( liteInv.getDeviceID() );
		starsInv.setCategory( YukonListFuncs.getYukonListEntry(liteInv.getCategoryID()).getEntryText() );
		starsInv.setDeviceLabel( StarsUtils.forceNotNull(liteInv.getDeviceLabel()) );
		
		InstallationCompany company = new InstallationCompany();
		company.setEntryID( liteInv.getInstallationCompanyID() );
		LiteServiceCompany liteCompany = energyCompany.getServiceCompany( liteInv.getInstallationCompanyID() );
		if (liteCompany != null)
			company.setContent( StarsUtils.forceNotNull(liteCompany.getCompanyName()) );
		else
			company.setContent( "(none)" );
		starsInv.setInstallationCompany( company );
		
		starsInv.setReceiveDate( StarsUtils.translateDate(liteInv.getReceiveDate()) );
		starsInv.setInstallDate( StarsUtils.translateDate(liteInv.getInstallDate()) );
		starsInv.setRemoveDate( StarsUtils.translateDate(liteInv.getRemoveDate()) );
		starsInv.setAltTrackingNumber( StarsUtils.forceNotNull(liteInv.getAlternateTrackingNumber()) );
		
		Voltage volt = new Voltage();
		volt.setEntryID( liteInv.getVoltageID() );
		volt.setContent( YukonListFuncs.getYukonListEntry(liteInv.getVoltageID()).getEntryText() );
		starsInv.setVoltage( volt );
		
		starsInv.setNotes( StarsUtils.forceNotNull(liteInv.getNotes()) );
		
		StarsLMHardwareHistory hwHist = new StarsLMHardwareHistory();
		for (int i = 0; i < liteInv.getInventoryHistory().size(); i++) {
			LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) liteInv.getInventoryHistory().get(i);
			StarsLMHardwareEvent starsEvent = new StarsLMHardwareEvent();
			setStarsLMCustomerEvent( starsEvent, liteEvent );
			hwHist.addStarsLMHardwareEvent( starsEvent );
		}
		starsInv.setStarsLMHardwareHistory( hwHist );
		
		// set installation notes
		starsInv.setInstallationNotes( "" );
		for (int i = hwHist.getStarsLMHardwareEventCount() - 1; i >= 0; i--) {
			if (hwHist.getStarsLMHardwareEvent(i).getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL) {
				starsInv.setInstallationNotes( hwHist.getStarsLMHardwareEvent(i).getNotes() );
				break;
			}
		}
		
		starsInv.setDeviceStatus( (DeviceStatus) StarsFactory.newStarsCustListEntry(
				energyCompany.getYukonListEntry( liteInv.getDeviceStatus() ), DeviceStatus.class) );
		
		if (liteInv instanceof LiteStarsLMHardware) {
			LiteStarsLMHardware liteHw = (LiteStarsLMHardware) liteInv;
			starsInv.setDeviceType( (DeviceType)StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry(liteHw.getLmHardwareTypeID()),
					DeviceType.class) );
			
			LMHardware hw = new LMHardware();
			hw.setRouteID( liteHw.getRouteID() );
			hw.setManufacturerSerialNumber( StarsUtils.forceNotNull(((LiteStarsLMHardware)liteInv).getManufacturerSerialNumber()) );
			
			if (liteHw.getThermostatSettings() != null) {
				StarsThermostatSettings starsSettings = new StarsThermostatSettings();
				setStarsThermostatSettings( starsSettings, liteHw, energyCompany );
				hw.setStarsThermostatSettings( starsSettings );
			}
			
			if (liteHw.getLMConfiguration() != null)
				hw.setStarsLMConfiguration( createStarsLMConfiguration(liteHw.getLMConfiguration()) );
			
			starsInv.setLMHardware( hw );
		}
		else if (InventoryUtils.isMCT( liteInv.getCategoryID() )) {
			starsInv.setDeviceType( (DeviceType)StarsFactory.newStarsCustListEntry(
					energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT),
					DeviceType.class) );
			
			MCT mct = new MCT();
			if (liteInv.getDeviceID() > 0)
				mct.setDeviceName( PAOFuncs.getYukonPAOName(liteInv.getDeviceID()) );
			else if (liteInv.getDeviceLabel() != null && liteInv.getDeviceLabel().length() > 0)
				mct.setDeviceName( liteInv.getDeviceLabel() );
			else
				mct.setDeviceName( "(none)" );
			
			starsInv.setMCT( mct );
		}
		else {
			starsInv.setDeviceType( (DeviceType)StarsFactory.newEmptyStarsCustListEntry( DeviceType.class ));
			starsInv.setLMHardware( null );
			starsInv.setMCT( null );
		}
	}
	
	
	public static StarsCustAccountInformation createStarsCustAccountInformation(LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany, boolean isOperator) {
		StarsCustAccountInformation starsAcctInfo = new StarsCustAccountInformation();
		setStarsCustAccountInformation( starsAcctInfo, liteAcctInfo, energyCompany, isOperator );
		return starsAcctInfo;
	}
	
	public static StarsSiteInformation createStarsSiteInformation(LiteSiteInformation liteSite, LiteStarsEnergyCompany energyCompany) {
		StarsSiteInformation starsSite = new StarsSiteInformation();
		
		starsSite.setSiteID( liteSite.getSiteID() );
		starsSite.setFeeder( StarsUtils.forceNotNull(liteSite.getFeeder()) );
		starsSite.setPole( StarsUtils.forceNotNull(liteSite.getPole()) );
		starsSite.setTransformerSize( StarsUtils.forceNotNull(liteSite.getTransformerSize()) );
		starsSite.setServiceVoltage( StarsUtils.forceNotNull(liteSite.getServiceVoltage()) );
		
		LiteSubstation liteSub = energyCompany.getSubstation( liteSite.getSubstationID() );
		if (liteSub != null) {
			Substation sub = new Substation();
			sub.setEntryID( liteSub.getSubstationID() );
			sub.setContent( liteSub.getSubstationName() );
			starsSite.setSubstation( sub );
		}
		else {
			starsSite.setSubstation( (Substation)StarsFactory.newEmptyStarsCustListEntry(Substation.class) );
		}
		
		return starsSite;
	}
	
	public static StarsInventory createStarsInventory(LiteInventoryBase liteInv, LiteStarsEnergyCompany energyCompany) {
		StarsInventory starsInv = new StarsInventory();
		setStarsInv( starsInv, liteInv, energyCompany );
		return starsInv;
	}
	
	public static StarsServiceRequest createStarsServiceRequest(LiteWorkOrderBase liteOrder, LiteStarsEnergyCompany energyCompany) {
		StarsServiceRequest starsOrder = new StarsServiceRequest();
		starsOrder.setOrderID( liteOrder.getOrderID() );
		starsOrder.setOrderNumber( StarsUtils.forceNotNull(liteOrder.getOrderNumber()) );
		
		starsOrder.setServiceType(
			(ServiceType) StarsFactory.newStarsCustListEntry(
				YukonListFuncs.getYukonListEntry(liteOrder.getWorkTypeID()),
				ServiceType.class)
		);
		
		ServiceCompany company = new ServiceCompany();
		company.setEntryID( liteOrder.getServiceCompanyID() );
		LiteServiceCompany liteCompany = energyCompany.getServiceCompany( liteOrder.getServiceCompanyID() );
		if (liteCompany != null)
			company.setContent( StarsUtils.forceNotNull(liteCompany.getCompanyName()) );
		else
			company.setContent( "(none)" );
		starsOrder.setServiceCompany( company );
		
		starsOrder.setCurrentState(
			(CurrentState) StarsFactory.newStarsCustListEntry(
				YukonListFuncs.getYukonListEntry(liteOrder.getCurrentStateID()),
				CurrentState.class)
		);
		
		starsOrder.setDateReported( StarsUtils.translateDate(liteOrder.getDateReported()) );
		starsOrder.setDateScheduled( StarsUtils.translateDate(liteOrder.getDateScheduled()) );
		starsOrder.setDateCompleted( StarsUtils.translateDate(liteOrder.getDateCompleted()) );
		starsOrder.setOrderedBy( StarsUtils.forceNotNull(liteOrder.getOrderedBy()) );
		starsOrder.setDescription( StarsUtils.forceNotNull(liteOrder.getDescription()) );
		starsOrder.setActionTaken( StarsUtils.forceNotNull(liteOrder.getActionTaken()) );
		
		return starsOrder;
	}
	
	public static StarsLMProgram createStarsLMProgram(LiteStarsLMProgram liteProg, LiteStarsCustAccountInformation liteAcctInfo)
	{
		StarsLMProgram starsProg = new StarsLMProgram();
		starsProg.setProgramID( liteProg.getProgramID() );
		starsProg.setApplianceCategoryID( liteProg.getPublishedProgram().getApplianceCategoryID() );
		starsProg.setGroupID( liteProg.getGroupID() );
		starsProg.setProgramName( StarsUtils.getPublishedProgramName(liteProg.getPublishedProgram()) );
		
		if (liteProg.isInService())
			starsProg.setStatus( ServletUtils.IN_SERVICE );
		else
			starsProg.setStatus( ServletUtils.OUT_OF_SERVICE );
		
		for (int i = liteAcctInfo.getProgramHistory().size() - 1; i >= 0; i--) {
			LiteLMProgramEvent liteEvent = (LiteLMProgramEvent) liteAcctInfo.getProgramHistory().get(i);
			if (liteEvent.getProgramID() == liteProg.getProgramID()) {
				YukonListEntry entry = YukonListFuncs.getYukonListEntry( liteEvent.getActionID() );
				if (entry != null && entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_SIGNUP) {
					starsProg.setDateEnrolled( new Date(liteEvent.getEventDateTime()) );
					break;
				}
			}
		}
		
		return starsProg;
	}
	
	public static StarsLMProgramHistory createStarsLMProgramHistory(
		LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany)
	{
		StarsLMProgramHistory starsProgHist = new StarsLMProgramHistory();
		ArrayList liteProgHist2 = new ArrayList( liteAcctInfo.getProgramHistory() );
		TreeMap progHistMap = new TreeMap();
		
		for (int i = 0; i < liteProgHist2.size(); i++) {
			LiteLMProgramEvent liteEvent = (LiteLMProgramEvent) liteProgHist2.get(i);
    		
			StarsLMProgramEvent starsEvent = new StarsLMProgramEvent();
			setStarsLMCustomerEvent( starsEvent, liteEvent );
			starsEvent.addProgramID( liteEvent.getProgramID() );
			
			if (starsEvent.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION) {
				// Get opt out duration (in number of hours)
				boolean foundDuration = false;
				
				Iterator it = liteProgHist2.listIterator(i + 1);
				while (it.hasNext()) {
					LiteLMProgramEvent liteEvent2 = (LiteLMProgramEvent) it.next();
					if (liteEvent2.getProgramID() != liteEvent.getProgramID())
						continue;
					
					YukonListEntry entry = YukonListFuncs.getYukonListEntry( liteEvent2.getActionID() );
					
					if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION
						|| entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED
						|| entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION)
					{
						int duration = (int) ((liteEvent2.getEventDateTime() - liteEvent.getEventDateTime()) * 0.001 / 3600 + 0.5);
						starsEvent.setDuration( duration );
						foundDuration = true;
						
						it.remove();
						break;
					}
					else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION) {
						it.remove();
					}
					else {
						CTILogger.error( "Invalid event action after opt out event, event id=" + liteEvent.getEventID() + "," + liteEvent2.getEventID() );
						break;
					}
				}
				
				if (!foundDuration) continue;
			}
			
			StarsLMProgramEvent starsEvent2 = (StarsLMProgramEvent) progHistMap.get( starsEvent.getEventDateTime() );
			if (starsEvent2 == null)	// No other events happened at the same time
				progHistMap.put( starsEvent.getEventDateTime(), starsEvent );
			else {	// Found events happened at the same time
				if (starsEvent2.getYukonDefID() != starsEvent.getYukonDefID())	// Not the same action
					progHistMap.put( starsEvent.getEventDateTime(), starsEvent );
				else {	// Same event action
					if (!starsEvent.hasDuration())	// Not Temporary opt out action
						starsEvent2.addProgramID( liteEvent.getProgramID() );
					else {	// Temporary opt out action
						if (starsEvent2.getDuration() == starsEvent.getDuration())	// Same duration
							starsEvent2.addProgramID( liteEvent.getProgramID() );
						else	// Different duration
							progHistMap.put( starsEvent.getEventDateTime(), starsEvent );
					}
				}
			}
		}
		
		StarsLMProgramEvent[] starsEvents = new StarsLMProgramEvent[ progHistMap.size() ];
		progHistMap.values().toArray( starsEvents );
		starsProgHist.setStarsLMProgramEvent( starsEvents );
		
		OptOutEventQueue queue = OptOutEventQueue.getInstance();
		if (queue != null) {
			OptOutEventQueue.OptOutEvent[] optoutEvents = queue.findOptOutEvents( liteAcctInfo.getAccountID() );
			ArrayList events = new ArrayList();
			
			for (int i = 0; i < optoutEvents.length; i++) {
				StarsLMProgramEvent relatedEvent = null;
				for (int j = 0; j < events.size(); j++) {
					StarsLMProgramEvent event = (StarsLMProgramEvent) events.get(j);
					if (Math.abs(event.getEventDateTime().getTime() - optoutEvents[i].getStartDateTime()) < 1000
						&& event.getDuration() == optoutEvents[i].getPeriod())
					{
						relatedEvent = event;
						break;
					}
				}
				
				if (relatedEvent == null) {
					StarsLMProgramEvent event = createStarsOptOutEvent( optoutEvents[i], energyCompany );
					starsProgHist.addStarsLMProgramEvent( event );
					events.add( event );
				}
				else {
					if (optoutEvents[i].getInventoryID() == 0) {
						// Opt out event for all programs
						relatedEvent.removeAllProgramID();
						for (int j = 0; j < liteAcctInfo.getPrograms().size(); j++)
							relatedEvent.addProgramID( ((LiteStarsLMProgram)liteAcctInfo.getPrograms().get(j)).getProgramID() );
					}
					else {
						// Opt out event for a hardware
						for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
							LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
							if (liteApp.getInventoryID() == optoutEvents[i].getInventoryID() && liteApp.getProgramID() > 0) {
								boolean programAdded = false;
								for (int k = 0; k < relatedEvent.getProgramIDCount(); k++) {
									if (relatedEvent.getProgramID(k) == liteApp.getProgramID()) {
										programAdded = true;
										break;
									}
								}
								if (!programAdded)
									relatedEvent.addProgramID( liteApp.getProgramID() );
							}
						}
					}
				}
			}
		}
		
		return starsProgHist;
	}
	
	public static StarsWebConfig createStarsWebConfig(LiteWebConfiguration liteWebConfig) {
		StarsWebConfig starsWebConfig = new StarsWebConfig();
		starsWebConfig.setLogoLocation( StarsUtils.forceNotNull(liteWebConfig.getLogoLocation()) );
		starsWebConfig.setDescription( StarsUtils.forceNotNull(liteWebConfig.getDescription()) );
		starsWebConfig.setAlternateDisplayName( StarsUtils.forceNotNull(liteWebConfig.getAlternateDisplayName()) );
		starsWebConfig.setURL( StarsUtils.forceNotNull(liteWebConfig.getUrl()) );
		
		return starsWebConfig;
	}
	
	public static void setAddressingGroups(StarsEnrLMProgram starsProg, LiteLMProgramWebPublishing liteProg) {
		starsProg.removeAllAddressingGroup();
		starsProg.addAddressingGroup( (AddressingGroup)StarsFactory.newEmptyStarsCustListEntry(AddressingGroup.class) );
		
		for (int j = 0; j < liteProg.getGroupIDs().length; j++) {
			LiteYukonPAObject litePao = PAOFuncs.getLiteYukonPAO( liteProg.getGroupIDs()[j] );
			
			if (litePao.getType() == PAOGroups.MACRO_GROUP) {
				java.sql.Connection conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
				try {
					GenericMacro[] groups = GenericMacro.getGenericMacros(
							new Integer(litePao.getYukonID()), MacroTypes.GROUP, conn );
					
					for (int k = 0; k < groups.length; k++) {
						int groupID = groups[k].getChildID().intValue();
						AddressingGroup group = new AddressingGroup();
						group.setEntryID( groupID );
						group.setContent( PAOFuncs.getYukonPAOName(groupID) );
						starsProg.addAddressingGroup( group );
					}
				}
				catch (java.sql.SQLException e) {
					CTILogger.error( e.getMessage(), e );
				}
				finally {
					try {
						if (conn != null) conn.close();
					}
					catch (java.sql.SQLException e) {}
				}
			}
			else {
				AddressingGroup group = new AddressingGroup();
				group.setEntryID( liteProg.getGroupIDs()[j] );
				group.setContent( litePao.getPaoName() );
				starsProg.addAddressingGroup( group );
			}
		}
	}
	
	public static StarsApplianceCategory createStarsApplianceCategory(LiteApplianceCategory liteAppCat, LiteStarsEnergyCompany energyCompany) {
		StarsApplianceCategory starsAppCat = new StarsApplianceCategory();
		starsAppCat.setApplianceCategoryID( liteAppCat.getApplianceCategoryID() );
		starsAppCat.setCategoryID( liteAppCat.getCategoryID() );
		starsAppCat.setInherited( !energyCompany.getApplianceCategories().contains(liteAppCat) );
		starsAppCat.setDescription( StarsUtils.forceNotNull(liteAppCat.getDescription()) );
		
		LiteWebConfiguration liteConfig = StarsDatabaseCache.getInstance().getWebConfiguration( liteAppCat.getWebConfigurationID() );
		StarsWebConfig starsConfig = createStarsWebConfig( liteConfig );
		starsAppCat.setStarsWebConfig( starsConfig );
		
		for (int i = 0; i < liteAppCat.getPublishedPrograms().size(); i++) {
			LiteLMProgramWebPublishing liteProg = (LiteLMProgramWebPublishing) liteAppCat.getPublishedPrograms().get(i);
			
			StarsEnrLMProgram starsProg = new StarsEnrLMProgram();
			starsProg.setProgramID( liteProg.getProgramID() );
			starsProg.setDeviceID( liteProg.getDeviceID() );
			if (liteProg.getDeviceID() > 0)
				starsProg.setYukonName( PAOFuncs.getYukonPAOName(liteProg.getDeviceID()) );
			
			liteConfig = StarsDatabaseCache.getInstance().getWebConfiguration( liteProg.getWebSettingsID() );
			starsProg.setStarsWebConfig( createStarsWebConfig(liteConfig) );
			
			setAddressingGroups( starsProg, liteProg );
			
			if (liteProg.getChanceOfControlID() != 0) {
				starsProg.setChanceOfControl( (ChanceOfControl) StarsFactory.newStarsCustListEntry(
						YukonListFuncs.getYukonListEntry(liteProg.getChanceOfControlID()), ChanceOfControl.class) );
			}
			
			starsAppCat.addStarsEnrLMProgram( starsProg );
		}
		
		return starsAppCat;
	}
	
	public static StarsUser createStarsUser(LiteYukonUser liteUser, LiteStarsEnergyCompany energyCompany) {
		StarsUser starsUser = new StarsUser();
		starsUser.setUsername( StarsUtils.forceNotNull(liteUser.getUsername()) );
		//starsUser.setPassword( ServerUtils.forceNotNull(liteUser.getPassword()) );
		starsUser.setPassword( "" );
		starsUser.setStatus( StarsMsgUtils.getLoginStatus(liteUser.getStatus()) );
		
		LiteYukonGroup[] custGroups = energyCompany.getResidentialCustomerGroups();
		com.cannontech.database.cache.DefaultDatabaseCache cache =
				com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
		
		synchronized (cache) {
			java.util.List userGroups = (java.util.List) cache.getYukonUserGroupMap().get( liteUser );
			for (int i = 0; i < custGroups.length; i++) {
				if (userGroups.contains( custGroups[i] )) {
					starsUser.setGroupID( custGroups[i].getGroupID() );
					break;
				}
			}
		}
		
		return starsUser;
	}
	
	public static StarsThermostatSchedule createStarsThermostatSchedule(int towID, ArrayList liteEntries) {
		if (liteEntries.size() != 4) return null;
		
		StarsThermostatSchedule starsSched = new StarsThermostatSchedule();
		starsSched.setDay( StarsMsgUtils.getThermDaySetting(towID) );
		
		LiteLMThermostatSeasonEntry liteEntry = (LiteLMThermostatSeasonEntry) liteEntries.get(0);
		starsSched.setTime1( new org.exolab.castor.types.Time(liteEntry.getStartTime() * 1000) );
		starsSched.setTemperature1( liteEntry.getTemperature() );
		
		liteEntry = (LiteLMThermostatSeasonEntry) liteEntries.get(1);
		starsSched.setTime2( new org.exolab.castor.types.Time(liteEntry.getStartTime() * 1000) );
		starsSched.setTemperature2( liteEntry.getTemperature() );
		
		liteEntry = (LiteLMThermostatSeasonEntry) liteEntries.get(2);
		starsSched.setTime3( new org.exolab.castor.types.Time(liteEntry.getStartTime() * 1000) );
		starsSched.setTemperature3( liteEntry.getTemperature() );
		
		liteEntry = (LiteLMThermostatSeasonEntry) liteEntries.get(3);
		starsSched.setTime4( new org.exolab.castor.types.Time(liteEntry.getStartTime() * 1000) );
		starsSched.setTemperature4( liteEntry.getTemperature() );
		
		return starsSched;
	}
	
	public static StarsThermostatSeason createStarsThermostatSeason(LiteLMThermostatSeason liteSeason, LiteStarsEnergyCompany energyCompany) {
		StarsThermostatSeason starsSeason = new StarsThermostatSeason();
/*		
		Calendar startCal = Calendar.getInstance();
		startCal.setTime( new Date(liteSeason.getStartDate()) );
		startCal.set( Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) );
		starsSeason.setStartDate( new org.exolab.castor.types.Date(startCal.getTime()) );
*/		
		//if (starsConfig.getURL().equalsIgnoreCase("Cool"))	// Temporarily use URL field to define cool/heat mode
		if (liteSeason.getWebConfigurationID() == StarsMsgUtils.YUK_WEB_CONFIG_ID_COOL)
			starsSeason.setMode( StarsThermoModeSettings.COOL );
		else
			starsSeason.setMode( StarsThermoModeSettings.HEAT );
		
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
			ArrayList liteEntries = (ArrayList) entry.getValue();
			
			StarsThermostatSchedule starsSched = createStarsThermostatSchedule( towID.intValue(), liteEntries );
			if (starsSched != null)
				starsSeason.addStarsThermostatSchedule( starsSched );
		}
		
		return starsSeason;
	}
	
	public static StarsThermostatManualEvent createStarsThermostatManualEvent(LiteLMThermostatManualEvent liteEvent) {
		StarsThermostatManualEvent starsEvent = new StarsThermostatManualEvent();
		setStarsLMCustomerEvent(starsEvent, liteEvent);
		
		ThermostatManualOption starsOption = new ThermostatManualOption();
		starsOption.setTemperature( liteEvent.getPreviousTemperature() );
		starsOption.setHold( liteEvent.isHoldTemperature() );
		starsOption.setMode( StarsMsgUtils.getThermModeSetting(liteEvent.getOperationStateID()) );
		starsOption.setFan( StarsMsgUtils.getThermFanSetting(liteEvent.getFanOperationID()) );
		starsEvent.setThermostatManualOption( starsOption );
		
		return starsEvent;
	}
	
	public static StarsAppliance createStarsAppliance(LiteStarsAppliance liteApp, LiteStarsEnergyCompany energyCompany) {
		StarsAppliance starsApp = new StarsAppliance();
        
		starsApp.setApplianceID( liteApp.getApplianceID() );
		starsApp.setApplianceCategoryID( liteApp.getApplianceCategoryID() );
		starsApp.setInventoryID( liteApp.getInventoryID() );
		starsApp.setProgramID( liteApp.getProgramID() );
		starsApp.setLoadNumber( liteApp.getLoadNumber() );
		
		starsApp.setNotes( StarsUtils.forceNotNull(liteApp.getNotes()) );
		starsApp.setModelNumber( StarsUtils.forceNotNull(liteApp.getModelNumber()) );
        
		if (liteApp.getKWCapacity() > 0)
			starsApp.setKWCapacity( liteApp.getKWCapacity() );
		if (liteApp.getEfficiencyRating() > 0)
			starsApp.setEfficiencyRating( liteApp.getEfficiencyRating() );
		if (liteApp.getYearManufactured() > 0)
			starsApp.setYearManufactured( liteApp.getYearManufactured() );
       	
		Manufacturer manu = new Manufacturer();
		setStarsCustListEntry( manu, YukonListFuncs.getYukonListEntry(liteApp.getManufacturerID()) );
		starsApp.setManufacturer( manu );
        
		Location loc = new Location();
		setStarsCustListEntry( loc, YukonListFuncs.getYukonListEntry(liteApp.getLocationID()) );
		starsApp.setLocation( loc );
        
		starsApp.setServiceCompany( new ServiceCompany() );
	    
		int appCatDefID = YukonListFuncs.getYukonListEntry( energyCompany.getApplianceCategory(liteApp.getApplianceCategoryID()).getCategoryID() ).getYukonDefID();
		
		if (liteApp.getAirConditioner() != null || appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_AIR_CONDITIONER) {
			LiteStarsAppliance.AirConditioner liteAC = liteApp.getAirConditioner();
			if (liteAC == null) liteAC = new LiteStarsAppliance.AirConditioner();
			
			AirConditioner ac = new AirConditioner();
			ac.setTonnage(
				(Tonnage) StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry( liteAC.getTonnageID() ),
					Tonnage.class)
			);
			ac.setACType(
				(ACType) StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry( liteAC.getTypeID() ),
					ACType.class)
			);
			starsApp.setAirConditioner( ac );
		}
		else if (liteApp.getWaterHeater() != null || appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER) {
			LiteStarsAppliance.WaterHeater liteWH = liteApp.getWaterHeater();
			if (liteWH == null) liteWH = new LiteStarsAppliance.WaterHeater();
			
			WaterHeater wh = new WaterHeater();
			wh.setNumberOfGallons(
				(NumberOfGallons) StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry( liteWH.getNumberOfGallonsID() ),
					NumberOfGallons.class)
			);
			wh.setEnergySource(
				(EnergySource) StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry( liteWH.getEnergySourceID() ),
					EnergySource.class)
			);
			if (liteWH.getNumberOfElements() > 0)
				wh.setNumberOfElements( liteWH.getNumberOfElements() );
			starsApp.setWaterHeater( wh );
		}
		else if (liteApp.getDualFuel() != null || appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL) {
			LiteStarsAppliance.DualFuel liteDF = liteApp.getDualFuel();
			if (liteDF == null) liteDF = new LiteStarsAppliance.DualFuel();
			
			DualFuel df = new DualFuel();
			df.setSwitchOverType(
				(SwitchOverType) StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry( liteDF.getSwitchOverTypeID() ),
					SwitchOverType.class)
			);
			if (liteDF.getSecondaryKWCapacity() > 0)
				df.setSecondaryKWCapacity( liteDF.getSecondaryKWCapacity() );
			df.setSecondaryEnergySource(
				(SecondaryEnergySource) StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry( liteDF.getSecondaryEnergySourceID() ),
					SecondaryEnergySource.class)
			);
			starsApp.setDualFuel( df );
		}
		else if (liteApp.getGenerator() != null || appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GENERATOR) {
			LiteStarsAppliance.Generator liteGen = liteApp.getGenerator();
			if (liteGen == null) liteGen = new LiteStarsAppliance.Generator();
			
			Generator gen = new Generator();
			gen.setTransferSwitchType(
				(TransferSwitchType) StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry( liteGen.getTransferSwitchTypeID() ),
					TransferSwitchType.class)
			);
			gen.setTransferSwitchManufacturer(
				(TransferSwitchManufacturer) StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry( liteGen.getTransferSwitchMfgID() ),
					TransferSwitchManufacturer.class)
			);
			if (liteGen.getPeakKWCapacity() > 0)
				gen.setPeakKWCapacity( liteGen.getPeakKWCapacity() );
			if (liteGen.getFuelCapGallons() > 0)
				gen.setFuelCapGallons( liteGen.getFuelCapGallons() );
			if (liteGen.getStartDelaySeconds() > 0)
				gen.setStartDelaySeconds( liteGen.getStartDelaySeconds() );
			starsApp.setGenerator( gen );
		}
		else if (liteApp.getGrainDryer() != null || appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GRAIN_DRYER) {
			LiteStarsAppliance.GrainDryer liteGDry = liteApp.getGrainDryer();
			if (liteGDry == null) liteGDry = new LiteStarsAppliance.GrainDryer();
			
			GrainDryer gd = new GrainDryer();
			gd.setDryerType(
				(DryerType) StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry( liteGDry.getDryerTypeID() ),
					DryerType.class)
			);
			gd.setBinSize(
				(BinSize) StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry( liteGDry.getBinSizeID() ),
					BinSize.class)
			);
			gd.setBlowerEnergySource(
				(BlowerEnergySource) StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry( liteGDry.getBlowerEnergySourceID() ),
					BlowerEnergySource.class)
			);
			gd.setBlowerHorsePower(
				(BlowerHorsePower) StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry( liteGDry.getBlowerHorsePowerID() ),
					BlowerHorsePower.class)
			);
			gd.setBlowerHeatSource(
				(BlowerHeatSource) StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry( liteGDry.getBlowerHeatSourceID() ),
					BlowerHeatSource.class)
			);
			starsApp.setGrainDryer( gd );
		}
		else if (liteApp.getStorageHeat() != null || appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT) {
			LiteStarsAppliance.StorageHeat liteSH = liteApp.getStorageHeat();
			if (liteSH == null) liteSH = new LiteStarsAppliance.StorageHeat();
			
			StorageHeat sh = new StorageHeat();
			sh.setStorageType(
				(StorageType) StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry( liteSH.getStorageTypeID() ),
					StorageType.class)
			);
			if (liteSH.getPeakKWCapacity() > 0)
				sh.setPeakKWCapacity( liteSH.getPeakKWCapacity() );
			if (liteSH.getHoursToRecharge() > 0)
				sh.setHoursToRecharge( liteSH.getHoursToRecharge() );
			starsApp.setStorageHeat( sh );
		}
		else if (liteApp.getHeatPump() != null || appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_HEAT_PUMP) {
			LiteStarsAppliance.HeatPump liteHP = liteApp.getHeatPump();
			if (liteHP == null) liteHP = new LiteStarsAppliance.HeatPump();
			
			HeatPump hp = new HeatPump();
			hp.setPumpType(
				(PumpType) StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry( liteHP.getPumpTypeID() ),
					PumpType.class)
			);
			hp.setPumpSize(
				(PumpSize) StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry( liteHP.getPumpSizeID() ),
					PumpSize.class)
			);
			hp.setStandbySource(
				(StandbySource) StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry( liteHP.getStandbySourceID() ),
					StandbySource.class)
			);
			if (liteHP.getSecondsDelayToRestart() > 0)
				hp.setRestartDelaySeconds( liteHP.getSecondsDelayToRestart() );
			starsApp.setHeatPump( hp );
		}
		else if (liteApp.getIrrigation() != null || appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_IRRIGATION) {
			LiteStarsAppliance.Irrigation liteIrr = liteApp.getIrrigation();
			if (liteIrr == null) liteIrr = new LiteStarsAppliance.Irrigation();
			
			Irrigation irr = new Irrigation();
			irr.setIrrigationType(
				(IrrigationType) StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry( liteIrr.getIrrigationTypeID() ),
					IrrigationType.class)
			);
			irr.setHorsePower(
				(HorsePower) StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry( liteIrr.getHorsePowerID() ),
					HorsePower.class)
			);
			irr.setEnergySource(
				(EnergySource) StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry( liteIrr.getEnergySourceID() ),
					EnergySource.class)
			);
			irr.setSoilType(
				(SoilType) StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry( liteIrr.getSoilTypeID() ),
					SoilType.class)
			);
			irr.setMeterLocation(
				(MeterLocation) StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry( liteIrr.getMeterLocationID() ),
					MeterLocation.class)
			);
			irr.setMeterVoltage(
				(MeterVoltage) StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry( liteIrr.getMeterVoltageID() ),
					MeterVoltage.class)
			);
			starsApp.setIrrigation( irr );
		}
        
		return starsApp;
	}
	
	public static StarsCustSelectionList createStarsCustSelectionList(YukonSelectionList yukonList) {
		StarsCustSelectionList starsList = new StarsCustSelectionList();
		
		starsList.setListID( yukonList.getListID() );
		starsList.setListName( yukonList.getListName() );
		
		ArrayList entries = yukonList.getYukonListEntries();
		if (entries.size() == 0) {
			// Assign the list a "default" entry if the list is empty
			starsList.addStarsSelectionListEntry( (StarsSelectionListEntry)
					StarsFactory.newEmptyStarsCustListEntry(StarsSelectionListEntry.class) );
		}
		else {
			for (int i = 0; i < entries.size(); i++) {
				YukonListEntry yukonEntry = (YukonListEntry) entries.get(i);
				StarsSelectionListEntry starsEntry = new StarsSelectionListEntry();
				setStarsCustListEntry( starsEntry, yukonEntry );
				starsEntry.setYukonDefID( yukonEntry.getYukonDefID() );
				starsList.addStarsSelectionListEntry( starsEntry );
			}
		}
		
		return starsList;
	}
	
	public static StarsCustomerSelectionLists createStarsCustomerSelectionLists(ArrayList selectionLists) {
		StarsCustomerSelectionLists starsCustSelLists = new StarsCustomerSelectionLists();
		for (int i = 0; i < selectionLists.size(); i++) {
			YukonSelectionList list = (YukonSelectionList) selectionLists.get(i);
			starsCustSelLists.addStarsCustSelectionList( StarsLiteFactory.createStarsCustSelectionList(list) );
		}
        
		return starsCustSelLists;
	}
	
	public static void setStarsEnrollmentPrograms(StarsEnrollmentPrograms starsAppCats, ArrayList liteAppCats, LiteStarsEnergyCompany energyCompany) {
		starsAppCats.removeAllStarsApplianceCategory();
		
		for (int i = 0; i < liteAppCats.size(); i++) {
			LiteApplianceCategory liteAppCat = (LiteApplianceCategory) liteAppCats.get(i);
			starsAppCats.addStarsApplianceCategory(
				StarsLiteFactory.createStarsApplianceCategory(liteAppCat, energyCompany) );
		}
	}
	
	public static void setStarsCustomerFAQs(StarsCustomerFAQs starsCustFAQs, LiteStarsEnergyCompany energyCompany) {
		ArrayList liteFAQs = energyCompany.getAllCustomerFAQs();
		YukonSelectionList subjects = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_CUSTOMER_FAQ_GROUP );
		
		for (int i = 0; i < subjects.getYukonListEntries().size(); i++) {
			YukonListEntry subject = (YukonListEntry) subjects.getYukonListEntries().get(i);
			ArrayList faqs = new ArrayList();
			
			for (int j = 0; j < liteFAQs.size(); j++) {
				LiteCustomerFAQ liteFAQ = (LiteCustomerFAQ) liteFAQs.get(j);
				if (liteFAQ.getSubjectID() == subject.getEntryID())
					faqs.add( liteFAQ );
			}
			
			if (faqs.size() > 0) {
				StarsCustomerFAQGroup starsGrp = new StarsCustomerFAQGroup();
				starsGrp.setSubjectID( subject.getEntryID() );
				starsGrp.setSubject( subject.getEntryText() );
				
				for (int j = 0; j < faqs.size(); j++) {
					LiteCustomerFAQ liteFAQ = (LiteCustomerFAQ) faqs.get(j);
					StarsCustomerFAQ starsFAQ = new StarsCustomerFAQ();
					starsFAQ.setQuestionID( liteFAQ.getQuestionID() );
					starsFAQ.setQuestion( liteFAQ.getQuestion() );
					starsFAQ.setAnswer( liteFAQ.getAnswer() );
					starsGrp.addStarsCustomerFAQ( starsFAQ );
				}
				
				starsCustFAQs.addStarsCustomerFAQGroup( starsGrp );
			}
		}
	}
	
	public static StarsLMProgramEvent createStarsOptOutEvent(OptOutEventQueue.OptOutEvent event, LiteStarsEnergyCompany energyCompany) {
		StarsLMProgramEvent starsEvent = new StarsLMProgramEvent();
		
		YukonListEntry optOutEntry = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION );
		starsEvent.setEventAction( optOutEntry.getEntryText() );
		starsEvent.setYukonDefID( optOutEntry.getYukonDefID() );
		starsEvent.setEventDateTime( new Date(event.getStartDateTime()) );
		starsEvent.setDuration( event.getPeriod() );
		starsEvent.setNotes( "" );
		
		LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation( event.getAccountID(), true );
		if (event.getInventoryID() != 0) {
			for (int i = 0; i < liteAcctInfo.getAppliances().size(); i++) {
				LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(i);
				if (liteApp.getInventoryID() == event.getInventoryID() && liteApp.getProgramID() > 0) {
					boolean programAdded = false;
					for (int j = 0; j < starsEvent.getProgramIDCount(); j++) {
						if (starsEvent.getProgramID(j) == liteApp.getProgramID()) {
							programAdded = true;
							break;
						}
					}
					if (!programAdded)
						starsEvent.addProgramID( liteApp.getProgramID() );
				}
			}
		}
		else {
			for (int i = 0; i < liteAcctInfo.getPrograms().size(); i++) {
				LiteStarsLMProgram liteProg = (LiteStarsLMProgram) liteAcctInfo.getPrograms().get(i);
				starsEvent.addProgramID( liteProg.getProgramID() );
			}
		}
		
		return starsEvent;
	}
	
	
	public static boolean isIdenticalCustomerContact(LiteContact liteContact, StarsCustomerContact starsContact) {
		if (!StarsUtils.forceNotNull(liteContact.getContLastName()).equals( starsContact.getLastName() )
			|| !StarsUtils.forceNotNull(liteContact.getContFirstName()).equals( starsContact.getFirstName() )
			|| liteContact.getLiteContactNotifications().size() != starsContact.getContactNotificationCount())
			return false;
		
		for (int i = 0; i < liteContact.getLiteContactNotifications().size(); i++) {
			LiteContactNotification liteNotif = (LiteContactNotification) liteContact.getLiteContactNotifications().get(i);
			StarsContactNotification starsNotif = ServletUtils.getContactNotification( starsContact, liteNotif.getNotificationCategoryID() );
			if (starsNotif == null
				|| liteNotif.isDisabled() != starsNotif.getDisabled()
				|| !StarsUtils.forceNotNull(liteNotif.getNotification()).equals( starsNotif.getNotification() ))
				return false;
		}
		
		return true;
	}
	
	public static boolean isIdenticalCustomerAddress(LiteAddress liteAddr, StarsCustomerAddress starsAddr) {
		return (StarsUtils.forceNotNull(liteAddr.getLocationAddress1()).equals( starsAddr.getStreetAddr1() )
				&& StarsUtils.forceNotNull(liteAddr.getLocationAddress2()).equals( starsAddr.getStreetAddr2() )
				&& StarsUtils.forceNotNull(liteAddr.getCityName()).equals( starsAddr.getCity() )
				&& StarsUtils.forceNotNull(liteAddr.getStateCode()).equals( starsAddr.getState() )
				&& StarsUtils.forceNotNull(liteAddr.getZipCode()).equals( starsAddr.getZip() )
				&& StarsUtils.forceNotNull(liteAddr.getCounty()).equals( StarsUtils.forceNotNull(starsAddr.getCounty()) ));
	}
	
	public static boolean isIdenticalSiteInformation(LiteSiteInformation liteSite, StarsSiteInformation starsSite) {
		return (StarsUtils.forceNotNull(liteSite.getFeeder()).equals( starsSite.getFeeder() )
				&& StarsUtils.forceNotNull(liteSite.getPole()).equals( starsSite.getPole() )
				&& StarsUtils.forceNotNull(liteSite.getTransformerSize()).equals( starsSite.getTransformerSize() )
				&& StarsUtils.forceNotNull(liteSite.getServiceVoltage()).equals( starsSite.getServiceVoltage() )
				&& liteSite.getSubstationID() == starsSite.getSubstation().getEntryID());
	}
	
	public static boolean isIdenticalCustomerAccount(LiteCustomerAccount liteAccount, StarsCustAccount starsAccount) {
		return (StarsUtils.forceNotNull(liteAccount.getAccountNumber()).equals( starsAccount.getAccountNumber() )
				&& StarsUtils.forceNotNull(liteAccount.getAccountNotes()).equals( starsAccount.getAccountNotes() ));
	}
	
	public static boolean isIdenticalCustomer(LiteCustomer liteCustomer, StarsCustAccount starsCustomer) {
		return ((starsCustomer.getIsCommercial() && liteCustomer.getCustomerTypeID() == CustomerTypes.CUSTOMER_CI
					|| !starsCustomer.getIsCommercial() && liteCustomer.getCustomerTypeID() == CustomerTypes.CUSTOMER_RESIDENTIAL)
				&& (starsCustomer.getTimeZone() == null || liteCustomer.getTimeZone().equalsIgnoreCase( starsCustomer.getTimeZone() )));
	}
	
	public static boolean isIdenticalAccountSite(LiteAccountSite liteAcctSite, StarsCustAccount starsAcctSite) {
		return (StarsUtils.forceNotNull(liteAcctSite.getSiteNumber()).equals( starsAcctSite.getPropertyNumber() )
				&& StarsUtils.forceNotNull(liteAcctSite.getPropertyNotes()).equals( starsAcctSite.getPropertyNotes() ));
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
