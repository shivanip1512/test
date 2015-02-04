/**
 * ConvenienceFeeItem.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ConvenienceFeeItem  implements java.io.Serializable {
    private java.lang.String transactionID;

    private java.lang.Float convenienceFee;

    private java.lang.Float utilityFee;

    private com.cannontech.multispeak.deploy.service.ResponseCode responseCode;

    public ConvenienceFeeItem() {
    }

    public ConvenienceFeeItem(
           java.lang.String transactionID,
           java.lang.Float convenienceFee,
           java.lang.Float utilityFee,
           com.cannontech.multispeak.deploy.service.ResponseCode responseCode) {
           this.transactionID = transactionID;
           this.convenienceFee = convenienceFee;
           this.utilityFee = utilityFee;
           this.responseCode = responseCode;
    }


    /**
     * Gets the transactionID value for this ConvenienceFeeItem.
     * 
     * @return transactionID
     */
    public java.lang.String getTransactionID() {
        return transactionID;
    }


    /**
     * Sets the transactionID value for this ConvenienceFeeItem.
     * 
     * @param transactionID
     */
    public void setTransactionID(java.lang.String transactionID) {
        this.transactionID = transactionID;
    }


    /**
     * Gets the convenienceFee value for this ConvenienceFeeItem.
     * 
     * @return convenienceFee
     */
    public java.lang.Float getConvenienceFee() {
        return convenienceFee;
    }


    /**
     * Sets the convenienceFee value for this ConvenienceFeeItem.
     * 
     * @param convenienceFee
     */
    public void setConvenienceFee(java.lang.Float convenienceFee) {
        this.convenienceFee = convenienceFee;
    }


    /**
     * Gets the utilityFee value for this ConvenienceFeeItem.
     * 
     * @return utilityFee
     */
    public java.lang.Float getUtilityFee() {
        return utilityFee;
    }


    /**
     * Sets the utilityFee value for this ConvenienceFeeItem.
     * 
     * @param utilityFee
     */
    public void setUtilityFee(java.lang.Float utilityFee) {
        this.utilityFee = utilityFee;
    }


    /**
     * Gets the responseCode value for this ConvenienceFeeItem.
     * 
     * @return responseCode
     */
    public com.cannontech.multispeak.deploy.service.ResponseCode getResponseCode() {
        return responseCode;
    }


    /**
     * Sets the responseCode value for this ConvenienceFeeItem.
     * 
     * @param responseCode
     */
    public void setResponseCode(com.cannontech.multispeak.deploy.service.ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ConvenienceFeeItem)) return false;
        ConvenienceFeeItem other = (ConvenienceFeeItem) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.transactionID==null && other.getTransactionID()==null) || 
             (this.transactionID!=null &&
              this.transactionID.equals(other.getTransactionID()))) &&
            ((this.convenienceFee==null && other.getConvenienceFee()==null) || 
             (this.convenienceFee!=null &&
              this.convenienceFee.equals(other.getConvenienceFee()))) &&
            ((this.utilityFee==null && other.getUtilityFee()==null) || 
             (this.utilityFee!=null &&
              this.utilityFee.equals(other.getUtilityFee()))) &&
            ((this.responseCode==null && other.getResponseCode()==null) || 
             (this.responseCode!=null &&
              this.responseCode.equals(other.getResponseCode())));
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
        if (getTransactionID() != null) {
            _hashCode += getTransactionID().hashCode();
        }
        if (getConvenienceFee() != null) {
            _hashCode += getConvenienceFee().hashCode();
        }
        if (getUtilityFee() != null) {
            _hashCode += getUtilityFee().hashCode();
        }
        if (getResponseCode() != null) {
            _hashCode += getResponseCode().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ConvenienceFeeItem.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "convenienceFeeItem"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transactionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("convenienceFee");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "convenienceFee"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("utilityFee");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "utilityFee"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responseCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "responseCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "responseCode"));
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
