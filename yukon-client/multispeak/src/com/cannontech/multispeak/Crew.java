/**
 * Crew.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Crew  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.String foreman;
    private java.lang.String contactInfo;
    private java.lang.Boolean isForeign;
    private java.lang.String baseLocation;
    private java.lang.String crewType;
    private com.cannontech.multispeak.ArrayOfTruck truckList;
    private com.cannontech.multispeak.ArrayOfEmployee employeeList;
    private com.cannontech.multispeak.ArrayOfEquipment equipmentList;

    public Crew() {
    }

    public Crew(
           java.lang.String foreman,
           java.lang.String contactInfo,
           java.lang.Boolean isForeign,
           java.lang.String baseLocation,
           java.lang.String crewType,
           com.cannontech.multispeak.ArrayOfTruck truckList,
           com.cannontech.multispeak.ArrayOfEmployee employeeList,
           com.cannontech.multispeak.ArrayOfEquipment equipmentList) {
           this.foreman = foreman;
           this.contactInfo = contactInfo;
           this.isForeign = isForeign;
           this.baseLocation = baseLocation;
           this.crewType = crewType;
           this.truckList = truckList;
           this.employeeList = employeeList;
           this.equipmentList = equipmentList;
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
    public java.lang.String getContactInfo() {
        return contactInfo;
    }


    /**
     * Sets the contactInfo value for this Crew.
     * 
     * @param contactInfo
     */
    public void setContactInfo(java.lang.String contactInfo) {
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
    public com.cannontech.multispeak.ArrayOfTruck getTruckList() {
        return truckList;
    }


    /**
     * Sets the truckList value for this Crew.
     * 
     * @param truckList
     */
    public void setTruckList(com.cannontech.multispeak.ArrayOfTruck truckList) {
        this.truckList = truckList;
    }


    /**
     * Gets the employeeList value for this Crew.
     * 
     * @return employeeList
     */
    public com.cannontech.multispeak.ArrayOfEmployee getEmployeeList() {
        return employeeList;
    }


    /**
     * Sets the employeeList value for this Crew.
     * 
     * @param employeeList
     */
    public void setEmployeeList(com.cannontech.multispeak.ArrayOfEmployee employeeList) {
        this.employeeList = employeeList;
    }


    /**
     * Gets the equipmentList value for this Crew.
     * 
     * @return equipmentList
     */
    public com.cannontech.multispeak.ArrayOfEquipment getEquipmentList() {
        return equipmentList;
    }


    /**
     * Sets the equipmentList value for this Crew.
     * 
     * @param equipmentList
     */
    public void setEquipmentList(com.cannontech.multispeak.ArrayOfEquipment equipmentList) {
        this.equipmentList = equipmentList;
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
              this.truckList.equals(other.getTruckList()))) &&
            ((this.employeeList==null && other.getEmployeeList()==null) || 
             (this.employeeList!=null &&
              this.employeeList.equals(other.getEmployeeList()))) &&
            ((this.equipmentList==null && other.getEquipmentList()==null) || 
             (this.equipmentList!=null &&
              this.equipmentList.equals(other.getEquipmentList())));
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
            _hashCode += getTruckList().hashCode();
        }
        if (getEmployeeList() != null) {
            _hashCode += getEmployeeList().hashCode();
        }
        if (getEquipmentList() != null) {
            _hashCode += getEquipmentList().hashCode();
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
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfTruck"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("employeeList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "employeeList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfEmployee"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("equipmentList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "equipmentList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfEquipment"));
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
