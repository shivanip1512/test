/**
 * Premise.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class Premise  extends com.cannontech.multispeak.deploy.service.MspPointObject  implements java.io.Serializable {
    private java.lang.String description;

    private java.lang.String[] serviceLocationList;

    private java.lang.String owner;

    private java.lang.String parcelID;

    private com.cannontech.multispeak.deploy.service.PremiseService[] premiseServiceList;

    public Premise() {
    }

    public Premise(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           com.cannontech.multispeak.deploy.service.PointType mapLocation,
           java.lang.String gridLocation,
           java.lang.Float rotation,
           java.lang.String facilityID,
           java.lang.String description,
           java.lang.String[] serviceLocationList,
           java.lang.String owner,
           java.lang.String parcelID,
           com.cannontech.multispeak.deploy.service.PremiseService[] premiseServiceList) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList,
            mapLocation,
            gridLocation,
            rotation,
            facilityID);
        this.description = description;
        this.serviceLocationList = serviceLocationList;
        this.owner = owner;
        this.parcelID = parcelID;
        this.premiseServiceList = premiseServiceList;
    }


    /**
     * Gets the description value for this Premise.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this Premise.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the serviceLocationList value for this Premise.
     * 
     * @return serviceLocationList
     */
    public java.lang.String[] getServiceLocationList() {
        return serviceLocationList;
    }


    /**
     * Sets the serviceLocationList value for this Premise.
     * 
     * @param serviceLocationList
     */
    public void setServiceLocationList(java.lang.String[] serviceLocationList) {
        this.serviceLocationList = serviceLocationList;
    }


    /**
     * Gets the owner value for this Premise.
     * 
     * @return owner
     */
    public java.lang.String getOwner() {
        return owner;
    }


    /**
     * Sets the owner value for this Premise.
     * 
     * @param owner
     */
    public void setOwner(java.lang.String owner) {
        this.owner = owner;
    }


    /**
     * Gets the parcelID value for this Premise.
     * 
     * @return parcelID
     */
    public java.lang.String getParcelID() {
        return parcelID;
    }


    /**
     * Sets the parcelID value for this Premise.
     * 
     * @param parcelID
     */
    public void setParcelID(java.lang.String parcelID) {
        this.parcelID = parcelID;
    }


    /**
     * Gets the premiseServiceList value for this Premise.
     * 
     * @return premiseServiceList
     */
    public com.cannontech.multispeak.deploy.service.PremiseService[] getPremiseServiceList() {
        return premiseServiceList;
    }


    /**
     * Sets the premiseServiceList value for this Premise.
     * 
     * @param premiseServiceList
     */
    public void setPremiseServiceList(com.cannontech.multispeak.deploy.service.PremiseService[] premiseServiceList) {
        this.premiseServiceList = premiseServiceList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Premise)) return false;
        Premise other = (Premise) obj;
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
            ((this.serviceLocationList==null && other.getServiceLocationList()==null) || 
             (this.serviceLocationList!=null &&
              java.util.Arrays.equals(this.serviceLocationList, other.getServiceLocationList()))) &&
            ((this.owner==null && other.getOwner()==null) || 
             (this.owner!=null &&
              this.owner.equals(other.getOwner()))) &&
            ((this.parcelID==null && other.getParcelID()==null) || 
             (this.parcelID!=null &&
              this.parcelID.equals(other.getParcelID()))) &&
            ((this.premiseServiceList==null && other.getPremiseServiceList()==null) || 
             (this.premiseServiceList!=null &&
              java.util.Arrays.equals(this.premiseServiceList, other.getPremiseServiceList())));
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
        if (getServiceLocationList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getServiceLocationList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getServiceLocationList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getOwner() != null) {
            _hashCode += getOwner().hashCode();
        }
        if (getParcelID() != null) {
            _hashCode += getParcelID().hashCode();
        }
        if (getPremiseServiceList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPremiseServiceList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPremiseServiceList(), i);
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
        new org.apache.axis.description.TypeDesc(Premise.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "premise"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceLocationList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocationList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servLoc"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("owner");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "owner"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("parcelID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "parcelID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("premiseServiceList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "premiseServiceList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "premiseService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "premiseService"));
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
