package com.cannontech.stars.web.action;

import javax.servlet.http.*;

import java.util.*;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.serialize.types.StarsSearchByType;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class SearchCustAccountAction extends ActionBase {

    public SearchCustAccountAction() {
        super();
    }

    public StarsOperation build(HttpServletRequest req, HttpSession session) {
        StarsSearchCustomerAccount searchAccount = new StarsSearchCustomerAccount();
        searchAccount.setSearchBy( StarsSearchByType.valueOf(req.getParameter("SearchBy")) );
        searchAccount.setSearchValue( req.getParameter("SearchValue") );

        StarsOperation operation = new StarsOperation();
        operation.setStarsSearchCustomerAccount( searchAccount );

        return operation;
    }

    public StarsOperation process(StarsOperation reqOper, HttpSession session) {
        StarsOperation respOper = new StarsOperation();

        /* This part is for consumer login, must be removed later */
        Integer energyCompanyID = (Integer) session.getAttribute("ENERGY_COMPANY_ID");

        if (energyCompanyID == null) {
            com.cannontech.database.data.web.Operator operator = (com.cannontech.database.data.web.Operator) session.getAttribute("OPERATOR");
            if (operator == null) {
                StarsFailure failure = new StarsFailure();
                failure.setContent("Session invalidated, please login again");
                respOper.setStarsFailure( failure );
                return respOper;
            }

            energyCompanyID = new Integer( (int)operator.getEnergyCompanyID() );
        }

        StarsSearchCustomerAccount searchAccount = reqOper.getStarsSearchCustomerAccount();
        com.cannontech.database.data.starscustomer.CustomerAccount account = null;

        if (searchAccount.getSearchBy().getType() == StarsSearchByType.ACCOUNTNUMBER_TYPE) {
            account = com.cannontech.database.data.starscustomer.CustomerAccount.searchByAccountNumber(
                        energyCompanyID, searchAccount.getSearchValue() );
        }

        if (account == null) {
            StarsFailure failure = new StarsFailure();
            failure.setContent("No customer account matching the specified account number");
            respOper.setStarsFailure( failure );
            return respOper;
        }

        session.setAttribute("CUSTOMER_ACCOUNT", account);

        com.cannontech.database.db.starscustomer.CustomerAccount accountDB = account.getCustomerAccount();
        com.cannontech.database.data.starscustomer.CustomerBase customer = account.getCustomerBase();
        com.cannontech.database.db.starscustomer.CustomerBase customerDB = customer.getCustomerBase();

        StarsCustomerAccount starsAccount = new StarsCustomerAccount();
        starsAccount.setAccountNumber( accountDB.getAccountNumber() );
        starsAccount.setIsCommercial( customerDB.getCustomerType().equalsIgnoreCase("Commercial") );
        starsAccount.setCompany( "" );
        if (accountDB.getAccountNotes() != null)
            starsAccount.setAccountNotes( accountDB.getAccountNotes() );
        else
            starsAccount.setAccountNotes( "" );

        com.cannontech.database.data.starscustomer.AccountSite site = account.getAccountSite();
        com.cannontech.database.db.starscustomer.AccountSite siteDB = site.getAccountSite();

        starsAccount.setPropertyNumber( siteDB.getSiteNumber() );
        if (siteDB.getPropertyNotes() != null)
            starsAccount.setPropertyNotes( siteDB.getPropertyNotes() );
        else
            starsAccount.setPropertyNotes( "" );

        com.cannontech.database.db.customer.CustomerAddress addr = site.getStreetAddress();

        StreetAddress siteAddr = new StreetAddress();
        siteAddr.setStreetAddr1( addr.getLocationAddress1() );
        siteAddr.setStreetAddr2( addr.getLocationAddress2() );
        siteAddr.setCity( addr.getCityName() );
        siteAddr.setState( addr.getStateCode() );
        siteAddr.setZip( addr.getZipCode() );
        starsAccount.setStreetAddress( siteAddr );

        com.cannontech.database.data.starscustomer.SiteInformation siteInfo = site.getSiteInformation();
        com.cannontech.database.db.starscustomer.SiteInformation siteInfoDB = siteInfo.getSiteInformation();
        com.cannontech.database.db.starscustomer.Substation substation = siteInfo.getSubstation();

        StarsSiteInformation starsSiteInfo = new StarsSiteInformation();
        starsSiteInfo.setSubstationName( substation.getSubstationName() );
        starsSiteInfo.setFeeder( siteInfoDB.getFeeder() );
        starsSiteInfo.setPole( siteInfoDB.getPole() );
        starsSiteInfo.setTransformerSize( siteInfoDB.getTransformerSize() );
        starsSiteInfo.setServiceVoltage( siteInfoDB.getServiceVoltage() );
        starsAccount.setStarsSiteInformation( starsSiteInfo );

        addr = account.getBillingAddress();

        BillingAddress billAddr = new BillingAddress();
        billAddr.setStreetAddr1( addr.getLocationAddress1() );
        billAddr.setStreetAddr2( addr.getLocationAddress2() );
        billAddr.setCity( addr.getCityName() );
        billAddr.setState( addr.getStateCode() );
        billAddr.setZip( addr.getZipCode() );
        starsAccount.setBillingAddress( billAddr );

        com.cannontech.database.db.customer.CustomerContact contact = customer.getPrimaryContact();

        PrimaryContact primContact = new PrimaryContact();
        primContact.setLastName( contact.getContLastName() );
        primContact.setFirstName( contact.getContFirstName() );
        primContact.setHomePhone( contact.getContPhone1() );
        primContact.setWorkPhone( contact.getContPhone2() );
        starsAccount.setPrimaryContact( primContact );

        Vector contactList = customer.getCustomerContactVector();

        for (int i = 0; i < contactList.size(); i++) {
            contact = (com.cannontech.database.db.customer.CustomerContact) contactList.elementAt(i);

            AdditionalContact addContact = new AdditionalContact();
            addContact.setLastName( contact.getContLastName() );
            addContact.setFirstName( contact.getContFirstName() );
            addContact.setHomePhone( contact.getContPhone1() );
            addContact.setWorkPhone( contact.getContPhone2() );
            starsAccount.addAdditionalContact( addContact );
        }

        StarsCustomerAccountInformation accountInfo = new StarsCustomerAccountInformation();
        accountInfo.setStarsCustomerAccount( starsAccount );
        accountInfo.setStarsLMPrograms( new StarsLMPrograms() );

        StarsAppliances appliances = new StarsAppliances();
        Vector applianceVector = account.getApplianceVector();

        for (int i = 0; i < applianceVector.size(); i++) {
            com.cannontech.database.data.starsappliance.ApplianceBase appliance =
                        (com.cannontech.database.data.starsappliance.ApplianceBase) applianceVector.elementAt(i);
            com.cannontech.database.db.starsappliance.ApplianceCategory category = appliance.getApplianceCategory();
            com.cannontech.database.db.starshardware.LMHardwareConfiguration config = appliance.getLMHardwareConfig();

            StarsAppliance starsApp = new StarsAppliance();
            starsApp.setApplianceID( appliance.getApplianceBase().getApplianceID().intValue() );
            if (config != null)
                starsApp.setInventoryID( config.getInventoryID().intValue() );
            else
                starsApp.setInventoryID( -1 );
            starsApp.setLmProgramID( appliance.getApplianceBase().getLMProgramID().intValue() );

            StarsApplianceCategory starsCat = new StarsApplianceCategory();
            starsCat.setCategory( category.getCategory() );
            starsCat.setDescription( category.getDescription() );
            starsApp.setStarsApplianceCategory( starsCat );

            if (appliance.getApplianceBase().getNotes() != null)
                starsApp.setNotes( appliance.getApplianceBase().getNotes() );
            else
                starsApp.setNotes( "" );
            // No program information available for now!!!

            appliances.addStarsAppliance( starsApp );
        }
        accountInfo.setStarsAppliances( appliances );

        StarsInventories inventories = new StarsInventories();
        Vector inventoryVector = account.getInventoryVector();

        for (int i = 0; i < inventoryVector.size(); i++) {
            com.cannontech.database.data.starshardware.InventoryBase inventory =
                        (com.cannontech.database.data.starshardware.InventoryBase) inventoryVector.elementAt(i);

            if (inventory instanceof com.cannontech.database.data.starshardware.LMHardwareBase) {
                // This is a LM hardware
                com.cannontech.database.data.starshardware.LMHardwareBase hardware =
                            (com.cannontech.database.data.starshardware.LMHardwareBase) inventory;

                StarsLMHardware starsHW = new StarsLMHardware();
                starsHW.setInventoryID( hardware.getInventoryBase().getInventoryID().intValue() );
                starsHW.setCategory( hardware.getInventoryBase().getCategory() );
                starsHW.setInstallationCompany( hardware.getInstallationCompany().getCompanyName() );
                starsHW.setReceiveDate( new org.exolab.castor.types.Date(hardware.getInventoryBase().getReceiveDate()) );
                starsHW.setInstallDate( new org.exolab.castor.types.Date(hardware.getInventoryBase().getInstallDate()) );
                starsHW.setRemoveDate( new org.exolab.castor.types.Date(hardware.getInventoryBase().getReceiveDate()) );
                if (hardware.getInventoryBase().getAlternateTrackingNumber() != null)
                    starsHW.setAltTrackingNumber( hardware.getInventoryBase().getAlternateTrackingNumber() );
                else
                    starsHW.setAltTrackingNumber( "" );
                starsHW.setVoltage( hardware.getInventoryBase().getVoltage() );
                if (hardware.getInventoryBase().getNotes() != null)
                    starsHW.setNotes( hardware.getInventoryBase().getNotes() );
                else
                    starsHW.setNotes( "" );
                starsHW.setManufactureSerialNumber( hardware.getLMHardwareBase().getManufacturerSerialNumber() );
                starsHW.setLMDeviceType( hardware.getLMHardwareBase().getLMDeviceType() );

                inventories.addStarsLMHardware( starsHW );
            }
        }
        accountInfo.setStarsInventories( inventories );

        StarsLMPrograms programs = new StarsLMPrograms();
        Hashtable progTable = new Hashtable();

        for (int i = 0; i < applianceVector.size(); i++) {
            com.cannontech.database.data.starsappliance.ApplianceBase appliance =
                    (com.cannontech.database.data.starsappliance.ApplianceBase) applianceVector.elementAt(i);
            com.cannontech.database.data.device.lm.LMProgramBase program = appliance.getLMProgram();
            if (program.getPAObjectID().intValue() == com.cannontech.database.db.starsappliance.ApplianceBase.NONE_INT)
                continue;

            StarsLMProgram starsProg = new StarsLMProgram();
            starsProg.setProgramID( program.getPAObjectID().intValue() );
            starsProg.setProgramName( program.getPAOName() );
            starsProg.setGroupID( appliance.getLMHardwareConfig().getAddressingGroupID().intValue() );

            StarsLMControlHistory starsCtrlHist = new StarsLMControlHistory();
            com.cannontech.database.db.pao.LMControlHistory[] ctrlHist =
                    com.cannontech.database.data.starsprogram.LMControlHistory.getLMControlHistory(
                        appliance.getLMHardwareConfig().getAddressingGroupID(), com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod.PASTDAY);
            com.cannontech.database.db.pao.LMControlHistory lastCtrlHist = null;

            for (int j = 0; j < ctrlHist.length; j++) {
                ControlHistory hist = new ControlHistory();
                hist.setControlType( ctrlHist[j].getControlType() );
                hist.setStartDateTime( ctrlHist[j].getStartDateTime() );
                hist.setControlDuration( ctrlHist[j].getControlDuration().intValue() );
                starsCtrlHist.addControlHistory( hist );

                if (lastCtrlHist == null || lastCtrlHist.getLmCtrlHistID().intValue() < ctrlHist[j].getLmCtrlHistID().intValue())
                    lastCtrlHist = ctrlHist[j];
            }

            ControlSummary summary = new ControlSummary();
            summary.setDailyTime(0);
            summary.setMonthlyTime(0);
            summary.setSeasonalTime(0);
            summary.setAnnualTime(0);

            if (lastCtrlHist != null) {
                summary.setDailyTime( lastCtrlHist.getCurrentDailyTime().intValue() );
                summary.setMonthlyTime( lastCtrlHist.getCurrentMonthlyTime().intValue() );
                summary.setSeasonalTime( lastCtrlHist.getCurrentSeasonalTime().intValue() );
                summary.setAnnualTime( lastCtrlHist.getCurrentAnnualTime().intValue() );
            }
            else {
                lastCtrlHist = com.cannontech.database.data.starsprogram.LMControlHistory.getLastLMControlHistory(
                        appliance.getLMHardwareConfig().getAddressingGroupID() );

                if (lastCtrlHist != null) {
                    Calendar nowCal = Calendar.getInstance();
                    Calendar lastCal = Calendar.getInstance();
                    lastCal.setTime( lastCtrlHist.getStartDateTime() );

                    if (lastCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR)) {
                        summary.setAnnualTime( lastCtrlHist.getCurrentAnnualTime().intValue() );
                        // Don't quite know how to deal with season yet, so just let it go with year now
                        summary.setSeasonalTime( lastCtrlHist.getCurrentSeasonalTime().intValue() );

                        if (lastCal.get(Calendar.MONTH) == nowCal.get(Calendar.MONTH)) {
                            summary.setMonthlyTime( lastCtrlHist.getCurrentMonthlyTime().intValue() );

                            if (lastCal.get(Calendar.DAY_OF_MONTH) == nowCal.get(Calendar.DAY_OF_MONTH))
                                summary.setDailyTime( lastCtrlHist.getCurrentDailyTime().intValue() );
                        }
                    }
                }
            }

            starsCtrlHist.setControlSummary( summary );
            starsProg.setStarsLMControlHistory( starsCtrlHist );

            progTable.put(program.getPAObjectID(), starsProg);
        }

        Iterator it = progTable.values().iterator();
        while (it.hasNext()) {
            StarsLMProgram starsProg = (StarsLMProgram) it.next();
            programs.addStarsLMProgram( starsProg );
        }

        accountInfo.setStarsLMPrograms( programs );

        respOper.setStarsCustomerAccountInformation( accountInfo );
        return respOper;
    }

    public boolean parse(StarsOperation operation, HttpSession session) {
        StarsCustomerAccountInformation accountInfo = operation.getStarsCustomerAccountInformation();
        if (accountInfo == null) return false;

        session.setAttribute("CUSTOMER_ACCOUNT_INFORMATION", accountInfo);
        return true;
    }

    public static void main(String[] args) {
    }
}