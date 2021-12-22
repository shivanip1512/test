package com.cannontech.web.picker;

import com.cannontech.common.search.result.UltraLightPao;

public class GatewayPickerModel implements UltraLightPao {
	private String nmIpAddress;
	private Integer nmPort;

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
	@Override
	public String getPaoName() {
		return this.getPaoName();
	}
	@Override
	public String getType() {
		return this.getType();
	}
	@Override
	public int getPaoId() {
		return this.getPaoId();
	}
}
