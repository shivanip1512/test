package com.cannontech.capcontrol.dao.providers.fields;

import java.util.Date;

import com.cannontech.common.pao.service.PaoTemplatePart;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.db.capcontrol.CapBankAdditional;

public class CapbankAdditionalFields implements PaoTemplatePart {
	
	private int maintenanceAreaId = 0;
	private int poleNumber = 0;
	private int commStrength = 0;
	private double latitude = 0.0;
	private double longitude = 0.0;
	private Date lastMaintenanceVisit = CapBankAdditional.getBeginningTime();
	private Date lastInspection = CapBankAdditional.getBeginningTime();
	private Date opCountResetDate = CapBankAdditional.getBeginningTime();
	private Date cbcInstallDate = CapBankAdditional.getBeginningTime();
	private String capbankConfig = CtiUtilities.STRING_NONE;
	private String commMedium = CtiUtilities.STRING_NONE;
	private String driveDirections = CtiUtilities.STRING_NONE;
	private String potentialTransformer = CtiUtilities.STRING_NONE;
	private String opTeamComments = CtiUtilities.STRING_NONE;
	private String antennaType = CtiUtilities.STRING_NONE;
	private String otherComments = CtiUtilities.STRING_NONE;
	private YNBoolean extAntenna = YNBoolean.NO;
	private YNBoolean maintenanceRequired = YNBoolean.NO;
	
	public int getMaintenanceAreaId() {
		return maintenanceAreaId;
	}
	
	public void setMaintenanceAreaId(int maintenanceAreaId) {
		this.maintenanceAreaId = maintenanceAreaId;
	}

	public int getPoleNumber() {
		return poleNumber;
	}

	public void setPoleNumber(int poleNumber) {
		this.poleNumber = poleNumber;
	}

	public int getCommStrength() {
		return commStrength;
	}

	public void setCommStrength(int commStrength) {
		this.commStrength = commStrength;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public Date getLastMaintenanceVisit() {
		return lastMaintenanceVisit;
	}

	public void setLastMaintenanceVisit(Date lastMaintenanceVisit) {
		this.lastMaintenanceVisit = lastMaintenanceVisit;
	}

	public Date getLastInspection() {
		return lastInspection;
	}

	public void setLastInspection(Date lastInspection) {
		this.lastInspection = lastInspection;
	}

	public Date getOpCountResetDate() {
		return opCountResetDate;
	}

	public void setOpCountResetDate(Date opCountResetDate) {
		this.opCountResetDate = opCountResetDate;
	}

	public Date getCbcInstallDate() {
		return cbcInstallDate;
	}

	public void setCbcInstallDate(Date cbcInstallDate) {
		this.cbcInstallDate = cbcInstallDate;
	}

	public String getCapbankConfig() {
		return capbankConfig;
	}

	public void setCapbankConfig(String capbankConfig) {
		this.capbankConfig = capbankConfig;
	}

	public String getCommMedium() {
		return commMedium;
	}

	public void setCommMedium(String commMedium) {
		this.commMedium = commMedium;
	}

	public String getDriveDirections() {
		return driveDirections;
	}

	public void setDriveDirections(String driveDirections) {
		this.driveDirections = driveDirections;
	}

	public String getPotentialTransformer() {
		return potentialTransformer;
	}

	public void setPotentialTransformer(String potentialTransformer) {
		this.potentialTransformer = potentialTransformer;
	}

	public YNBoolean getMaintenanceRequired() {
		return maintenanceRequired;
	}

	public void setMaintenanceRequired(YNBoolean maintenanceRequired) {
		this.maintenanceRequired = maintenanceRequired;
	}

	public String getOtherComments() {
		return otherComments;
	}

	public void setOtherComments(String otherComments) {
		this.otherComments = otherComments;
	}

	public String getOpTeamComments() {
		return opTeamComments;
	}

	public void setOpTeamComments(String opTeamComments) {
		this.opTeamComments = opTeamComments;
	}

	public YNBoolean getExtAntenna() {
		return extAntenna;
	}

	public void setExtAntenna(YNBoolean extAntenna) {
		this.extAntenna = extAntenna;
	}

	public String getAntennaType() {
		return antennaType;
	}

	public void setAntennaType(String antennaType) {
		this.antennaType = antennaType;
	}
}
