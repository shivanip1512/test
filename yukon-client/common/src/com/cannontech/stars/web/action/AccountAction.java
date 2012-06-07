package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.database.data.lite.LiteStarsCustAccountInformation;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.StarsLiteFactory;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.xml.serialize.StarsDeleteLMHardware;
import com.cannontech.user.UserUtils;

public class AccountAction {

    /* from deleteCustAccountAction */
    public static void deleteCustomerAccount(LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany)
    throws TransactionException, WebClientException
    {
        ApplianceDao applianceDao = YukonSpringHook.getBean("applianceDao", ApplianceDao.class);
        
        
         /* Remove all the inventory from the account and move it to the warehouse.  This
          * also includes any unenrolling that is needed.
          */
        List<Integer> inventories = new ArrayList<Integer>(); 
        inventories.addAll(liteAcctInfo.getInventories());
        for (int inventoryId : inventories) {
            
            StarsDeleteLMHardware deleteHw = new StarsDeleteLMHardware();
            deleteHw.setInventoryID(inventoryId);
            deleteHw.setRemoveDate(new Date());
            
            HardwareAction.removeInventory(deleteHw, liteAcctInfo, energyCompany);
        }
        
        applianceDao.deleteAppliancesByAccountId(liteAcctInfo.getAccountID());
    
        com.cannontech.stars.database.data.customer.CustomerAccount account = 
                StarsLiteFactory.createCustomerAccount(liteAcctInfo, energyCompany);
        Transaction.createTransaction( Transaction.DELETE, account ).execute();
        
        // Delete contacts from database
        LiteContact primContact = DaoFactory.getContactDao().getContact( liteAcctInfo.getCustomer().getPrimaryContactID() );
        com.cannontech.database.data.customer.Contact contact =
                (com.cannontech.database.data.customer.Contact) StarsLiteFactory.createDBPersistent( primContact );
        Transaction.createTransaction( Transaction.DELETE, contact ).execute();
        ServerUtils.handleDBChange( primContact, DbChangeType.DELETE );
        
        Vector<LiteContact> contacts = liteAcctInfo.getCustomer().getAdditionalContacts();
        for (int i = 0; i < contacts.size(); i++) {
            LiteContact liteContact = contacts.get(i);
            contact = (com.cannontech.database.data.customer.Contact) StarsLiteFactory.createDBPersistent( liteContact );
            Transaction.createTransaction( Transaction.DELETE, contact ).execute();
            ServerUtils.handleDBChange( liteContact, DbChangeType.DELETE );
            int userId = liteContact.getLoginID();
            if (userId != UserUtils.USER_DEFAULT_ID &&
                    userId != UserUtils.USER_ADMIN_ID &&
                    userId != UserUtils.USER_YUKON_ID)
                    LoginAction.deleteLogin( userId );
        }
        
        // Delete login
        int userID = primContact.getLoginID();
        if (userID != UserUtils.USER_DEFAULT_ID && userID != UserUtils.USER_ADMIN_ID && userID != UserUtils.USER_YUKON_ID) {
            LoginAction.deleteLogin( userID );
        }
        
        // Delete lite and stars objects
        energyCompany.deleteCustAccountInformation( liteAcctInfo );
        ServerUtils.handleDBChange( liteAcctInfo, DbChangeType.DELETE );
    }

    
    
}
