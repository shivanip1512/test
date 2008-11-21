/**
 * LM_SCADA.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public interface LM_SCADA extends javax.xml.rpc.Service {
    public java.lang.String getLM_SCADASoapAddress();

    public com.cannontech.multispeak.deploy.service.LM_SCADASoap_PortType getLM_SCADASoap() throws javax.xml.rpc.ServiceException;

    public com.cannontech.multispeak.deploy.service.LM_SCADASoap_PortType getLM_SCADASoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
