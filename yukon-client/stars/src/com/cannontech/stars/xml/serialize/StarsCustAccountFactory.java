package com.cannontech.stars.xml.serialize;

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
}