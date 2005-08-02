/**
 * OA_ODLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class OA_ODLocator extends org.apache.axis.client.Service implements com.cannontech.multispeak.OA_OD {

    public OA_ODLocator() {
    }


    public OA_ODLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public OA_ODLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for OA_ODSoap
    private java.lang.String OA_ODSoap_address = "http://www.turtletech.com/multispeakws/5_OA_OD.asmx";

    public java.lang.String getOA_ODSoapAddress() {
        return OA_ODSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String OA_ODSoapWSDDServiceName = "OA_ODSoap";

    public java.lang.String getOA_ODSoapWSDDServiceName() {
        return OA_ODSoapWSDDServiceName;
    }

    public void setOA_ODSoapWSDDServiceName(java.lang.String name) {
        OA_ODSoapWSDDServiceName = name;
    }

    public com.cannontech.multispeak.OA_ODSoap_PortType getOA_ODSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(OA_ODSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getOA_ODSoap(endpoint);
    }

    public com.cannontech.multispeak.OA_ODSoap_PortType getOA_ODSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.cannontech.multispeak.OA_ODSoap_BindingStub _stub = new com.cannontech.multispeak.OA_ODSoap_BindingStub(portAddress, this);
            _stub.setPortName(getOA_ODSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setOA_ODSoapEndpointAddress(java.lang.String address) {
        OA_ODSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.cannontech.multispeak.OA_ODSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.cannontech.multispeak.OA_ODSoap_BindingStub _stub = new com.cannontech.multispeak.OA_ODSoap_BindingStub(new java.net.URL(OA_ODSoap_address), this);
                _stub.setPortName(getOA_ODSoapWSDDServiceName());
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
        if ("OA_ODSoap".equals(inputPortName)) {
            return getOA_ODSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "OA_OD");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "OA_ODSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        if ("OA_ODSoap".equals(portName)) {
            setOA_ODSoapEndpointAddress(address);
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
