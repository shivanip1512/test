package com.cannontech.stars.database.data.lite;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListEnum;
import com.cannontech.common.constants.YukonSelectionListOrder;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.macro.GenericMacro;
import com.cannontech.database.db.macro.MacroTypes;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsWorkOrderBaseDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.hardware.InventoryBase;
import com.cannontech.stars.database.db.appliance.ApplianceChiller;
import com.cannontech.stars.database.db.appliance.ApplianceDualStageAirCond;
import com.cannontech.stars.database.db.hardware.MeterHardwareBase;
import com.cannontech.stars.database.db.report.ServiceCompanyDesignationCode;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.event.dao.LMHardwareEventDao;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.OptOutEventQueue;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.*;

public class StarsLiteFactory {
    private final static LMHardwareEventDao hardwareEventDao = YukonSpringHook.getBean(LMHardwareEventDao.class);
    private final static AddressDao addressDao = YukonSpringHook.getBean(AddressDao.class);
    private final static SelectionListService selectionListService  = YukonSpringHook.getBean(SelectionListService.class);

    public static LiteBase createLite( com.cannontech.database.db.DBPersistent db ) {
        LiteBase lite = null;
        
        if (db instanceof com.cannontech.database.data.customer.Contact) {
            lite = new LiteContact(0);
            setLiteContact( (LiteContact) lite, (com.cannontech.database.data.customer.Contact) db );
        } else if (db instanceof com.cannontech.database.db.customer.Address) {
            lite = new LiteAddress();
            setLiteAddress( (LiteAddress) lite, (com.cannontech.database.db.customer.Address) db );
        } else if (db instanceof com.cannontech.stars.database.data.hardware.LMHardwareBase) {
            lite = new LiteLmHardwareBase();
            setLiteStarsLMHardware( (LiteLmHardwareBase) lite, (com.cannontech.stars.database.data.hardware.LMHardwareBase) db );
        } else if (db instanceof com.cannontech.stars.database.data.hardware.MeterHardwareBase) {
            lite = new LiteMeterHardwareBase();
            setLiteMeterHardwareBase( (LiteMeterHardwareBase) lite, (com.cannontech.stars.database.data.hardware.MeterHardwareBase) db );
        } else if (db instanceof com.cannontech.stars.database.data.hardware.MeterHardwareBase) {
            lite = new LiteInventoryBase();
            setLiteInventoryBase( (LiteInventoryBase) lite, ((com.cannontech.stars.database.data.hardware.MeterHardwareBase)db).getInventoryBase() );
        } else if (db instanceof com.cannontech.stars.database.data.event.LMHardwareEvent) {
            lite = new LiteLMHardwareEvent();
            setLiteLMHardwareEvent( (LiteLMHardwareEvent) lite, (com.cannontech.stars.database.data.event.LMHardwareEvent) db );
        } else if (db instanceof com.cannontech.stars.database.data.event.LMProgramEvent) {
            lite = new LiteLMProgramEvent();
            setLiteLMProgramEvent( (LiteLMProgramEvent) lite, (com.cannontech.stars.database.data.event.LMProgramEvent) db );
        } else if (db instanceof com.cannontech.stars.database.data.report.WorkOrderBase) {
            lite = new LiteWorkOrderBase();
            setLiteWorkOrderBase( (LiteWorkOrderBase) lite, (com.cannontech.stars.database.data.report.WorkOrderBase) db );
        }
        else if (db instanceof com.cannontech.stars.database.db.customer.CustomerAccount) {
            lite = new LiteCustomerAccount();
            setLiteCustomerAccount( (LiteCustomerAccount) lite, (com.cannontech.stars.database.db.customer.CustomerAccount) db );
        } else if (db instanceof com.cannontech.stars.database.db.customer.AccountSite) {
            lite = new LiteAccountSite();
            setLiteAccountSite( (LiteAccountSite) lite, (com.cannontech.stars.database.db.customer.AccountSite) db );
        } else if (db instanceof com.cannontech.stars.database.db.customer.SiteInformation) {
            lite = new LiteSiteInformation();
            setLiteSiteInformation( (LiteSiteInformation) lite, (com.cannontech.stars.database.db.customer.SiteInformation) db );
        } else if (db instanceof com.cannontech.database.db.pao.LMControlHistory) {
            lite = new LiteLMControlHistory();
            setLiteLMControlHistory( (LiteLMControlHistory) lite, (com.cannontech.database.db.pao.LMControlHistory) db );
        } else if (db instanceof com.cannontech.stars.database.data.event.LMThermostatManualEvent) {
            lite = new LiteLMThermostatManualEvent();
            setLiteLMThermostatManualEvent( (LiteLMThermostatManualEvent) lite, (com.cannontech.stars.database.data.event.LMThermostatManualEvent) db );
        } else if (db instanceof com.cannontech.stars.database.data.appliance.ApplianceBase) {
            lite = new LiteStarsAppliance();
            setLiteStarsAppliance( (LiteStarsAppliance) lite, (com.cannontech.stars.database.data.appliance.ApplianceBase) db );
        } else if (db instanceof com.cannontech.stars.database.db.customer.CustomerResidence) {
            lite = new LiteCustomerResidence();
            setLiteCustomerResidence( (LiteCustomerResidence) lite, (com.cannontech.stars.database.db.customer.CustomerResidence) db );
        } else if (db instanceof com.cannontech.stars.database.db.appliance.ApplianceCategory) {
            lite = new LiteApplianceCategory();
            setLiteApplianceCategory( (LiteApplianceCategory) lite, (com.cannontech.stars.database.db.appliance.ApplianceCategory) db );
        } else if (db instanceof com.cannontech.stars.database.data.report.ServiceCompany) {
            lite = new LiteServiceCompany();
            setLiteServiceCompany( (LiteServiceCompany) lite, (com.cannontech.stars.database.data.report.ServiceCompany) db );
        } else if (db instanceof ServiceCompanyDesignationCode) {
            lite = new LiteServiceCompanyDesignationCode();
            setLiteServiceCompanyDesignationCode( (LiteServiceCompanyDesignationCode) lite, (ServiceCompanyDesignationCode) db );
        } else if (db instanceof com.cannontech.stars.database.db.Substation) {
            lite = new LiteSubstation();
            setLiteSubstation( (LiteSubstation) lite, (com.cannontech.stars.database.db.Substation) db );
        } else if (db instanceof com.cannontech.database.db.web.YukonWebConfiguration) {
            lite = new LiteWebConfiguration();
            setLiteWebConfiguration( (LiteWebConfiguration) lite, (com.cannontech.database.db.web.YukonWebConfiguration) db );
        } else if (db instanceof com.cannontech.stars.database.db.LMProgramWebPublishing) {
            lite = new LiteLMProgramWebPublishing();
            setLiteLMProgramWebPublishing( (LiteLMProgramWebPublishing) lite, (com.cannontech.stars.database.db.LMProgramWebPublishing) db );
        } else {
            CTILogger.error(" ***** NO lite object created: " + db.getClass());
        }

        return lite;
    }
    
