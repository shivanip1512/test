/**
 * GetModifiedMeters.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GetModifiedMeters  implements java.io.Serializable {
    private java.lang.String previousSessionID;
    private java.lang.String lastReceived;

    public GetModifiedMeters() {
    }

    public GetModifiedMeters(
           java.lang.String previousSessionID,
           java.lang.String lastReceived) {
           this.previousSessionID = previousSessionID;
           this.lastReceived = lastReceived;
    }


    /**
     * Gets the previousSessionID value for this GetModifiedMeters.
     * 
     * @return previousSessionID
     */
    public java.lang.String getPreviousSessionID() {
        return previousSessionID;
    }


    /**
     * Sets the previousSessionID value for this GetModifiedMeters.
     * 
     * @param previousSessionID
     */
    public void setPreviousSessionID(java.lang.String previousSessionID) {
        this.previousSessionID = previousSessionID;
    }


    /**
     * Gets the lastReceived value for this GetModifiedMeters.
     * 
     * @return lastReceived
     */
    public java.lang.String getLastReceived() {
        return lastReceived;
    }


    /**
     * Sets the lastReceived value for this GetModifiedMeters.
     * 
     * @param lastReceived
     */
    public void setLastReceived(java.lang.String lastReceived) {
        this.lastReceived = lastReceived;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetModifiedMeters)) return false;
        GetModifiedMeters other = (GetModifiedMeters) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.previousSessionID==null && other.getPreviousSessionID()==null) || 
             (this.previousSessionID!=null &&
              this.previousSessionID.equals(other.getPreviousSessionID()))) &&
            ((this.lastReceived==null && other.getLastReceived()==null) || 
             (this.lastReceived!=null &&
              this.lastReceived.equals(other.getLastReceived())));
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
        if (getPreviousSessionID() != null) {
            _hashCode += getPreviousSessionID().hashCode();
        }
        if (getLastReceived() != null) {
            _hashCode += getLastReceived().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetModifiedMeters.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetModifiedMeters"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("previousSessionID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "previousSessionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastReceived");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"));
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
