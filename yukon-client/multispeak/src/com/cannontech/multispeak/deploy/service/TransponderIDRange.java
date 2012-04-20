/**
 * TransponderIDRange.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class TransponderIDRange  implements java.io.Serializable {
    private java.lang.String start;

    private java.lang.String end;

    public TransponderIDRange() {
    }

    public TransponderIDRange(
           java.lang.String start,
           java.lang.String end) {
           this.start = start;
           this.end = end;
    }


    /**
     * Gets the start value for this TransponderIDRange.
     * 
     * @return start
     */
    public java.lang.String getStart() {
        return start;
    }


    /**
     * Sets the start value for this TransponderIDRange.
     * 
     * @param start
     */
    public void setStart(java.lang.String start) {
        this.start = start;
    }


    /**
     * Gets the end value for this TransponderIDRange.
     * 
     * @return end
     */
    public java.lang.String getEnd() {
        return end;
    }


    /**
     * Sets the end value for this TransponderIDRange.
     * 
     * @param end
     */
    public void setEnd(java.lang.String end) {
        this.end = end;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TransponderIDRange)) return false;
        TransponderIDRange other = (TransponderIDRange) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.start==null && other.getStart()==null) || 
             (this.start!=null &&
              this.start.equals(other.getStart()))) &&
            ((this.end==null && other.getEnd()==null) || 
             (this.end!=null &&
              this.end.equals(other.getEnd())));
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
        if (getStart() != null) {
            _hashCode += getStart().hashCode();
        }
        if (getEnd() != null) {
            _hashCode += getEnd().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TransponderIDRange.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transponderIDRange"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("start");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "start"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("end");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "end"));
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
