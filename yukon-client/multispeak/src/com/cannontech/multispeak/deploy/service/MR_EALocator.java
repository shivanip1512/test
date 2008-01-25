/**
 * MR_EALocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class MR_EALocator extends org.apache.axis.client.Service implements com.cannontech.multispeak.deploy.service.MR_EA {

    public MR_EALocator() {
    }


    public MR_EALocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public MR_EALocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for MR_EASoap
    private java.lang.String MR_EASoap_address = "http://www.multispeak.org/interface/30p/4_MR_EA.asmx";

    public java.lang.String getMR_EASoapAddress() {
        return MR_EASoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String MR_EASoapWSDDServiceName = "MR_EASoap";

    public java.lang.String getMR_EASoapWSDDServiceName() {
        return MR_EASoapWSDDServiceName;
    }

    public void setMR_EASoapWSDDServiceName(java.lang.String name) {
        MR_EASoapWSDDServiceName = name;
    }

    public com.cannontech.multispeak.deploy.service.MR_EASoap_PortType getMR_EASoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MR_EASoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMR_EASoap(endpoint);
    }

    public com.cannontech.multispeak.deploy.service.MR_EASoap_PortType getMR_EASoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.cannontech.multispeak.deploy.service.MR_EASoap_BindingStub _stub = new com.cannontech.multispeak.deploy.service.MR_EASoap_BindingStub(portAddress, this);
            _stub.setPortName(getMR_EASoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMR_EASoapEndpointAddress(java.lang.String address) {
        MR_EASoap_address = address;
    }


    // Use to get a proxy class for MR_EASoap12
    private java.lang.String MR_EASoap12_address = "http://www.multispeak.org/interface/30p/4_MR_EA.asmx";

    public java.lang.String getMR_EASoap12Address() {
        return MR_EASoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String MR_EASoap12WSDDServiceName = "MR_EASoap12";

    public java.lang.String getMR_EASoap12WSDDServiceName() {
        return MR_EASoap12WSDDServiceName;
    }

    public void setMR_EASoap12WSDDServiceName(java.lang.String name) {
        MR_EASoap12WSDDServiceName = name;
    }

    public com.cannontech.multispeak.deploy.service.MR_EASoap_PortType getMR_EASoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MR_EASoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMR_EASoap12(endpoint);
    }

    public com.cannontech.multispeak.deploy.service.MR_EASoap_PortType getMR_EASoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.cannontech.multispeak.deploy.service.MR_EASoap12Stub _stub = new com.cannontech.multispeak.deploy.service.MR_EASoap12Stub(portAddress, this);
            _stub.setPortName(getMR_EASoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMR_EASoap12EndpointAddress(java.lang.String address) {
        MR_EASoap12_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     * This service has multiple ports for a given interface;
     * the proxy implementation returned may be indeterminate.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.cannontech.multispeak.deploy.service.MR_EASoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.cannontech.multispeak.deploy.service.MR_EASoap_BindingStub _stub = new com.cannontech.multispeak.deploy.service.MR_EASoap_BindingStub(new java.net.URL(MR_EASoap_address), this);
                _stub.setPortName(getMR_EASoapWSDDServiceName());
                return _stub;
            }
            if (com.cannontech.multispeak.deploy.service.MR_EASoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.cannontech.multispeak.deploy.service.MR_EASoap12Stub _stub = new com.cannontech.multispeak.deploy.service.MR_EASoap12Stub(new java.net.URL(MR_EASoap12_address), this);
                _stub.setPortName(getMR_EASoap12WSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("MR_EASoap".equals(inputPortName)) {
            return getMR_EASoap();
        }
        else if ("MR_EASoap12".equals(inputPortName)) {
            return getMR_EASoap12();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MR_EA");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MR_EASoap"));
            ports.add(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MR_EASoap12"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("MR_EASoap".equals(portName)) {
            setMR_EASoapEndpointAddress(address);
        }
        else 
if ("MR_EASoap12".equals(portName)) {
            setMR_EASoap12EndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
