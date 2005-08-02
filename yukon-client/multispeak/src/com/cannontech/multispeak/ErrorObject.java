/**
 * ErrorObject.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ErrorObject  implements java.io.Serializable {
    private java.lang.String objectID;  // attribute
    private java.lang.String errorString;  // attribute
    private java.lang.String nounType;  // attribute
    private java.util.Calendar eventTime;  // attribute

    public ErrorObject() {
    }

    public ErrorObject(
           java.lang.String objectID,
           java.lang.String errorString,
           java.lang.String nounType,
           java.util.Calendar eventTime) {
           this.objectID = objectID;
           this.errorString = errorString;
           this.nounType = nounType;
           this.eventTime = eventTime;
    }


    /**
     * Gets the objectID value for this ErrorObject.
     * 
     * @return objectID
     */
    public java.lang.String getObjectID() {
        return objectID;
    }


    /**
     * Sets the objectID value for this ErrorObject.
     * 
     * @param objectID
     */
    public void setObjectID(java.lang.String objectID) {
        this.objectID = objectID;
    }


    /**
     * Gets the errorString value for this ErrorObject.
     * 
     * @return errorString
     */
    public java.lang.String getErrorString() {
        return errorString;
    }


    /**
     * Sets the errorString value for this ErrorObject.
     * 
     * @param errorString
     */
    public void setErrorString(java.lang.String errorString) {
        this.errorString = errorString;
    }


    /**
     * Gets the nounType value for this ErrorObject.
     * 
     * @return nounType
     */
    public java.lang.String getNounType() {
        return nounType;
    }


    /**
     * Sets the nounType value for this ErrorObject.
     * 
     * @param nounType
     */
    public void setNounType(java.lang.String nounType) {
        this.nounType = nounType;
    }


    /**
     * Gets the eventTime value for this ErrorObject.
     * 
     * @return eventTime
     */
    public java.util.Calendar getEventTime() {
        return eventTime;
    }


    /**
     * Sets the eventTime value for this ErrorObject.
     * 
     * @param eventTime
     */
    public void setEventTime(java.util.Calendar eventTime) {
        this.eventTime = eventTime;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ErrorObject)) return false;
        ErrorObject other = (ErrorObject) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.objectID==null && other.getObjectID()==null) || 
             (this.objectID!=null &&
              this.objectID.equals(other.getObjectID()))) &&
            ((this.errorString==null && other.getErrorString()==null) || 
             (this.errorString!=null &&
              this.errorString.equals(other.getErrorString()))) &&
            ((this.nounType==null && other.getNounType()==null) || 
             (this.nounType!=null &&
              this.nounType.equals(other.getNounType()))) &&
            ((this.eventTime==null && other.getEventTime()==null) || 
             (this.eventTime!=null &&
              this.eventTime.equals(other.getEventTime())));
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
        if (getObjectID() != null) {
            _hashCode += getObjectID().hashCode();
        }
        if (getErrorString() != null) {
            _hashCode += getErrorString().hashCode();
        }
        if (getNounType() != null) {
            _hashCode += getNounType().hashCode();
        }
        if (getEventTime() != null) {
            _hashCode += getEventTime().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ErrorObject.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("objectID");
        attrField.setXmlName(new javax.xml.namespace.QName("", "objectID"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("errorString");
        attrField.setXmlName(new javax.xml.namespace.QName("", "errorString"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("nounType");
        attrField.setXmlName(new javax.xml.namespace.QName("", "nounType"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("eventTime");
        attrField.setXmlName(new javax.xml.namespace.QName("", "eventTime"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        typeDesc.addFieldDesc(attrField);
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
