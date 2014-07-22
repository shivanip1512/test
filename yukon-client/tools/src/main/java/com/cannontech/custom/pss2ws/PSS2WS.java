/**
 * PSS2WS.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.custom.pss2ws;

public interface PSS2WS extends javax.xml.rpc.Service {
    public java.lang.String getPSS2WSSEIPortAddress();

    public com.cannontech.custom.pss2ws.PSS2WSSEI getPSS2WSSEIPort() throws javax.xml.rpc.ServiceException;

    public com.cannontech.custom.pss2ws.PSS2WSSEI getPSS2WSSEIPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
