package com.cannontech.multispeak.db;


public class MspLMGroupCommunications {
	private Integer lmGroupId;
	private Integer routeId;	//route
	private Integer transmitterId;	//signal transmitter
	private boolean disabled;	//transmitter disable flag
	
	public enum MspLMGroupStatus {
		FAILED("Failed"),
		OFFLINE("Offline"),
		ONLINE("Online");
		
		private String status;
		private MspLMGroupStatus(String status) {
			this.status = status;
		}
		@Override
		public String toString() {
			return status;
		}
	}

	public enum MspLMProgramMode {
		COIN("Coin"),
		NONCOIN("Non-Coin");
		
		private String mode;
		private MspLMProgramMode(String mode) {
			this.mode = mode;
		}
		@Override
		public String toString() {
			return mode;
		}
	}

	public MspLMGroupCommunications(Integer lmGroupId, Integer routeId,
			Integer transmitterId, boolean disableFlag) {
		super();
		this.lmGroupId = lmGroupId;
		this.routeId = routeId;
		this.transmitterId = transmitterId;
		this.disabled = disableFlag;
	}
	
	public Integer getLmGroupId() {
		return lmGroupId;
	}
	public Integer getRouteId() {
		return routeId;
	}
	public Integer getTransmitterId() {
		return transmitterId;
	}
	public boolean isDisabled() {
		return disabled;
	}
}
