package com.cannontech.common.device.programming.message;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.common.device.programming.model.ProgramStatus;
import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * JMS Queue name: com.eaton.eas.yukon.MeterProgramStatusRequest
 */
public class MeterProgramStatusArchiveRequest implements Serializable {

	public enum Source {
		PORTER,
		// MeterProgrammingServiceImpl
		WS_COLLECTION_ACTION,
		// RfnStatusArchiveRequestListener
		SM_STATUS_ARCHIVE,
		// RfnRemoteMeterConfigurationFailureEventArchiveRequestProcessor
		SM_CONFIG_FAILURE
	}
	
	private static final long serialVersionUID = 1L;

    private RfnIdentifier rfnIdentifier;
    private String configurationId;
    //IDLE, UPLOADING, CONFIRMING, FAILED
    private ProgramStatus status;
    private DeviceError error;
    private long timeStamp;
    private Source source;
    
	public RfnIdentifier getRfnIdentifier() {
		return rfnIdentifier;
	}
	public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
		this.rfnIdentifier = rfnIdentifier;
	}
	public ProgramStatus getStatus() {
		return status;
	}
	public void setStatus(ProgramStatus status) {
		this.status = status;
	}
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public DeviceError getError() {
		return error;
	}
	public void setError(DeviceError error) {
		this.error = error;
	}

    public Source getSource() {
		return source;
	}
	public void setSource(Source source) {
		this.source = source;
	}
	
	public String getConfigurationId() {
		return configurationId;
	}
	public void setConfigurationId(String configurationId) {
		this.configurationId = configurationId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((configurationId == null) ? 0 : configurationId.hashCode());
		result = prime * result + ((error == null) ? 0 : error.hashCode());
		result = prime * result + ((rfnIdentifier == null) ? 0 : rfnIdentifier.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + (int) (timeStamp ^ (timeStamp >>> 32));
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
		MeterProgramStatusArchiveRequest other = (MeterProgramStatusArchiveRequest) obj;
		if (configurationId == null) {
			if (other.configurationId != null)
				return false;
		} else if (!configurationId.equals(other.configurationId))
			return false;
		if (error != other.error)
			return false;
		if (rfnIdentifier == null) {
			if (other.rfnIdentifier != null)
				return false;
		} else if (!rfnIdentifier.equals(other.rfnIdentifier))
			return false;
		if (source != other.source)
			return false;
		if (status != other.status)
			return false;
		if (timeStamp != other.timeStamp)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
				+ System.getProperty("line.separator");
	}
}
