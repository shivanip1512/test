/**
 * ArrayOfProfileType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfProfileType  implements java.io.Serializable {
    private com.cannontech.multispeak.ProfileType[] loadProfile;

    public ArrayOfProfileType() {
    }

    public ArrayOfProfileType(
           com.cannontech.multispeak.ProfileType[] loadProfile) {
           this.loadProfile = loadProfile;
    }


    /**
     * Gets the loadProfile value for this ArrayOfProfileType.
     * 
     * @return loadProfile
     */
    public com.cannontech.multispeak.ProfileType[] getLoadProfile() {
        return loadProfile;
    }


    /**
     * Sets the loadProfile value for this ArrayOfProfileType.
     * 
     * @param loadProfile
     */
    public void setLoadProfile(com.cannontech.multispeak.ProfileType[] loadProfile) {
        this.loadProfile = loadProfile;
    }

    public com.cannontech.multispeak.ProfileType getLoadProfile(int i) {
        return this.loadProfile[i];
    }

    public void setLoadProfile(int i, com.cannontech.multispeak.ProfileType _value) {
        this.loadProfile[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfProfileType)) return false;
        ArrayOfProfileType other = (ArrayOfProfileType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.loadProfile==null && other.getLoadProfile()==null) || 
             (this.loadProfile!=null &&
              java.util.Arrays.equals(this.loadProfile, other.getLoadProfile())));
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
        if (getLoadProfile() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getLoadProfile());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getLoadProfile(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfProfileType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfProfileType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loadProfile");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadProfile"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "profileType"));
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
