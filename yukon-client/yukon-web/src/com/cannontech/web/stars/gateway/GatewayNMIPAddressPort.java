package com.cannontech.web.stars.gateway;

public class GatewayNMIPAddressPort {
	
	// IP address that the gateway should use to connect to NM.
    private String nmIpAddress;
    // Port that the gateway should use to connect to NM.
    private Integer nmPort;
    
    public GatewayNMIPAddressPort() {}
    
    public GatewayNMIPAddressPort(String nmIpAddress, Integer nmPort) {
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
	
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nmIpAddress == null) ? 0 : nmIpAddress.hashCode());
		result = prime * result + ((nmPort == null) ? 0 : nmPort.hashCode());
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
		GatewayNMIPAddressPort other = (GatewayNMIPAddressPort) obj;
		if (nmIpAddress == null) {
			if (other.nmIpAddress != null)
				return false;
		} else if (!nmIpAddress.equals(other.nmIpAddress))
			return false;
		if (nmPort == null) {
			if (other.nmPort != null)
				return false;
		} else if (!nmPort.equals(other.nmPort))
			return false;
		return true;
	}

}
