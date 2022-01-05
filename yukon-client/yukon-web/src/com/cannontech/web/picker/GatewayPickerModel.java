package com.cannontech.web.picker;

public class GatewayPickerModel {
	private Integer gatewayId;
	private String gatewayName;
	private String nmIpAddress;
	private Integer nmPort;
	
	public GatewayPickerModel(int gatewayId, String gatewayName, String nmIpAddress, int nmPort) {
		this.gatewayId = gatewayId;
		this.gatewayName = gatewayName;
		this.nmIpAddress = nmIpAddress;
		this.nmPort = nmPort;
	}

	public String getNmIpAddress() {
		return nmIpAddress;
	}
	public void setNmIpAddress(String nmIpAddress) {
		this.nmIpAddress = nmIpAddress;
	}
	public Integer getNmPort() {
		return nmPort;
	}
	public void setNmPort(Integer nmPort) {
		this.nmPort = nmPort;
	}
	public Integer getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(Integer gatewayId) {
		this.gatewayId = gatewayId;
	}
	public String getGatewayName() {
		return gatewayName;
	}
	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}
}
