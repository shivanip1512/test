/**
 * ProfileObject.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ProfileObject  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfSource sourceList;
    private com.cannontech.multispeak.ArrayOfProfileType loadProfileList;

    public ProfileObject() {
    }

    public ProfileObject(
           com.cannontech.multispeak.ArrayOfSource sourceList,
           com.cannontech.multispeak.ArrayOfProfileType loadProfileList) {
           this.sourceList = sourceList;
           this.loadProfileList = loadProfileList;
    }


    /**
     * Gets the sourceList value for this ProfileObject.
     * 
     * @return sourceList
     */
    public com.cannontech.multispeak.ArrayOfSource getSourceList() {
        return sourceList;
    }


    /**
     * Sets the sourceList value for this ProfileObject.
     * 
     * @param sourceList
     */
    public void setSourceList(com.cannontech.multispeak.ArrayOfSource sourceList) {
        this.sourceList = sourceList;
    }


    /**
     * Gets the loadProfileList value for this ProfileObject.
     * 
     * @return loadProfileList
     */
    public com.cannontech.multispeak.ArrayOfProfileType getLoadProfileList() {
        return loadProfileList;
    }


    /**
     * Sets the loadProfileList value for this ProfileObject.
     * 
     * @param loadProfileList
     */
    public void setLoadProfileList(com.cannontech.multispeak.ArrayOfProfileType loadProfileList) {
        this.loadProfileList = loadProfileList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ProfileObject)) return false;
        ProfileObject other = (ProfileObject) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.sourceList==null && other.getSourceList()==null) || 
             (this.sourceList!=null &&
              this.sourceList.equals(other.getSourceList()))) &&
            ((this.loadProfileList==null && other.getLoadProfileList()==null) || 
             (this.loadProfileList!=null &&
              this.loadProfileList.equals(other.getLoadProfileList())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getSourceList() != null) {
            _hashCode += getSourceList().hashCode();
        }
        if (getLoadProfileList() != null) {
            _hashCode += getLoadProfileList().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ProfileObject.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "profileObject"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sourceList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sourceList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfSource"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loadProfileList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadProfileList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfProfileType"));
        elemField.setMinOccurs(0);
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