    public static void setLiteContact(LiteContact liteContact, com.cannontech.database.data.customer.Contact contact) {
        liteContact.setContactID( contact.getContact().getContactID().intValue() );
        liteContact.setContLastName( contact.getContact().getContLastName() );
        liteContact.setContFirstName( contact.getContact().getContFirstName() );
        liteContact.setLoginID( contact.getContact().getLogInID().intValue() );
        liteContact.setAddressID( contact.getContact().getAddressID().intValue() );
        
        liteContact.getLiteContactNotifications().clear();  
        
        for (int i = 0; i < contact.getContactNotifVect().size(); i++) {
            com.cannontech.database.db.contact.ContactNotification notif =
                    contact.getContactNotifVect().get(i);
            
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
    
    public static void setLiteInventoryBase(LiteInventoryBase liteInv, com.cannontech.stars.database.db.hardware.InventoryBase invDB) {
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
        liteInv.setCurrentStateID(invDB.getCurrentStateID().intValue());
        
    }
    
    public static void setLiteStarsLMHardware(LiteLmHardwareBase liteHw, com.cannontech.stars.database.data.hardware.LMHardwareBase hw) {
        setLiteInventoryBase( liteHw, hw.getInventoryBase() );
        
        liteHw.setManufacturerSerialNumber( hw.getLMHardwareBase().getManufacturerSerialNumber() );
        liteHw.setLmHardwareTypeID( hw.getLMHardwareBase().getLMHardwareTypeID().intValue() );
        liteHw.setRouteID( hw.getLMHardwareBase().getRouteID().intValue() );
        liteHw.setConfigurationID( hw.getLMHardwareBase().getConfigurationID().intValue() );
    }
    
    public static void setLiteMeterHardwareBase(LiteMeterHardwareBase liteHw, com.cannontech.stars.database.data.hardware.MeterHardwareBase hw) {
        setLiteInventoryBase( liteHw, hw.getInventoryBase() );
        
        liteHw.setMeterNumber( hw.getMeterHardwareBase().getMeterNumber() );
        liteHw.setMeterTypeID( hw.getMeterHardwareBase().getMeterTypeID().intValue() );
    }
    
    public static void setLiteLMConfiguration(LiteLMConfiguration liteCfg, com.cannontech.stars.database.data.hardware.LMConfigurationBase cfg) {
        liteCfg.setConfigurationID( cfg.getLMConfigurationBase().getConfigurationID().intValue() );
        liteCfg.setColdLoadPickup( cfg.getLMConfigurationBase().getColdLoadPickup() );
        liteCfg.setTamperDetect( cfg.getLMConfigurationBase().getTamperDetect() );
        
        if (cfg.getExpressCom() != null) {
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
        else if (cfg.getSA205() != null) {
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
        else if (cfg.getSASimple() != null) {
            LiteLMConfiguration.SASimple simple = new LiteLMConfiguration.SASimple();
            simple.setOperationalAddress( cfg.getSASimple().getOperationalAddress() );
            liteCfg.setSASimple( simple );
        }
    }
    
    public static void extendLiteInventoryBase(LiteInventoryBase liteInv, LiteStarsEnergyCompany energyCompany) {
        if (liteInv instanceof LiteLmHardwareBase) {
            LiteLmHardwareBase liteHw = (LiteLmHardwareBase) liteInv;
            if (liteHw.isThermostat()) {
                liteHw.setThermostatSettings( energyCompany.getThermostatSettings(liteHw) );
            }
        }
        
        liteInv.setExtended( true );
    }
    
    public static void setLiteLMCustomerEvent(LiteLMCustomerEvent liteEvent, com.cannontech.stars.database.db.event.LMCustomerEventBase event) {
        liteEvent.setEventID( event.getEventID().intValue() );
        liteEvent.setActionID( event.getActionID().intValue() );
        liteEvent.setEventDateTime( event.getEventDateTime().getTime() );
        liteEvent.setEventTypeID( event.getEventTypeID().intValue() );
        liteEvent.setNotes( event.getNotes() );
    }
    
    public static void setLiteLMHardwareEvent(LiteLMHardwareEvent liteEvent, com.cannontech.stars.database.data.event.LMHardwareEvent event) {
        setLiteLMCustomerEvent( liteEvent, event.getLMCustomerEventBase() );
        liteEvent.setInventoryID( event.getLMHardwareEvent().getInventoryID().intValue() );
    }
    
    public static void setLiteLMProgramEvent(LiteLMProgramEvent liteEvent, com.cannontech.stars.database.data.event.LMProgramEvent event) {
        setLiteLMCustomerEvent( liteEvent, event.getLMCustomerEventBase() );
        liteEvent.setProgramID( event.getLMProgramEvent().getProgramID().intValue() );
    }
    
    public static void setLiteWorkOrderBase(LiteWorkOrderBase liteOrder, com.cannontech.stars.database.data.report.WorkOrderBase order) {
        liteOrder.setOrderID( order.getWorkOrderBase().getOrderID().intValue() );
        liteOrder.setOrderNumber( order.getWorkOrderBase().getOrderNumber() );
        liteOrder.setWorkTypeID( order.getWorkOrderBase().getWorkTypeID().intValue() );
        liteOrder.setCurrentStateID( order.getWorkOrderBase().getCurrentStateID().intValue() );
        liteOrder.setServiceCompanyID( order.getWorkOrderBase().getServiceCompanyID().intValue() );
        liteOrder.setDateReported( order.getWorkOrderBase().getDateReported().getTime() );
        liteOrder.setDateScheduled( order.getWorkOrderBase().getDateScheduled().getTime() );
        liteOrder.setDateCompleted( order.getWorkOrderBase().getDateCompleted().getTime() );
        liteOrder.setDescription( order.getWorkOrderBase().getDescription() );
        liteOrder.setActionTaken( order.getWorkOrderBase().getActionTaken() );
        liteOrder.setOrderedBy( order.getWorkOrderBase().getOrderedBy() );
        liteOrder.setAccountID( order.getWorkOrderBase().getAccountID().intValue() );
        liteOrder.setAdditionalOrderNumber(order.getWorkOrderBase().getAdditionalOrderNumber());
        liteOrder.setEnergyCompanyID( order.getEnergyCompanyID().intValue());
    }
    
    public static void setLiteSiteInformation(LiteSiteInformation liteSiteInfo, com.cannontech.stars.database.db.customer.SiteInformation siteInfo) {
        liteSiteInfo.setSiteID( siteInfo.getSiteID().intValue() );
        liteSiteInfo.setFeeder( siteInfo.getFeeder() );
        liteSiteInfo.setPole( siteInfo.getPole() );
        liteSiteInfo.setTransformerSize( siteInfo.getTransformerSize() );
        liteSiteInfo.setServiceVoltage( siteInfo.getServiceVoltage() );
        liteSiteInfo.setSubstationID( siteInfo.getSubstationID().intValue() );
    }
    
    public static void setLiteCustomerAccount(LiteCustomerAccount liteAccount, com.cannontech.stars.database.db.customer.CustomerAccount account) {
        liteAccount.setAccountID( account.getAccountID().intValue() );
        liteAccount.setAccountSiteID( account.getAccountSiteID().intValue() );
        liteAccount.setAccountNumber( account.getAccountNumber() );
        liteAccount.setCustomerID( account.getCustomerID().intValue() );
        liteAccount.setBillingAddressID( account.getBillingAddressID().intValue() );
        liteAccount.setAccountNotes( account.getAccountNotes() );
    }

    public static void setLiteAccountSite(LiteAccountSite liteAcctSite, com.cannontech.stars.database.db.customer.AccountSite acctSite) {
        liteAcctSite.setAccountSiteID( acctSite.getAccountSiteID().intValue() );
        liteAcctSite.setSiteInformationID( acctSite.getSiteInformationID().intValue() );
        liteAcctSite.setSiteNumber( acctSite.getSiteNumber() );
        liteAcctSite.setStreetAddressID( acctSite.getStreetAddressID().intValue() );
        liteAcctSite.setPropertyNotes( acctSite.getPropertyNotes() );
        liteAcctSite.setCustAtHome( acctSite.getCustAtHome());
        liteAcctSite.setCustStatus( acctSite.getCustomerStatus() );
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
    
    public static void setLiteLMThermostatManualEvent(LiteLMThermostatManualEvent liteEvent, com.cannontech.stars.database.data.event.LMThermostatManualEvent event) {
        setLiteLMCustomerEvent(liteEvent, event.getLMCustomerEventBase());
        liteEvent.setInventoryID( event.getLmThermostatManualEvent().getInventoryID().intValue() );
        liteEvent.setHoldTemperature( event.getLmThermostatManualEvent().getHoldTemperature().equalsIgnoreCase("Y") );
        liteEvent.setOperationStateID( event.getLmThermostatManualEvent().getOperationStateID().intValue() );
        liteEvent.setFanOperationID( event.getLmThermostatManualEvent().getFanOperationID().intValue() );
        if (event.getLmThermostatManualEvent().getPreviousCoolTemperature() != null) {
            liteEvent.setPreviousCoolTemperature( event.getLmThermostatManualEvent().getPreviousCoolTemperature() );
        }
        if (event.getLmThermostatManualEvent().getPreviousHeatTemperature() != null) {
            liteEvent.setPreviousHeatTemperature( event.getLmThermostatManualEvent().getPreviousHeatTemperature() );
        }
    }
    
    public static void setLiteStarsAppliance(LiteStarsAppliance liteApp, com.cannontech.stars.database.data.appliance.ApplianceBase app) {
        liteApp.setApplianceID( app.getApplianceBase().getApplianceID().intValue() );
        liteApp.setAccountID( app.getApplianceBase().getAccountID().intValue() );
        
        ApplianceCategoryDao applianceCategoryDao = 
            YukonSpringHook.getBean("applianceCategoryDao", ApplianceCategoryDao.class);
        ApplianceCategory applianceCategory = 
            applianceCategoryDao.getById(app.getApplianceBase().getApplianceCategoryID());
        liteApp.setApplianceCategory(applianceCategory);

        liteApp.setProgramID( app.getApplianceBase().getProgramID().intValue() );
        liteApp.setYearManufactured( app.getApplianceBase().getYearManufactured().intValue() );
        liteApp.setManufacturerID( app.getApplianceBase().getManufacturerID().intValue() );
        liteApp.setLocationID( app.getApplianceBase().getLocationID().intValue() );
        liteApp.setNotes( app.getApplianceBase().getNotes() );
        liteApp.setModelNumber( app.getApplianceBase().getModelNumber() );
        liteApp.setKwCapacity( app.getApplianceBase().getKWCapacity().doubleValue() );
        liteApp.setEfficiencyRating( app.getApplianceBase().getEfficiencyRating().doubleValue() );
        
        if (app.getLMHardwareConfig() != null && app.getLMHardwareConfig().getInventoryID() != null) {
            liteApp.setInventoryID( app.getLMHardwareConfig().getInventoryID().intValue() );
            liteApp.setAddressingGroupID( app.getLMHardwareConfig().getAddressingGroupID().intValue() );
            liteApp.setLoadNumber( app.getLMHardwareConfig().getLoadNumber().intValue() );
        }
    }
    
    public static void setLiteAppAirConditioner(LiteStarsAppliance.AirConditioner liteAppAC, com.cannontech.stars.database.db.appliance.ApplianceAirConditioner appAC) {
        liteAppAC.setTonnageID( appAC.getTonnageID().intValue() );
        liteAppAC.setTypeID( appAC.getTypeID().intValue() );
    }
      
    public static void setLiteAppDualStageAirCond(LiteStarsAppliance.DualStageAirCond liteAppDS, ApplianceDualStageAirCond appDS) {
        liteAppDS.setStageOneTonnageID( appDS.getStageOneTonnageID().intValue() );
        liteAppDS.setStageTwoTonnageID( appDS.getStageTwoTonnageID().intValue() );
        liteAppDS.setTypeID( appDS.getTypeID().intValue() );
    }
    
    public static void setLiteAppChiller(LiteStarsAppliance.Chiller liteAppChill, ApplianceChiller appChill) {
        liteAppChill.setTonnageID( appChill.getTonnageID().intValue() );
        liteAppChill.setTypeID( appChill.getTypeID().intValue() );
    }
    
    public static void setLiteAppWaterHeater(LiteStarsAppliance.WaterHeater liteAppWH, com.cannontech.stars.database.db.appliance.ApplianceWaterHeater appWH) {
        liteAppWH.setNumberOfGallonsID( appWH.getNumberOfGallonsID().intValue() );
        liteAppWH.setEnergySourceID( appWH.getEnergySourceID().intValue() );
        liteAppWH.setNumberOfElements( appWH.getNumberOfElements().intValue() );
    }
    
    public static void setLiteAppDualFuel(LiteStarsAppliance.DualFuel liteAppDF, com.cannontech.stars.database.db.appliance.ApplianceDualFuel appDF) {
        liteAppDF.setSwitchOverTypeID( appDF.getSwitchOverTypeID().intValue() );
        liteAppDF.setSecondaryKWCapacity( appDF.getSecondaryKWCapacity().intValue() );
        liteAppDF.setSecondaryEnergySourceID( appDF.getSecondaryEnergySourceID().intValue() );
    }
    
    public static void setLiteAppGenerator(LiteStarsAppliance.Generator liteAppGen, com.cannontech.stars.database.db.appliance.ApplianceGenerator appGen) {
        liteAppGen.setTransferSwitchTypeID( appGen.getTransferSwitchTypeID().intValue() );
        liteAppGen.setTransferSwitchMfgID( appGen.getTransferSwitchMfgID().intValue() );
        liteAppGen.setPeakKWCapacity( appGen.getPeakKWCapacity().intValue() );
        liteAppGen.setFuelCapGallons( appGen.getFuelCapGallons().intValue() );
        liteAppGen.setStartDelaySeconds( appGen.getStartDelaySeconds().intValue() );
    }
    
    public static void setLiteAppGrainDryer(LiteStarsAppliance.GrainDryer liteAppGD, com.cannontech.stars.database.db.appliance.ApplianceGrainDryer appGD) {
        liteAppGD.setDryerTypeID( appGD.getDryerTypeID().intValue() );
        liteAppGD.setBinSizeID( appGD.getBinSizeID().intValue() );
        liteAppGD.setBlowerEnergySourceID( appGD.getBlowerEnergySourceID().intValue() );
        liteAppGD.setBlowerHorsePowerID( appGD.getBlowerHorsePowerID().intValue() );
        liteAppGD.setBlowerHeatSourceID( appGD.getBlowerHeatSourceID().intValue() );
    }
    
    public static void setLiteAppStorageHeat(LiteStarsAppliance.StorageHeat liteAppSH, com.cannontech.stars.database.db.appliance.ApplianceStorageHeat appSH) {
        liteAppSH.setStorageTypeID( appSH.getStorageTypeID().intValue() );
        liteAppSH.setPeakKWCapacity( appSH.getPeakKWCapacity().intValue() );
        liteAppSH.setHoursToRecharge( appSH.getHoursToRecharge().intValue() );
    }
    
    public static void setLiteAppHeatPump(LiteStarsAppliance.HeatPump liteAppHP, com.cannontech.stars.database.db.appliance.ApplianceHeatPump appHP) {
        liteAppHP.setPumpTypeID( appHP.getPumpTypeID().intValue() );
        liteAppHP.setPumpSizeID( appHP.getPumpSizeID().intValue() );
        liteAppHP.setStandbySourceID( appHP.getStandbySourceID().intValue() );
        liteAppHP.setSecondsDelayToRestart( appHP.getSecondsDelayToRestart().intValue() );
    }
    
    public static void setLiteAppIrrigation(LiteStarsAppliance.Irrigation liteAppIrr, com.cannontech.stars.database.db.appliance.ApplianceIrrigation appIrr) {
        liteAppIrr.setIrrigationTypeID( appIrr.getIrrigationTypeID().intValue() );
        liteAppIrr.setHorsePowerID( appIrr.getHorsePowerID().intValue() );
        liteAppIrr.setEnergySourceID( appIrr.getEnergySourceID().intValue() );
        liteAppIrr.setSoilTypeID( appIrr.getSoilTypeID().intValue() );
        liteAppIrr.setMeterLocationID( appIrr.getMeterLocationID().intValue() );
        liteAppIrr.setMeterVoltageID( appIrr.getMeterVoltageID().intValue() );
    }
    
    public static void setLiteCustomerResidence(LiteCustomerResidence liteRes, com.cannontech.stars.database.db.customer.CustomerResidence res) {
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
    
    public static LiteStarsAppliance createLiteStarsAppliance(com.cannontech.stars.database.data.appliance.ApplianceBase appliance, LiteStarsEnergyCompany energyCompany) {
        LiteStarsAppliance liteApp = new LiteStarsAppliance();
        setLiteStarsAppliance( liteApp, appliance );
        
        LiteApplianceCategory liteAppCat = energyCompany.getApplianceCategory( appliance.getApplianceBase().getApplianceCategoryID().intValue() );
        if (liteAppCat.getCategoryID() == selectionListService.getListEntry(energyCompany, YukonListEntryTypes.YUK_DEF_ID_APP_CAT_AIR_CONDITIONER).getEntryID()) {
            com.cannontech.stars.database.db.appliance.ApplianceAirConditioner app =
                    com.cannontech.stars.database.db.appliance.ApplianceAirConditioner.getApplianceAirConditioner( appliance.getApplianceBase().getApplianceID() );
            if (app != null) {
                liteApp.setAirConditioner( new LiteStarsAppliance.AirConditioner() );
                StarsLiteFactory.setLiteAppAirConditioner( liteApp.getAirConditioner(), app );
            }
        }
        else if (liteAppCat.getCategoryID() == selectionListService.getListEntry(energyCompany, YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUALSTAGE).getEntryID()) {
            com.cannontech.stars.database.db.appliance.ApplianceDualStageAirCond app =
                    com.cannontech.stars.database.db.appliance.ApplianceDualStageAirCond.getApplianceDualStageAirCond( appliance.getApplianceBase().getApplianceID() );
            if (app != null) {
                liteApp.setDualStageAirCond( new LiteStarsAppliance.DualStageAirCond() );
                StarsLiteFactory.setLiteAppDualStageAirCond( liteApp.getDualStageAirCond(), app );
            }
        }
        else if (liteAppCat.getCategoryID() == selectionListService.getListEntry(energyCompany, YukonListEntryTypes.YUK_DEF_ID_APP_CAT_CHILLER).getEntryID()) {
            com.cannontech.stars.database.db.appliance.ApplianceChiller app =
                    com.cannontech.stars.database.db.appliance.ApplianceChiller.getApplianceChiller( appliance.getApplianceBase().getApplianceID() );
            if (app != null) {
                liteApp.setChiller( new LiteStarsAppliance.Chiller() );
                StarsLiteFactory.setLiteAppChiller( liteApp.getChiller(), app );
            }
        }
        else if (liteAppCat.getCategoryID() == selectionListService.getListEntry(energyCompany, YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER).getEntryID()) {
            com.cannontech.stars.database.db.appliance.ApplianceWaterHeater app =
                    com.cannontech.stars.database.db.appliance.ApplianceWaterHeater.getApplianceWaterHeater( appliance.getApplianceBase().getApplianceID() );
            if (app != null) {
                liteApp.setWaterHeater( new LiteStarsAppliance.WaterHeater() );
                StarsLiteFactory.setLiteAppWaterHeater( liteApp.getWaterHeater(), app );
            }
        }
        else if (liteAppCat.getCategoryID() == selectionListService.getListEntry(energyCompany, YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL).getEntryID()) {
            com.cannontech.stars.database.db.appliance.ApplianceDualFuel app =
                    com.cannontech.stars.database.db.appliance.ApplianceDualFuel.getApplianceDualFuel( appliance.getApplianceBase().getApplianceID() );
            if (app != null) {
                liteApp.setDualFuel( new LiteStarsAppliance.DualFuel() );
                StarsLiteFactory.setLiteAppDualFuel( liteApp.getDualFuel(), app );
            }
        }
        else if (liteAppCat.getCategoryID() == selectionListService.getListEntry(energyCompany, YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GRAIN_DRYER).getEntryID()) {
            com.cannontech.stars.database.db.appliance.ApplianceGrainDryer app =
                    com.cannontech.stars.database.db.appliance.ApplianceGrainDryer.getApplianceGrainDryer( appliance.getApplianceBase().getApplianceID() );
            if (app != null) {
                liteApp.setGrainDryer( new LiteStarsAppliance.GrainDryer() );
                StarsLiteFactory.setLiteAppGrainDryer( liteApp.getGrainDryer(), app );
            }
        }
        else if (liteAppCat.getCategoryID() == selectionListService.getListEntry(energyCompany, YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT).getEntryID()) {
            com.cannontech.stars.database.db.appliance.ApplianceStorageHeat app =
                    com.cannontech.stars.database.db.appliance.ApplianceStorageHeat.getApplianceStorageHeat( appliance.getApplianceBase().getApplianceID() );
            if (app != null) {
                liteApp.setStorageHeat( new LiteStarsAppliance.StorageHeat() );
                StarsLiteFactory.setLiteAppStorageHeat( liteApp.getStorageHeat(), app );
            }
        }
        else if (liteAppCat.getCategoryID() == selectionListService.getListEntry(energyCompany, YukonListEntryTypes.YUK_DEF_ID_APP_CAT_HEAT_PUMP).getEntryID()) {
            com.cannontech.stars.database.db.appliance.ApplianceHeatPump app =
                    com.cannontech.stars.database.db.appliance.ApplianceHeatPump.getApplianceHeatPump( appliance.getApplianceBase().getApplianceID() );
            if (app != null) {
                liteApp.setHeatPump( new LiteStarsAppliance.HeatPump() );
                StarsLiteFactory.setLiteAppHeatPump( liteApp.getHeatPump(), app );
            }
        }
        else if (liteAppCat.getCategoryID() == selectionListService.getListEntry(energyCompany, YukonListEntryTypes.YUK_DEF_ID_APP_CAT_IRRIGATION).getEntryID()) {
            com.cannontech.stars.database.db.appliance.ApplianceIrrigation app =
                    com.cannontech.stars.database.db.appliance.ApplianceIrrigation.getApplianceIrrigation( appliance.getApplianceBase().getApplianceID() );
            if (app != null) {
                liteApp.setIrrigation( new LiteStarsAppliance.Irrigation() );
                StarsLiteFactory.setLiteAppIrrigation( liteApp.getIrrigation(), app );
            }
        }
        else if (liteAppCat.getCategoryID() == selectionListService.getListEntry(energyCompany, YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GENERATOR).getEntryID()) {
            com.cannontech.stars.database.db.appliance.ApplianceGenerator app =
                    com.cannontech.stars.database.db.appliance.ApplianceGenerator.getApplianceGenerator( appliance.getApplianceBase().getApplianceID() );
            if (app != null) {
                liteApp.setGenerator( new LiteStarsAppliance.Generator() );
                StarsLiteFactory.setLiteAppGenerator( liteApp.getGenerator(), app );
            }
        }
        
        return liteApp;
    }
    
    public static void setLiteApplianceCategory(LiteApplianceCategory liteAppCat, com.cannontech.stars.database.db.appliance.ApplianceCategory appCat) {
        liteAppCat.setApplianceCategoryID( appCat.getApplianceCategoryID().intValue() );
        liteAppCat.setCategoryID( appCat.getCategoryID().intValue() );
        liteAppCat.setDescription( appCat.getDescription() );
        liteAppCat.setWebConfigurationID( appCat.getWebConfigurationID().intValue() );
    }
    
    public static void setLiteServiceCompany(LiteServiceCompany liteCompany, com.cannontech.stars.database.data.report.ServiceCompany company) {
        liteCompany.setCompanyID( company.getServiceCompany().getCompanyID().intValue() );
        liteCompany.setCompanyName( company.getServiceCompany().getCompanyName() );
        liteCompany.setAddressID( company.getServiceCompany().getAddressID().intValue() );
        liteCompany.setMainPhoneNumber( company.getServiceCompany().getMainPhoneNumber() );
        liteCompany.setMainFaxNumber( company.getServiceCompany().getMainFaxNumber() );
        liteCompany.setPrimaryContactID( company.getServiceCompany().getPrimaryContactID().intValue() );
        liteCompany.setHiType( company.getServiceCompany().getHIType() );
        liteCompany.setDesignationCodes( company.getDesignationCodes());
    }

    public static void setLiteServiceCompanyDesignationCode(LiteServiceCompanyDesignationCode liteCompanyDesignCode, ServiceCompanyDesignationCode servCompDesignCode) {
        liteCompanyDesignCode.setDesignationCodeID(servCompDesignCode.getDesignationCodeID().intValue());
        liteCompanyDesignCode.setDesignationCodeValue(servCompDesignCode.getDesignationCodeValue());
        liteCompanyDesignCode.setServiceCompanyID(servCompDesignCode.getServiceCompanyID().intValue());
    }

    public static void setLiteSubstation(LiteSubstation liteSub, com.cannontech.stars.database.db.Substation sub) {
        liteSub.setSubstationID( sub.getSubstationID().intValue() );
        liteSub.setSubstationName( sub.getSubstationName() );
    }
    
    public static void setLiteWebConfiguration(LiteWebConfiguration liteConfig, com.cannontech.database.db.web.YukonWebConfiguration config) {
        liteConfig.setConfigID(config.getConfigurationID().intValue() );
        liteConfig.setLogoLocation( config.getLogoLocation() );
        liteConfig.setDescription( config.getDescription() );
        liteConfig.setAlternateDisplayName( config.getAlternateDisplayName() );
        liteConfig.setUrl( config.getURL() );
    }
    
    public static void setLiteLMProgramWebPublishing(LiteLMProgramWebPublishing liteProg, com.cannontech.stars.database.db.LMProgramWebPublishing pubProg) {
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
                for (int k = 0; k < groups.length; k++) {
                    groupIDs[k] = groups[k].getLmGroupDeviceID().intValue();
                }
                liteProg.setGroupIDs( groupIDs );
            }
            catch (java.sql.SQLException e) {
                CTILogger.error( e.getMessage(), e );
            }
        } else {
            liteProg.setGroupIDs( new int[0] );
        }
    }
    
    
    public static DBPersistent createDBPersistent(LiteBase lite) {
        DBPersistent db = null;
        
        switch (lite.getLiteType()) {
            case LiteTypes.CONTACT:
                db = new com.cannontech.database.data.customer.Contact();
                setContact( (com.cannontech.database.data.customer.Contact) db, (LiteContact) lite );
                break;
            case LiteTypes.ADDRESS:
                db = new com.cannontech.database.db.customer.Address();
                setAddress( (com.cannontech.database.db.customer.Address) db, (LiteAddress) lite );
                break;
            case LiteTypes.STARS_CUSTOMER_ACCOUNT:
                db = new com.cannontech.stars.database.db.customer.CustomerAccount();
                setCustomerAccount( (com.cannontech.stars.database.db.customer.CustomerAccount) db, (LiteCustomerAccount) lite );
                break;
            case LiteTypes.STARS_ACCOUNT_SITE:
                db = new com.cannontech.stars.database.db.customer.AccountSite();
                setAccountSite( (com.cannontech.stars.database.db.customer.AccountSite) db, (LiteAccountSite) lite );
                break;
            case LiteTypes.STARS_SITE_INFORMATION:
                db = new com.cannontech.stars.database.db.customer.SiteInformation();
                setSiteInformation( (com.cannontech.stars.database.db.customer.SiteInformation) db, (LiteSiteInformation) lite );
                break;
            case LiteTypes.STARS_LMHARDWARE_EVENT:
                db = new com.cannontech.stars.database.data.event.LMHardwareEvent();
                setLMCustomerEventBase( (com.cannontech.stars.database.data.event.LMCustomerEventBase) db, (LiteLMCustomerEvent) lite );
                break;
            case LiteTypes.STARS_LMPROGRAM_EVENT:
                db = new com.cannontech.stars.database.data.event.LMProgramEvent();
                setLMCustomerEventBase( (com.cannontech.stars.database.data.event.LMCustomerEventBase) db, (LiteLMCustomerEvent) lite );
                break;
            case LiteTypes.STARS_LMTHERMOSTAT_MANUAL_EVENT:
                db = new com.cannontech.stars.database.data.event.LMThermostatManualEvent();
                setLMThermostatManualEvent( (com.cannontech.stars.database.data.event.LMThermostatManualEvent) db, (LiteLMThermostatManualEvent) lite );
                break;
            case LiteTypes.STARS_LMHARDWARE:
                db = new com.cannontech.stars.database.data.hardware.LMHardwareBase();
                setLMHardwareBase( (com.cannontech.stars.database.data.hardware.LMHardwareBase) db, (LiteLmHardwareBase) lite );
                break;
            case LiteTypes.STARS_METERHARDWAREBASE:
                db = new com.cannontech.stars.database.data.hardware.MeterHardwareBase();
                setMeterHardwareBase( (com.cannontech.stars.database.data.hardware.MeterHardwareBase) db, (LiteMeterHardwareBase) lite );
                break;                
            case LiteTypes.YUKON_USER:
                db = new com.cannontech.database.db.user.YukonUser();
                setYukonUser( (com.cannontech.database.db.user.YukonUser) db, (com.cannontech.database.data.lite.LiteYukonUser) lite );
                break;
            case LiteTypes.STARS_APPLIANCE:
                db = new com.cannontech.stars.database.data.appliance.ApplianceBase();
                setApplianceBase( (com.cannontech.stars.database.data.appliance.ApplianceBase) db, (LiteStarsAppliance) lite );
                break;
            case LiteTypes.STARS_WORK_ORDER_BASE:
                db = new com.cannontech.stars.database.data.report.WorkOrderBase();
                setWorkOrderBase( (com.cannontech.stars.database.data.report.WorkOrderBase) db, (LiteWorkOrderBase) lite );
                break;
            case LiteTypes.ENERGY_COMPANY:
                db = new com.cannontech.database.db.company.EnergyCompany();
                setEnergyCompany( (com.cannontech.database.db.company.EnergyCompany) db, (LiteStarsEnergyCompany) lite );
                break;
            case LiteTypes.STARS_APPLIANCE_CATEGORY:
                db = new com.cannontech.stars.database.db.appliance.ApplianceCategory();
                setApplianceCategory( (com.cannontech.stars.database.db.appliance.ApplianceCategory) db, (LiteApplianceCategory) lite );
                break;
            case LiteTypes.STARS_SERVICE_COMPANY:
                db = new com.cannontech.stars.database.data.report.ServiceCompany();
                setServiceCompany( (com.cannontech.stars.database.data.report.ServiceCompany) db, (LiteServiceCompany) lite );
                break;
            case LiteTypes.STARS_SERVICE_COMPANY_DESIGNATION_CODE:
                db = new ServiceCompanyDesignationCode();
                setServiceCompanyDesignationCode( (ServiceCompanyDesignationCode) db, (LiteServiceCompanyDesignationCode) lite );
                break;
            case LiteTypes.STARS_SUBSTATION:
                db = new com.cannontech.stars.database.db.Substation();
                setSubstation( (com.cannontech.stars.database.db.Substation) db, (LiteSubstation) lite );
            default:
                /*Need to handle Yukon meters which are JUST LiteInventoryBase and don't have a specific liteType
                 * Shouldn't truly make this the default, though, in case something else comes through here, so we'll still check
                 */
                YukonListEntry listEntry = YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(((LiteInventoryBase)lite).getCategoryID());
                if(lite instanceof LiteInventoryBase && listEntry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_INV_CAT_YUKON_METER) {
                    db = new InventoryBase(((LiteInventoryBase)lite).getInventoryID());
                    ((InventoryBase)db).setAccountID(((LiteInventoryBase)lite).getAccountID());
                }
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
            LiteContactNotification liteNotif = 
                    liteContact.getLiteContactNotifications().get(i);
            
            com.cannontech.database.db.contact.ContactNotification notif =
                    new com.cannontech.database.db.contact.ContactNotification();
            notif.setContactNotifID( new Integer(liteNotif.getContactNotifID()) );
            notif.setContactID( new Integer(liteNotif.getContactID()) );
            notif.setNotificationCatID( new Integer(liteNotif.getNotificationCategoryID()) );
            notif.setDisableFlag( liteNotif.getDisableFlag() );
            notif.setNotification( liteNotif.getNotification() );
            notif.setOrdering( new Integer(i) );
            
            contact.getContactNotifVect().add( notif );
        }
    }
    
    public static void setContact(com.cannontech.database.data.customer.Contact contact, LiteContact liteContact, LiteStarsEnergyCompany energyCompany) {
        setContact( contact, liteContact );
        if (liteContact.getAddressID() > 0) {
            setAddress(contact.getAddress(), addressDao.getByAddressId(liteContact.getAddressID()));
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
    
    public static void setCustomerAccount(com.cannontech.stars.database.db.customer.CustomerAccount account, LiteCustomerAccount liteAccount) {
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
        customer.setCustomerNumber( liteCustomer.getCustomerNumber() );
        customer.setRateScheduleID( new Integer(liteCustomer.getRateScheduleID()) );
        customer.setAltTrackingNumber( liteCustomer.getAltTrackingNumber() );
    }
    
    public static void setCICustomerBase(com.cannontech.database.data.customer.CICustomerBase ci, LiteCICustomer liteCI) {
        setCustomer( ci.getCustomer(), liteCI );
        ci.setCustomerID( ci.getCustomer().getCustomerID() );
        ci.getCiCustomerBase().setMainAddressID( new Integer(liteCI.getMainAddressID()) );
        ci.getCiCustomerBase().setCustDmdLevel( new Double(liteCI.getDemandLevel()) );
        ci.getCiCustomerBase().setCurtailAmount( new Double(liteCI.getCurtailAmount()) );
        ci.getCiCustomerBase().setCompanyName( liteCI.getCompanyName() );
        ci.getCiCustomerBase().setCICustType( new Integer(liteCI.getCICustType()));
    }
    
    public static void setAccountSite(com.cannontech.stars.database.db.customer.AccountSite acctSite, LiteAccountSite liteAcctSite) {
        acctSite.setAccountSiteID( new Integer(liteAcctSite.getAccountSiteID()) );
        acctSite.setSiteInformationID( new Integer(liteAcctSite.getSiteInformationID()) );
        acctSite.setSiteNumber( liteAcctSite.getSiteNumber() );
        acctSite.setStreetAddressID( new Integer(liteAcctSite.getStreetAddressID()) );
        acctSite.setPropertyNotes( liteAcctSite.getPropertyNotes() );
        acctSite.setCustAtHome(liteAcctSite.getCustAtHome());
    }
    
    public static void setSiteInformation(com.cannontech.stars.database.db.customer.SiteInformation siteInfo, LiteSiteInformation liteSiteInfo) {
        siteInfo.setSiteID( new Integer(liteSiteInfo.getSiteID()) );
        siteInfo.setSubstationID( new Integer(liteSiteInfo.getSubstationID()) );
        siteInfo.setFeeder( liteSiteInfo.getFeeder() );
        siteInfo.setPole( liteSiteInfo.getPole() );
        siteInfo.setTransformerSize( liteSiteInfo.getTransformerSize() );
        siteInfo.setServiceVoltage( liteSiteInfo.getServiceVoltage() );
    }
    
    public static void setLMCustomerEventBase(com.cannontech.stars.database.data.event.LMCustomerEventBase event, LiteLMCustomerEvent liteEvent) {
        event.setEventID( new Integer(liteEvent.getEventID()) );
        event.getLMCustomerEventBase().setEventTypeID( new Integer(liteEvent.getEventTypeID()) );
        event.getLMCustomerEventBase().setActionID( new Integer(liteEvent.getActionID()) );
        event.getLMCustomerEventBase().setEventDateTime( new Date(liteEvent.getEventDateTime()) );
        event.getLMCustomerEventBase().setNotes( liteEvent.getNotes() );
    }
    
    public static void setInventoryBase(com.cannontech.stars.database.db.hardware.InventoryBase invDB, LiteInventoryBase liteInv) {
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
        invDB.setCurrentStateID( new Integer (liteInv.getCurrentStateID()));
    }
    
    public static void setLMHardwareBase(com.cannontech.stars.database.data.hardware.LMHardwareBase hw, LiteLmHardwareBase liteHw) {
        setInventoryBase( hw.getInventoryBase(), liteHw );
        
        hw.setInventoryID( hw.getInventoryBase().getInventoryID() );
        hw.getLMHardwareBase().setManufacturerSerialNumber( liteHw.getManufacturerSerialNumber() );
        hw.getLMHardwareBase().setLMHardwareTypeID( new Integer(liteHw.getLmHardwareTypeID()) );
        hw.getLMHardwareBase().setRouteID( new Integer(liteHw.getRouteID()) );
        hw.getLMHardwareBase().setConfigurationID( new Integer(liteHw.getConfigurationID()) );
    }

    public static void setMeterHardwareBase(com.cannontech.stars.database.data.hardware.MeterHardwareBase hw, LiteMeterHardwareBase liteHw) {
        setInventoryBase( hw.getInventoryBase(), liteHw );
        
        hw.setInventoryID( hw.getInventoryBase().getInventoryID() );
        hw.getMeterHardwareBase().setMeterNumber( liteHw.getMeterNumber() );
        hw.getMeterHardwareBase().setMeterTypeID( new Integer(liteHw.getMeterTypeID()) );
    }

    public static void setYukonUser(com.cannontech.database.db.user.YukonUser user, com.cannontech.database.data.lite.LiteYukonUser liteUser) {
        user.setUserID( new Integer(liteUser.getUserID()) );
        user.setUsername( liteUser.getUsername() );
        user.setLoginStatus( liteUser.getLoginStatus() );
    }
    
    public static void setLMThermostatManualEvent(com.cannontech.stars.database.data.event.LMThermostatManualEvent event, LiteLMThermostatManualEvent liteEvent) {
        setLMCustomerEventBase( event, liteEvent );
        event.getLmThermostatManualEvent().setInventoryID( new Integer(liteEvent.getInventoryID()) );
        event.getLmThermostatManualEvent().setHoldTemperature( liteEvent.isHoldTemperature() ? "Y" : "N" );
        event.getLmThermostatManualEvent().setOperationStateID( new Integer(liteEvent.getOperationStateID()) );
        event.getLmThermostatManualEvent().setFanOperationID( new Integer(liteEvent.getFanOperationID()) );
        if (liteEvent.getPreviousCoolTemperature() == 0.0) {
            event.getLmThermostatManualEvent().setPreviousCoolTemperature( null );
        } else {
            event.getLmThermostatManualEvent().setPreviousCoolTemperature( liteEvent.getPreviousCoolTemperature() );
        }
        if (liteEvent.getPreviousHeatTemperature() == 0.0) {
            event.getLmThermostatManualEvent().setPreviousHeatTemperature( null );
        } else {
            event.getLmThermostatManualEvent().setPreviousHeatTemperature( liteEvent.getPreviousHeatTemperature() );
        }
    }
    
    public static void setApplianceBase(com.cannontech.stars.database.data.appliance.ApplianceBase app, LiteStarsAppliance liteApp) {
        app.setApplianceID( new Integer(liteApp.getApplianceID()) );
        app.getApplianceBase().setAccountID( new Integer(liteApp.getAccountID()) );
        app.getApplianceBase().setApplianceCategoryID( 
                                   new Integer(liteApp.getApplianceCategory().getApplianceCategoryId()) );
        app.getApplianceBase().setProgramID( new Integer(liteApp.getProgramID()) );
        app.getApplianceBase().setYearManufactured( new Integer(liteApp.getYearManufactured()) );
        app.getApplianceBase().setManufacturerID( new Integer(liteApp.getManufacturerID()) );
        app.getApplianceBase().setLocationID( new Integer(liteApp.getLocationID()) );
        app.getApplianceBase().setNotes( liteApp.getNotes() );
        app.getApplianceBase().setModelNumber( liteApp.getModelNumber() );
        app.getApplianceBase().setKWCapacity( new Double(liteApp.getKwCapacity()) );
        app.getApplianceBase().setEfficiencyRating( new Double(liteApp.getEfficiencyRating()) );
        
        if (liteApp.getInventoryID() != CtiUtilities.NONE_ZERO_ID) {
            app.getLMHardwareConfig().setApplianceID( app.getApplianceBase().getApplianceID() );
            app.getLMHardwareConfig().setInventoryID( new Integer(liteApp.getInventoryID()) );
            app.getLMHardwareConfig().setAddressingGroupID( new Integer(liteApp.getAddressingGroupID()) );
            app.getLMHardwareConfig().setLoadNumber( new Integer(liteApp.getLoadNumber()) );
        }
    }
    
    public static void setApplianceAirConditioner(com.cannontech.stars.database.db.appliance.ApplianceAirConditioner app, LiteStarsAppliance liteApp) {
        app.setApplianceID( new Integer(liteApp.getApplianceID()) );
        app.setTonnageID( new Integer(liteApp.getAirConditioner().getTonnageID()) );
        app.setTypeID( new Integer(liteApp.getAirConditioner().getTypeID()) );
    }
    
    public static void setApplianceDualStageAirCond(com.cannontech.stars.database.db.appliance.ApplianceDualStageAirCond app, LiteStarsAppliance liteApp) {
        app.setApplianceID( new Integer(liteApp.getApplianceID()) );
        app.setStageOneTonnageID( new Integer(liteApp.getDualStageAirCond().getStageOneTonnageID())) ;
        app.setStageTwoTonnageID( new Integer(liteApp.getDualStageAirCond().getStageTwoTonnageID())) ;
        app.setTypeID( new Integer(liteApp.getDualStageAirCond().getTypeID()) );
    }
    
    public static void setApplianceChiller(com.cannontech.stars.database.db.appliance.ApplianceChiller app, LiteStarsAppliance liteApp) {
        app.setApplianceID( new Integer(liteApp.getApplianceID()) );
        app.setTonnageID( new Integer(liteApp.getChiller().getTonnageID()) );
        app.setTypeID( new Integer(liteApp.getChiller().getTypeID()) );
    }
    
    public static void setApplianceWaterHeater(com.cannontech.stars.database.db.appliance.ApplianceWaterHeater app, LiteStarsAppliance liteApp) {
        app.setApplianceID( new Integer(liteApp.getApplianceID()) );
        app.setNumberOfGallonsID( new Integer(liteApp.getWaterHeater().getNumberOfGallonsID()) );
        app.setEnergySourceID( new Integer(liteApp.getWaterHeater().getNumberOfGallonsID()) );
        app.setNumberOfElements( new Integer(liteApp.getWaterHeater().getNumberOfElements()) );
    }
    
    public static void setApplianceDualFuel(com.cannontech.stars.database.db.appliance.ApplianceDualFuel app, LiteStarsAppliance liteApp) {
        app.setApplianceID( new Integer(liteApp.getApplianceID()) );
        app.setSwitchOverTypeID( new Integer(liteApp.getDualFuel().getSwitchOverTypeID()) );
        app.setSecondaryEnergySourceID( new Integer(liteApp.getDualFuel().getSecondaryEnergySourceID()) );
        app.setSecondaryKWCapacity( new Integer(liteApp.getDualFuel().getSecondaryKWCapacity()) );
    }
    
    public static void setApplianceGenerator(com.cannontech.stars.database.db.appliance.ApplianceGenerator app, LiteStarsAppliance liteApp) {
        app.setApplianceID( new Integer(liteApp.getApplianceID()) );
        app.setTransferSwitchTypeID( new Integer(liteApp.getGenerator().getTransferSwitchTypeID()) );
        app.setTransferSwitchMfgID( new Integer(liteApp.getGenerator().getTransferSwitchMfgID()) );
        app.setPeakKWCapacity( new Integer(liteApp.getGenerator().getPeakKWCapacity()) );
        app.setFuelCapGallons( new Integer(liteApp.getGenerator().getFuelCapGallons()) );
        app.setStartDelaySeconds( new Integer(liteApp.getGenerator().getStartDelaySeconds()) );
    }
    
    public static void setApplianceGrainDryer(com.cannontech.stars.database.db.appliance.ApplianceGrainDryer app, LiteStarsAppliance liteApp) {
        app.setApplianceID( new Integer(liteApp.getApplianceID()) );
        app.setDryerTypeID( new Integer(liteApp.getGrainDryer().getDryerTypeID()) );
        app.setBinSizeID( new Integer(liteApp.getGrainDryer().getBinSizeID()) );
        app.setBlowerEnergySourceID( new Integer(liteApp.getGrainDryer().getBlowerEnergySourceID()) );
        app.setBlowerHorsePowerID( new Integer(liteApp.getGrainDryer().getBlowerHorsePowerID()) );
        app.setBlowerHeatSourceID( new Integer(liteApp.getGrainDryer().getBlowerHeatSourceID()) );
    }
    
    public static void setApplianceStorageHeat(com.cannontech.stars.database.db.appliance.ApplianceStorageHeat app, LiteStarsAppliance liteApp) {
        app.setApplianceID( new Integer(liteApp.getApplianceID()) );
        app.setStorageTypeID( new Integer(liteApp.getStorageHeat().getStorageTypeID()) );
        app.setPeakKWCapacity( new Integer(liteApp.getStorageHeat().getPeakKWCapacity()) );
        app.setHoursToRecharge( new Integer(liteApp.getStorageHeat().getHoursToRecharge()) );
    }
    
    public static void setApplianceHeatPump(com.cannontech.stars.database.db.appliance.ApplianceHeatPump app, LiteStarsAppliance liteApp) {
        app.setApplianceID( new Integer(liteApp.getApplianceID()) );
        app.setPumpTypeID( new Integer(liteApp.getHeatPump().getPumpTypeID()) );
        app.setPumpSizeID( new Integer(liteApp.getHeatPump().getPumpSizeID()) );
        app.setStandbySourceID( new Integer(liteApp.getHeatPump().getStandbySourceID()) );
        app.setSecondsDelayToRestart( new Integer(liteApp.getHeatPump().getSecondsDelayToRestart()) );
    }
    
    public static void setApplianceIrrigation(com.cannontech.stars.database.db.appliance.ApplianceIrrigation app, LiteStarsAppliance liteApp) {
        app.setApplianceID( new Integer(liteApp.getApplianceID()) );
        app.setIrrigationTypeID( new Integer(liteApp.getIrrigation().getIrrigationTypeID()) );
        app.setHorsePowerID( new Integer(liteApp.getIrrigation().getHorsePowerID()) );
        app.setEnergySourceID( new Integer(liteApp.getIrrigation().getEnergySourceID()) );
        app.setSoilTypeID( new Integer(liteApp.getIrrigation().getSoilTypeID()) );
        app.setMeterLocationID( new Integer(liteApp.getIrrigation().getMeterLocationID()) );
        app.setMeterVoltageID( new Integer(liteApp.getIrrigation().getMeterVoltageID()) );
    }
    
    public static void setWorkOrderBase(com.cannontech.stars.database.data.report.WorkOrderBase order, LiteWorkOrderBase liteOrder) {
        order.getWorkOrderBase().setOrderID( new Integer(liteOrder.getOrderID()) );
        order.getWorkOrderBase().setOrderNumber( liteOrder.getOrderNumber() );
        order.getWorkOrderBase().setWorkTypeID( new Integer(liteOrder.getWorkTypeID()) );
        order.getWorkOrderBase().setCurrentStateID( new Integer(liteOrder.getCurrentStateID()) );
        order.getWorkOrderBase().setServiceCompanyID( new Integer(liteOrder.getServiceCompanyID()) );
        order.getWorkOrderBase().setDateReported( new Date(liteOrder.getDateReported()) );
        order.getWorkOrderBase().setOrderedBy( liteOrder.getOrderedBy() );
        order.getWorkOrderBase().setDescription( liteOrder.getDescription() );
        order.getWorkOrderBase().setDateScheduled( new Date(liteOrder.getDateScheduled()) );
        order.getWorkOrderBase().setDateCompleted( new Date(liteOrder.getDateCompleted()) );
        order.getWorkOrderBase().setActionTaken( liteOrder.getActionTaken() );
        order.getWorkOrderBase().setAccountID( new Integer(liteOrder.getAccountID()) );
        order.getWorkOrderBase().setAdditionalOrderNumber( liteOrder.getAdditionalOrderNumber());
        order.setEnergyCompanyID( new Integer(liteOrder.getEnergyCompanyID()) );
    }
    
    public static void setEnergyCompany(com.cannontech.database.db.company.EnergyCompany company, LiteStarsEnergyCompany liteCompany) {
        company.setEnergyCompanyId( liteCompany.getEnergyCompanyId() );
        company.setName( liteCompany.getName() );
        company.setPrimaryContactId( new Integer(liteCompany.getPrimaryContactID()) );
        company.setUserId(liteCompany.getUser().getUserID());
    }

    public static void setApplianceCategory(com.cannontech.stars.database.db.appliance.ApplianceCategory appCat, LiteApplianceCategory liteAppCat) {
        appCat.setApplianceCategoryID( new Integer(liteAppCat.getApplianceCategoryID()) );
        appCat.setCategoryID( new Integer(liteAppCat.getCategoryID()) );
        appCat.setDescription( liteAppCat.getDescription() );
        appCat.setWebConfigurationID( new Integer(liteAppCat.getWebConfigurationID()) );
    }
    
    public static void setServiceCompany(com.cannontech.stars.database.data.report.ServiceCompany company, LiteServiceCompany liteCompany) {
        company.setCompanyID( new Integer(liteCompany.getCompanyID()) );
        company.getServiceCompany().setCompanyName( liteCompany.getCompanyName() );
        company.getServiceCompany().setAddressID( new Integer(liteCompany.getAddressID()) );
        company.getServiceCompany().setMainPhoneNumber( liteCompany.getMainPhoneNumber() );
        company.getServiceCompany().setMainFaxNumber( liteCompany.getMainFaxNumber() );
        company.getServiceCompany().setPrimaryContactID( new Integer(liteCompany.getPrimaryContactID()) );
        company.getServiceCompany().setHIType( liteCompany.getHiType() );
        company.setDesignationCodes(liteCompany.getDesignationCodes());
    }

    public static void setServiceCompanyDesignationCode(ServiceCompanyDesignationCode servCompDesignCode, LiteServiceCompanyDesignationCode liteServCompDesignCode) {
        servCompDesignCode.setDesignationCodeID( new Integer(liteServCompDesignCode.getDesignationCodeID()) );
        servCompDesignCode.setDesignationCodeValue( liteServCompDesignCode.getDesignationCodeValue() );
        servCompDesignCode.setServiceCompanyID( new Integer(liteServCompDesignCode.getServiceCompanyID()) );
    }
    
    public static void setSubstation(com.cannontech.stars.database.db.Substation sub, LiteSubstation liteSub) {
        sub.setSubstationID( new Integer(liteSub.getSubstationID()) );
        sub.setSubstationName( liteSub.getSubstationName() );
    }

    public static void setConstantYukonListEntry(
            com.cannontech.common.constants.YukonListEntry cEntry, com.cannontech.database.db.constants.YukonListEntry entry) {
        cEntry.setEntryID( entry.getEntryID().intValue() );
        cEntry.setListID( entry.getListID().intValue() );
        cEntry.setEntryOrder( entry.getEntryOrder().intValue() );
        cEntry.setEntryText( entry.getEntryText() );
        cEntry.setYukonDefID( entry.getYukonDefID().intValue() );
    }

    public static void setConstantYukonSelectionList(
            com.cannontech.common.constants.YukonSelectionList cList, 
            com.cannontech.database.db.constants.YukonSelectionList list) {
        cList.setListId(list.getListID().intValue());
        cList.setOrdering(YukonSelectionListOrder.getForDbString(list.getOrdering()));
        cList.setSelectionLabel(list.getSelectionLabel());
        cList.setWhereIsList(list.getWhereIsList());
        cList.setType(YukonSelectionListEnum.getForName(list.getListName()));
        cList.setUserUpdateAvailable(list.getUserUpdateAvailable().equals("Y"));
        cList.setEnergyCompanyId(list.getEnergyCompanyId());
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
        starsContact.setLoginID( liteContact.getLoginID() );
        
        starsContact.removeAllContactNotification();
        for (int i = 0; i < liteContact.getLiteContactNotifications().size(); i++) {
            LiteContactNotification liteContNotif = liteContact.getLiteContactNotifications().get(i);
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
        YukonListEntry entry = YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteEvent.getActionID() );
        starsEvent.setEventAction( entry.getEntryText() );
        starsEvent.setEventDateTime( new Date(liteEvent.getEventDateTime()) );
        starsEvent.setNotes( StarsUtils.forceNotNull(liteEvent.getNotes()) );
        starsEvent.setYukonDefID( entry.getYukonDefID() );
    }
    
    public static void setStarsCustListEntry(StarsCustListEntry starsEntry, YukonListEntry yukonEntry) {
        starsEntry.setEntryID( yukonEntry.getEntryID() );
        starsEntry.setContent( yukonEntry.getEntryText() );
        //starsEntry.setYukonDefID( yukonEntry.getYukonDefID() );
    }
    
    public static void setStarsCustResidence(StarsCustResidence starsRes, LiteCustomerResidence liteRes) {
        ResidenceType resType = new ResidenceType();
        setStarsCustListEntry( resType, YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(liteRes.getResidenceTypeID()) );
        starsRes.setResidenceType( resType );
        
        ConstructionMaterial material = new ConstructionMaterial();
        setStarsCustListEntry( material, YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(liteRes.getConstructionMaterialID()) );
        starsRes.setConstructionMaterial( material );
        
        DecadeBuilt decade = new DecadeBuilt();
        setStarsCustListEntry( decade, YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(liteRes.getDecadeBuiltID()) );
        starsRes.setDecadeBuilt( decade );
        
        SquareFeet sqrFeet = new SquareFeet();
        setStarsCustListEntry( sqrFeet, YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(liteRes.getSquareFeetID()) );
        starsRes.setSquareFeet( sqrFeet );
        
        InsulationDepth depth = new InsulationDepth();
        setStarsCustListEntry( depth, YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(liteRes.getInsulationDepthID()) );
        starsRes.setInsulationDepth( depth );
        
        GeneralCondition condition = new GeneralCondition();
        setStarsCustListEntry( condition, YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(liteRes.getGeneralConditionID()) );
        starsRes.setGeneralCondition( condition );
        
        MainCoolingSystem coolSys = new MainCoolingSystem();
        setStarsCustListEntry( coolSys, YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(liteRes.getMainCoolingSystemID()) );
        starsRes.setMainCoolingSystem( coolSys );
        
        MainHeatingSystem heatSys = new MainHeatingSystem();
        setStarsCustListEntry( heatSys, YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(liteRes.getMainHeatingSystemID()) );
        starsRes.setMainHeatingSystem( heatSys );
        
        NumberOfOccupants number = new NumberOfOccupants();
        setStarsCustListEntry( number, YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(liteRes.getNumberOfOccupantsID()) );
        starsRes.setNumberOfOccupants( number );
        
        OwnershipType ownType = new OwnershipType();
        setStarsCustListEntry( ownType, YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(liteRes.getOwnershipTypeID()) );
        starsRes.setOwnershipType( ownType );
        
        MainFuelType fuelType = new MainFuelType();
        setStarsCustListEntry( fuelType, YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(liteRes.getMainFuelTypeID()) );
        starsRes.setMainFuelType( fuelType );
        
        starsRes.setNotes( liteRes.getNotes() );
    }
    
    public static StarsLMPrograms createStarsLMPrograms(LiteAccountInfo liteAcctInfo, LiteStarsEnergyCompany energyCompany) {
        StarsLMPrograms starsProgs = new StarsLMPrograms();
        
        for (int i = 0; i < liteAcctInfo.getPrograms().size(); i++) {
            LiteStarsLMProgram liteProg = liteAcctInfo.getPrograms().get(i);
            starsProgs.addStarsLMProgram( createStarsLMProgram(liteProg, liteAcctInfo) );
        }
        
        starsProgs.setStarsLMProgramHistory( createStarsLMProgramHistory(liteAcctInfo, energyCompany) );
        
        return starsProgs;
    }
    
    public static StarsAppliances createStarsAppliances(List<LiteStarsAppliance> liteApps, LiteStarsEnergyCompany energyCompany) {
        StarsAppliances starsApps = new StarsAppliances();
        
        Map<String, List<StarsAppliance>> appMap = new TreeMap<String, List<StarsAppliance>>();
        for (int i = 0; i < liteApps.size(); i++) {
            LiteStarsAppliance liteApp = liteApps.get(i);
            StarsAppliance starsApp = createStarsAppliance(liteApp, energyCompany);
            
            String description = energyCompany.getApplianceCategory( starsApp.getApplianceCategoryID() ).getDescription();
            List<StarsAppliance> list = appMap.get( description );
            if (list == null) {
                list = new ArrayList<StarsAppliance>();
                appMap.put( description, list );
            }
            list.add( starsApp );
        }
        
        Iterator<List<StarsAppliance>> it = appMap.values().iterator();
        while (it.hasNext()) {
            List<StarsAppliance> list = it.next();
            for (int i = 0; i < list.size(); i++) {
                starsApps.addStarsAppliance( list.get(i) );
            }
        }
        
        return starsApps;
    }
    
    public static void setStarsCustAccountInformation(StarsCustAccountInformation starsAcctInfo, LiteAccountInfo liteAcctInfo, LiteStarsEnergyCompany energyCompany, boolean isOperator) {
        LiteCustomerAccount liteAccount = liteAcctInfo.getCustomerAccount();
        LiteCustomer liteCustomer = liteAcctInfo.getCustomer();
        LiteAccountSite liteAcctSite = liteAcctInfo.getAccountSite();
        LiteSiteInformation liteSiteInfo = liteAcctInfo.getSiteInformation();
        LiteContact liteContact = YukonSpringHook.getBean(ContactDao.class).getContact( liteAcctInfo.getCustomer().getPrimaryContactID() );
        
        StarsCustomerAccount starsAccount = new StarsCustomerAccount();
        starsAccount.setAccountID( liteAccount.getAccountID() );
        starsAccount.setCustomerID( liteAccount.getCustomerID() );
        starsAccount.setAccountNumber( StarsUtils.forceNotNull(liteAccount.getAccountNumber()) );
        starsAccount.setIsCommercial( liteCustomer.getCustomerTypeID() == CustomerTypes.CUSTOMER_CI );
        if (liteCustomer instanceof LiteCICustomer && liteCustomer.getCustomerTypeID() == CustomerTypes.CUSTOMER_CI) {
            starsAccount.setCompany( ((LiteCICustomer)liteCustomer).getCompanyName() );
            starsAccount.setCICustomerType( ((LiteCICustomer)liteCustomer).getCICustType() );
        } else {
            starsAccount.setCompany( "" );
        }
        starsAccount.setAccountNotes( StarsUtils.forceNotNull(liteAccount.getAccountNotes()) );
        starsAccount.setPropertyNumber( StarsUtils.forceNotNull(liteAcctSite.getSiteNumber()) );
        starsAccount.setPropertyNotes( StarsUtils.forceNotNull(liteAcctSite.getPropertyNotes()) );
        starsAccount.setTimeZone( liteCustomer.getTimeZone() );
        starsAccount.setCustomerNumber( liteCustomer.getCustomerNumber() );
        starsAccount.setRateScheduleID(liteCustomer.getRateScheduleID() );
        starsAccount.setAltTrackingNumber( liteCustomer.getAltTrackingNumber() );
        starsAccount.setCustAtHome( liteAcctSite.getCustAtHome() );
        starsAccount.setCustStatus( liteAcctSite.getCustStatus() );
        starsAcctInfo.setStarsCustomerAccount( starsAccount );
        
        StreetAddress streetAddr = new StreetAddress();
        setStarsCustomerAddress(streetAddr, addressDao.getByAddressId(liteAcctSite.getStreetAddressID()));
        starsAccount.setStreetAddress( streetAddr );
        
        starsAccount.setStarsSiteInformation( createStarsSiteInformation(liteSiteInfo, energyCompany) );
                
        BillingAddress billAddr = new BillingAddress();
        setStarsCustomerAddress(billAddr, addressDao.getByAddressId(liteAccount.getBillingAddressID()));
        starsAccount.setBillingAddress( billAddr );

        PrimaryContact primContact = new PrimaryContact();
        setStarsCustomerContact( primContact, liteContact );
        starsAccount.setPrimaryContact( primContact );
        
        starsAccount.removeAllAdditionalContact();
        for (int i = 0; i < liteCustomer.getAdditionalContacts().size(); i++) {
            LiteContact lContact = liteCustomer.getAdditionalContacts().get(i);
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
        
        List<Integer> liteInvs = liteAcctInfo.getInventories();
        StarsInventories starsInvs = new StarsInventories();
        starsAcctInfo.setStarsInventories( starsInvs );
        
        InventoryBaseDao inventoryBaseDao = 
            YukonSpringHook.getBean("inventoryBaseDao", InventoryBaseDao.class);
        
        Map<String, List<StarsInventory>> invMap = new TreeMap<String, List<StarsInventory>>();
        for (int i = 0; i < liteInvs.size(); i++) {
            
            LiteInventoryBase liteInv = inventoryBaseDao.getByInventoryId(liteInvs.get(i));
            StarsInventory starsInv = createStarsInventory( liteInv, energyCompany );
            
            List<StarsInventory> list = invMap.get( starsInv.getDeviceLabel() );
            if (list == null) {
                list = new ArrayList<StarsInventory>();
                invMap.put( ServletUtils.getInventoryLabel(starsInv), list );
            }
            list.add( starsInv );
        }
        
        Iterator<List<StarsInventory>> it = invMap.values().iterator();
        while (it.hasNext()) {
            List<StarsInventory> list = it.next();
            for (int i = 0; i < list.size(); i++) {
                starsInvs.addStarsInventory( list.get(i) );
            }
        }
        
        if (liteContact.getLoginID() != com.cannontech.user.UserUtils.USER_NONE_ID &&
            liteContact.getLoginID() != com.cannontech.user.UserUtils.USER_ADMIN_ID) {
            LiteYukonUser liteUser = YukonSpringHook.getBean(YukonUserDao.class).getLiteYukonUser( liteContact.getLoginID() );
            starsAcctInfo.setStarsUser(liteUser);
        }
        
        if (isOperator) {
            starsAcctInfo.setStarsAppliances( createStarsAppliances(liteAcctInfo.getAppliances(), energyCompany) );
            starsAcctInfo.setUnassignedStarsAppliances( createStarsAppliances(liteAcctInfo.getUnassignedAppliances(), energyCompany) );

            List<StarsCallReport> liteCalls = liteAcctInfo.getCallReportHistory();
            StarsCallReportHistory starsCalls = new StarsCallReportHistory();
            starsAcctInfo.setStarsCallReportHistory( starsCalls );
            
            for (int i = 0; i < liteCalls.size(); i++) {
                StarsCallReport starsCall = liteCalls.get(i);
                starsCalls.addStarsCallReport( starsCall );
            }
            
            List<Integer> liteOrders = liteAcctInfo.getServiceRequestHistory();
            StarsServiceRequestHistory starsOrders = new StarsServiceRequestHistory();
            starsAcctInfo.setStarsServiceRequestHistory( starsOrders );
            
            StarsWorkOrderBaseDao starsWorkOrderBaseDao = 
                YukonSpringHook.getBean("starsWorkOrderBaseDao", StarsWorkOrderBaseDao.class);
            List<LiteWorkOrderBase> workOrderList = starsWorkOrderBaseDao.getByIds(liteOrders);
            
            for (final LiteWorkOrderBase workOrder : workOrderList) {
                starsOrders.addStarsServiceRequest( createStarsServiceRequest(workOrder, energyCompany) );
            }
        }
    }
    
    public static void setStarsEnergyCompany(StarsEnergyCompany starsCompany, LiteStarsEnergyCompany liteCompany) {
        ContactDao contactDao = YukonSpringHook.getBean(ContactDao.class);
        ContactNotificationDao contactNotificatinDao = YukonSpringHook.getBean(ContactNotificationDao.class);
        EnergyCompanyService energyCompanyService = YukonSpringHook.getBean(EnergyCompanyService.class);

        starsCompany.setCompanyName(liteCompany.getName());
        starsCompany.setMainPhoneNumber("");
        starsCompany.setMainFaxNumber("");
        starsCompany.setEmail("");
        starsCompany.setCompanyAddress((CompanyAddress) StarsFactory.newStarsCustomerAddress(CompanyAddress.class));
        starsCompany.setTimeZone(energyCompanyService.getDefaultTimeZone(liteCompany.getEnergyCompanyId()).getID());
        
        LiteContact liteContact = contactDao.getContact(liteCompany.getPrimaryContactID());
        if (liteContact != null) {
            LiteContactNotification liteNotifPhone = 
                    contactNotificatinDao.getFirstNotificationForContactByType(liteContact, ContactNotificationType.PHONE);
            starsCompany.setMainPhoneNumber( StarsUtils.getNotification(liteNotifPhone));
            
            LiteContactNotification liteNotifFax = 
                    contactNotificatinDao.getFirstNotificationForContactByType(liteContact, ContactNotificationType.FAX);
            starsCompany.setMainFaxNumber(StarsUtils.getNotification(liteNotifFax));
            
            LiteContactNotification liteNotifEmail = 
                    contactNotificatinDao.getFirstNotificationForContactByType(liteContact, ContactNotificationType.EMAIL);
            starsCompany.setEmail(StarsUtils.getNotification(liteNotifEmail));

            if (liteContact.getAddressID() != CtiUtilities.NONE_ZERO_ID) {
                LiteAddress liteAddr = addressDao.getByAddressId(liteContact.getAddressID()); 
                CompanyAddress starsAddr = new CompanyAddress();
                setStarsCustomerAddress(starsAddr, liteAddr);
                starsCompany.setCompanyAddress(starsAddr);
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
        
        if (liteCompany.getAddressID() != CtiUtilities.NONE_ZERO_ID) {
            LiteAddress liteAddr = addressDao.getByAddressId(liteCompany.getAddressID());
            CompanyAddress companyAddr = new CompanyAddress();
            setStarsCustomerAddress( companyAddr, liteAddr );
            starsCompany.setCompanyAddress( companyAddr );
        }
        
        LiteContact liteContact = YukonSpringHook.getBean(ContactDao.class).getContact( liteCompany.getPrimaryContactID());
        if (liteContact != null) {
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

    /**
     * Given an instance of LiteLMConfiguration, create and return an instance
     * of StarsLMConfiguration. This is a simple translation because these two
     * objects contain the same data except that StarsLMConfiguration doesn't
     * store the configurationId.
     * @param liteCfg
     * @return the new StarsLMConfiguration.
     */
    public static StarsLMConfiguration createStarsLMConfiguration(LiteLMConfiguration liteCfg) {
        StarsLMConfiguration starsCfg = new StarsLMConfiguration();
        
        starsCfg.setColdLoadPickup( StarsUtils.forceNotNone(liteCfg.getColdLoadPickup()) );
        starsCfg.setTamperDetect( StarsUtils.forceNotNone(liteCfg.getTamperDetect()) );
        
        if (liteCfg.getExpressCom() != null) {
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
        else if (liteCfg.getSA205() != null) {
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
        else if (liteCfg.getSASimple() != null) {
            SASimple simple = new SASimple();
            simple.setOperationalAddress( StarsUtils.forceNotNull(liteCfg.getSASimple().getOperationalAddress()) );
            starsCfg.setSASimple( simple );
        }
        
        return starsCfg;
    }
    
    public static void setStarsInv(StarsInv starsInv, LiteInventoryBase liteInv, LiteStarsEnergyCompany energyCompany) {
        starsInv.setInventoryID( liteInv.getInventoryID() );
        starsInv.setDeviceID( liteInv.getDeviceID() );
        starsInv.setCategory( YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(liteInv.getCategoryID()).getEntryText() );
        starsInv.setDeviceLabel( StarsUtils.forceNotNull(liteInv.getDeviceLabel()) );
        
        InstallationCompany company = new InstallationCompany();
        company.setEntryID( liteInv.getInstallationCompanyID() );
        LiteServiceCompany liteCompany = energyCompany.getServiceCompany( liteInv.getInstallationCompanyID() );
        if (liteCompany != null) {
            company.setContent( StarsUtils.forceNotNull(liteCompany.getCompanyName()) );
        } else {
            company.setContent( "(none)" );
        }
        starsInv.setInstallationCompany( company );
        
        //TODO: DATEALTER
        starsInv.setReceiveDate( StarsUtils.translateDate(liteInv.getReceiveDate()) );
        starsInv.setInstallDate( StarsUtils.translateDate(liteInv.getInstallDate()) );
        starsInv.setRemoveDate( StarsUtils.translateDate(liteInv.getRemoveDate()) );
        starsInv.setAltTrackingNumber( StarsUtils.forceNotNull(liteInv.getAlternateTrackingNumber()) );
        
        Voltage volt = new Voltage();
        volt.setEntryID( liteInv.getVoltageID() );
        volt.setContent( YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(liteInv.getVoltageID()).getEntryText() );
        starsInv.setVoltage( volt );
        
        starsInv.setNotes( StarsUtils.forceNotNull(liteInv.getNotes()) );
        
        StarsLMHardwareHistory hwHist = new StarsLMHardwareHistory();
        List<LiteLMHardwareEvent> list = hardwareEventDao.getByInventoryId(liteInv.getLiteID());
        for (int i = 0; i < list.size(); i++) {
            LiteLMCustomerEvent liteEvent = list.get(i);
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
                YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteInv.getCurrentStateID() ), DeviceStatus.class) );

        if (liteInv instanceof LiteLmHardwareBase) {
            LiteLmHardwareBase liteHw = (LiteLmHardwareBase) liteInv;
            starsInv.setDeviceType( (DeviceType)StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(liteHw.getLmHardwareTypeID()),
                    DeviceType.class) );
            
            LMHardware hw = new LMHardware();
            hw.setRouteID( liteHw.getRouteID() );
            hw.setManufacturerSerialNumber( StarsUtils.forceNotNull(((LiteLmHardwareBase)liteInv).getManufacturerSerialNumber()) );
            extendLiteInventoryBase( liteHw, energyCompany);
            
            if (liteHw.getLMConfiguration() != null) {
                hw.setStarsLMConfiguration( createStarsLMConfiguration(liteHw.getLMConfiguration()) );
            }
            
            starsInv.setLMHardware( hw );
        }
        else if (InventoryUtils.isMCT( liteInv.getCategoryID() )) {
            starsInv.setDeviceType( (DeviceType)StarsFactory.newStarsCustListEntry(
                    selectionListService.getListEntry(energyCompany, YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_NON_YUKON_METER),
                    DeviceType.class) );
            
            MCT mct = new MCT();
            if (liteInv.getDeviceID() > 0)
            {
                try
                {
                    mct.setDeviceName( YukonSpringHook.getBean(PaoDao.class).getYukonPAOName(liteInv.getDeviceID()) );
                }
                catch(NotFoundException e) 
                {
                    CTILogger.error(e.getMessage(), e);
                    mct.setDeviceName( "(none)" );
                }
            }
            else if (liteInv.getDeviceLabel() != null && liteInv.getDeviceLabel().length() > 0) {
                mct.setDeviceName( liteInv.getDeviceLabel() );
            } else {
                mct.setDeviceName( "(none)" );
            }
            
            starsInv.setMCT( mct );
        }
        else {
            starsInv.setDeviceType( (DeviceType)StarsFactory.newEmptyStarsCustListEntry( DeviceType.class ));
            starsInv.setLMHardware( null );
            starsInv.setMCT( null );
            starsInv.setMeterNumber(MeterHardwareBase.getMeterNumberFromInventoryID(liteInv.getInventoryID()));
            
        }
    }
    
    
    public static StarsCustAccountInformation createStarsCustAccountInformation(LiteAccountInfo liteAcctInfo, LiteStarsEnergyCompany energyCompany, boolean isOperator) {
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
                YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(liteOrder.getWorkTypeID()),
                ServiceType.class)
        );
        
        ServiceCompany company = new ServiceCompany();
        company.setEntryID( liteOrder.getServiceCompanyID() );
        LiteServiceCompany liteCompany = energyCompany.getServiceCompany( liteOrder.getServiceCompanyID() );
        if (liteCompany != null) {
            company.setContent( StarsUtils.forceNotNull(liteCompany.getCompanyName()) );
        } else {
            company.setContent( "(none)" );
        }
        starsOrder.setServiceCompany( company );
        
        starsOrder.setCurrentState(
            (CurrentState) StarsFactory.newStarsCustListEntry(
                YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(liteOrder.getCurrentStateID()),
                CurrentState.class)
        );
        
        starsOrder.setDateReported( StarsUtils.translateDate(liteOrder.getDateReported()) );
        starsOrder.setDateScheduled( StarsUtils.translateDate(liteOrder.getDateScheduled()) );
        starsOrder.setDateCompleted( StarsUtils.translateDate(liteOrder.getDateCompleted()) );
        starsOrder.setOrderedBy( StarsUtils.forceNotNull(liteOrder.getOrderedBy()) );
        starsOrder.setDescription( StarsUtils.forceNotNull(liteOrder.getDescription()) );
        starsOrder.setActionTaken( StarsUtils.forceNotNull(liteOrder.getActionTaken()) );
        starsOrder.setAddtlOrderNumber( StarsUtils.forceNotNull(liteOrder.getAdditionalOrderNumber()) );
        
        return starsOrder;
    }
    
    public static StarsLMProgram createStarsLMProgram(LiteStarsLMProgram liteProg, LiteAccountInfo liteAcctInfo)
    {
        StarsLMProgram starsProg = new StarsLMProgram();
        starsProg.setProgramID( liteProg.getProgramID() );
        starsProg.setApplianceCategoryID( liteProg.getPublishedProgram().getApplianceCategoryID() );
        starsProg.setGroupID( liteProg.getGroupID() );
        starsProg.setProgramName( StarsUtils.getPublishedProgramName(liteProg.getPublishedProgram()) );
        
        if (liteProg.isInService()) {
            starsProg.setStatus( ServletUtils.IN_SERVICE );
        } else {
            starsProg.setStatus( ServletUtils.OUT_OF_SERVICE );
        }
        
        for (int i = liteAcctInfo.getProgramHistory().size() - 1; i >= 0; i--) {
            LiteLMProgramEvent liteEvent = liteAcctInfo.getProgramHistory().get(i);
            if (liteEvent.getProgramID() == liteProg.getProgramID()) {
                YukonListEntry entry = YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteEvent.getActionID() );
                if (entry != null && entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_SIGNUP) {
                    starsProg.setDateEnrolled( new Date(liteEvent.getEventDateTime()) );
                    break;
                }
            }
        }
        
        return starsProg;
    }
    
    public static StarsLMProgramHistory createStarsLMProgramHistory(
        LiteAccountInfo liteAcctInfo, LiteStarsEnergyCompany energyCompany)
    {
        StarsLMProgramHistory starsProgHist = new StarsLMProgramHistory();
        List<LiteLMProgramEvent> liteProgHist2 = 
            new ArrayList<LiteLMProgramEvent>( liteAcctInfo.getProgramHistory() );
        Map<Date, StarsLMProgramEvent> progHistMap = new TreeMap<Date, StarsLMProgramEvent>();
        
        for (int i = 0; i < liteProgHist2.size(); i++) {
            LiteLMProgramEvent liteEvent = liteProgHist2.get(i);
            
            StarsLMProgramEvent starsEvent = new StarsLMProgramEvent();
            setStarsLMCustomerEvent( starsEvent, liteEvent );
            starsEvent.addProgramID( liteEvent.getProgramID() );
            
            if (starsEvent.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION) {
                // Get opt out duration (in number of hours)
                boolean foundDuration = false;
                
                Iterator<LiteLMProgramEvent> it = liteProgHist2.listIterator(i + 1);
                while (it.hasNext()) {
                    LiteLMProgramEvent liteEvent2 = it.next();
                    if (liteEvent2.getProgramID() != liteEvent.getProgramID()) {
                        continue;
                    }
                    
                    YukonListEntry entry = YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteEvent2.getActionID() );
                    
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
                
                if (!foundDuration) {
                    continue;
                }
            }
            
            StarsLMProgramEvent starsEvent2 = progHistMap.get( starsEvent.getEventDateTime() );
            if (starsEvent2 == null) {
                progHistMap.put( starsEvent.getEventDateTime(), starsEvent );
            } else {    // Found events happened at the same time
                if (starsEvent2.getYukonDefID() != starsEvent.getYukonDefID()) {
                    progHistMap.put( starsEvent.getEventDateTime(), starsEvent );
                } else {    // Same event action
                    if (!starsEvent.hasDuration()) {
                        starsEvent2.addProgramID( liteEvent.getProgramID() );
                    } else {    // Temporary opt out action
                        if (starsEvent2.getDuration() == starsEvent.getDuration()) {
                            starsEvent2.addProgramID( liteEvent.getProgramID() );
                        } else {
                            progHistMap.put( starsEvent.getEventDateTime(), starsEvent );
                        }
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
            List<StarsLMProgramEvent> events = new ArrayList<StarsLMProgramEvent>();
            
            for (int i = 0; i < optoutEvents.length; i++) {
                StarsLMProgramEvent relatedEvent = null;
                for (int j = 0; j < events.size(); j++) {
                    StarsLMProgramEvent event = events.get(j);
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
                        for (int j = 0; j < liteAcctInfo.getPrograms().size(); j++) {
                            relatedEvent.addProgramID( (liteAcctInfo.getPrograms().get(j)).getProgramID() );
                        }
                    }
                    else {
                        // Opt out event for a hardware
                        for (LiteStarsAppliance liteApp : liteAcctInfo.getAppliances()) {
                            if (liteApp.getInventoryID() == optoutEvents[i].getInventoryID() && liteApp.getProgramID() > 0) {
                                boolean programAdded = false;
                                for (int k = 0; k < relatedEvent.getProgramIDCount(); k++) {
                                    if (relatedEvent.getProgramID(k) == liteApp.getProgramID()) {
                                        programAdded = true;
                                        break;
                                    }
                                }
                                if (!programAdded) {
                                    relatedEvent.addProgramID( liteApp.getProgramID() );
                                }
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
        
        for (int j = 0; j < liteProg.getGroupIDs().length; j++) {
            YukonPao yukonPao = YukonSpringHook.getBean(PaoDao.class).getYukonPao(liteProg.getGroupIDs()[j]);
            
            if (yukonPao.getPaoIdentifier().getPaoType() == PaoType.MACRO_GROUP) {
                java.sql.Connection conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
                try {
                    GenericMacro[] groups = GenericMacro.getGenericMacros(new Integer(yukonPao.getPaoIdentifier().getPaoId()),
                                                                          MacroTypes.GROUP,
                                                                          conn);
                    List<Integer> paoIds = new ArrayList<Integer>();
                    for (GenericMacro group : groups) {
                        paoIds.add(group.getChildID());
                    }
                    Map<Integer, String> paoMap = YukonSpringHook.getBean(PaoDao.class)
                                                            .getYukonPAONames(paoIds);
                    for (GenericMacro group : groups) {
                        AddressingGroup addrGroup = new AddressingGroup();
                        addrGroup.setEntryID(group.getChildID());
                        addrGroup.setContent(paoMap.get(group.getChildID()));
                        starsProg.addAddressingGroup(addrGroup);
                    }
                }
                catch (java.sql.SQLException e) {
                    CTILogger.error( e.getMessage(), e );
                }
                finally {
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                    }
                    catch (java.sql.SQLException e) {}
                }
            }
            else {
                AddressingGroup addrGroup = new AddressingGroup();
                addrGroup.setEntryID( liteProg.getGroupIDs()[j] );
                addrGroup.setContent( YukonSpringHook.getBean(PaoDao.class).getYukonPAOName(yukonPao.getPaoIdentifier().getPaoId()) );
                starsProg.addAddressingGroup( addrGroup );
            }
        }
        
        if (starsProg.getAddressingGroupCount() == 0) {
            starsProg.addAddressingGroup( (AddressingGroup)StarsFactory.newEmptyStarsCustListEntry(AddressingGroup.class) );
        }
    }
    
    public static StarsApplianceCategory createStarsApplianceCategory(LiteApplianceCategory liteAppCat, LiteStarsEnergyCompany energyCompany) {
        StarsApplianceCategory starsAppCat = new StarsApplianceCategory();
        starsAppCat.setApplianceCategoryID( liteAppCat.getApplianceCategoryID() );
        starsAppCat.setCategoryID( liteAppCat.getCategoryID() );
        starsAppCat.setInherited(energyCompany.isApplianceCategoryInherited(liteAppCat.getApplianceCategoryID()));
        starsAppCat.setDescription( StarsUtils.forceNotNull(liteAppCat.getDescription()) );
        
        LiteWebConfiguration liteConfig = StarsDatabaseCache.getInstance().getWebConfiguration( liteAppCat.getWebConfigurationID() );
        StarsWebConfig starsConfig = createStarsWebConfig( liteConfig );
        starsAppCat.setStarsWebConfig( starsConfig );
        
        Iterable<LiteLMProgramWebPublishing> programs = liteAppCat.getPublishedPrograms();
        Set<Integer> deviceIds = new HashSet<Integer>();
        
        for (final LiteLMProgramWebPublishing program : programs) {
            int deviceId = program.getDeviceID();
            if (deviceId > 0) {
                deviceIds.add(deviceId);
            }    
        }
        
        StarsDatabaseCache starsDatabaseCache = StarsDatabaseCache.getInstance();
        Map<Integer, String> nameMap = YukonSpringHook.getBean(PaoDao.class).getYukonPAONames(deviceIds);
        
        for (final LiteLMProgramWebPublishing liteProg : programs) {
            int deviceId = liteProg.getDeviceID();
            
            StarsEnrLMProgram starsProg = new StarsEnrLMProgram();
            starsProg.setProgramID( liteProg.getProgramID() );
            starsProg.setDeviceID(deviceId);
            starsProg.setProgramOrder(liteProg.getProgramOrder());
            
            if (deviceId > 0) {
                String yukonName = nameMap.get(deviceId);
                if (yukonName == null) {
                    yukonName = "No Yukon name found.";
                }
                starsProg.setYukonName(yukonName);
            }
            
            liteConfig = starsDatabaseCache.getWebConfiguration( liteProg.getWebSettingsID() );
            starsProg.setStarsWebConfig( createStarsWebConfig(liteConfig) );
            
            setAddressingGroups( starsProg, liteProg );
            
            if (liteProg.getChanceOfControlID() != 0) {
                starsProg.setChanceOfControl( (ChanceOfControl) StarsFactory.newStarsCustListEntry(
                        YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(liteProg.getChanceOfControlID()), ChanceOfControl.class) );
            }
            
            starsAppCat.addStarsEnrLMProgram( starsProg );
        }
        
        return starsAppCat;
    }
    
    public static StarsAppliance createStarsAppliance(LiteStarsAppliance liteApp, LiteStarsEnergyCompany energyCompany) {
        StarsAppliance starsApp = new StarsAppliance();
        
        starsApp.setApplianceID( liteApp.getApplianceID() );
        starsApp.setApplianceCategoryID(liteApp.getApplianceCategory().getApplianceCategoryId());
        starsApp.setInventoryID( liteApp.getInventoryID() );
        starsApp.setProgramID( liteApp.getProgramID() );
        starsApp.setAddressingGroupID( liteApp.getAddressingGroupID() );
        starsApp.setLoadNumber( liteApp.getLoadNumber() );
        
        starsApp.setNotes( StarsUtils.forceNotNull(liteApp.getNotes()) );
        starsApp.setModelNumber( StarsUtils.forceNotNull(liteApp.getModelNumber()) );
        
        if (liteApp.getKwCapacity() > 0) {
            starsApp.setKwCapacity( liteApp.getKwCapacity() );
        }
        if (liteApp.getEfficiencyRating() > 0) {
            starsApp.setEfficiencyRating( liteApp.getEfficiencyRating() );
        }
        if (liteApp.getYearManufactured() > 0) {
            starsApp.setYearManufactured( liteApp.getYearManufactured() );
        }
           
        Manufacturer manu = new Manufacturer();
        setStarsCustListEntry( manu, YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(liteApp.getManufacturerID()) );
        starsApp.setManufacturer( manu );
        
        Location loc = new Location();
        setStarsCustListEntry( loc, YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(liteApp.getLocationID()) );
        starsApp.setLocation( loc );
        
        starsApp.setServiceCompany( new ServiceCompany() );
        
        int appCatDefID = YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( energyCompany.getApplianceCategory(liteApp.getApplianceCategory().getApplianceCategoryId()).getCategoryID() ).getYukonDefID();
        
        if (liteApp.getAirConditioner() != null || appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_AIR_CONDITIONER) {
            LiteStarsAppliance.AirConditioner liteAC = liteApp.getAirConditioner();
            if (liteAC == null) {
                liteAC = new LiteStarsAppliance.AirConditioner();
            }
            
            AirConditioner ac = new AirConditioner();
            ac.setTonnage(
                (Tonnage) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteAC.getTonnageID() ),
                    Tonnage.class)
            );
            ac.setAcType(
                (ACType) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteAC.getTypeID() ),
                    ACType.class)
            );
            starsApp.setAirConditioner( ac );
        }
        else if (liteApp.getDualStageAirCond() != null || appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUALSTAGE) {
            LiteStarsAppliance.DualStageAirCond liteAC = liteApp.getDualStageAirCond();
            if (liteAC == null) {
                liteAC = new LiteStarsAppliance.DualStageAirCond();
            }
            
            DualStageAC ac = new DualStageAC();
            ac.setTonnage(
                (Tonnage) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteAC.getStageOneTonnageID() ),
                    Tonnage.class)
            );
            ac.setStageTwoTonnage(
                  (Tonnage) StarsFactory.newStarsCustListEntry(
                      YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteAC.getStageTwoTonnageID() ),
                      Tonnage.class)
            );
            ac.setAcType(
                (ACType) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteAC.getTypeID() ),
                    ACType.class)
            );
            starsApp.setDualStageAC( ac );
        }
        else if (liteApp.getChiller() != null || appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_CHILLER) {
            LiteStarsAppliance.Chiller liteChill = liteApp.getChiller();
            if (liteChill == null) {
                liteChill = new LiteStarsAppliance.Chiller();
            }
            
            Chiller chill = new Chiller();
            chill.setTonnage(
                (Tonnage) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteChill.getTonnageID() ),
                    Tonnage.class)
            );
            chill.setAcType(
                (ACType) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteChill.getTypeID() ),
                    ACType.class)
            );
            starsApp.setChiller( chill );
        }
        else if (liteApp.getWaterHeater() != null || appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER) {
            LiteStarsAppliance.WaterHeater liteWH = liteApp.getWaterHeater();
            if (liteWH == null) {
                liteWH = new LiteStarsAppliance.WaterHeater();
            }
            
            WaterHeater wh = new WaterHeater();
            wh.setNumberOfGallons(
                (NumberOfGallons) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteWH.getNumberOfGallonsID() ),
                    NumberOfGallons.class)
            );
            wh.setEnergySource(
                (EnergySource) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteWH.getEnergySourceID() ),
                    EnergySource.class)
            );
            if (liteWH.getNumberOfElements() > 0) {
                wh.setNumberOfElements( liteWH.getNumberOfElements() );
            }
            starsApp.setWaterHeater( wh );
        }
        else if (liteApp.getDualFuel() != null || appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL) {
            LiteStarsAppliance.DualFuel liteDF = liteApp.getDualFuel();
            if (liteDF == null) {
                liteDF = new LiteStarsAppliance.DualFuel();
            }
            
            DualFuel df = new DualFuel();
            df.setSwitchOverType(
                (SwitchOverType) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteDF.getSwitchOverTypeID() ),
                    SwitchOverType.class)
            );
            if (liteDF.getSecondaryKWCapacity() > 0) {
                df.setSecondaryKWCapacity( liteDF.getSecondaryKWCapacity() );
            }
            df.setSecondaryEnergySource(
                (SecondaryEnergySource) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteDF.getSecondaryEnergySourceID() ),
                    SecondaryEnergySource.class)
            );
            starsApp.setDualFuel( df );
        }
        else if (liteApp.getGenerator() != null || appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GENERATOR) {
            LiteStarsAppliance.Generator liteGen = liteApp.getGenerator();
            if (liteGen == null) {
                liteGen = new LiteStarsAppliance.Generator();
            }
            
            Generator gen = new Generator();
            gen.setTransferSwitchType(
                (TransferSwitchType) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteGen.getTransferSwitchTypeID() ),
                    TransferSwitchType.class)
            );
            gen.setTransferSwitchManufacturer(
                (TransferSwitchManufacturer) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteGen.getTransferSwitchMfgID() ),
                    TransferSwitchManufacturer.class)
            );
            if (liteGen.getPeakKWCapacity() > 0) {
                gen.setPeakKWCapacity( liteGen.getPeakKWCapacity() );
            }
            if (liteGen.getFuelCapGallons() > 0) {
                gen.setFuelCapGallons( liteGen.getFuelCapGallons() );
            }
            if (liteGen.getStartDelaySeconds() > 0) {
                gen.setStartDelaySeconds( liteGen.getStartDelaySeconds() );
            }
            starsApp.setGenerator( gen );
        }
        else if (liteApp.getGrainDryer() != null || appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GRAIN_DRYER) {
            LiteStarsAppliance.GrainDryer liteGDry = liteApp.getGrainDryer();
            if (liteGDry == null) {
                liteGDry = new LiteStarsAppliance.GrainDryer();
            }
            
            GrainDryer gd = new GrainDryer();
            gd.setDryerType(
                (DryerType) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteGDry.getDryerTypeID() ),
                    DryerType.class)
            );
            gd.setBinSize(
                (BinSize) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteGDry.getBinSizeID() ),
                    BinSize.class)
            );
            gd.setBlowerEnergySource(
                (BlowerEnergySource) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteGDry.getBlowerEnergySourceID() ),
                    BlowerEnergySource.class)
            );
            gd.setBlowerHorsePower(
                (BlowerHorsePower) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteGDry.getBlowerHorsePowerID() ),
                    BlowerHorsePower.class)
            );
            gd.setBlowerHeatSource(
                (BlowerHeatSource) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteGDry.getBlowerHeatSourceID() ),
                    BlowerHeatSource.class)
            );
            starsApp.setGrainDryer( gd );
        }
        else if (liteApp.getStorageHeat() != null || appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT) {
            LiteStarsAppliance.StorageHeat liteSH = liteApp.getStorageHeat();
            if (liteSH == null) {
                liteSH = new LiteStarsAppliance.StorageHeat();
            }
            
            StorageHeat sh = new StorageHeat();
            sh.setStorageType(
                (StorageType) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteSH.getStorageTypeID() ),
                    StorageType.class)
            );
            if (liteSH.getPeakKWCapacity() > 0) {
                sh.setPeakKWCapacity( liteSH.getPeakKWCapacity() );
            }
            if (liteSH.getHoursToRecharge() > 0) {
                sh.setHoursToRecharge( liteSH.getHoursToRecharge() );
            }
            starsApp.setStorageHeat( sh );
        }
        else if (liteApp.getHeatPump() != null || appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_HEAT_PUMP) {
            LiteStarsAppliance.HeatPump liteHP = liteApp.getHeatPump();
            if (liteHP == null) {
                liteHP = new LiteStarsAppliance.HeatPump();
            }
            
            HeatPump hp = new HeatPump();
            hp.setPumpType(
                (PumpType) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteHP.getPumpTypeID() ),
                    PumpType.class)
            );
            hp.setPumpSize(
                (PumpSize) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteHP.getPumpSizeID() ),
                    PumpSize.class)
            );
            hp.setStandbySource(
                (StandbySource) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteHP.getStandbySourceID() ),
                    StandbySource.class)
            );
            if (liteHP.getSecondsDelayToRestart() > 0) {
                hp.setRestartDelaySeconds( liteHP.getSecondsDelayToRestart() );
            }
            starsApp.setHeatPump( hp );
        }
        else if (liteApp.getIrrigation() != null || appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_IRRIGATION) {
            LiteStarsAppliance.Irrigation liteIrr = liteApp.getIrrigation();
            if (liteIrr == null) {
                liteIrr = new LiteStarsAppliance.Irrigation();
            }
            
            Irrigation irr = new Irrigation();
            irr.setIrrigationType(
                (IrrigationType) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteIrr.getIrrigationTypeID() ),
                    IrrigationType.class)
            );
            irr.setHorsePower(
                (HorsePower) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteIrr.getHorsePowerID() ),
                    HorsePower.class)
            );
            irr.setEnergySource(
                (EnergySource) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteIrr.getEnergySourceID() ),
                    EnergySource.class)
            );
            irr.setSoilType(
                (SoilType) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteIrr.getSoilTypeID() ),
                    SoilType.class)
            );
            irr.setMeterLocation(
                (MeterLocation) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteIrr.getMeterLocationID() ),
                    MeterLocation.class)
            );
            irr.setMeterVoltage(
                (MeterVoltage) StarsFactory.newStarsCustListEntry(
                    YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteIrr.getMeterVoltageID() ),
                    MeterVoltage.class)
            );
            starsApp.setIrrigation( irr );
        }
        
        return starsApp;
    }
    
    public static StarsCustSelectionList createStarsCustSelectionList(YukonSelectionList yukonList) {
        StarsCustSelectionList starsList = new StarsCustSelectionList();

        starsList.setListID(yukonList.getListId());
        starsList.setListName( yukonList.getListName() );
        
        List<YukonListEntry> entries = yukonList.getYukonListEntries();
        if (entries.size() == 0) {
            // Assign the list a "default" entry if the list is empty
            starsList.addStarsSelectionListEntry( (StarsSelectionListEntry)
                    StarsFactory.newEmptyStarsCustListEntry(StarsSelectionListEntry.class) );
        }
        else {
            for (int i = 0; i < entries.size(); i++) {
                YukonListEntry yukonEntry = entries.get(i);
                StarsSelectionListEntry starsEntry = new StarsSelectionListEntry();
                setStarsCustListEntry( starsEntry, yukonEntry );
                starsEntry.setYukonDefID( yukonEntry.getYukonDefID() );
                starsList.addStarsSelectionListEntry( starsEntry );
            }
        }
        
        return starsList;
    }
    
    public static StarsCustomerSelectionLists createStarsCustomerSelectionLists(
            List<YukonSelectionList> selectionLists) {
        StarsCustomerSelectionLists starsCustSelLists = new StarsCustomerSelectionLists();
        for (int i = 0; i < selectionLists.size(); i++) {
            YukonSelectionList list = selectionLists.get(i);
            starsCustSelLists.addStarsCustSelectionList( StarsLiteFactory.createStarsCustSelectionList(list) );
        }
        
        return starsCustSelLists;
    }
    
    public static void setStarsEnrollmentPrograms(
            StarsEnrollmentPrograms starsAppCats, 
            Iterable<LiteApplianceCategory> liteAppCats, 
            LiteStarsEnergyCompany energyCompany) 
    {
        starsAppCats.removeAllStarsApplianceCategory();
        
        for (final LiteApplianceCategory liteAppCat : liteAppCats) {
            starsAppCats.addStarsApplianceCategory(
                    StarsLiteFactory.createStarsApplianceCategory(liteAppCat, energyCompany) );
        }
    }
    
    public static StarsLMProgramEvent createStarsOptOutEvent(OptOutEventQueue.OptOutEvent event, LiteStarsEnergyCompany energyCompany) {
        StarsLMProgramEvent starsEvent = new StarsLMProgramEvent();
        
        YukonListEntry optOutEntry = selectionListService.getListEntry(energyCompany, YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION );
        starsEvent.setEventAction( optOutEntry.getEntryText() );
        starsEvent.setYukonDefID( optOutEntry.getYukonDefID() );
        starsEvent.setEventDateTime( new Date(event.getStartDateTime()) );
        starsEvent.setDuration( event.getPeriod() );
        starsEvent.setNotes( "" );
        
        final StarsCustAccountInformationDao starsCustAccountInformationDao =
            YukonSpringHook.getBean("starsCustAccountInformationDao", StarsCustAccountInformationDao.class);
        
        LiteAccountInfo liteAcctInfo = 
            starsCustAccountInformationDao.getById(event.getAccountID(), energyCompany.getEnergyCompanyId());
        
        if (event.getInventoryID() != 0) {
            for (LiteStarsAppliance liteApp : liteAcctInfo.getAppliances()) {
                if (liteApp.getInventoryID() == event.getInventoryID() && liteApp.getProgramID() > 0) {
                    boolean programAdded = false;
                    for (int j = 0; j < starsEvent.getProgramIDCount(); j++) {
                        if (starsEvent.getProgramID(j) == liteApp.getProgramID()) {
                            programAdded = true;
                            break;
                        }
                    }
                    if (!programAdded) {
                        starsEvent.addProgramID( liteApp.getProgramID() );
                    }
                }
            }
        }
        else {
            for (int i = 0; i < liteAcctInfo.getPrograms().size(); i++) {
                LiteStarsLMProgram liteProg = liteAcctInfo.getPrograms().get(i);
                starsEvent.addProgramID( liteProg.getProgramID() );
            }
        }
        
        return starsEvent;
    }
    
    
    public static boolean isIdenticalCustomerContact(LiteContact liteContact, StarsCustomerContact starsContact) {
        if (!StarsUtils.forceNotNull(liteContact.getContLastName()).equals( starsContact.getLastName() )
            || !StarsUtils.forceNotNull(liteContact.getContFirstName()).equals( starsContact.getFirstName() )
            || liteContact.getLiteContactNotifications().size() != starsContact.getContactNotificationCount()) {
            return false;
        }
        
        for (int i = 0; i < liteContact.getLiteContactNotifications().size(); i++) {
            LiteContactNotification liteNotif = liteContact.getLiteContactNotifications().get(i);
            StarsContactNotification starsNotif = ServletUtils.getContactNotification( starsContact, liteNotif.getNotificationCategoryID() );
            if (starsNotif == null
                || liteNotif.isDisabled() != starsNotif.getDisabled()
                || !StarsUtils.forceNotNull(liteNotif.getNotification()).equals( starsNotif.getNotification() )) {
                return false;
            }
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
                && StarsUtils.forceNotNull(liteAcctSite.getPropertyNotes()).equals( starsAcctSite.getPropertyNotes() )
                && StarsUtils.forceNotNull(liteAcctSite.getCustAtHome()).equals( starsAcctSite.getCustAtHome() )
                && StarsUtils.forceNotNull(liteAcctSite.getCustStatus()).equals( starsAcctSite.getCustStatus() ));
    }
    
}
