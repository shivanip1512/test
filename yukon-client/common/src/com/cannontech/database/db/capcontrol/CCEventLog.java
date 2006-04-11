package com.cannontech.database.db.capcontrol;

import java.util.Date;

public class CCEventLog {
public static final String CONSTRAINT_COLUMNS[] = { "LogID" };
public static final String COLUMNS[] = { "PointID", 
"DateTime", "SubId", "FeederId",
"EventType", "SeqId", "Value", "Text", "UserName" };

public final static String TABLE_NAME = "CCEventLog";


private Long logId;
private Long pointId;
private Date  dateTime;
private Long subId;
private Long feederId;
private Integer eventType;
private Long seqId;
private Long value;
private String text;
private String userName;


public Date getDateTime() {
	return dateTime;
}

public void setDateTime(Date dateTime) {
	this.dateTime = dateTime;
}

public Integer getEventType() {
	return eventType;
}

public void setEventType(Integer eventType) {
	this.eventType = eventType;
}

public Long getFeederId() {
	return feederId;
}

public void setFeederId(Long feederId) {
	this.feederId = feederId;
}

public Long getLogId() {
	return logId;
}

public void setLogId(Long logId) {
	this.logId = logId;
}

public Long getPointId() {
	return pointId;
}

public void setPointId(Long pointId) {
	this.pointId = pointId;
}

public Long getSeqId() {
	return seqId;
}

public void setSeqId(Long seqId) {
	this.seqId = seqId;
}

public Long getSubId() {
	return subId;
}

public void setSubId(Long subId) {
	this.subId = subId;
}

public String getText() {
	return text;
}

public void setText(String text) {
	this.text = text;
}

public String getUserName() {
	return userName;
}

public void setUserName(String userName) {
	this.userName = userName;
}

public Long getValue() {
	return value;
}

public void setValue(Long value) {
	this.value = value;
}





}
