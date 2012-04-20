/**
 * ElectricServiceLocation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ElectricServiceLocation  extends com.cannontech.multispeak.deploy.service.MspServiceLocation  implements java.io.Serializable {
    private java.lang.String sectionID;

    private com.cannontech.multispeak.deploy.service.ObjectRef parentSectionID;

    private com.cannontech.multispeak.deploy.service.NodeIdentifier toNodeID;

    private com.cannontech.multispeak.deploy.service.NodeIdentifier fromNodeID;

    private com.cannontech.multispeak.deploy.service.PowerStatus outageStatus;

    private java.lang.String specialNeeds;

    private java.lang.String loadMgmt;

    private java.lang.Boolean isCogenerationSite;

    private com.cannontech.multispeak.deploy.service.PhaseCd phaseCode;

    private com.cannontech.multispeak.deploy.service.MspLoadGroup load;

    public ElectricServiceLocation() {
    }

    public ElectricServiceLocation(
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
           com.cannontech.multispeak.deploy.service.GraphicSymbol[] graphicSymbol,
           com.cannontech.multispeak.deploy.service.GenericAnnotationFeature[] annotationList,
           java.lang.String custID,
           java.lang.String accountNumber,
           java.lang.String facilityName,
           java.lang.String siteID,
           com.cannontech.multispeak.deploy.service.Address serviceAddress,
           java.lang.String revenueClass,
           java.lang.String servStatus,
           java.lang.String billingCycle,
           java.lang.String route,
           java.lang.String budgBill,
           java.lang.Float acRecvBal,
           java.lang.Float acRecvCur,
           java.lang.Float acRecv30,
           java.lang.Float acRecv60,
           java.lang.Float acRecv90,
           java.util.Calendar paymentDueDate,
           java.util.Calendar lastPaymentDate,
           java.lang.Float lastPaymentAmount,
           java.util.Calendar billDate,
           java.util.Calendar shutOffDate,
           java.util.Calendar connectDate,
           java.util.Calendar disconnectDate,
           com.cannontech.multispeak.deploy.service.MspNetwork network,
           java.lang.String SIC,
           java.lang.String woNumber,
           java.lang.String soNumber,
           java.lang.String sectionID,
           com.cannontech.multispeak.deploy.service.ObjectRef parentSectionID,
           com.cannontech.multispeak.deploy.service.NodeIdentifier toNodeID,
           com.cannontech.multispeak.deploy.service.NodeIdentifier fromNodeID,
           com.cannontech.multispeak.deploy.service.PowerStatus outageStatus,
           java.lang.String specialNeeds,
           java.lang.String loadMgmt,
           java.lang.Boolean isCogenerationSite,
           com.cannontech.multispeak.deploy.service.PhaseCd phaseCode,
           com.cannontech.multispeak.deploy.service.MspLoadGroup load) {
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
            facilityID,
            graphicSymbol,
            annotationList,
            custID,
            accountNumber,
            facilityName,
            siteID,
            serviceAddress,
            revenueClass,
            servStatus,
            billingCycle,
            route,
            budgBill,
            acRecvBal,
            acRecvCur,
            acRecv30,
            acRecv60,
            acRecv90,
            paymentDueDate,
            lastPaymentDate,
            lastPaymentAmount,
            billDate,
            shutOffDate,
            connectDate,
            disconnectDate,
            network,
            SIC,
            woNumber,
            soNumber);
        this.sectionID = sectionID;
        this.parentSectionID = parentSectionID;
        this.toNodeID = toNodeID;
        this.fromNodeID = fromNodeID;
        this.outageStatus = outageStatus;
        this.specialNeeds = specialNeeds;
        this.loadMgmt = loadMgmt;
        this.isCogenerationSite = isCogenerationSite;
        this.phaseCode = phaseCode;
        this.load = load;
    }


    /**
     * Gets the sectionID value for this ElectricServiceLocation.
     * 
     * @return sectionID
     */
    public java.lang.String getSectionID() {
        return sectionID;
    }


    /**
     * Sets the sectionID value for this ElectricServiceLocation.
     * 
     * @param sectionID
     */
    public void setSectionID(java.lang.String sectionID) {
        this.sectionID = sectionID;
    }


    /**
     * Gets the parentSectionID value for this ElectricServiceLocation.
     * 
     * @return parentSectionID
     */
    public com.cannontech.multispeak.deploy.service.ObjectRef getParentSectionID() {
        return parentSectionID;
    }


    /**
     * Sets the parentSectionID value for this ElectricServiceLocation.
     * 
     * @param parentSectionID
     */
    public void setParentSectionID(com.cannontech.multispeak.deploy.service.ObjectRef parentSectionID) {
        this.parentSectionID = parentSectionID;
    }


    /**
     * Gets the toNodeID value for this ElectricServiceLocation.
     * 
     * @return toNodeID
     */
    public com.cannontech.multispeak.deploy.service.NodeIdentifier getToNodeID() {
        return toNodeID;
    }


    /**
     * Sets the toNodeID value for this ElectricServiceLocation.
     * 
     * @param toNodeID
     */
    public void setToNodeID(com.cannontech.multispeak.deploy.service.NodeIdentifier toNodeID) {
        this.toNodeID = toNodeID;
    }


    /**
     * Gets the fromNodeID value for this ElectricServiceLocation.
     * 
     * @return fromNodeID
     */
    public com.cannontech.multispeak.deploy.service.NodeIdentifier getFromNodeID() {
        return fromNodeID;
    }


    /**
     * Sets the fromNodeID value for this ElectricServiceLocation.
     * 
     * @param fromNodeID
     */
    public void setFromNodeID(com.cannontech.multispeak.deploy.service.NodeIdentifier fromNodeID) {
        this.fromNodeID = fromNodeID;
    }


    /**
     * Gets the outageStatus value for this ElectricServiceLocation.
     * 
     * @return outageStatus
     */
    public com.cannontech.multispeak.deploy.service.PowerStatus getOutageStatus() {
        return outageStatus;
    }


    /**
     * Sets the outageStatus value for this ElectricServiceLocation.
     * 
     * @param outageStatus
     */
    public void setOutageStatus(com.cannontech.multispeak.deploy.service.PowerStatus outageStatus) {
        this.outageStatus = outageStatus;
    }


    /**
     * Gets the specialNeeds value for this ElectricServiceLocation.
     * 
     * @return specialNeeds
     */
    public java.lang.String getSpecialNeeds() {
        return specialNeeds;
    }


    /**
     * Sets the specialNeeds value for this ElectricServiceLocation.
     * 
     * @param specialNeeds
     */
    public void setSpecialNeeds(java.lang.String specialNeeds) {
        this.specialNeeds = specialNeeds;
    }


    /**
     * Gets the loadMgmt value for this ElectricServiceLocation.
     * 
     * @return loadMgmt
     */
    public java.lang.String getLoadMgmt() {
        return loadMgmt;
    }


    /**
     * Sets the loadMgmt value for this ElectricServiceLocation.
     * 
     * @param loadMgmt
     */
    public void setLoadMgmt(java.lang.String loadMgmt) {
        this.loadMgmt = loadMgmt;
    }


    /**
     * Gets the isCogenerationSite value for this ElectricServiceLocation.
     * 
     * @return isCogenerationSite
     */
    public java.lang.Boolean getIsCogenerationSite() {
        return isCogenerationSite;
    }


    /**
     * Sets the isCogenerationSite value for this ElectricServiceLocation.
     * 
     * @param isCogenerationSite
     */
    public void setIsCogenerationSite(java.lang.Boolean isCogenerationSite) {
        this.isCogenerationSite = isCogenerationSite;
    }


    /**
     * Gets the phaseCode value for this ElectricServiceLocation.
     * 
     * @return phaseCode
     */
    public com.cannontech.multispeak.deploy.service.PhaseCd getPhaseCode() {
        return phaseCode;
    }


    /**
     * Sets the phaseCode value for this ElectricServiceLocation.
     * 
     * @param phaseCode
     */
    public void setPhaseCode(com.cannontech.multispeak.deploy.service.PhaseCd phaseCode) {
        this.phaseCode = phaseCode;
    }


    /**
     * Gets the load value for this ElectricServiceLocation.
     * 
     * @return load
     */
    public com.cannontech.multispeak.deploy.service.MspLoadGroup getLoad() {
        return load;
    }


    /**
     * Sets the load value for this ElectricServiceLocation.
     * 
     * @param load
     */
    public void setLoad(com.cannontech.multispeak.deploy.service.MspLoadGroup load) {
        this.load = load;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ElectricServiceLocation)) return false;
        ElectricServiceLocation other = (ElectricServiceLocation) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.sectionID==null && other.getSectionID()==null) || 
             (this.sectionID!=null &&
              this.sectionID.equals(other.getSectionID()))) &&
            ((this.parentSectionID==null && other.getParentSectionID()==null) || 
             (this.parentSectionID!=null &&
              this.parentSectionID.equals(other.getParentSectionID()))) &&
            ((this.toNodeID==null && other.getToNodeID()==null) || 
             (this.toNodeID!=null &&
              this.toNodeID.equals(other.getToNodeID()))) &&
            ((this.fromNodeID==null && other.getFromNodeID()==null) || 
             (this.fromNodeID!=null &&
              this.fromNodeID.equals(other.getFromNodeID()))) &&
            ((this.outageStatus==null && other.getOutageStatus()==null) || 
             (this.outageStatus!=null &&
              this.outageStatus.equals(other.getOutageStatus()))) &&
            ((this.specialNeeds==null && other.getSpecialNeeds()==null) || 
             (this.specialNeeds!=null &&
              this.specialNeeds.equals(other.getSpecialNeeds()))) &&
            ((this.loadMgmt==null && other.getLoadMgmt()==null) || 
             (this.loadMgmt!=null &&
              this.loadMgmt.equals(other.getLoadMgmt()))) &&
            ((this.isCogenerationSite==null && other.getIsCogenerationSite()==null) || 
             (this.isCogenerationSite!=null &&
              this.isCogenerationSite.equals(other.getIsCogenerationSite()))) &&
            ((this.phaseCode==null && other.getPhaseCode()==null) || 
             (this.phaseCode!=null &&
              this.phaseCode.equals(other.getPhaseCode()))) &&
            ((this.load==null && other.getLoad()==null) || 
             (this.load!=null &&
              this.load.equals(other.getLoad())));
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
        if (getSectionID() != null) {
            _hashCode += getSectionID().hashCode();
        }
        if (getParentSectionID() != null) {
            _hashCode += getParentSectionID().hashCode();
        }
        if (getToNodeID() != null) {
            _hashCode += getToNodeID().hashCode();
        }
        if (getFromNodeID() != null) {
            _hashCode += getFromNodeID().hashCode();
        }
        if (getOutageStatus() != null) {
            _hashCode += getOutageStatus().hashCode();
        }
        if (getSpecialNeeds() != null) {
            _hashCode += getSpecialNeeds().hashCode();
        }
        if (getLoadMgmt() != null) {
            _hashCode += getLoadMgmt().hashCode();
        }
        if (getIsCogenerationSite() != null) {
            _hashCode += getIsCogenerationSite().hashCode();
        }
        if (getPhaseCode() != null) {
            _hashCode += getPhaseCode().hashCode();
        }
        if (getLoad() != null) {
            _hashCode += getLoad().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ElectricServiceLocation.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricServiceLocation"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sectionID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sectionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("parentSectionID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "parentSectionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "objectRef"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("toNodeID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "toNodeID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "nodeIdentifier"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fromNodeID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fromNodeID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "nodeIdentifier"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "powerStatus"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("specialNeeds");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "specialNeeds"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loadMgmt");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadMgmt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isCogenerationSite");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "isCogenerationSite"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phaseCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("load");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "load"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspLoadGroup"));
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
