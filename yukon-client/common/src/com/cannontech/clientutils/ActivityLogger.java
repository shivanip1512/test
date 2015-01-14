package com.cannontech.clientutils;

import java.util.Vector;

import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.activity.ActivityLog;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;

/**
 * ActivityLogger provides a mechanism for applications to log interesting things that a yukon user might do along with
 * other possibly interesting information about the user. i.e. account id, energy company id.
 * It is intended to be useful to generate reports from.
 */
public class ActivityLogger {

    /**
     * The log method with every parameter (except the PaoID). If you don't care
     * about some of them, set their values to -1.
     */
    public static void logEvent(int userID, int accountID, int energyCompanyID,
            int customerID, String action, String description) {
        logEvent(userID, accountID, energyCompanyID, customerID, -1, action, description);
    }

    /**
     * The log method used by LoginController when the login attempt failed
     */
    public static void logEvent(String action, String description) {
        logEvent(-1, -1, -1, -1, -1, action, description);
    }

    public static void logEvent(int userID, int paoID, String action,
            String description) {
        logEvent(userID, -1, -1, -1, paoID, action, description);
    }

    /**
     * The log method used by LoginController. The rest of the fields will be filled as much as we can.
     */
    public static void logEvent(int userID, String action, String description) {
        logEvent(userID, -1, -1, -1, -1, action, description);
    }

    public static void logEvent(ActivityLog event) {
        logEvent(event.getUserID(),
                 event.getAccountID(),
                 event.getEnergyCompanyID(),
                 event.getCustomerID(),
                 event.getPaoID(),
                 event.getAction(),
                 event.getDescription());
    }

    /**
     * logEvent with all possible parameters. Set any parameters that aren't
     * relevant to -1 and attempts will be made to fill them automagically.
     * @param userID
     * @param accountID
     * @param energyCompanyID
     * @param customerID
     * @param paoID
     * @param action
     * @param description
     */
    public static void logEvent(int userID, int accountID, int energyCompanyID,
            int customerID, int paoID, String action, String description) {
        if (userID != -1) {
            if (energyCompanyID == -1) {
                LiteYukonUser liteUser = YukonSpringHook.getBean(YukonUserDao.class)
                                                        .getLiteYukonUser(userID);
                if (liteUser != null) {
                    EnergyCompany liteComp = YukonSpringHook.getBean(EnergyCompanyDao.class)
                                                            .getEnergyCompany(liteUser);
                    if (liteComp != null) {
                        energyCompanyID = liteComp.getId();
                    }
                }
            }

            if (customerID == -1 || energyCompanyID == -1 || accountID == -1) {
                LiteContact liteContact = YukonSpringHook.getBean(YukonUserDao.class)
                                                         .getLiteContact(userID);
                if (liteContact != null) {
                    LiteCustomer liteCust = YukonSpringHook.getBean(ContactDao.class)
                                                           .getCustomer(liteContact.getContactID());

                    if (liteCust != null) {
                        if (customerID == -1) {
                            customerID = liteCust.getCustomerID();
                        }

                        Vector<Integer> acctIDs = liteCust.getAccountIDs();
                        if (acctIDs.size() > 0) {
                            // This customer has residential information
                            if (energyCompanyID == -1) {
                                energyCompanyID = liteCust.getEnergyCompanyID();
                            }
                            if (accountID == -1) {
                                accountID = acctIDs.get(0).intValue();
                            }
                        }
                    }
                }
            }
        }

        ActivityLog actLog = new ActivityLog();
        actLog.setUserID(userID);
        actLog.setAccountID(accountID);
        actLog.setEnergyCompanyID(energyCompanyID);
        actLog.setCustomerID(customerID);
        actLog.setAction(action);
        actLog.setPaoID(paoID);
        actLog.setDescription(description);

        try {
            Transaction.createTransaction(Transaction.INSERT, actLog).execute();
        } catch (TransactionException e) {
            CTILogger.error("Failed to write action '" + action + "' to activity log",
                            e);
        }
    }
}
