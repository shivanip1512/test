/**
 * EA_ServerLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class EA_ServerLocator extends org.apache.axis.client.Service implements com.cannontech.multispeak.deploy.service.EA_Server {

    public EA_ServerLocator() {
    }


    public EA_ServerLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public EA_ServerLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for EA_ServerSoap
    private java.lang.String EA_ServerSoap_address = "http://localhost:55273/EA_Server.asmx";

    public java.lang.String getEA_ServerSoapAddress() {
        return EA_ServerSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String EA_ServerSoapWSDDServiceName = "EA_ServerSoap";

    public java.lang.String getEA_ServerSoapWSDDServiceName() {
        return EA_ServerSoapWSDDServiceName;
    }

    public void setEA_ServerSoapWSDDServiceName(java.lang.String name) {
        EA_ServerSoapWSDDServiceName = name;
    }

    public com.cannontech.multispeak.deploy.service.EA_ServerSoap_PortType getEA_ServerSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(EA_ServerSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getEA_ServerSoap(endpoint);
    }

    public com.cannontech.multispeak.deploy.service.EA_ServerSoap_PortType getEA_ServerSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.cannontech.multispeak.deploy.service.EA_ServerSoap_BindingStub _stub = new com.cannontech.multispeak.deploy.service.EA_ServerSoap_BindingStub(portAddress, this);
            _stub.setPortName(getEA_ServerSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setEA_ServerSoapEndpointAddress(java.lang.String address) {
        EA_ServerSoap_address = address;
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
            if (com.cannontech.multispeak.deploy.service.EA_ServerSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.cannontech.multispeak.deploy.service.EA_ServerSoap_BindingStub _stub = new com.cannontech.multispeak.deploy.service.EA_ServerSoap_BindingStub(new java.net.URL(EA_ServerSoap_address), this);
                _stub.setPortName(getEA_ServerSoapWSDDServiceName());
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
        if ("EA_ServerSoap".equals(inputPortName)) {
            return getEA_ServerSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "EA_Server");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "EA_ServerSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("EA_ServerSoap".equals(portName)) {
            setEA_ServerSoapEndpointAddress(address);
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
