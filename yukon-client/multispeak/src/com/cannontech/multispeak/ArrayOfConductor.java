/**
 * ArrayOfConductor.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfConductor  implements java.io.Serializable {
    private com.cannontech.multispeak.Conductor[] conductor;

    public ArrayOfConductor() {
    }

    public ArrayOfConductor(
           com.cannontech.multispeak.Conductor[] conductor) {
           this.conductor = conductor;
    }


    /**
     * Gets the conductor value for this ArrayOfConductor.
     * 
     * @return conductor
     */
    public com.cannontech.multispeak.Conductor[] getConductor() {
        return conductor;
    }


    /**
     * Sets the conductor value for this ArrayOfConductor.
     * 
     * @param conductor
     */
    public void setConductor(com.cannontech.multispeak.Conductor[] conductor) {
        this.conductor = conductor;
    }

    public com.cannontech.multispeak.Conductor getConductor(int i) {
        return this.conductor[i];
    }

    public void setConductor(int i, com.cannontech.multispeak.Conductor _value) {
        this.conductor[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfConductor)) return false;
        ArrayOfConductor other = (ArrayOfConductor) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.conductor==null && other.getConductor()==null) || 
             (this.conductor!=null &&
              java.util.Arrays.equals(this.conductor, other.getConductor())));
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
        if (getConductor() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getConductor());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getConductor(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfConductor.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfConductor"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conductor");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "conductor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "conductor"));
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
