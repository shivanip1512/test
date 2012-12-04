package com.cannontech.common.pao.model;

import org.joda.time.Instant;

import com.cannontech.common.pao.annotation.YukonPaoField;
import com.cannontech.common.pao.annotation.YukonPaoPart;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.capcontrol.CapBankAdditional;
import com.google.common.base.Objects;

@YukonPaoPart(idColumnName = "deviceId")
public class CompleteCapBankAdditional {
    private int maintenanceAreaId = 0;
    private int poleNumber = 0;
    private int commStrength = 0;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private Instant lastMaintenanceVisit = new Instant(CapBankAdditional.getBeginningTime());
    private Instant lastInspection = new Instant(CapBankAdditional.getBeginningTime());
    private Instant opCountResetDate = new Instant(CapBankAdditional.getBeginningTime());
    private Instant cbcInstallDate = new Instant(CapBankAdditional.getBeginningTime());
    private String capbankConfig = CtiUtilities.STRING_NONE;
    private String commMedium = CtiUtilities.STRING_NONE;
    private String driveDirections = CtiUtilities.STRING_NONE;
    private String potentialTransformer = CtiUtilities.STRING_NONE;
    private String opTeamComments = CtiUtilities.STRING_NONE;
    private String antennaType = CtiUtilities.STRING_NONE;
    private String otherComments = CtiUtilities.STRING_NONE;
    private boolean extAntenna = false;
    private boolean maintenanceRequired = false;

    @YukonPaoField
    public int getMaintenanceAreaId() {
        return maintenanceAreaId;
    }

    public void setMaintenanceAreaId(int maintenanceAreaId) {
        this.maintenanceAreaId = maintenanceAreaId;
    }

    @YukonPaoField
    public int getPoleNumber() {
        return poleNumber;
    }

    public void setPoleNumber(int poleNumber) {
        this.poleNumber = poleNumber;
    }

    @YukonPaoField
    public int getCommStrength() {
        return commStrength;
    }

    public void setCommStrength(int commStrength) {
        this.commStrength = commStrength;
    }

    @YukonPaoField
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @YukonPaoField
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @YukonPaoField(columnName="LastMaintVisit")
    public Instant getLastMaintenanceVisit() {
        return lastMaintenanceVisit;
    }

    public void setLastMaintenanceVisit(Instant lastMaintenanceVisit) {
        this.lastMaintenanceVisit = lastMaintenanceVisit;
    }

    @YukonPaoField(columnName="LastInspVisit")
    public Instant getLastInspection() {
        return lastInspection;
    }

    public void setLastInspection(Instant lastInspection) {
        this.lastInspection = lastInspection;
    }

    @YukonPaoField
    public Instant getOpCountResetDate() {
        return opCountResetDate;
    }

    public void setOpCountResetDate(Instant opCountResetDate) {
        this.opCountResetDate = opCountResetDate;
    }

    @YukonPaoField(columnName="CbcBattInstallDate")
    public Instant getCbcInstallDate() {
        return cbcInstallDate;
    }

    public void setCbcInstallDate(Instant cbcInstallDate) {
        this.cbcInstallDate = cbcInstallDate;
    }

    @YukonPaoField
    public String getCapbankConfig() {
        return capbankConfig;
    }

    public void setCapbankConfig(String capbankConfig) {
        this.capbankConfig = capbankConfig;
    }

    @YukonPaoField
    public String getCommMedium() {
        return commMedium;
    }

    public void setCommMedium(String commMedium) {
        this.commMedium = commMedium;
    }

    @YukonPaoField
    public String getDriveDirections() {
        return driveDirections;
    }

    public void setDriveDirections(String driveDirections) {
        this.driveDirections = driveDirections;
    }

    @YukonPaoField
    public String getPotentialTransformer() {
        return potentialTransformer;
    }

    public void setPotentialTransformer(String potentialTransformer) {
        this.potentialTransformer = potentialTransformer;
    }

