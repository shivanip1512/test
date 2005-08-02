/**
 * WorkOrder.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class WorkOrder  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.String woNumber;
    private java.lang.String jobNumber;
    private java.lang.String jobDescr;
    private java.lang.String statusCode;
    private java.lang.String designCode;
    private java.lang.String lastName;
    private java.lang.String firstName;
    private java.lang.String mName;
    private java.lang.String homeAc;
    private java.lang.String homePhone;
    private java.lang.String dayAc;
    private java.lang.String dayPhone;
    private java.lang.String billAddr1;
    private java.lang.String billAddr2;
    private java.lang.String billCity;
    private java.lang.String billState;
    private java.lang.String billZip;
    private java.lang.String custID;
    private java.lang.String servAddr1;
    private java.lang.String servAddr2;
    private java.lang.String servCity;
    private java.lang.String servState;
    private java.lang.String servZip;
    private java.lang.String county;
    private java.lang.String section;
    private java.lang.String township;
    private java.lang.String range;
    private java.lang.String townshipName;
    private java.lang.String subdivision;
    private java.lang.String block;
    private java.lang.String lot;
    private java.lang.String boardDist;
    private java.lang.String taxDist;
    private java.lang.String franchiseDist;
    private java.lang.String schoolDist;
    private java.lang.String district;
    private java.lang.Float systemVolts;
    private java.lang.String contractor;
    private java.lang.String contrAc;
    private java.lang.String contrPhone;
    private java.lang.String electrician;
    private java.lang.String elecAc;
    private java.lang.String elecPhone;
    private java.lang.String woType;
    private java.lang.String cd740C;
    private java.lang.String ext740C;
    private java.lang.String loanNo;
    private java.lang.String cwpYr;
    private java.lang.String cwpCd;
    private java.lang.Long budgetPur;
    private java.lang.String engrAsnd;
    private java.lang.Long cfmsCode;
    private java.lang.Long acNo;
    private java.lang.Long budgetCode;
    private java.lang.String actCode;
    private java.lang.Long deptCode;
    private java.lang.String warehouseID;
    private java.lang.Long grpCode;
    private java.lang.String crewCode;
    private java.util.Calendar startDate;
    private java.util.Date clsDate;
    private java.util.Date estDate;
    private com.cannontech.multispeak.ActionCode actionCode;
    private java.lang.String laborCategoryID;
    private java.lang.Float atc;
    private com.cannontech.multispeak.LoadDistrict loadDistrict;
    private com.cannontech.multispeak.ConstGrade constGrade;
    private java.lang.Float duration;
    private java.lang.String accountNumber;
    private com.cannontech.multispeak.ArrayOfStation stationList;
    private com.cannontech.multispeak.BackgroundGraphics backgroundGraphics;

    public WorkOrder() {
    }

    public WorkOrder(
           java.lang.String woNumber,
           java.lang.String jobNumber,
           java.lang.String jobDescr,
           java.lang.String statusCode,
           java.lang.String designCode,
           java.lang.String lastName,
           java.lang.String firstName,
           java.lang.String mName,
           java.lang.String homeAc,
           java.lang.String homePhone,
           java.lang.String dayAc,
           java.lang.String dayPhone,
           java.lang.String billAddr1,
           java.lang.String billAddr2,
           java.lang.String billCity,
           java.lang.String billState,
           java.lang.String billZip,
           java.lang.String custID,
           java.lang.String servAddr1,
           java.lang.String servAddr2,
           java.lang.String servCity,
           java.lang.String servState,
           java.lang.String servZip,
           java.lang.String county,
           java.lang.String section,
           java.lang.String township,
           java.lang.String range,
           java.lang.String townshipName,
           java.lang.String subdivision,
           java.lang.String block,
           java.lang.String lot,
           java.lang.String boardDist,
           java.lang.String taxDist,
           java.lang.String franchiseDist,
           java.lang.String schoolDist,
           java.lang.String district,
           java.lang.Float systemVolts,
           java.lang.String contractor,
           java.lang.String contrAc,
           java.lang.String contrPhone,
           java.lang.String electrician,
           java.lang.String elecAc,
           java.lang.String elecPhone,
           java.lang.String woType,
           java.lang.String cd740C,
           java.lang.String ext740C,
           java.lang.String loanNo,
           java.lang.String cwpYr,
           java.lang.String cwpCd,
           java.lang.Long budgetPur,
           java.lang.String engrAsnd,
           java.lang.Long cfmsCode,
           java.lang.Long acNo,
           java.lang.Long budgetCode,
           java.lang.String actCode,
           java.lang.Long deptCode,
           java.lang.String warehouseID,
           java.lang.Long grpCode,
           java.lang.String crewCode,
           java.util.Calendar startDate,
           java.util.Date clsDate,
           java.util.Date estDate,
           com.cannontech.multispeak.ActionCode actionCode,
           java.lang.String laborCategoryID,
           java.lang.Float atc,
           com.cannontech.multispeak.LoadDistrict loadDistrict,
           com.cannontech.multispeak.ConstGrade constGrade,
           java.lang.Float duration,
           java.lang.String accountNumber,
           com.cannontech.multispeak.ArrayOfStation stationList,
           com.cannontech.multispeak.BackgroundGraphics backgroundGraphics) {
           this.woNumber = woNumber;
           this.jobNumber = jobNumber;
           this.jobDescr = jobDescr;
           this.statusCode = statusCode;
           this.designCode = designCode;
           this.lastName = lastName;
           this.firstName = firstName;
           this.mName = mName;
           this.homeAc = homeAc;
           this.homePhone = homePhone;
           this.dayAc = dayAc;
           this.dayPhone = dayPhone;
           this.billAddr1 = billAddr1;
           this.billAddr2 = billAddr2;
           this.billCity = billCity;
           this.billState = billState;
           this.billZip = billZip;
           this.custID = custID;
           this.servAddr1 = servAddr1;
           this.servAddr2 = servAddr2;
           this.servCity = servCity;
           this.servState = servState;
           this.servZip = servZip;
           this.county = county;
           this.section = section;
           this.township = township;
           this.range = range;
           this.townshipName = townshipName;
           this.subdivision = subdivision;
           this.block = block;
           this.lot = lot;
           this.boardDist = boardDist;
           this.taxDist = taxDist;
           this.franchiseDist = franchiseDist;
           this.schoolDist = schoolDist;
           this.district = district;
           this.systemVolts = systemVolts;
           this.contractor = contractor;
           this.contrAc = contrAc;
           this.contrPhone = contrPhone;
           this.electrician = electrician;
           this.elecAc = elecAc;
           this.elecPhone = elecPhone;
           this.woType = woType;
           this.cd740C = cd740C;
           this.ext740C = ext740C;
           this.loanNo = loanNo;
           this.cwpYr = cwpYr;
           this.cwpCd = cwpCd;
           this.budgetPur = budgetPur;
           this.engrAsnd = engrAsnd;
           this.cfmsCode = cfmsCode;
           this.acNo = acNo;
           this.budgetCode = budgetCode;
           this.actCode = actCode;
           this.deptCode = deptCode;
           this.warehouseID = warehouseID;
           this.grpCode = grpCode;
           this.crewCode = crewCode;
           this.startDate = startDate;
           this.clsDate = clsDate;
           this.estDate = estDate;
           this.actionCode = actionCode;
           this.laborCategoryID = laborCategoryID;
           this.atc = atc;
           this.loadDistrict = loadDistrict;
           this.constGrade = constGrade;
           this.duration = duration;
           this.accountNumber = accountNumber;
           this.stationList = stationList;
           this.backgroundGraphics = backgroundGraphics;
    }


    /**
     * Gets the woNumber value for this WorkOrder.
     * 
     * @return woNumber
     */
    public java.lang.String getWoNumber() {
        return woNumber;
    }


    /**
     * Sets the woNumber value for this WorkOrder.
     * 
     * @param woNumber
     */
    public void setWoNumber(java.lang.String woNumber) {
        this.woNumber = woNumber;
    }


    /**
     * Gets the jobNumber value for this WorkOrder.
     * 
     * @return jobNumber
     */
    public java.lang.String getJobNumber() {
        return jobNumber;
    }


    /**
     * Sets the jobNumber value for this WorkOrder.
     * 
     * @param jobNumber
     */
    public void setJobNumber(java.lang.String jobNumber) {
        this.jobNumber = jobNumber;
    }


    /**
     * Gets the jobDescr value for this WorkOrder.
     * 
     * @return jobDescr
     */
    public java.lang.String getJobDescr() {
        return jobDescr;
    }


    /**
     * Sets the jobDescr value for this WorkOrder.
     * 
     * @param jobDescr
     */
    public void setJobDescr(java.lang.String jobDescr) {
        this.jobDescr = jobDescr;
    }


    /**
     * Gets the statusCode value for this WorkOrder.
     * 
     * @return statusCode
     */
    public java.lang.String getStatusCode() {
        return statusCode;
    }


    /**
     * Sets the statusCode value for this WorkOrder.
     * 
     * @param statusCode
     */
    public void setStatusCode(java.lang.String statusCode) {
        this.statusCode = statusCode;
    }


    /**
     * Gets the designCode value for this WorkOrder.
     * 
     * @return designCode
     */
    public java.lang.String getDesignCode() {
        return designCode;
    }


    /**
     * Sets the designCode value for this WorkOrder.
     * 
     * @param designCode
     */
    public void setDesignCode(java.lang.String designCode) {
        this.designCode = designCode;
    }


    /**
     * Gets the lastName value for this WorkOrder.
     * 
     * @return lastName
     */
    public java.lang.String getLastName() {
        return lastName;
    }


    /**
     * Sets the lastName value for this WorkOrder.
     * 
     * @param lastName
     */
    public void setLastName(java.lang.String lastName) {
        this.lastName = lastName;
    }


    /**
     * Gets the firstName value for this WorkOrder.
     * 
     * @return firstName
     */
    public java.lang.String getFirstName() {
        return firstName;
    }


    /**
     * Sets the firstName value for this WorkOrder.
     * 
     * @param firstName
     */
    public void setFirstName(java.lang.String firstName) {
        this.firstName = firstName;
    }


    /**
     * Gets the mName value for this WorkOrder.
     * 
     * @return mName
     */
    public java.lang.String getMName() {
        return mName;
    }


    /**
     * Sets the mName value for this WorkOrder.
     * 
     * @param mName
     */
    public void setMName(java.lang.String mName) {
        this.mName = mName;
    }


    /**
     * Gets the homeAc value for this WorkOrder.
     * 
     * @return homeAc
     */
    public java.lang.String getHomeAc() {
        return homeAc;
    }


    /**
     * Sets the homeAc value for this WorkOrder.
     * 
     * @param homeAc
     */
    public void setHomeAc(java.lang.String homeAc) {
        this.homeAc = homeAc;
    }


    /**
     * Gets the homePhone value for this WorkOrder.
     * 
     * @return homePhone
     */
    public java.lang.String getHomePhone() {
        return homePhone;
    }


    /**
     * Sets the homePhone value for this WorkOrder.
     * 
     * @param homePhone
     */
    public void setHomePhone(java.lang.String homePhone) {
        this.homePhone = homePhone;
    }


    /**
     * Gets the dayAc value for this WorkOrder.
     * 
     * @return dayAc
     */
    public java.lang.String getDayAc() {
        return dayAc;
    }


    /**
     * Sets the dayAc value for this WorkOrder.
     * 
     * @param dayAc
     */
    public void setDayAc(java.lang.String dayAc) {
        this.dayAc = dayAc;
    }


    /**
     * Gets the dayPhone value for this WorkOrder.
     * 
     * @return dayPhone
     */
    public java.lang.String getDayPhone() {
        return dayPhone;
    }


    /**
     * Sets the dayPhone value for this WorkOrder.
     * 
     * @param dayPhone
     */
    public void setDayPhone(java.lang.String dayPhone) {
        this.dayPhone = dayPhone;
    }


    /**
     * Gets the billAddr1 value for this WorkOrder.
     * 
     * @return billAddr1
     */
    public java.lang.String getBillAddr1() {
        return billAddr1;
    }


    /**
     * Sets the billAddr1 value for this WorkOrder.
     * 
     * @param billAddr1
     */
    public void setBillAddr1(java.lang.String billAddr1) {
        this.billAddr1 = billAddr1;
    }


    /**
     * Gets the billAddr2 value for this WorkOrder.
     * 
     * @return billAddr2
     */
    public java.lang.String getBillAddr2() {
        return billAddr2;
    }


    /**
     * Sets the billAddr2 value for this WorkOrder.
     * 
     * @param billAddr2
     */
    public void setBillAddr2(java.lang.String billAddr2) {
        this.billAddr2 = billAddr2;
    }


    /**
     * Gets the billCity value for this WorkOrder.
     * 
     * @return billCity
     */
    public java.lang.String getBillCity() {
        return billCity;
    }


    /**
     * Sets the billCity value for this WorkOrder.
     * 
     * @param billCity
     */
    public void setBillCity(java.lang.String billCity) {
        this.billCity = billCity;
    }


    /**
     * Gets the billState value for this WorkOrder.
     * 
     * @return billState
     */
    public java.lang.String getBillState() {
        return billState;
    }


    /**
     * Sets the billState value for this WorkOrder.
     * 
     * @param billState
     */
    public void setBillState(java.lang.String billState) {
        this.billState = billState;
    }


    /**
     * Gets the billZip value for this WorkOrder.
     * 
     * @return billZip
     */
    public java.lang.String getBillZip() {
        return billZip;
    }


    /**
     * Sets the billZip value for this WorkOrder.
     * 
     * @param billZip
     */
    public void setBillZip(java.lang.String billZip) {
        this.billZip = billZip;
    }


    /**
     * Gets the custID value for this WorkOrder.
     * 
     * @return custID
     */
    public java.lang.String getCustID() {
        return custID;
    }


    /**
     * Sets the custID value for this WorkOrder.
     * 
     * @param custID
     */
    public void setCustID(java.lang.String custID) {
        this.custID = custID;
    }


    /**
     * Gets the servAddr1 value for this WorkOrder.
     * 
     * @return servAddr1
     */
    public java.lang.String getServAddr1() {
        return servAddr1;
    }


    /**
     * Sets the servAddr1 value for this WorkOrder.
     * 
     * @param servAddr1
     */
    public void setServAddr1(java.lang.String servAddr1) {
        this.servAddr1 = servAddr1;
    }


    /**
     * Gets the servAddr2 value for this WorkOrder.
     * 
     * @return servAddr2
     */
    public java.lang.String getServAddr2() {
        return servAddr2;
    }


    /**
     * Sets the servAddr2 value for this WorkOrder.
     * 
     * @param servAddr2
     */
    public void setServAddr2(java.lang.String servAddr2) {
        this.servAddr2 = servAddr2;
    }


    /**
     * Gets the servCity value for this WorkOrder.
     * 
     * @return servCity
     */
    public java.lang.String getServCity() {
        return servCity;
    }


    /**
     * Sets the servCity value for this WorkOrder.
     * 
     * @param servCity
     */
    public void setServCity(java.lang.String servCity) {
        this.servCity = servCity;
    }


    /**
     * Gets the servState value for this WorkOrder.
     * 
     * @return servState
     */
    public java.lang.String getServState() {
        return servState;
    }


    /**
     * Sets the servState value for this WorkOrder.
     * 
     * @param servState
     */
    public void setServState(java.lang.String servState) {
        this.servState = servState;
    }


    /**
     * Gets the servZip value for this WorkOrder.
     * 
     * @return servZip
     */
    public java.lang.String getServZip() {
        return servZip;
    }


    /**
     * Sets the servZip value for this WorkOrder.
     * 
     * @param servZip
     */
    public void setServZip(java.lang.String servZip) {
        this.servZip = servZip;
    }


    /**
     * Gets the county value for this WorkOrder.
     * 
     * @return county
     */
    public java.lang.String getCounty() {
        return county;
    }


    /**
     * Sets the county value for this WorkOrder.
     * 
     * @param county
     */
    public void setCounty(java.lang.String county) {
        this.county = county;
    }


    /**
     * Gets the section value for this WorkOrder.
     * 
     * @return section
     */
    public java.lang.String getSection() {
        return section;
    }


    /**
     * Sets the section value for this WorkOrder.
     * 
     * @param section
     */
    public void setSection(java.lang.String section) {
        this.section = section;
    }


    /**
     * Gets the township value for this WorkOrder.
     * 
     * @return township
     */
    public java.lang.String getTownship() {
        return township;
    }


    /**
     * Sets the township value for this WorkOrder.
     * 
     * @param township
     */
    public void setTownship(java.lang.String township) {
        this.township = township;
    }


    /**
     * Gets the range value for this WorkOrder.
     * 
     * @return range
     */
    public java.lang.String getRange() {
        return range;
    }


    /**
     * Sets the range value for this WorkOrder.
     * 
     * @param range
     */
    public void setRange(java.lang.String range) {
        this.range = range;
    }


    /**
     * Gets the townshipName value for this WorkOrder.
     * 
     * @return townshipName
     */
    public java.lang.String getTownshipName() {
        return townshipName;
    }


    /**
     * Sets the townshipName value for this WorkOrder.
     * 
     * @param townshipName
     */
    public void setTownshipName(java.lang.String townshipName) {
        this.townshipName = townshipName;
    }


    /**
     * Gets the subdivision value for this WorkOrder.
     * 
     * @return subdivision
     */
    public java.lang.String getSubdivision() {
        return subdivision;
    }


    /**
     * Sets the subdivision value for this WorkOrder.
     * 
     * @param subdivision
     */
    public void setSubdivision(java.lang.String subdivision) {
        this.subdivision = subdivision;
    }


    /**
     * Gets the block value for this WorkOrder.
     * 
     * @return block
     */
    public java.lang.String getBlock() {
        return block;
    }


    /**
     * Sets the block value for this WorkOrder.
     * 
     * @param block
     */
    public void setBlock(java.lang.String block) {
        this.block = block;
    }


    /**
     * Gets the lot value for this WorkOrder.
     * 
     * @return lot
     */
    public java.lang.String getLot() {
        return lot;
    }


    /**
     * Sets the lot value for this WorkOrder.
     * 
     * @param lot
     */
    public void setLot(java.lang.String lot) {
        this.lot = lot;
    }


    /**
     * Gets the boardDist value for this WorkOrder.
     * 
     * @return boardDist
     */
    public java.lang.String getBoardDist() {
        return boardDist;
    }


    /**
     * Sets the boardDist value for this WorkOrder.
     * 
     * @param boardDist
     */
    public void setBoardDist(java.lang.String boardDist) {
        this.boardDist = boardDist;
    }


    /**
     * Gets the taxDist value for this WorkOrder.
     * 
     * @return taxDist
     */
    public java.lang.String getTaxDist() {
        return taxDist;
    }


    /**
     * Sets the taxDist value for this WorkOrder.
     * 
     * @param taxDist
     */
    public void setTaxDist(java.lang.String taxDist) {
        this.taxDist = taxDist;
    }


    /**
     * Gets the franchiseDist value for this WorkOrder.
     * 
     * @return franchiseDist
     */
    public java.lang.String getFranchiseDist() {
        return franchiseDist;
    }


    /**
     * Sets the franchiseDist value for this WorkOrder.
     * 
     * @param franchiseDist
     */
    public void setFranchiseDist(java.lang.String franchiseDist) {
        this.franchiseDist = franchiseDist;
    }


    /**
     * Gets the schoolDist value for this WorkOrder.
     * 
     * @return schoolDist
     */
    public java.lang.String getSchoolDist() {
        return schoolDist;
    }


    /**
     * Sets the schoolDist value for this WorkOrder.
     * 
     * @param schoolDist
     */
    public void setSchoolDist(java.lang.String schoolDist) {
        this.schoolDist = schoolDist;
    }


    /**
     * Gets the district value for this WorkOrder.
     * 
     * @return district
     */
    public java.lang.String getDistrict() {
        return district;
    }


    /**
     * Sets the district value for this WorkOrder.
     * 
     * @param district
     */
    public void setDistrict(java.lang.String district) {
        this.district = district;
    }


    /**
     * Gets the systemVolts value for this WorkOrder.
     * 
     * @return systemVolts
     */
    public java.lang.Float getSystemVolts() {
        return systemVolts;
    }


    /**
     * Sets the systemVolts value for this WorkOrder.
     * 
     * @param systemVolts
     */
    public void setSystemVolts(java.lang.Float systemVolts) {
        this.systemVolts = systemVolts;
    }


    /**
     * Gets the contractor value for this WorkOrder.
     * 
     * @return contractor
     */
    public java.lang.String getContractor() {
        return contractor;
    }


    /**
     * Sets the contractor value for this WorkOrder.
     * 
     * @param contractor
     */
    public void setContractor(java.lang.String contractor) {
        this.contractor = contractor;
    }


    /**
     * Gets the contrAc value for this WorkOrder.
     * 
     * @return contrAc
     */
    public java.lang.String getContrAc() {
        return contrAc;
    }


    /**
     * Sets the contrAc value for this WorkOrder.
     * 
     * @param contrAc
     */
    public void setContrAc(java.lang.String contrAc) {
        this.contrAc = contrAc;
    }


    /**
     * Gets the contrPhone value for this WorkOrder.
     * 
     * @return contrPhone
     */
    public java.lang.String getContrPhone() {
        return contrPhone;
    }


    /**
     * Sets the contrPhone value for this WorkOrder.
     * 
     * @param contrPhone
     */
    public void setContrPhone(java.lang.String contrPhone) {
        this.contrPhone = contrPhone;
    }


    /**
     * Gets the electrician value for this WorkOrder.
     * 
     * @return electrician
     */
    public java.lang.String getElectrician() {
        return electrician;
    }


    /**
     * Sets the electrician value for this WorkOrder.
     * 
     * @param electrician
     */
    public void setElectrician(java.lang.String electrician) {
        this.electrician = electrician;
    }


    /**
     * Gets the elecAc value for this WorkOrder.
     * 
     * @return elecAc
     */
    public java.lang.String getElecAc() {
        return elecAc;
    }


    /**
     * Sets the elecAc value for this WorkOrder.
     * 
     * @param elecAc
     */
    public void setElecAc(java.lang.String elecAc) {
        this.elecAc = elecAc;
    }


    /**
     * Gets the elecPhone value for this WorkOrder.
     * 
     * @return elecPhone
     */
    public java.lang.String getElecPhone() {
        return elecPhone;
    }


    /**
     * Sets the elecPhone value for this WorkOrder.
     * 
     * @param elecPhone
     */
    public void setElecPhone(java.lang.String elecPhone) {
        this.elecPhone = elecPhone;
    }


    /**
     * Gets the woType value for this WorkOrder.
     * 
     * @return woType
     */
    public java.lang.String getWoType() {
        return woType;
    }


    /**
     * Sets the woType value for this WorkOrder.
     * 
     * @param woType
     */
    public void setWoType(java.lang.String woType) {
        this.woType = woType;
    }


    /**
     * Gets the cd740C value for this WorkOrder.
     * 
     * @return cd740C
     */
    public java.lang.String getCd740C() {
        return cd740C;
    }


    /**
     * Sets the cd740C value for this WorkOrder.
     * 
     * @param cd740C
     */
    public void setCd740C(java.lang.String cd740C) {
        this.cd740C = cd740C;
    }


    /**
     * Gets the ext740C value for this WorkOrder.
     * 
     * @return ext740C
     */
    public java.lang.String getExt740C() {
        return ext740C;
    }


    /**
     * Sets the ext740C value for this WorkOrder.
     * 
     * @param ext740C
     */
    public void setExt740C(java.lang.String ext740C) {
        this.ext740C = ext740C;
    }


    /**
     * Gets the loanNo value for this WorkOrder.
     * 
     * @return loanNo
     */
    public java.lang.String getLoanNo() {
        return loanNo;
    }


    /**
     * Sets the loanNo value for this WorkOrder.
     * 
     * @param loanNo
     */
    public void setLoanNo(java.lang.String loanNo) {
        this.loanNo = loanNo;
    }


    /**
     * Gets the cwpYr value for this WorkOrder.
     * 
     * @return cwpYr
     */
    public java.lang.String getCwpYr() {
        return cwpYr;
    }


    /**
     * Sets the cwpYr value for this WorkOrder.
     * 
     * @param cwpYr
     */
    public void setCwpYr(java.lang.String cwpYr) {
        this.cwpYr = cwpYr;
    }


    /**
     * Gets the cwpCd value for this WorkOrder.
     * 
     * @return cwpCd
     */
    public java.lang.String getCwpCd() {
        return cwpCd;
    }


    /**
     * Sets the cwpCd value for this WorkOrder.
     * 
     * @param cwpCd
     */
    public void setCwpCd(java.lang.String cwpCd) {
        this.cwpCd = cwpCd;
    }


    /**
     * Gets the budgetPur value for this WorkOrder.
     * 
     * @return budgetPur
     */
    public java.lang.Long getBudgetPur() {
        return budgetPur;
    }


    /**
     * Sets the budgetPur value for this WorkOrder.
     * 
     * @param budgetPur
     */
    public void setBudgetPur(java.lang.Long budgetPur) {
        this.budgetPur = budgetPur;
    }


    /**
     * Gets the engrAsnd value for this WorkOrder.
     * 
     * @return engrAsnd
     */
    public java.lang.String getEngrAsnd() {
        return engrAsnd;
    }


    /**
     * Sets the engrAsnd value for this WorkOrder.
     * 
     * @param engrAsnd
     */
    public void setEngrAsnd(java.lang.String engrAsnd) {
        this.engrAsnd = engrAsnd;
    }


    /**
     * Gets the cfmsCode value for this WorkOrder.
     * 
     * @return cfmsCode
     */
    public java.lang.Long getCfmsCode() {
        return cfmsCode;
    }


    /**
     * Sets the cfmsCode value for this WorkOrder.
     * 
     * @param cfmsCode
     */
    public void setCfmsCode(java.lang.Long cfmsCode) {
        this.cfmsCode = cfmsCode;
    }


    /**
     * Gets the acNo value for this WorkOrder.
     * 
     * @return acNo
     */
    public java.lang.Long getAcNo() {
        return acNo;
    }


    /**
     * Sets the acNo value for this WorkOrder.
     * 
     * @param acNo
     */
    public void setAcNo(java.lang.Long acNo) {
        this.acNo = acNo;
    }


    /**
     * Gets the budgetCode value for this WorkOrder.
     * 
     * @return budgetCode
     */
    public java.lang.Long getBudgetCode() {
        return budgetCode;
    }


    /**
     * Sets the budgetCode value for this WorkOrder.
     * 
     * @param budgetCode
     */
    public void setBudgetCode(java.lang.Long budgetCode) {
        this.budgetCode = budgetCode;
    }


    /**
     * Gets the actCode value for this WorkOrder.
     * 
     * @return actCode
     */
    public java.lang.String getActCode() {
        return actCode;
    }


    /**
     * Sets the actCode value for this WorkOrder.
     * 
     * @param actCode
     */
    public void setActCode(java.lang.String actCode) {
        this.actCode = actCode;
    }


    /**
     * Gets the deptCode value for this WorkOrder.
     * 
     * @return deptCode
     */
    public java.lang.Long getDeptCode() {
        return deptCode;
    }


    /**
     * Sets the deptCode value for this WorkOrder.
     * 
     * @param deptCode
     */
    public void setDeptCode(java.lang.Long deptCode) {
        this.deptCode = deptCode;
    }


    /**
     * Gets the warehouseID value for this WorkOrder.
     * 
     * @return warehouseID
     */
    public java.lang.String getWarehouseID() {
        return warehouseID;
    }


    /**
     * Sets the warehouseID value for this WorkOrder.
     * 
     * @param warehouseID
     */
    public void setWarehouseID(java.lang.String warehouseID) {
        this.warehouseID = warehouseID;
    }


    /**
     * Gets the grpCode value for this WorkOrder.
     * 
     * @return grpCode
     */
    public java.lang.Long getGrpCode() {
        return grpCode;
    }


    /**
     * Sets the grpCode value for this WorkOrder.
     * 
     * @param grpCode
     */
    public void setGrpCode(java.lang.Long grpCode) {
        this.grpCode = grpCode;
    }


    /**
     * Gets the crewCode value for this WorkOrder.
     * 
     * @return crewCode
     */
    public java.lang.String getCrewCode() {
        return crewCode;
    }


    /**
     * Sets the crewCode value for this WorkOrder.
     * 
     * @param crewCode
     */
    public void setCrewCode(java.lang.String crewCode) {
        this.crewCode = crewCode;
    }


    /**
     * Gets the startDate value for this WorkOrder.
     * 
     * @return startDate
     */
    public java.util.Calendar getStartDate() {
        return startDate;
    }


    /**
     * Sets the startDate value for this WorkOrder.
     * 
     * @param startDate
     */
    public void setStartDate(java.util.Calendar startDate) {
        this.startDate = startDate;
    }


    /**
     * Gets the clsDate value for this WorkOrder.
     * 
     * @return clsDate
     */
    public java.util.Date getClsDate() {
        return clsDate;
    }


    /**
     * Sets the clsDate value for this WorkOrder.
     * 
     * @param clsDate
     */
    public void setClsDate(java.util.Date clsDate) {
        this.clsDate = clsDate;
    }


    /**
     * Gets the estDate value for this WorkOrder.
     * 
     * @return estDate
     */
    public java.util.Date getEstDate() {
        return estDate;
    }


    /**
     * Sets the estDate value for this WorkOrder.
     * 
     * @param estDate
     */
    public void setEstDate(java.util.Date estDate) {
        this.estDate = estDate;
    }


    /**
     * Gets the actionCode value for this WorkOrder.
     * 
     * @return actionCode
     */
    public com.cannontech.multispeak.ActionCode getActionCode() {
        return actionCode;
    }


    /**
     * Sets the actionCode value for this WorkOrder.
     * 
     * @param actionCode
     */
    public void setActionCode(com.cannontech.multispeak.ActionCode actionCode) {
        this.actionCode = actionCode;
    }


    /**
     * Gets the laborCategoryID value for this WorkOrder.
     * 
     * @return laborCategoryID
     */
    public java.lang.String getLaborCategoryID() {
        return laborCategoryID;
    }


    /**
     * Sets the laborCategoryID value for this WorkOrder.
     * 
     * @param laborCategoryID
     */
    public void setLaborCategoryID(java.lang.String laborCategoryID) {
        this.laborCategoryID = laborCategoryID;
    }


    /**
     * Gets the atc value for this WorkOrder.
     * 
     * @return atc
     */
    public java.lang.Float getAtc() {
        return atc;
    }


    /**
     * Sets the atc value for this WorkOrder.
     * 
     * @param atc
     */
    public void setAtc(java.lang.Float atc) {
        this.atc = atc;
    }


    /**
     * Gets the loadDistrict value for this WorkOrder.
     * 
     * @return loadDistrict
     */
    public com.cannontech.multispeak.LoadDistrict getLoadDistrict() {
        return loadDistrict;
    }


    /**
     * Sets the loadDistrict value for this WorkOrder.
     * 
     * @param loadDistrict
     */
    public void setLoadDistrict(com.cannontech.multispeak.LoadDistrict loadDistrict) {
        this.loadDistrict = loadDistrict;
    }


    /**
     * Gets the constGrade value for this WorkOrder.
     * 
     * @return constGrade
     */
    public com.cannontech.multispeak.ConstGrade getConstGrade() {
        return constGrade;
    }


    /**
     * Sets the constGrade value for this WorkOrder.
     * 
     * @param constGrade
     */
    public void setConstGrade(com.cannontech.multispeak.ConstGrade constGrade) {
        this.constGrade = constGrade;
    }


    /**
     * Gets the duration value for this WorkOrder.
     * 
     * @return duration
     */
    public java.lang.Float getDuration() {
        return duration;
    }


    /**
     * Sets the duration value for this WorkOrder.
     * 
     * @param duration
     */
    public void setDuration(java.lang.Float duration) {
        this.duration = duration;
    }


    /**
     * Gets the accountNumber value for this WorkOrder.
     * 
     * @return accountNumber
     */
    public java.lang.String getAccountNumber() {
        return accountNumber;
    }


    /**
     * Sets the accountNumber value for this WorkOrder.
     * 
     * @param accountNumber
     */
    public void setAccountNumber(java.lang.String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the stationList value for this WorkOrder.
     * 
     * @return stationList
     */
    public com.cannontech.multispeak.ArrayOfStation getStationList() {
        return stationList;
    }


    /**
     * Sets the stationList value for this WorkOrder.
     * 
     * @param stationList
     */
    public void setStationList(com.cannontech.multispeak.ArrayOfStation stationList) {
        this.stationList = stationList;
    }


    /**
     * Gets the backgroundGraphics value for this WorkOrder.
     * 
     * @return backgroundGraphics
     */
    public com.cannontech.multispeak.BackgroundGraphics getBackgroundGraphics() {
        return backgroundGraphics;
    }


    /**
     * Sets the backgroundGraphics value for this WorkOrder.
     * 
     * @param backgroundGraphics
     */
    public void setBackgroundGraphics(com.cannontech.multispeak.BackgroundGraphics backgroundGraphics) {
        this.backgroundGraphics = backgroundGraphics;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof WorkOrder)) return false;
        WorkOrder other = (WorkOrder) obj;
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
            ((this.jobDescr==null && other.getJobDescr()==null) || 
             (this.jobDescr!=null &&
              this.jobDescr.equals(other.getJobDescr()))) &&
            ((this.statusCode==null && other.getStatusCode()==null) || 
             (this.statusCode!=null &&
              this.statusCode.equals(other.getStatusCode()))) &&
            ((this.designCode==null && other.getDesignCode()==null) || 
             (this.designCode!=null &&
              this.designCode.equals(other.getDesignCode()))) &&
            ((this.lastName==null && other.getLastName()==null) || 
             (this.lastName!=null &&
              this.lastName.equals(other.getLastName()))) &&
            ((this.firstName==null && other.getFirstName()==null) || 
             (this.firstName!=null &&
              this.firstName.equals(other.getFirstName()))) &&
            ((this.mName==null && other.getMName()==null) || 
             (this.mName!=null &&
              this.mName.equals(other.getMName()))) &&
            ((this.homeAc==null && other.getHomeAc()==null) || 
             (this.homeAc!=null &&
              this.homeAc.equals(other.getHomeAc()))) &&
            ((this.homePhone==null && other.getHomePhone()==null) || 
             (this.homePhone!=null &&
              this.homePhone.equals(other.getHomePhone()))) &&
            ((this.dayAc==null && other.getDayAc()==null) || 
             (this.dayAc!=null &&
              this.dayAc.equals(other.getDayAc()))) &&
            ((this.dayPhone==null && other.getDayPhone()==null) || 
             (this.dayPhone!=null &&
              this.dayPhone.equals(other.getDayPhone()))) &&
            ((this.billAddr1==null && other.getBillAddr1()==null) || 
             (this.billAddr1!=null &&
              this.billAddr1.equals(other.getBillAddr1()))) &&
            ((this.billAddr2==null && other.getBillAddr2()==null) || 
             (this.billAddr2!=null &&
              this.billAddr2.equals(other.getBillAddr2()))) &&
            ((this.billCity==null && other.getBillCity()==null) || 
             (this.billCity!=null &&
              this.billCity.equals(other.getBillCity()))) &&
            ((this.billState==null && other.getBillState()==null) || 
             (this.billState!=null &&
              this.billState.equals(other.getBillState()))) &&
            ((this.billZip==null && other.getBillZip()==null) || 
             (this.billZip!=null &&
              this.billZip.equals(other.getBillZip()))) &&
            ((this.custID==null && other.getCustID()==null) || 
             (this.custID!=null &&
              this.custID.equals(other.getCustID()))) &&
            ((this.servAddr1==null && other.getServAddr1()==null) || 
             (this.servAddr1!=null &&
              this.servAddr1.equals(other.getServAddr1()))) &&
            ((this.servAddr2==null && other.getServAddr2()==null) || 
             (this.servAddr2!=null &&
              this.servAddr2.equals(other.getServAddr2()))) &&
            ((this.servCity==null && other.getServCity()==null) || 
             (this.servCity!=null &&
              this.servCity.equals(other.getServCity()))) &&
            ((this.servState==null && other.getServState()==null) || 
             (this.servState!=null &&
              this.servState.equals(other.getServState()))) &&
            ((this.servZip==null && other.getServZip()==null) || 
             (this.servZip!=null &&
              this.servZip.equals(other.getServZip()))) &&
            ((this.county==null && other.getCounty()==null) || 
             (this.county!=null &&
              this.county.equals(other.getCounty()))) &&
            ((this.section==null && other.getSection()==null) || 
             (this.section!=null &&
              this.section.equals(other.getSection()))) &&
            ((this.township==null && other.getTownship()==null) || 
             (this.township!=null &&
              this.township.equals(other.getTownship()))) &&
            ((this.range==null && other.getRange()==null) || 
             (this.range!=null &&
              this.range.equals(other.getRange()))) &&
            ((this.townshipName==null && other.getTownshipName()==null) || 
             (this.townshipName!=null &&
              this.townshipName.equals(other.getTownshipName()))) &&
            ((this.subdivision==null && other.getSubdivision()==null) || 
             (this.subdivision!=null &&
              this.subdivision.equals(other.getSubdivision()))) &&
            ((this.block==null && other.getBlock()==null) || 
             (this.block!=null &&
              this.block.equals(other.getBlock()))) &&
            ((this.lot==null && other.getLot()==null) || 
             (this.lot!=null &&
              this.lot.equals(other.getLot()))) &&
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
            ((this.systemVolts==null && other.getSystemVolts()==null) || 
             (this.systemVolts!=null &&
              this.systemVolts.equals(other.getSystemVolts()))) &&
            ((this.contractor==null && other.getContractor()==null) || 
             (this.contractor!=null &&
              this.contractor.equals(other.getContractor()))) &&
            ((this.contrAc==null && other.getContrAc()==null) || 
             (this.contrAc!=null &&
              this.contrAc.equals(other.getContrAc()))) &&
            ((this.contrPhone==null && other.getContrPhone()==null) || 
             (this.contrPhone!=null &&
              this.contrPhone.equals(other.getContrPhone()))) &&
            ((this.electrician==null && other.getElectrician()==null) || 
             (this.electrician!=null &&
              this.electrician.equals(other.getElectrician()))) &&
            ((this.elecAc==null && other.getElecAc()==null) || 
             (this.elecAc!=null &&
              this.elecAc.equals(other.getElecAc()))) &&
            ((this.elecPhone==null && other.getElecPhone()==null) || 
             (this.elecPhone!=null &&
              this.elecPhone.equals(other.getElecPhone()))) &&
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
            ((this.engrAsnd==null && other.getEngrAsnd()==null) || 
             (this.engrAsnd!=null &&
              this.engrAsnd.equals(other.getEngrAsnd()))) &&
            ((this.cfmsCode==null && other.getCfmsCode()==null) || 
             (this.cfmsCode!=null &&
              this.cfmsCode.equals(other.getCfmsCode()))) &&
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
            ((this.warehouseID==null && other.getWarehouseID()==null) || 
             (this.warehouseID!=null &&
              this.warehouseID.equals(other.getWarehouseID()))) &&
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
            ((this.actionCode==null && other.getActionCode()==null) || 
             (this.actionCode!=null &&
              this.actionCode.equals(other.getActionCode()))) &&
            ((this.laborCategoryID==null && other.getLaborCategoryID()==null) || 
             (this.laborCategoryID!=null &&
              this.laborCategoryID.equals(other.getLaborCategoryID()))) &&
            ((this.atc==null && other.getAtc()==null) || 
             (this.atc!=null &&
              this.atc.equals(other.getAtc()))) &&
            ((this.loadDistrict==null && other.getLoadDistrict()==null) || 
             (this.loadDistrict!=null &&
              this.loadDistrict.equals(other.getLoadDistrict()))) &&
            ((this.constGrade==null && other.getConstGrade()==null) || 
             (this.constGrade!=null &&
              this.constGrade.equals(other.getConstGrade()))) &&
            ((this.duration==null && other.getDuration()==null) || 
             (this.duration!=null &&
              this.duration.equals(other.getDuration()))) &&
            ((this.accountNumber==null && other.getAccountNumber()==null) || 
             (this.accountNumber!=null &&
              this.accountNumber.equals(other.getAccountNumber()))) &&
            ((this.stationList==null && other.getStationList()==null) || 
             (this.stationList!=null &&
              this.stationList.equals(other.getStationList()))) &&
            ((this.backgroundGraphics==null && other.getBackgroundGraphics()==null) || 
             (this.backgroundGraphics!=null &&
              this.backgroundGraphics.equals(other.getBackgroundGraphics())));
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
        if (getJobDescr() != null) {
            _hashCode += getJobDescr().hashCode();
        }
        if (getStatusCode() != null) {
            _hashCode += getStatusCode().hashCode();
        }
        if (getDesignCode() != null) {
            _hashCode += getDesignCode().hashCode();
        }
        if (getLastName() != null) {
            _hashCode += getLastName().hashCode();
        }
        if (getFirstName() != null) {
            _hashCode += getFirstName().hashCode();
        }
        if (getMName() != null) {
            _hashCode += getMName().hashCode();
        }
        if (getHomeAc() != null) {
            _hashCode += getHomeAc().hashCode();
        }
        if (getHomePhone() != null) {
            _hashCode += getHomePhone().hashCode();
        }
        if (getDayAc() != null) {
            _hashCode += getDayAc().hashCode();
        }
        if (getDayPhone() != null) {
            _hashCode += getDayPhone().hashCode();
        }
        if (getBillAddr1() != null) {
            _hashCode += getBillAddr1().hashCode();
        }
        if (getBillAddr2() != null) {
            _hashCode += getBillAddr2().hashCode();
        }
        if (getBillCity() != null) {
            _hashCode += getBillCity().hashCode();
        }
        if (getBillState() != null) {
            _hashCode += getBillState().hashCode();
        }
        if (getBillZip() != null) {
            _hashCode += getBillZip().hashCode();
        }
        if (getCustID() != null) {
            _hashCode += getCustID().hashCode();
        }
        if (getServAddr1() != null) {
            _hashCode += getServAddr1().hashCode();
        }
        if (getServAddr2() != null) {
            _hashCode += getServAddr2().hashCode();
        }
        if (getServCity() != null) {
            _hashCode += getServCity().hashCode();
        }
        if (getServState() != null) {
            _hashCode += getServState().hashCode();
        }
        if (getServZip() != null) {
            _hashCode += getServZip().hashCode();
        }
        if (getCounty() != null) {
            _hashCode += getCounty().hashCode();
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
        if (getTownshipName() != null) {
            _hashCode += getTownshipName().hashCode();
        }
        if (getSubdivision() != null) {
            _hashCode += getSubdivision().hashCode();
        }
        if (getBlock() != null) {
            _hashCode += getBlock().hashCode();
        }
        if (getLot() != null) {
            _hashCode += getLot().hashCode();
        }
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
        if (getSystemVolts() != null) {
            _hashCode += getSystemVolts().hashCode();
        }
        if (getContractor() != null) {
            _hashCode += getContractor().hashCode();
        }
        if (getContrAc() != null) {
            _hashCode += getContrAc().hashCode();
        }
        if (getContrPhone() != null) {
            _hashCode += getContrPhone().hashCode();
        }
        if (getElectrician() != null) {
            _hashCode += getElectrician().hashCode();
        }
        if (getElecAc() != null) {
            _hashCode += getElecAc().hashCode();
        }
        if (getElecPhone() != null) {
            _hashCode += getElecPhone().hashCode();
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
        if (getEngrAsnd() != null) {
            _hashCode += getEngrAsnd().hashCode();
        }
        if (getCfmsCode() != null) {
            _hashCode += getCfmsCode().hashCode();
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
        if (getWarehouseID() != null) {
            _hashCode += getWarehouseID().hashCode();
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
        if (getActionCode() != null) {
            _hashCode += getActionCode().hashCode();
        }
        if (getLaborCategoryID() != null) {
            _hashCode += getLaborCategoryID().hashCode();
        }
        if (getAtc() != null) {
            _hashCode += getAtc().hashCode();
        }
        if (getLoadDistrict() != null) {
            _hashCode += getLoadDistrict().hashCode();
        }
        if (getConstGrade() != null) {
            _hashCode += getConstGrade().hashCode();
        }
        if (getDuration() != null) {
            _hashCode += getDuration().hashCode();
        }
        if (getAccountNumber() != null) {
            _hashCode += getAccountNumber().hashCode();
        }
        if (getStationList() != null) {
            _hashCode += getStationList().hashCode();
        }
        if (getBackgroundGraphics() != null) {
            _hashCode += getBackgroundGraphics().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(WorkOrder.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "workOrder"));
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
        elemField.setFieldName("jobDescr");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "jobDescr"));
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
        elemField.setFieldName("designCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "designCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firstName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "firstName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("homeAc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "homeAc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("homePhone");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "homePhone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dayAc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "dayAc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dayPhone");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "dayPhone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billAddr1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billAddr1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billAddr2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billAddr2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billCity");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billCity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billState");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billZip");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billZip"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("custID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "custID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("servAddr1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servAddr1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("servAddr2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servAddr2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("servCity");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servCity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("servState");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("servZip");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servZip"));
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
        elemField.setFieldName("townshipName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "townshipName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subdivision");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "subdivision"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("block");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "block"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lot");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lot"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
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
        elemField.setFieldName("systemVolts");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "systemVolts"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("contractor");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "contractor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("contrAc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "contrAc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("contrPhone");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "contrPhone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("electrician");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electrician"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("elecAc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "elecAc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("elecPhone");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "elecPhone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
        elemField.setFieldName("engrAsnd");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "engrAsnd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cfmsCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cfmsCode"));
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
        elemField.setFieldName("warehouseID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "warehouseID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("estDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "estDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("actionCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "actionCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "actionCode"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("laborCategoryID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "laborCategoryID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("atc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "atc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loadDistrict");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadDistrict"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadDistrict"));
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
        elemField.setFieldName("duration");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "duration"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
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
        elemField.setFieldName("stationList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "stationList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfStation"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("backgroundGraphics");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "backgroundGraphics"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "backgroundGraphics"));
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
