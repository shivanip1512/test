package com.cannontech.capcontrol.model;

import java.util.Date;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;

public class CapbankAdditional implements YukonPao {

	PaoIdentifier paoIdentifier;
	private int maintenanceAreaId;
	private int poleNumber;
	private int commStrength;
	private double longitude;
	private double latitude;
	private Date lastMaintenanceVisit;
	private Date cbcInstallDate;
	private Date lastInspection;
	private Date opCountResetDate;
	private String extAntenna;
	private String antennaType;
	private String capbankConfig;
	private String commMedium;
	private String driveDirections;
	private String potentialTransformer;
	private String maintenanceRequired;
	private String otherComments;
	private String opTeamComments;
	
	public CapbankAdditional(PaoIdentifier paoIdentifier) {
		this.paoIdentifier = paoIdentifier;
	}
	
	public int getPaoId() {
		return paoIdentifier.getPaoId();
	}

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

	public String getDriveDirections() {
		return driveDirections;
	}

	public void setDriveDirections(String driveDirections) {
		this.driveDirections = driveDirections;
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
	
	public int getCommStrength() {
		return commStrength;
	}

	public void setCommStrength(int commStrength) {
		this.commStrength = commStrength;
	}

	public String getExtAntenna() {
		return extAntenna;
	}

	public void setExtAntenna(String extAntenna) {
		this.extAntenna = extAntenna;
	}

	public String getAntennaType() {
		return antennaType;
	}

	public void setAntennaType(String antennaType) {
		this.antennaType = antennaType;
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

	public String getPotentialTransformer() {
		return potentialTransformer;
	}

	public void setPotentialTransformer(String potentialTransformer) {
		this.potentialTransformer = potentialTransformer;
	}

	public String getMaintenanceRequired() {
		return maintenanceRequired;
	}

	public void setMaintenanceRequired(String maintenanceRequired) {
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

	public Date getCbcInstallDate() {
		return cbcInstallDate;
	}

	public void setCbcInstallDate(Date cbcInstallDate) {
		this.cbcInstallDate = cbcInstallDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((antennaType == null) ? 0 : antennaType.hashCode());
		result = prime * result
				+ ((capbankConfig == null) ? 0 : capbankConfig.hashCode());
		result = prime * result
				+ ((cbcInstallDate == null) ? 0 : cbcInstallDate.hashCode());
		result = prime * result
				+ ((commMedium == null) ? 0 : commMedium.hashCode());
		result = prime * result + commStrength;
		result = prime * result
				+ ((driveDirections == null) ? 0 : driveDirections.hashCode());
		result = prime * result
				+ ((extAntenna == null) ? 0 : extAntenna.hashCode());
		result = prime * result
				+ ((lastInspection == null) ? 0 : lastInspection.hashCode());
		result = prime
				* result
				+ ((lastMaintenanceVisit == null) ? 0 : lastMaintenanceVisit
						.hashCode());
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + maintenanceAreaId;
		result = prime
				* result
				+ ((maintenanceRequired == null) ? 0 : maintenanceRequired
						.hashCode());
		result = prime
				* result
				+ ((opCountResetDate == null) ? 0 : opCountResetDate.hashCode());
		result = prime * result
				+ ((opTeamComments == null) ? 0 : opTeamComments.hashCode());
		result = prime * result
				+ ((otherComments == null) ? 0 : otherComments.hashCode());
		result = prime * result + poleNumber;
		result = prime
				* result
				+ ((potentialTransformer == null) ? 0 : potentialTransformer
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CapbankAdditional other = (CapbankAdditional) obj;
		if (antennaType == null) {
			if (other.antennaType != null)
				return false;
		} else if (!antennaType.equals(other.antennaType))
			return false;
		if (capbankConfig == null) {
			if (other.capbankConfig != null)
				return false;
		} else if (!capbankConfig.equals(other.capbankConfig))
			return false;
		if (cbcInstallDate == null) {
			if (other.cbcInstallDate != null)
				return false;
		} else if (!cbcInstallDate.equals(other.cbcInstallDate))
			return false;
		if (commMedium == null) {
			if (other.commMedium != null)
				return false;
		} else if (!commMedium.equals(other.commMedium))
			return false;
		if (commStrength != other.commStrength)
			return false;
		if (driveDirections == null) {
			if (other.driveDirections != null)
				return false;
		} else if (!driveDirections.equals(other.driveDirections))
			return false;
		if (extAntenna == null) {
			if (other.extAntenna != null)
				return false;
		} else if (!extAntenna.equals(other.extAntenna))
			return false;
		if (lastInspection == null) {
			if (other.lastInspection != null)
				return false;
		} else if (!lastInspection.equals(other.lastInspection))
			return false;
		if (lastMaintenanceVisit == null) {
			if (other.lastMaintenanceVisit != null)
				return false;
		} else if (!lastMaintenanceVisit.equals(other.lastMaintenanceVisit))
			return false;
		if (Double.doubleToLongBits(latitude) != Double
				.doubleToLongBits(other.latitude))
			return false;
		if (Double.doubleToLongBits(longitude) != Double
				.doubleToLongBits(other.longitude))
			return false;
		if (maintenanceAreaId != other.maintenanceAreaId)
			return false;
		if (maintenanceRequired == null) {
			if (other.maintenanceRequired != null)
				return false;
		} else if (!maintenanceRequired.equals(other.maintenanceRequired))
			return false;
		if (opCountResetDate == null) {
			if (other.opCountResetDate != null)
				return false;
		} else if (!opCountResetDate.equals(other.opCountResetDate))
			return false;
		if (opTeamComments == null) {
			if (other.opTeamComments != null)
				return false;
		} else if (!opTeamComments.equals(other.opTeamComments))
			return false;
		if (otherComments == null) {
			if (other.otherComments != null)
				return false;
		} else if (!otherComments.equals(other.otherComments))
			return false;
		if (poleNumber != other.poleNumber)
			return false;
		if (potentialTransformer == null) {
			if (other.potentialTransformer != null)
				return false;
		} else if (!potentialTransformer.equals(other.potentialTransformer))
			return false;
		return true;
	}

	public PaoIdentifier getPaoIdentifier() {
		return paoIdentifier;
	}
}
