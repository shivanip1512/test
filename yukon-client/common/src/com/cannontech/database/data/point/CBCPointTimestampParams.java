package com.cannontech.database.data.point;

import java.util.Date;

public class CBCPointTimestampParams {
private Integer pointId = new Integer (0);
private String pointName = "";
private String value = "";
//new Double (0.0);
private Date timestamp = null;

	public CBCPointTimestampParams() {
		super();
	}

	public void setPointId(Integer pointId) {
		this.pointId = pointId;
	}

	public void setPointName(String pointName) {
		this.pointName = pointName;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getPointId() {
		return pointId;
	}

	public String getPointName() {
		return pointName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
