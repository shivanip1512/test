/**
 * ArrayOfUsageInstance.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfUsageInstance  implements java.io.Serializable {
    private com.cannontech.multispeak.UsageInstance[] usageInstance;

    public ArrayOfUsageInstance() {
    }

    public ArrayOfUsageInstance(
           com.cannontech.multispeak.UsageInstance[] usageInstance) {
           this.usageInstance = usageInstance;
    }


    /**
     * Gets the usageInstance value for this ArrayOfUsageInstance.
     * 
     * @return usageInstance
     */
    public com.cannontech.multispeak.UsageInstance[] getUsageInstance() {
        return usageInstance;
    }


    /**
     * Sets the usageInstance value for this ArrayOfUsageInstance.
     * 
     * @param usageInstance
     */
    public void setUsageInstance(com.cannontech.multispeak.UsageInstance[] usageInstance) {
        this.usageInstance = usageInstance;
    }

    public com.cannontech.multispeak.UsageInstance getUsageInstance(int i) {
        return this.usageInstance[i];
    }

    public void setUsageInstance(int i, com.cannontech.multispeak.UsageInstance _value) {
        this.usageInstance[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfUsageInstance)) return false;
        ArrayOfUsageInstance other = (ArrayOfUsageInstance) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.usageInstance==null && other.getUsageInstance()==null) || 
             (this.usageInstance!=null &&
              java.util.Arrays.equals(this.usageInstance, other.getUsageInstance())));
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
        if (getUsageInstance() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getUsageInstance());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getUsageInstance(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfUsageInstance.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfUsageInstance"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("usageInstance");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageInstance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageInstance"));
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
