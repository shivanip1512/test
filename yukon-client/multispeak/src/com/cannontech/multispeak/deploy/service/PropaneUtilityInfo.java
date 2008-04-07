/**
 * PropaneUtilityInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class PropaneUtilityInfo  implements java.io.Serializable {
    private java.lang.String owner;

    private java.lang.String district;

    private java.lang.String serviceLocationID;

    private java.lang.String accountNumber;

    private java.lang.String customerID;

    private com.cannontech.multispeak.deploy.service.PointType mapLocation;

    public PropaneUtilityInfo() {
    }

    public PropaneUtilityInfo(
           java.lang.String owner,
           java.lang.String district,
           java.lang.String serviceLocationID,
           java.lang.String accountNumber,
           java.lang.String customerID,
           com.cannontech.multispeak.deploy.service.PointType mapLocation) {
           this.owner = owner;
           this.district = district;
           this.serviceLocationID = serviceLocationID;
           this.accountNumber = accountNumber;
           this.customerID = customerID;
           this.mapLocation = mapLocation;
    }


    /**
     * Gets the owner value for this PropaneUtilityInfo.
     * 
     * @return owner
     */
    public java.lang.String getOwner() {
        return owner;
    }


    /**
     * Sets the owner value for this PropaneUtilityInfo.
     * 
     * @param owner
     */
    public void setOwner(java.lang.String owner) {
        this.owner = owner;
    }


    /**
     * Gets the district value for this PropaneUtilityInfo.
     * 
     * @return district
     */
    public java.lang.String getDistrict() {
        return district;
    }


    /**
     * Sets the district value for this PropaneUtilityInfo.
     * 
     * @param district
     */
    public void setDistrict(java.lang.String district) {
        this.district = district;
    }


    /**
     * Gets the serviceLocationID value for this PropaneUtilityInfo.
     * 
     * @return serviceLocationID
     */
    public java.lang.String getServiceLocationID() {
        return serviceLocationID;
    }


    /**
     * Sets the serviceLocationID value for this PropaneUtilityInfo.
     * 
     * @param serviceLocationID
     */
    public void setServiceLocationID(java.lang.String serviceLocationID) {
        this.serviceLocationID = serviceLocationID;
    }


    /**
     * Gets the accountNumber value for this PropaneUtilityInfo.
     * 
     * @return accountNumber
     */
    public java.lang.String getAccountNumber() {
        return accountNumber;
    }


    /**
     * Sets the accountNumber value for this PropaneUtilityInfo.
     * 
     * @param accountNumber
     */
    public void setAccountNumber(java.lang.String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the customerID value for this PropaneUtilityInfo.
     * 
     * @return customerID
     */
    public java.lang.String getCustomerID() {
        return customerID;
    }


    /**
     * Sets the customerID value for this PropaneUtilityInfo.
     * 
     * @param customerID
     */
    public void setCustomerID(java.lang.String customerID) {
        this.customerID = customerID;
    }


    /**
     * Gets the mapLocation value for this PropaneUtilityInfo.
     * 
     * @return mapLocation
     */
    public com.cannontech.multispeak.deploy.service.PointType getMapLocation() {
        return mapLocation;
    }


    /**
     * Sets the mapLocation value for this PropaneUtilityInfo.
     * 
     * @param mapLocation
     */
    public void setMapLocation(com.cannontech.multispeak.deploy.service.PointType mapLocation) {
        this.mapLocation = mapLocation;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PropaneUtilityInfo)) return false;
        PropaneUtilityInfo other = (PropaneUtilityInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.owner==null && other.getOwner()==null) || 
             (this.owner!=null &&
              this.owner.equals(other.getOwner()))) &&
            ((this.district==null && other.getDistrict()==null) || 
             (this.district!=null &&
              this.district.equals(other.getDistrict()))) &&
            ((this.serviceLocationID==null && other.getServiceLocationID()==null) || 
             (this.serviceLocationID!=null &&
              this.serviceLocationID.equals(other.getServiceLocationID()))) &&
            ((this.accountNumber==null && other.getAccountNumber()==null) || 
             (this.accountNumber!=null &&
              this.accountNumber.equals(other.getAccountNumber()))) &&
            ((this.customerID==null && other.getCustomerID()==null) || 
             (this.customerID!=null &&
              this.customerID.equals(other.getCustomerID()))) &&
            ((this.mapLocation==null && other.getMapLocation()==null) || 
             (this.mapLocation!=null &&
              this.mapLocation.equals(other.getMapLocation())));
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
        if (getOwner() != null) {
            _hashCode += getOwner().hashCode();
        }
        if (getDistrict() != null) {
            _hashCode += getDistrict().hashCode();
        }
        if (getServiceLocationID() != null) {
            _hashCode += getServiceLocationID().hashCode();
        }
        if (getAccountNumber() != null) {
            _hashCode += getAccountNumber().hashCode();
        }
        if (getCustomerID() != null) {
            _hashCode += getCustomerID().hashCode();
        }
        if (getMapLocation() != null) {
            _hashCode += getMapLocation().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PropaneUtilityInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneUtilityInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("owner");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "owner"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("district");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "district"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceLocationID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocationID"));
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
        elemField.setFieldName("customerID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customerID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mapLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mapLocation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "PointType"));
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
