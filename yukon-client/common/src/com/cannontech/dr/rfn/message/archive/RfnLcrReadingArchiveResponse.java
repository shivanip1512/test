package com.cannontech.dr.rfn.message.archive;

import java.io.Serializable;

/**
 * JMS Queue name: yukon.qr.obj.dr.rfn.LcrReadingArchiveResponse
 */
public class RfnLcrReadingArchiveResponse implements Serializable {

	private static final long serialVersionUID = 2L;

	private long dataPointId;
	private RfnLcrReadingType type;

	public long getDataPointId() {
		return dataPointId;
	}

	public void setDataPointId(long dataPointId) {
		this.dataPointId = dataPointId;
	}

	public RfnLcrReadingType getType() {
		return type;
	}

	public void setType(RfnLcrReadingType type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (dataPointId ^ (dataPointId >>> 32));
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		RfnLcrReadingArchiveResponse other = (RfnLcrReadingArchiveResponse) obj;
		if (dataPointId != other.dataPointId)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format(
				"RfnLcrReadingArchiveResponse [dataPointId=%s, type=%s]",
				dataPointId, type);
	}

}