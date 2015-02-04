/**
 * OA_MRLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class OA_MRLocator extends org.apache.axis.client.Service implements com.cannontech.multispeak.deploy.service.OA_MR {

    public OA_MRLocator() {
    }


    public OA_MRLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public OA_MRLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for OA_MRSoap
    private java.lang.String OA_MRSoap_address = "http://www.multispeak.org/interface/30q/27_OA_MR.asmx";

    public java.lang.String getOA_MRSoapAddress() {
        return OA_MRSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String OA_MRSoapWSDDServiceName = "OA_MRSoap";

    public java.lang.String getOA_MRSoapWSDDServiceName() {
        return OA_MRSoapWSDDServiceName;
    }

    public void setOA_MRSoapWSDDServiceName(java.lang.String name) {
        OA_MRSoapWSDDServiceName = name;
    }

    public com.cannontech.multispeak.deploy.service.OA_MRSoap_PortType getOA_MRSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(OA_MRSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getOA_MRSoap(endpoint);
    }

    public com.cannontech.multispeak.deploy.service.OA_MRSoap_PortType getOA_MRSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.cannontech.multispeak.deploy.service.OA_MRSoap_BindingStub _stub = new com.cannontech.multispeak.deploy.service.OA_MRSoap_BindingStub(portAddress, this);
            _stub.setPortName(getOA_MRSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setOA_MRSoapEndpointAddress(java.lang.String address) {
        OA_MRSoap_address = address;
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
            if (com.cannontech.multispeak.deploy.service.OA_MRSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.cannontech.multispeak.deploy.service.OA_MRSoap_BindingStub _stub = new com.cannontech.multispeak.deploy.service.OA_MRSoap_BindingStub(new java.net.URL(OA_MRSoap_address), this);
                _stub.setPortName(getOA_MRSoapWSDDServiceName());
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
        if ("OA_MRSoap".equals(inputPortName)) {
            return getOA_MRSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "OA_MR");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "OA_MRSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("OA_MRSoap".equals(portName)) {
            setOA_MRSoapEndpointAddress(address);
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
