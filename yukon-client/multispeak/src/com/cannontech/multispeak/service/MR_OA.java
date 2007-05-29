/**
 * MR_OA.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public interface MR_OA extends javax.xml.rpc.Service {
    public java.lang.String getMR_OASoap12Address();

    public com.cannontech.multispeak.service.MR_OASoap_PortType getMR_OASoap12() throws javax.xml.rpc.ServiceException;

    public com.cannontech.multispeak.service.MR_OASoap_PortType getMR_OASoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    public java.lang.String getMR_OASoapAddress();

    public com.cannontech.multispeak.service.MR_OASoap_PortType getMR_OASoap() throws javax.xml.rpc.ServiceException;

    public com.cannontech.multispeak.service.MR_OASoap_PortType getMR_OASoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
