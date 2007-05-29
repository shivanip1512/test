/**
 * MR_OALocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class MR_OALocator extends org.apache.axis.client.Service implements com.cannontech.multispeak.service.MR_OA {

    public MR_OALocator() {
    }


    public MR_OALocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public MR_OALocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for MR_OASoap12
    private java.lang.String MR_OASoap12_address = "http://www.multispeak.org/interface/30n/27_MR_OA.asmx";

    public java.lang.String getMR_OASoap12Address() {
        return MR_OASoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String MR_OASoap12WSDDServiceName = "MR_OASoap12";

    public java.lang.String getMR_OASoap12WSDDServiceName() {
        return MR_OASoap12WSDDServiceName;
    }

    public void setMR_OASoap12WSDDServiceName(java.lang.String name) {
        MR_OASoap12WSDDServiceName = name;
    }

    public com.cannontech.multispeak.service.MR_OASoap_PortType getMR_OASoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MR_OASoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMR_OASoap12(endpoint);
    }

    public com.cannontech.multispeak.service.MR_OASoap_PortType getMR_OASoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.cannontech.multispeak.service.MR_OASoap12Stub _stub = new com.cannontech.multispeak.service.MR_OASoap12Stub(portAddress, this);
            _stub.setPortName(getMR_OASoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMR_OASoap12EndpointAddress(java.lang.String address) {
        MR_OASoap12_address = address;
    }


    // Use to get a proxy class for MR_OASoap
    private java.lang.String MR_OASoap_address = "http://www.multispeak.org/interface/30n/27_MR_OA.asmx";

    public java.lang.String getMR_OASoapAddress() {
        return MR_OASoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String MR_OASoapWSDDServiceName = "MR_OASoap";

    public java.lang.String getMR_OASoapWSDDServiceName() {
        return MR_OASoapWSDDServiceName;
    }

    public void setMR_OASoapWSDDServiceName(java.lang.String name) {
        MR_OASoapWSDDServiceName = name;
    }

    public com.cannontech.multispeak.service.MR_OASoap_PortType getMR_OASoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MR_OASoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMR_OASoap(endpoint);
    }

    public com.cannontech.multispeak.service.MR_OASoap_PortType getMR_OASoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.cannontech.multispeak.service.MR_OASoap_BindingStub _stub = new com.cannontech.multispeak.service.MR_OASoap_BindingStub(portAddress, this);
            _stub.setPortName(getMR_OASoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMR_OASoapEndpointAddress(java.lang.String address) {
        MR_OASoap_address = address;
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
            if (com.cannontech.multispeak.service.MR_OASoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.cannontech.multispeak.service.MR_OASoap12Stub _stub = new com.cannontech.multispeak.service.MR_OASoap12Stub(new java.net.URL(MR_OASoap12_address), this);
                _stub.setPortName(getMR_OASoap12WSDDServiceName());
                return _stub;
            }
            if (com.cannontech.multispeak.service.MR_OASoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.cannontech.multispeak.service.MR_OASoap_BindingStub _stub = new com.cannontech.multispeak.service.MR_OASoap_BindingStub(new java.net.URL(MR_OASoap_address), this);
                _stub.setPortName(getMR_OASoapWSDDServiceName());
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
        if ("MR_OASoap12".equals(inputPortName)) {
            return getMR_OASoap12();
        }
        else if ("MR_OASoap".equals(inputPortName)) {
            return getMR_OASoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MR_OA");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MR_OASoap12"));
            ports.add(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MR_OASoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        if ("MR_OASoap12".equals(portName)) {
            setMR_OASoap12EndpointAddress(address);
        }
        if ("MR_OASoap".equals(portName)) {
            setMR_OASoapEndpointAddress(address);
        }
        else { // Unknown Port Name
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
