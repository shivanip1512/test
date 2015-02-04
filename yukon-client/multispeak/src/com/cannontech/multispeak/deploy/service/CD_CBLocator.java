/**
 * CD_CBLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class CD_CBLocator extends org.apache.axis.client.Service implements com.cannontech.multispeak.deploy.service.CD_CB {

    public CD_CBLocator() {
    }


    public CD_CBLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public CD_CBLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for CD_CBSoap
    private java.lang.String CD_CBSoap_address = "http://localhost/MultiSpeakWebServicesV30v/2B_CD_CB.asmx";

    public java.lang.String getCD_CBSoapAddress() {
        return CD_CBSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String CD_CBSoapWSDDServiceName = "CD_CBSoap";

    public java.lang.String getCD_CBSoapWSDDServiceName() {
        return CD_CBSoapWSDDServiceName;
    }

    public void setCD_CBSoapWSDDServiceName(java.lang.String name) {
        CD_CBSoapWSDDServiceName = name;
    }

    public com.cannontech.multispeak.deploy.service.CD_CBSoap_PortType getCD_CBSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(CD_CBSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getCD_CBSoap(endpoint);
    }

    public com.cannontech.multispeak.deploy.service.CD_CBSoap_PortType getCD_CBSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.cannontech.multispeak.deploy.service.CD_CBSoap_BindingStub _stub = new com.cannontech.multispeak.deploy.service.CD_CBSoap_BindingStub(portAddress, this);
            _stub.setPortName(getCD_CBSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setCD_CBSoapEndpointAddress(java.lang.String address) {
        CD_CBSoap_address = address;
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
            if (com.cannontech.multispeak.deploy.service.CD_CBSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.cannontech.multispeak.deploy.service.CD_CBSoap_BindingStub _stub = new com.cannontech.multispeak.deploy.service.CD_CBSoap_BindingStub(new java.net.URL(CD_CBSoap_address), this);
                _stub.setPortName(getCD_CBSoapWSDDServiceName());
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
        if ("CD_CBSoap".equals(inputPortName)) {
            return getCD_CBSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CD_CB");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CD_CBSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("CD_CBSoap".equals(portName)) {
            setCD_CBSoapEndpointAddress(address);
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
