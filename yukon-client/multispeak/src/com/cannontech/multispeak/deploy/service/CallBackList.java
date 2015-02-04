/**
 * CallBackList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class CallBackList  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.lang.String outageEventID;

    private com.cannontech.multispeak.deploy.service.OutageStatus outageStatus;

    private java.lang.String outageDescription;

    private com.cannontech.multispeak.deploy.service.OutageCustomer[] outageCustomerList;

    private com.cannontech.multispeak.deploy.service.Message message;

    private java.lang.String callBackListType;

    public CallBackList() {
    }

    public CallBackList(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String outageEventID,
           com.cannontech.multispeak.deploy.service.OutageStatus outageStatus,
           java.lang.String outageDescription,
           com.cannontech.multispeak.deploy.service.OutageCustomer[] outageCustomerList,
           com.cannontech.multispeak.deploy.service.Message message,
           java.lang.String callBackListType) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.outageEventID = outageEventID;
        this.outageStatus = outageStatus;
        this.outageDescription = outageDescription;
        this.outageCustomerList = outageCustomerList;
        this.message = message;
        this.callBackListType = callBackListType;
    }


    /**
     * Gets the outageEventID value for this CallBackList.
     * 
     * @return outageEventID
     */
    public java.lang.String getOutageEventID() {
        return outageEventID;
    }


    /**
     * Sets the outageEventID value for this CallBackList.
     * 
     * @param outageEventID
     */
    public void setOutageEventID(java.lang.String outageEventID) {
        this.outageEventID = outageEventID;
    }


    /**
     * Gets the outageStatus value for this CallBackList.
     * 
     * @return outageStatus
     */
    public com.cannontech.multispeak.deploy.service.OutageStatus getOutageStatus() {
        return outageStatus;
    }


    /**
     * Sets the outageStatus value for this CallBackList.
     * 
     * @param outageStatus
     */
    public void setOutageStatus(com.cannontech.multispeak.deploy.service.OutageStatus outageStatus) {
        this.outageStatus = outageStatus;
    }


    /**
     * Gets the outageDescription value for this CallBackList.
     * 
     * @return outageDescription
     */
    public java.lang.String getOutageDescription() {
        return outageDescription;
    }


    /**
     * Sets the outageDescription value for this CallBackList.
     * 
     * @param outageDescription
     */
    public void setOutageDescription(java.lang.String outageDescription) {
        this.outageDescription = outageDescription;
    }


    /**
     * Gets the outageCustomerList value for this CallBackList.
     * 
     * @return outageCustomerList
     */
    public com.cannontech.multispeak.deploy.service.OutageCustomer[] getOutageCustomerList() {
        return outageCustomerList;
    }


    /**
     * Sets the outageCustomerList value for this CallBackList.
     * 
     * @param outageCustomerList
     */
    public void setOutageCustomerList(com.cannontech.multispeak.deploy.service.OutageCustomer[] outageCustomerList) {
        this.outageCustomerList = outageCustomerList;
    }


    /**
     * Gets the message value for this CallBackList.
     * 
     * @return message
     */
    public com.cannontech.multispeak.deploy.service.Message getMessage() {
        return message;
    }


    /**
     * Sets the message value for this CallBackList.
     * 
     * @param message
     */
    public void setMessage(com.cannontech.multispeak.deploy.service.Message message) {
        this.message = message;
    }


    /**
     * Gets the callBackListType value for this CallBackList.
     * 
     * @return callBackListType
     */
    public java.lang.String getCallBackListType() {
        return callBackListType;
    }


    /**
     * Sets the callBackListType value for this CallBackList.
     * 
     * @param callBackListType
     */
    public void setCallBackListType(java.lang.String callBackListType) {
        this.callBackListType = callBackListType;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CallBackList)) return false;
        CallBackList other = (CallBackList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.outageEventID==null && other.getOutageEventID()==null) || 
             (this.outageEventID!=null &&
              this.outageEventID.equals(other.getOutageEventID()))) &&
            ((this.outageStatus==null && other.getOutageStatus()==null) || 
             (this.outageStatus!=null &&
              this.outageStatus.equals(other.getOutageStatus()))) &&
            ((this.outageDescription==null && other.getOutageDescription()==null) || 
             (this.outageDescription!=null &&
              this.outageDescription.equals(other.getOutageDescription()))) &&
            ((this.outageCustomerList==null && other.getOutageCustomerList()==null) || 
             (this.outageCustomerList!=null &&
              java.util.Arrays.equals(this.outageCustomerList, other.getOutageCustomerList()))) &&
            ((this.message==null && other.getMessage()==null) || 
             (this.message!=null &&
              this.message.equals(other.getMessage()))) &&
            ((this.callBackListType==null && other.getCallBackListType()==null) || 
             (this.callBackListType!=null &&
              this.callBackListType.equals(other.getCallBackListType())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getOutageEventID() != null) {
            _hashCode += getOutageEventID().hashCode();
        }
        if (getOutageStatus() != null) {
            _hashCode += getOutageStatus().hashCode();
        }
        if (getOutageDescription() != null) {
            _hashCode += getOutageDescription().hashCode();
        }
        if (getOutageCustomerList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getOutageCustomerList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getOutageCustomerList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getMessage() != null) {
            _hashCode += getMessage().hashCode();
        }
        if (getCallBackListType() != null) {
            _hashCode += getCallBackListType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CallBackList.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callBackList"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageEventID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageEventID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageStatus"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageCustomerList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageCustomerList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageCustomer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageCustomer"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "message"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("callBackListType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callBackListType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