    @YukonPaoField
    public String getOpTeamComments() {
        return opTeamComments;
    }

    public void setOpTeamComments(String opTeamComments) {
        this.opTeamComments = opTeamComments;
    }

    @YukonPaoField
    public String getAntennaType() {
        return antennaType;
    }

    public void setAntennaType(String antennaType) {
        this.antennaType = antennaType;
    }

    @YukonPaoField
    public String getOtherComments() {
        return otherComments;
    }

    public void setOtherComments(String otherComments) {
        this.otherComments = otherComments;
    }

    @YukonPaoField
    public boolean isExtAntenna() {
        return extAntenna;
    }

    public void setExtAntenna(boolean extAntenna) {
        this.extAntenna = extAntenna;
    }

    @YukonPaoField(columnName="MaintenanceReqPend")
    public boolean isMaintenanceRequired() {
        return maintenanceRequired;
    }

    public void setMaintenanceRequired(boolean maintenanceRequired) {
        this.maintenanceRequired = maintenanceRequired;
    }

    @Override
    public int hashCode(){
        return Objects.hashCode(maintenanceAreaId, poleNumber, commStrength, latitude, longitude, 
                                lastMaintenanceVisit, lastInspection, opCountResetDate, 
                                cbcInstallDate, capbankConfig, commMedium, driveDirections, 
                                potentialTransformer, opTeamComments, antennaType, otherComments, 
                                extAntenna, maintenanceRequired);
    }
    
    @Override
    public boolean equals(Object object){
        if (object instanceof CompleteCapBankAdditional) {
            CompleteCapBankAdditional that = (CompleteCapBankAdditional) object;
            return Objects.equal(this.maintenanceAreaId, that.maintenanceAreaId)
                && Objects.equal(this.poleNumber, that.poleNumber)
                && Objects.equal(this.commStrength, that.commStrength)
                && Objects.equal(this.latitude, that.latitude)
                && Objects.equal(this.longitude, that.longitude)
                && Objects.equal(this.lastMaintenanceVisit, that.lastMaintenanceVisit)
                && Objects.equal(this.lastInspection, that.lastInspection)
                && Objects.equal(this.opCountResetDate, that.opCountResetDate)
                && Objects.equal(this.cbcInstallDate, that.cbcInstallDate)
                && Objects.equal(this.capbankConfig, that.capbankConfig)
                && Objects.equal(this.commMedium, that.commMedium)
                && Objects.equal(this.driveDirections, that.driveDirections)
                && Objects.equal(this.potentialTransformer, that.potentialTransformer)
                && Objects.equal(this.opTeamComments, that.opTeamComments)
                && Objects.equal(this.antennaType, that.antennaType)
                && Objects.equal(this.otherComments, that.otherComments)
                && Objects.equal(this.extAntenna, that.extAntenna)
                && Objects.equal(this.maintenanceRequired, that.maintenanceRequired);
        }
        return false;
    }

    @Override
    public String toString() {
        return "CompleteCapBankAdditional [maintenanceAreaId=" + maintenanceAreaId
               + ", poleNumber=" + poleNumber + ", commStrength=" + commStrength + ", latitude="
               + latitude + ", longitude=" + longitude + ", lastMaintenanceVisit="
               + lastMaintenanceVisit + ", lastInspection=" + lastInspection
               + ", opCountResetDate=" + opCountResetDate + ", cbcInstallDate=" + cbcInstallDate
               + ", capbankConfig=" + capbankConfig + ", commMedium=" + commMedium
               + ", driveDirections=" + driveDirections + ", potentialTransformer="
               + potentialTransformer + ", opTeamComments=" + opTeamComments + ", antennaType="
               + antennaType + ", otherComments=" + otherComments + ", extAntenna=" + extAntenna
               + ", maintenanceRequired=" + maintenanceRequired + "]";
    }
}
