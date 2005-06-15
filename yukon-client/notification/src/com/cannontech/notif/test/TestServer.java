package com.cannontech.notif.test;

import java.text.DateFormat;
import java.util.TimeZone;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.message.notif.NotifLMControlMsg;
import com.cannontech.notif.server.NotificationServer;

public class TestServer {

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        NotificationServer serv = new NotificationServer();
        try {
            serv.start();
            
            NotifLMControlMsg msg = new NotifLMControlMsg();
            msg.notifGroupIds = new int[] {3};
            msg.notifType = NotifLMControlMsg.STARTING_CONTROL_NOTIFICATION;
            msg.programId = 10;
            DateFormat dateTimeInstance = DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT);
            dateTimeInstance.setTimeZone(TimeZone.getTimeZone("US/Central"));
            msg.startTime = dateTimeInstance.parse("6/1/05 11:45 am");
            msg.stopTime = dateTimeInstance.parse("6/2/05 1:05 am");
            int times = 3;
            while (times-- > 0) {
                serv.testInjectMessage(msg);
                Thread.sleep(3000);
            }
            serv.stop();

            
        } catch (Exception e) {
            CTILogger.error("Unknown error", e);
        }
    }

}
