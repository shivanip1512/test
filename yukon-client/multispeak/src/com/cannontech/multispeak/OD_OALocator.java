/**
 * OD_OALocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class OD_OALocator extends org.apache.axis.client.Service implements com.cannontech.multispeak.OD_OA {

    public OD_OALocator() {
    }


    public OD_OALocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public OD_OALocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for OD_OASoap
    private java.lang.String OD_OASoap_address = "http://www.turtletech.com/multispeakws/5_OD_OA.asmx";

    public java.lang.String getOD_OASoapAddress() {
        return OD_OASoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String OD_OASoapWSDDServiceName = "OD_OASoap";

    public java.lang.String getOD_OASoapWSDDServiceName() {
        return OD_OASoapWSDDServiceName;
    }

    public void setOD_OASoapWSDDServiceName(java.lang.String name) {
        OD_OASoapWSDDServiceName = name;
    }

    public com.cannontech.multispeak.OD_OASoap_PortType getOD_OASoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(OD_OASoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getOD_OASoap(endpoint);
    }

    public com.cannontech.multispeak.OD_OASoap_PortType getOD_OASoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.cannontech.multispeak.OD_OASoap_BindingStub _stub = new com.cannontech.multispeak.OD_OASoap_BindingStub(portAddress, this);
            _stub.setPortName(getOD_OASoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setOD_OASoapEndpointAddress(java.lang.String address) {
        OD_OASoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.cannontech.multispeak.OD_OASoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.cannontech.multispeak.OD_OASoap_BindingStub _stub = new com.cannontech.multispeak.OD_OASoap_BindingStub(new java.net.URL(OD_OASoap_address), this);
                _stub.setPortName(getOD_OASoapWSDDServiceName());
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
        if ("OD_OASoap".equals(inputPortName)) {
            return getOD_OASoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "OD_OA");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "OD_OASoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        if ("OD_OASoap".equals(portName)) {
            setOD_OASoapEndpointAddress(address);
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
