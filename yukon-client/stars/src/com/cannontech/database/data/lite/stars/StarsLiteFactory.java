package com.cannontech.database.data.lite.stars;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonListFuncs;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod;
import com.cannontech.stars.xml.serialize.types.StarsThermoDaySettings;
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
		else if (db instanceof com.cannontech.database.data.stars.event.LMThermostatManualEvent) {
			lite = new LiteLMThermostatManualEvent();
			setLiteLMThermostatManualEvent( (LiteLMThermostatManualEvent) lite, (com.cannontech.database.data.stars.event.LMThermostatManualEvent) db );
		}
		else if (db instanceof com.cannontech.database.data.stars.appliance.ApplianceBase) {
			lite = new LiteStarsAppliance();
			setLiteApplianceBase( (LiteStarsAppliance) lite, (com.cannontech.database.data.stars.appliance.ApplianceBase) db );
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
		
		return lite;
	}
	
	public static void setLiteCustomerContact(LiteCustomerContact liteContact, com.cannontech.database.data.customer.Contact contact) {
		liteContact.setContactID( contact.getContact().getContactID().intValue() );
		liteContact.setLastName( contact.getContact().getContLastName() );
		liteContact.setFirstName( contact.getContact().getContFirstName() );
		liteContact.setAddressID( contact.getContact().getAddressID().intValue() );
		
		for (int i = 0; i < contact.getContactNotifVect().size(); i++) {
			com.cannontech.database.db.contact.ContactNotification notif =
					(com.cannontech.database.db.contact.ContactNotification) contact.getContactNotifVect().get(i);
			if (notif.getNotificationCatID().intValue() == SOAPServer.YUK_LIST_ENTRY_ID_HOME_PHONE)
				liteContact.setHomePhone( notif.getNotification() );
			else if (notif.getNotificationCatID().intValue() == SOAPServer.YUK_LIST_ENTRY_ID_WORK_PHONE)
				liteContact.setWorkPhone( notif.getNotification() );
			else if (notif.getNotificationCatID().intValue() == SOAPServer.YUK_LIST_ENTRY_ID_EMAIL)
				liteContact.setEmail( LiteCustomerContact.ContactNotification.newInstance(
						notif.getDisableFlag().equalsIgnoreCase("N"), notif.getNotification()) );
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
		
		ArrayList hwHist = liteHw.getLmHardwareHistory();
		hwHist.clear();
		com.cannontech.database.data.stars.event.LMHardwareEvent[] events =
				com.cannontech.database.data.stars.event.LMHardwareEvent.getAllLMHardwareEvents( hw.getInventoryBase().getInventoryID() );
		for (int i = 0; i < events.length; i++) {
			LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) createLite( events[i] );
			hwHist.add( liteEvent );
		}
		
		liteHw.updateDeviceStatus();
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
	
	public static void setLiteLMThermostatManualEvent(LiteLMThermostatManualEvent liteEvent, com.cannontech.database.data.stars.event.LMThermostatManualEvent event) {
		setLiteLMCustomerEvent(liteEvent, event.getLMCustomerEventBase());
		liteEvent.setInventoryID( event.getLmThermostatManualEvent().getInventoryID().intValue() );
		liteEvent.setPreviousTemperature( event.getLmThermostatManualEvent().getPreviousTemperature().intValue() );
		liteEvent.setHoldTemperature( event.getLmThermostatManualEvent().getHoldTemperature().equalsIgnoreCase("Y") );
		liteEvent.setOperationStateID( event.getLmThermostatManualEvent().getOperationStateID().intValue() );
		liteEvent.setFanOperationID( event.getLmThermostatManualEvent().getFanOperationID().intValue() );
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
	}
	
	public static void setLiteAppWaterHeater(LiteStarsAppWaterHeater liteAppWH, com.cannontech.database.db.stars.appliance.ApplianceWaterHeater appWH) {
		liteAppWH.setNumberOfGallonsID( appWH.getNumberOfGallonsID().intValue() );
		liteAppWH.setEnergySourceID( appWH.getEnergySourceID().intValue() );
		liteAppWH.setNumberOfElements( appWH.getNumberOfElements().intValue() );
	}
	
	public static void setLiteAppDualFuel(LiteStarsAppDualFuel liteAppDF, com.cannontech.database.db.stars.appliance.ApplianceDualFuel appDF) {
		liteAppDF.setSwitchOverTypeID( appDF.getSwitchOverTypeID().intValue() );
		liteAppDF.setSecondaryKWCapacity( appDF.getSecondaryKWCapacity().intValue() );
		liteAppDF.setSecondaryEnergySourceID( appDF.getSecondaryEnergySourceID().intValue() );
	}
	
	public static void setLiteAppGenerator(LiteStarsAppGenerator liteAppGen, com.cannontech.database.db.stars.appliance.ApplianceGenerator appGen) {
		liteAppGen.setTransferSwitchTypeID( appGen.getTransferSwitchTypeID().intValue() );
		liteAppGen.setTransferSwitchMfgID( appGen.getTransferSwitchMfgID().intValue() );
		liteAppGen.setPeakKWCapacity( appGen.getPeakKWCapacity().intValue() );
		liteAppGen.setFuelCapGallons( appGen.getFuelCapGallons().intValue() );
		liteAppGen.setStartDelaySeconds( appGen.getStartDelaySeconds().intValue() );
	}
	
	public static void setLiteAppGrainDryer(LiteStarsAppGrainDryer liteAppGD, com.cannontech.database.db.stars.appliance.ApplianceGrainDryer appGD) {
		liteAppGD.setDryerTypeID( appGD.getDryerTypeID().intValue() );
		liteAppGD.setBinSizeID( appGD.getBinSizeID().intValue() );
		liteAppGD.setBlowerEnergySourceID( appGD.getBlowerEnergySourceID().intValue() );
		liteAppGD.setBlowerHorsePowerID( appGD.getBlowerHorsePowerID().intValue() );
		liteAppGD.setBlowerHeatSourceID( appGD.getBlowerHeatSourceID().intValue() );
	}
	
	public static void setLiteAppStorageHeat(LiteStarsAppStorageHeat liteAppSH, com.cannontech.database.db.stars.appliance.ApplianceStorageHeat appSH) {
		liteAppSH.setStorageTypeID( appSH.getStorageTypeID().intValue() );
		liteAppSH.setPeakKWCapacity( appSH.getPeakKWCapacity().intValue() );
		liteAppSH.setHoursToRecharge( appSH.getHoursToRecharge().intValue() );
	}
	
	public static void setLiteAppHeatPump(LiteStarsAppHeatPump liteAppHP, com.cannontech.database.db.stars.appliance.ApplianceHeatPump appHP) {
		liteAppHP.setPumpTypeID( appHP.getPumpTypeID().intValue() );
		liteAppHP.setStandbySourceID( appHP.getStandbySourceID().intValue() );
		liteAppHP.setSecondsDelayToRestart( appHP.getSecondsDelayToRestart().intValue() );
	}
	
	public static void setLiteAppIrrigation(LiteStarsAppIrrigation liteAppIrr, com.cannontech.database.db.stars.appliance.ApplianceIrrigation appIrr) {
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
	
	
	public static DBPersistent createDBPersistent(LiteBase lite) {
		DBPersistent db = null;
		
		switch (lite.getLiteType()) {
			case LiteTypes.STARS_CUSTOMER_CONTACT:
				db = new com.cannontech.database.data.customer.Contact();
				setContact( (com.cannontech.database.data.customer.Contact) db, (LiteCustomerContact) lite, true );
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
			case LiteTypes.STARS_LMTHERMOSTAT_MANUAL_EVENT:
				db = new com.cannontech.database.data.stars.event.LMThermostatManualEvent();
				setLMThermostatManualEvent( (com.cannontech.database.data.stars.event.LMThermostatManualEvent) db, (LiteLMThermostatManualEvent) lite );
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
	
	public static void setContact(com.cannontech.database.data.customer.Contact contact, LiteCustomerContact liteContact, boolean isResidential) {
		contact.getContact().setContactID( new Integer(liteContact.getContactID()) );
		contact.getContact().setContLastName( liteContact.getLastName() );
		contact.getContact().setContFirstName( liteContact.getFirstName() );
		contact.getContact().setAddressID( new Integer(liteContact.getAddressID()) );
		
		if (liteContact.getHomePhone() != null && liteContact.getHomePhone().length() > 0) {
			com.cannontech.database.db.contact.ContactNotification notif = new com.cannontech.database.db.contact.ContactNotification();
			notif.setContactID( new Integer(liteContact.getContactID()) );
			if (isResidential)
				notif.setNotificationCatID( new Integer(SOAPServer.YUK_LIST_ENTRY_ID_HOME_PHONE) );
			else
				notif.setNotificationCatID( new Integer(SOAPServer.YUK_LIST_ENTRY_ID_PHONE) );
			notif.setNotification( liteContact.getHomePhone() );
			notif.setDisableFlag( "Y" );
			contact.getContactNotifVect().add( notif );
		}
		
		if (liteContact.getWorkPhone() != null && liteContact.getWorkPhone().length() > 0) {
			com.cannontech.database.db.contact.ContactNotification notif = new com.cannontech.database.db.contact.ContactNotification();
			notif.setContactID( new Integer(liteContact.getContactID()) );
			if (isResidential)
				notif.setNotificationCatID( new Integer(SOAPServer.YUK_LIST_ENTRY_ID_WORK_PHONE) );
			else
				notif.setNotificationCatID( new Integer(SOAPServer.YUK_LIST_ENTRY_ID_FAX) );
			notif.setNotification( liteContact.getWorkPhone() );
			notif.setDisableFlag( "Y" );
			contact.getContactNotifVect().add( notif );
		}
		
		if (liteContact.getEmail() != null && liteContact.getEmail().getNotification() != null && liteContact.getEmail().getNotification().length() > 0) {
			com.cannontech.database.db.contact.ContactNotification notif = new com.cannontech.database.db.contact.ContactNotification();
			notif.setContactID( new Integer(liteContact.getContactID()) );
			notif.setNotificationCatID( new Integer(SOAPServer.YUK_LIST_ENTRY_ID_EMAIL) );
			notif.setNotification( liteContact.getEmail().getNotification() );
			notif.setDisableFlag( liteContact.getEmail().isEnabled() ? "N" : "Y" );
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

	
	public static void setStarsCustomerContact(StarsCustomerContact starsContact, LiteCustomerContact liteContact) {
		starsContact.setContactID( liteContact.getContactID() );
		starsContact.setLastName( ServerUtils.forceNotNull(liteContact.getLastName()) );
		starsContact.setFirstName( ServerUtils.forceNotNull(liteContact.getFirstName()) );
		starsContact.setHomePhone( ServerUtils.forceNotNull(liteContact.getHomePhone()) );
		starsContact.setWorkPhone( ServerUtils.forceNotNull(liteContact.getWorkPhone()) );
		
		if (liteContact.getEmail() != null)
			starsContact.setEmail( (Email) StarsFactory.newStarsContactNotification(
					liteContact.getEmail().isEnabled(), ServerUtils.forceNotNull(liteContact.getEmail().getNotification()), Email.class) );
		else
			starsContact.setEmail( (Email) StarsFactory.newStarsContactNotification(
					false, "", Email.class) );
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
		starsDynData.setLastUpdateTime( new Date(liteDynData.getTimestamp()) );
		starsDynData.setDisplayedTemperature( liteDynData.getDisplayedTemperature() );
		if (liteDynData.getDisplayedTempUnit() != null)
			starsDynData.setDisplayedTempUnit( liteDynData.getDisplayedTempUnit().equalsIgnoreCase("C") ? "Celsius" : "Fahrenheit" );
		starsDynData.setFan( ServerUtils.getThermFanSetting(liteDynData.getFanSwitch()) );
		starsDynData.setMode( ServerUtils.getThermModeSetting(liteDynData.getSystemSwitch()) );
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
		if (liteDynData.getOutdoorTemperature() > 0) {
			String desc = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_GED_OUTDOOR_TEMP ).getEntryText();
			starsDynData.addInfoString( desc + ": " + liteDynData.getOutdoorTemperature() + "&deg;" );
		}
		if (liteDynData.getFilterRemaining() > 0) {
			StringTokenizer st = new StringTokenizer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_GED_FILTER).getEntryText(), "," );
			String desc = st.nextToken();
			String dayUnit = (liteDynData.getFilterRemaining() > 1)? "days" : "day";
			starsDynData.addInfoString( desc + ": " + liteDynData.getFilterRemaining() + " " + dayUnit );
		}
		for (int i = 0; i < liteDynData.getInfoStrings().size(); i++)
			starsDynData.addInfoString( (String) liteDynData.getInfoStrings().get(i) );
	}
	
	public static void setStarsThermostatSettings(StarsThermoSettings starsSettings, LiteStarsThermostatSettings liteSettings, int energyCompanyID) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
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
	
	public static void setStarsCustAccountInformation(StarsCustAccountInformation starsAcctInfo, LiteStarsCustAccountInformation liteAcctInfo, int energyCompanyID, boolean isOperator) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
		
		LiteCustomerAccount liteAccount = liteAcctInfo.getCustomerAccount();
		LiteCustomer liteCustomer = liteAcctInfo.getCustomer();
		LiteAccountSite liteAcctSite = liteAcctInfo.getAccountSite();
		LiteSiteInformation liteSiteInfo = liteAcctInfo.getSiteInformation();
		
		StarsCustomerAccount starsAccount = new StarsCustomerAccount();
		starsAccount.setAccountID( liteAccount.getAccountID() );
		starsAccount.setCustomerID( liteAccount.getCustomerID() );
		starsAccount.setAccountNumber( ServerUtils.forceNotNull(liteAccount.getAccountNumber()) );
		starsAccount.setIsCommercial( liteCustomer.getCustomerTypeID() == CustomerTypes.CUSTOMER_CI );
		starsAccount.setCompany( "" );
		starsAccount.setAccountNotes( ServerUtils.forceNotNull(liteAccount.getAccountNotes()) );
		starsAccount.setPropertyNumber( ServerUtils.forceNotNull(liteAcctSite.getSiteNumber()) );
		starsAccount.setPropertyNotes( ServerUtils.forceNotNull(liteAcctSite.getPropertyNotes()) );
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
			
			starsProgs.addStarsLMProgram( createStarsLMProgram(liteProg, liteApp, energyCompanyID) );
		}
/*		
		if (liteAcctInfo.getThermostatSettings() != null) {
			StarsThermostatSettings starsThermSettings = new StarsThermostatSettings();
			setStarsThermostatSettings( starsThermSettings, liteAcctInfo.getThermostatSettings(), energyCompanyID );
			starsAcctInfo.setStarsThermostatSettings( starsThermSettings );
		}
*/		
		if (isOperator) {
			ArrayList liteApps = liteAcctInfo.getAppliances();
			StarsAppliances starsApps = new StarsAppliances();
			starsAcctInfo.setStarsAppliances( starsApps );
			
			TreeMap tmap = new TreeMap();
			for (int i = 0; i < liteApps.size(); i++) {
				LiteStarsAppliance liteApp = (LiteStarsAppliance) liteApps.get(i);
				StarsAppliance starsApp = (StarsAppliance) createStarsAppliance(liteApp, energyCompanyID);
				
				ArrayList list = (ArrayList) tmap.get( starsApp.getDescription() );
				if (list == null) {
					list = new ArrayList();
					tmap.put( starsApp.getDescription(), list );
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
		
	
	public static StarsCustAccountInformation createStarsCustAccountInformation(LiteStarsCustAccountInformation liteAcctInfo, int energyCompanyID, boolean isOperator) {
		StarsCustAccountInformation starsAcctInfo = new StarsCustAccountInformation();
		setStarsCustAccountInformation( starsAcctInfo, liteAcctInfo, energyCompanyID, isOperator );
		return starsAcctInfo;
	}
	
	public static StarsSiteInformation createStarsSiteInformation(LiteSiteInformation liteSite, int energyCompanyID) {
		StarsSiteInformation starsSite = new StarsSiteInformation();
		
		starsSite.setSiteID( liteSite.getSiteID() );
		starsSite.setFeeder( ServerUtils.forceNotNull(liteSite.getFeeder()) );
		starsSite.setPole( ServerUtils.forceNotNull(liteSite.getPole()) );
		starsSite.setTransformerSize( ServerUtils.forceNotNull(liteSite.getTransformerSize()) );
		starsSite.setServiceVoltage( ServerUtils.forceNotNull(liteSite.getServiceVoltage()) );
		starsSite.setSubstation(
			(Substation) StarsFactory.newStarsCustListEntry(
				SOAPServer.getEnergyCompany(energyCompanyID).getYukonListEntry(
					com.cannontech.database.db.stars.Substation.LISTNAME_SUBSTATION,
					liteSite.getSubstationID()
				),
				Substation.class)
		);
		
		return starsSite;
	}
	
	public static StarsLMHardware createStarsLMHardware(LiteLMHardwareBase liteHw, int energyCompanyID) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
		StarsLMHardware starsHw = new StarsLMHardware();
		
		starsHw.setInventoryID( liteHw.getInventoryID() );
		starsHw.setCategory( YukonListFuncs.getYukonListEntry(liteHw.getCategoryID()).getEntryText() );
				
		InstallationCompany company = new InstallationCompany();
		company.setEntryID( liteHw.getInstallationCompanyID() );
		LiteServiceCompany liteCompany = energyCompany.getServiceCompany( liteHw.getInstallationCompanyID() );
		if (liteCompany != null)
			company.setContent( ServerUtils.forceNotNull(liteCompany.getCompanyName()) );
		else
			company.setContent( "(none)" );
		starsHw.setInstallationCompany( company );
		
		starsHw.setReceiveDate( ServerUtils.translateDate(liteHw.getReceiveDate()) );
		starsHw.setInstallDate( ServerUtils.translateDate(liteHw.getInstallDate()) );
		starsHw.setRemoveDate( ServerUtils.translateDate(liteHw.getRemoveDate()) );
		starsHw.setAltTrackingNumber( ServerUtils.forceNotNull(liteHw.getAlternateTrackingNumber()) );
		
		Voltage volt = new Voltage();
		volt.setEntryID( liteHw.getVoltageID() );
		volt.setContent( YukonListFuncs.getYukonListEntry(liteHw.getVoltageID()).getEntryText() );
		starsHw.setVoltage( volt );
		
		starsHw.setNotes( ServerUtils.forceNotNull(liteHw.getNotes()) );
		starsHw.setManufactureSerialNumber( ServerUtils.forceNotNull(liteHw.getManufactureSerialNumber()) );
		
		LMDeviceType hwType = new LMDeviceType();
		hwType.setEntryID( liteHw.getLmHardwareTypeID() );
		hwType.setContent( YukonListFuncs.getYukonListEntry(liteHw.getLmHardwareTypeID()).getEntryText() );
		starsHw.setLMDeviceType( hwType );
		starsHw.setInstallationNotes( "" );
		
		starsHw.setDeviceStatus( (DeviceStatus)	StarsFactory.newStarsCustListEntry(
				energyCompany.getYukonListEntry( liteHw.getDeviceStatus() ), DeviceStatus.class) );
		
		StarsLMHardwareHistory hwHist = new StarsLMHardwareHistory();
		for (int i = 0; i < liteHw.getLmHardwareHistory().size(); i++) {
			LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) liteHw.getLmHardwareHistory().get(i);
			StarsLMHardwareEvent starsEvent = new StarsLMHardwareEvent();
			setStarsLMCustomerEvent( starsEvent, liteEvent );
			hwHist.addStarsLMHardwareEvent( starsEvent );
		}
		starsHw.setStarsLMHardwareHistory( hwHist );
		
		// set installation notes
		for (int i = 0; i < hwHist.getStarsLMHardwareEventCount(); i++) {
			if (hwHist.getStarsLMHardwareEvent(i).getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL) {
				starsHw.setInstallationNotes( hwHist.getStarsLMHardwareEvent(i).getNotes() );
				break;
			}
		}
		
		if (liteHw.getThermostatSettings() != null) {
			StarsThermostatSettings starsSettings = new StarsThermostatSettings();
			setStarsThermostatSettings( starsSettings, liteHw.getThermostatSettings(), energyCompanyID );
			starsHw.setStarsThermostatSettings( starsSettings );
		}
		
		return starsHw;
	}
	
	public static StarsServiceRequest createStarsServiceRequest(LiteWorkOrderBase liteOrder, int energyCompanyID) {
		StarsServiceRequest starsOrder = new StarsServiceRequest();
		starsOrder.setOrderID( liteOrder.getOrderID() );
		
		String orderNo = ServerUtils.forceNotNull(liteOrder.getOrderNumber());
		if (orderNo.startsWith( ServerUtils.CTI_NUMBER ))
			orderNo = orderNo.substring( ServerUtils.CTI_NUMBER.length() );
		starsOrder.setOrderNumber( orderNo );
		
		starsOrder.setServiceType(
			(ServiceType) StarsFactory.newStarsCustListEntry(
				YukonListFuncs.getYukonListEntry(liteOrder.getWorkTypeID()),
				ServiceType.class)
		);
		
		ServiceCompany company = new ServiceCompany();
		company.setEntryID( liteOrder.getServiceCompanyID() );
		LiteServiceCompany liteCompany = SOAPServer.getEnergyCompany( energyCompanyID ).getServiceCompany( liteOrder.getServiceCompanyID() );
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
		
		starsOrder.setDateReported( new Date(liteOrder.getDateReported()) );
		starsOrder.setDateScheduled( new Date(liteOrder.getDateScheduled()) );
		starsOrder.setDateCompleted( new Date(liteOrder.getDateCompleted()) );
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
	
	public static StarsLMProgram createStarsLMProgram(LiteStarsLMProgram liteProg, LiteStarsAppliance liteApp, int energyCompanyID) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
		
		StarsLMProgram starsProg = new StarsLMProgram();
		starsProg.setProgramID( liteProg.getLmProgram().getProgramID() );
		starsProg.setGroupID( liteProg.getGroupID() );
		starsProg.setProgramName( ServerUtils.forceNotNull(liteProg.getLmProgram().getProgramName()) );
		starsProg.setApplianceCategoryID( liteApp.getApplianceCategoryID() );

		// Temporarily use the "URL" field in YukonWebConfiguration table for program alias
		StarsWebConfig starsConfig = energyCompany.getStarsWebConfig( liteProg.getLmProgram().getWebSettingsID() );
		if (starsConfig.getURL().length() > 0)
			starsProg.setProgramName( starsConfig.getURL() );
		
		LiteStarsLMControlHistory liteCtrlHist = energyCompany.getLMControlHistory( liteProg.getGroupID() );
		if (liteCtrlHist != null)
			starsProg.setStarsLMControlHistory( energyCompany.getStarsLMControlHistory(liteCtrlHist) );
		else
			starsProg.setStarsLMControlHistory( new StarsLMControlHistory() );
		
		if (liteProg.getProgramHistory() != null) {
			StarsLMProgramHistory progHist = new StarsLMProgramHistory();
			for (int k = 0; k < liteProg.getProgramHistory().size(); k++) {
				LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) liteProg.getProgramHistory().get(k);
				StarsLMProgramEvent starsEvent = new StarsLMProgramEvent();
				setStarsLMCustomerEvent( starsEvent, liteEvent );
				progHist.addStarsLMProgramEvent( starsEvent );
			}
			starsProg.setStarsLMProgramHistory( progHist );
		}
		
		if (liteProg.isInService())
			starsProg.setStatus( ServletUtils.IN_SERVICE );
		else
			starsProg.setStatus( ServletUtils.OUT_OF_SERVICE );
		
		return starsProg;
	}
	
	public static StarsWebConfig createStarsWebConfig(LiteWebConfiguration liteWebConfig) {
		StarsWebConfig starsWebConfig = new StarsWebConfig();
		starsWebConfig.setLogoLocation( ServerUtils.forceNotNull(liteWebConfig.getLogoLocation()) );
		starsWebConfig.setDescription( ServerUtils.forceNotNull(liteWebConfig.getDescription()) );
		starsWebConfig.setAlternateDisplayName( ServerUtils.forceNotNull(liteWebConfig.getAlternateDisplayName()) );
		starsWebConfig.setURL( ServerUtils.forceNotNull(liteWebConfig.getUrl()) );
		
		return starsWebConfig;
	}
	
	public static StarsApplianceCategory createStarsApplianceCategory(LiteApplianceCategory liteAppCat, int energyCompanyID) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
		
		StarsApplianceCategory starsAppCat = new StarsApplianceCategory();
		starsAppCat.setApplianceCategoryID( liteAppCat.getApplianceCategoryID() );
		starsAppCat.setCategoryID( liteAppCat.getCategoryID() );
		starsAppCat.setDescription( ServerUtils.forceNotNull(liteAppCat.getDescription()) );
		starsAppCat.setStarsWebConfig( energyCompany.getStarsWebConfig(liteAppCat.getWebConfigurationID()) );
		
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
		
		return starsAppCat;
	}
	
	public static StarsServiceCompany createStarsServiceCompany(LiteServiceCompany liteCompany, int energyCompanyID) {
		StarsServiceCompany starsCompany = new StarsServiceCompany();
		starsCompany.setCompanyID( liteCompany.getCompanyID() );
		starsCompany.setCompanyName( ServerUtils.forceNotNull(liteCompany.getCompanyName()) );
		starsCompany.setMainPhoneNumber( ServerUtils.forceNotNone(liteCompany.getMainPhoneNumber()) );
		starsCompany.setMainFaxNumber( ServerUtils.forceNotNone(liteCompany.getMainFaxNumber()) );
		starsCompany.setCompanyAddress( (CompanyAddress) StarsFactory.newStarsCustomerAddress(CompanyAddress.class) );
		starsCompany.setPrimaryContact( (PrimaryContact) StarsFactory.newStarsCustomerContact(PrimaryContact.class) );
		
		if (liteCompany.getAddressID() != CtiUtilities.NONE_ID) {
			LiteAddress liteAddr = SOAPServer.getEnergyCompany(energyCompanyID).getAddress( liteCompany.getAddressID());
			CompanyAddress companyAddr = new CompanyAddress();
			setStarsCustomerAddress( companyAddr, liteAddr );
			starsCompany.setCompanyAddress( companyAddr );
		}
		
		if (liteCompany.getPrimaryContactID() != CtiUtilities.NONE_ID) {
			LiteCustomerContact liteContact = SOAPServer.getEnergyCompany(energyCompanyID).getCustomerContact( liteCompany.getPrimaryContactID() );
			PrimaryContact primContact = new PrimaryContact();
			setStarsCustomerContact( primContact, liteContact );
			starsCompany.setPrimaryContact( primContact );
		}
		
		return starsCompany;
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
		starsSched.setDay( ServerUtils.getThermDaySetting(towID) );
		
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
		if (liteSeason.getWebConfigurationID() == SOAPServer.YUK_WEB_CONFIG_ID_COOL)
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
		starsOption.setMode( ServerUtils.getThermModeSetting(liteEvent.getOperationStateID()) );
		starsOption.setFan( ServerUtils.getThermFanSetting(liteEvent.getFanOperationID()) );
		starsEvent.setThermostatManualOption( starsOption );
		
		return starsEvent;
	}
	
	public static StarsAppliance createStarsAppliance(LiteStarsAppliance liteApp, int energyCompanyID) {
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
        
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
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
	
	public static StarsEnergyCompany createStarsEnergyCompany(LiteStarsEnergyCompany liteCompany) {
		StarsEnergyCompany starsCompany = new StarsEnergyCompany();
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
				for (int i = 0; i < liteContact.getLiteContactNotifications().size(); i++) {
					LiteContactNotification liteNotif = (LiteContactNotification) liteContact.getLiteContactNotifications().get(i);
					if (liteNotif.getNotificationCategoryID() == SOAPServer.YUK_LIST_ENTRY_ID_PHONE)
						starsCompany.setMainPhoneNumber( liteNotif.getNotification() );
					else if (liteNotif.getNotificationCategoryID() == SOAPServer.YUK_LIST_ENTRY_ID_FAX)
						starsCompany.setMainFaxNumber( liteNotif.getNotification() );
					else if (liteNotif.getNotificationCategoryID() == SOAPServer.YUK_LIST_ENTRY_ID_EMAIL)
						starsCompany.setEmail( liteNotif.getNotification() );
				}
				
				if (liteContact.getAddressID() != CtiUtilities.NONE_ID) {
					LiteAddress liteAddr = liteCompany.getAddress( liteContact.getAddressID() );
					CompanyAddress starsAddr = new CompanyAddress();
					setStarsCustomerAddress( starsAddr, liteAddr );
					starsCompany.setCompanyAddress( starsAddr );
				}
			}
		}
		
		return starsCompany;
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
				StarsSelectionListEntry starsEntry = new StarsSelectionListEntry();
				YukonListEntry yukonEntry = (YukonListEntry) entries.get(i);
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
	
	public static StarsEnrollmentPrograms createStarsEnrollmentPrograms(ArrayList liteAppCats, int energyCompanyID) {
		StarsEnrollmentPrograms starsEnrPrograms = new StarsEnrollmentPrograms();
        	
        for (int i = 0; i < liteAppCats.size(); i++) {
        	LiteApplianceCategory liteAppCat = (LiteApplianceCategory) liteAppCats.get(i);
    		starsEnrPrograms.addStarsApplianceCategory(
    			StarsLiteFactory.createStarsApplianceCategory(liteAppCat, energyCompanyID) );
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
	
	
	public static boolean isIdenticalContactNotification(LiteCustomerContact.ContactNotification liteNotif, StarsContactNotification starsNotif) {
		if (liteNotif == null)
			return (starsNotif.getNotification().length() == 0);
		return (ServerUtils.forceNotNull(liteNotif.getNotification()).equals( starsNotif.getNotification() )
				&& liteNotif.isEnabled() == starsNotif.getEnabled());
	}
	
	public static boolean isIdenticalCustomerContact(LiteCustomerContact liteContact, StarsCustomerContact starsContact) {
		return (ServerUtils.forceNotNull(liteContact.getLastName()).equals( starsContact.getLastName() )
				&& ServerUtils.forceNotNull(liteContact.getFirstName()).equals( starsContact.getFirstName() )
				&& ServerUtils.forceNotNull(liteContact.getHomePhone()).equals( starsContact.getHomePhone() )
				&& ServerUtils.forceNotNull(liteContact.getWorkPhone()).equals( starsContact.getWorkPhone() )
				&& isIdenticalContactNotification( liteContact.getEmail(), starsContact.getEmail() ));
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
