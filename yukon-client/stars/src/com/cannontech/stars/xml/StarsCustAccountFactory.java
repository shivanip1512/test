package com.cannontech.stars.xml;

import com.cannontech.stars.xml.serialize.StarsCustAccount;
import com.cannontech.stars.xml.serialize.StarsSiteInformation;
import com.cannontech.database.db.stars.customer.CustomerAccount;
import com.cannontech.database.db.stars.customer.AccountSite;
import com.cannontech.database.db.stars.customer.SiteInformation;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class StarsCustAccountFactory {

    public StarsCustAccountFactory() {
    }

    public static StarsCustAccount newStarsCustAccount(StarsCustAccount account, Class type) {
        try {
            StarsCustAccount newAccount = (StarsCustAccount) type.newInstance();

            newAccount.setAccountNumber( account.getAccountNumber() );
            newAccount.setIsCommercial( account.getIsCommercial() );
            newAccount.setCompany( account.getCompany() );
            newAccount.setAccountNotes( account.getAccountNotes() );
            newAccount.setPropertyNumber( account.getPropertyNumber() );
            newAccount.setPropertyNotes( account.getPropertyNotes() );
            newAccount.setStreetAddress( account.getStreetAddress() );
            newAccount.setStarsSiteInformation( account.getStarsSiteInformation() );
            newAccount.setBillingAddress( account.getBillingAddress() );
            newAccount.setPrimaryContact( account.getPrimaryContact() );
            newAccount.setAdditionalContact( account.getAdditionalContact() );

            return newAccount;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    public static void setCustomerAccount(CustomerAccount account, StarsCustAccount starsAccount) {
    	account.setAccountNumber( starsAccount.getAccountNumber() );
    	account.setAccountNotes( starsAccount.getAccountNotes() );
    }
    
    public static void setAccountSite(AccountSite acctSite, StarsCustAccount starsAccount) {
    	acctSite.setSiteNumber( starsAccount.getPropertyNumber() );
    	acctSite.setPropertyNotes( starsAccount.getPropertyNotes() );
    }
    
    public static void setSiteInformation(SiteInformation siteInfo, StarsCustAccount starsAccount) {
    	StarsSiteInformation starsSiteInfo = starsAccount.getStarsSiteInformation();
    	
    	siteInfo.setSubstationID( new Integer(starsSiteInfo.getSubstation().getEntryID()) );
    	siteInfo.setFeeder( starsSiteInfo.getFeeder() );
    	siteInfo.setPole( starsSiteInfo.getPole() );
    	siteInfo.setTransformerSize( starsSiteInfo.getTransformerSize() );
    	siteInfo.setServiceVoltage( starsSiteInfo.getServiceVoltage() );
    }
}