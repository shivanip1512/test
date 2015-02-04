/**
 * Crew.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class Crew  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.lang.String foreman;

    private com.cannontech.multispeak.deploy.service.ContactInfo contactInfo;

    private java.lang.Boolean isForeign;

    private java.lang.String baseLocation;

    private java.lang.String crewType;

    private com.cannontech.multispeak.deploy.service.Truck[] truckList;

    private com.cannontech.multispeak.deploy.service.Employee[] employeeList;

    private com.cannontech.multispeak.deploy.service.Equipment[] equipmentList;

    private java.lang.Boolean isActive;

    public Crew() {
    }

    public Crew(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String foreman,
           com.cannontech.multispeak.deploy.service.ContactInfo contactInfo,
           java.lang.Boolean isForeign,
           java.lang.String baseLocation,
           java.lang.String crewType,
           com.cannontech.multispeak.deploy.service.Truck[] truckList,
           com.cannontech.multispeak.deploy.service.Employee[] employeeList,
           com.cannontech.multispeak.deploy.service.Equipment[] equipmentList,
           java.lang.Boolean isActive) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.foreman = foreman;
        this.contactInfo = contactInfo;
        this.isForeign = isForeign;
        this.baseLocation = baseLocation;
        this.crewType = crewType;
        this.truckList = truckList;
        this.employeeList = employeeList;
        this.equipmentList = equipmentList;
        this.isActive = isActive;
    }


    /**
     * Gets the foreman value for this Crew.
     * 
     * @return foreman
     */
    public java.lang.String getForeman() {
        return foreman;
    }


    /**
     * Sets the foreman value for this Crew.
     * 
     * @param foreman
     */
    public void setForeman(java.lang.String foreman) {
        this.foreman = foreman;
    }


    /**
     * Gets the contactInfo value for this Crew.
     * 
     * @return contactInfo
     */
    public com.cannontech.multispeak.deploy.service.ContactInfo getContactInfo() {
        return contactInfo;
    }


    /**
     * Sets the contactInfo value for this Crew.
     * 
     * @param contactInfo
     */
    public void setContactInfo(com.cannontech.multispeak.deploy.service.ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }


    /**
     * Gets the isForeign value for this Crew.
     * 
     * @return isForeign
     */
    public java.lang.Boolean getIsForeign() {
        return isForeign;
    }


    /**
     * Sets the isForeign value for this Crew.
     * 
     * @param isForeign
     */
    public void setIsForeign(java.lang.Boolean isForeign) {
        this.isForeign = isForeign;
    }


    /**
     * Gets the baseLocation value for this Crew.
     * 
     * @return baseLocation
     */
    public java.lang.String getBaseLocation() {
        return baseLocation;
    }


    /**
     * Sets the baseLocation value for this Crew.
     * 
     * @param baseLocation
     */
    public void setBaseLocation(java.lang.String baseLocation) {
        this.baseLocation = baseLocation;
    }


    /**
     * Gets the crewType value for this Crew.
     * 
     * @return crewType
     */
    public java.lang.String getCrewType() {
        return crewType;
    }


    /**
     * Sets the crewType value for this Crew.
     * 
     * @param crewType
     */
    public void setCrewType(java.lang.String crewType) {
        this.crewType = crewType;
    }


    /**
     * Gets the truckList value for this Crew.
     * 
     * @return truckList
     */
    public com.cannontech.multispeak.deploy.service.Truck[] getTruckList() {
        return truckList;
    }


    /**
     * Sets the truckList value for this Crew.
     * 
     * @param truckList
     */
    public void setTruckList(com.cannontech.multispeak.deploy.service.Truck[] truckList) {
        this.truckList = truckList;
    }


    /**
     * Gets the employeeList value for this Crew.
     * 
     * @return employeeList
     */
    public com.cannontech.multispeak.deploy.service.Employee[] getEmployeeList() {
        return employeeList;
    }


    /**
     * Sets the employeeList value for this Crew.
     * 
     * @param employeeList
     */
    public void setEmployeeList(com.cannontech.multispeak.deploy.service.Employee[] employeeList) {
        this.employeeList = employeeList;
    }


    /**
     * Gets the equipmentList value for this Crew.
     * 
     * @return equipmentList
     */
    public com.cannontech.multispeak.deploy.service.Equipment[] getEquipmentList() {
        return equipmentList;
    }


    /**
     * Sets the equipmentList value for this Crew.
     * 
     * @param equipmentList
     */
    public void setEquipmentList(com.cannontech.multispeak.deploy.service.Equipment[] equipmentList) {
        this.equipmentList = equipmentList;
    }


    /**
     * Gets the isActive value for this Crew.
     * 
     * @return isActive
     */
    public java.lang.Boolean getIsActive() {
        return isActive;
    }


    /**
     * Sets the isActive value for this Crew.
     * 
     * @param isActive
     */
    public void setIsActive(java.lang.Boolean isActive) {
        this.isActive = isActive;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Crew)) return false;
        Crew other = (Crew) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.foreman==null && other.getForeman()==null) || 
             (this.foreman!=null &&
              this.foreman.equals(other.getForeman()))) &&
            ((this.contactInfo==null && other.getContactInfo()==null) || 
             (this.contactInfo!=null &&
              this.contactInfo.equals(other.getContactInfo()))) &&
            ((this.isForeign==null && other.getIsForeign()==null) || 
             (this.isForeign!=null &&
              this.isForeign.equals(other.getIsForeign()))) &&
            ((this.baseLocation==null && other.getBaseLocation()==null) || 
             (this.baseLocation!=null &&
              this.baseLocation.equals(other.getBaseLocation()))) &&
            ((this.crewType==null && other.getCrewType()==null) || 
             (this.crewType!=null &&
              this.crewType.equals(other.getCrewType()))) &&
            ((this.truckList==null && other.getTruckList()==null) || 
             (this.truckList!=null &&
              java.util.Arrays.equals(this.truckList, other.getTruckList()))) &&
            ((this.employeeList==null && other.getEmployeeList()==null) || 
             (this.employeeList!=null &&
              java.util.Arrays.equals(this.employeeList, other.getEmployeeList()))) &&
            ((this.equipmentList==null && other.getEquipmentList()==null) || 
             (this.equipmentList!=null &&
              java.util.Arrays.equals(this.equipmentList, other.getEquipmentList()))) &&
            ((this.isActive==null && other.getIsActive()==null) || 
             (this.isActive!=null &&
              this.isActive.equals(other.getIsActive())));
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
        if (getForeman() != null) {
            _hashCode += getForeman().hashCode();
        }
        if (getContactInfo() != null) {
            _hashCode += getContactInfo().hashCode();
        }
        if (getIsForeign() != null) {
            _hashCode += getIsForeign().hashCode();
        }
        if (getBaseLocation() != null) {
            _hashCode += getBaseLocation().hashCode();
        }
        if (getCrewType() != null) {
            _hashCode += getCrewType().hashCode();
        }
        if (getTruckList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTruckList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getTruckList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getEmployeeList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getEmployeeList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getEmployeeList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getEquipmentList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getEquipmentList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getEquipmentList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getIsActive() != null) {
            _hashCode += getIsActive().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Crew.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "crew"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("foreman");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "foreman"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("contactInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "contactInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "contactInfo"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isForeign");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "isForeign"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("baseLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "baseLocation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("crewType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "crewType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("truckList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "truckList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "truck"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "truck"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("employeeList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "employeeList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "employee"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "employee"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("equipmentList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "equipmentList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "equipment"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "equipment"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isActive");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "IsActive"));
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
