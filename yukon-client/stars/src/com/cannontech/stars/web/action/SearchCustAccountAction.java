package com.cannontech.stars.web.action;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.xml.serialize.AdditionalContact;
import com.cannontech.stars.xml.serialize.BillingAddress;
import com.cannontech.stars.xml.serialize.PrimaryContact;
import com.cannontech.stars.xml.serialize.SearchBy;
import com.cannontech.stars.xml.serialize.StarsAppliance;
import com.cannontech.stars.xml.serialize.StarsAppliances;
import com.cannontech.stars.xml.serialize.StarsCustAccountInfo;
import com.cannontech.stars.xml.serialize.StarsCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.stars.xml.serialize.StarsLMHardware;
import com.cannontech.stars.xml.serialize.StarsLMProgram;
import com.cannontech.stars.xml.serialize.StarsLMPrograms;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSearchCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsSearchCustomerAccountResponse;
import com.cannontech.stars.xml.serialize.StarsSiteInformation;
import com.cannontech.stars.xml.serialize.StreetAddress;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

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
            SearchBy searchBy = new SearchBy();
            searchBy.setEntryID( Integer.parseInt(req.getParameter("SearchBy")) );
            searchAccount.setSearchBy( searchBy );
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
            com.cannontech.database.data.stars.customer.CustomerAccount account = null;

            if (searchAccount.getSearchBy().getEntryID() == 22) {		// "Acct #"
                account = com.cannontech.database.data.stars.customer.CustomerAccount.searchByAccountNumber(
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

            com.cannontech.database.db.stars.customer.CustomerAccount accountDB = account.getCustomerAccount();
            com.cannontech.database.data.stars.customer.CustomerBase customer = account.getCustomerBase();
            com.cannontech.database.db.stars.customer.CustomerBase customerDB = customer.getCustomerBase();
            
            Hashtable selectionList = (Hashtable) operator.getAttribute( "CUSTOMER_SELECTION_LIST" );
            Integer custTypeCommID = com.cannontech.database.data.stars.CustomerListEntry.getListEntryID(
		    		(Integer) selectionList.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_CUSTOMERTYPE),
		            com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_CUSTTYPE_COMM );
		    if (custTypeCommID == null)
		    	custTypeCommID = new Integer( com.cannontech.database.db.stars.CustomerListEntry.NONE_INT );

            StarsCustomerAccount starsAccount = new StarsCustomerAccount();
            starsAccount.setAccountNumber( accountDB.getAccountNumber() );
            starsAccount.setIsCommercial( customerDB.getCustomerTypeID().intValue() == custTypeCommID.intValue() );
            starsAccount.setCompany( "" );
            if (accountDB.getAccountNotes() != null)
                starsAccount.setAccountNotes( accountDB.getAccountNotes() );
            else
                starsAccount.setAccountNotes( "" );

            com.cannontech.database.data.stars.customer.AccountSite site = account.getAccountSite();
            com.cannontech.database.db.stars.customer.AccountSite siteDB = site.getAccountSite();

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

            com.cannontech.database.data.stars.customer.SiteInformation siteInfo = site.getSiteInformation();
            com.cannontech.database.db.stars.customer.SiteInformation siteInfoDB = siteInfo.getSiteInformation();
            com.cannontech.database.db.stars.Substation substation = siteInfo.getSubstation();

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
                com.cannontech.database.data.stars.appliance.ApplianceBase appliance =
                            (com.cannontech.database.data.stars.appliance.ApplianceBase) applianceVector.elementAt(i);
                com.cannontech.database.db.stars.appliance.ApplianceCategory category = appliance.getApplianceCategory();
                com.cannontech.database.db.stars.hardware.LMHardwareConfiguration config = appliance.getLMHardwareConfig();

                StarsAppliance starsApp = new StarsAppliance();
                starsApp.setApplianceID( appliance.getApplianceBase().getApplianceID().intValue() );
                if (config != null)
                    starsApp.setInventoryID( config.getInventoryID().intValue() );
                else
                    starsApp.setInventoryID( -1 );
                starsApp.setLmProgramID( appliance.getApplianceBase().getLMProgramID().intValue() );
                starsApp.setApplianceCategoryID( category.getCategoryID().intValue() );
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
                com.cannontech.database.data.stars.hardware.InventoryBase inventory =
                            (com.cannontech.database.data.stars.hardware.InventoryBase) inventoryVector.elementAt(i);

                if (inventory instanceof com.cannontech.database.data.stars.hardware.LMHardwareBase) {
                    // This is a LM hardware
                    com.cannontech.database.data.stars.hardware.LMHardwareBase hardware =
                                (com.cannontech.database.data.stars.hardware.LMHardwareBase) inventory;

                    StarsLMHardware starsHW = new StarsLMHardware();
                    starsHW.setInventoryID( hardware.getInventoryBase().getInventoryID().intValue() );
                    starsHW.setCategory( hardware.getCategory().getEntryText() );
                    starsHW.setInstallationCompany( hardware.getInstallationCompany().getCompanyName() );
                    starsHW.setReceiveDate( hardware.getInventoryBase().getReceiveDate() );
                    starsHW.setInstallDate( hardware.getInventoryBase().getInstallDate() );
                    starsHW.setRemoveDate( hardware.getInventoryBase().getReceiveDate() );
                    if (hardware.getInventoryBase().getAlternateTrackingNumber() != null)
                        starsHW.setAltTrackingNumber( hardware.getInventoryBase().getAlternateTrackingNumber() );
                    else
                        starsHW.setAltTrackingNumber( "" );
                    starsHW.setVoltage( hardware.getVoltage().getEntryText() );
                    if (hardware.getInventoryBase().getNotes() != null)
                        starsHW.setNotes( hardware.getInventoryBase().getNotes() );
                    else
                        starsHW.setNotes( "" );
                    starsHW.setManufactureSerialNumber( hardware.getLMHardwareBase().getManufacturerSerialNumber() );
                    starsHW.setLMDeviceType( hardware.getLMHardwareType().getEntryText() );
                    starsHW.setStarsLMHardwareHistory( com.cannontech.database.data.stars.event.LMHardwareEvent.getStarsLMHardwareHistory(
                    			hardware.getInventoryBase().getInventoryID()) );

                    inventories.addStarsLMHardware( starsHW );
                }
            }
            accountInfo.setStarsInventories( inventories );

            StarsLMPrograms programs = new StarsLMPrograms();
            Hashtable progTable = new Hashtable();

            for (int i = 0; i < applianceVector.size(); i++) {
                com.cannontech.database.data.stars.appliance.ApplianceBase appliance =
                        (com.cannontech.database.data.stars.appliance.ApplianceBase) applianceVector.elementAt(i);
                com.cannontech.database.data.device.lm.LMProgramBase program = appliance.getLMProgram();
                if (program.getPAObjectID().intValue() == com.cannontech.database.db.stars.appliance.ApplianceBase.NONE_INT)
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