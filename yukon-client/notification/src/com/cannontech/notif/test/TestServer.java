package com.cannontech.notif.test;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.notif.outputs.VoiceHandler;
import com.cannontech.notif.server.NotificationServer;

public class TestServer {

    /**
     * @param args
     */
    public static void main(String[] args) {
/*        VoiceHandler voiceHandler = new VoiceHandler();
        voiceHandler.startup();
        
        CTILogger.info("Yea!!!");
        
        voiceHandler.shutdown();

        CTILogger.info("Yea!!!");*/
        
        NotificationServer serv = new NotificationServer();
        try {
            serv.start();
        } catch (Exception e) {
            CTILogger.debug(e);
        }
    }

}
