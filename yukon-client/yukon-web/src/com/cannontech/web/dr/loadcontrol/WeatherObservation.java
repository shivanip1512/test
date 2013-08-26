package com.cannontech.web.dr.loadcontrol;

public class WeatherObservation {

	private String stationId = null;
	private String locationDesc = null;
	private Double latitude = null;
	private Double longitude = null;
	private String observationTime = null;
	private String conditionDesc = null;
	private Double temperature_F = null;
	private Double temperature_C = null;
	private Double relativeHumidity = null;
	private String windDirection = null;
	private Double windDegrees = null;
	private Double windSpeedMPH = null;
	private Double windSpeedKT = null;
	private Double dewPoint_F = null;
	private Double dewPoint_C = null;
	private Double heatIndex_F = null;
	private Double heatIndex_C = null;
	private Double visibility = null;

	public WeatherObservation() {
		
	}
	
	public WeatherObservation(String stationId, String locationDesc,
			Double latitude, Double longitude, String observationTime,
			String conditionsDesc, Double temperature_F, Double temperature_C,
			Double relativeHumidity, String windDirection, Double windDegrees,
			Double windSpeedMPH, Double windSpeedKT, Double dewPoint_F,
			Double dewPoint_C, Double heatIndex_F, Double heatIndex_C,
			Double visibility) {
		this.stationId = stationId;
		this.locationDesc = locationDesc;
		this.latitude = latitude;
		this.longitude = longitude;
		this.observationTime = observationTime;
		this.conditionDesc = conditionsDesc;
		this.temperature_F = temperature_F;
		this.temperature_C = temperature_C;
		this.relativeHumidity = relativeHumidity;
		this.windDirection = windDirection;
		this.windDegrees = windDegrees;
		this.windSpeedMPH = windSpeedMPH;
		this.windSpeedKT = windSpeedKT;
		this.dewPoint_F = dewPoint_F;
		this.dewPoint_C = dewPoint_C;
		this.heatIndex_F = heatIndex_F;
		this.heatIndex_C = heatIndex_C;
		this.visibility = visibility;
		
	}
	
	public void setStationId(String stationId) {
		this.stationId = stationId;
	}
	
	public void setLocationDesc(String locationDesc) {
		this.locationDesc = locationDesc;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	public void setObservationTime(String observationTime) {
		this.observationTime = observationTime;
	}
	
	public void setConditionDesc(String conditionDesc) {
		this.conditionDesc = conditionDesc;
	}
	
	public void setTemperatureF(Double temperature_F) {
		this.temperature_F = temperature_F;
	}
	
	public void setTemperatureC(Double temperature_C) {
		this.temperature_C = temperature_C;
	}
	
	public void setRelativeHumidity(Double relativeHumidity) {
		this.relativeHumidity = relativeHumidity;
	}
	
	public void setWindDirection(String windDirection) {
		this.windDirection = windDirection;
	}
	
	public void setWindDegrees(Double windDegrees) {
		this.windDegrees = windDegrees;
	}

	public void setWindSpeedMPH(Double windSpeedMPH) {
		this.windSpeedMPH = windSpeedMPH;
	}
	
	public void setWindSpeedKT(Double windSpeedKT) {
		this.windSpeedKT = windSpeedKT;
	}
	
	public void setDewPointF(Double dewPoint_F) {
		this.dewPoint_F = dewPoint_F;
	}
	
	public void setDewPointC(Double dewPoint_C) {
		this.dewPoint_C = dewPoint_C;
	}
	
	public void setHeatIndexF(Double heatIndex_F) {
		this.heatIndex_F = heatIndex_F;
	}

	public void setHeatIndexC(Double heatIndex_C) {
		this.heatIndex_C = heatIndex_C;
	}
	
	public void setVsibility(Double visibility) {
		this.visibility = visibility;
	}

	public String getStationId() {
		return this.stationId;
	}
	
	public String getLocationDesc() {
		return this.locationDesc;
	}

	public Double getLatitude() {
		return this.latitude;
	}

	public Double getLongitude() {
		return this.longitude;
	}

	public String getObservationTime() {
		return this.observationTime;
	}

	public String getConditionDesc() {
		return this.conditionDesc;
	}

	public Double getTemperatureF() {
		return this.temperature_F;
	}

	public Double getTemperatureC() {
		return this.temperature_C;
	}

	public Double getRelativeHumidity() {
		return this.relativeHumidity;
	}

	public String getWindDirection() {
		return this.windDirection;
	}

	public Double getWindDegrees() {
		return this.windDegrees;
	}

	public Double getWindSpeedMPH() {
		return this.windSpeedMPH;
	}

	public Double getWindSpeedKT() {
		return this.windSpeedKT;
	}

	public Double getDewPointF() {
		return this.dewPoint_F;
	}

	public Double getDewPointC() {
		return this.dewPoint_C;
	}

	public Double getHeatIndexF() {
		return this.heatIndex_F;
	}

	public Double getHeatIndexC() {
		return this.heatIndex_C;
	}

	public Double getVisibility() {
		return this.visibility;
	}

}
