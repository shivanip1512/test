package com.cannontech.database.data.lite.stars;

import java.util.*;

import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.stars.CustomerSelectionList;
import com.cannontech.database.db.stars.CustomerListEntry;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.util.ServerUtils;
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
		
		if (db instanceof com.cannontech.database.db.customer.CustomerContact) {
			lite = new LiteCustomerContact();
			setLiteCustomerContact( (LiteCustomerContact) lite, (com.cannontech.database.db.customer.CustomerContact) db );
		}
		else if (db instanceof com.cannontech.database.db.customer.CustomerAddress) {
			lite = new LiteCustomerAddress();
			setLiteCustomerAddress( (LiteCustomerAddress) lite, (com.cannontech.database.db.customer.CustomerAddress) db );
		}
		else if (db instanceof com.cannontech.database.data.stars.hardware.LMHardwareBase) {
			lite = new LiteLMHardware();
			setLiteLMHardware( (LiteLMHardware) lite, (com.cannontech.database.data.stars.hardware.LMHardwareBase) db );
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
		else if (db instanceof com.cannontech.database.data.stars.customer.CustomerBase) {
			lite = new LiteCustomerBase();
			setLiteCustomerBase( (LiteCustomerBase) lite, (com.cannontech.database.data.stars.customer.CustomerBase) db );
		}
		else if (db instanceof com.cannontech.database.db.stars.customer.AccountSite) {
			lite = new LiteAccountSite();
			setLiteAccountSite( (LiteAccountSite) lite, (com.cannontech.database.db.stars.customer.AccountSite) db );
		}
		else if (db instanceof com.cannontech.database.db.stars.customer.SiteInformation) {
			lite = new LiteSiteInformation();
			setLiteSiteInformation( (LiteSiteInformation) lite, (com.cannontech.database.db.stars.customer.SiteInformation) db );
		}
		
		return lite;
	}
	
	public static void setLiteCustomerContact(LiteCustomerContact liteContact, com.cannontech.database.db.customer.CustomerContact contact) {
		liteContact.setContactID( contact.getContactID().intValue() );
		liteContact.setLastName( contact.getContLastName() );
		liteContact.setFirstName( contact.getContFirstName() );
		liteContact.setHomePhone( contact.getContPhone1() );
		liteContact.setWorkPhone( contact.getContPhone2() );
	}
	
	public static void setLiteCustomerAddress(LiteCustomerAddress liteAddr, com.cannontech.database.db.customer.CustomerAddress addr) {
		liteAddr.setAddressID( addr.getAddressID().intValue() );
		liteAddr.setLocationAddress1( addr.getLocationAddress1() );
		liteAddr.setLocationAddress2( addr.getLocationAddress2() );
		liteAddr.setCityName( addr.getCityName() );
		liteAddr.setStateCode( addr.getStateCode() );
		liteAddr.setZipCode( addr.getZipCode() );
	}
	
	public static void setLiteLMHardware(LiteLMHardware liteHw, com.cannontech.database.data.stars.hardware.LMHardwareBase hw) {
		liteHw.setInventoryID( hw.getInventoryBase().getInventoryID().intValue() );
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
		liteOrder.setCustomerID( order.getCustomerID().intValue() );
		liteOrder.setSiteID( order.getSiteID().intValue() );
		liteOrder.setServiceCompanyID( order.getServiceCompanyID().intValue() );
		liteOrder.setDateReported( order.getDateReported().getTime() );
		liteOrder.setDateScheduled( order.getDateScheduled().getTime() );
		liteOrder.setDateCompleted( order.getDateCompleted().getTime() );
		liteOrder.setDescription( order.getDescription() );
		liteOrder.setActionTaken( order.getActionTaken() );
		liteOrder.setOrderedBy( order.getOrderedBy() );
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
	}
	
	public static void setLiteCustomerBase(LiteCustomerBase liteCustomer, com.cannontech.database.data.stars.customer.CustomerBase customer) {
		liteCustomer.setCustomerID( customer.getCustomerBase().getCustomerID().intValue() );
		liteCustomer.setPrimaryContactID( customer.getCustomerBase().getPrimaryContactID().intValue() );
		liteCustomer.setCustomerTypeID( customer.getCustomerBase().getCustomerTypeID().intValue() );
		
		Vector contacts = customer.getCustomerContactVector();
		liteCustomer.setAdditionalContacts( new ArrayList() );
		for (int i = 0; i < contacts.size(); i++) {
			com.cannontech.database.db.customer.CustomerContact contact = (com.cannontech.database.db.customer.CustomerContact) contacts.get(i);
			liteCustomer.getAdditionalContacts().add( contact.getContactID() );
		}
	}
	
	public static void setLiteAccountSite(LiteAccountSite liteAcctSite, com.cannontech.database.db.stars.customer.AccountSite acctSite) {
		liteAcctSite.setAccountSiteID( acctSite.getAccountSiteID().intValue() );
		liteAcctSite.setSiteInformationID( acctSite.getSiteInformationID().intValue() );
		liteAcctSite.setSiteNumber( acctSite.getSiteNumber() );
		liteAcctSite.setStreetAddressID( acctSite.getStreetAddressID().intValue() );
		liteAcctSite.setPropertyNotes( acctSite.getPropertyNotes() );
	}
	
	
	public static DBPersistent createDBPersistent(LiteBase lite) {
		DBPersistent db = null;
		
		switch (lite.getLiteType()) {
			case LiteTypes.STARS_CUSTOMER_CONTACT:
				db = new com.cannontech.database.db.customer.CustomerContact();
				((com.cannontech.database.db.customer.CustomerContact) db).setContactID( new Integer(lite.getLiteID()) );
				break;
			case LiteTypes.STARS_CUSTOMER_ADDRESS:
				db = new com.cannontech.database.db.customer.CustomerAddress();
				((com.cannontech.database.db.customer.CustomerAddress) db).setAddressID( new Integer(lite.getLiteID()) );
				break;
			case LiteTypes.STARS_CUSTOMER_ACCOUNT:
				db = new com.cannontech.database.db.stars.customer.CustomerAccount();
				((com.cannontech.database.db.stars.customer.CustomerAccount) db).setAccountID( new Integer(lite.getLiteID()) );
				break;
			case LiteTypes.STARS_CUSTOMER_BASE:
				db = new com.cannontech.database.db.stars.customer.CustomerBase();
				((com.cannontech.database.db.stars.customer.CustomerBase) db).setCustomerID( new Integer(lite.getLiteID()) );
				break;
			case LiteTypes.STARS_ACCOUNT_SITE:
				db = new com.cannontech.database.db.stars.customer.AccountSite();
				((com.cannontech.database.db.stars.customer.AccountSite) db).setAccountSiteID( new Integer(lite.getLiteID()) );
				break;
			case LiteTypes.STARS_SITE_INFORMATION:
				db = new com.cannontech.database.db.stars.customer.SiteInformation();
				((com.cannontech.database.db.stars.customer.SiteInformation) db).setSiteID( new Integer(lite.getLiteID()) );
				break;
			case LiteTypes.STARS_LMHARDWARE_EVENT:
				db = new com.cannontech.database.data.stars.event.LMHardwareEvent();
				((com.cannontech.database.data.stars.event.LMHardwareEvent) db).setEventID( new Integer(lite.getLiteID()) );
				break;
			case LiteTypes.STARS_LMPROGRAM_EVENT:
				db = new com.cannontech.database.data.stars.event.LMProgramEvent();
				((com.cannontech.database.data.stars.event.LMProgramEvent) db).setEventID( new Integer(lite.getLiteID()) );
				break;
		}
		
		return db;
	}
	
	public static void setStarsCustomerContact(StarsCustomerContact starsContact, LiteCustomerContact liteContact) {
		starsContact.setContactID( liteContact.getContactID() );
		starsContact.setLastName( liteContact.getLastName() );
		starsContact.setFirstName( liteContact.getFirstName() );
		starsContact.setHomePhone( liteContact.getHomePhone() );
		starsContact.setWorkPhone( liteContact.getWorkPhone() );
	}
	
	public static void setStarsCustomerAddress(StarsCustomerAddress starsAddr, LiteCustomerAddress liteAddr) {
		starsAddr.setAddressID( liteAddr.getAddressID() );
		starsAddr.setStreetAddr1( liteAddr.getLocationAddress1() );
		starsAddr.setStreetAddr2( liteAddr.getLocationAddress2() );
		starsAddr.setCity( liteAddr.getCityName() );
		starsAddr.setState( liteAddr.getStateCode() );
		starsAddr.setZip( liteAddr.getZipCode() );
	}
	
	public static void setStarsLMCustomerEvent(StarsLMCustomerEvent starsEvent, LiteLMCustomerEvent liteEvent, Hashtable selectionLists) {
		StarsSelectionListEntry entry = StarsCustListEntryFactory.getStarsCustListEntry(
				(LiteCustomerSelectionList) selectionLists.get(CustomerSelectionList.LISTNAME_LMCUSTOMERACTION),
				liteEvent.getActionID() );
		starsEvent.setEventAction( entry.getContent() );
		starsEvent.setEventDateTime( new Date(liteEvent.getEventDateTime()) );
		starsEvent.setNotes( liteEvent.getNotes() );
		starsEvent.setYukonDefinition( entry.getYukonDefinition() );
	}
	
	public static StarsSiteInformation createStarsSiteInformation(LiteSiteInformation liteSite, Hashtable selectionLists) {
		StarsSiteInformation starsSite = new StarsSiteInformation();
		
		starsSite.setSiteID( liteSite.getSiteID() );
		starsSite.setFeeder( liteSite.getFeeder() );
		starsSite.setPole( liteSite.getPole() );
		starsSite.setTransformerSize( liteSite.getTransformerSize() );
		starsSite.setServiceVoltage( liteSite.getServiceVoltage() );
		
		Substation sub = new Substation();
		sub.setEntryID( liteSite.getSubstationID() );
		sub.setContent( StarsCustListEntryFactory.getStarsCustListEntry(
				(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.Substation.LISTNAME_SUBSTATION),
				liteSite.getSubstationID()).getContent() );
		starsSite.setSubstation( sub );
		
		return starsSite;
	}
	
	public static StarsLMHardware createStarsLMHardware(LiteLMHardware liteHw, Hashtable selectionLists) {
		StarsLMHardware starsHw = new StarsLMHardware();
		
		starsHw.setInventoryID( liteHw.getInventoryID() );
		starsHw.setCategory( StarsCustListEntryFactory.getStarsCustListEntry(
				(LiteCustomerSelectionList) selectionLists.get(CustomerSelectionList.LISTNAME_INVENTORYCATEGORY),
				liteHw.getCategoryID()).getContent() );
				
		InstallationCompany company = new InstallationCompany();
		company.setEntryID( liteHw.getInstallationCompanyID() );
		company.setContent( StarsCustListEntryFactory.getStarsCustListEntry(
				(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.report.ServiceCompany.LISTNAME_SERVICECOMPANY),
				liteHw.getInstallationCompanyID()).getContent() );
		starsHw.setInstallationCompany( company );
		
		starsHw.setReceiveDate( new Date(liteHw.getReceiveDate()) );
		starsHw.setInstallDate( new Date(liteHw.getInstallDate()) );
		starsHw.setRemoveDate( new Date(liteHw.getRemoveDate()) );
		starsHw.setAltTrackingNumber( liteHw.getAlternateTrackingNumber() );
		
		Voltage volt = new Voltage();
		volt.setEntryID( liteHw.getVoltageID() );
		volt.setContent( StarsCustListEntryFactory.getStarsCustListEntry(
				(LiteCustomerSelectionList) selectionLists.get(CustomerSelectionList.LISTNAME_INVENTORYVOLTAGE),
				liteHw.getVoltageID()).getContent() );
		starsHw.setVoltage( volt );
		
		starsHw.setNotes( liteHw.getNotes() );
		starsHw.setManufactureSerialNumber( liteHw.getManufactureSerialNumber() );
		
		LMDeviceType hwType = new LMDeviceType();
		hwType.setEntryID( liteHw.getLmHardwareTypeID() );
		hwType.setContent( StarsCustListEntryFactory.getStarsCustListEntry(
				(LiteCustomerSelectionList) selectionLists.get(CustomerSelectionList.LISTNAME_DEVICETYPE),
				liteHw.getLmHardwareTypeID()).getContent() );
		starsHw.setLMDeviceType( hwType );
		starsHw.setInstallationNotes( "" );
		
		DeviceStatus hwStatus = (DeviceStatus) StarsCustListEntryFactory.newStarsCustListEntry(
				StarsCustListEntryFactory.getStarsCustListEntry(
					(LiteCustomerSelectionList) selectionLists.get(CustomerSelectionList.LISTNAME_DEVICESTATUS),
					com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_DEVSTAT_UNAVAIL ),
				DeviceStatus.class );
		starsHw.setDeviceStatus( hwStatus );
		
		if (liteHw.getLmHardwareHistory() != null) {
			StarsLMHardwareHistory hwHist = new StarsLMHardwareHistory();
			for (int i = 0; i < liteHw.getLmHardwareHistory().size(); i++) {
				LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) liteHw.getLmHardwareHistory().get(i);
				StarsLMHardwareEvent starsEvent = new StarsLMHardwareEvent();
				setStarsLMCustomerEvent( starsEvent, liteEvent, selectionLists );
				hwHist.addStarsLMHardwareEvent( starsEvent );
			}
			starsHw.setStarsLMHardwareHistory( hwHist );
				
			// set hardware status and installation notes
			for (int i = hwHist.getStarsLMHardwareEventCount() - 1; i >= 0; i--) {
				if (hwHist.getStarsLMHardwareEvent(i).getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_CONFIG)) {
					hwStatus = (DeviceStatus) StarsCustListEntryFactory.newStarsCustListEntry(
							StarsCustListEntryFactory.getStarsCustListEntry(
								(LiteCustomerSelectionList) selectionLists.get(CustomerSelectionList.LISTNAME_DEVICESTATUS),
								CustomerListEntry.YUKONDEF_DEVSTAT_AVAIL ),
							DeviceStatus.class );
					starsHw.setDeviceStatus( hwStatus );
				}
				else if (hwHist.getStarsLMHardwareEvent(i).getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_COMPLETED )) {
					hwStatus = (DeviceStatus) StarsCustListEntryFactory.newStarsCustListEntry(
							StarsCustListEntryFactory.getStarsCustListEntry(
								(LiteCustomerSelectionList) selectionLists.get(CustomerSelectionList.LISTNAME_DEVICESTATUS),
								CustomerListEntry.YUKONDEF_DEVSTAT_AVAIL ),
							DeviceStatus.class );
					starsHw.setDeviceStatus( hwStatus );
					break;
				}
				else if (hwHist.getStarsLMHardwareEvent(i).getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_TEMPTERMINATION )) {
					hwStatus = (DeviceStatus) StarsCustListEntryFactory.newStarsCustListEntry(
							StarsCustListEntryFactory.getStarsCustListEntry(
								(LiteCustomerSelectionList) selectionLists.get(CustomerSelectionList.LISTNAME_DEVICESTATUS),
								CustomerListEntry.YUKONDEF_DEVSTAT_TEMPUNAVAIL ),
							DeviceStatus.class );
					starsHw.setDeviceStatus( hwStatus );
					break;
				}
			}
			
			for (int i = 0; i < hwHist.getStarsLMHardwareEventCount(); i++) {
				if (hwHist.getStarsLMHardwareEvent(i).getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_INSTALL )) {
					starsHw.setInstallationNotes( hwHist.getStarsLMHardwareEvent(i).getNotes() );
					break;
				}
			}
		}
		
		return starsHw;
	}
	
	public static StarsServiceRequest createStarsServiceRequest(LiteWorkOrderBase liteOrder, Hashtable selectionLists) {
		StarsServiceRequest starsOrder = new StarsServiceRequest();
		
		starsOrder.setOrderID( liteOrder.getOrderID() );
		starsOrder.setOrderNumber( liteOrder.getOrderNumber() );
		
		ServiceType servType = new ServiceType();
		servType.setEntryID( liteOrder.getWorkTypeID() );
		servType.setContent( StarsCustListEntryFactory.getStarsCustListEntry(
				(LiteCustomerSelectionList) selectionLists.get(CustomerSelectionList.LISTNAME_SERVICETYPE),
				liteOrder.getWorkTypeID()).getContent() );
		starsOrder.setServiceType( servType );
		
		ServiceCompany company = new ServiceCompany(); 
		company.setEntryID( liteOrder.getServiceCompanyID() );
		company.setContent( StarsCustListEntryFactory.getStarsCustListEntry(
				(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.report.ServiceCompany.LISTNAME_SERVICECOMPANY),
				liteOrder.getServiceCompanyID()).getContent() );
		starsOrder.setServiceCompany( company );
		
		starsOrder.setDateReported( new Date(liteOrder.getDateReported()) );
		starsOrder.setDateScheduled( new Date(liteOrder.getDateScheduled()) );
		starsOrder.setDateCompleted( new Date(liteOrder.getDateCompleted()) );
		starsOrder.setOrderedBy( liteOrder.getOrderedBy() );
		starsOrder.setDescription( liteOrder.getDescription() );
		starsOrder.setActionTaken( liteOrder.getActionTaken() );
		
		CurrentState status = new CurrentState();
		status.setEntryID( liteOrder.getCurrentStateID() );
		status.setContent( StarsCustListEntryFactory.getStarsCustListEntry(
				(LiteCustomerSelectionList) selectionLists.get(CustomerSelectionList.LISTNAME_SERVICESTATUS),
				liteOrder.getCurrentStateID()).getContent() );
		starsOrder.setCurrentState( status );
		
		return starsOrder;
	}
	
	public static StarsLMControlHistory createStarsLMControlHistory(LiteStarsLMControlHistory liteCtrlHist, StarsCtrlHistPeriod period, boolean getSummary) {
        StarsLMControlHistory starsCtrlHist = new StarsLMControlHistory();
        
        int startIndex = 0;
        if (period.getType() == StarsCtrlHistPeriod.PASTDAY_TYPE)
        	startIndex = liteCtrlHist.getCurrentDayStartIndex();
        else if (period.getType() == StarsCtrlHistPeriod.PASTWEEK_TYPE)
        	startIndex = liteCtrlHist.getCurrentWeekStartIndex();
        else if (period.getType() == StarsCtrlHistPeriod.PASTMONTH_TYPE)
        	startIndex = liteCtrlHist.getCurrentMonthStartIndex();
        else if (period.getType() == StarsCtrlHistPeriod.PASTYEAR_TYPE)
        	startIndex = liteCtrlHist.getCurrentYearStartIndex();
        
        for (int i = startIndex; i < liteCtrlHist.getLmControlHistory().size(); i++) {
        	LiteLMControlHistory lmCtrlHist = (LiteLMControlHistory) liteCtrlHist.getLmControlHistory().get(i);
        	
            ControlHistory hist = new ControlHistory();
            hist.setControlType( lmCtrlHist.getControlType() );
            hist.setStartDateTime( new Date(lmCtrlHist.getStartDateTime()) );
            hist.setControlDuration( (int) lmCtrlHist.getControlDuration() );
            starsCtrlHist.addControlHistory( hist );
        }

        if (getSummary) {
            ControlSummary summary = new ControlSummary();
            int dailyTime = 0;
            int monthlyTime = 0;
            int seasonalTime = 0;
            int annualTime = 0;
            
            for (int i = liteCtrlHist.getCurrentYearStartIndex(); i < liteCtrlHist.getLmControlHistory().size(); i++) {
	        	LiteLMControlHistory lmCtrlHist = (LiteLMControlHistory) liteCtrlHist.getLmControlHistory().get(i);
	        	
	        	annualTime += lmCtrlHist.getControlDuration();
	        	seasonalTime += lmCtrlHist.getControlDuration();	// Seasonal is the same as annual for now, need to revisit this
	        	if (i >= liteCtrlHist.getCurrentMonthStartIndex())
	        		monthlyTime += lmCtrlHist.getControlDuration();
	        	if (i >= liteCtrlHist.getCurrentDayStartIndex())
	        		dailyTime += lmCtrlHist.getControlDuration();
            }
            
            summary.setDailyTime( dailyTime );
            summary.setMonthlyTime( monthlyTime );
            summary.setSeasonalTime( seasonalTime );
            summary.setAnnualTime( annualTime );
            
            starsCtrlHist.setControlSummary( summary );
        }

        return starsCtrlHist;
	}
	
	public static StarsCustAccountInformation createStarsCustAccountInformation(LiteStarsCustAccountInformation liteAcctInfo, Integer energyCompanyID, boolean isOperator) {
		StarsCustAccountInformation starsAcctInfo = new StarsCustAccountInformation();
		Hashtable selectionLists = SOAPServer.getAllSelectionLists( energyCompanyID );
		
		LiteCustomerAccount liteAccount = liteAcctInfo.getCustomerAccount();
		LiteCustomerBase liteCustomer = liteAcctInfo.getCustomerBase();
		LiteAccountSite liteAcctSite = liteAcctInfo.getAccountSite();
		LiteSiteInformation liteSiteInfo = liteAcctInfo.getSiteInformation();
		
		StarsCustomerAccount starsAccount = new StarsCustomerAccount();
		starsAcctInfo.setStarsCustomerAccount( starsAccount );
		
		starsAccount.setAccountID( liteAccount.getAccountID() );
		starsAccount.setCustomerID( liteAccount.getCustomerID() );
		starsAccount.setAccountNumber( liteAccount.getAccountNumber() );
		
		int custTypeCommID = StarsCustListEntryFactory.getStarsCustListEntry(
				(LiteCustomerSelectionList) selectionLists.get(CustomerSelectionList.LISTNAME_CUSTOMERTYPE),
				CustomerListEntry.YUKONDEF_CUSTTYPE_COMM ).getEntryID();
		starsAccount.setIsCommercial( liteCustomer.getCustomerTypeID() == custTypeCommID );
		
		starsAccount.setCompany( "" );
		starsAccount.setAccountNotes( liteAccount.getAccountNotes() );
		starsAccount.setPropertyNumber( liteAcctSite.getSiteNumber() );
		starsAccount.setPropertyNotes( liteAcctSite.getPropertyNotes() );
		
		StreetAddress streetAddr = new StreetAddress();
		setStarsCustomerAddress( streetAddr, SOAPServer.getCustomerAddress(energyCompanyID, new Integer(liteAcctSite.getStreetAddressID())) );
		starsAccount.setStreetAddress( streetAddr );
		
		starsAccount.setStarsSiteInformation( createStarsSiteInformation(liteSiteInfo, selectionLists) );
				
		BillingAddress billAddr = new BillingAddress();
		setStarsCustomerAddress( billAddr, SOAPServer.getCustomerAddress(energyCompanyID, new Integer(liteAccount.getBillingAddressID())) );
		starsAccount.setBillingAddress( billAddr );
		
		PrimaryContact primContact = new PrimaryContact();
		setStarsCustomerContact( primContact, SOAPServer.getCustomerContact(energyCompanyID, new Integer(liteCustomer.getPrimaryContactID())) );
		starsAccount.setPrimaryContact( primContact );
		
		for (int i = 0; i < liteCustomer.getAdditionalContacts().size(); i++) {
			Integer contactID = (Integer) liteCustomer.getAdditionalContacts().get(i);
			AdditionalContact contact = new AdditionalContact();
			setStarsCustomerContact( contact, SOAPServer.getCustomerContact(energyCompanyID, contactID) );
			starsAccount.addAdditionalContact( contact );
		}
		
		ArrayList liteProgs = liteAcctInfo.getLmPrograms();
		StarsLMPrograms starsProgs = new StarsLMPrograms();
		starsAcctInfo.setStarsLMPrograms( starsProgs );
		
		ArrayList lProgs = SOAPServer.getAllLMPrograms( energyCompanyID );
		for (int i = 0; i < liteProgs.size(); i++) {
			LiteStarsLMProgram liteProg = (LiteStarsLMProgram) liteProgs.get(i);
			
			for (int j = 0; j < lProgs.size(); j++) {
				LiteLMProgram lProg = (LiteLMProgram) lProgs.get(j);
				if (lProg.getProgramID() == liteProg.getLmProgramID()) {
					StarsLMProgram starsProg = new StarsLMProgram();
					starsProg.setProgramID( lProg.getProgramID() );
					starsProg.setGroupID( liteProg.getGroupID() );
					starsProg.setProgramName( lProg.getProgramName() );
					
					ArrayList liteApps = liteAcctInfo.getAppliances();
					for (int k = 0; k < liteApps.size(); k++) {
						StarsAppliance starsApp = (StarsAppliance) liteApps.get(k);
						if (starsApp.getLmProgramID() == lProg.getProgramID())
							starsProg.setApplianceCategoryID( starsApp.getApplianceCategoryID() );
					}
			
					LiteStarsLMControlHistory liteCtrlHist = SOAPServer.getLMControlHistory( energyCompanyID, new Integer(liteProg.getGroupID()) );
					if (liteCtrlHist != null)
						starsProg.setStarsLMControlHistory( createStarsLMControlHistory(liteCtrlHist, StarsCtrlHistPeriod.PASTDAY, true) );
					
					if (liteProg.getProgramHistory() != null) {
						StarsLMProgramHistory progHist = new StarsLMProgramHistory();
						for (int k = 0; k < liteProg.getProgramHistory().size(); k++) {
							LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) liteProg.getProgramHistory().get(k);
							StarsLMProgramEvent starsEvent = new StarsLMProgramEvent();
							setStarsLMCustomerEvent( starsEvent, liteEvent, selectionLists );
							progHist.addStarsLMProgramEvent( starsEvent );
						}
						starsProg.setStarsLMProgramHistory( progHist );
						
						if (ServerUtils.isInService(
								liteProg.getProgramHistory(),
								new Integer(StarsCustListEntryFactory.getStarsCustListEntry(
									(LiteCustomerSelectionList) selectionLists.get(CustomerSelectionList.LISTNAME_LMCUSTOMERACTION),
									CustomerListEntry.YUKONDEF_ACT_FUTUREACTIVATION).getEntryID()),
								new Integer(StarsCustListEntryFactory.getStarsCustListEntry(
									(LiteCustomerSelectionList) selectionLists.get(CustomerSelectionList.LISTNAME_LMCUSTOMERACTION),
									CustomerListEntry.YUKONDEF_ACT_COMPLETED).getEntryID())
							))
							starsProg.setStatus( "In Service" );
						else
							starsProg.setStatus( "Out of Service" );
					}
					
					starsProgs.addStarsLMProgram( starsProg );
					break;
				}
			}
		}
		
		if (isOperator) {
			ArrayList liteApps = liteAcctInfo.getAppliances();
			StarsAppliances starsApps = new StarsAppliances();
			starsAcctInfo.setStarsAppliances( starsApps );
			
			for (int i = 0; i < liteApps.size(); i++) {
				StarsAppliance starsApp = (StarsAppliance) liteApps.get(i);
				starsApps.addStarsAppliance( starsApp );
			}
			
			ArrayList liteInvs = liteAcctInfo.getInventories();
			StarsInventories starsInvs = new StarsInventories();
			starsAcctInfo.setStarsInventories( starsInvs );
			
			for (int i = 0; i < liteInvs.size(); i++) {
				LiteLMHardware liteHw = SOAPServer.getLMHardware( energyCompanyID, (Integer) liteInvs.get(i), true );
				starsInvs.addStarsLMHardware( createStarsLMHardware(liteHw, selectionLists) );
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
				LiteWorkOrderBase liteOrder = SOAPServer.getWorkOrderBase( energyCompanyID, (Integer) liteOrders.get(i) );
				starsOrders.addStarsServiceRequest( createStarsServiceRequest(liteOrder, selectionLists) );
			}
		}
		
		return starsAcctInfo;
	}
	
	public static StarsCustSelectionList createStarsCustSelectionList(LiteCustomerSelectionList liteList) {
		StarsCustSelectionList starsList = new StarsCustSelectionList();
		starsList.setListID( liteList.getListID() );
		starsList.setListName( liteList.getListName() );
		
		StarsSelectionListEntry[] entries = liteList.getListEntries();
		for (int i = 0; i < entries.length; i++)
			starsList.addStarsSelectionListEntry( entries[i] );
			
		return starsList;
	}
	
	public static StarsWebConfig createStarsWebConfig(LiteWebConfiguration liteWebConfig) {
		StarsWebConfig starsWebConfig = new StarsWebConfig();
		starsWebConfig.setLogoLocation( liteWebConfig.getLogoLocation() );
		starsWebConfig.setDescription( liteWebConfig.getDescription() );
		starsWebConfig.setAlternateDisplayName( liteWebConfig.getAlternateDisplayName() );
		starsWebConfig.setURL( liteWebConfig.getUrl() );
		
		return starsWebConfig;
	}
	
	public static StarsApplianceCategory createStarsApplianceCategory(LiteApplianceCategory liteAppCat, ArrayList liteProgs) {
		StarsApplianceCategory starsAppCat = new StarsApplianceCategory();
		starsAppCat.setApplianceCategoryID( liteAppCat.getApplianceCategoryID() );
		starsAppCat.setCategoryID( liteAppCat.getCategoryID() );
		starsAppCat.setDescription( liteAppCat.getDescription() );
		
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
	
	
	public static StarsAppliance createStarsAppliance(com.cannontech.database.data.stars.appliance.ApplianceBase app, Integer energyCompanyID) {
        StarsAppliance starsApp = new StarsAppliance();
        
        starsApp.setApplianceID( app.getApplianceBase().getApplianceID().intValue() );
        starsApp.setApplianceCategoryID( app.getApplianceBase().getApplianceCategoryID().intValue() );
        if (app.getLMHardwareConfig().getInventoryID() != null)
        	starsApp.setInventoryID( app.getLMHardwareConfig().getInventoryID().intValue() );
    	starsApp.setLmProgramID( app.getApplianceBase().getLMProgramID().intValue() );
        starsApp.setManufacturer( "" );
        starsApp.setManufactureYear( "" );
        starsApp.setLocation( "" );
        starsApp.setServiceCompany( new ServiceCompany() );
        starsApp.setNotes( app.getApplianceBase().getNotes() );
        
        LiteApplianceCategory liteAppCat = SOAPServer.getApplianceCategory( energyCompanyID, app.getApplianceBase().getApplianceCategoryID() );
        if (liteAppCat != null)
	        starsApp.setCategoryName( liteAppCat.getDescription() );
	    else
	    	starsApp.setCategoryName( "(Unknown)" );
        
        return starsApp;
	}
	
	
	public static boolean isIdentical(LiteBase lite, Object obj) {
		if (lite instanceof LiteCustomerContact) {
			if (obj instanceof StarsCustomerContact)
				return isIdenticalCustomerContact( (LiteCustomerContact) lite, (StarsCustomerContact) obj );
		}
		else if (lite instanceof LiteCustomerAddress) {
			if (obj instanceof StarsCustomerAddress)
				return isIdenticalCustomerAddress( (LiteCustomerAddress) lite, (StarsCustomerAddress) obj );
		}
		else if (lite instanceof LiteSiteInformation) {
			if (obj instanceof StarsSiteInformation)
				return isIdenticalSiteInformation( (LiteSiteInformation) lite, (StarsSiteInformation) obj );
		}
		else if (lite instanceof LiteCustomerAccount) {
			if (obj instanceof StarsCustomerAccount)
				return isIdenticalCustomerAccount( (LiteCustomerAccount) lite, (StarsCustomerAccount) obj );
		}
		else if (lite instanceof LiteCustomerBase) {
			if (obj instanceof StarsCustomerAccount)
				return isIdenticalCustomerBase( (LiteCustomerBase) lite, (StarsCustomerAccount) obj );
		}
		else if (lite instanceof LiteAccountSite) {
			if (obj instanceof StarsCustomerAccount)
				return isIdenticalAccountSite( (LiteAccountSite) lite, (StarsCustomerAccount) obj );
		}
		
		return false;
	}
	
	public static boolean isIdenticalCustomerContact(LiteCustomerContact liteContact, StarsCustomerContact starsContact) {
		return (liteContact.getLastName().equals( starsContact.getLastName() )
				&& liteContact.getFirstName().equals( starsContact.getFirstName() )
				&& liteContact.getHomePhone().equals( starsContact.getHomePhone() )
				&& liteContact.getWorkPhone().equals( starsContact.getWorkPhone() ));
	}
	
	public static boolean isIdenticalCustomerAddress(LiteCustomerAddress liteAddr, StarsCustomerAddress starsAddr) {
		return (liteAddr.getLocationAddress1().equals( starsAddr.getStreetAddr1() )
				&& liteAddr.getLocationAddress2().equals( starsAddr.getStreetAddr2() )
				&& liteAddr.getCityName().equals( starsAddr.getCity() )
				&& liteAddr.getStateCode().equals( starsAddr.getState() )
				&& liteAddr.getZipCode().equals( starsAddr.getZip() ));
	}
	
	public static boolean isIdenticalSiteInformation(LiteSiteInformation liteSite, StarsSiteInformation starsSite) {
		return (liteSite.getFeeder().equals( starsSite.getFeeder() )
				&& liteSite.getPole().equals( starsSite.getPole() )
				&& liteSite.getTransformerSize().equals( starsSite.getTransformerSize() )
				&& liteSite.getServiceVoltage().equals( starsSite.getServiceVoltage() )
				&& liteSite.getSubstationID() == starsSite.getSubstation().getEntryID());
	}
	
	public static boolean isIdenticalCustomerAccount(LiteCustomerAccount liteAccount, StarsCustomerAccount starsAccount) {
		return (liteAccount.getAccountNumber().equals( starsAccount.getAccountNumber() )
				&& liteAccount.getAccountNotes().equals( starsAccount.getAccountNotes() ));
	}
	
	public static boolean isIdenticalCustomerBase(LiteCustomerBase liteCustomer, StarsCustomerAccount starsCustomer) {
		// Should compare customerTypeID with isCommercial
		return true;
	}
	
	public static boolean isIdenticalAccountSite(LiteAccountSite liteAcctSite, StarsCustomerAccount starsAcctSite) {
		return (liteAcctSite.getSiteNumber().equalsIgnoreCase( starsAcctSite.getPropertyNumber() )
				&& liteAcctSite.getPropertyNotes().equalsIgnoreCase( starsAcctSite.getPropertyNotes() ));
	}
}
