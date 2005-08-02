/**
 * Station.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Station  extends com.cannontech.multispeak.MspConnectivityPoint  implements java.io.Serializable {
    private java.lang.String substationCode;
    private java.lang.String feeder;
    private com.cannontech.multispeak.PhaseCd phaseCd;
    private com.cannontech.multispeak.PoleUse poleUse;
    private com.cannontech.multispeak.ArrayOfBackSpan backSpanList;
    private java.lang.String constTyp;
    private com.cannontech.multispeak.StaStatus staStatus;
    private com.cannontech.multispeak.ConstGrade constGrade;
    private java.lang.Float angle;
    private java.lang.Boolean isTap;
    private java.lang.Long terrain;
    private com.cannontech.multispeak.GpsLocation gpsLocation;
    private com.cannontech.multispeak.AssemblyList assemblyList;
    private com.cannontech.multispeak.ArrayOfCPR CPRList;
    private com.cannontech.multispeak.PhysicalObjectList objectList;
    private com.cannontech.multispeak.PickList pickList;

    public Station() {
    }

    public Station(
           java.lang.String substationCode,
           java.lang.String feeder,
           com.cannontech.multispeak.PhaseCd phaseCd,
           com.cannontech.multispeak.PoleUse poleUse,
           com.cannontech.multispeak.ArrayOfBackSpan backSpanList,
           java.lang.String constTyp,
           com.cannontech.multispeak.StaStatus staStatus,
           com.cannontech.multispeak.ConstGrade constGrade,
           java.lang.Float angle,
           java.lang.Boolean isTap,
           java.lang.Long terrain,
           com.cannontech.multispeak.GpsLocation gpsLocation,
           com.cannontech.multispeak.AssemblyList assemblyList,
           com.cannontech.multispeak.ArrayOfCPR CPRList,
           com.cannontech.multispeak.PhysicalObjectList objectList,
           com.cannontech.multispeak.PickList pickList) {
           this.substationCode = substationCode;
           this.feeder = feeder;
           this.phaseCd = phaseCd;
           this.poleUse = poleUse;
           this.backSpanList = backSpanList;
           this.constTyp = constTyp;
           this.staStatus = staStatus;
           this.constGrade = constGrade;
           this.angle = angle;
           this.isTap = isTap;
           this.terrain = terrain;
           this.gpsLocation = gpsLocation;
           this.assemblyList = assemblyList;
           this.CPRList = CPRList;
           this.objectList = objectList;
           this.pickList = pickList;
    }


    /**
     * Gets the substationCode value for this Station.
     * 
     * @return substationCode
     */
    public java.lang.String getSubstationCode() {
        return substationCode;
    }


    /**
     * Sets the substationCode value for this Station.
     * 
     * @param substationCode
     */
    public void setSubstationCode(java.lang.String substationCode) {
        this.substationCode = substationCode;
    }


    /**
     * Gets the feeder value for this Station.
     * 
     * @return feeder
     */
    public java.lang.String getFeeder() {
        return feeder;
    }


    /**
     * Sets the feeder value for this Station.
     * 
     * @param feeder
     */
    public void setFeeder(java.lang.String feeder) {
        this.feeder = feeder;
    }


    /**
     * Gets the phaseCd value for this Station.
     * 
     * @return phaseCd
     */
    public com.cannontech.multispeak.PhaseCd getPhaseCd() {
        return phaseCd;
    }


    /**
     * Sets the phaseCd value for this Station.
     * 
     * @param phaseCd
     */
    public void setPhaseCd(com.cannontech.multispeak.PhaseCd phaseCd) {
        this.phaseCd = phaseCd;
    }


    /**
     * Gets the poleUse value for this Station.
     * 
     * @return poleUse
     */
    public com.cannontech.multispeak.PoleUse getPoleUse() {
        return poleUse;
    }


    /**
     * Sets the poleUse value for this Station.
     * 
     * @param poleUse
     */
    public void setPoleUse(com.cannontech.multispeak.PoleUse poleUse) {
        this.poleUse = poleUse;
    }


    /**
     * Gets the backSpanList value for this Station.
     * 
     * @return backSpanList
     */
    public com.cannontech.multispeak.ArrayOfBackSpan getBackSpanList() {
        return backSpanList;
    }


    /**
     * Sets the backSpanList value for this Station.
     * 
     * @param backSpanList
     */
    public void setBackSpanList(com.cannontech.multispeak.ArrayOfBackSpan backSpanList) {
        this.backSpanList = backSpanList;
    }


    /**
     * Gets the constTyp value for this Station.
     * 
     * @return constTyp
     */
    public java.lang.String getConstTyp() {
        return constTyp;
    }


    /**
     * Sets the constTyp value for this Station.
     * 
     * @param constTyp
     */
    public void setConstTyp(java.lang.String constTyp) {
        this.constTyp = constTyp;
    }


    /**
     * Gets the staStatus value for this Station.
     * 
     * @return staStatus
     */
    public com.cannontech.multispeak.StaStatus getStaStatus() {
        return staStatus;
    }


    /**
     * Sets the staStatus value for this Station.
     * 
     * @param staStatus
     */
    public void setStaStatus(com.cannontech.multispeak.StaStatus staStatus) {
        this.staStatus = staStatus;
    }


    /**
     * Gets the constGrade value for this Station.
     * 
     * @return constGrade
     */
    public com.cannontech.multispeak.ConstGrade getConstGrade() {
        return constGrade;
    }


    /**
     * Sets the constGrade value for this Station.
     * 
     * @param constGrade
     */
    public void setConstGrade(com.cannontech.multispeak.ConstGrade constGrade) {
        this.constGrade = constGrade;
    }


    /**
     * Gets the angle value for this Station.
     * 
     * @return angle
     */
    public java.lang.Float getAngle() {
        return angle;
    }


    /**
     * Sets the angle value for this Station.
     * 
     * @param angle
     */
    public void setAngle(java.lang.Float angle) {
        this.angle = angle;
    }


    /**
     * Gets the isTap value for this Station.
     * 
     * @return isTap
     */
    public java.lang.Boolean getIsTap() {
        return isTap;
    }


    /**
     * Sets the isTap value for this Station.
     * 
     * @param isTap
     */
    public void setIsTap(java.lang.Boolean isTap) {
        this.isTap = isTap;
    }


    /**
     * Gets the terrain value for this Station.
     * 
     * @return terrain
     */
    public java.lang.Long getTerrain() {
        return terrain;
    }


    /**
     * Sets the terrain value for this Station.
     * 
     * @param terrain
     */
    public void setTerrain(java.lang.Long terrain) {
        this.terrain = terrain;
    }


    /**
     * Gets the gpsLocation value for this Station.
     * 
     * @return gpsLocation
     */
    public com.cannontech.multispeak.GpsLocation getGpsLocation() {
        return gpsLocation;
    }


    /**
     * Sets the gpsLocation value for this Station.
     * 
     * @param gpsLocation
     */
    public void setGpsLocation(com.cannontech.multispeak.GpsLocation gpsLocation) {
        this.gpsLocation = gpsLocation;
    }


    /**
     * Gets the assemblyList value for this Station.
     * 
     * @return assemblyList
     */
    public com.cannontech.multispeak.AssemblyList getAssemblyList() {
        return assemblyList;
    }


    /**
     * Sets the assemblyList value for this Station.
     * 
     * @param assemblyList
     */
    public void setAssemblyList(com.cannontech.multispeak.AssemblyList assemblyList) {
        this.assemblyList = assemblyList;
    }


    /**
     * Gets the CPRList value for this Station.
     * 
     * @return CPRList
     */
    public com.cannontech.multispeak.ArrayOfCPR getCPRList() {
        return CPRList;
    }


    /**
     * Sets the CPRList value for this Station.
     * 
     * @param CPRList
     */
    public void setCPRList(com.cannontech.multispeak.ArrayOfCPR CPRList) {
        this.CPRList = CPRList;
    }


    /**
     * Gets the objectList value for this Station.
     * 
     * @return objectList
     */
    public com.cannontech.multispeak.PhysicalObjectList getObjectList() {
        return objectList;
    }


    /**
     * Sets the objectList value for this Station.
     * 
     * @param objectList
     */
    public void setObjectList(com.cannontech.multispeak.PhysicalObjectList objectList) {
        this.objectList = objectList;
    }


    /**
     * Gets the pickList value for this Station.
     * 
     * @return pickList
     */
    public com.cannontech.multispeak.PickList getPickList() {
        return pickList;
    }


    /**
     * Sets the pickList value for this Station.
     * 
     * @param pickList
     */
    public void setPickList(com.cannontech.multispeak.PickList pickList) {
        this.pickList = pickList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Station)) return false;
        Station other = (Station) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.substationCode==null && other.getSubstationCode()==null) || 
             (this.substationCode!=null &&
              this.substationCode.equals(other.getSubstationCode()))) &&
            ((this.feeder==null && other.getFeeder()==null) || 
             (this.feeder!=null &&
              this.feeder.equals(other.getFeeder()))) &&
            ((this.phaseCd==null && other.getPhaseCd()==null) || 
             (this.phaseCd!=null &&
              this.phaseCd.equals(other.getPhaseCd()))) &&
            ((this.poleUse==null && other.getPoleUse()==null) || 
             (this.poleUse!=null &&
              this.poleUse.equals(other.getPoleUse()))) &&
            ((this.backSpanList==null && other.getBackSpanList()==null) || 
             (this.backSpanList!=null &&
              this.backSpanList.equals(other.getBackSpanList()))) &&
            ((this.constTyp==null && other.getConstTyp()==null) || 
             (this.constTyp!=null &&
              this.constTyp.equals(other.getConstTyp()))) &&
            ((this.staStatus==null && other.getStaStatus()==null) || 
             (this.staStatus!=null &&
              this.staStatus.equals(other.getStaStatus()))) &&
            ((this.constGrade==null && other.getConstGrade()==null) || 
             (this.constGrade!=null &&
              this.constGrade.equals(other.getConstGrade()))) &&
            ((this.angle==null && other.getAngle()==null) || 
             (this.angle!=null &&
              this.angle.equals(other.getAngle()))) &&
            ((this.isTap==null && other.getIsTap()==null) || 
             (this.isTap!=null &&
              this.isTap.equals(other.getIsTap()))) &&
            ((this.terrain==null && other.getTerrain()==null) || 
             (this.terrain!=null &&
              this.terrain.equals(other.getTerrain()))) &&
            ((this.gpsLocation==null && other.getGpsLocation()==null) || 
             (this.gpsLocation!=null &&
              this.gpsLocation.equals(other.getGpsLocation()))) &&
            ((this.assemblyList==null && other.getAssemblyList()==null) || 
             (this.assemblyList!=null &&
              this.assemblyList.equals(other.getAssemblyList()))) &&
            ((this.CPRList==null && other.getCPRList()==null) || 
             (this.CPRList!=null &&
              this.CPRList.equals(other.getCPRList()))) &&
            ((this.objectList==null && other.getObjectList()==null) || 
             (this.objectList!=null &&
              this.objectList.equals(other.getObjectList()))) &&
            ((this.pickList==null && other.getPickList()==null) || 
             (this.pickList!=null &&
              this.pickList.equals(other.getPickList())));
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
        if (getSubstationCode() != null) {
            _hashCode += getSubstationCode().hashCode();
        }
        if (getFeeder() != null) {
            _hashCode += getFeeder().hashCode();
        }
        if (getPhaseCd() != null) {
            _hashCode += getPhaseCd().hashCode();
        }
        if (getPoleUse() != null) {
            _hashCode += getPoleUse().hashCode();
        }
        if (getBackSpanList() != null) {
            _hashCode += getBackSpanList().hashCode();
        }
        if (getConstTyp() != null) {
            _hashCode += getConstTyp().hashCode();
        }
        if (getStaStatus() != null) {
            _hashCode += getStaStatus().hashCode();
        }
        if (getConstGrade() != null) {
            _hashCode += getConstGrade().hashCode();
        }
        if (getAngle() != null) {
            _hashCode += getAngle().hashCode();
        }
        if (getIsTap() != null) {
            _hashCode += getIsTap().hashCode();
        }
        if (getTerrain() != null) {
            _hashCode += getTerrain().hashCode();
        }
        if (getGpsLocation() != null) {
            _hashCode += getGpsLocation().hashCode();
        }
        if (getAssemblyList() != null) {
            _hashCode += getAssemblyList().hashCode();
        }
        if (getCPRList() != null) {
            _hashCode += getCPRList().hashCode();
        }
        if (getObjectList() != null) {
            _hashCode += getObjectList().hashCode();
        }
        if (getPickList() != null) {
            _hashCode += getPickList().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Station.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "station"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("substationCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "substationCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("feeder");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "feeder"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phaseCd");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("poleUse");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleUse"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleUse"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("backSpanList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "backSpanList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfBackSpan"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("constTyp");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "constTyp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("staStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "staStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "staStatus"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("constGrade");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "constGrade"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "constGrade"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("angle");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "angle"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isTap");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "isTap"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terrain");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "terrain"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gpsLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gpsLocation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gpsLocation"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("assemblyList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assemblyList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assemblyList"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CPRList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CPRList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCPR"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("objectList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "objectList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "physicalObjectList"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pickList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pickList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pickList"));
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
