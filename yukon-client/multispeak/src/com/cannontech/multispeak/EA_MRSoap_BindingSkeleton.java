/**
 * EA_MRSoap_BindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class EA_MRSoap_BindingSkeleton implements com.cannontech.multispeak.EA_MRSoap_PortType, org.apache.axis.wsdl.Skeleton {
    private com.cannontech.multispeak.EA_MRSoap_PortType impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("pingURL", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "PingURLResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "PingURL"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/PingURL");
        _myOperationsList.add(_oper);
        if (_myOperations.get("pingURL") == null) {
            _myOperations.put("pingURL", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("pingURL")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("getMethods", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMethodsResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetMethods"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetMethods");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getMethods") == null) {
            _myOperations.put("getMethods", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getMethods")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("getDomainNames", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetDomainNamesResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetDomainNames"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetDomainNames");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getDomainNames") == null) {
            _myOperations.put("getDomainNames", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getDomainNames")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "domainName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getDomainMembers", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetDomainMembersResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfDomainMember"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetDomainMembers"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetDomainMembers");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getDomainMembers") == null) {
            _myOperations.put("getDomainMembers", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getDomainMembers")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("getSubstationNames", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetSubstationNamesResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetSubstationNames"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetSubstationNames");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getSubstationNames") == null) {
            _myOperations.put("getSubstationNames", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getSubstationNames")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eaLoc"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getDownlineCircuitElements", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetDownlineCircuitElementsResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCircuitElement"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetDownlineCircuitElements"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetDownlineCircuitElements");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getDownlineCircuitElements") == null) {
            _myOperations.put("getDownlineCircuitElements", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getDownlineCircuitElements")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eaLoc"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getUplineCircuitElements", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetUplineCircuitElementsResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCircuitElement"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetUplineCircuitElements"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetUplineCircuitElements");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getUplineCircuitElements") == null) {
            _myOperations.put("getUplineCircuitElements", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getUplineCircuitElements")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eaLoc"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getChildCircuitElements", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetChildCircuitElementsResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCircuitElement"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetChildCircuitElements"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetChildCircuitElements");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getChildCircuitElements") == null) {
            _myOperations.put("getChildCircuitElements", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getChildCircuitElements")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eaLoc"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getParentCircuitElements", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetParentCircuitElementsResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCircuitElement"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetParentCircuitElements"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetParentCircuitElements");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getParentCircuitElements") == null) {
            _myOperations.put("getParentCircuitElements", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getParentCircuitElements")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getAllCircuitElements", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllCircuitElementsResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCircuitElement"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllCircuitElements"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetAllCircuitElements");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getAllCircuitElements") == null) {
            _myOperations.put("getAllCircuitElements", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getAllCircuitElements")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "previousSessionID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getModifiedCircuitElements", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedCircuitElementsResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCircuitElement"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedCircuitElements"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetModifiedCircuitElements");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getModifiedCircuitElements") == null) {
            _myOperations.put("getModifiedCircuitElements", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getModifiedCircuitElements")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getDownlineMeterConnectivity", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetDownlineMeterConnectivityResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterConnectivity"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetDownlineMeterConnectivity"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetDownlineMeterConnectivity");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getDownlineMeterConnectivity") == null) {
            _myOperations.put("getDownlineMeterConnectivity", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getDownlineMeterConnectivity")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getUplineMeterConnectivity", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetUplineMeterConnectivityResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterConnectivity"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetUplineMeterConnectivity"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetUplineMeterConnectivity");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getUplineMeterConnectivity") == null) {
            _myOperations.put("getUplineMeterConnectivity", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getUplineMeterConnectivity")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getSiblingMeterConnectivity", _params, new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetSiblingMeterConnectivityResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterConnectivity"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetSiblingMeterConnectivity"));
        _oper.setSoapAction("http://www.multispeak.org/Version_3.0/GetSiblingMeterConnectivity");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getSiblingMeterConnectivity") == null) {
            _myOperations.put("getSiblingMeterConnectivity", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getSiblingMeterConnectivity")).add(_oper);
    }

    public EA_MRSoap_BindingSkeleton() {
        this.impl = new com.cannontech.multispeak.EA_MRSoap_BindingImpl();
    }

    public EA_MRSoap_BindingSkeleton(com.cannontech.multispeak.EA_MRSoap_PortType impl) {
        this.impl = impl;
    }
    public com.cannontech.multispeak.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfErrorObject ret = impl.pingURL();
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfString getMethods() throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfString ret = impl.getMethods();
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfString getDomainNames() throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfString ret = impl.getDomainNames();
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfDomainMember getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfDomainMember ret = impl.getDomainMembers(domainName);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfString getSubstationNames() throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfString ret = impl.getSubstationNames();
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfCircuitElement getDownlineCircuitElements(java.lang.String eaLoc, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfCircuitElement ret = impl.getDownlineCircuitElements(eaLoc, lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfCircuitElement getUplineCircuitElements(java.lang.String eaLoc) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfCircuitElement ret = impl.getUplineCircuitElements(eaLoc);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfCircuitElement getChildCircuitElements(java.lang.String eaLoc) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfCircuitElement ret = impl.getChildCircuitElements(eaLoc);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfCircuitElement getParentCircuitElements(java.lang.String eaLoc) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfCircuitElement ret = impl.getParentCircuitElements(eaLoc);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfCircuitElement getAllCircuitElements(java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfCircuitElement ret = impl.getAllCircuitElements(lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfCircuitElement getModifiedCircuitElements(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfCircuitElement ret = impl.getModifiedCircuitElements(previousSessionID, lastReceived);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfMeterConnectivity getDownlineMeterConnectivity(java.lang.String meterNo) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfMeterConnectivity ret = impl.getDownlineMeterConnectivity(meterNo);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfMeterConnectivity getUplineMeterConnectivity(java.lang.String meterNo) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfMeterConnectivity ret = impl.getUplineMeterConnectivity(meterNo);
        return ret;
    }

    public com.cannontech.multispeak.ArrayOfMeterConnectivity getSiblingMeterConnectivity(java.lang.String meterNo) throws java.rmi.RemoteException
    {
        com.cannontech.multispeak.ArrayOfMeterConnectivity ret = impl.getSiblingMeterConnectivity(meterNo);
        return ret;
    }

}
