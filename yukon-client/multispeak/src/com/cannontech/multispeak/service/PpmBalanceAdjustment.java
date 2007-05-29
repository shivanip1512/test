/**
 * PpmBalanceAdjustment.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class PpmBalanceAdjustment  extends com.cannontech.multispeak.service.MspObject  implements java.io.Serializable {
    private java.lang.String description;
    private java.lang.String accountNumber;
    private java.lang.String serviceType;
    private java.lang.String ppmLocationID;
    private com.cannontech.multispeak.service.AdjustmentItemList adustmentList;

    public PpmBalanceAdjustment() {
    }

    public PpmBalanceAdjustment(
           java.lang.String description,
           java.lang.String accountNumber,
           java.lang.String serviceType,
           java.lang.String ppmLocationID,
           com.cannontech.multispeak.service.AdjustmentItemList adustmentList) {
           this.description = description;
           this.accountNumber = accountNumber;
           this.serviceType = serviceType;
           this.ppmLocationID = ppmLocationID;
           this.adustmentList = adustmentList;
    }


    /**
     * Gets the description value for this PpmBalanceAdjustment.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this PpmBalanceAdjustment.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the accountNumber value for this PpmBalanceAdjustment.
     * 
     * @return accountNumber
     */
    public java.lang.String getAccountNumber() {
        return accountNumber;
    }


    /**
     * Sets the accountNumber value for this PpmBalanceAdjustment.
     * 
     * @param accountNumber
     */
    public void setAccountNumber(java.lang.String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the serviceType value for this PpmBalanceAdjustment.
     * 
     * @return serviceType
     */
    public java.lang.String getServiceType() {
        return serviceType;
    }


    /**
     * Sets the serviceType value for this PpmBalanceAdjustment.
     * 
     * @param serviceType
     */
    public void setServiceType(java.lang.String serviceType) {
        this.serviceType = serviceType;
    }


    /**
     * Gets the ppmLocationID value for this PpmBalanceAdjustment.
     * 
     * @return ppmLocationID
     */
    public java.lang.String getPpmLocationID() {
        return ppmLocationID;
    }


    /**
     * Sets the ppmLocationID value for this PpmBalanceAdjustment.
     * 
     * @param ppmLocationID
     */
    public void setPpmLocationID(java.lang.String ppmLocationID) {
        this.ppmLocationID = ppmLocationID;
    }


    /**
     * Gets the adustmentList value for this PpmBalanceAdjustment.
     * 
     * @return adustmentList
     */
    public com.cannontech.multispeak.service.AdjustmentItemList getAdustmentList() {
        return adustmentList;
    }


    /**
     * Sets the adustmentList value for this PpmBalanceAdjustment.
     * 
     * @param adustmentList
     */
    public void setAdustmentList(com.cannontech.multispeak.service.AdjustmentItemList adustmentList) {
        this.adustmentList = adustmentList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PpmBalanceAdjustment)) return false;
        PpmBalanceAdjustment other = (PpmBalanceAdjustment) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.accountNumber==null && other.getAccountNumber()==null) || 
             (this.accountNumber!=null &&
              this.accountNumber.equals(other.getAccountNumber()))) &&
            ((this.serviceType==null && other.getServiceType()==null) || 
             (this.serviceType!=null &&
              this.serviceType.equals(other.getServiceType()))) &&
            ((this.ppmLocationID==null && other.getPpmLocationID()==null) || 
             (this.ppmLocationID!=null &&
              this.ppmLocationID.equals(other.getPpmLocationID()))) &&
            ((this.adustmentList==null && other.getAdustmentList()==null) || 
             (this.adustmentList!=null &&
              this.adustmentList.equals(other.getAdustmentList())));
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
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getAccountNumber() != null) {
            _hashCode += getAccountNumber().hashCode();
        }
        if (getServiceType() != null) {
            _hashCode += getServiceType().hashCode();
        }
        if (getPpmLocationID() != null) {
            _hashCode += getPpmLocationID().hashCode();
        }
        if (getAdustmentList() != null) {
            _hashCode += getAdustmentList().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PpmBalanceAdjustment.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ppmBalanceAdjustment"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ppmLocationID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ppmLocationID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("adustmentList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "adustmentList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "adjustmentItemList"));
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
