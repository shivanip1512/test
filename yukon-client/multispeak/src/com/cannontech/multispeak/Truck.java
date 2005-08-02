/**
 * Truck.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Truck  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.String truckType;
    private java.lang.String contactInfo;
    private java.lang.Boolean isCompanyOwned;

    public Truck() {
    }

    public Truck(
           java.lang.String truckType,
           java.lang.String contactInfo,
           java.lang.Boolean isCompanyOwned) {
           this.truckType = truckType;
           this.contactInfo = contactInfo;
           this.isCompanyOwned = isCompanyOwned;
    }


    /**
     * Gets the truckType value for this Truck.
     * 
     * @return truckType
     */
    public java.lang.String getTruckType() {
        return truckType;
    }


    /**
     * Sets the truckType value for this Truck.
     * 
     * @param truckType
     */
    public void setTruckType(java.lang.String truckType) {
        this.truckType = truckType;
    }


    /**
     * Gets the contactInfo value for this Truck.
     * 
     * @return contactInfo
     */
    public java.lang.String getContactInfo() {
        return contactInfo;
    }


    /**
     * Sets the contactInfo value for this Truck.
     * 
     * @param contactInfo
     */
    public void setContactInfo(java.lang.String contactInfo) {
        this.contactInfo = contactInfo;
    }


    /**
     * Gets the isCompanyOwned value for this Truck.
     * 
     * @return isCompanyOwned
     */
    public java.lang.Boolean getIsCompanyOwned() {
        return isCompanyOwned;
    }


    /**
     * Sets the isCompanyOwned value for this Truck.
     * 
     * @param isCompanyOwned
     */
    public void setIsCompanyOwned(java.lang.Boolean isCompanyOwned) {
        this.isCompanyOwned = isCompanyOwned;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Truck)) return false;
        Truck other = (Truck) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.truckType==null && other.getTruckType()==null) || 
             (this.truckType!=null &&
              this.truckType.equals(other.getTruckType()))) &&
            ((this.contactInfo==null && other.getContactInfo()==null) || 
             (this.contactInfo!=null &&
              this.contactInfo.equals(other.getContactInfo()))) &&
            ((this.isCompanyOwned==null && other.getIsCompanyOwned()==null) || 
             (this.isCompanyOwned!=null &&
              this.isCompanyOwned.equals(other.getIsCompanyOwned())));
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
        if (getTruckType() != null) {
            _hashCode += getTruckType().hashCode();
        }
        if (getContactInfo() != null) {
            _hashCode += getContactInfo().hashCode();
        }
        if (getIsCompanyOwned() != null) {
            _hashCode += getIsCompanyOwned().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Truck.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "truck"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("truckType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "truckType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("contactInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "contactInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isCompanyOwned");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "isCompanyOwned"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
