/**
 * ArrayOfChannelBlock.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfChannelBlock  implements java.io.Serializable {
    private com.cannontech.multispeak.ChannelBlock[] block;

    public ArrayOfChannelBlock() {
    }

    public ArrayOfChannelBlock(
           com.cannontech.multispeak.ChannelBlock[] block) {
           this.block = block;
    }


    /**
     * Gets the block value for this ArrayOfChannelBlock.
     * 
     * @return block
     */
    public com.cannontech.multispeak.ChannelBlock[] getBlock() {
        return block;
    }


    /**
     * Sets the block value for this ArrayOfChannelBlock.
     * 
     * @param block
     */
    public void setBlock(com.cannontech.multispeak.ChannelBlock[] block) {
        this.block = block;
    }

    public com.cannontech.multispeak.ChannelBlock getBlock(int i) {
        return this.block[i];
    }

    public void setBlock(int i, com.cannontech.multispeak.ChannelBlock _value) {
        this.block[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfChannelBlock)) return false;
        ArrayOfChannelBlock other = (ArrayOfChannelBlock) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.block==null && other.getBlock()==null) || 
             (this.block!=null &&
              java.util.Arrays.equals(this.block, other.getBlock())));
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
        if (getBlock() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getBlock());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getBlock(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfChannelBlock.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfChannelBlock"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("block");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "block"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "channelBlock"));
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
