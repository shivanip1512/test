package com.cannontech.web.cayenta.model;

import org.springframework.core.style.ToStringCreator;

public class CayentaLocationInfo {

	private String locationCity;
	private String locationZipCode;
	private String locationState;
	private String mapNumber;
	
	public String getLocationCity() {
		return locationCity;
	}
	public void setLocationCity(String locationCity) {
		this.locationCity = locationCity;
	}
	public String getLocationZipCode() {
		return locationZipCode;
	}
	public void setLocationZipCode(String locationZipCode) {
		this.locationZipCode = locationZipCode;
	}
	public String getLocationState() {
		return locationState;
	}
	public void setLocationState(String locationState) {
		this.locationState = locationState;
	}
	public String getMapNumber() {
		return mapNumber;
	}
	public void setMapNumber(String mapNumber) {
		this.mapNumber = mapNumber;
	}
	
	public String toString() {

		ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("locationCity", getLocationCity());
        tsc.append("locationZipCode", getLocationZipCode());
        tsc.append("locationState", getLocationState());
        return tsc.toString();
	}
}
