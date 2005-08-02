/**
 * ArrayOfCDCustomer.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfCDCustomer  implements java.io.Serializable {
    private com.cannontech.multispeak.CDCustomer[] CDCustomer;

    public ArrayOfCDCustomer() {
    }

    public ArrayOfCDCustomer(
           com.cannontech.multispeak.CDCustomer[] CDCustomer) {
           this.CDCustomer = CDCustomer;
    }


    /**
     * Gets the CDCustomer value for this ArrayOfCDCustomer.
     * 
     * @return CDCustomer
     */
    public com.cannontech.multispeak.CDCustomer[] getCDCustomer() {
        return CDCustomer;
    }


    /**
     * Sets the CDCustomer value for this ArrayOfCDCustomer.
     * 
     * @param CDCustomer
     */
    public void setCDCustomer(com.cannontech.multispeak.CDCustomer[] CDCustomer) {
        this.CDCustomer = CDCustomer;
    }

    public com.cannontech.multispeak.CDCustomer getCDCustomer(int i) {
        return this.CDCustomer[i];
    }

    public void setCDCustomer(int i, com.cannontech.multispeak.CDCustomer _value) {
        this.CDCustomer[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfCDCustomer)) return false;
        ArrayOfCDCustomer other = (ArrayOfCDCustomer) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.CDCustomer==null && other.getCDCustomer()==null) || 
             (this.CDCustomer!=null &&
              java.util.Arrays.equals(this.CDCustomer, other.getCDCustomer())));
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
        if (getCDCustomer() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCDCustomer());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCDCustomer(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ArrayOfCDCustomer.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCDCustomer"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CDCustomer");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDCustomer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDCustomer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
