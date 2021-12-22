package com.cannontech.web.stars.gateway;

import java.util.List;

public class GatewayBulkUpdateModel {
	
	private List<Integer> gatewayIds;
	private String nmIpAddress;
	private Integer nmPort;
	
	public List<Integer> getGatewayIds() {
		return gatewayIds;
	}
	public void setGatewayIds(List<Integer> gatewayIds) {
		this.gatewayIds = gatewayIds;
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

}
