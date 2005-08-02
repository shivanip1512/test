/**
 * Channel.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Channel  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfChannelBlock blockList;
    private org.apache.axis.types.UnsignedInt intervalSourceID;  // attribute
    private org.apache.axis.types.UnsignedInt endReadingSourceID;  // attribute
    private org.apache.axis.types.UnsignedInt channelNumber;  // attribute

    public Channel() {
    }

    public Channel(
           com.cannontech.multispeak.ArrayOfChannelBlock blockList,
           org.apache.axis.types.UnsignedInt intervalSourceID,
           org.apache.axis.types.UnsignedInt endReadingSourceID,
           org.apache.axis.types.UnsignedInt channelNumber) {
           this.blockList = blockList;
           this.intervalSourceID = intervalSourceID;
           this.endReadingSourceID = endReadingSourceID;
           this.channelNumber = channelNumber;
    }


    /**
     * Gets the blockList value for this Channel.
     * 
     * @return blockList
     */
    public com.cannontech.multispeak.ArrayOfChannelBlock getBlockList() {
        return blockList;
    }


    /**
     * Sets the blockList value for this Channel.
     * 
     * @param blockList
     */
    public void setBlockList(com.cannontech.multispeak.ArrayOfChannelBlock blockList) {
        this.blockList = blockList;
    }


    /**
     * Gets the intervalSourceID value for this Channel.
     * 
     * @return intervalSourceID
     */
    public org.apache.axis.types.UnsignedInt getIntervalSourceID() {
        return intervalSourceID;
    }


    /**
     * Sets the intervalSourceID value for this Channel.
     * 
     * @param intervalSourceID
     */
    public void setIntervalSourceID(org.apache.axis.types.UnsignedInt intervalSourceID) {
        this.intervalSourceID = intervalSourceID;
    }


    /**
     * Gets the endReadingSourceID value for this Channel.
     * 
     * @return endReadingSourceID
     */
    public org.apache.axis.types.UnsignedInt getEndReadingSourceID() {
        return endReadingSourceID;
    }


    /**
     * Sets the endReadingSourceID value for this Channel.
     * 
     * @param endReadingSourceID
     */
    public void setEndReadingSourceID(org.apache.axis.types.UnsignedInt endReadingSourceID) {
        this.endReadingSourceID = endReadingSourceID;
    }


    /**
     * Gets the channelNumber value for this Channel.
     * 
     * @return channelNumber
     */
    public org.apache.axis.types.UnsignedInt getChannelNumber() {
        return channelNumber;
    }


    /**
     * Sets the channelNumber value for this Channel.
     * 
     * @param channelNumber
     */
    public void setChannelNumber(org.apache.axis.types.UnsignedInt channelNumber) {
        this.channelNumber = channelNumber;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Channel)) return false;
        Channel other = (Channel) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.blockList==null && other.getBlockList()==null) || 
             (this.blockList!=null &&
              this.blockList.equals(other.getBlockList()))) &&
            ((this.intervalSourceID==null && other.getIntervalSourceID()==null) || 
             (this.intervalSourceID!=null &&
              this.intervalSourceID.equals(other.getIntervalSourceID()))) &&
            ((this.endReadingSourceID==null && other.getEndReadingSourceID()==null) || 
             (this.endReadingSourceID!=null &&
              this.endReadingSourceID.equals(other.getEndReadingSourceID()))) &&
            ((this.channelNumber==null && other.getChannelNumber()==null) || 
             (this.channelNumber!=null &&
              this.channelNumber.equals(other.getChannelNumber())));
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
        if (getBlockList() != null) {
            _hashCode += getBlockList().hashCode();
        }
        if (getIntervalSourceID() != null) {
            _hashCode += getIntervalSourceID().hashCode();
        }
        if (getEndReadingSourceID() != null) {
            _hashCode += getEndReadingSourceID().hashCode();
        }
        if (getChannelNumber() != null) {
            _hashCode += getChannelNumber().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Channel.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "channel"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("intervalSourceID");
        attrField.setXmlName(new javax.xml.namespace.QName("", "intervalSourceID"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedInt"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("endReadingSourceID");
        attrField.setXmlName(new javax.xml.namespace.QName("", "endReadingSourceID"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedInt"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("channelNumber");
        attrField.setXmlName(new javax.xml.namespace.QName("", "channelNumber"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedInt"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("blockList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "blockList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfChannelBlock"));
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
