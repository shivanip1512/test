/**
 * Tender.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class Tender  extends com.cannontech.multispeak.service.MspObject  implements java.io.Serializable {
    private float amount;
    private java.lang.Float convenienceFee;
    private java.lang.Float utilityFee;
    private java.lang.Float total;
    private java.lang.String authorizationCode;
    private com.cannontech.multispeak.service.PayableItemList payableItemList;
    private java.lang.String paymentMode;
    private com.cannontech.multispeak.service.ResponseCode responseCode;

    public Tender() {
    }

    public Tender(
           float amount,
           java.lang.Float convenienceFee,
           java.lang.Float utilityFee,
           java.lang.Float total,
           java.lang.String authorizationCode,
           com.cannontech.multispeak.service.PayableItemList payableItemList,
           java.lang.String paymentMode,
           com.cannontech.multispeak.service.ResponseCode responseCode) {
           this.amount = amount;
           this.convenienceFee = convenienceFee;
           this.utilityFee = utilityFee;
           this.total = total;
           this.authorizationCode = authorizationCode;
           this.payableItemList = payableItemList;
           this.paymentMode = paymentMode;
           this.responseCode = responseCode;
    }


    /**
     * Gets the amount value for this Tender.
     * 
     * @return amount
     */
    public float getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this Tender.
     * 
     * @param amount
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }


    /**
     * Gets the convenienceFee value for this Tender.
     * 
     * @return convenienceFee
     */
    public java.lang.Float getConvenienceFee() {
        return convenienceFee;
    }


    /**
     * Sets the convenienceFee value for this Tender.
     * 
     * @param convenienceFee
     */
    public void setConvenienceFee(java.lang.Float convenienceFee) {
        this.convenienceFee = convenienceFee;
    }


    /**
     * Gets the utilityFee value for this Tender.
     * 
     * @return utilityFee
     */
    public java.lang.Float getUtilityFee() {
        return utilityFee;
    }


    /**
     * Sets the utilityFee value for this Tender.
     * 
     * @param utilityFee
     */
    public void setUtilityFee(java.lang.Float utilityFee) {
        this.utilityFee = utilityFee;
    }


    /**
     * Gets the total value for this Tender.
     * 
     * @return total
     */
    public java.lang.Float getTotal() {
        return total;
    }


    /**
     * Sets the total value for this Tender.
     * 
     * @param total
     */
    public void setTotal(java.lang.Float total) {
        this.total = total;
    }


    /**
     * Gets the authorizationCode value for this Tender.
     * 
     * @return authorizationCode
     */
    public java.lang.String getAuthorizationCode() {
        return authorizationCode;
    }


    /**
     * Sets the authorizationCode value for this Tender.
     * 
     * @param authorizationCode
     */
    public void setAuthorizationCode(java.lang.String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }


    /**
     * Gets the payableItemList value for this Tender.
     * 
     * @return payableItemList
     */
    public com.cannontech.multispeak.service.PayableItemList getPayableItemList() {
        return payableItemList;
    }


    /**
     * Sets the payableItemList value for this Tender.
     * 
     * @param payableItemList
     */
    public void setPayableItemList(com.cannontech.multispeak.service.PayableItemList payableItemList) {
        this.payableItemList = payableItemList;
    }


    /**
     * Gets the paymentMode value for this Tender.
     * 
     * @return paymentMode
     */
    public java.lang.String getPaymentMode() {
        return paymentMode;
    }


    /**
     * Sets the paymentMode value for this Tender.
     * 
     * @param paymentMode
     */
    public void setPaymentMode(java.lang.String paymentMode) {
        this.paymentMode = paymentMode;
    }


    /**
     * Gets the responseCode value for this Tender.
     * 
     * @return responseCode
     */
    public com.cannontech.multispeak.service.ResponseCode getResponseCode() {
        return responseCode;
    }


    /**
     * Sets the responseCode value for this Tender.
     * 
     * @param responseCode
     */
    public void setResponseCode(com.cannontech.multispeak.service.ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Tender)) return false;
        Tender other = (Tender) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.amount == other.getAmount() &&
            ((this.convenienceFee==null && other.getConvenienceFee()==null) || 
             (this.convenienceFee!=null &&
              this.convenienceFee.equals(other.getConvenienceFee()))) &&
            ((this.utilityFee==null && other.getUtilityFee()==null) || 
             (this.utilityFee!=null &&
              this.utilityFee.equals(other.getUtilityFee()))) &&
            ((this.total==null && other.getTotal()==null) || 
             (this.total!=null &&
              this.total.equals(other.getTotal()))) &&
            ((this.authorizationCode==null && other.getAuthorizationCode()==null) || 
             (this.authorizationCode!=null &&
              this.authorizationCode.equals(other.getAuthorizationCode()))) &&
            ((this.payableItemList==null && other.getPayableItemList()==null) || 
             (this.payableItemList!=null &&
              this.payableItemList.equals(other.getPayableItemList()))) &&
            ((this.paymentMode==null && other.getPaymentMode()==null) || 
             (this.paymentMode!=null &&
              this.paymentMode.equals(other.getPaymentMode()))) &&
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
        int _hashCode = super.hashCode();
        _hashCode += new Float(getAmount()).hashCode();
        if (getConvenienceFee() != null) {
            _hashCode += getConvenienceFee().hashCode();
        }
        if (getUtilityFee() != null) {
            _hashCode += getUtilityFee().hashCode();
        }
        if (getTotal() != null) {
            _hashCode += getTotal().hashCode();
        }
        if (getAuthorizationCode() != null) {
            _hashCode += getAuthorizationCode().hashCode();
        }
        if (getPayableItemList() != null) {
            _hashCode += getPayableItemList().hashCode();
        }
        if (getPaymentMode() != null) {
            _hashCode += getPaymentMode().hashCode();
        }
        if (getResponseCode() != null) {
            _hashCode += getResponseCode().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Tender.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "tender"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
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
        elemField.setFieldName("total");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "total"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authorizationCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "authorizationCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payableItemList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "payableItemList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "payableItemList"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paymentMode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "paymentMode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
