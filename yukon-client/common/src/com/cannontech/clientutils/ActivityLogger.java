package com.cannontech.clientutils;

import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.db.activity.ActivityLog;

/**
 * ActivityLogger provides a mechanism for applications to log interesting things that a yukon user might do along with
 * other possibly interesting information about the user. i.e. account id, energy company id.
 * It is intended to be useful to generate reports from.
 */
public class ActivityLogger {

    /**
     * The log method with every parameter (except the PaoID). 
     * To be used for accounts/customer type events, not for pao/device related things.
     */
    public static void logEvent(int userID, int accountID, int energyCompanyID, int customerID, String action, String description) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setUserID(userID);
        activityLog.setAccountID(accountID);
        activityLog.setEnergyCompanyID(energyCompanyID);
        activityLog.setCustomerID(customerID);
        activityLog.setAction(action);
        activityLog.setDescription(description);

        addActivityLog(activityLog);
    }

    /**
     * The log method used by LoginController when the login attempt failed.
     * To be used only for events where no userId is/can be known, ie login attempts.
     */
    public static void logEvent(String action, String description) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setAction(action);
        activityLog.setDescription(description);

        addActivityLog(activityLog);
    }

    /**
     * To be used for logging things about PaoIds; not for use with customers/accounts.
     */
    public static void logEvent(int userID, int paoID, String action, String description) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setUserID(userID);
        activityLog.setAction(action);
        activityLog.setPaoID(paoID);
        activityLog.setDescription(description);

        addActivityLog(activityLog);
    }

    /**
     * Method to be used for loging events (success).
     */
    public static void logEvent(int userID, String action, String description) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setUserID(userID);
        activityLog.setAction(action);
        activityLog.setDescription(description);

        addActivityLog(activityLog);
    }

    public static void logEvent(ActivityLog activityLog) {
        addActivityLog(activityLog);
    }
    
    private static void addActivityLog(ActivityLog activityLog) {
        try {
            Transaction.createTransaction(Transaction.INSERT, activityLog).execute();
        } catch (TransactionException e) {
            CTILogger.error("Failed to write action '" + activityLog.getAction() + "' to activity log", e);
        }
    }
}