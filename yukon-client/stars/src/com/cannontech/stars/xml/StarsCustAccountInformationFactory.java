package com.cannontech.stars.xml;

import java.util.*;
import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.xml.util.*;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.database.data.lite.stars.LiteCustomerSelectionList;
import com.cannontech.database.data.stars.customer.CustomerAccount;
import com.cannontech.database.db.stars.*;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class StarsCustAccountInformationFactory {
/*
 * Commented out since cache is used now
 * 
	public static StarsCustAccountInformation getStarsCustAccountInfo(CustomerAccount account, Hashtable selectionLists) {
		try {
            StarsCustAccountInformation accountInfo = new StarsCustAccountInformation();

            com.cannontech.database.db.stars.customer.CustomerAccount accountDB = account.getCustomerAccount();
            com.cannontech.database.data.stars.customer.CustomerBase customer = account.getCustomerBase();
            com.cannontech.database.db.stars.customer.CustomerBase customerDB = customer.getCustomerBase();
            
            int custTypeCommID = StarsCustListEntryFactory.getStarsCustListEntry(
            		(LiteCustomerSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_CUSTOMERTYPE ),
            		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_CUSTTYPE_COMM )
            		.getEntryID();

            StarsCustomerAccount starsAccount = new StarsCustomerAccount();
            starsAccount.setAccountID( accountDB.getAccountID().intValue() );
            starsAccount.setCustomerID( account.getCustomerBase().getCustomerBase().getCustomerID().intValue() );
            starsAccount.setAccountNumber( accountDB.getAccountNumber() );
            starsAccount.setIsCommercial( customerDB.getCustomerTypeID().intValue() == custTypeCommID );
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
            siteAddr.setAddressID( addr.getAddressID().intValue() );
            siteAddr.setStreetAddr1( addr.getLocationAddress1() );
            siteAddr.setStreetAddr2( addr.getLocationAddress2() );
            siteAddr.setCity( addr.getCityName() );
            siteAddr.setState( addr.getStateCode() );
            siteAddr.setZip( addr.getZipCode() );
            starsAccount.setStreetAddress( siteAddr );

            com.cannontech.database.data.stars.customer.SiteInformation siteInfo = site.getSiteInformation();
            com.cannontech.database.db.stars.customer.SiteInformation siteInfoDB = siteInfo.getSiteInformation();
            com.cannontech.database.db.stars.Substation substation = siteInfo.getSubstation();

			com.cannontech.stars.xml.serialize.Substation starsSub = new com.cannontech.stars.xml.serialize.Substation();
			starsSub.setEntryID( substation.getSubstationID().intValue() );
			starsSub.setContent( substation.getSubstationName() );
			
            StarsSiteInformation starsSiteInfo = new StarsSiteInformation();
            starsSiteInfo.setSiteID( siteInfoDB.getSiteID().intValue() );
            starsSiteInfo.setSubstation( starsSub );
            starsSiteInfo.setFeeder( siteInfoDB.getFeeder() );
            starsSiteInfo.setPole( siteInfoDB.getPole() );
            starsSiteInfo.setTransformerSize( siteInfoDB.getTransformerSize() );
            starsSiteInfo.setServiceVoltage( siteInfoDB.getServiceVoltage() );
            starsAccount.setStarsSiteInformation( starsSiteInfo );

            addr = account.getBillingAddress();

            BillingAddress billAddr = new BillingAddress();
            billAddr.setAddressID( addr.getAddressID().intValue() );
            billAddr.setStreetAddr1( addr.getLocationAddress1() );
            billAddr.setStreetAddr2( addr.getLocationAddress2() );
            billAddr.setCity( addr.getCityName() );
            billAddr.setState( addr.getStateCode() );
            billAddr.setZip( addr.getZipCode() );
            starsAccount.setBillingAddress( billAddr );

            com.cannontech.database.db.customer.CustomerContact contact = customer.getPrimaryContact();

            PrimaryContact primContact = new PrimaryContact();
            primContact.setContactID( contact.getContactID().intValue() );
            primContact.setLastName( contact.getContLastName() );
            primContact.setFirstName( contact.getContFirstName() );
            primContact.setHomePhone( contact.getContPhone1() );
            primContact.setWorkPhone( contact.getContPhone2() );
            starsAccount.setPrimaryContact( primContact );

            Vector contactList = customer.getCustomerContactVector();

            for (int i = 0; i < contactList.size(); i++) {
                contact = (com.cannontech.database.db.customer.CustomerContact) contactList.elementAt(i);

                AdditionalContact addContact = new AdditionalContact();
	            addContact.setContactID( contact.getContactID().intValue() );
                addContact.setLastName( contact.getContLastName() );
                addContact.setFirstName( contact.getContFirstName() );
                addContact.setHomePhone( contact.getContPhone1() );
                addContact.setWorkPhone( contact.getContPhone2() );
                starsAccount.addAdditionalContact( addContact );
            }

            accountInfo.setStarsCustomerAccount( starsAccount );
            accountInfo.setStarsLMPrograms( new StarsLMPrograms() );

            StarsAppliances appliances = new StarsAppliances();
            Vector applianceVector = account.getApplianceVector();            
            StarsCustSelectionList appCatList = (StarsCustSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_APPLIANCECATEGORY );

            for (int i = 0; i < applianceVector.size(); i++) {
                com.cannontech.database.data.stars.appliance.ApplianceBase appliance =
                            (com.cannontech.database.data.stars.appliance.ApplianceBase) applianceVector.elementAt(i);
                com.cannontech.database.db.stars.appliance.ApplianceCategory category = appliance.getApplianceCategory();
                com.cannontech.database.db.stars.hardware.LMHardwareConfiguration config = appliance.getLMHardwareConfig();

                StarsAppliance starsApp = new StarsAppliance();
                starsApp.setApplianceID( appliance.getApplianceBase().getApplianceID().intValue() );
                starsApp.setApplianceCategoryID( category.getApplianceCategoryID().intValue() );
                if (config.getInventoryID() != null)
                    starsApp.setInventoryID( config.getInventoryID().intValue() );
                else
                    starsApp.setInventoryID( -1 );
                starsApp.setLmProgramID( appliance.getApplianceBase().getLMProgramID().intValue() );
                
                starsApp.setCategoryName( "" );
                for (int j = 0; j < appCatList.getStarsSelectionListEntryCount(); j++) {
                	StarsSelectionListEntry entry = appCatList.getStarsSelectionListEntry(j);
                	if (entry.getEntryID() == category.getCategoryID().intValue()) {
                		starsApp.setCategoryName( entry.getContent() );
                		break;
                	}
                }
                starsApp.setManufacturer( "" );
                starsApp.setManufactureYear( "" );
                starsApp.setLocation( "" );
                starsApp.setServiceCompany( new ServiceCompany() );
                starsApp.setNotes( "" );

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
                    starsHW.setReceiveDate( hardware.getInventoryBase().getReceiveDate() );
                    starsHW.setInstallDate( hardware.getInventoryBase().getInstallDate() );
                    starsHW.setRemoveDate( hardware.getInventoryBase().getRemoveDate() );
                    starsHW.setAltTrackingNumber( hardware.getInventoryBase().getAlternateTrackingNumber() );
                    starsHW.setNotes( hardware.getInventoryBase().getNotes() );
                    starsHW.setManufactureSerialNumber( hardware.getLMHardwareBase().getManufacturerSerialNumber() );
                    
                    InstallationCompany company = new InstallationCompany();
                    company.setEntryID( hardware.getInstallationCompany().getCompanyID().intValue() );
                    company.setContent( hardware.getInstallationCompany().getCompanyName() );
                    starsHW.setInstallationCompany( company );
                    
                    Voltage volt = new Voltage();
                    volt.setEntryID( hardware.getVoltage().getEntryID().intValue() );
                    volt.setContent( hardware.getVoltage().getEntryText() );
                    starsHW.setVoltage( volt );
                    
                    LMDeviceType devType = new LMDeviceType();
                    devType.setEntryID( hardware.getLMHardwareType().getEntryID().intValue() );
                    devType.setContent( hardware.getLMHardwareType().getEntryText() );
                    starsHW.setLMDeviceType( devType );
                    
                    starsHW.setStarsLMHardwareHistory( com.cannontech.database.data.stars.event.LMHardwareEvent.getStarsLMHardwareHistory(
                    			hardware.getInventoryBase().getInventoryID()) );
                    
                    // Set hardware status and installation notes based on hardware history
                    StarsCustListEntry entry = StarsCustListEntryFactory.getStarsCustListEntry(
                    		(LiteCustomerSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_DEVICESTATUS ),
                    		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_DEVSTAT_UNAVAIL );
                    DeviceStatus status = (DeviceStatus) StarsCustListEntryFactory.newStarsCustListEntry( entry, DeviceStatus.class );
                    starsHW.setInstallationNotes( "" );
                    
                    StarsLMHardwareHistory hwHist = starsHW.getStarsLMHardwareHistory();
                    if (hwHist != null) {
                    	for (int j = 0; j < hwHist.getStarsLMHardwareEventCount(); j++) {
	                    	StarsLMHardwareEvent event = hwHist.getStarsLMHardwareEvent(j);
    	                	if (event.getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_TERMINATION ))
    	                		break;
    	                	else if (event.getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_COMPLETED )) {
    	                		entry = StarsCustListEntryFactory.getStarsCustListEntry(
                    					(LiteCustomerSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_DEVICESTATUS ),
                    					com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_DEVSTAT_AVAIL );
                    			status = (DeviceStatus) StarsCustListEntryFactory.newStarsCustListEntry( entry, DeviceStatus.class );
                    			break;
    	                	}
    	                	else if (event.getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_TEMPTERMINATION )) {
    	                		entry = StarsCustListEntryFactory.getStarsCustListEntry(
                    					(LiteCustomerSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_DEVICESTATUS ),
                    					com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_DEVSTAT_TEMPUNAVAIL );
                    			status = (DeviceStatus) StarsCustListEntryFactory.newStarsCustListEntry( entry, DeviceStatus.class );
                    			break;
    	                	}
                    	}
                    	
                    	for (int j = hwHist.getStarsLMHardwareEventCount() - 1; j >= 0; j--) {
	                    	StarsLMHardwareEvent event = hwHist.getStarsLMHardwareEvent(j);
	                    	if (event.getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_INSTALL )) {
	                    		starsHW.setInstallationNotes( event.getNotes() );
	                    		break;
	                    	}
                    	}
                    }
                    
                    starsHW.setDeviceStatus( status );

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
                if (program.getPAObjectID().intValue() == 0) continue;

                StarsLMProgram starsProg = new StarsLMProgram();
                starsProg.setProgramID( program.getPAObjectID().intValue() );
                starsProg.setStatus( "In Service" );
                starsProg.setProgramName( program.getPAOName() );
                starsProg.setGroupID( appliance.getLMHardwareConfig().getAddressingGroupID().intValue() );

                StarsLMControlHistory ctrlHist = LMControlHistory.getStarsLMControlHistory(
                        appliance.getLMHardwareConfig().getAddressingGroupID(), com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod.PASTDAY, true);
                starsProg.setStarsLMControlHistory( ctrlHist );
                
                starsProg.setStarsLMProgramHistory( com.cannontech.database.data.stars.event.LMProgramEvent.getStarsLMProgramHistory(
                		account.getCustomerAccount().getAccountID(), program.getPAObjectID()) );

                progTable.put(program.getPAObjectID(), starsProg);
            }

            Iterator it = progTable.values().iterator();
            while (it.hasNext()) {
                StarsLMProgram starsProg = (StarsLMProgram) it.next();
                programs.addStarsLMProgram( starsProg );
            }

            accountInfo.setStarsLMPrograms( programs );
            return accountInfo;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}*/
}
