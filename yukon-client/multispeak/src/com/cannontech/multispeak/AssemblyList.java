/**
 * AssemblyList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class AssemblyList  implements java.io.Serializable {
    private com.cannontech.multispeak.Assembly[] assembly;

    public AssemblyList() {
    }

    public AssemblyList(
           com.cannontech.multispeak.Assembly[] assembly) {
           this.assembly = assembly;
    }


    /**
     * Gets the assembly value for this AssemblyList.
     * 
     * @return assembly
     */
    public com.cannontech.multispeak.Assembly[] getAssembly() {
        return assembly;
    }


    /**
     * Sets the assembly value for this AssemblyList.
     * 
     * @param assembly
     */
    public void setAssembly(com.cannontech.multispeak.Assembly[] assembly) {
        this.assembly = assembly;
    }

    public com.cannontech.multispeak.Assembly getAssembly(int i) {
        return this.assembly[i];
    }

    public void setAssembly(int i, com.cannontech.multispeak.Assembly _value) {
        this.assembly[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AssemblyList)) return false;
        AssemblyList other = (AssemblyList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.assembly==null && other.getAssembly()==null) || 
             (this.assembly!=null &&
              java.util.Arrays.equals(this.assembly, other.getAssembly())));
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
        if (getAssembly() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAssembly());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAssembly(), i);
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
        new org.apache.axis.description.TypeDesc(AssemblyList.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assemblyList"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("assembly");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assembly"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assembly"));
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
