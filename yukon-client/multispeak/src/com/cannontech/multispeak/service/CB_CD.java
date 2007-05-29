/**
 * CB_CD.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public interface CB_CD extends javax.xml.rpc.Service {
    public java.lang.String getCB_CDSoap12Address();

    public com.cannontech.multispeak.service.CB_CDSoap_PortType getCB_CDSoap12() throws javax.xml.rpc.ServiceException;

    public com.cannontech.multispeak.service.CB_CDSoap_PortType getCB_CDSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    public java.lang.String getCB_CDSoapAddress();

    public com.cannontech.multispeak.service.CB_CDSoap_PortType getCB_CDSoap() throws javax.xml.rpc.ServiceException;

    public com.cannontech.multispeak.service.CB_CDSoap_PortType getCB_CDSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
