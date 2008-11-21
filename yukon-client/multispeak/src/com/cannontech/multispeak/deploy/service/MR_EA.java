/**
 * MR_EA.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public interface MR_EA extends javax.xml.rpc.Service {
    public java.lang.String getMR_EASoapAddress();

    public com.cannontech.multispeak.deploy.service.MR_EASoap_PortType getMR_EASoap() throws javax.xml.rpc.ServiceException;

    public com.cannontech.multispeak.deploy.service.MR_EASoap_PortType getMR_EASoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
