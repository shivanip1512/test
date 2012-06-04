/**
 * MDM_ServerLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class MDM_ServerLocator extends org.apache.axis.client.Service implements com.cannontech.multispeak.deploy.service.MDM_Server {

    public MDM_ServerLocator() {
    }


    public MDM_ServerLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public MDM_ServerLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for MDM_ServerSoap
    private java.lang.String MDM_ServerSoap_address = "http://localhost:55273/MDM_Server.asmx";

    public java.lang.String getMDM_ServerSoapAddress() {
        return MDM_ServerSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String MDM_ServerSoapWSDDServiceName = "MDM_ServerSoap";

    public java.lang.String getMDM_ServerSoapWSDDServiceName() {
        return MDM_ServerSoapWSDDServiceName;
    }

    public void setMDM_ServerSoapWSDDServiceName(java.lang.String name) {
        MDM_ServerSoapWSDDServiceName = name;
    }

    public com.cannontech.multispeak.deploy.service.MDM_ServerSoap_PortType getMDM_ServerSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MDM_ServerSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMDM_ServerSoap(endpoint);
    }

    public com.cannontech.multispeak.deploy.service.MDM_ServerSoap_PortType getMDM_ServerSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.cannontech.multispeak.deploy.service.MDM_ServerSoap_BindingStub _stub = new com.cannontech.multispeak.deploy.service.MDM_ServerSoap_BindingStub(portAddress, this);
            _stub.setPortName(getMDM_ServerSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMDM_ServerSoapEndpointAddress(java.lang.String address) {
        MDM_ServerSoap_address = address;
    }


    // Use to get a proxy class for MDM_ServerSoap12
    private java.lang.String MDM_ServerSoap12_address = "http://localhost:55273/MDM_Server.asmx";

    public java.lang.String getMDM_ServerSoap12Address() {
        return MDM_ServerSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String MDM_ServerSoap12WSDDServiceName = "MDM_ServerSoap12";

    public java.lang.String getMDM_ServerSoap12WSDDServiceName() {
        return MDM_ServerSoap12WSDDServiceName;
    }

    public void setMDM_ServerSoap12WSDDServiceName(java.lang.String name) {
        MDM_ServerSoap12WSDDServiceName = name;
    }

    public com.cannontech.multispeak.deploy.service.MDM_ServerSoap_PortType getMDM_ServerSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MDM_ServerSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMDM_ServerSoap12(endpoint);
    }

    public com.cannontech.multispeak.deploy.service.MDM_ServerSoap_PortType getMDM_ServerSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.cannontech.multispeak.deploy.service.MDM_ServerSoap12Stub _stub = new com.cannontech.multispeak.deploy.service.MDM_ServerSoap12Stub(portAddress, this);
            _stub.setPortName(getMDM_ServerSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMDM_ServerSoap12EndpointAddress(java.lang.String address) {
        MDM_ServerSoap12_address = address;
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
            if (com.cannontech.multispeak.deploy.service.MDM_ServerSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.cannontech.multispeak.deploy.service.MDM_ServerSoap_BindingStub _stub = new com.cannontech.multispeak.deploy.service.MDM_ServerSoap_BindingStub(new java.net.URL(MDM_ServerSoap_address), this);
                _stub.setPortName(getMDM_ServerSoapWSDDServiceName());
                return _stub;
            }
            if (com.cannontech.multispeak.deploy.service.MDM_ServerSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.cannontech.multispeak.deploy.service.MDM_ServerSoap12Stub _stub = new com.cannontech.multispeak.deploy.service.MDM_ServerSoap12Stub(new java.net.URL(MDM_ServerSoap12_address), this);
                _stub.setPortName(getMDM_ServerSoap12WSDDServiceName());
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
        if ("MDM_ServerSoap".equals(inputPortName)) {
            return getMDM_ServerSoap();
        }
        else if ("MDM_ServerSoap12".equals(inputPortName)) {
            return getMDM_ServerSoap12();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MDM_Server");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MDM_ServerSoap"));
            ports.add(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "MDM_ServerSoap12"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("MDM_ServerSoap".equals(portName)) {
            setMDM_ServerSoapEndpointAddress(address);
        }
        else 
if ("MDM_ServerSoap12".equals(portName)) {
            setMDM_ServerSoap12EndpointAddress(address);
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
