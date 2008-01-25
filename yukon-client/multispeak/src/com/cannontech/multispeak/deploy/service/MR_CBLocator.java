/**
 * MR_CBLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class MR_CBLocator extends org.apache.axis.client.Service implements com.cannontech.multispeak.deploy.service.MR_CB {

    public MR_CBLocator() {
    }


    public MR_CBLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public MR_CBLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for MR_CBSoap
    private java.lang.String MR_CBSoap_address = "http://www.multispeak.org/interface/30p/2A_MR_CB.asmx";

    public java.lang.String getMR_CBSoapAddress() {
        return MR_CBSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String MR_CBSoapWSDDServiceName = "MR_CBSoap";

    public java.lang.String getMR_CBSoapWSDDServiceName() {
        return MR_CBSoapWSDDServiceName;
    }

    public void setMR_CBSoapWSDDServiceName(java.lang.String name) {
        MR_CBSoapWSDDServiceName = name;
    }

    public com.cannontech.multispeak.deploy.service.MR_CBSoap_PortType getMR_CBSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MR_CBSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMR_CBSoap(endpoint);
    }

    public com.cannontech.multispeak.deploy.service.MR_CBSoap_PortType getMR_CBSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.cannontech.multispeak.deploy.service.MR_CBSoap_BindingStub _stub = new com.cannontech.multispeak.deploy.service.MR_CBSoap_BindingStub(portAddress, this);
            _stub.setPortName(getMR_CBSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMR_CBSoapEndpointAddress(java.lang.String address) {
        MR_CBSoap_address = address;
    }


    // Use to get a proxy class for MR_CBSoap12
    private java.lang.String MR_CBSoap12_address = "http://www.multispeak.org/interface/30p/2A_MR_CB.asmx";

    public java.lang.String getMR_CBSoap12Address() {
        return MR_CBSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String MR_CBSoap12WSDDServiceName = "MR_CBSoap12";

    public java.lang.String getMR_CBSoap12WSDDServiceName() {
        return MR_CBSoap12WSDDServiceName;
    }

    public void setMR_CBSoap12WSDDServiceName(java.lang.String name) {
        MR_CBSoap12WSDDServiceName = name;
    }

    public com.cannontech.multispeak.deploy.service.MR_CBSoap_PortType getMR_CBSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MR_CBSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMR_CBSoap12(endpoint);
    }

    public com.cannontech.multispeak.deploy.service.MR_CBSoap_PortType getMR_CBSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.cannontech.multispeak.deploy.service.MR_CBSoap12Stub _stub = new com.cannontech.multispeak.deploy.service.MR_CBSoap12Stub(portAddress, this);
            _stub.setPortName(getMR_CBSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMR_CBSoap12EndpointAddress(java.lang.String address) {
        MR_CBSoap12_address = address;
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
            if (com.cannontech.multispeak.deploy.service.MR_CBSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.cannontech.multispeak.deploy.service.MR_CBSoap_BindingStub _stub = new com.cannontech.multispeak.deploy.service.MR_CBSoap_BindingStub(new java.net.URL(MR_CBSoap_address), this);
                _stub.setPortName(getMR_CBSoapWSDDServiceName());
                return _stub;
            }
            if (com.cannontech.multispeak.deploy.service.MR_CBSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.cannontech.multispeak.deploy.service.MR_CBSoap12Stub _stub = new com.cannontech.multispeak.deploy.service.MR_CBSoap12Stub(new java.net.URL(MR_CBSoap12_address), this);
                _stub.setPortName(getMR_CBSoap12WSDDServiceName());
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
        if ("MR_CBSoap".equals(inputPortName)) {
            return getMR_CBSoap();
        }
        else if ("MR_CBSoap12".equals(inputPortName)) {
            return getMR_CBSoap12();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MR_CB");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MR_CBSoap"));
            ports.add(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MR_CBSoap12"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("MR_CBSoap".equals(portName)) {
            setMR_CBSoapEndpointAddress(address);
        }
        else 
if ("MR_CBSoap12".equals(portName)) {
            setMR_CBSoap12EndpointAddress(address);
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
