/**
 * ResolvedCaller.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ResolvedCaller  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.CustomerCall[] resolvedCallers;

    public ResolvedCaller() {
    }

    public ResolvedCaller(
           com.cannontech.multispeak.deploy.service.CustomerCall[] resolvedCallers) {
           this.resolvedCallers = resolvedCallers;
    }


    /**
     * Gets the resolvedCallers value for this ResolvedCaller.
     * 
     * @return resolvedCallers
     */
    public com.cannontech.multispeak.deploy.service.CustomerCall[] getResolvedCallers() {
        return resolvedCallers;
    }


    /**
     * Sets the resolvedCallers value for this ResolvedCaller.
     * 
     * @param resolvedCallers
     */
    public void setResolvedCallers(com.cannontech.multispeak.deploy.service.CustomerCall[] resolvedCallers) {
        this.resolvedCallers = resolvedCallers;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ResolvedCaller)) return false;
        ResolvedCaller other = (ResolvedCaller) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.resolvedCallers==null && other.getResolvedCallers()==null) || 
             (this.resolvedCallers!=null &&
              java.util.Arrays.equals(this.resolvedCallers, other.getResolvedCallers())));
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
        if (getResolvedCallers() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getResolvedCallers());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getResolvedCallers(), i);
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
        new org.apache.axis.description.TypeDesc(ResolvedCaller.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ResolvedCaller"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resolvedCallers");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "resolvedCallers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customerCall"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customerCall"));
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
