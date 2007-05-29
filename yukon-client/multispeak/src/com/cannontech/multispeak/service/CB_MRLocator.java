/**
 * CB_MRLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class CB_MRLocator extends org.apache.axis.client.Service implements com.cannontech.multispeak.service.CB_MR {

    public CB_MRLocator() {
    }


    public CB_MRLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public CB_MRLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for CB_MRSoap
    private java.lang.String CB_MRSoap_address = "http://www.multispeak.org/interface/30n/2A_CB_MR.asmx";

    public java.lang.String getCB_MRSoapAddress() {
        return CB_MRSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String CB_MRSoapWSDDServiceName = "CB_MRSoap";

    public java.lang.String getCB_MRSoapWSDDServiceName() {
        return CB_MRSoapWSDDServiceName;
    }

    public void setCB_MRSoapWSDDServiceName(java.lang.String name) {
        CB_MRSoapWSDDServiceName = name;
    }

    public com.cannontech.multispeak.service.CB_MRSoap_PortType getCB_MRSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(CB_MRSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getCB_MRSoap(endpoint);
    }

    public com.cannontech.multispeak.service.CB_MRSoap_PortType getCB_MRSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.cannontech.multispeak.service.CB_MRSoap_BindingStub _stub = new com.cannontech.multispeak.service.CB_MRSoap_BindingStub(portAddress, this);
            _stub.setPortName(getCB_MRSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setCB_MRSoapEndpointAddress(java.lang.String address) {
        CB_MRSoap_address = address;
    }


    // Use to get a proxy class for CB_MRSoap12
    private java.lang.String CB_MRSoap12_address = "http://www.multispeak.org/interface/30n/2A_CB_MR.asmx";

    public java.lang.String getCB_MRSoap12Address() {
        return CB_MRSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String CB_MRSoap12WSDDServiceName = "CB_MRSoap12";

    public java.lang.String getCB_MRSoap12WSDDServiceName() {
        return CB_MRSoap12WSDDServiceName;
    }

    public void setCB_MRSoap12WSDDServiceName(java.lang.String name) {
        CB_MRSoap12WSDDServiceName = name;
    }

    public com.cannontech.multispeak.service.CB_MRSoap_PortType getCB_MRSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(CB_MRSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getCB_MRSoap12(endpoint);
    }

    public com.cannontech.multispeak.service.CB_MRSoap_PortType getCB_MRSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.cannontech.multispeak.service.CB_MRSoap12Stub _stub = new com.cannontech.multispeak.service.CB_MRSoap12Stub(portAddress, this);
            _stub.setPortName(getCB_MRSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setCB_MRSoap12EndpointAddress(java.lang.String address) {
        CB_MRSoap12_address = address;
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
            if (com.cannontech.multispeak.service.CB_MRSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.cannontech.multispeak.service.CB_MRSoap_BindingStub _stub = new com.cannontech.multispeak.service.CB_MRSoap_BindingStub(new java.net.URL(CB_MRSoap_address), this);
                _stub.setPortName(getCB_MRSoapWSDDServiceName());
                return _stub;
            }
            if (com.cannontech.multispeak.service.CB_MRSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.cannontech.multispeak.service.CB_MRSoap12Stub _stub = new com.cannontech.multispeak.service.CB_MRSoap12Stub(new java.net.URL(CB_MRSoap12_address), this);
                _stub.setPortName(getCB_MRSoap12WSDDServiceName());
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
        if ("CB_MRSoap".equals(inputPortName)) {
            return getCB_MRSoap();
        }
        else if ("CB_MRSoap12".equals(inputPortName)) {
            return getCB_MRSoap12();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CB_MR");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CB_MRSoap"));
            ports.add(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CB_MRSoap12"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        if ("CB_MRSoap".equals(portName)) {
            setCB_MRSoapEndpointAddress(address);
        }
        if ("CB_MRSoap12".equals(portName)) {
            setCB_MRSoap12EndpointAddress(address);
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
