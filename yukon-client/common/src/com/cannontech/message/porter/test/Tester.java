package com.cannontech.message.porter.test;

import com.cannontech.message.util.ClientConnectionFactory;

public class Tester {
    public static void main(String[] args) {
        int deviceID = 1;
        String command = "GetStatus";
        com.cannontech.message.porter.PorterClientConnection conn =
            ClientConnectionFactory.getInstance().createPorterConn();
        conn.connect();
        com.cannontech.message.porter.message.Request req =
            new com.cannontech.message.porter.message.Request(deviceID, command, 1);
        conn.write(req);
        Object o = conn.read();
        if (o instanceof com.cannontech.message.porter.message.Return) {
            com.cannontech.message.porter.message.Return r = (com.cannontech.message.porter.message.Return) o;
            com.cannontech.clientutils.CTILogger.info(r);
        }
    }
}
