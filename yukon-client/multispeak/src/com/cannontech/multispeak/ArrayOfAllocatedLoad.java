/**
 * ArrayOfAllocatedLoad.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfAllocatedLoad  implements java.io.Serializable {
    private com.cannontech.multispeak.AllocatedLoad[] allocatedLoad;

    public ArrayOfAllocatedLoad() {
    }

    public ArrayOfAllocatedLoad(
           com.cannontech.multispeak.AllocatedLoad[] allocatedLoad) {
           this.allocatedLoad = allocatedLoad;
    }


    /**
     * Gets the allocatedLoad value for this ArrayOfAllocatedLoad.
     * 
     * @return allocatedLoad
     */
    public com.cannontech.multispeak.AllocatedLoad[] getAllocatedLoad() {
        return allocatedLoad;
    }


    /**
     * Sets the allocatedLoad value for this ArrayOfAllocatedLoad.
     * 
     * @param allocatedLoad
     */
    public void setAllocatedLoad(com.cannontech.multispeak.AllocatedLoad[] allocatedLoad) {
        this.allocatedLoad = allocatedLoad;
    }

    public com.cannontech.multispeak.AllocatedLoad getAllocatedLoad(int i) {
        return this.allocatedLoad[i];
    }

    public void setAllocatedLoad(int i, com.cannontech.multispeak.AllocatedLoad _value) {
        this.allocatedLoad[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfAllocatedLoad)) return false;
        ArrayOfAllocatedLoad other = (ArrayOfAllocatedLoad) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.allocatedLoad==null && other.getAllocatedLoad()==null) || 
             (this.allocatedLoad!=null &&
              java.util.Arrays.equals(this.allocatedLoad, other.getAllocatedLoad())));
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
        if (getAllocatedLoad() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAllocatedLoad());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAllocatedLoad(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfAllocatedLoad.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfAllocatedLoad"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("allocatedLoad");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "allocatedLoad"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "allocatedLoad"));
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
