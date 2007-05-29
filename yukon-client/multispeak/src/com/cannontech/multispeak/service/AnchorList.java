/**
 * AnchorList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class AnchorList  implements java.io.Serializable {
    private com.cannontech.multispeak.service.Anchor[] anchor;

    public AnchorList() {
    }

    public AnchorList(
           com.cannontech.multispeak.service.Anchor[] anchor) {
           this.anchor = anchor;
    }


    /**
     * Gets the anchor value for this AnchorList.
     * 
     * @return anchor
     */
    public com.cannontech.multispeak.service.Anchor[] getAnchor() {
        return anchor;
    }


    /**
     * Sets the anchor value for this AnchorList.
     * 
     * @param anchor
     */
    public void setAnchor(com.cannontech.multispeak.service.Anchor[] anchor) {
        this.anchor = anchor;
    }

    public com.cannontech.multispeak.service.Anchor getAnchor(int i) {
        return this.anchor[i];
    }

    public void setAnchor(int i, com.cannontech.multispeak.service.Anchor _value) {
        this.anchor[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AnchorList)) return false;
        AnchorList other = (AnchorList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.anchor==null && other.getAnchor()==null) || 
             (this.anchor!=null &&
              java.util.Arrays.equals(this.anchor, other.getAnchor())));
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
        if (getAnchor() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAnchor());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAnchor(), i);
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
        new org.apache.axis.description.TypeDesc(AnchorList.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "anchorList"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("anchor");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "anchor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "anchor"));
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
