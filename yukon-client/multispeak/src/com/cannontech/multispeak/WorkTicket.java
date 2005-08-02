/**
 * WorkTicket.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class WorkTicket  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.String woNumber;
    private java.lang.String jobNumber;
    private java.lang.String description;
    private java.lang.String statusCode;
    private java.lang.String location;
    private java.lang.String county;
    private java.lang.String taxDist;
    private java.lang.String franchiseDist;
    private java.lang.String schoolDist;
    private java.lang.String district;
    private java.lang.Float systemVolts;
    private java.lang.String woType;
    private java.lang.String cd740C;
    private java.lang.String ext740C;
    private java.lang.String loanNo;
    private java.lang.String cwpYr;
    private java.lang.String cwpCd;
    private java.lang.Long budgetPur;
    private java.lang.Long acNo;
    private java.lang.Long budgetCode;
    private java.lang.String actCode;
    private java.lang.Long deptCode;
    private java.lang.Long wrhsCode;
    private java.lang.Long grpCode;
    private java.lang.String crewCode;
    private java.util.Calendar startDate;
    private java.util.Calendar clsDate;
    private java.util.Calendar estDate;
    private java.lang.String contCode;
    private java.lang.Long priority;
    private java.lang.Float duration;

    public WorkTicket() {
    }

    public WorkTicket(
           java.lang.String woNumber,
           java.lang.String jobNumber,
           java.lang.String description,
           java.lang.String statusCode,
           java.lang.String location,
           java.lang.String county,
           java.lang.String taxDist,
           java.lang.String franchiseDist,
           java.lang.String schoolDist,
           java.lang.String district,
           java.lang.Float systemVolts,
           java.lang.String woType,
           java.lang.String cd740C,
           java.lang.String ext740C,
           java.lang.String loanNo,
           java.lang.String cwpYr,
           java.lang.String cwpCd,
           java.lang.Long budgetPur,
           java.lang.Long acNo,
           java.lang.Long budgetCode,
           java.lang.String actCode,
           java.lang.Long deptCode,
           java.lang.Long wrhsCode,
           java.lang.Long grpCode,
           java.lang.String crewCode,
           java.util.Calendar startDate,
           java.util.Calendar clsDate,
           java.util.Calendar estDate,
           java.lang.String contCode,
           java.lang.Long priority,
           java.lang.Float duration) {
           this.woNumber = woNumber;
           this.jobNumber = jobNumber;
           this.description = description;
           this.statusCode = statusCode;
           this.location = location;
           this.county = county;
           this.taxDist = taxDist;
           this.franchiseDist = franchiseDist;
           this.schoolDist = schoolDist;
           this.district = district;
           this.systemVolts = systemVolts;
           this.woType = woType;
           this.cd740C = cd740C;
           this.ext740C = ext740C;
           this.loanNo = loanNo;
           this.cwpYr = cwpYr;
           this.cwpCd = cwpCd;
           this.budgetPur = budgetPur;
           this.acNo = acNo;
           this.budgetCode = budgetCode;
           this.actCode = actCode;
           this.deptCode = deptCode;
           this.wrhsCode = wrhsCode;
           this.grpCode = grpCode;
           this.crewCode = crewCode;
           this.startDate = startDate;
           this.clsDate = clsDate;
           this.estDate = estDate;
           this.contCode = contCode;
           this.priority = priority;
           this.duration = duration;
    }


    /**
     * Gets the woNumber value for this WorkTicket.
     * 
     * @return woNumber
     */
    public java.lang.String getWoNumber() {
        return woNumber;
    }


    /**
     * Sets the woNumber value for this WorkTicket.
     * 
     * @param woNumber
     */
    public void setWoNumber(java.lang.String woNumber) {
        this.woNumber = woNumber;
    }


    /**
     * Gets the jobNumber value for this WorkTicket.
     * 
     * @return jobNumber
     */
    public java.lang.String getJobNumber() {
        return jobNumber;
    }


    /**
     * Sets the jobNumber value for this WorkTicket.
     * 
     * @param jobNumber
     */
    public void setJobNumber(java.lang.String jobNumber) {
        this.jobNumber = jobNumber;
    }


    /**
     * Gets the description value for this WorkTicket.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this WorkTicket.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the statusCode value for this WorkTicket.
     * 
     * @return statusCode
     */
    public java.lang.String getStatusCode() {
        return statusCode;
    }


    /**
     * Sets the statusCode value for this WorkTicket.
     * 
     * @param statusCode
     */
    public void setStatusCode(java.lang.String statusCode) {
        this.statusCode = statusCode;
    }


    /**
     * Gets the location value for this WorkTicket.
     * 
     * @return location
     */
    public java.lang.String getLocation() {
        return location;
    }


    /**
     * Sets the location value for this WorkTicket.
     * 
     * @param location
     */
    public void setLocation(java.lang.String location) {
        this.location = location;
    }


    /**
     * Gets the county value for this WorkTicket.
     * 
     * @return county
     */
    public java.lang.String getCounty() {
        return county;
    }


    /**
     * Sets the county value for this WorkTicket.
     * 
     * @param county
     */
    public void setCounty(java.lang.String county) {
        this.county = county;
    }


    /**
     * Gets the taxDist value for this WorkTicket.
     * 
     * @return taxDist
     */
    public java.lang.String getTaxDist() {
        return taxDist;
    }


    /**
     * Sets the taxDist value for this WorkTicket.
     * 
     * @param taxDist
     */
    public void setTaxDist(java.lang.String taxDist) {
        this.taxDist = taxDist;
    }


    /**
     * Gets the franchiseDist value for this WorkTicket.
     * 
     * @return franchiseDist
     */
    public java.lang.String getFranchiseDist() {
        return franchiseDist;
    }


    /**
     * Sets the franchiseDist value for this WorkTicket.
     * 
     * @param franchiseDist
     */
    public void setFranchiseDist(java.lang.String franchiseDist) {
        this.franchiseDist = franchiseDist;
    }


    /**
     * Gets the schoolDist value for this WorkTicket.
     * 
     * @return schoolDist
     */
    public java.lang.String getSchoolDist() {
        return schoolDist;
    }


    /**
     * Sets the schoolDist value for this WorkTicket.
     * 
     * @param schoolDist
     */
    public void setSchoolDist(java.lang.String schoolDist) {
        this.schoolDist = schoolDist;
    }


    /**
     * Gets the district value for this WorkTicket.
     * 
     * @return district
     */
    public java.lang.String getDistrict() {
        return district;
    }


    /**
     * Sets the district value for this WorkTicket.
     * 
     * @param district
     */
    public void setDistrict(java.lang.String district) {
        this.district = district;
    }


    /**
     * Gets the systemVolts value for this WorkTicket.
     * 
     * @return systemVolts
     */
    public java.lang.Float getSystemVolts() {
        return systemVolts;
    }


    /**
     * Sets the systemVolts value for this WorkTicket.
     * 
     * @param systemVolts
     */
    public void setSystemVolts(java.lang.Float systemVolts) {
        this.systemVolts = systemVolts;
    }


    /**
     * Gets the woType value for this WorkTicket.
     * 
     * @return woType
     */
    public java.lang.String getWoType() {
        return woType;
    }


    /**
     * Sets the woType value for this WorkTicket.
     * 
     * @param woType
     */
    public void setWoType(java.lang.String woType) {
        this.woType = woType;
    }


    /**
     * Gets the cd740C value for this WorkTicket.
     * 
     * @return cd740C
     */
    public java.lang.String getCd740C() {
        return cd740C;
    }


    /**
     * Sets the cd740C value for this WorkTicket.
     * 
     * @param cd740C
     */
    public void setCd740C(java.lang.String cd740C) {
        this.cd740C = cd740C;
    }


    /**
     * Gets the ext740C value for this WorkTicket.
     * 
     * @return ext740C
     */
    public java.lang.String getExt740C() {
        return ext740C;
    }


    /**
     * Sets the ext740C value for this WorkTicket.
     * 
     * @param ext740C
     */
    public void setExt740C(java.lang.String ext740C) {
        this.ext740C = ext740C;
    }


    /**
     * Gets the loanNo value for this WorkTicket.
     * 
     * @return loanNo
     */
    public java.lang.String getLoanNo() {
        return loanNo;
    }


    /**
     * Sets the loanNo value for this WorkTicket.
     * 
     * @param loanNo
     */
    public void setLoanNo(java.lang.String loanNo) {
        this.loanNo = loanNo;
    }


    /**
     * Gets the cwpYr value for this WorkTicket.
     * 
     * @return cwpYr
     */
    public java.lang.String getCwpYr() {
        return cwpYr;
    }


    /**
     * Sets the cwpYr value for this WorkTicket.
     * 
     * @param cwpYr
     */
    public void setCwpYr(java.lang.String cwpYr) {
        this.cwpYr = cwpYr;
    }


    /**
     * Gets the cwpCd value for this WorkTicket.
     * 
     * @return cwpCd
     */
    public java.lang.String getCwpCd() {
        return cwpCd;
    }


    /**
     * Sets the cwpCd value for this WorkTicket.
     * 
     * @param cwpCd
     */
    public void setCwpCd(java.lang.String cwpCd) {
        this.cwpCd = cwpCd;
    }


    /**
     * Gets the budgetPur value for this WorkTicket.
     * 
     * @return budgetPur
     */
    public java.lang.Long getBudgetPur() {
        return budgetPur;
    }


    /**
     * Sets the budgetPur value for this WorkTicket.
     * 
     * @param budgetPur
     */
    public void setBudgetPur(java.lang.Long budgetPur) {
        this.budgetPur = budgetPur;
    }


    /**
     * Gets the acNo value for this WorkTicket.
     * 
     * @return acNo
     */
    public java.lang.Long getAcNo() {
        return acNo;
    }


    /**
     * Sets the acNo value for this WorkTicket.
     * 
     * @param acNo
     */
    public void setAcNo(java.lang.Long acNo) {
        this.acNo = acNo;
    }


    /**
     * Gets the budgetCode value for this WorkTicket.
     * 
     * @return budgetCode
     */
    public java.lang.Long getBudgetCode() {
        return budgetCode;
    }


    /**
     * Sets the budgetCode value for this WorkTicket.
     * 
     * @param budgetCode
     */
    public void setBudgetCode(java.lang.Long budgetCode) {
        this.budgetCode = budgetCode;
    }


    /**
     * Gets the actCode value for this WorkTicket.
     * 
     * @return actCode
     */
    public java.lang.String getActCode() {
        return actCode;
    }


    /**
     * Sets the actCode value for this WorkTicket.
     * 
     * @param actCode
     */
    public void setActCode(java.lang.String actCode) {
        this.actCode = actCode;
    }


    /**
     * Gets the deptCode value for this WorkTicket.
     * 
     * @return deptCode
     */
    public java.lang.Long getDeptCode() {
        return deptCode;
    }


    /**
     * Sets the deptCode value for this WorkTicket.
     * 
     * @param deptCode
     */
    public void setDeptCode(java.lang.Long deptCode) {
        this.deptCode = deptCode;
    }


    /**
     * Gets the wrhsCode value for this WorkTicket.
     * 
     * @return wrhsCode
     */
    public java.lang.Long getWrhsCode() {
        return wrhsCode;
    }


    /**
     * Sets the wrhsCode value for this WorkTicket.
     * 
     * @param wrhsCode
     */
    public void setWrhsCode(java.lang.Long wrhsCode) {
        this.wrhsCode = wrhsCode;
    }


    /**
     * Gets the grpCode value for this WorkTicket.
     * 
     * @return grpCode
     */
    public java.lang.Long getGrpCode() {
        return grpCode;
    }


    /**
     * Sets the grpCode value for this WorkTicket.
     * 
     * @param grpCode
     */
    public void setGrpCode(java.lang.Long grpCode) {
        this.grpCode = grpCode;
    }


    /**
     * Gets the crewCode value for this WorkTicket.
     * 
     * @return crewCode
     */
    public java.lang.String getCrewCode() {
        return crewCode;
    }


    /**
     * Sets the crewCode value for this WorkTicket.
     * 
     * @param crewCode
     */
    public void setCrewCode(java.lang.String crewCode) {
        this.crewCode = crewCode;
    }


    /**
     * Gets the startDate value for this WorkTicket.
     * 
     * @return startDate
     */
    public java.util.Calendar getStartDate() {
        return startDate;
    }


    /**
     * Sets the startDate value for this WorkTicket.
     * 
     * @param startDate
     */
    public void setStartDate(java.util.Calendar startDate) {
        this.startDate = startDate;
    }


    /**
     * Gets the clsDate value for this WorkTicket.
     * 
     * @return clsDate
     */
    public java.util.Calendar getClsDate() {
        return clsDate;
    }


    /**
     * Sets the clsDate value for this WorkTicket.
     * 
     * @param clsDate
     */
    public void setClsDate(java.util.Calendar clsDate) {
        this.clsDate = clsDate;
    }


    /**
     * Gets the estDate value for this WorkTicket.
     * 
     * @return estDate
     */
    public java.util.Calendar getEstDate() {
        return estDate;
    }


    /**
     * Sets the estDate value for this WorkTicket.
     * 
     * @param estDate
     */
    public void setEstDate(java.util.Calendar estDate) {
        this.estDate = estDate;
    }


    /**
     * Gets the contCode value for this WorkTicket.
     * 
     * @return contCode
     */
    public java.lang.String getContCode() {
        return contCode;
    }


    /**
     * Sets the contCode value for this WorkTicket.
     * 
     * @param contCode
     */
    public void setContCode(java.lang.String contCode) {
        this.contCode = contCode;
    }


    /**
     * Gets the priority value for this WorkTicket.
     * 
     * @return priority
     */
    public java.lang.Long getPriority() {
        return priority;
    }


    /**
     * Sets the priority value for this WorkTicket.
     * 
     * @param priority
     */
    public void setPriority(java.lang.Long priority) {
        this.priority = priority;
    }


    /**
     * Gets the duration value for this WorkTicket.
     * 
     * @return duration
     */
    public java.lang.Float getDuration() {
        return duration;
    }


    /**
     * Sets the duration value for this WorkTicket.
     * 
     * @param duration
     */
    public void setDuration(java.lang.Float duration) {
        this.duration = duration;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof WorkTicket)) return false;
        WorkTicket other = (WorkTicket) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.woNumber==null && other.getWoNumber()==null) || 
             (this.woNumber!=null &&
              this.woNumber.equals(other.getWoNumber()))) &&
            ((this.jobNumber==null && other.getJobNumber()==null) || 
             (this.jobNumber!=null &&
              this.jobNumber.equals(other.getJobNumber()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.statusCode==null && other.getStatusCode()==null) || 
             (this.statusCode!=null &&
              this.statusCode.equals(other.getStatusCode()))) &&
            ((this.location==null && other.getLocation()==null) || 
             (this.location!=null &&
              this.location.equals(other.getLocation()))) &&
            ((this.county==null && other.getCounty()==null) || 
             (this.county!=null &&
              this.county.equals(other.getCounty()))) &&
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
            ((this.systemVolts==null && other.getSystemVolts()==null) || 
             (this.systemVolts!=null &&
              this.systemVolts.equals(other.getSystemVolts()))) &&
            ((this.woType==null && other.getWoType()==null) || 
             (this.woType!=null &&
              this.woType.equals(other.getWoType()))) &&
            ((this.cd740C==null && other.getCd740C()==null) || 
             (this.cd740C!=null &&
              this.cd740C.equals(other.getCd740C()))) &&
            ((this.ext740C==null && other.getExt740C()==null) || 
             (this.ext740C!=null &&
              this.ext740C.equals(other.getExt740C()))) &&
            ((this.loanNo==null && other.getLoanNo()==null) || 
             (this.loanNo!=null &&
              this.loanNo.equals(other.getLoanNo()))) &&
            ((this.cwpYr==null && other.getCwpYr()==null) || 
             (this.cwpYr!=null &&
              this.cwpYr.equals(other.getCwpYr()))) &&
            ((this.cwpCd==null && other.getCwpCd()==null) || 
             (this.cwpCd!=null &&
              this.cwpCd.equals(other.getCwpCd()))) &&
            ((this.budgetPur==null && other.getBudgetPur()==null) || 
             (this.budgetPur!=null &&
              this.budgetPur.equals(other.getBudgetPur()))) &&
            ((this.acNo==null && other.getAcNo()==null) || 
             (this.acNo!=null &&
              this.acNo.equals(other.getAcNo()))) &&
            ((this.budgetCode==null && other.getBudgetCode()==null) || 
             (this.budgetCode!=null &&
              this.budgetCode.equals(other.getBudgetCode()))) &&
            ((this.actCode==null && other.getActCode()==null) || 
             (this.actCode!=null &&
              this.actCode.equals(other.getActCode()))) &&
            ((this.deptCode==null && other.getDeptCode()==null) || 
             (this.deptCode!=null &&
              this.deptCode.equals(other.getDeptCode()))) &&
            ((this.wrhsCode==null && other.getWrhsCode()==null) || 
             (this.wrhsCode!=null &&
              this.wrhsCode.equals(other.getWrhsCode()))) &&
            ((this.grpCode==null && other.getGrpCode()==null) || 
             (this.grpCode!=null &&
              this.grpCode.equals(other.getGrpCode()))) &&
            ((this.crewCode==null && other.getCrewCode()==null) || 
             (this.crewCode!=null &&
              this.crewCode.equals(other.getCrewCode()))) &&
            ((this.startDate==null && other.getStartDate()==null) || 
             (this.startDate!=null &&
              this.startDate.equals(other.getStartDate()))) &&
            ((this.clsDate==null && other.getClsDate()==null) || 
             (this.clsDate!=null &&
              this.clsDate.equals(other.getClsDate()))) &&
            ((this.estDate==null && other.getEstDate()==null) || 
             (this.estDate!=null &&
              this.estDate.equals(other.getEstDate()))) &&
            ((this.contCode==null && other.getContCode()==null) || 
             (this.contCode!=null &&
              this.contCode.equals(other.getContCode()))) &&
            ((this.priority==null && other.getPriority()==null) || 
             (this.priority!=null &&
              this.priority.equals(other.getPriority()))) &&
            ((this.duration==null && other.getDuration()==null) || 
             (this.duration!=null &&
              this.duration.equals(other.getDuration())));
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
        if (getWoNumber() != null) {
            _hashCode += getWoNumber().hashCode();
        }
        if (getJobNumber() != null) {
            _hashCode += getJobNumber().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getStatusCode() != null) {
            _hashCode += getStatusCode().hashCode();
        }
        if (getLocation() != null) {
            _hashCode += getLocation().hashCode();
        }
        if (getCounty() != null) {
            _hashCode += getCounty().hashCode();
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
        if (getSystemVolts() != null) {
            _hashCode += getSystemVolts().hashCode();
        }
        if (getWoType() != null) {
            _hashCode += getWoType().hashCode();
        }
        if (getCd740C() != null) {
            _hashCode += getCd740C().hashCode();
        }
        if (getExt740C() != null) {
            _hashCode += getExt740C().hashCode();
        }
        if (getLoanNo() != null) {
            _hashCode += getLoanNo().hashCode();
        }
        if (getCwpYr() != null) {
            _hashCode += getCwpYr().hashCode();
        }
        if (getCwpCd() != null) {
            _hashCode += getCwpCd().hashCode();
        }
        if (getBudgetPur() != null) {
            _hashCode += getBudgetPur().hashCode();
        }
        if (getAcNo() != null) {
            _hashCode += getAcNo().hashCode();
        }
        if (getBudgetCode() != null) {
            _hashCode += getBudgetCode().hashCode();
        }
        if (getActCode() != null) {
            _hashCode += getActCode().hashCode();
        }
        if (getDeptCode() != null) {
            _hashCode += getDeptCode().hashCode();
        }
        if (getWrhsCode() != null) {
            _hashCode += getWrhsCode().hashCode();
        }
        if (getGrpCode() != null) {
            _hashCode += getGrpCode().hashCode();
        }
        if (getCrewCode() != null) {
            _hashCode += getCrewCode().hashCode();
        }
        if (getStartDate() != null) {
            _hashCode += getStartDate().hashCode();
        }
        if (getClsDate() != null) {
            _hashCode += getClsDate().hashCode();
        }
        if (getEstDate() != null) {
            _hashCode += getEstDate().hashCode();
        }
        if (getContCode() != null) {
            _hashCode += getContCode().hashCode();
        }
        if (getPriority() != null) {
            _hashCode += getPriority().hashCode();
        }
        if (getDuration() != null) {
            _hashCode += getDuration().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(WorkTicket.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "workTicket"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("woNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "woNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("jobNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "jobNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("statusCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "statusCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("location");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "location"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("county");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "county"));
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
        elemField.setFieldName("systemVolts");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "systemVolts"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("woType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "woType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cd740C");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cd740c"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ext740C");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ext740c"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loanNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loanNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cwpYr");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cwpYr"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cwpCd");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cwpCd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("budgetPur");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "budgetPur"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "acNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("budgetCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "budgetCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("actCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "actCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deptCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "deptCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("wrhsCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "wrhsCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("grpCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "grpCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("crewCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "crewCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("startDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "startDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("clsDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "clsDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("estDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "estDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("contCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "contCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("priority");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "priority"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("duration");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "duration"));
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
