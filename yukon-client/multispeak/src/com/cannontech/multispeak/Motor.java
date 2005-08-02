/**
 * Motor.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Motor  extends com.cannontech.multispeak.MspMotorGenerator  implements java.io.Serializable {
    private java.math.BigInteger model;
    private java.lang.Integer status;
    private java.lang.Float hp;
    private java.lang.Float pf;
    private java.lang.Float eff;
    private java.lang.Float lgVolts;
    private java.lang.Float dropout;
    private java.lang.Long nemaTyp;
    private java.lang.Float limit;
    private java.lang.Long limitedBy;
    private java.lang.Long sftStTyp;
    private java.lang.Float sftStR;
    private java.lang.Float sftStX;
    private java.lang.Float sftStTap;
    private java.lang.Float sftStWdg;
    private java.lang.Float lrPf;
    private java.lang.Float lrMult;

    public Motor() {
    }

    public Motor(
           java.math.BigInteger model,
           java.lang.Integer status,
           java.lang.Float hp,
           java.lang.Float pf,
           java.lang.Float eff,
           java.lang.Float lgVolts,
           java.lang.Float dropout,
           java.lang.Long nemaTyp,
           java.lang.Float limit,
           java.lang.Long limitedBy,
           java.lang.Long sftStTyp,
           java.lang.Float sftStR,
           java.lang.Float sftStX,
           java.lang.Float sftStTap,
           java.lang.Float sftStWdg,
           java.lang.Float lrPf,
           java.lang.Float lrMult) {
           this.model = model;
           this.status = status;
           this.hp = hp;
           this.pf = pf;
           this.eff = eff;
           this.lgVolts = lgVolts;
           this.dropout = dropout;
           this.nemaTyp = nemaTyp;
           this.limit = limit;
           this.limitedBy = limitedBy;
           this.sftStTyp = sftStTyp;
           this.sftStR = sftStR;
           this.sftStX = sftStX;
           this.sftStTap = sftStTap;
           this.sftStWdg = sftStWdg;
           this.lrPf = lrPf;
           this.lrMult = lrMult;
    }


    /**
     * Gets the model value for this Motor.
     * 
     * @return model
     */
    public java.math.BigInteger getModel() {
        return model;
    }


    /**
     * Sets the model value for this Motor.
     * 
     * @param model
     */
    public void setModel(java.math.BigInteger model) {
        this.model = model;
    }


    /**
     * Gets the status value for this Motor.
     * 
     * @return status
     */
    public java.lang.Integer getStatus() {
        return status;
    }


    /**
     * Sets the status value for this Motor.
     * 
     * @param status
     */
    public void setStatus(java.lang.Integer status) {
        this.status = status;
    }


    /**
     * Gets the hp value for this Motor.
     * 
     * @return hp
     */
    public java.lang.Float getHp() {
        return hp;
    }


    /**
     * Sets the hp value for this Motor.
     * 
     * @param hp
     */
    public void setHp(java.lang.Float hp) {
        this.hp = hp;
    }


    /**
     * Gets the pf value for this Motor.
     * 
     * @return pf
     */
    public java.lang.Float getPf() {
        return pf;
    }


    /**
     * Sets the pf value for this Motor.
     * 
     * @param pf
     */
    public void setPf(java.lang.Float pf) {
        this.pf = pf;
    }


    /**
     * Gets the eff value for this Motor.
     * 
     * @return eff
     */
    public java.lang.Float getEff() {
        return eff;
    }


    /**
     * Sets the eff value for this Motor.
     * 
     * @param eff
     */
    public void setEff(java.lang.Float eff) {
        this.eff = eff;
    }


    /**
     * Gets the lgVolts value for this Motor.
     * 
     * @return lgVolts
     */
    public java.lang.Float getLgVolts() {
        return lgVolts;
    }


    /**
     * Sets the lgVolts value for this Motor.
     * 
     * @param lgVolts
     */
    public void setLgVolts(java.lang.Float lgVolts) {
        this.lgVolts = lgVolts;
    }


    /**
     * Gets the dropout value for this Motor.
     * 
     * @return dropout
     */
    public java.lang.Float getDropout() {
        return dropout;
    }


    /**
     * Sets the dropout value for this Motor.
     * 
     * @param dropout
     */
    public void setDropout(java.lang.Float dropout) {
        this.dropout = dropout;
    }


    /**
     * Gets the nemaTyp value for this Motor.
     * 
     * @return nemaTyp
     */
    public java.lang.Long getNemaTyp() {
        return nemaTyp;
    }


    /**
     * Sets the nemaTyp value for this Motor.
     * 
     * @param nemaTyp
     */
    public void setNemaTyp(java.lang.Long nemaTyp) {
        this.nemaTyp = nemaTyp;
    }


    /**
     * Gets the limit value for this Motor.
     * 
     * @return limit
     */
    public java.lang.Float getLimit() {
        return limit;
    }


    /**
     * Sets the limit value for this Motor.
     * 
     * @param limit
     */
    public void setLimit(java.lang.Float limit) {
        this.limit = limit;
    }


    /**
     * Gets the limitedBy value for this Motor.
     * 
     * @return limitedBy
     */
    public java.lang.Long getLimitedBy() {
        return limitedBy;
    }


    /**
     * Sets the limitedBy value for this Motor.
     * 
     * @param limitedBy
     */
    public void setLimitedBy(java.lang.Long limitedBy) {
        this.limitedBy = limitedBy;
    }


    /**
     * Gets the sftStTyp value for this Motor.
     * 
     * @return sftStTyp
     */
    public java.lang.Long getSftStTyp() {
        return sftStTyp;
    }


    /**
     * Sets the sftStTyp value for this Motor.
     * 
     * @param sftStTyp
     */
    public void setSftStTyp(java.lang.Long sftStTyp) {
        this.sftStTyp = sftStTyp;
    }


    /**
     * Gets the sftStR value for this Motor.
     * 
     * @return sftStR
     */
    public java.lang.Float getSftStR() {
        return sftStR;
    }


    /**
     * Sets the sftStR value for this Motor.
     * 
     * @param sftStR
     */
    public void setSftStR(java.lang.Float sftStR) {
        this.sftStR = sftStR;
    }


    /**
     * Gets the sftStX value for this Motor.
     * 
     * @return sftStX
     */
    public java.lang.Float getSftStX() {
        return sftStX;
    }


    /**
     * Sets the sftStX value for this Motor.
     * 
     * @param sftStX
     */
    public void setSftStX(java.lang.Float sftStX) {
        this.sftStX = sftStX;
    }


    /**
     * Gets the sftStTap value for this Motor.
     * 
     * @return sftStTap
     */
    public java.lang.Float getSftStTap() {
        return sftStTap;
    }


    /**
     * Sets the sftStTap value for this Motor.
     * 
     * @param sftStTap
     */
    public void setSftStTap(java.lang.Float sftStTap) {
        this.sftStTap = sftStTap;
    }


    /**
     * Gets the sftStWdg value for this Motor.
     * 
     * @return sftStWdg
     */
    public java.lang.Float getSftStWdg() {
        return sftStWdg;
    }


    /**
     * Sets the sftStWdg value for this Motor.
     * 
     * @param sftStWdg
     */
    public void setSftStWdg(java.lang.Float sftStWdg) {
        this.sftStWdg = sftStWdg;
    }


    /**
     * Gets the lrPf value for this Motor.
     * 
     * @return lrPf
     */
    public java.lang.Float getLrPf() {
        return lrPf;
    }


    /**
     * Sets the lrPf value for this Motor.
     * 
     * @param lrPf
     */
    public void setLrPf(java.lang.Float lrPf) {
        this.lrPf = lrPf;
    }


    /**
     * Gets the lrMult value for this Motor.
     * 
     * @return lrMult
     */
    public java.lang.Float getLrMult() {
        return lrMult;
    }


    /**
     * Sets the lrMult value for this Motor.
     * 
     * @param lrMult
     */
    public void setLrMult(java.lang.Float lrMult) {
        this.lrMult = lrMult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Motor)) return false;
        Motor other = (Motor) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.model==null && other.getModel()==null) || 
             (this.model!=null &&
              this.model.equals(other.getModel()))) &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.hp==null && other.getHp()==null) || 
             (this.hp!=null &&
              this.hp.equals(other.getHp()))) &&
            ((this.pf==null && other.getPf()==null) || 
             (this.pf!=null &&
              this.pf.equals(other.getPf()))) &&
            ((this.eff==null && other.getEff()==null) || 
             (this.eff!=null &&
              this.eff.equals(other.getEff()))) &&
            ((this.lgVolts==null && other.getLgVolts()==null) || 
             (this.lgVolts!=null &&
              this.lgVolts.equals(other.getLgVolts()))) &&
            ((this.dropout==null && other.getDropout()==null) || 
             (this.dropout!=null &&
              this.dropout.equals(other.getDropout()))) &&
            ((this.nemaTyp==null && other.getNemaTyp()==null) || 
             (this.nemaTyp!=null &&
              this.nemaTyp.equals(other.getNemaTyp()))) &&
            ((this.limit==null && other.getLimit()==null) || 
             (this.limit!=null &&
              this.limit.equals(other.getLimit()))) &&
            ((this.limitedBy==null && other.getLimitedBy()==null) || 
             (this.limitedBy!=null &&
              this.limitedBy.equals(other.getLimitedBy()))) &&
            ((this.sftStTyp==null && other.getSftStTyp()==null) || 
             (this.sftStTyp!=null &&
              this.sftStTyp.equals(other.getSftStTyp()))) &&
            ((this.sftStR==null && other.getSftStR()==null) || 
             (this.sftStR!=null &&
              this.sftStR.equals(other.getSftStR()))) &&
            ((this.sftStX==null && other.getSftStX()==null) || 
             (this.sftStX!=null &&
              this.sftStX.equals(other.getSftStX()))) &&
            ((this.sftStTap==null && other.getSftStTap()==null) || 
             (this.sftStTap!=null &&
              this.sftStTap.equals(other.getSftStTap()))) &&
            ((this.sftStWdg==null && other.getSftStWdg()==null) || 
             (this.sftStWdg!=null &&
              this.sftStWdg.equals(other.getSftStWdg()))) &&
            ((this.lrPf==null && other.getLrPf()==null) || 
             (this.lrPf!=null &&
              this.lrPf.equals(other.getLrPf()))) &&
            ((this.lrMult==null && other.getLrMult()==null) || 
             (this.lrMult!=null &&
              this.lrMult.equals(other.getLrMult())));
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
        if (getModel() != null) {
            _hashCode += getModel().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getHp() != null) {
            _hashCode += getHp().hashCode();
        }
        if (getPf() != null) {
            _hashCode += getPf().hashCode();
        }
        if (getEff() != null) {
            _hashCode += getEff().hashCode();
        }
        if (getLgVolts() != null) {
            _hashCode += getLgVolts().hashCode();
        }
        if (getDropout() != null) {
            _hashCode += getDropout().hashCode();
        }
        if (getNemaTyp() != null) {
            _hashCode += getNemaTyp().hashCode();
        }
        if (getLimit() != null) {
            _hashCode += getLimit().hashCode();
        }
        if (getLimitedBy() != null) {
            _hashCode += getLimitedBy().hashCode();
        }
        if (getSftStTyp() != null) {
            _hashCode += getSftStTyp().hashCode();
        }
        if (getSftStR() != null) {
            _hashCode += getSftStR().hashCode();
        }
        if (getSftStX() != null) {
            _hashCode += getSftStX().hashCode();
        }
        if (getSftStTap() != null) {
            _hashCode += getSftStTap().hashCode();
        }
        if (getSftStWdg() != null) {
            _hashCode += getSftStWdg().hashCode();
        }
        if (getLrPf() != null) {
            _hashCode += getLrPf().hashCode();
        }
        if (getLrMult() != null) {
            _hashCode += getLrMult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Motor.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "motor"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("model");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "model"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hp");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "hp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pf");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pf"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eff");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eff"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lgVolts");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lgVolts"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dropout");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "dropout"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nemaTyp");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "nemaTyp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("limit");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "limit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("limitedBy");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "limitedBy"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sftStTyp");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sftStTyp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sftStR");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sftStR"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sftStX");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sftStX"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sftStTap");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sftStTap"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sftStWdg");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sftStWdg"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lrPf");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lrPf"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lrMult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lrMult"));
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
