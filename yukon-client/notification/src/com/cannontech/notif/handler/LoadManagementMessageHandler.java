package com.cannontech.notif.handler;

import java.util.Iterator;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.functions.NotificationGroupFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.device.lm.LMProgramDirect;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.db.device.lm.LMDirectNotificationGroupList;
import com.cannontech.message.util.Message;
import com.cannontech.notif.message.NotifLMControlMsg;
import com.cannontech.notif.outputs.Notification;
import com.cannontech.notif.outputs.OutputHandlerHelper;

/**
 * 
 */
public class LoadManagementMessageHandler extends NotifHandler {

    public LoadManagementMessageHandler(OutputHandlerHelper helper) {
        super(helper);
    }

    public boolean canHandle(Message msg) {
        return msg instanceof NotifLMControlMsg;
    }

    public void handleMessage(Message msg_) {
        NotifLMControlMsg msg = (NotifLMControlMsg) msg_;

        // building the Notification object is the main work of
        // this function
        Notification notif = new Notification("loadmanagement");

        int programID = msg.programId;

        LMProgramDirect programObject = 
            (LMProgramDirect) LiteFactory.createDBPersistent(PAOFuncs.getLiteYukonPAO(programID));
        try {
            Transaction t = Transaction.createTransaction(Transaction.RETRIEVE,
                                                          programObject);
            t.execute();

            List notifyGroupVector = programObject.getLmProgramDirectNotifyGroupVector();
            for (Iterator iter = notifyGroupVector.iterator(); iter.hasNext();) {
                LMDirectNotificationGroupList notificationGroup = 
                    (LMDirectNotificationGroupList) iter.next();
                
                int notificationGroupId = notificationGroup.getNotificationGroupID().intValue();
                LiteNotificationGroup liteNotificationGroup = 
                    NotificationGroupFuncs.getLiteNotificationGroup(notificationGroupId);

                outputNotification(notif, liteNotificationGroup);
            }
        } catch (TransactionException e) {
            CTILogger.error("Unable to handle message " + msg, e);
        }
    }

}
