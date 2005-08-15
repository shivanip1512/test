/**
 * PSS2WSLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.custom.pss2ws;

public class PSS2WSLocator extends org.apache.axis.client.Service implements com.cannontech.custom.pss2ws.PSS2WS {

    public PSS2WSLocator() {
    }


    public PSS2WSLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public PSS2WSLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for PSS2WSSEIPort
    private java.lang.String PSS2WSSEIPort_address = "http://www.electricprice.net/PSS2WS/PSS2WS";

    public java.lang.String getPSS2WSSEIPortAddress() {
        return PSS2WSSEIPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String PSS2WSSEIPortWSDDServiceName = "PSS2WSSEIPort";

    public java.lang.String getPSS2WSSEIPortWSDDServiceName() {
        return PSS2WSSEIPortWSDDServiceName;
    }

    public void setPSS2WSSEIPortWSDDServiceName(java.lang.String name) {
        PSS2WSSEIPortWSDDServiceName = name;
    }

    public com.cannontech.custom.pss2ws.PSS2WSSEI getPSS2WSSEIPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(PSS2WSSEIPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getPSS2WSSEIPort(endpoint);
    }

    public com.cannontech.custom.pss2ws.PSS2WSSEI getPSS2WSSEIPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.cannontech.custom.pss2ws.PSS2WSSEIBindingStub _stub = new com.cannontech.custom.pss2ws.PSS2WSSEIBindingStub(portAddress, this);
            _stub.setPortName(getPSS2WSSEIPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setPSS2WSSEIPortEndpointAddress(java.lang.String address) {
        PSS2WSSEIPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.cannontech.custom.pss2ws.PSS2WSSEI.class.isAssignableFrom(serviceEndpointInterface)) {
                com.cannontech.custom.pss2ws.PSS2WSSEIBindingStub _stub = new com.cannontech.custom.pss2ws.PSS2WSSEIBindingStub(new java.net.URL(PSS2WSSEIPort_address), this);
                _stub.setPortName(getPSS2WSSEIPortWSDDServiceName());
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
        if ("PSS2WSSEIPort".equals(inputPortName)) {
            return getPSS2WSSEIPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://pss2.lbl.gov", "PSS2WS");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://pss2.lbl.gov", "PSS2WSSEIPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        if ("PSS2WSSEIPort".equals(portName)) {
            setPSS2WSSEIPortEndpointAddress(address);
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
