/**
 * GetServiceLocationByShutOffDate.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GetServiceLocationByShutOffDate  implements java.io.Serializable {
    private java.util.Calendar shutOffDate;

    public GetServiceLocationByShutOffDate() {
    }

    public GetServiceLocationByShutOffDate(
           java.util.Calendar shutOffDate) {
           this.shutOffDate = shutOffDate;
    }


    /**
     * Gets the shutOffDate value for this GetServiceLocationByShutOffDate.
     * 
     * @return shutOffDate
     */
    public java.util.Calendar getShutOffDate() {
        return shutOffDate;
    }


    /**
     * Sets the shutOffDate value for this GetServiceLocationByShutOffDate.
     * 
     * @param shutOffDate
     */
    public void setShutOffDate(java.util.Calendar shutOffDate) {
        this.shutOffDate = shutOffDate;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetServiceLocationByShutOffDate)) return false;
        GetServiceLocationByShutOffDate other = (GetServiceLocationByShutOffDate) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.shutOffDate==null && other.getShutOffDate()==null) || 
             (this.shutOffDate!=null &&
              this.shutOffDate.equals(other.getShutOffDate())));
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
        if (getShutOffDate() != null) {
            _hashCode += getShutOffDate().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetServiceLocationByShutOffDate.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByShutOffDate"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shutOffDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "shutOffDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
