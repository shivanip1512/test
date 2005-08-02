/**
 * PcbTestList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class PcbTestList  implements java.io.Serializable {
    private com.cannontech.multispeak.TestInstance[] testInstance;

    public PcbTestList() {
    }

    public PcbTestList(
           com.cannontech.multispeak.TestInstance[] testInstance) {
           this.testInstance = testInstance;
    }


    /**
     * Gets the testInstance value for this PcbTestList.
     * 
     * @return testInstance
     */
    public com.cannontech.multispeak.TestInstance[] getTestInstance() {
        return testInstance;
    }


    /**
     * Sets the testInstance value for this PcbTestList.
     * 
     * @param testInstance
     */
    public void setTestInstance(com.cannontech.multispeak.TestInstance[] testInstance) {
        this.testInstance = testInstance;
    }

    public com.cannontech.multispeak.TestInstance getTestInstance(int i) {
        return this.testInstance[i];
    }

    public void setTestInstance(int i, com.cannontech.multispeak.TestInstance _value) {
        this.testInstance[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PcbTestList)) return false;
        PcbTestList other = (PcbTestList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.testInstance==null && other.getTestInstance()==null) || 
             (this.testInstance!=null &&
              java.util.Arrays.equals(this.testInstance, other.getTestInstance())));
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
        if (getTestInstance() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTestInstance());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getTestInstance(), i);
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
        new org.apache.axis.description.TypeDesc(PcbTestList.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pcbTestList"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("testInstance");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "testInstance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "testInstance"));
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
