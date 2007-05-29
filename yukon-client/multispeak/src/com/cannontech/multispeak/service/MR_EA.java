/**
 * MR_EA.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public interface MR_EA extends javax.xml.rpc.Service {
    public java.lang.String getMR_EASoapAddress();

    public com.cannontech.multispeak.service.MR_EASoap_PortType getMR_EASoap() throws javax.xml.rpc.ServiceException;

    public com.cannontech.multispeak.service.MR_EASoap_PortType getMR_EASoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    public java.lang.String getMR_EASoap12Address();

    public com.cannontech.multispeak.service.MR_EASoap_PortType getMR_EASoap12() throws javax.xml.rpc.ServiceException;

    public com.cannontech.multispeak.service.MR_EASoap_PortType getMR_EASoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
