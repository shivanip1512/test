/**
 * ProfileType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ProfileType  implements java.io.Serializable {
    private java.lang.String meterID;
    private org.apache.axis.types.UnsignedByte dataSetNumber;
    private com.cannontech.multispeak.ArrayOfChannel channelList;

    public ProfileType() {
    }

    public ProfileType(
           java.lang.String meterID,
           org.apache.axis.types.UnsignedByte dataSetNumber,
           com.cannontech.multispeak.ArrayOfChannel channelList) {
           this.meterID = meterID;
           this.dataSetNumber = dataSetNumber;
           this.channelList = channelList;
    }


    /**
     * Gets the meterID value for this ProfileType.
     * 
     * @return meterID
     */
    public java.lang.String getMeterID() {
        return meterID;
    }


    /**
     * Sets the meterID value for this ProfileType.
     * 
     * @param meterID
     */
    public void setMeterID(java.lang.String meterID) {
        this.meterID = meterID;
    }


    /**
     * Gets the dataSetNumber value for this ProfileType.
     * 
     * @return dataSetNumber
     */
    public org.apache.axis.types.UnsignedByte getDataSetNumber() {
        return dataSetNumber;
    }


    /**
     * Sets the dataSetNumber value for this ProfileType.
     * 
     * @param dataSetNumber
     */
    public void setDataSetNumber(org.apache.axis.types.UnsignedByte dataSetNumber) {
        this.dataSetNumber = dataSetNumber;
    }


    /**
     * Gets the channelList value for this ProfileType.
     * 
     * @return channelList
     */
    public com.cannontech.multispeak.ArrayOfChannel getChannelList() {
        return channelList;
    }


    /**
     * Sets the channelList value for this ProfileType.
     * 
     * @param channelList
     */
    public void setChannelList(com.cannontech.multispeak.ArrayOfChannel channelList) {
        this.channelList = channelList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ProfileType)) return false;
        ProfileType other = (ProfileType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.meterID==null && other.getMeterID()==null) || 
             (this.meterID!=null &&
              this.meterID.equals(other.getMeterID()))) &&
            ((this.dataSetNumber==null && other.getDataSetNumber()==null) || 
             (this.dataSetNumber!=null &&
              this.dataSetNumber.equals(other.getDataSetNumber()))) &&
            ((this.channelList==null && other.getChannelList()==null) || 
             (this.channelList!=null &&
              this.channelList.equals(other.getChannelList())));
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
        if (getMeterID() != null) {
            _hashCode += getMeterID().hashCode();
        }
        if (getDataSetNumber() != null) {
            _hashCode += getDataSetNumber().hashCode();
        }
        if (getChannelList() != null) {
            _hashCode += getChannelList().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ProfileType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "profileType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataSetNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "dataSetNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedByte"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("channelList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "channelList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfChannel"));
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
