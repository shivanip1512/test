/**
 * OA_SCADALocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class OA_SCADALocator extends org.apache.axis.client.Service implements com.cannontech.multispeak.deploy.service.OA_SCADA {

    public OA_SCADALocator() {
    }


    public OA_SCADALocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public OA_SCADALocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for OA_SCADASoap
    private java.lang.String OA_SCADASoap_address = "http://localhost/MultiSpeakWebServiceV30u/9_OA_SCADA.asmx";

    public java.lang.String getOA_SCADASoapAddress() {
        return OA_SCADASoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String OA_SCADASoapWSDDServiceName = "OA_SCADASoap";

    public java.lang.String getOA_SCADASoapWSDDServiceName() {
        return OA_SCADASoapWSDDServiceName;
    }

    public void setOA_SCADASoapWSDDServiceName(java.lang.String name) {
        OA_SCADASoapWSDDServiceName = name;
    }

    public com.cannontech.multispeak.deploy.service.OA_SCADASoap_PortType getOA_SCADASoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(OA_SCADASoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getOA_SCADASoap(endpoint);
    }

    public com.cannontech.multispeak.deploy.service.OA_SCADASoap_PortType getOA_SCADASoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.cannontech.multispeak.deploy.service.OA_SCADASoap_BindingStub _stub = new com.cannontech.multispeak.deploy.service.OA_SCADASoap_BindingStub(portAddress, this);
            _stub.setPortName(getOA_SCADASoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setOA_SCADASoapEndpointAddress(java.lang.String address) {
        OA_SCADASoap_address = address;
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
            if (com.cannontech.multispeak.deploy.service.OA_SCADASoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.cannontech.multispeak.deploy.service.OA_SCADASoap_BindingStub _stub = new com.cannontech.multispeak.deploy.service.OA_SCADASoap_BindingStub(new java.net.URL(OA_SCADASoap_address), this);
                _stub.setPortName(getOA_SCADASoapWSDDServiceName());
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
        if ("OA_SCADASoap".equals(inputPortName)) {
            return getOA_SCADASoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "OA_SCADA");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "OA_SCADASoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("OA_SCADASoap".equals(portName)) {
            setOA_SCADASoapEndpointAddress(address);
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
