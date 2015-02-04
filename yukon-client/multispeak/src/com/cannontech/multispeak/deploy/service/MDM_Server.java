/**
 * MDM_Server.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public interface MDM_Server extends javax.xml.rpc.Service {
    public java.lang.String getMDM_ServerSoapAddress();

    public com.cannontech.multispeak.deploy.service.MDM_ServerSoap_PortType getMDM_ServerSoap() throws javax.xml.rpc.ServiceException;

    public com.cannontech.multispeak.deploy.service.MDM_ServerSoap_PortType getMDM_ServerSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    public java.lang.String getMDM_ServerSoap12Address();

    public com.cannontech.multispeak.deploy.service.MDM_ServerSoap_PortType getMDM_ServerSoap12() throws javax.xml.rpc.ServiceException;

    public com.cannontech.multispeak.deploy.service.MDM_ServerSoap_PortType getMDM_ServerSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
