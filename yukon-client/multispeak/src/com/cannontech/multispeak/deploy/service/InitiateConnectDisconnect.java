/**
 * InitiateConnectDisconnect.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class InitiateConnectDisconnect  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent[] cdEvents;

    private java.lang.String responseURL;

    private java.lang.String transactionID;

    private float expirationTime;

    public InitiateConnectDisconnect() {
    }

    public InitiateConnectDisconnect(
           com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent[] cdEvents,
           java.lang.String responseURL,
           java.lang.String transactionID,
           float expirationTime) {
           this.cdEvents = cdEvents;
           this.responseURL = responseURL;
           this.transactionID = transactionID;
           this.expirationTime = expirationTime;
    }


    /**
     * Gets the cdEvents value for this InitiateConnectDisconnect.
     * 
     * @return cdEvents
     */
    public com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent[] getCdEvents() {
        return cdEvents;
    }


    /**
     * Sets the cdEvents value for this InitiateConnectDisconnect.
     * 
     * @param cdEvents
     */
    public void setCdEvents(com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent[] cdEvents) {
        this.cdEvents = cdEvents;
    }


    /**
     * Gets the responseURL value for this InitiateConnectDisconnect.
     * 
     * @return responseURL
     */
    public java.lang.String getResponseURL() {
        return responseURL;
    }


    /**
     * Sets the responseURL value for this InitiateConnectDisconnect.
     * 
     * @param responseURL
     */
    public void setResponseURL(java.lang.String responseURL) {
        this.responseURL = responseURL;
    }


    /**
     * Gets the transactionID value for this InitiateConnectDisconnect.
     * 
     * @return transactionID
     */
    public java.lang.String getTransactionID() {
        return transactionID;
    }


    /**
     * Sets the transactionID value for this InitiateConnectDisconnect.
     * 
     * @param transactionID
     */
    public void setTransactionID(java.lang.String transactionID) {
        this.transactionID = transactionID;
    }


    /**
     * Gets the expirationTime value for this InitiateConnectDisconnect.
     * 
     * @return expirationTime
     */
    public float getExpirationTime() {
        return expirationTime;
    }


    /**
     * Sets the expirationTime value for this InitiateConnectDisconnect.
     * 
     * @param expirationTime
     */
    public void setExpirationTime(float expirationTime) {
        this.expirationTime = expirationTime;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InitiateConnectDisconnect)) return false;
        InitiateConnectDisconnect other = (InitiateConnectDisconnect) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.cdEvents==null && other.getCdEvents()==null) || 
             (this.cdEvents!=null &&
              java.util.Arrays.equals(this.cdEvents, other.getCdEvents()))) &&
            ((this.responseURL==null && other.getResponseURL()==null) || 
             (this.responseURL!=null &&
              this.responseURL.equals(other.getResponseURL()))) &&
            ((this.transactionID==null && other.getTransactionID()==null) || 
             (this.transactionID!=null &&
              this.transactionID.equals(other.getTransactionID()))) &&
            this.expirationTime == other.getExpirationTime();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getCdEvents() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCdEvents());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCdEvents(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getResponseURL() != null) {
            _hashCode += getResponseURL().hashCode();
        }
        if (getTransactionID() != null) {
            _hashCode += getTransactionID().hashCode();
        }
        _hashCode += new Float(getExpirationTime()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(InitiateConnectDisconnect.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">InitiateConnectDisconnect"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cdEvents");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cdEvents"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "connectDisconnectEvent"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "connectDisconnectEvent"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responseURL");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "responseURL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transactionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expirationTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "expirationTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
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
