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
import com.cannontech.common.util.CtiUtilities;
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
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod;
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
			lite = new LiteLMProgram();
			setLiteLMProgram( (LiteLMProgram) lite, (com.cannontech.database.db.stars.LMProgramWebPublishing) db );
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
	}
	
	public static void setLiteStarsLMHardware(LiteStarsLMHardware liteHw, com.cannontech.database.data.stars.hardware.LMHardwareBase hw) {
		setLiteInventoryBase( liteHw, hw.getInventoryBase() );
		liteHw.setManufacturerSerialNumber( hw.getLMHardwareBase().getManufacturerSerialNumber() );
		liteHw.setLmHardwareTypeID( hw.getLMHardwareBase().getLMHardwareTypeID().intValue() );
	}
	
	public static void extendLiteInventoryBase(LiteInventoryBase liteInv, LiteStarsEnergyCompany energyCompany) {
		ArrayList invHist = liteInv.getInventoryHistory();
		invHist.clear();
		
		com.cannontech.database.data.stars.event.LMHardwareEvent[] events =
				com.cannontech.database.data.stars.event.LMHardwareEvent.getAllLMHardwareEvents( new Integer(liteInv.getInventoryID()) );
		for (int i = 0; i < events.length; i++) {
			LiteLMHardwareEvent liteEvent = (LiteLMHardwareEvent) createLite( events[i] );
			invHist.add( liteEvent );
		}
		
		liteInv.updateDeviceStatus();
		
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
		liteEvent.setProgramID( event.getLMProgramEvent().getLMProgramID().intValue() );
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
		liteApp.setLmProgramID( app.getApplianceBase().getLMProgramID().intValue() );
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
		}
	}
	
	public static void setLiteAppAirConditioner(LiteStarsAppAirConditioner liteAppAC, com.cannontech.database.db.stars.appliance.ApplianceAirConditioner appAC) {
		liteAppAC.setTonnageID( appAC.getTonnageID().intValue() );
		liteAppAC.setTypeID( appAC.getTypeID().intValue() );
		liteAppAC.setExtended( true );
	}
	
	public static void setLiteAppWaterHeater(LiteStarsAppWaterHeater liteAppWH, com.cannontech.database.db.stars.appliance.ApplianceWaterHeater appWH) {
		liteAppWH.setNumberOfGallonsID( appWH.getNumberOfGallonsID().intValue() );
		liteAppWH.setEnergySourceID( appWH.getEnergySourceID().intValue() );
		liteAppWH.setNumberOfElements( appWH.getNumberOfElements().intValue() );
		liteAppWH.setExtended( true );
	}
	
	public static void setLiteAppDualFuel(LiteStarsAppDualFuel liteAppDF, com.cannontech.database.db.stars.appliance.ApplianceDualFuel appDF) {
		liteAppDF.setSwitchOverTypeID( appDF.getSwitchOverTypeID().intValue() );
		liteAppDF.setSecondaryKWCapacity( appDF.getSecondaryKWCapacity().intValue() );
		liteAppDF.setSecondaryEnergySourceID( appDF.getSecondaryEnergySourceID().intValue() );
		liteAppDF.setExtended( true );
	}
	
	public static void setLiteAppGenerator(LiteStarsAppGenerator liteAppGen, com.cannontech.database.db.stars.appliance.ApplianceGenerator appGen) {
		liteAppGen.setTransferSwitchTypeID( appGen.getTransferSwitchTypeID().intValue() );
		liteAppGen.setTransferSwitchMfgID( appGen.getTransferSwitchMfgID().intValue() );
		liteAppGen.setPeakKWCapacity( appGen.getPeakKWCapacity().intValue() );
		liteAppGen.setFuelCapGallons( appGen.getFuelCapGallons().intValue() );
		liteAppGen.setStartDelaySeconds( appGen.getStartDelaySeconds().intValue() );
		liteAppGen.setExtended( true );
	}
	
	public static void setLiteAppGrainDryer(LiteStarsAppGrainDryer liteAppGD, com.cannontech.database.db.stars.appliance.ApplianceGrainDryer appGD) {
		liteAppGD.setDryerTypeID( appGD.getDryerTypeID().intValue() );
		liteAppGD.setBinSizeID( appGD.getBinSizeID().intValue() );
		liteAppGD.setBlowerEnergySourceID( appGD.getBlowerEnergySourceID().intValue() );
		liteAppGD.setBlowerHorsePowerID( appGD.getBlowerHorsePowerID().intValue() );
		liteAppGD.setBlowerHeatSourceID( appGD.getBlowerHeatSourceID().intValue() );
		liteAppGD.setExtended( true );
	}
	
	public static void setLiteAppStorageHeat(LiteStarsAppStorageHeat liteAppSH, com.cannontech.database.db.stars.appliance.ApplianceStorageHeat appSH) {
		liteAppSH.setStorageTypeID( appSH.getStorageTypeID().intValue() );
		liteAppSH.setPeakKWCapacity( appSH.getPeakKWCapacity().intValue() );
		liteAppSH.setHoursToRecharge( appSH.getHoursToRecharge().intValue() );
		liteAppSH.setExtended( true );
	}
	
	public static void setLiteAppHeatPump(LiteStarsAppHeatPump liteAppHP, com.cannontech.database.db.stars.appliance.ApplianceHeatPump appHP) {
		liteAppHP.setPumpTypeID( appHP.getPumpTypeID().intValue() );
		liteAppHP.setStandbySourceID( appHP.getStandbySourceID().intValue() );
		liteAppHP.setSecondsDelayToRestart( appHP.getSecondsDelayToRestart().intValue() );
		liteAppHP.setExtended( true );
	}
	
	public static void setLiteAppIrrigation(LiteStarsAppIrrigation liteAppIrr, com.cannontech.database.db.stars.appliance.ApplianceIrrigation appIrr) {
		liteAppIrr.setIrrigationTypeID( appIrr.getIrrigationTypeID().intValue() );
		liteAppIrr.setHorsePowerID( appIrr.getHorsePowerID().intValue() );
		liteAppIrr.setEnergySourceID( appIrr.getEnergySourceID().intValue() );
		liteAppIrr.setSoilTypeID( appIrr.getSoilTypeID().intValue() );
		liteAppIrr.setMeterLocationID( appIrr.getMeterLocationID().intValue() );
		liteAppIrr.setMeterVoltageID( appIrr.getMeterVoltageID().intValue() );
		liteAppIrr.setExtended( true );
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
		LiteStarsAppliance liteApp = null;
		
        LiteApplianceCategory liteAppCat = energyCompany.getApplianceCategory( appliance.getApplianceBase().getApplianceCategoryID().intValue() );
        if (liteAppCat.getCategoryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_AIR_CONDITIONER).getEntryID()) {
        	liteApp = new LiteStarsAppAirConditioner();
        	com.cannontech.database.db.stars.appliance.ApplianceAirConditioner app =
        			com.cannontech.database.db.stars.appliance.ApplianceAirConditioner.getApplianceAirConditioner( appliance.getApplianceBase().getApplianceID() );
        	if (app != null)
            	StarsLiteFactory.setLiteAppAirConditioner( (LiteStarsAppAirConditioner) liteApp, app );
        }
        else if (liteAppCat.getCategoryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER).getEntryID()) {
        	liteApp = new LiteStarsAppWaterHeater();
        	com.cannontech.database.db.stars.appliance.ApplianceWaterHeater app =
        			com.cannontech.database.db.stars.appliance.ApplianceWaterHeater.getApplianceWaterHeater( appliance.getApplianceBase().getApplianceID() );
        	if (app != null)
            	StarsLiteFactory.setLiteAppWaterHeater( (LiteStarsAppWaterHeater) liteApp, app );
        }
        else if (liteAppCat.getCategoryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL).getEntryID()) {
        	liteApp = new LiteStarsAppDualFuel();
        	com.cannontech.database.db.stars.appliance.ApplianceDualFuel app =
        			com.cannontech.database.db.stars.appliance.ApplianceDualFuel.getApplianceDualFuel( appliance.getApplianceBase().getApplianceID() );
        	if (app != null)
            	StarsLiteFactory.setLiteAppDualFuel( (LiteStarsAppDualFuel) liteApp, app );
        }
        else if (liteAppCat.getCategoryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER).getEntryID()) {
        	liteApp = new LiteStarsAppGenerator();
        	com.cannontech.database.db.stars.appliance.ApplianceGenerator app =
        			com.cannontech.database.db.stars.appliance.ApplianceGenerator.getApplianceGenerator( appliance.getApplianceBase().getApplianceID() );
        	if (app != null)
            	StarsLiteFactory.setLiteAppGenerator( (LiteStarsAppGenerator) liteApp, app );
        }
        else if (liteAppCat.getCategoryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GRAIN_DRYER).getEntryID()) {
        	liteApp = new LiteStarsAppGrainDryer();
        	com.cannontech.database.db.stars.appliance.ApplianceGrainDryer app =
        			com.cannontech.database.db.stars.appliance.ApplianceGrainDryer.getApplianceGrainDryer( appliance.getApplianceBase().getApplianceID() );
        	if (app != null)
            	StarsLiteFactory.setLiteAppGrainDryer( (LiteStarsAppGrainDryer) liteApp, app );
        }
        else if (liteAppCat.getCategoryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT).getEntryID()) {
        	liteApp = new LiteStarsAppStorageHeat();
        	com.cannontech.database.db.stars.appliance.ApplianceStorageHeat app =
        			com.cannontech.database.db.stars.appliance.ApplianceStorageHeat.getApplianceStorageHeat( appliance.getApplianceBase().getApplianceID() );
        	if (app != null)
            	StarsLiteFactory.setLiteAppStorageHeat( (LiteStarsAppStorageHeat) liteApp, app );
        }
        else if (liteAppCat.getCategoryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_HEAT_PUMP).getEntryID()) {
        	liteApp = new LiteStarsAppHeatPump();
        	com.cannontech.database.db.stars.appliance.ApplianceHeatPump app =
        			com.cannontech.database.db.stars.appliance.ApplianceHeatPump.getApplianceHeatPump( appliance.getApplianceBase().getApplianceID() );
        	if (app != null)
            	StarsLiteFactory.setLiteAppHeatPump( (LiteStarsAppHeatPump) liteApp, app );
        }
        else if (liteAppCat.getCategoryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_IRRIGATION).getEntryID()) {
        	liteApp = new LiteStarsAppIrrigation();
        	com.cannontech.database.db.stars.appliance.ApplianceIrrigation app =
        			com.cannontech.database.db.stars.appliance.ApplianceIrrigation.getApplianceIrrigation( appliance.getApplianceBase().getApplianceID() );
        	if (app != null)
            	StarsLiteFactory.setLiteAppIrrigation( (LiteStarsAppIrrigation) liteApp, app );
        }
        else {
        	liteApp = new LiteStarsAppliance();
        }
        
        setLiteStarsAppliance( liteApp, appliance );
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
	
	public static void setLiteLMProgram(LiteLMProgram liteProg, com.cannontech.database.db.stars.LMProgramWebPublishing pubProg) {
		liteProg.setProgramID( pubProg.getLMProgramID().intValue() );
		liteProg.setWebSettingsID( pubProg.getWebSettingsID().intValue() );
		liteProg.setChanceOfControlID( pubProg.getChanceOfControlID().intValue() );
		liteProg.setProgramCategory( "LMPrograms" );
		
		com.cannontech.database.data.lite.LiteYukonPAObject progPao =
				com.cannontech.database.cache.functions.PAOFuncs.getLiteYukonPAO( pubProg.getLMProgramID().intValue() );
		if (progPao != null)
			liteProg.setProgramName( progPao.getPaoName() );
		
		try {
			com.cannontech.database.db.device.lm.LMProgramDirectGroup[] groups =
					com.cannontech.database.db.device.lm.LMProgramDirectGroup.getAllDirectGroups( pubProg.getLMProgramID() );
			int[] groupIDs = new int[ groups.length ];
			for (int k = 0; k < groups.length; k++)
				groupIDs[k] = groups[k].getLmGroupDeviceID().intValue();
			liteProg.setGroupIDs( groupIDs );
		}
		catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
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
		app.getApplianceBase().setLMProgramID( new Integer(liteApp.getLmProgramID()) );
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
		}
	}
	
	public static void setApplianceAirConditioner(com.cannontech.database.db.stars.appliance.ApplianceAirConditioner app, LiteStarsAppAirConditioner liteApp) {
		app.setApplianceID( new Integer(liteApp.getApplianceID()) );
		app.setTonnageID( new Integer(liteApp.getTonnageID()) );
		app.setTypeID( new Integer(liteApp.getTypeID()) );
	}
	
	public static void setApplianceWaterHeater(com.cannontech.database.db.stars.appliance.ApplianceWaterHeater app, LiteStarsAppWaterHeater liteApp) {
		app.setApplianceID( new Integer(liteApp.getApplianceID()) );
		app.setNumberOfGallonsID( new Integer(liteApp.getNumberOfGallonsID()) );
		app.setEnergySourceID( new Integer(liteApp.getNumberOfGallonsID()) );
		app.setNumberOfElements( new Integer(liteApp.getNumberOfElements()) );
	}
	
	public static void setApplianceDualFuel(com.cannontech.database.db.stars.appliance.ApplianceDualFuel app, LiteStarsAppDualFuel liteApp) {
		app.setApplianceID( new Integer(liteApp.getApplianceID()) );
		app.setSwitchOverTypeID( new Integer(liteApp.getSwitchOverTypeID()) );
		app.setSecondaryEnergySourceID( new Integer(liteApp.getSecondaryEnergySourceID()) );
		app.setSecondaryKWCapacity( new Integer(liteApp.getSecondaryKWCapacity()) );
	}
	
	public static void setApplianceGenerator(com.cannontech.database.db.stars.appliance.ApplianceGenerator app, LiteStarsAppGenerator liteApp) {
		app.setApplianceID( new Integer(liteApp.getApplianceID()) );
		app.setTransferSwitchTypeID( new Integer(liteApp.getTransferSwitchTypeID()) );
		app.setTransferSwitchMfgID( new Integer(liteApp.getTransferSwitchMfgID()) );
		app.setPeakKWCapacity( new Integer(liteApp.getPeakKWCapacity()) );
		app.setFuelCapGallons( new Integer(liteApp.getFuelCapGallons()) );
		app.setStartDelaySeconds( new Integer(liteApp.getStartDelaySeconds()) );
	}
	
	public static void setApplianceGrainDryer(com.cannontech.database.db.stars.appliance.ApplianceGrainDryer app, LiteStarsAppGrainDryer liteApp) {
		app.setApplianceID( new Integer(liteApp.getApplianceID()) );
		app.setDryerTypeID( new Integer(liteApp.getDryerTypeID()) );
		app.setBinSizeID( new Integer(liteApp.getBinSizeID()) );
		app.setBlowerEnergySourceID( new Integer(liteApp.getBlowerEnergySourceID()) );
		app.setBlowerHorsePowerID( new Integer(liteApp.getBlowerHorsePowerID()) );
		app.setBlowerHeatSourceID( new Integer(liteApp.getBlowerHeatSourceID()) );
	}
	
	public static void setApplianceStorageHeat(com.cannontech.database.db.stars.appliance.ApplianceStorageHeat app, LiteStarsAppStorageHeat liteApp) {
		app.setApplianceID( new Integer(liteApp.getApplianceID()) );
		app.setStorageTypeID( new Integer(liteApp.getStorageTypeID()) );
		app.setPeakKWCapacity( new Integer(liteApp.getPeakKWCapacity()) );
		app.setHoursToRecharge( new Integer(liteApp.getHoursToRecharge()) );
	}
	
	public static void setApplianceHeatPump(com.cannontech.database.db.stars.appliance.ApplianceHeatPump app, LiteStarsAppHeatPump liteApp) {
		app.setApplianceID( new Integer(liteApp.getApplianceID()) );
		app.setPumpTypeID( new Integer(liteApp.getPumpTypeID()) );
		app.setStandbySourceID( new Integer(liteApp.getStandbySourceID()) );
		app.setSecondsDelayToRestart( new Integer(liteApp.getSecondsDelayToRestart()) );
	}
	
	public static void setApplianceIrrigation(com.cannontech.database.db.stars.appliance.ApplianceIrrigation app, LiteStarsAppIrrigation liteApp) {
		app.setApplianceID( new Integer(liteApp.getApplianceID()) );
		app.setIrrigationTypeID( new Integer(liteApp.getIrrigationTypeID()) );
		app.setHorsePowerID( new Integer(liteApp.getHorsePowerID()) );
		app.setEnergySourceID( new Integer(liteApp.getEnergySourceID()) );
		app.setSoilTypeID( new Integer(liteApp.getSoilTypeID()) );
		app.setMeterLocationID( new Integer(liteApp.getMeterLocationID()) );
		app.setMeterVoltageID( new Integer(liteApp.getMeterVoltageID()) );
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
			com.cannontech.database.data.customer.CICustomerBase ci = new com.cannontech.database.data.customer.CICustomerBase();
			setCICustomerBase( ci, (LiteCICustomer)liteAccount.getCustomer() );
			account.setCustomer( ci );
		}
		else {
			com.cannontech.database.data.customer.Customer customer = new com.cannontech.database.data.customer.Customer();
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

	
	public static void setStarsCustomerContact(StarsCustomerContact starsContact, LiteContact liteContact) {
		starsContact.setContactID( liteContact.getContactID() );
		starsContact.setLastName( ServerUtils.forceNotNull(liteContact.getContLastName()) );
		starsContact.setFirstName( ServerUtils.forceNotNull(liteContact.getContFirstName()) );
		
		LiteContactNotification liteNotifHPhone = ContactFuncs.getContactNotification( liteContact, YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE );
		starsContact.setHomePhone( ServerUtils.getNotification(liteNotifHPhone) );
		
		LiteContactNotification liteNotifWPhone = ContactFuncs.getContactNotification( liteContact, YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE );
		starsContact.setWorkPhone( ServerUtils.getNotification(liteNotifWPhone) );
		
		LiteContactNotification liteNotifEmail = ContactFuncs.getContactNotification( liteContact, YukonListEntryTypes.YUK_ENTRY_ID_EMAIL );
		if (liteNotifEmail != null) {
			starsContact.setEmail( (Email)StarsFactory.newStarsContactNotification(
					liteNotifEmail.getDisableFlag().equalsIgnoreCase("N"), ServerUtils.forceNotNull(liteNotifEmail.getNotification()), Email.class) );
		}
		else {
			starsContact.setEmail( (Email)StarsFactory.newStarsContactNotification(false, "", Email.class) );
		}
	}
	
	public static void setStarsCustomerAddress(StarsCustomerAddress starsAddr, LiteAddress liteAddr) {
		starsAddr.setAddressID( liteAddr.getAddressID() );
		starsAddr.setStreetAddr1( ServerUtils.forceNotNone(liteAddr.getLocationAddress1()) );
		starsAddr.setStreetAddr2( ServerUtils.forceNotNone(liteAddr.getLocationAddress2()) );
		starsAddr.setCity( ServerUtils.forceNotNone(liteAddr.getCityName()) );
		starsAddr.setState( ServerUtils.forceNotNone(liteAddr.getStateCode()) );
		starsAddr.setZip( ServerUtils.forceNotNone(liteAddr.getZipCode()) );
		starsAddr.setCounty( ServerUtils.forceNotNone(liteAddr.getCounty()) );
	}
	
	public static void setStarsLMCustomerEvent(StarsLMCustomerEvent starsEvent, LiteLMCustomerEvent liteEvent) {
		YukonListEntry entry = YukonListFuncs.getYukonListEntry( liteEvent.getActionID() );
		starsEvent.setEventAction( entry.getEntryText() );
		starsEvent.setEventDateTime( new Date(liteEvent.getEventDateTime()) );
		starsEvent.setNotes( ServerUtils.forceNotNull(liteEvent.getNotes()) );
		starsEvent.setYukonDefID( entry.getYukonDefID() );
	}
	
	public static void setStarsThermostatDynamicData(StarsThermostatDynamicData starsDynData, LiteStarsGatewayEndDevice liteDynData, LiteStarsEnergyCompany energyCompany) {
		if (liteDynData.getTimestamp() != 0)
			starsDynData.setLastUpdatedTime( new Date(liteDynData.getTimestamp()) );
		starsDynData.setDisplayedTemperature( liteDynData.getDisplayedTemperature() );
		if (liteDynData.getDisplayedTempUnit() != null)
			starsDynData.setDisplayedTempUnit( liteDynData.getDisplayedTempUnit().equalsIgnoreCase("C") ? "Celsius" : "Fahrenheit" );
		starsDynData.setFan( ECUtils.getThermFanSetting(liteDynData.getFanSwitch()) );
		starsDynData.setMode( ECUtils.getThermModeSetting(liteDynData.getSystemSwitch()) );
		starsDynData.setCoolSetpoint( liteDynData.getCoolSetpoint() );
		starsDynData.setHeatSetpoint( liteDynData.getHeatSetpoint() );
		if (liteDynData.getSetpointStatus() != null &&
			(liteDynData.getSetpointStatus().equalsIgnoreCase("HOLD") ||
			liteDynData.getSetpointStatus().equalsIgnoreCase("VACATION")))
			starsDynData.setSetpointHold( true );
		else
			starsDynData.setSetpointHold( false );
		starsDynData.setLowerCoolSetpointLimit( liteDynData.getLowerCoolSetpointLimit() );
		starsDynData.setUpperHeatSetpointLimit( liteDynData.getUpperHeatSetpointLimit() );
		
		starsDynData.removeAllInfoString();

		// If the current mode is auto, then display that in the text area, and set mode to the last none-auto mode of the thermostat
		StarsThermoModeSettings mode = ECUtils.getThermModeSetting( liteDynData.getSystemSwitch() );
		if (mode != null && mode.getType() == StarsThermoModeSettings.AUTO_TYPE) {
			mode = ECUtils.getThermModeSetting( liteDynData.getLastSystemSwitch() );
			starsDynData.addInfoString( "Mode: AUTO" );
		}
		starsDynData.setMode( mode );
		
		if (liteDynData.getOutdoorTemperature() > 0) {
			String desc = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_GED_OUTDOOR_TEMP ).getEntryText();
			starsDynData.addInfoString( desc + ": " + liteDynData.getOutdoorTemperature() + "&deg;" + ServerUtils.forceNotNull(liteDynData.getDisplayedTempUnit()) );
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
	
	public static void setStarsThermostatSettings(StarsThermoSettings starsSettings, LiteStarsThermostatSettings liteSettings, LiteStarsEnergyCompany energyCompany) {
		starsSettings.setInventoryID( liteSettings.getInventoryID() );
		
		for (int i = 0; i < liteSettings.getThermostatSeasons().size(); i++) {
			LiteLMThermostatSeason liteSeason = (LiteLMThermostatSeason) liteSettings.getThermostatSeasons().get(i);
			StarsThermostatSeason starsSeason = createStarsThermostatSeason( liteSeason, energyCompany );
			starsSettings.addStarsThermostatSeason( starsSeason );
			
			if (liteSettings.getInventoryID() < 0) {
				// Add thermostat season web configuration only to default settings
				starsSeason.setStarsWebConfig( energyCompany.getStarsWebConfig(liteSeason.getWebConfigurationID()) );
			}
		}
		
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
	
	public static void setStarsCustAccountInformation(StarsCustAccountInformation starsAcctInfo, LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany, boolean isOperator) {
		LiteCustomerAccount liteAccount = liteAcctInfo.getCustomerAccount();
		LiteCustomer liteCustomer = liteAcctInfo.getCustomer();
		LiteAccountSite liteAcctSite = liteAcctInfo.getAccountSite();
		LiteSiteInformation liteSiteInfo = liteAcctInfo.getSiteInformation();
		LiteContact liteContact = energyCompany.getContact( liteAcctInfo.getCustomer().getPrimaryContactID(), liteAcctInfo );
		
		StarsCustomerAccount starsAccount = new StarsCustomerAccount();
		starsAccount.setAccountID( liteAccount.getAccountID() );
		starsAccount.setCustomerID( liteAccount.getCustomerID() );
		starsAccount.setAccountNumber( ServerUtils.forceNotNull(liteAccount.getAccountNumber()) );
		starsAccount.setIsCommercial( liteCustomer.getCustomerTypeID() == CustomerTypes.CUSTOMER_CI );
		if (liteCustomer instanceof LiteCICustomer)
			starsAccount.setCompany( ((LiteCICustomer)liteCustomer).getCompanyName() );
		else
			starsAccount.setCompany( "" );
		starsAccount.setAccountNotes( ServerUtils.forceNotNull(liteAccount.getAccountNotes()) );
		starsAccount.setPropertyNumber( ServerUtils.forceNotNull(liteAcctSite.getSiteNumber()) );
		starsAccount.setPropertyNotes( ServerUtils.forceNotNull(liteAcctSite.getPropertyNotes()) );
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
			
			starsProgs.addStarsLMProgram( createStarsLMProgram(liteProg, liteApp, energyCompany) );
		}
		
		ArrayList liteProgHist = liteAcctInfo.getProgramHistory();
		starsProgs.setStarsLMProgramHistory( createStarsLMProgramHistory(liteProgHist) );
		
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
				invMap.put( starsInv.getDeviceLabel(), list );
			}
			list.add( starsInv );
		}
		
		Iterator it = invMap.values().iterator();
		while (it.hasNext()) {
			ArrayList list = (ArrayList) it.next();
			for (int i = 0; i < list.size(); i++)
				starsInvs.addStarsInventory( (StarsInventory)list.get(i) );
		}
		
        if (liteContact.getLoginID() > com.cannontech.user.UserUtils.USER_YUKON_ID) {
	        LiteYukonUser liteUser = com.cannontech.database.cache.functions.YukonUserFuncs.getLiteYukonUser( liteContact.getLoginID() );
			starsAcctInfo.setStarsUser( createStarsUser(liteUser) );
        }
		
		if (isOperator) {
			ArrayList liteApps = liteAcctInfo.getAppliances();
			StarsAppliances starsApps = new StarsAppliances();
			starsAcctInfo.setStarsAppliances( starsApps );
			
			TreeMap appMap = new TreeMap();
			for (int i = 0; i < liteApps.size(); i++) {
				LiteStarsAppliance liteApp = (LiteStarsAppliance) liteApps.get(i);
				StarsAppliance starsApp = (StarsAppliance) createStarsAppliance(liteApp, energyCompany);
				
				ArrayList list = (ArrayList) appMap.get( starsApp.getDescription() );
				if (list == null) {
					list = new ArrayList();
					appMap.put( starsApp.getDescription(), list );
				}
				list.add( starsApp );
			}
			
			it = appMap.values().iterator();
			while (it.hasNext()) {
				ArrayList list = (ArrayList) it.next();
				for (int i = 0; i < list.size(); i++)
					starsApps.addStarsAppliance( (StarsAppliance) list.get(i) );
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
				LiteWorkOrderBase liteOrder = energyCompany.getWorkOrderBase( ((Integer) liteOrders.get(i)).intValue(), true );
				starsOrders.addStarsServiceRequest( createStarsServiceRequest(liteOrder, energyCompany) );
			}
		}
	}
	
	public static void setStarsLMControlHistory(StarsLMControlHistory starsCtrlHist, LiteStarsLMControlHistory liteCtrlHist, StarsCtrlHistPeriod period, boolean getSummary) {
		starsCtrlHist.removeAllControlHistory();
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
        	long lastStopTime = 0;
	        for (int i = startIndex; i < liteCtrlHist.getLmControlHistory().size(); i++) {
	        	LiteLMControlHistory lmCtrlHist = (LiteLMControlHistory) liteCtrlHist.getLmControlHistory().get(i);

                /*
                 * ActiveRestore is defined as below:
                 * N - This is the first entry for any new control.
                 * C - Previous command was repeated extending the current control interval.
                 * T - Control terminated based on time set in load group.
                 * M - Control terminated because of an active restore or terminate command being sent.
                 * O - Control terminated because a new command of a different nature was sent to this group.
                 * L - Time log
                 */
                if (lmCtrlHist.getActiveRestore().equals("N")) {
                	if (Math.abs(lmCtrlHist.getStartDateTime() - lastStartTime) > 1000) {
                		// This is a new control
	                	lastStartTime = lmCtrlHist.getStartDateTime();
	                	lastStopTime = lmCtrlHist.getStopDateTime();
	                	
	                	hist = new ControlHistory();
	                	hist.setStartDateTime( new Date(lmCtrlHist.getStartDateTime()) );
			            hist.setControlDuration( 0 );
	                	starsCtrlHist.addControlHistory( hist );
                	}
                	else {	// This is the continuation of the last control
                		lastStopTime = lmCtrlHist.getStopDateTime();
                	}
                }
                else if (lmCtrlHist.getActiveRestore().equals("C")
                		|| lmCtrlHist.getActiveRestore().equals("L"))
                {
                	if (Math.abs(lmCtrlHist.getStartDateTime() - lastStartTime) < 1000) {
                		if (hist != null)
	                		hist.setControlDuration( (int)(lmCtrlHist.getStopDateTime() - lastStartTime) / 1000 );
                	}
                }
	        	else if (lmCtrlHist.getActiveRestore().equals("M")
	        			|| lmCtrlHist.getActiveRestore().equals("T")
	        			|| lmCtrlHist.getActiveRestore().equals("O"))
	        	{
	        		if (Math.abs(lmCtrlHist.getStartDateTime() - lastStartTime) < 1000) {
	        			lastStopTime = lmCtrlHist.getStopDateTime();
	        			if (hist != null)
				            hist.setControlDuration( (int)(lmCtrlHist.getStopDateTime() - lastStartTime) / 1000 );
	        		}
		            hist = null;
	        	}
	        }
	        
	        starsCtrlHist.setBeingControlled( new Date().getTime() < lastStopTime );
        }
        
        /* This is wrong!!!
         * Now the summary is computed at run time from the control history 
         */
        if (getSummary) {
            ControlSummary summary = new ControlSummary();
            int dailyTime = 0;
            int monthlyTime = 0;
            int seasonalTime = 0;
            int annualTime = 0;
            
            int size = liteCtrlHist.getLmControlHistory().size();
            if (size > 0) {
            	LiteLMControlHistory lastCtrlHist = (LiteLMControlHistory) liteCtrlHist.getLmControlHistory().get(size - 1);
            	dailyTime = (int) lastCtrlHist.getCurrentDailyTime();
            	monthlyTime = (int) lastCtrlHist.getCurrentMonthlyTime();
            	seasonalTime = (int) lastCtrlHist.getCurrentSeasonalTime();
            	annualTime = (int) lastCtrlHist.getCurrentAnnualTime();
            }
            
            summary.setDailyTime( dailyTime );
            summary.setMonthlyTime( monthlyTime );
            summary.setSeasonalTime( seasonalTime );
            summary.setAnnualTime( annualTime );
            starsCtrlHist.setControlSummary( summary );
        }
	}
	
	public static void setStarsEnergyCompany(StarsEnergyCompany starsCompany, LiteStarsEnergyCompany liteCompany) {
		starsCompany.setEnergyCompanyID( liteCompany.getLiteID() );
		starsCompany.setCompanyName( liteCompany.getName() );
		starsCompany.setMainPhoneNumber( "" );
		starsCompany.setMainFaxNumber( "" );
		starsCompany.setEmail( "" );
		starsCompany.setCompanyAddress( (CompanyAddress) StarsFactory.newStarsCustomerAddress(CompanyAddress.class) );
		starsCompany.setTimeZone( liteCompany.getEnergyCompanySetting(EnergyCompanyRole.DEFAULT_TIME_ZONE) );
		
		if (liteCompany.getPrimaryContactID() != CtiUtilities.NONE_ID) {
			LiteContact liteContact = ContactFuncs.getContact( liteCompany.getPrimaryContactID() );
			
			if (liteContact != null) {
				LiteContactNotification liteNotifPhone = ContactFuncs.getContactNotification( liteContact, YukonListEntryTypes.YUK_ENTRY_ID_PHONE );
				starsCompany.setMainPhoneNumber( ServerUtils.getNotification(liteNotifPhone) );
				
				LiteContactNotification liteNotifFax = ContactFuncs.getContactNotification( liteContact, YukonListEntryTypes.YUK_ENTRY_ID_FAX );
				starsCompany.setMainFaxNumber( ServerUtils.getNotification(liteNotifFax) );
				
				LiteContactNotification liteNotifEmail = ContactFuncs.getContactNotification( liteContact, YukonListEntryTypes.YUK_ENTRY_ID_EMAIL );
				starsCompany.setEmail( ServerUtils.getNotification(liteNotifEmail) );
				
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
		starsCompany.setCompanyName( ServerUtils.forceNotNull(liteCompany.getCompanyName()) );
		starsCompany.setMainPhoneNumber( ServerUtils.forceNotNone(liteCompany.getMainPhoneNumber()) );
		starsCompany.setMainFaxNumber( ServerUtils.forceNotNone(liteCompany.getMainFaxNumber()) );
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
	
	public static void setStarsInv(StarsInv starsInv, LiteInventoryBase liteInv, LiteStarsEnergyCompany energyCompany) {
		starsInv.setInventoryID( liteInv.getInventoryID() );
		starsInv.setDeviceID( liteInv.getDeviceID() );
		starsInv.setCategory( YukonListFuncs.getYukonListEntry(liteInv.getCategoryID()).getEntryText() );
		starsInv.setDeviceLabel( ServerUtils.forceNotNull(liteInv.getDeviceLabel()) );
		
		InstallationCompany company = new InstallationCompany();
		company.setEntryID( liteInv.getInstallationCompanyID() );
		LiteServiceCompany liteCompany = energyCompany.getServiceCompany( liteInv.getInstallationCompanyID() );
		if (liteCompany != null)
			company.setContent( ServerUtils.forceNotNull(liteCompany.getCompanyName()) );
		else
			company.setContent( "(none)" );
		starsInv.setInstallationCompany( company );
		
		starsInv.setReceiveDate( ServerUtils.translateDate(liteInv.getReceiveDate()) );
		starsInv.setInstallDate( ServerUtils.translateDate(liteInv.getInstallDate()) );
		starsInv.setRemoveDate( ServerUtils.translateDate(liteInv.getRemoveDate()) );
		starsInv.setAltTrackingNumber( ServerUtils.forceNotNull(liteInv.getAlternateTrackingNumber()) );
		
		Voltage volt = new Voltage();
		volt.setEntryID( liteInv.getVoltageID() );
		volt.setContent( YukonListFuncs.getYukonListEntry(liteInv.getVoltageID()).getEntryText() );
		starsInv.setVoltage( volt );
		
		starsInv.setNotes( ServerUtils.forceNotNull(liteInv.getNotes()) );
		
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
			
			LMHardware hw = new LMHardware();
			hw.setLMHardwareType( (LMHardwareType)StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry(liteHw.getLmHardwareTypeID()),
					LMHardwareType.class) );
			hw.setManufacturerSerialNumber( ServerUtils.forceNotNull(((LiteStarsLMHardware)liteInv).getManufacturerSerialNumber()) );
			
			if (liteHw.getThermostatSettings() != null) {
				StarsThermostatSettings starsSettings = new StarsThermostatSettings();
				setStarsThermostatSettings( starsSettings, liteHw.getThermostatSettings(), energyCompany );
				hw.setStarsThermostatSettings( starsSettings );
			}
			
			starsInv.setLMHardware( hw );
		}
		else if (ECUtils.isMCT( liteInv.getCategoryID() )) {
			MCT mct = new MCT();
			mct.setDeviceName( PAOFuncs.getYukonPAOName(liteInv.getDeviceID()) );
			
			starsInv.setMCT( mct );
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
		starsSite.setFeeder( ServerUtils.forceNotNull(liteSite.getFeeder()) );
		starsSite.setPole( ServerUtils.forceNotNull(liteSite.getPole()) );
		starsSite.setTransformerSize( ServerUtils.forceNotNull(liteSite.getTransformerSize()) );
		starsSite.setServiceVoltage( ServerUtils.forceNotNull(liteSite.getServiceVoltage()) );
		starsSite.setSubstation(
			(Substation) StarsFactory.newStarsCustListEntry(
				energyCompany.getYukonListEntry(
					com.cannontech.database.db.stars.Substation.LISTNAME_SUBSTATION,
					liteSite.getSubstationID()
				),
				Substation.class)
		);
		
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
		
		String orderNo = ServerUtils.forceNotNull(liteOrder.getOrderNumber());
		if (orderNo.startsWith( ServerUtils.AUTO_GEN_NUM_PREC ))
			orderNo = orderNo.substring( ServerUtils.AUTO_GEN_NUM_PREC.length() );
		starsOrder.setOrderNumber( orderNo );
		
		starsOrder.setServiceType(
			(ServiceType) StarsFactory.newStarsCustListEntry(
				YukonListFuncs.getYukonListEntry(liteOrder.getWorkTypeID()),
				ServiceType.class)
		);
		
		ServiceCompany company = new ServiceCompany();
		company.setEntryID( liteOrder.getServiceCompanyID() );
		LiteServiceCompany liteCompany = energyCompany.getServiceCompany( liteOrder.getServiceCompanyID() );
		if (liteCompany != null)
			company.setContent( ServerUtils.forceNotNull(liteCompany.getCompanyName()) );
		else
			company.setContent( "(none)" );
		starsOrder.setServiceCompany( company );
		
		starsOrder.setCurrentState(
			(CurrentState) StarsFactory.newStarsCustListEntry(
				YukonListFuncs.getYukonListEntry(liteOrder.getCurrentStateID()),
				CurrentState.class)
		);
		
		starsOrder.setDateReported( ServerUtils.translateDate(liteOrder.getDateReported()) );
		starsOrder.setDateScheduled( ServerUtils.translateDate(liteOrder.getDateScheduled()) );
		starsOrder.setDateCompleted( ServerUtils.translateDate(liteOrder.getDateCompleted()) );
		starsOrder.setOrderedBy( ServerUtils.forceNotNull(liteOrder.getOrderedBy()) );
		starsOrder.setDescription( ServerUtils.forceNotNull(liteOrder.getDescription()) );
		starsOrder.setActionTaken( ServerUtils.forceNotNull(liteOrder.getActionTaken()) );
		
		return starsOrder;
	}
	
	public static StarsLMControlHistory createStarsLMControlHistory(LiteStarsLMControlHistory liteCtrlHist, StarsCtrlHistPeriod period, boolean getSummary) {
        StarsLMControlHistory starsCtrlHist = new StarsLMControlHistory();
        setStarsLMControlHistory( starsCtrlHist, liteCtrlHist, period, getSummary );
        return starsCtrlHist;
	}
	
	public static StarsLMProgram createStarsLMProgram(LiteStarsLMProgram liteProg, LiteStarsAppliance liteApp, LiteStarsEnergyCompany energyCompany) {
		StarsLMProgram starsProg = new StarsLMProgram();
		starsProg.setProgramID( liteProg.getLmProgram().getProgramID() );
		starsProg.setGroupID( liteProg.getGroupID() );
		starsProg.setProgramName( ServerUtils.forceNotNull(liteProg.getLmProgram().getProgramName()) );
		starsProg.setApplianceCategoryID( liteApp.getApplianceCategoryID() );

		// AlternativeDisplayName field: (program alias),(short name used in enrollment page)
		StarsWebConfig starsConfig = energyCompany.getStarsWebConfig( liteProg.getLmProgram().getWebSettingsID() );
		String[] dispNames = starsConfig.getAlternateDisplayName().split(",");
		if (dispNames.length > 0 && dispNames[0].length() > 0)
			starsProg.setProgramName( dispNames[0] );
		
		LiteStarsLMControlHistory liteCtrlHist = energyCompany.getLMControlHistory( liteProg.getGroupID() );
		if (liteCtrlHist != null)
			starsProg.setStarsLMControlHistory( energyCompany.getStarsLMControlHistory(liteCtrlHist) );
		else
			starsProg.setStarsLMControlHistory( new StarsLMControlHistory() );
		
		if (liteProg.isInService())
			starsProg.setStatus( ServletUtils.IN_SERVICE );
		else
			starsProg.setStatus( ServletUtils.OUT_OF_SERVICE );
		
		return starsProg;
	}
	
	public static StarsLMProgramHistory createStarsLMProgramHistory(ArrayList liteProgHist) {
		StarsLMProgramHistory starsProgHist = new StarsLMProgramHistory();
		ArrayList liteProgHist2 = new ArrayList( liteProgHist );
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
		
		return starsProgHist;
	}
	
	public static StarsWebConfig createStarsWebConfig(LiteWebConfiguration liteWebConfig) {
		StarsWebConfig starsWebConfig = new StarsWebConfig();
		starsWebConfig.setLogoLocation( ServerUtils.forceNotNull(liteWebConfig.getLogoLocation()) );
		starsWebConfig.setDescription( ServerUtils.forceNotNull(liteWebConfig.getDescription()) );
		starsWebConfig.setAlternateDisplayName( ServerUtils.forceNotNull(liteWebConfig.getAlternateDisplayName()) );
		starsWebConfig.setURL( ServerUtils.forceNotNull(liteWebConfig.getUrl()) );
		
		return starsWebConfig;
	}
	
	public static StarsApplianceCategory createStarsApplianceCategory(LiteApplianceCategory liteAppCat, LiteStarsEnergyCompany energyCompany) {
		StarsApplianceCategory starsAppCat = new StarsApplianceCategory();
		starsAppCat.setApplianceCategoryID( liteAppCat.getApplianceCategoryID() );
		starsAppCat.setCategoryID( liteAppCat.getCategoryID() );
		starsAppCat.setDescription( ServerUtils.forceNotNull(liteAppCat.getDescription()) );
		starsAppCat.setStarsWebConfig( energyCompany.getStarsWebConfig(liteAppCat.getWebConfigurationID()) );
		
		if (liteAppCat.getPublishedPrograms() != null) {
			for (int i = 0; i < liteAppCat.getPublishedPrograms().length; i++) {
				LiteLMProgram liteProg = liteAppCat.getPublishedPrograms()[i];
				
				StarsEnrLMProgram starsProg = new StarsEnrLMProgram();
				starsProg.setProgramID( liteProg.getProgramID() );
				starsProg.setProgramName( liteProg.getProgramName() );
				starsProg.setStarsWebConfig( energyCompany.getStarsWebConfig(liteProg.getWebSettingsID()) );
				
				for (int j = 0; j < liteProg.getGroupIDs().length; j++) {
	    			String groupName = com.cannontech.database.cache.functions.PAOFuncs.getYukonPAOName( liteProg.getGroupIDs()[j] );
	    			AddressingGroup group = new AddressingGroup();
	    			group.setEntryID( liteProg.getGroupIDs()[j] );
	    			group.setContent( groupName );
	    			starsProg.addAddressingGroup( group );
				}
				
				if (liteProg.getChanceOfControlID() != 0) {
					starsProg.setChanceOfControl( (ChanceOfControl) StarsFactory.newStarsCustListEntry(
							YukonListFuncs.getYukonListEntry(liteProg.getChanceOfControlID()), ChanceOfControl.class) );
				}
				
				starsAppCat.addStarsEnrLMProgram( starsProg );
			}
		}
		
		return starsAppCat;
	}
	
	public static StarsUser createStarsUser(com.cannontech.database.data.lite.LiteYukonUser liteUser) {
		StarsUser starsUser = new StarsUser();
		starsUser.setUsername( ServerUtils.forceNotNull(liteUser.getUsername()) );
		starsUser.setPassword( ServerUtils.forceNotNull(liteUser.getPassword()) );
		
		return starsUser;
	}
	
	public static StarsThermostatSchedule createStarsThermostatSchedule(int towID, ArrayList liteEntries) {
		if (liteEntries.size() != 4) return null;
		
		StarsThermostatSchedule starsSched = new StarsThermostatSchedule();
		starsSched.setDay( ECUtils.getThermDaySetting(towID) );
		
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
		if (liteSeason.getWebConfigurationID() == ECUtils.YUK_WEB_CONFIG_ID_COOL)
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
		starsOption.setMode( ECUtils.getThermModeSetting(liteEvent.getOperationStateID()) );
		starsOption.setFan( ECUtils.getThermFanSetting(liteEvent.getFanOperationID()) );
		starsEvent.setThermostatManualOption( starsOption );
		
		return starsEvent;
	}
	
	public static StarsAppliance createStarsAppliance(LiteStarsAppliance liteApp, LiteStarsEnergyCompany energyCompany) {
        StarsAppliance starsApp = new StarsAppliance();
        
        starsApp.setApplianceID( liteApp.getApplianceID() );
        starsApp.setApplianceCategoryID( liteApp.getApplianceCategoryID() );
    	starsApp.setInventoryID( liteApp.getInventoryID() );
    	starsApp.setLmProgramID( liteApp.getLmProgramID() );
        starsApp.setNotes( ServerUtils.forceNotNull(liteApp.getNotes()) );
        starsApp.setModelNumber( ServerUtils.forceNotNull(liteApp.getModelNumber()) );
        starsApp.setKWCapacity( liteApp.getKWCapacity() );
        starsApp.setEfficiencyRating( liteApp.getEfficiencyRating() );
        
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
        
        LiteApplianceCategory liteAppCat = energyCompany.getApplianceCategory( liteApp.getApplianceCategoryID() );
        if (liteAppCat != null)
	        starsApp.setDescription( ServerUtils.forceNotNull(liteAppCat.getDescription()) );
	    else
	    	starsApp.setDescription( "(Unknown)" );
	    
	    if (liteApp instanceof LiteStarsAppAirConditioner) {
	    	AirConditioner ac = new AirConditioner();
	    	ac.setTonnage(
	    		(Tonnage) StarsFactory.newStarsCustListEntry(
	    			YukonListFuncs.getYukonListEntry( ((LiteStarsAppAirConditioner) liteApp).getTonnageID() ),
	    			Tonnage.class)
	    	);
	    	ac.setACType(
	    		(ACType) StarsFactory.newStarsCustListEntry(
	    			YukonListFuncs.getYukonListEntry( ((LiteStarsAppAirConditioner) liteApp).getTypeID() ),
	    			ACType.class)
	    	);
	    	starsApp.setAirConditioner( ac );
	    }
	    else if (liteApp instanceof LiteStarsAppWaterHeater) {
	    	WaterHeater wh = new WaterHeater();
	    	wh.setNumberOfGallons(
	    		(NumberOfGallons) StarsFactory.newStarsCustListEntry(
	    			YukonListFuncs.getYukonListEntry( ((LiteStarsAppWaterHeater) liteApp).getNumberOfGallonsID() ),
	    			NumberOfGallons.class)
	    	);
	    	wh.setEnergySource(
	    		(EnergySource) StarsFactory.newStarsCustListEntry(
	    			YukonListFuncs.getYukonListEntry( ((LiteStarsAppWaterHeater) liteApp).getEnergySourceID() ),
	    			EnergySource.class)
	    	);
	    	wh.setNumberOfElements( ((LiteStarsAppWaterHeater) liteApp).getNumberOfElements() );
	    	starsApp.setWaterHeater( wh );
	    }
	    else if (liteApp instanceof LiteStarsAppDualFuel) {
	    	DualFuel df = new DualFuel();
	    	df.setSwitchOverType(
	    		(SwitchOverType) StarsFactory.newStarsCustListEntry(
	    			YukonListFuncs.getYukonListEntry( ((LiteStarsAppDualFuel) liteApp).getSwitchOverTypeID() ),
	    			SwitchOverType.class)
	    	);
	    	df.setSecondaryKWCapacity( ((LiteStarsAppDualFuel) liteApp).getSecondaryKWCapacity() );
	    	df.setSecondaryEnergySource(
	    		(SecondaryEnergySource) StarsFactory.newStarsCustListEntry(
	    			YukonListFuncs.getYukonListEntry( ((LiteStarsAppDualFuel) liteApp).getSecondaryEnergySourceID() ),
	    			SecondaryEnergySource.class)
	    	);
	    	starsApp.setDualFuel( df );
	    }
	    else if (liteApp instanceof LiteStarsAppGenerator) {
	    	Generator gen = new Generator();
	    	gen.setTransferSwitchType(
	    		(TransferSwitchType) StarsFactory.newStarsCustListEntry(
	    			YukonListFuncs.getYukonListEntry( ((LiteStarsAppGenerator) liteApp).getTransferSwitchTypeID() ),
	    			TransferSwitchType.class)
	    	);
	    	gen.setTransferSwitchManufacturer(
	    		(TransferSwitchManufacturer) StarsFactory.newStarsCustListEntry(
	    			YukonListFuncs.getYukonListEntry( ((LiteStarsAppGenerator) liteApp).getTransferSwitchMfgID() ),
	    			TransferSwitchManufacturer.class)
	    	);
	    	gen.setPeakKWCapacity( ((LiteStarsAppGenerator) liteApp).getPeakKWCapacity() );
	    	gen.setFuelCapGallons( ((LiteStarsAppGenerator) liteApp).getFuelCapGallons() );
	    	gen.setStartDelaySeconds( ((LiteStarsAppGenerator) liteApp).getStartDelaySeconds() );
	    	starsApp.setGenerator( gen );
	    }
	    else if (liteApp instanceof LiteStarsAppGrainDryer) {
	    	GrainDryer gd = new GrainDryer();
	    	gd.setDryerType(
	    		(DryerType) StarsFactory.newStarsCustListEntry(
	    			YukonListFuncs.getYukonListEntry( ((LiteStarsAppGrainDryer) liteApp).getDryerTypeID() ),
	    			DryerType.class)
	    	);
	    	gd.setBinSize(
	    		(BinSize) StarsFactory.newStarsCustListEntry(
	    			YukonListFuncs.getYukonListEntry( ((LiteStarsAppGrainDryer) liteApp).getBinSizeID() ),
	    			BinSize.class)
	    	);
	    	gd.setBlowerEnergySource(
	    		(BlowerEnergySource) StarsFactory.newStarsCustListEntry(
	    			YukonListFuncs.getYukonListEntry( ((LiteStarsAppGrainDryer) liteApp).getBlowerEnergySourceID() ),
	    			BlowerEnergySource.class)
	    	);
	    	gd.setBlowerHorsePower(
	    		(BlowerHorsePower) StarsFactory.newStarsCustListEntry(
	    			YukonListFuncs.getYukonListEntry( ((LiteStarsAppGrainDryer) liteApp).getBlowerHorsePowerID() ),
	    			BlowerHorsePower.class)
	    	);
	    	gd.setBlowerHeatSource(
	    		(BlowerHeatSource) StarsFactory.newStarsCustListEntry(
	    			YukonListFuncs.getYukonListEntry( ((LiteStarsAppGrainDryer) liteApp).getBlowerHeatSourceID() ),
	    			BlowerHeatSource.class)
	    	);
	    	starsApp.setGrainDryer( gd );
	    }
	    else if (liteApp instanceof LiteStarsAppStorageHeat) {
	    	StorageHeat sh = new StorageHeat();
	    	sh.setStorageType(
	    		(StorageType) StarsFactory.newStarsCustListEntry(
	    			YukonListFuncs.getYukonListEntry( ((LiteStarsAppStorageHeat) liteApp).getStorageTypeID() ),
	    			StorageType.class)
	    	);
	    	sh.setPeakKWCapacity( ((LiteStarsAppStorageHeat) liteApp).getPeakKWCapacity() );
	    	sh.setHoursToRecharge( ((LiteStarsAppStorageHeat) liteApp).getHoursToRecharge() );
	    	starsApp.setStorageHeat( sh );
	    }
	    else if (liteApp instanceof LiteStarsAppHeatPump) {
	    	HeatPump hp = new HeatPump();
	    	hp.setPumpType(
	    		(PumpType) StarsFactory.newStarsCustListEntry(
	    			YukonListFuncs.getYukonListEntry( ((LiteStarsAppHeatPump) liteApp).getPumpTypeID() ),
	    			PumpType.class)
	    	);
	    	hp.setStandbySource(
	    		(StandbySource) StarsFactory.newStarsCustListEntry(
	    			YukonListFuncs.getYukonListEntry( ((LiteStarsAppHeatPump) liteApp).getStandbySourceID() ),
	    			StandbySource.class)
	    	);
	    	hp.setRestartDelaySeconds( ((LiteStarsAppHeatPump) liteApp).getSecondsDelayToRestart() );
	    	starsApp.setHeatPump( hp );
	    }
	    else if (liteApp instanceof LiteStarsAppIrrigation) {
	    	Irrigation irr = new Irrigation();
	    	irr.setIrrigationType(
	    		(IrrigationType) StarsFactory.newStarsCustListEntry(
	    			YukonListFuncs.getYukonListEntry( ((LiteStarsAppIrrigation) liteApp).getIrrigationTypeID() ),
	    			IrrigationType.class)
	    	);
	    	irr.setHorsePower(
	    		(HorsePower) StarsFactory.newStarsCustListEntry(
	    			YukonListFuncs.getYukonListEntry( ((LiteStarsAppIrrigation) liteApp).getHorsePowerID() ),
	    			HorsePower.class)
	    	);
	    	irr.setEnergySource(
	    		(EnergySource) StarsFactory.newStarsCustListEntry(
	    			YukonListFuncs.getYukonListEntry( ((LiteStarsAppIrrigation) liteApp).getEnergySourceID() ),
	    			EnergySource.class)
	    	);
	    	irr.setSoilType(
	    		(SoilType) StarsFactory.newStarsCustListEntry(
	    			YukonListFuncs.getYukonListEntry( ((LiteStarsAppIrrigation) liteApp).getSoilTypeID() ),
	    			SoilType.class)
	    	);
	    	irr.setMeterLocation(
	    		(MeterLocation) StarsFactory.newStarsCustListEntry(
	    			YukonListFuncs.getYukonListEntry( ((LiteStarsAppIrrigation) liteApp).getMeterLocationID() ),
	    			MeterLocation.class)
	    	);
	    	irr.setMeterVoltage(
	    		(MeterVoltage) StarsFactory.newStarsCustListEntry(
	    			YukonListFuncs.getYukonListEntry( ((LiteStarsAppIrrigation) liteApp).getMeterVoltageID() ),
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
	
	public static StarsEnrollmentPrograms createStarsEnrollmentPrograms(ArrayList liteAppCats, LiteStarsEnergyCompany energyCompany) {
		StarsEnrollmentPrograms starsEnrPrograms = new StarsEnrollmentPrograms();
        	
        for (int i = 0; i < liteAppCats.size(); i++) {
        	LiteApplianceCategory liteAppCat = (LiteApplianceCategory) liteAppCats.get(i);
    		starsEnrPrograms.addStarsApplianceCategory(
    			StarsLiteFactory.createStarsApplianceCategory(liteAppCat, energyCompany) );
        }
        
        return starsEnrPrograms;
	}
	
	public static StarsCustomerFAQs createStarsCustomerFAQs(ArrayList liteFAQs) {
		StarsCustomerFAQs starsCustFAQs = new StarsCustomerFAQs();
		
        int lastSubjectID = CtiUtilities.NONE_ID;
        StarsCustomerFAQGroup lastGroup = null;
        
        // Group the FAQs by their subjects
        for (int i = 0; i < liteFAQs.size(); i++) {
        	LiteCustomerFAQ liteFAQ = (LiteCustomerFAQ) liteFAQs.get(i);
        	
        	if (liteFAQ.getSubjectID() != lastSubjectID) {
        		lastSubjectID = liteFAQ.getSubjectID();
        		lastGroup = new StarsCustomerFAQGroup();
        		lastGroup.setSubjectID( lastSubjectID );
        		lastGroup.setSubject( YukonListFuncs.getYukonListEntry(lastSubjectID).getEntryText() );
        		starsCustFAQs.addStarsCustomerFAQGroup( lastGroup );
        	}
        	
			StarsCustomerFAQ starsFAQ = new StarsCustomerFAQ();
			starsFAQ.setQuestionID( liteFAQ.getQuestionID() );
			starsFAQ.setQuestion( liteFAQ.getQuestion() );
			starsFAQ.setAnswer( liteFAQ.getAnswer() );
        	lastGroup.addStarsCustomerFAQ( starsFAQ );
        }
        
        return starsCustFAQs;
	}
	
	
	public static boolean isIdenticalContactNotification(LiteContactNotification liteNotif, StarsContactNotification starsNotif) {
		if (liteNotif == null) return (starsNotif.getNotification().length() == 0);
		
		return (ServerUtils.forceNotNull(liteNotif.getNotification()).equals( starsNotif.getNotification() )
				&& (liteNotif.getDisableFlag().equalsIgnoreCase("Y") ^ starsNotif.getEnabled()));
	}
	
	public static boolean isIdenticalCustomerContact(LiteContact liteContact, StarsCustomerContact starsContact) {
		LiteContactNotification liteNotifHPhone = ContactFuncs.getContactNotification( liteContact, YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE );
		LiteContactNotification liteNotifWPhone = ContactFuncs.getContactNotification( liteContact, YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE );
		LiteContactNotification liteNotifEmail = ContactFuncs.getContactNotification( liteContact, YukonListEntryTypes.YUK_ENTRY_ID_EMAIL );
		
		return (ServerUtils.forceNotNull(liteContact.getContLastName()).equals( starsContact.getLastName() )
				&& ServerUtils.forceNotNull(liteContact.getContFirstName()).equals( starsContact.getFirstName() )
				&& ServerUtils.getNotification(liteNotifHPhone).equals( starsContact.getHomePhone() )
				&& ServerUtils.getNotification(liteNotifWPhone).equals( starsContact.getWorkPhone() )
				&& isIdenticalContactNotification( liteNotifEmail, starsContact.getEmail() ));
	}
	
	public static boolean isIdenticalCustomerAddress(LiteAddress liteAddr, StarsCustomerAddress starsAddr) {
		return (ServerUtils.forceNotNull(liteAddr.getLocationAddress1()).equals( starsAddr.getStreetAddr1() )
				&& ServerUtils.forceNotNull(liteAddr.getLocationAddress2()).equals( starsAddr.getStreetAddr2() )
				&& ServerUtils.forceNotNull(liteAddr.getCityName()).equals( starsAddr.getCity() )
				&& ServerUtils.forceNotNull(liteAddr.getStateCode()).equals( starsAddr.getState() )
				&& ServerUtils.forceNotNull(liteAddr.getZipCode()).equals( starsAddr.getZip() )
				&& ServerUtils.forceNotNull(liteAddr.getCounty()).equals( ServerUtils.forceNotNull(starsAddr.getCounty()) ));
	}
	
	public static boolean isIdenticalSiteInformation(LiteSiteInformation liteSite, StarsSiteInformation starsSite) {
		return (ServerUtils.forceNotNull(liteSite.getFeeder()).equals( starsSite.getFeeder() )
				&& ServerUtils.forceNotNull(liteSite.getPole()).equals( starsSite.getPole() )
				&& ServerUtils.forceNotNull(liteSite.getTransformerSize()).equals( starsSite.getTransformerSize() )
				&& ServerUtils.forceNotNull(liteSite.getServiceVoltage()).equals( starsSite.getServiceVoltage() )
				&& liteSite.getSubstationID() == starsSite.getSubstation().getEntryID());
	}
	
	public static boolean isIdenticalCustomerAccount(LiteCustomerAccount liteAccount, StarsCustAccount starsAccount) {
		return (ServerUtils.forceNotNull(liteAccount.getAccountNumber()).equals( starsAccount.getAccountNumber() )
				&& ServerUtils.forceNotNull(liteAccount.getAccountNotes()).equals( starsAccount.getAccountNotes() ));
	}
	
	public static boolean isIdenticalCustomer(LiteCustomer liteCustomer, StarsCustAccount starsCustomer) {
		return ((starsCustomer.getIsCommercial() && liteCustomer.getCustomerTypeID() == CustomerTypes.CUSTOMER_CI
					|| !starsCustomer.getIsCommercial() && liteCustomer.getCustomerTypeID() == CustomerTypes.CUSTOMER_RESIDENTIAL)
				&& (starsCustomer.getTimeZone() == null || liteCustomer.getTimeZone().equalsIgnoreCase( starsCustomer.getTimeZone() )));
	}
	
	public static boolean isIdenticalAccountSite(LiteAccountSite liteAcctSite, StarsCustAccount starsAcctSite) {
		return (ServerUtils.forceNotNull(liteAcctSite.getSiteNumber()).equals( starsAcctSite.getPropertyNumber() )
				&& ServerUtils.forceNotNull(liteAcctSite.getPropertyNotes()).equals( starsAcctSite.getPropertyNotes() ));
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
