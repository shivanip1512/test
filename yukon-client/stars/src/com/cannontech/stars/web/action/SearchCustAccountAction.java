package com.cannontech.stars.web.action;

import javax.servlet.http.*;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import javax.xml.soap.SOAPMessage;
import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.xml.util.*;
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

public class SearchCustAccountAction implements ActionBase {

    public SearchCustAccountAction() {
        super();
    }

    public SOAPMessage build(HttpServletRequest req, HttpSession session) {
        try {
            StarsSearchCustomerAccount searchAccount = new StarsSearchCustomerAccount();
            searchAccount.setSearchBy( StarsSearchByType.valueOf(req.getParameter("SearchBy")) );
            searchAccount.setSearchValue( req.getParameter("SearchValue") );

            StarsOperation operation = new StarsOperation();
            operation.setStarsSearchCustomerAccount( searchAccount );

            return SOAPUtil.buildSOAPMessage( operation );
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
        try {
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            StarsOperation respOper = new StarsOperation();

            /* This part is for consumer login, must be removed later */
            Integer energyCompanyID = (Integer) session.getAttribute("ENERGY_COMPANY_ID");
            StarsOperator operator = null;

            if (energyCompanyID == null) {
                operator = (StarsOperator) session.getAttribute("OPERATOR");
                if (operator == null) {
                    StarsFailure failure = new StarsFailure();
                    failure.setStatusCode( StarsConstants.FAILURE_CODE_SESSION_INVALID );
                    failure.setDescription("Session invalidated, please login again");
                    respOper.setStarsFailure( failure );
                    return SOAPUtil.buildSOAPMessage( respOper );
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
                failure.setStatusCode( StarsConstants.FAILURE_CODE_SESSION_INVALID );
                failure.setDescription("No customer account matching the specified account number");
                respOper.setStarsFailure( failure );
                return SOAPUtil.buildSOAPMessage( respOper );
            }

			if (operator != null)
            	operator.setAttribute("CUSTOMER_ACCOUNT", account);
            else
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

            StarsCustAccountInfo accountInfo = new StarsSearchCustomerAccountResponse();
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
                starsApp.setApplianceCategory( category.getCategory() );
                starsApp.setCategoryDescription( category.getDescription() );

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
                    starsHW.setStarsLMHardwareHistory( com.cannontech.database.data.starsevent.LMHardwareActivity.getStarsLMHardwareHistory(
                    			hardware.getInventoryBase().getInventoryID()) );

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
                starsProg.setStatus( "In Service" );
                starsProg.setProgramName( program.getPAOName() );
                starsProg.setGroupID( appliance.getLMHardwareConfig().getAddressingGroupID().intValue() );

                StarsLMControlHistory ctrlHist = LMControlHistory.getStarsLMControlHistory(
                        appliance.getLMHardwareConfig().getAddressingGroupID(), com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod.PASTDAY, true);
                starsProg.setStarsLMControlHistory( ctrlHist );

                progTable.put(program.getPAObjectID(), starsProg);
            }

            Iterator it = progTable.values().iterator();
            while (it.hasNext()) {
                StarsLMProgram starsProg = (StarsLMProgram) it.next();
                programs.addStarsLMProgram( starsProg );
            }

            accountInfo.setStarsLMPrograms( programs );
            respOper.setStarsSearchCustomerAccountResponse( (StarsSearchCustomerAccountResponse) accountInfo );

            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
        try {
            StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) return failure.getStatusCode();
			
            StarsCustAccountInfo accountInfo = operation.getStarsSearchCustomerAccountResponse();
            if (accountInfo == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;

			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			if (operator != null)
	            operator.setAttribute("CUSTOMER_ACCOUNT_INFORMATION", accountInfo);
	        else
	        	session.setAttribute("CUSTOMER_ACCOUNT_INFORMATION", accountInfo);
            
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
    }
}