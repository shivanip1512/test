/**
 * CB_CDLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class CB_CDLocator extends org.apache.axis.client.Service implements com.cannontech.multispeak.service.CB_CD {

    public CB_CDLocator() {
    }


    public CB_CDLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public CB_CDLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for CB_CDSoap12
    private java.lang.String CB_CDSoap12_address = "http://www.multispeak.org/interface/30n/2B_CB_CD.asmx";

    public java.lang.String getCB_CDSoap12Address() {
        return CB_CDSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String CB_CDSoap12WSDDServiceName = "CB_CDSoap12";

    public java.lang.String getCB_CDSoap12WSDDServiceName() {
        return CB_CDSoap12WSDDServiceName;
    }

    public void setCB_CDSoap12WSDDServiceName(java.lang.String name) {
        CB_CDSoap12WSDDServiceName = name;
    }

    public com.cannontech.multispeak.service.CB_CDSoap_PortType getCB_CDSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(CB_CDSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getCB_CDSoap12(endpoint);
    }

    public com.cannontech.multispeak.service.CB_CDSoap_PortType getCB_CDSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.cannontech.multispeak.service.CB_CDSoap12Stub _stub = new com.cannontech.multispeak.service.CB_CDSoap12Stub(portAddress, this);
            _stub.setPortName(getCB_CDSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setCB_CDSoap12EndpointAddress(java.lang.String address) {
        CB_CDSoap12_address = address;
    }


    // Use to get a proxy class for CB_CDSoap
    private java.lang.String CB_CDSoap_address = "http://www.multispeak.org/interface/30n/2B_CB_CD.asmx";

    public java.lang.String getCB_CDSoapAddress() {
        return CB_CDSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String CB_CDSoapWSDDServiceName = "CB_CDSoap";

    public java.lang.String getCB_CDSoapWSDDServiceName() {
        return CB_CDSoapWSDDServiceName;
    }

    public void setCB_CDSoapWSDDServiceName(java.lang.String name) {
        CB_CDSoapWSDDServiceName = name;
    }

    public com.cannontech.multispeak.service.CB_CDSoap_PortType getCB_CDSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(CB_CDSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getCB_CDSoap(endpoint);
    }

    public com.cannontech.multispeak.service.CB_CDSoap_PortType getCB_CDSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.cannontech.multispeak.service.CB_CDSoap_BindingStub _stub = new com.cannontech.multispeak.service.CB_CDSoap_BindingStub(portAddress, this);
            _stub.setPortName(getCB_CDSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setCB_CDSoapEndpointAddress(java.lang.String address) {
        CB_CDSoap_address = address;
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
            if (com.cannontech.multispeak.service.CB_CDSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.cannontech.multispeak.service.CB_CDSoap12Stub _stub = new com.cannontech.multispeak.service.CB_CDSoap12Stub(new java.net.URL(CB_CDSoap12_address), this);
                _stub.setPortName(getCB_CDSoap12WSDDServiceName());
                return _stub;
            }
            if (com.cannontech.multispeak.service.CB_CDSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.cannontech.multispeak.service.CB_CDSoap_BindingStub _stub = new com.cannontech.multispeak.service.CB_CDSoap_BindingStub(new java.net.URL(CB_CDSoap_address), this);
                _stub.setPortName(getCB_CDSoapWSDDServiceName());
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
        if ("CB_CDSoap12".equals(inputPortName)) {
            return getCB_CDSoap12();
        }
        else if ("CB_CDSoap".equals(inputPortName)) {
            return getCB_CDSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CB_CD");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CB_CDSoap12"));
            ports.add(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CB_CDSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        if ("CB_CDSoap12".equals(portName)) {
            setCB_CDSoap12EndpointAddress(address);
        }
        if ("CB_CDSoap".equals(portName)) {
            setCB_CDSoapEndpointAddress(address);
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
