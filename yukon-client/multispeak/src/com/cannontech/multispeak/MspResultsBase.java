/**
 * MspResultsBase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public abstract class MspResultsBase  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private com.cannontech.multispeak.ResultsType resultsType;
    private java.lang.String source;
    private java.lang.String feeder;
    private com.cannontech.multispeak.EaLoc eaLoc;
    private com.cannontech.multispeak.ObjectRef parentID;
    private com.cannontech.multispeak.PhaseCd phaseCode;
    private com.cannontech.multispeak.LdCon loadCon;
    private java.lang.String equipRef;
    private java.lang.Float milesFromSrc;
    private java.lang.Float sectionLength;
    private java.lang.Float baseKvA;
    private java.lang.Float baseKvB;
    private java.lang.Float baseKvC;
    private java.lang.Float baseKvBal;
    private java.lang.Float basePower;

    public MspResultsBase() {
    }

    public MspResultsBase(
           com.cannontech.multispeak.ResultsType resultsType,
           java.lang.String source,
           java.lang.String feeder,
           com.cannontech.multispeak.EaLoc eaLoc,
           com.cannontech.multispeak.ObjectRef parentID,
           com.cannontech.multispeak.PhaseCd phaseCode,
           com.cannontech.multispeak.LdCon loadCon,
           java.lang.String equipRef,
           java.lang.Float milesFromSrc,
           java.lang.Float sectionLength,
           java.lang.Float baseKvA,
           java.lang.Float baseKvB,
           java.lang.Float baseKvC,
           java.lang.Float baseKvBal,
           java.lang.Float basePower) {
           this.resultsType = resultsType;
           this.source = source;
           this.feeder = feeder;
           this.eaLoc = eaLoc;
           this.parentID = parentID;
           this.phaseCode = phaseCode;
           this.loadCon = loadCon;
           this.equipRef = equipRef;
           this.milesFromSrc = milesFromSrc;
           this.sectionLength = sectionLength;
           this.baseKvA = baseKvA;
           this.baseKvB = baseKvB;
           this.baseKvC = baseKvC;
           this.baseKvBal = baseKvBal;
           this.basePower = basePower;
    }


    /**
     * Gets the resultsType value for this MspResultsBase.
     * 
     * @return resultsType
     */
    public com.cannontech.multispeak.ResultsType getResultsType() {
        return resultsType;
    }


    /**
     * Sets the resultsType value for this MspResultsBase.
     * 
     * @param resultsType
     */
    public void setResultsType(com.cannontech.multispeak.ResultsType resultsType) {
        this.resultsType = resultsType;
    }


    /**
     * Gets the source value for this MspResultsBase.
     * 
     * @return source
     */
    public java.lang.String getSource() {
        return source;
    }


    /**
     * Sets the source value for this MspResultsBase.
     * 
     * @param source
     */
    public void setSource(java.lang.String source) {
        this.source = source;
    }


    /**
     * Gets the feeder value for this MspResultsBase.
     * 
     * @return feeder
     */
    public java.lang.String getFeeder() {
        return feeder;
    }


    /**
     * Sets the feeder value for this MspResultsBase.
     * 
     * @param feeder
     */
    public void setFeeder(java.lang.String feeder) {
        this.feeder = feeder;
    }


    /**
     * Gets the eaLoc value for this MspResultsBase.
     * 
     * @return eaLoc
     */
    public com.cannontech.multispeak.EaLoc getEaLoc() {
        return eaLoc;
    }


    /**
     * Sets the eaLoc value for this MspResultsBase.
     * 
     * @param eaLoc
     */
    public void setEaLoc(com.cannontech.multispeak.EaLoc eaLoc) {
        this.eaLoc = eaLoc;
    }


    /**
     * Gets the parentID value for this MspResultsBase.
     * 
     * @return parentID
     */
    public com.cannontech.multispeak.ObjectRef getParentID() {
        return parentID;
    }


    /**
     * Sets the parentID value for this MspResultsBase.
     * 
     * @param parentID
     */
    public void setParentID(com.cannontech.multispeak.ObjectRef parentID) {
        this.parentID = parentID;
    }


    /**
     * Gets the phaseCode value for this MspResultsBase.
     * 
     * @return phaseCode
     */
    public com.cannontech.multispeak.PhaseCd getPhaseCode() {
        return phaseCode;
    }


    /**
     * Sets the phaseCode value for this MspResultsBase.
     * 
     * @param phaseCode
     */
    public void setPhaseCode(com.cannontech.multispeak.PhaseCd phaseCode) {
        this.phaseCode = phaseCode;
    }


    /**
     * Gets the loadCon value for this MspResultsBase.
     * 
     * @return loadCon
     */
    public com.cannontech.multispeak.LdCon getLoadCon() {
        return loadCon;
    }


    /**
     * Sets the loadCon value for this MspResultsBase.
     * 
     * @param loadCon
     */
    public void setLoadCon(com.cannontech.multispeak.LdCon loadCon) {
        this.loadCon = loadCon;
    }


    /**
     * Gets the equipRef value for this MspResultsBase.
     * 
     * @return equipRef
     */
    public java.lang.String getEquipRef() {
        return equipRef;
    }


    /**
     * Sets the equipRef value for this MspResultsBase.
     * 
     * @param equipRef
     */
    public void setEquipRef(java.lang.String equipRef) {
        this.equipRef = equipRef;
    }


    /**
     * Gets the milesFromSrc value for this MspResultsBase.
     * 
     * @return milesFromSrc
     */
    public java.lang.Float getMilesFromSrc() {
        return milesFromSrc;
    }


    /**
     * Sets the milesFromSrc value for this MspResultsBase.
     * 
     * @param milesFromSrc
     */
    public void setMilesFromSrc(java.lang.Float milesFromSrc) {
        this.milesFromSrc = milesFromSrc;
    }


    /**
     * Gets the sectionLength value for this MspResultsBase.
     * 
     * @return sectionLength
     */
    public java.lang.Float getSectionLength() {
        return sectionLength;
    }


    /**
     * Sets the sectionLength value for this MspResultsBase.
     * 
     * @param sectionLength
     */
    public void setSectionLength(java.lang.Float sectionLength) {
        this.sectionLength = sectionLength;
    }


    /**
     * Gets the baseKvA value for this MspResultsBase.
     * 
     * @return baseKvA
     */
    public java.lang.Float getBaseKvA() {
        return baseKvA;
    }


    /**
     * Sets the baseKvA value for this MspResultsBase.
     * 
     * @param baseKvA
     */
    public void setBaseKvA(java.lang.Float baseKvA) {
        this.baseKvA = baseKvA;
    }


    /**
     * Gets the baseKvB value for this MspResultsBase.
     * 
     * @return baseKvB
     */
    public java.lang.Float getBaseKvB() {
        return baseKvB;
    }


    /**
     * Sets the baseKvB value for this MspResultsBase.
     * 
     * @param baseKvB
     */
    public void setBaseKvB(java.lang.Float baseKvB) {
        this.baseKvB = baseKvB;
    }


    /**
     * Gets the baseKvC value for this MspResultsBase.
     * 
     * @return baseKvC
     */
    public java.lang.Float getBaseKvC() {
        return baseKvC;
    }


    /**
     * Sets the baseKvC value for this MspResultsBase.
     * 
     * @param baseKvC
     */
    public void setBaseKvC(java.lang.Float baseKvC) {
        this.baseKvC = baseKvC;
    }


    /**
     * Gets the baseKvBal value for this MspResultsBase.
     * 
     * @return baseKvBal
     */
    public java.lang.Float getBaseKvBal() {
        return baseKvBal;
    }


    /**
     * Sets the baseKvBal value for this MspResultsBase.
     * 
     * @param baseKvBal
     */
    public void setBaseKvBal(java.lang.Float baseKvBal) {
        this.baseKvBal = baseKvBal;
    }


    /**
     * Gets the basePower value for this MspResultsBase.
     * 
     * @return basePower
     */
    public java.lang.Float getBasePower() {
        return basePower;
    }


    /**
     * Sets the basePower value for this MspResultsBase.
     * 
     * @param basePower
     */
    public void setBasePower(java.lang.Float basePower) {
        this.basePower = basePower;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MspResultsBase)) return false;
        MspResultsBase other = (MspResultsBase) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.resultsType==null && other.getResultsType()==null) || 
             (this.resultsType!=null &&
              this.resultsType.equals(other.getResultsType()))) &&
            ((this.source==null && other.getSource()==null) || 
             (this.source!=null &&
              this.source.equals(other.getSource()))) &&
            ((this.feeder==null && other.getFeeder()==null) || 
             (this.feeder!=null &&
              this.feeder.equals(other.getFeeder()))) &&
            ((this.eaLoc==null && other.getEaLoc()==null) || 
             (this.eaLoc!=null &&
              this.eaLoc.equals(other.getEaLoc()))) &&
            ((this.parentID==null && other.getParentID()==null) || 
             (this.parentID!=null &&
              this.parentID.equals(other.getParentID()))) &&
            ((this.phaseCode==null && other.getPhaseCode()==null) || 
             (this.phaseCode!=null &&
              this.phaseCode.equals(other.getPhaseCode()))) &&
            ((this.loadCon==null && other.getLoadCon()==null) || 
             (this.loadCon!=null &&
              this.loadCon.equals(other.getLoadCon()))) &&
            ((this.equipRef==null && other.getEquipRef()==null) || 
             (this.equipRef!=null &&
              this.equipRef.equals(other.getEquipRef()))) &&
            ((this.milesFromSrc==null && other.getMilesFromSrc()==null) || 
             (this.milesFromSrc!=null &&
              this.milesFromSrc.equals(other.getMilesFromSrc()))) &&
            ((this.sectionLength==null && other.getSectionLength()==null) || 
             (this.sectionLength!=null &&
              this.sectionLength.equals(other.getSectionLength()))) &&
            ((this.baseKvA==null && other.getBaseKvA()==null) || 
             (this.baseKvA!=null &&
              this.baseKvA.equals(other.getBaseKvA()))) &&
            ((this.baseKvB==null && other.getBaseKvB()==null) || 
             (this.baseKvB!=null &&
              this.baseKvB.equals(other.getBaseKvB()))) &&
            ((this.baseKvC==null && other.getBaseKvC()==null) || 
             (this.baseKvC!=null &&
              this.baseKvC.equals(other.getBaseKvC()))) &&
            ((this.baseKvBal==null && other.getBaseKvBal()==null) || 
             (this.baseKvBal!=null &&
              this.baseKvBal.equals(other.getBaseKvBal()))) &&
            ((this.basePower==null && other.getBasePower()==null) || 
             (this.basePower!=null &&
              this.basePower.equals(other.getBasePower())));
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
        if (getResultsType() != null) {
            _hashCode += getResultsType().hashCode();
        }
        if (getSource() != null) {
            _hashCode += getSource().hashCode();
        }
        if (getFeeder() != null) {
            _hashCode += getFeeder().hashCode();
        }
        if (getEaLoc() != null) {
            _hashCode += getEaLoc().hashCode();
        }
        if (getParentID() != null) {
            _hashCode += getParentID().hashCode();
        }
        if (getPhaseCode() != null) {
            _hashCode += getPhaseCode().hashCode();
        }
        if (getLoadCon() != null) {
            _hashCode += getLoadCon().hashCode();
        }
        if (getEquipRef() != null) {
            _hashCode += getEquipRef().hashCode();
        }
        if (getMilesFromSrc() != null) {
            _hashCode += getMilesFromSrc().hashCode();
        }
        if (getSectionLength() != null) {
            _hashCode += getSectionLength().hashCode();
        }
        if (getBaseKvA() != null) {
            _hashCode += getBaseKvA().hashCode();
        }
        if (getBaseKvB() != null) {
            _hashCode += getBaseKvB().hashCode();
        }
        if (getBaseKvC() != null) {
            _hashCode += getBaseKvC().hashCode();
        }
        if (getBaseKvBal() != null) {
            _hashCode += getBaseKvBal().hashCode();
        }
        if (getBasePower() != null) {
            _hashCode += getBasePower().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MspResultsBase.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspResultsBase"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultsType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "resultsType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "resultsType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("source");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "source"));
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
        elemField.setFieldName("eaLoc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eaLoc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eaLoc"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("parentID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "parentID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "objectRef"));
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
        elemField.setFieldName("loadCon");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadCon"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ldCon"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("equipRef");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "equipRef"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("milesFromSrc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "milesFromSrc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sectionLength");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sectionLength"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("baseKvA");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "baseKvA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("baseKvB");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "baseKvB"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("baseKvC");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "baseKvC"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("baseKvBal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "baseKvBal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("basePower");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "basePower"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
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
