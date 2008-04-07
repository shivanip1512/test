/**
 * EA_MRLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class EA_MRLocator extends org.apache.axis.client.Service implements com.cannontech.multispeak.deploy.service.EA_MR {

    public EA_MRLocator() {
    }


    public EA_MRLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public EA_MRLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for EA_MRSoap12
    private java.lang.String EA_MRSoap12_address = "http://www.multispeak.org/interface/30q/4_EA_MR.asmx";

    public java.lang.String getEA_MRSoap12Address() {
        return EA_MRSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String EA_MRSoap12WSDDServiceName = "EA_MRSoap12";

    public java.lang.String getEA_MRSoap12WSDDServiceName() {
        return EA_MRSoap12WSDDServiceName;
    }

    public void setEA_MRSoap12WSDDServiceName(java.lang.String name) {
        EA_MRSoap12WSDDServiceName = name;
    }

    public com.cannontech.multispeak.deploy.service.EA_MRSoap_PortType getEA_MRSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(EA_MRSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getEA_MRSoap12(endpoint);
    }

    public com.cannontech.multispeak.deploy.service.EA_MRSoap_PortType getEA_MRSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.cannontech.multispeak.deploy.service.EA_MRSoap12Stub _stub = new com.cannontech.multispeak.deploy.service.EA_MRSoap12Stub(portAddress, this);
            _stub.setPortName(getEA_MRSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setEA_MRSoap12EndpointAddress(java.lang.String address) {
        EA_MRSoap12_address = address;
    }


    // Use to get a proxy class for EA_MRSoap
    private java.lang.String EA_MRSoap_address = "http://www.multispeak.org/interface/30q/4_EA_MR.asmx";

    public java.lang.String getEA_MRSoapAddress() {
        return EA_MRSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String EA_MRSoapWSDDServiceName = "EA_MRSoap";

    public java.lang.String getEA_MRSoapWSDDServiceName() {
        return EA_MRSoapWSDDServiceName;
    }

    public void setEA_MRSoapWSDDServiceName(java.lang.String name) {
        EA_MRSoapWSDDServiceName = name;
    }

    public com.cannontech.multispeak.deploy.service.EA_MRSoap_PortType getEA_MRSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(EA_MRSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getEA_MRSoap(endpoint);
    }

    public com.cannontech.multispeak.deploy.service.EA_MRSoap_PortType getEA_MRSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.cannontech.multispeak.deploy.service.EA_MRSoap_BindingStub _stub = new com.cannontech.multispeak.deploy.service.EA_MRSoap_BindingStub(portAddress, this);
            _stub.setPortName(getEA_MRSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setEA_MRSoapEndpointAddress(java.lang.String address) {
        EA_MRSoap_address = address;
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
            if (com.cannontech.multispeak.deploy.service.EA_MRSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.cannontech.multispeak.deploy.service.EA_MRSoap12Stub _stub = new com.cannontech.multispeak.deploy.service.EA_MRSoap12Stub(new java.net.URL(EA_MRSoap12_address), this);
                _stub.setPortName(getEA_MRSoap12WSDDServiceName());
                return _stub;
            }
            if (com.cannontech.multispeak.deploy.service.EA_MRSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.cannontech.multispeak.deploy.service.EA_MRSoap_BindingStub _stub = new com.cannontech.multispeak.deploy.service.EA_MRSoap_BindingStub(new java.net.URL(EA_MRSoap_address), this);
                _stub.setPortName(getEA_MRSoapWSDDServiceName());
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
        if ("EA_MRSoap12".equals(inputPortName)) {
            return getEA_MRSoap12();
        }
        else if ("EA_MRSoap".equals(inputPortName)) {
            return getEA_MRSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "EA_MR");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "EA_MRSoap12"));
            ports.add(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "EA_MRSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("EA_MRSoap12".equals(portName)) {
            setEA_MRSoap12EndpointAddress(address);
        }
        else 
if ("EA_MRSoap".equals(portName)) {
            setEA_MRSoapEndpointAddress(address);
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
