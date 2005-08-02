/**
 * ArrayOfDomainMember.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfDomainMember  implements java.io.Serializable {
    private com.cannontech.multispeak.DomainMember[] domainMember;

    public ArrayOfDomainMember() {
    }

    public ArrayOfDomainMember(
           com.cannontech.multispeak.DomainMember[] domainMember) {
           this.domainMember = domainMember;
    }


    /**
     * Gets the domainMember value for this ArrayOfDomainMember.
     * 
     * @return domainMember
     */
    public com.cannontech.multispeak.DomainMember[] getDomainMember() {
        return domainMember;
    }


    /**
     * Sets the domainMember value for this ArrayOfDomainMember.
     * 
     * @param domainMember
     */
    public void setDomainMember(com.cannontech.multispeak.DomainMember[] domainMember) {
        this.domainMember = domainMember;
    }

    public com.cannontech.multispeak.DomainMember getDomainMember(int i) {
        return this.domainMember[i];
    }

    public void setDomainMember(int i, com.cannontech.multispeak.DomainMember _value) {
        this.domainMember[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfDomainMember)) return false;
        ArrayOfDomainMember other = (ArrayOfDomainMember) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.domainMember==null && other.getDomainMember()==null) || 
             (this.domainMember!=null &&
              java.util.Arrays.equals(this.domainMember, other.getDomainMember())));
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
        if (getDomainMember() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDomainMember());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDomainMember(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfDomainMember.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfDomainMember"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("domainMember");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "domainMember"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "domainMember"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
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
