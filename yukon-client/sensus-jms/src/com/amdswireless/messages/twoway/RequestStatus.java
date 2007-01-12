package com.amdswireless.messages.twoway;

import java.util.Hashtable;

public class RequestStatus implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private transient String correlationId;
    private String state;
    private String statusText;
    private int repId;
    private int attemptsMax;
    private int attemptsRemaining;
    private Hashtable<String,OneResponse> expectedResponses;
    private Route route;
    private String additionalText;
    private int sleepTime;
    private int totalSegments;
    private int completeSegments;
    private boolean finalMessage;

    public RequestStatus(String cId) {
        state = "UNKNOWN";
        statusText = "Farkeled";
        repId=0;
        expectedResponses = null;
        route = null;
        sleepTime = 0;
        finalMessage = true;
        this.correlationId=cId;
    }

    public void setState(String s) {
        this.state=s;
    }

    public String getState() {
        return this.state;
    }

    public void setStatusText(String s) {
        this.statusText=s;
    }

    public String getStatusText() {
        return this.statusText;
    }

    public void setAttemptsMax(int i) {
        this.attemptsMax=i;
    }

    public void setAttemptsRemaining(int i) {
        this.attemptsRemaining=i;
    }

    public void setExpectedResponses(Hashtable<java.lang.String,com.amdswireless.messages.twoway.OneResponse> e) {
        this.expectedResponses=e;
    }
    
    public void setRoute(Route r) {
        this.route=r;
    }

    public void setSleepTime(int i) {
        this.sleepTime=i;
    }

    public int getSleepTime() {
        return this.sleepTime;
    }

    public void setCorrelationId(String cid) {
        this.correlationId=cid;
    }

    public int getRepId() {
        return this.repId;
    }

    public void setRepId(int i) {
        this.repId=i;
    }

    public String getCorrelationId() {
        return this.correlationId;
    }

    public Route getRoute() {
        return this.route;
    }

    public void setFinalMessage(boolean b) {
        this.finalMessage=b;
    }

    public boolean isFinalMessage() {
        return this.finalMessage;
    }

    public String getAdditionalText() {
        return this.additionalText;
    }

    public void setAdditionalText(String s) {
        this.additionalText=s;
    }

	public int getAttemptsMax() {
		return attemptsMax;
	}

	public int getAttemptsRemaining() {
		return attemptsRemaining;
	}

	public Hashtable<String, OneResponse> getExpectedResponses() {
		return expectedResponses;
	}

	public int getCompleteSegments() {
		return completeSegments;
	}

	public void setCompleteSegments(int completeSegments) {
		this.completeSegments = completeSegments;
	}

	public int getTotalSegments() {
		return totalSegments;
	}

	public void setTotalSegments(int totalSegments) {
		this.totalSegments = totalSegments;
	}

}
