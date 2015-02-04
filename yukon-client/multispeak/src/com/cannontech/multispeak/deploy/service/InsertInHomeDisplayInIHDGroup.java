/**
 * InsertInHomeDisplayInIHDGroup.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class InsertInHomeDisplayInIHDGroup  implements java.io.Serializable {
    private java.lang.String[] inHomeDisplayIDs;

    private java.lang.String IHDGroupID;

    public InsertInHomeDisplayInIHDGroup() {
    }

    public InsertInHomeDisplayInIHDGroup(
           java.lang.String[] inHomeDisplayIDs,
           java.lang.String IHDGroupID) {
           this.inHomeDisplayIDs = inHomeDisplayIDs;
           this.IHDGroupID = IHDGroupID;
    }


    /**
     * Gets the inHomeDisplayIDs value for this InsertInHomeDisplayInIHDGroup.
     * 
     * @return inHomeDisplayIDs
     */
    public java.lang.String[] getInHomeDisplayIDs() {
        return inHomeDisplayIDs;
    }


    /**
     * Sets the inHomeDisplayIDs value for this InsertInHomeDisplayInIHDGroup.
     * 
     * @param inHomeDisplayIDs
     */
    public void setInHomeDisplayIDs(java.lang.String[] inHomeDisplayIDs) {
        this.inHomeDisplayIDs = inHomeDisplayIDs;
    }


    /**
     * Gets the IHDGroupID value for this InsertInHomeDisplayInIHDGroup.
     * 
     * @return IHDGroupID
     */
    public java.lang.String getIHDGroupID() {
        return IHDGroupID;
    }


    /**
     * Sets the IHDGroupID value for this InsertInHomeDisplayInIHDGroup.
     * 
     * @param IHDGroupID
     */
    public void setIHDGroupID(java.lang.String IHDGroupID) {
        this.IHDGroupID = IHDGroupID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InsertInHomeDisplayInIHDGroup)) return false;
        InsertInHomeDisplayInIHDGroup other = (InsertInHomeDisplayInIHDGroup) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.inHomeDisplayIDs==null && other.getInHomeDisplayIDs()==null) || 
             (this.inHomeDisplayIDs!=null &&
              java.util.Arrays.equals(this.inHomeDisplayIDs, other.getInHomeDisplayIDs()))) &&
            ((this.IHDGroupID==null && other.getIHDGroupID()==null) || 
             (this.IHDGroupID!=null &&
              this.IHDGroupID.equals(other.getIHDGroupID())));
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
        if (getInHomeDisplayIDs() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getInHomeDisplayIDs());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getInHomeDisplayIDs(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getIHDGroupID() != null) {
            _hashCode += getIHDGroupID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(InsertInHomeDisplayInIHDGroup.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">InsertInHomeDisplayInIHDGroup"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inHomeDisplayIDs");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayIDs"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("IHDGroupID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "IHDGroupID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
