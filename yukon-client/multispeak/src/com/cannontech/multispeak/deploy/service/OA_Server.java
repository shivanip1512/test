/**
 * OA_Server.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public interface OA_Server extends javax.xml.rpc.Service {
    public java.lang.String getOA_ServerSoapAddress();

    public com.cannontech.multispeak.deploy.service.OA_ServerSoap_PortType getOA_ServerSoap() throws javax.xml.rpc.ServiceException;

    public com.cannontech.multispeak.deploy.service.OA_ServerSoap_PortType getOA_ServerSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
