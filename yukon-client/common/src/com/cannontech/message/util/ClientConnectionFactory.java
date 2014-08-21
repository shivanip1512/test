package com.cannontech.message.util;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.message.dispatch.DispatchClientConnection;
import com.cannontech.message.porter.PorterClientConnection;
import com.cannontech.messaging.util.ConnectionFactoryService;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.conns.CapControlClientConnection;
import com.cannontech.yukon.conns.NotifClientConnection;
import com.cannontech.yukon.conns.ServerMACSConnection;

public class ClientConnectionFactory {

    @Autowired public ConnectionFactoryService connFactorySvc;

    /**
     * Creates a new Porter connection.
     */
    public PorterClientConnection createPorterConn() {
        PorterClientConnection porterCC = new PorterClientConnection();
        porterCC.setConnectionFactory(connFactorySvc.findConnectionFactory("Porter"));

        return porterCC;
    }

    /**
     * Creates a new Dispatch connection.
     */
    public DispatchClientConnection createDispatchConn() {
        DispatchClientConnection connToDispatch = new DispatchClientConnection();
        connToDispatch.setConnectionFactory(connFactorySvc.findConnectionFactory("Dispatch"));
        return connToDispatch;
    }

    /**
     * Creates a new MACS connection.
     */
    public ServerMACSConnection createMacsConn() {
        ServerMACSConnection macsConn = new ServerMACSConnection();
        macsConn.setConnectionFactory(connFactorySvc.findConnectionFactory("MACS"));
        return macsConn;
    }

    /**
     * Creates a new CapControl connection.
     */
    public CapControlClientConnection createCapControlConn() {
        CapControlClientConnection cbcConn = new CapControlClientConnection();
        cbcConn.setConnectionFactory(connFactorySvc.findConnectionFactory("CBC"));
        return cbcConn;
    }

    /**
     * Creates a new NotifServer connection.
     */
    public NotifClientConnection createNotificationConn() {
        NotifClientConnection notifConn = new NotifClientConnection();
        notifConn.setConnectionFactory(connFactorySvc.findConnectionFactory("Notification"));

        return notifConn;
    }

    public static ClientConnectionFactory getInstance() {
        return YukonSpringHook.getBean(ClientConnectionFactory.class);
    }
}
