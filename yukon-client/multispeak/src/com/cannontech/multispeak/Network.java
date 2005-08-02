/**
 * Network.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Network  implements java.io.Serializable {
    private java.lang.String boardDist;
    private java.lang.String taxDist;
    private java.lang.String franchiseDist;
    private java.lang.String schoolDist;
    private java.lang.String district;
    private java.lang.String substationCode;
    private java.lang.String feeder;
    private com.cannontech.multispeak.PhaseCd phaseCd;
    private com.cannontech.multispeak.EaLoc eaLoc;
    private java.lang.String poleNo;
    private java.lang.String section;
    private java.lang.String township;
    private java.lang.String range;
    private com.cannontech.multispeak.LinkedTransformer linkedTransformer;

    public Network() {
    }

    public Network(
           java.lang.String boardDist,
           java.lang.String taxDist,
           java.lang.String franchiseDist,
           java.lang.String schoolDist,
           java.lang.String district,
           java.lang.String substationCode,
           java.lang.String feeder,
           com.cannontech.multispeak.PhaseCd phaseCd,
           com.cannontech.multispeak.EaLoc eaLoc,
           java.lang.String poleNo,
           java.lang.String section,
           java.lang.String township,
           java.lang.String range,
           com.cannontech.multispeak.LinkedTransformer linkedTransformer) {
           this.boardDist = boardDist;
           this.taxDist = taxDist;
           this.franchiseDist = franchiseDist;
           this.schoolDist = schoolDist;
           this.district = district;
           this.substationCode = substationCode;
           this.feeder = feeder;
           this.phaseCd = phaseCd;
           this.eaLoc = eaLoc;
           this.poleNo = poleNo;
           this.section = section;
           this.township = township;
           this.range = range;
           this.linkedTransformer = linkedTransformer;
    }


    /**
     * Gets the boardDist value for this Network.
     * 
     * @return boardDist
     */
    public java.lang.String getBoardDist() {
        return boardDist;
    }


    /**
     * Sets the boardDist value for this Network.
     * 
     * @param boardDist
     */
    public void setBoardDist(java.lang.String boardDist) {
        this.boardDist = boardDist;
    }


    /**
     * Gets the taxDist value for this Network.
     * 
     * @return taxDist
     */
    public java.lang.String getTaxDist() {
        return taxDist;
    }


    /**
     * Sets the taxDist value for this Network.
     * 
     * @param taxDist
     */
    public void setTaxDist(java.lang.String taxDist) {
        this.taxDist = taxDist;
    }


    /**
     * Gets the franchiseDist value for this Network.
     * 
     * @return franchiseDist
     */
    public java.lang.String getFranchiseDist() {
        return franchiseDist;
    }


    /**
     * Sets the franchiseDist value for this Network.
     * 
     * @param franchiseDist
     */
    public void setFranchiseDist(java.lang.String franchiseDist) {
        this.franchiseDist = franchiseDist;
    }


    /**
     * Gets the schoolDist value for this Network.
     * 
     * @return schoolDist
     */
    public java.lang.String getSchoolDist() {
        return schoolDist;
    }


    /**
     * Sets the schoolDist value for this Network.
     * 
     * @param schoolDist
     */
    public void setSchoolDist(java.lang.String schoolDist) {
        this.schoolDist = schoolDist;
    }


    /**
     * Gets the district value for this Network.
     * 
     * @return district
     */
    public java.lang.String getDistrict() {
        return district;
    }


    /**
     * Sets the district value for this Network.
     * 
     * @param district
     */
    public void setDistrict(java.lang.String district) {
        this.district = district;
    }


    /**
     * Gets the substationCode value for this Network.
     * 
     * @return substationCode
     */
    public java.lang.String getSubstationCode() {
        return substationCode;
    }


    /**
     * Sets the substationCode value for this Network.
     * 
     * @param substationCode
     */
    public void setSubstationCode(java.lang.String substationCode) {
        this.substationCode = substationCode;
    }


    /**
     * Gets the feeder value for this Network.
     * 
     * @return feeder
     */
    public java.lang.String getFeeder() {
        return feeder;
    }


    /**
     * Sets the feeder value for this Network.
     * 
     * @param feeder
     */
    public void setFeeder(java.lang.String feeder) {
        this.feeder = feeder;
    }


    /**
     * Gets the phaseCd value for this Network.
     * 
     * @return phaseCd
     */
    public com.cannontech.multispeak.PhaseCd getPhaseCd() {
        return phaseCd;
    }


    /**
     * Sets the phaseCd value for this Network.
     * 
     * @param phaseCd
     */
    public void setPhaseCd(com.cannontech.multispeak.PhaseCd phaseCd) {
        this.phaseCd = phaseCd;
    }


    /**
     * Gets the eaLoc value for this Network.
     * 
     * @return eaLoc
     */
    public com.cannontech.multispeak.EaLoc getEaLoc() {
        return eaLoc;
    }


    /**
     * Sets the eaLoc value for this Network.
     * 
     * @param eaLoc
     */
    public void setEaLoc(com.cannontech.multispeak.EaLoc eaLoc) {
        this.eaLoc = eaLoc;
    }


    /**
     * Gets the poleNo value for this Network.
     * 
     * @return poleNo
     */
    public java.lang.String getPoleNo() {
        return poleNo;
    }


    /**
     * Sets the poleNo value for this Network.
     * 
     * @param poleNo
     */
    public void setPoleNo(java.lang.String poleNo) {
        this.poleNo = poleNo;
    }


    /**
     * Gets the section value for this Network.
     * 
     * @return section
     */
    public java.lang.String getSection() {
        return section;
    }


    /**
     * Sets the section value for this Network.
     * 
     * @param section
     */
    public void setSection(java.lang.String section) {
        this.section = section;
    }


    /**
     * Gets the township value for this Network.
     * 
     * @return township
     */
    public java.lang.String getTownship() {
        return township;
    }


    /**
     * Sets the township value for this Network.
     * 
     * @param township
     */
    public void setTownship(java.lang.String township) {
        this.township = township;
    }


    /**
     * Gets the range value for this Network.
     * 
     * @return range
     */
    public java.lang.String getRange() {
        return range;
    }


    /**
     * Sets the range value for this Network.
     * 
     * @param range
     */
    public void setRange(java.lang.String range) {
        this.range = range;
    }


    /**
     * Gets the linkedTransformer value for this Network.
     * 
     * @return linkedTransformer
     */
    public com.cannontech.multispeak.LinkedTransformer getLinkedTransformer() {
        return linkedTransformer;
    }


    /**
     * Sets the linkedTransformer value for this Network.
     * 
     * @param linkedTransformer
     */
    public void setLinkedTransformer(com.cannontech.multispeak.LinkedTransformer linkedTransformer) {
        this.linkedTransformer = linkedTransformer;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Network)) return false;
        Network other = (Network) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.boardDist==null && other.getBoardDist()==null) || 
             (this.boardDist!=null &&
              this.boardDist.equals(other.getBoardDist()))) &&
            ((this.taxDist==null && other.getTaxDist()==null) || 
             (this.taxDist!=null &&
              this.taxDist.equals(other.getTaxDist()))) &&
            ((this.franchiseDist==null && other.getFranchiseDist()==null) || 
             (this.franchiseDist!=null &&
              this.franchiseDist.equals(other.getFranchiseDist()))) &&
            ((this.schoolDist==null && other.getSchoolDist()==null) || 
             (this.schoolDist!=null &&
              this.schoolDist.equals(other.getSchoolDist()))) &&
            ((this.district==null && other.getDistrict()==null) || 
             (this.district!=null &&
              this.district.equals(other.getDistrict()))) &&
            ((this.substationCode==null && other.getSubstationCode()==null) || 
             (this.substationCode!=null &&
              this.substationCode.equals(other.getSubstationCode()))) &&
            ((this.feeder==null && other.getFeeder()==null) || 
             (this.feeder!=null &&
              this.feeder.equals(other.getFeeder()))) &&
            ((this.phaseCd==null && other.getPhaseCd()==null) || 
             (this.phaseCd!=null &&
              this.phaseCd.equals(other.getPhaseCd()))) &&
            ((this.eaLoc==null && other.getEaLoc()==null) || 
             (this.eaLoc!=null &&
              this.eaLoc.equals(other.getEaLoc()))) &&
            ((this.poleNo==null && other.getPoleNo()==null) || 
             (this.poleNo!=null &&
              this.poleNo.equals(other.getPoleNo()))) &&
            ((this.section==null && other.getSection()==null) || 
             (this.section!=null &&
              this.section.equals(other.getSection()))) &&
            ((this.township==null && other.getTownship()==null) || 
             (this.township!=null &&
              this.township.equals(other.getTownship()))) &&
            ((this.range==null && other.getRange()==null) || 
             (this.range!=null &&
              this.range.equals(other.getRange()))) &&
            ((this.linkedTransformer==null && other.getLinkedTransformer()==null) || 
             (this.linkedTransformer!=null &&
              this.linkedTransformer.equals(other.getLinkedTransformer())));
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
        if (getBoardDist() != null) {
            _hashCode += getBoardDist().hashCode();
        }
        if (getTaxDist() != null) {
            _hashCode += getTaxDist().hashCode();
        }
        if (getFranchiseDist() != null) {
            _hashCode += getFranchiseDist().hashCode();
        }
        if (getSchoolDist() != null) {
            _hashCode += getSchoolDist().hashCode();
        }
        if (getDistrict() != null) {
            _hashCode += getDistrict().hashCode();
        }
        if (getSubstationCode() != null) {
            _hashCode += getSubstationCode().hashCode();
        }
        if (getFeeder() != null) {
            _hashCode += getFeeder().hashCode();
        }
        if (getPhaseCd() != null) {
            _hashCode += getPhaseCd().hashCode();
        }
        if (getEaLoc() != null) {
            _hashCode += getEaLoc().hashCode();
        }
        if (getPoleNo() != null) {
            _hashCode += getPoleNo().hashCode();
        }
        if (getSection() != null) {
            _hashCode += getSection().hashCode();
        }
        if (getTownship() != null) {
            _hashCode += getTownship().hashCode();
        }
        if (getRange() != null) {
            _hashCode += getRange().hashCode();
        }
        if (getLinkedTransformer() != null) {
            _hashCode += getLinkedTransformer().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Network.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "network"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("boardDist");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "boardDist"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("taxDist");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "taxDist"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("franchiseDist");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "franchiseDist"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("schoolDist");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "schoolDist"));
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
        elemField.setFieldName("eaLoc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eaLoc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eaLoc"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("poleNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("section");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "section"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("township");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "township"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("range");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "range"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("linkedTransformer");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "linkedTransformer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "linkedTransformer"));
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
